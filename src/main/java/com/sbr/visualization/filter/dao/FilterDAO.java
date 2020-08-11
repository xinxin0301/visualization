package com.sbr.visualization.filter.dao;

import com.sbr.ms.springdata.repository.IBaseRepository;
import com.sbr.visualization.filter.model.Filter;

import java.util.List;

/**
* 描述：过滤器DAO
* @author DESKTOP-212O9VU
* @date 2020-06-30 11:18:23
*/
public interface FilterDAO extends IBaseRepository<Filter,String>{

    /**
     * @Author zxx
     * @Description //TODO 根据数据模型ID，查询过滤器
     * @Date 14:41 2020/6/30
     * @param id 数据模型ID
     * @return java.util.List<com.sbr.visualization.filter.model.Filter>
     **/
    List<Filter> findByDataModelId(String id);
}
