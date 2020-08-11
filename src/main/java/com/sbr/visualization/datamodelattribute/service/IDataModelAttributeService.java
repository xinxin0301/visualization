package com.sbr.visualization.datamodelattribute.service;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;

import java.util.List;

/**
 * <p>数数据模型属性表 服务实现层接口</p>
 *
 * @author DESKTOP-212O9VU  2020-06-15 11:05:32
 */

public interface IDataModelAttributeService {
    /**
     * <p>根据查询条件查询</p>
     *
     * @param finder 查询条件
     * @return 数据的集合
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    public List<DataModelAttribute> findByFinder(Finder finder);

    /**
     * <p>根据Id查询实体</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    public DataModelAttribute findById(String id);

    /**
     * <p>根据查询条件查询 带分页</p>
     *
     * @param finder 查询条件
     * @param page   分页信息
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    public Page<DataModelAttribute> findByFinderAndPage(Finder finder, Page<DataModelAttribute> page);

    /**
     * <p>新增数据模型维度表</p>
     *
     * @param dataModelDimension 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    public DataModelAttribute create(DataModelAttribute dataModelDimension);

    /**
     * <p>根据id删除数据模型维度表</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    public void delete(String id);

    /**
     * <p>根据实体删除数据模型维度表</p>
     *
     * @param dataModelDimension 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    public void deleteByEntity(DataModelAttribute dataModelDimension);

    /**
     * <p>更新部分数据</p>
     *
     * @param dataModelDimension 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    public DataModelAttribute patchUpdate(DataModelAttribute dataModelDimension);

    /**
     * <p>更新全部数据</p>
     *
     * @param dataModelDimension 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    public DataModelAttribute putUpdate(DataModelAttribute dataModelDimension);

    /**
     * <p>批量新增数据模型属性</p>
     *
     * @param list 需要新增的数据集合
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    InfoJson batchCreate(List<DataModelAttribute> list);

    /**
     * <p>批量修改数据模型属性</p>
     *
     * @param list 需要新增的数据集合
     * @param id   数据模型ID
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    InfoJson batchpatch(List<DataModelAttribute> list,String id) throws Exception;

    /**
     * @Author zxx
     * @Description //TODO 根据数据模型ID，查询数据模型属性数据
     * @Date 14:40 2020/6/16
     * @param id 数据模型ID
     * @return java.util.List<com.sbr.visualization.datamodelattribute.model.DataModelAttribute>
     **/
    List<DataModelAttribute> findDataModelAttributeByDataModelId(String id);

    /**
     * @Author zxx
     * @Description //TODO 根据数据映射查询数据模型属性
     * @Date 10:47 2020/8/3
     * @Param 
     * @param id  数据映射ID
     * @return java.util.List<com.sbr.visualization.datamodelattribute.model.DataModelAttribute>
     **/
    List<DataModelAttribute> findByMappingManageId(String id);
}
