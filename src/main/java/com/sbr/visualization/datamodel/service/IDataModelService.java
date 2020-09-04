package com.sbr.visualization.datamodel.service;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.datamodel.model.DataModel;

import java.util.List;
import java.util.Map;

/**
 * <p>数据模型管理 服务实现层接口</p>
 *
 * @author DESKTOP-212O9VU  2020-06-12 15:20:26
 */

public interface IDataModelService {
    /**
     * <p>根据查询条件查询</p>
     *
     * @param finder 查询条件
     * @return 数据的集合
     * @author DESKTOP-212O9VU2020-06-12 15:20:26
     */
    public List<DataModel> findByFinder(Finder finder);

    /**
     * <p>根据Id查询实体</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU2020-06-12 15:20:26
     */
    public DataModel findById(String id);

    /**
     * <p>根据查询条件查询 带分页</p>
     *
     * @param finder 查询条件
     * @param page   分页信息
     * @return 实体
     * @author DESKTOP-212O9VU2020-06-12 15:20:26
     */
    public Page<DataModel> findByFinderAndPage(Finder finder, Page<DataModel> page);

    /**
     * <p>新增数据模型管理</p>
     *
     * @param dataModel 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU2020-06-12 15:20:26
     */
    public DataModel create(DataModel dataModel);

    /**
     * <p>根据id删除数据模型管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU2020-06-12 15:20:26
     */
    public void delete(String id);

    /**
     * <p>根据实体删除数据模型管理</p>
     *
     * @param dataModel 实体
     * @author DESKTOP-212O9VU2020-06-12 15:20:26
     */
    public void deleteByEntity(DataModel dataModel);

    /**
     * <p>更新部分数据</p>
     *
     * @param dataModel 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU2020-06-12 15:20:26
     */
    public DataModel patchUpdate(DataModel dataModel);

    /**
     * <p>更新全部数据</p>
     *
     * @param dataModel 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU2020-06-12 15:20:26
     */
    public DataModel putUpdate(DataModel dataModel);

    /**
     * @param tablename 数据表名称
     * @param id        数据模型ID
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author zxx
     * @Description //TODO 根据表名获取字段名、字段类型
     * @Date 10:20 2020/6/15
     **/
    InfoJson getfieldsByTableName(String tablename, String id) throws Exception;

    /**
     * @param id 数据模型ID
     * @return java.util.List<java.lang.String>
     * @Author zxx
     * @Description //TODO 获取当前数据库下的所有表、或者es下所有索引
     * @Date 16:24 2020/6/11
     **/
    List<Map<String,Object>> getDatasourseManageAdnTables(String id) throws Exception;

    /**
     * @Author zxx
     * @Description //TODO 根据数据模型ID，表关系JSON，查询数据
     * @Date 14:33 2020/6/24
     * @param dataModel
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     **/
    InfoJson getDataByDataModel(DataModel dataModel) throws Exception;

    /**
     * @Author zxx
     * @Description //TODO 根据数据模型，字段属性查询属性数据
     * @Date 10:46 2020/6/28
     * @param dataModel 数据模型+
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     **/
    List<Map<String, String>> getDataByfield(DataModel dataModel) throws Exception;

    /**
     * @Author zxx
     * @Description //TODO 删除数据模型
     * @Date 11:15 2020/9/3
     * @param id
     * @return com.sbr.springboot.json.InfoJson
     **/
    InfoJson deleteDataModel(String id);
}
