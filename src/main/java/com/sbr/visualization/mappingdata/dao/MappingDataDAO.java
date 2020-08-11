package com.sbr.visualization.mappingdata.dao;

import com.sbr.ms.springdata.repository.IBaseRepository;
import com.sbr.visualization.mappingdata.model.MappingData;

import java.util.List;

/**
* 描述：映射数据表DAO
* @author DESKTOP-212O9VU
* @date 2020-06-15 16:23:30
*/
public interface MappingDataDAO extends IBaseRepository<MappingData,String>{

    List<MappingData> findByMappingManageId(String id);
}
