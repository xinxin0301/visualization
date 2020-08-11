package com.sbr.visualization.mappingdata.service;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.mappingdata.model.MappingData;

import java.util.List;

/**
* <p>映射数据表 服务实现层接口</p>
* @author DESKTOP-212O9VU  2020-06-15 16:23:30
*/

public interface IMappingDataService {
    /**
    *
    * <p>根据查询条件查询</p>
    * @param finder 查询条件
    * @return 数据的集合
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    public List<MappingData> findByFinder(Finder finder);

    /**
    *
    * <p>根据Id查询实体</p>
    * @param id 主键
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    public MappingData findById(String id);

    /**
    *
    * <p>根据查询条件查询 带分页</p>
    * @param finder 查询条件
    * @param page 分页信息
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    public Page<MappingData> findByFinderAndPage(Finder finder, Page<MappingData> page);

    /**
    *
    * <p>新增映射数据表</p>
    * @param mappingData 需要新增的数据
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    public MappingData create(MappingData mappingData);

    /**
    *
    * <p>根据id删除映射数据表</p>
    * @param id 主键
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    public void delete(String id);

    /**
    *
    * <p>根据实体删除映射数据表</p>
    * @param mappingData 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    public void deleteByEntity(MappingData mappingData);

    /**
    *
    * <p>更新部分数据</p>
    * @param mappingData 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    public MappingData patchUpdate(MappingData mappingData);

    /**
    *
    * <p>更新全部数据</p>
    * @param mappingData 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    public MappingData putUpdate(MappingData mappingData);

    /**
     * <p>批量新增映射数据表</p>
     * @param list 需要新增的数据集合
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 16:23:30
     */
    InfoJson batchCreate(List<MappingData> list);

    /**
     * <p>批量修改映射数据表</p>
     * @param list 需要修改的数据集合
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 16:23:30
     */
    InfoJson batchPatch(List<MappingData> list);
}
