package com.sbr.visualization.mappingmanage.service;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.visualization.mappingmanage.model.MappingManage;

import java.util.List;

/**
* <p>数据映射管理 服务实现层接口</p>
* @author DESKTOP-212O9VU  2020-06-15 16:23:39
*/

public interface IMappingManageService {
    /**
    *
    * <p>根据查询条件查询</p>
    * @param finder 查询条件
    * @return 数据的集合
    * @author DESKTOP-212O9VU 2020-06-15 16:23:39
    */
    public List<MappingManage> findByFinder(Finder finder);

    /**
    *
    * <p>根据Id查询实体</p>
    * @param id 主键
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:39
    */
    public MappingManage findById(String id);

    /**
    *
    * <p>根据查询条件查询 带分页</p>
    * @param finder 查询条件
    * @param page 分页信息
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:39
    */
    public Page<MappingManage> findByFinderAndPage(Finder finder, Page<MappingManage> page);

    /**
    *
    * <p>新增数据映射管理</p>
    * @param mappingManage 需要新增的数据
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:39
    */
    public MappingManage create(MappingManage mappingManage);

    /**
    *
    * <p>根据id删除数据映射管理</p>
    * @param id 主键
    * @author DESKTOP-212O9VU 2020-06-15 16:23:39
    */
    public void delete(String id);

    /**
    *
    * <p>根据实体删除数据映射管理</p>
    * @param mappingManage 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:39
    */
    public void deleteByEntity(MappingManage mappingManage);

    /**
    *
    * <p>更新部分数据</p>
    * @param mappingManage 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:39
    */
    public MappingManage patchUpdate(MappingManage mappingManage);

    /**
    *
    * <p>更新全部数据</p>
    * @param mappingManage 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:39
    */
    public MappingManage putUpdate(MappingManage mappingManage);

    /**
     * @Author zxx
     * @Description //TODO 创建映射管理和多个映射数据
     * @Date 10:11 2020/6/16
     * @param mappingManage
     * @return com.sbr.springboot.json.InfoJson
     **/
    MappingManage batchMappingManageAndDataSave(MappingManage mappingManage);

    /**
     * @Author zxx
     * @Description //TODO 编辑映射管理和多个映射数据
     * @Date 10:11 2020/6/16
     * @param mappingManage
     * @return com.sbr.springboot.json.InfoJson
     **/
    MappingManage batchMappingManageAndDataPatch(MappingManage mappingManage);

    void deleteMappingManageAndData(String id);
}
