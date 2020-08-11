package com.sbr.visualization.datasourcemanage.service.impl;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.visualization.config.EsConfig;
import com.sbr.visualization.databasetype.dao.DatabaseTypeManageDAO;
import com.sbr.visualization.databasetype.model.DatabaseTypeManage;
import com.sbr.visualization.datasourcemanage.dao.DatasourceManageDAO;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.datasourcemanage.service.IDatasourceManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：数据源管理 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-11 15:07:07
 */
@Service
@Transactional(readOnly = true)
public class DatasourceManageServiceImpl implements IDatasourceManageService {

    private Logger logger = LoggerFactory.getLogger(String.valueOf(DatasourceManageServiceImpl.class));

    @Autowired
    private DatasourceManageDAO datasourceManageDAO;

    @Autowired
    private DatabaseTypeManageDAO databaseTypeManageDAO;

    @Autowired
    private EsConfig esConfig;

    @Override
    public List<DatasourceManage> findByFinder(Finder finder) {
        return datasourceManageDAO.findByFinder(finder);
    }

    @Override
    public DatasourceManage findById(String id) {
        return datasourceManageDAO.findOne(id);
    }

    @Override
    public Page<DatasourceManage> findByFinderAndPage(Finder finder, Page<DatasourceManage> page) {
        return datasourceManageDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public DatasourceManage create(DatasourceManage datasourseManage) {
        return datasourceManageDAO.save(datasourseManage);
    }

    @Override
    @Transactional
    public void delete(String id) {
        datasourceManageDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(DatasourceManage datasourseManage) {
        datasourceManageDAO.delete(datasourseManage);
    }

    @Override
    @Transactional
    public DatasourceManage patchUpdate(DatasourceManage datasourseManage) {
        DatasourceManage entity = findById(datasourseManage.getId());

        if (datasourseManage.getDatabaseTypeManage() != null && StringUtils.isNotEmpty(datasourseManage.getDatabaseTypeManage().getId())) {
            //查询出来对应的数据源类型
            DatabaseTypeManage databaseTypeManage = databaseTypeManageDAO.findOne(datasourseManage.getDatabaseTypeManage().getId());
            if (databaseTypeManage != null) {
                entity.setDatabaseTypeManage(databaseTypeManage);
            } else {
                datasourseManage.setDatabaseTypeManage(null);
            }
        }

        ClassUtil.merge(entity, datasourseManage);
        return datasourceManageDAO.save(entity);
    }

    @Override
    @Transactional
    public DatasourceManage putUpdate(DatasourceManage datasourseManage) {
        return datasourceManageDAO.save(datasourseManage);
    }
}