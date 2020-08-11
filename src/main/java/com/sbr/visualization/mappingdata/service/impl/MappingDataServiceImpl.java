package com.sbr.visualization.mappingdata.service.impl;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.mappingdata.dao.MappingDataDAO;
import com.sbr.visualization.mappingdata.model.MappingData;
import com.sbr.visualization.mappingdata.service.IMappingDataService;
import com.sbr.visualization.mappingmanage.dao.MappingManageDAO;
import com.sbr.visualization.mappingmanage.model.MappingManage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：映射数据表 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-15 16:23:30
 */
//标记为Service类
@Service
//设置整个类的事务处理方式
@Transactional(readOnly = true)
public class MappingDataServiceImpl implements IMappingDataService {

    @Autowired
    private MappingDataDAO mappingDataDAO;

    @Autowired
    private MappingManageDAO mappingManageDAO;

    @Override
    public List<MappingData> findByFinder(Finder finder) {
        return mappingDataDAO.findByFinder(finder);
    }

    @Override
    public MappingData findById(String id) {
        return mappingDataDAO.findOne(id);
    }

    @Override
    public Page<MappingData> findByFinderAndPage(Finder finder, Page<MappingData> page) {
        return mappingDataDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public MappingData create(MappingData mappingData) {
        return mappingDataDAO.save(mappingData);
    }

    @Override
    @Transactional
    public void delete(String id) {
        mappingDataDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(MappingData mappingData) {
        mappingDataDAO.delete(mappingData);
    }

    @Override
    @Transactional
    public MappingData patchUpdate(MappingData mappingData) {
        MappingData entity = findById(mappingData.getId());

        if (mappingData.getMappingManage() != null && StringUtils.isNotEmpty(mappingData.getMappingManage().getId())) {
            MappingManage mappingManage = mappingManageDAO.getOne(mappingData.getMappingManage().getId());
            entity.setMappingManage(mappingManage);
        } else {
            mappingData.setMappingManage(null);
        }

        ClassUtil.merge(entity, mappingData);
        return mappingDataDAO.save(entity);
    }

    @Override
    @Transactional
    public MappingData putUpdate(MappingData mappingData) {
        return mappingDataDAO.save(mappingData);
    }

    @Override
    @Transactional
    public InfoJson batchCreate(List<MappingData> list) {
        InfoJson infoJson = new InfoJson();
        List<MappingData> save = mappingDataDAO.save(list);
        if(save==null){
            infoJson.setSuccess(false);
            infoJson.setDescription("批量新增失败");
        }
        return infoJson;
    }

    @Override
    @Transactional
    public InfoJson batchPatch(List<MappingData> list) {
        InfoJson infoJson = new InfoJson();
        List<MappingData> mappingDataList = new ArrayList<>();
        if (list != null && list.size() > 0) {
            list.stream().forEach(mappingData -> {
                MappingData entity = findById(mappingData.getId());
                if (mappingData.getMappingManage() != null && StringUtils.isNotEmpty(mappingData.getMappingManage().getId())) {
                    MappingManage mappingManage = mappingManageDAO.getOne(mappingData.getMappingManage().getId());
                    entity.setMappingManage(mappingManage);
                } else {
                    mappingData.setMappingManage(null);
                }
                ClassUtil.merge(entity, mappingData);
                mappingDataList.add(entity);
            });
        }
        List<MappingData> mappingDataList1 = mappingDataDAO.save(mappingDataList);
        if(mappingDataList1==null){
            infoJson.setSuccess(false);
            infoJson.setDescription("批量修改失败");
        }
        return infoJson;
    }
}