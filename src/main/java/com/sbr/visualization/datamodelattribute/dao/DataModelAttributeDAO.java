package com.sbr.visualization.datamodelattribute.dao;

import com.sbr.ms.springdata.repository.IBaseRepository;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;

import java.util.List;

/**
 * 描述：数据模型属性表DAO
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-03 11:05:32
 */
public interface DataModelAttributeDAO extends IBaseRepository<DataModelAttribute, String> {

    /**
     * @Author zxx
     * @Description //TODO 根据数据模型ID，查询数据模型属性值
     * @Date 14:41 2020/6/16
     * @param id 数据模型ID
     * @return java.util.List<com.sbr.visualization.datamodelattribute.model.DataModelAttribute>
     **/
    List<DataModelAttribute> findByDataModelId(String id);

    /**
     * @Author zxx
     * @Description //TODO 根据数据模型ID，删除所有数据模型属性值
     * @Date 10:55 2020/6/17
     * @param id 数据模型ID
     * @return void
     **/
    void deleteByDataModelId(String id);

    /**
     * @Author zxx
     * @Description //TODO 根据数据映射ID，查询数据模型属性
     * @Date 10:48 2020/8/3
     * @param id 数据映射ID
     * @return java.util.List<com.sbr.visualization.datamodelattribute.model.DataModelAttribute>
     **/
    List<DataModelAttribute> findByMappingManageId(String id);
}
