package com.sbr.visualization.datamodelattribute.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbr.common.finder.Finder;
import com.sbr.common.finder.Sorter;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.dao.DataModelAttributeDAO;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datamodelattribute.service.IDataModelAttributeService;
import com.sbr.visualization.datasourcemanage.dao.DatasourceManageDAO;
import com.sbr.visualization.filter.dao.FilterDAO;
import com.sbr.visualization.filter.model.Filter;
import com.sbr.visualization.mappingmanage.dao.MappingManageDAO;
import com.sbr.visualization.mappingmanage.model.MappingManage;
import com.sbr.visualization.util.DataBaseUtil;
import com.sbr.visualization.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

/**
 * 描述：数据模型属性表 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-15 11:05:32
 */
@Service
@Transactional(readOnly = true)
public class DataModelAttributeServiceImpl implements IDataModelAttributeService {

    @Autowired
    private DataModelAttributeDAO dataModelDimensionDAO;

    @Autowired
    private DataModelDAO dataModelDAO;

    @Autowired
    private MappingManageDAO mappingManageDAO;

    @Autowired
    private DatasourceManageDAO datasourceManageDAO;

    @Autowired
    private FilterDAO filterDAO;

    @Override
    public List<DataModelAttribute> findByFinder(Finder finder) {
        return dataModelDimensionDAO.findByFinder(finder);
    }

    @Override
    public DataModelAttribute findById(String id) {
        return dataModelDimensionDAO.findOne(id);
    }

