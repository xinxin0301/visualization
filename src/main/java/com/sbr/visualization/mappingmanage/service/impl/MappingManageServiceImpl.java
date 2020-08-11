package com.sbr.visualization.mappingmanage.service.impl;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.visualization.mappingdata.dao.MappingDataDAO;
import com.sbr.visualization.mappingdata.model.MappingData;
import com.sbr.visualization.mappingmanage.dao.MappingManageDAO;
import com.sbr.visualization.mappingmanage.model.MappingManage;
import com.sbr.visualization.mappingmanage.service.IMappingManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * 描述：数据映射管理 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-15 16:23:39
 */
//标记为Service类
@Service
//设置整个类的事务处理方式
@Transactional(readOnly = true)
public class MappingManageServiceImpl implements IMappingManageService {

    @Autowired
    private MappingManageDAO mappingManageDAO;

    @Autowired
    private MappingDataDAO mappingDataDAO;

    @Override
    public List<MappingManage> findByFinder(Finder finder) {
        return mappingManageDAO.findByFinder(finder);
    }

    @Override
    public MappingManage findById(String id) {
        return mappingManageDAO.findOne(id);
    }

    @Override
    public Page<MappingManage> findByFinderAndPage(Finder finder, Page<MappingManage> page) {
        return mappingManageDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public MappingManage create(MappingManage mappingManage) {
        return mappingManageDAO.save(mappingManage);
    }

    @Override
    @Transactional
    public void delete(String id) {
        mappingManageDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(MappingManage mappingManage) {
        mappingManageDAO.delete(mappingManage);
    }

    @Override
    @Transactional
    public MappingManage patchUpdate(MappingManage mappingManage) {
        MappingManage entity = findById(mappingManage.getId());
        ClassUtil.merge(entity, mappingManage);
        return mappingManageDAO.save(entity);
    }

    @Override
    @Transactional
    public MappingManage putUpdate(MappingManage mappingManage) {
        return mappingManageDAO.save(mappingManage);
    }

    @Override
    @Transactional
    public MappingManage batchMappingManageAndDataSave(MappingManage mappingManage) {
        //新增映射管理
        MappingManage save = mappingManageDAO.save(mappingManage);
        //批量新增映射数据
        if (mappingManage != null) {
            Set<MappingData> mappingDataSet = mappingManage.getMappingDataSet();
            mappingDataSet.stream().forEach(mappingData -> {
                mappingData.setMappingManage(save);
            });
            mappingDataDAO.save(mappingDataSet);
        }
        return save;
    }

    @Override
    @Transactional
    public MappingManage batchMappingManageAndDataPatch(MappingManage mappingManage) {

        Set<MappingData> mappingDataSet = null;

        //编辑映射管理
        MappingManage entity = findById(mappingManage.getId());
        ClassUtil.merge(entity, mappingManage);

        //编辑映射数据
        //根据映射管理ID，查询对应映射数据
        List<MappingData> mappingDataList = mappingDataDAO.findByMappingManageId(mappingManage.getId());
        //删除关联管理的映射数据
        mappingDataDAO.deleteInBatch(mappingDataList);
        //设置数据映射重新创建数据
        if (mappingManage.getMappingDataSet() != null && mappingManage.getMappingDataSet().size() > 0) {
            mappingDataSet = mappingManage.getMappingDataSet();
            mappingDataSet.stream().forEach(mappingData -> {
                mappingData.setMappingManage(entity);
            });
        }

        //新增映射数据
        mappingDataDAO.save(mappingDataSet);
        //编辑映射管理
        MappingManage save = mappingManageDAO.save(entity);
        return save;
    }

    @Override
    @Transactional
    public void deleteMappingManageAndData(String id) {
        //获取当前映射管理下所有数据并删除
        List<MappingData> byMappingManageId = mappingDataDAO.findByMappingManageId(id);
        mappingDataDAO.deleteInBatch(byMappingManageId);
        mappingManageDAO.delete(id);
    }
}