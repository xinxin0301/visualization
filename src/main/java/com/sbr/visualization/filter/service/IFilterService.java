package com.sbr.visualization.filter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.filter.model.Filter;

import java.io.IOException;
import java.util.List;

/**
* <p>过滤器 服务实现层接口</p>
* @author DESKTOP-212O9VU  2020-06-30 11:18:23
*/

public interface IFilterService {
    /**
    *
    * <p>根据查询条件查询</p>
    * @param finder 查询条件
    * @return 数据的集合
    * @author DESKTOP-212O9VU 2020-06-30 11:18:23
    */
    public List<Filter> findByFinder(Finder finder);

    /**
    *
    * <p>根据Id查询实体</p>
    * @param id 主键
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-30 11:18:23
    */
    public Filter findById(String id);

    /**
    *
    * <p>根据查询条件查询 带分页</p>
    * @param finder 查询条件
    * @param page 分页信息
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-30 11:18:23
    */
    public Page<Filter> findByFinderAndPage(Finder finder, Page<Filter> page);

    /**
    *
    * <p>新增过滤器</p>
    * @param filter 需要新增的数据
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-30 11:18:23
    */
    public Filter create(Filter filter);

    /**
    *
    * <p>根据id删除过滤器</p>
    * @param id 主键
    * @author DESKTOP-212O9VU 2020-06-30 11:18:23
    */
    public void delete(String id);

    /**
    *
    * <p>根据实体删除过滤器</p>
    * @param filter 实体
    * @author DESKTOP-212O9VU 2020-06-30 11:18:23
    */
    public void deleteByEntity(Filter filter);

    /**
    *
    * <p>更新部分数据</p>
    * @param filters 需要更新的数据实体
    * @param id 数据模型ID
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-30 11:18:23
    */
    public InfoJson patchUpdate(List<Filter> filters,String id) throws IOException;

    /**
    *
    * <p>更新全部数据</p>
    * @param filter 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-30 11:18:23
    */
    public Filter putUpdate(Filter filter);

    /**
     * @Author zxx
     * @Description //TODO 根据数据模型ID，查询过滤器
     * @Date 14:41 2020/6/30
     * @param id 数据模型ID
     * @return java.util.List<com.sbr.visualization.filter.model.Filter>
     **/
    List<Filter> findByDataModelId(String id);
}
