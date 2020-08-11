package com.sbr.visualization.databasetype.service.impl;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.visualization.databasetype.dao.DatabaseTypeManageDAO;
import com.sbr.visualization.databasetype.model.DatabaseTypeManage;
import com.sbr.visualization.databasetype.service.IDatabaseTypeManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：数据库类型管理 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-11 14:48:01
 */
@Service
@Transactional(readOnly = true)
public class DatabaseTypeManageServiceImpl implements IDatabaseTypeManageService {

    @Autowired
    private DatabaseTypeManageDAO databaseTypeManageDAO;

    @Override
    public List<DatabaseTypeManage> findByFinder(Finder finder) {
        return databaseTypeManageDAO.findByFinder(finder);
    }

    @Override
    public DatabaseTypeManage findById(String id) {
        return databaseTypeManageDAO.findOne(id);
    }

    @Override
    public Page<DatabaseTypeManage> findByFinderAndPage(Finder finder, Page<DatabaseTypeManage> page) {
        return databaseTypeManageDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public DatabaseTypeManage create(DatabaseTypeManage databaseTypeManage) {
        return databaseTypeManageDAO.save(databaseTypeManage);
    }

    @Override
    @Transactional
    public void delete(String id) {
        databaseTypeManageDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(DatabaseTypeManage databaseTypeManage) {
        databaseTypeManageDAO.delete(databaseTypeManage);
    }

    @Override
    @Transactional
    public DatabaseTypeManage patchUpdate(DatabaseTypeManage databaseTypeManage) {
        DatabaseTypeManage entity = findById(databaseTypeManage.getId());
        ClassUtil.merge(entity, databaseTypeManage);
        return databaseTypeManageDAO.save(entity);
    }

    @Override
    @Transactional
    public DatabaseTypeManage putUpdate(DatabaseTypeManage databaseTypeManage) {
        return databaseTypeManageDAO.save(databaseTypeManage);
    }
}