package com.sbr.visualization.filter.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.common.util.StringUtil;
import com.sbr.ms.feign.system.dictionary.api.DictionaryFeignClient;
import com.sbr.ms.feign.system.dictionary.model.DataDictionary;
import com.sbr.ms.feign.system.organization.api.OrganizationFeignClient;
import com.sbr.ms.feign.system.organization.model.Organization;
import com.sbr.platform.auth.util.SecurityContextUtil;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.controlpanel.componentmanage.dao.ComponentManageDAO;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.dao.DataModelAttributeDAO;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datamodelattribute.service.impl.DataModelAttributeServiceImpl;
import com.sbr.visualization.filter.dao.FilterDAO;
import com.sbr.visualization.filter.model.Filter;
import com.sbr.visualization.filter.service.IFilterService;
import org.apache.catalina.security.SecurityUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.ValidationMode;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述：过滤器 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-30 11:18:23
 */
//标记为Service类
@Service
//设置整个类的事务处理方式
@Transactional(readOnly = true)
public class FilterServiceImpl implements IFilterService {

    @Autowired
    private FilterDAO filterDAO;

    @Autowired
    private ComponentManageDAO componentManageDAO;

    @Autowired
    private DataModelDAO dataModelDAO;

    @Autowired
    private DataModelAttributeDAO dataModelAttributeDAO;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private DictionaryFeignClient dictionaryFeignClient;

    @Override
    public List<Filter> findByFinder(Finder finder) {
        return filterDAO.findByFinder(finder);
    }

    @Override
    public Filter findById(String id) {
        return filterDAO.findOne(id);
    }

    @Override
    public Page<Filter> findByFinderAndPage(Finder finder, Page<Filter> page) {
        return filterDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public Filter create(Filter filter) {
        return filterDAO.save(filter);
    }

    @Override
    @Transactional
    public void delete(String id) {
        filterDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(Filter filter) {
        filterDAO.delete(filter);
    }

    @Override
    @Transactional
    public InfoJson patchUpdate(List<Filter> filters, String id) throws IOException {
        InfoJson infoJson = new InfoJson();
        if (id == null || StringUtils.isEmpty(id)) {
            infoJson.setSuccess(false);
            infoJson.setDescription("数据模型ID，不能为空！");
            return infoJson;
        }

        //查询数据模型
        DataModel one = dataModelDAO.findOne(id);
        if (one == null) {
            infoJson.setSuccess(false);
            infoJson.setDescription("当前数据模型不存在！");
            return infoJson;
        }

        //模型ID，查询过滤器,并删除过滤器
        List<Filter> filterList = filterDAO.findByDataModelId(id);
        if (filterList != null && filterList.size() > 0) {
            filterDAO.delete(filterList);
        }

        //先删除在创建
        if (filters != null && filters.size() > 0) {
            for (Filter filter : filters) {
                if (filter.getComponentManage() == null || StringUtils.isEmpty(filter.getComponentManage().getId())) {
                    filter.setComponentManage(null);
                }
                filter.setDataModel(one);

                //添加单位权限
                if (filter.getOrgCategory() != null && !StringUtil.isEmpty(filter.getOrgCategory())) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    Set<Organization> organizationSet = new HashSet<>();
                    //1、包含下级 2、包含上级 3、包含自己
                    String orgCategory = filter.getOrgCategory();
                    String[] split = orgCategory.split(",");
                    //当前单位ID
                    String orgId = SecurityContextUtil.getUserSessionInfo().getOrgId();
                    for (String s : split) {
                        switch (s) {
                            case "1":
                                organizationSet.addAll(organizationFeignClient.findChildOrg(orgId));
                                break;
                            case "2":
                                Organization parentOrg = organizationFeignClient.findParentOrg(orgId);
                                organizationSet.add(parentOrg);
                                if (parentOrg.getParent() != null) {
                                    organizationSet.add(parentOrg.getParent());
                                }
                                break;
                            case "3":
                                organizationSet.add(organizationFeignClient.findParentOrg(orgId));
                        }
                    }

                    List<String> idList = null;
                    if (filter.getOrgType() != null && !StringUtil.isEmpty(filter.getOrgType())) {

                        //获取机构类型字典Key
                        List<Integer> orgTypeList = new ArrayList<>();
                        String orgType = filter.getOrgType();
                        String[] split1 = orgType.split(",");
                        Map<String, Object> queryMap = new HashMap<>();
                        queryMap.put("dictionary_group", "org_type");
                        List<DataDictionary> dataDictionaryPage = dictionaryFeignClient.findDataDictionaryPage(queryMap);
                        for (DataDictionary dataDictionary : dataDictionaryPage) {
                            for (String s : split1) {
                                if (s.equals(dataDictionary.getDictionaryValue())) {
                                    orgTypeList.add(Integer.valueOf(dataDictionary.getDictionaryKey()));
                                }
                            }
                        }

                        //过滤字典数据
                        if (organizationSet != null && organizationSet.size() > 0) {
                            List<Organization> collect = organizationSet.stream().filter((Organization o) -> orgTypeList.contains(o.getOrgType())).collect(Collectors.toList());
                            idList = collect.stream().map(organization -> organization.getId()).collect(Collectors.toList());
                        }
                    }

                    String listStr = "";
                    if (idList != null) {
                        listStr = objectMapper.writeValueAsString(idList);
                    }

                    //查询单位权限过滤字段
                    DataModelAttribute dataModelAttribute = dataModelAttributeDAO.findOne(filter.getFieldId());
                    filter.setListMatch("{\"mode\":\"list\",\"name\":\"`" + dataModelAttribute.getTableName() + "`.`" + dataModelAttribute.getFieldsName() + "`\",\"list\":" + listStr + "}");
                }
                filterDAO.saveAndFlush(filter);
                //构建全新条件
                List<DataModelAttribute> dataModelAttributeList = dataModelAttributeDAO.findByDataModelId(one.getId());
                DataModelAttributeServiceImpl.buidGetSql(dataModelAttributeList, one, filterDAO, dataModelDAO);
            }
        }


        infoJson.setSuccess(true);
        infoJson.setDescription("操作成功");
        return infoJson;
    }

    @Override
    @Transactional
    public Filter putUpdate(Filter filter) {
        return filterDAO.save(filter);
    }

    @Override
    public List<Filter> findByDataModelId(String id) {
        return filterDAO.findByDataModelId(id);
    }
}