    @Override
    public Page<DataModelAttribute> findByFinderAndPage(Finder finder, Page<DataModelAttribute> page) {
        finder.appendSorter("sortIndex", Sorter.SortType.ASC);
        return dataModelDimensionDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public DataModelAttribute create(DataModelAttribute dataModelDimension) {
        return dataModelDimensionDAO.save(dataModelDimension);
    }

    @Override
    @Transactional
    public void delete(String id) {
        dataModelDimensionDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(DataModelAttribute dataModelDimension) {
        dataModelDimensionDAO.delete(dataModelDimension);
    }

    @Override
    @Transactional
    public DataModelAttribute patchUpdate(DataModelAttribute dataModelDimension) {
        DataModelAttribute entity = findById(dataModelDimension.getId());

        if (dataModelDimension.getDataModel() != null && StringUtils.isNotEmpty(dataModelDimension.getDataModel().getId())) {
            //获取数据模型
            DataModel daoOne = dataModelDAO.findOne(dataModelDimension.getDataModel().getId());
            entity.setDataModel(daoOne);
        } else {
            entity.setDataModel(null);
        }

        ClassUtil.merge(entity, dataModelDimension);
        return dataModelDimensionDAO.save(entity);
    }

    @Override
    @Transactional
    public DataModelAttribute putUpdate(DataModelAttribute dataModelDimension) {
        return dataModelDimensionDAO.save(dataModelDimension);
    }

    @Override
    @Transactional
    public InfoJson batchCreate(List<DataModelAttribute> list) {
        InfoJson infoJson = new InfoJson();
        List<DataModelAttribute> attributes = dataModelDimensionDAO.save(list);
        if (attributes == null) {
            infoJson.setDescription("操作失败");
            infoJson.setSuccess(false);
        }
        return infoJson;
    }

    @Override
    @Transactional
    public InfoJson batchpatch(List<DataModelAttribute> list, String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        List<DataModelAttribute> attributeList = null;
        //获取数据模型
        DataModel dataModel = dataModelDAO.findOne(id);
        if (dataModel == null) {
            infoJson.setDescription("数据模型不存在！");
            infoJson.setSuccess(false);
            return infoJson;
        }

        //查询数据模型下的模型属性,并删除
        List<DataModelAttribute> dataModelAttributes = dataModelDimensionDAO.findByDataModelId(id);
        if (dataModelAttributes != null && dataModelAttributes.size() > 0) {
            dataModelDimensionDAO.delete(dataModelAttributes);
        }

        //TODO 设置模型屬性
        if (list != null && list.size() > 0) {
            List<DataModelAttribute> finalAttributeList = attributeList = new ArrayList<>();
            list.forEach(dataModelAttribute -> {
                MappingManage mappingManage = null;
                //设置映射管理关联对象
                if (dataModelAttribute.getMappingManage() != null && StringUtils.isNotEmpty(dataModelAttribute.getMappingManage().getId())) {
                    //获取映射管理
                    mappingManage = mappingManageDAO.findOne(dataModelAttribute.getMappingManage().getId());
                    dataModelAttribute.setMappingManage(mappingManage);
                }
                //设置数据模型关联对象
                dataModelAttribute.setDataModel(dataModel);
                //每一个放入集合.生成随机数别名用于拼接SQL使用
                dataModelAttribute.setRandomAlias(Util.getRandomString(16).toUpperCase());
                finalAttributeList.add(dataModelAttribute);
            });
            //批量創建
            List<DataModelAttribute> attributes = dataModelDimensionDAO.save(attributeList);
            if (attributes == null) {
                infoJson.setDescription("操作失败");
                infoJson.setSuccess(false);
            }
        }

        //TODO 生成SQL，并保存SQL字符串
        buidGetSql(attributeList, dataModel);
        return infoJson;
    }

    /**
     * @param attributeList 数据模型属性
     * @param dataModel     数据模型
     * @return void
     * @Author zxx
     * @Description //TODO 生成SQL
     * @Date 13:39 2020/6/29
     **/
    private void buidGetSql(List<DataModelAttribute> attributeList, DataModel dataModel) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (dataModel.getAssociation() != null && StringUtils.isNotEmpty(dataModel.getAssociation())) {
            Map map = objectMapper.readValue(dataModel.getAssociation(), Map.class);
            //获取JOIN关联关系
            List<Map<String, Object>> joinList = (List<Map<String, Object>>) map.get("children");
            //处理SQL,如果两个表出现重复字段，只显示一个
            String fieldSql = "";
            //展示SQL
            String showSQL = "";
            //处理查询字段
            if (attributeList != null && attributeList.size() > 0) {
                Collections.sort(attributeList, Comparator.comparing(DataModelAttribute::getModelType).thenComparing(DataModelAttribute::getSortIndex));
                //获取查询字段SQL
                StringBuffer fieldBuffer = DataBaseUtil.buildVeidooSQL(attributeList);
                //TODO 处理条件
                //根据数据模型查询，过滤器
                List<Filter> filterList = filterDAO.findByDataModelId(dataModel.getId());
                //构建WHERE条件SQL
                List<String> sqlParam = new ArrayList<>();
                //0条件SQL 1展示SQL
                List<StringBuffer> stringBufferList = DataBaseUtil.buidWhereSQL(filterList, sqlParam);
                //拼接WHERE条件
                String whereSql = "";
                if (StringUtils.isNotEmpty(stringBufferList.get(0).toString())) {
                    whereSql = " WHERE " + stringBufferList.get(0).substring(0, stringBufferList.get(0).length() - 3);
                }
                //拼接WHERE展示
                String whereShowSql = "";
                if (StringUtils.isNotEmpty(stringBufferList.get(1).toString())) {
                    whereShowSql = " WHERE " + stringBufferList.get(1).substring(0, stringBufferList.get(1).length() - 3);
                }
                //条件SQL
                fieldSql = buidSQL(dataModel, map, joinList, fieldBuffer, whereSql);
                //展示SQL
                showSQL = buidSQL(dataModel, map, joinList, fieldBuffer, whereShowSql);

                //TODO 实体对象赋值
                //SQL条件
                dataModel.setSqlParam(objectMapper.writeValueAsString(sqlParam));
                //SQL条件
                dataModel.setSqlCondition(whereSql);
                //占位符SQL
                dataModel.setSqlStr(fieldSql);
                //展示SQL
                dataModel.setSqlShow(showSQL);
            } else {
                dataModel.setSqlShow("");
                dataModel.setSqlStr("");
                dataModel.setSqlParam("");
                dataModel.setSqlCondition("");
            }
            dataModelDAO.save(dataModel);
        }
    }

    private String buidSQL(DataModel dataModel, Map map, List<Map<String, Object>> joinList, StringBuffer fieldBuffer, String whereSql) throws IOException {
        String fieldSql;
        if (joinList != null && joinList.size() > 0) {
            //多表处理
            //获取连接SQL
            StringBuffer joinBuffer = DataBaseUtil.getJOINSqlByAssociation(dataModel.getAssociation());
            //结果SQL
            fieldSql = "SELECT " + fieldBuffer.toString().substring(0, fieldBuffer.toString().length() - 1) + " FROM " + joinBuffer + whereSql + " LIMIT 0,500";
        } else {
            String tableName = (String) map.get("name");
            //单表处理
            fieldSql = "SELECT " + fieldBuffer.toString().substring(0, fieldBuffer.toString().length() - 1) + " FROM " + tableName + whereSql + " LIMIT 0,500";
        }
        return fieldSql;
    }


    /**
     * @param attributeList
     * @return java.lang.StringBuffer
     * @Author zxx
     * @Description //TODO 获取属性SQL
     * @Date 13:37 2020/6/29
     **/
    public StringBuffer getSQLfield(List<DataModelAttribute> attributeList) {
        StringBuffer sqlBuffer = new StringBuffer();
        attributeList.forEach(dataModelAttribute -> {
            if (dataModelAttribute.getIsHide() == 2) {//如果该字段隐藏的话，不用拼接
                //拼接属性SQL，如果当前属性存在追加表名 AS 'xx(tableName)'
                sqlBuffer.append(" `" + dataModelAttribute.getTableName() + "`.`" + dataModelAttribute.getFieldsName() + "`AS " + dataModelAttribute.getRandomAlias() + ",");
            }
        });
        return sqlBuffer;
    }

    @Override
    public List<DataModelAttribute> findDataModelAttributeByDataModelId(String id) {
        return dataModelDimensionDAO.findByDataModelId(id);
    }

    @Override
    public List<DataModelAttribute> findByMappingManageId(String id) {
        return dataModelDimensionDAO.findByMappingManageId(id);
    }
}