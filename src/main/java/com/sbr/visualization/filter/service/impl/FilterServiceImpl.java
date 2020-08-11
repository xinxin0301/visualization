package com.sbr.visualization.filter.service.impl;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.controlpanel.componentmanage.dao.ComponentManageDAO;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.dao.DataModelAttributeDAO;
import com.sbr.visualization.filter.dao.FilterDAO;
import com.sbr.visualization.filter.model.Filter;
import com.sbr.visualization.filter.service.IFilterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public InfoJson patchUpdate(List<Filter> filters, String id) {
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
                filterDAO.save(filter);
            }
        }

        /*for (Filter filter : filters) {
            //TODO 编辑
            if (filter.getId() != null && StringUtils.isNotEmpty(filter.getId())) {
                Filter daoOne = filterDAO.findOne(filter.getId());
                if (daoOne == null) {
                    throw new RestResouceNotFoundException("当前过滤器不存在！");
                }

                //编辑元组件
                if (filter.getComponentManage() != null && StringUtils.isNotEmpty(filter.getComponentManage().getId())) {
                    ComponentManage componentManage = componentManageDAO.findOne(filter.getComponentManage().getId());
                    daoOne.setComponentManage(componentManage);
                } else {
                    filter.setComponentManage(null);
                }

                //编辑数据模型
                if (filter.getDataModel() != null && StringUtils.isNotEmpty(filter.getDataModel().getId())) {
                    DataModel dataModel = dataModelDAO.findOne(filter.getDataModel().getId());
                    daoOne.setDataModel(dataModel);
                } else {
                    filter.setDataModel(null);
                }
                ClassUtil.merge(daoOne, filter);
                filterDAO.save(daoOne);
            } else {
                //TODO 新增
                if (filter.getComponentManage() == null || StringUtils.isEmpty(filter.getComponentManage().getId())) {
                    filter.setComponentManage(null);
                }
                if (filter.getDataModel() == null || StringUtils.isEmpty(filter.getDataModel().getId())) {
                    filter.setDataModel(null);
                }
                filterDAO.save(filter);
            }
        }*/
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