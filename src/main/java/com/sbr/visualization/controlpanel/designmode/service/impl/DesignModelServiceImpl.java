package com.sbr.visualization.controlpanel.designmode.service.impl;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.common.util.DateUtil;
import com.sbr.platform.auth.util.SecurityContextUtil;
import com.sbr.visualization.controlpanel.designmode.dao.DesignModelDAO;
import com.sbr.visualization.controlpanel.designmode.model.DesignModel;
import com.sbr.visualization.controlpanel.designmode.service.IDesignModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：设计模型 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-02 10:54:32
 */
//标记为Service类
@Service
//设置整个类的事务处理方式
@Transactional(readOnly = true)
public class DesignModelServiceImpl implements IDesignModelService {

    @Autowired
    private DesignModelDAO designModelDAO;

    @Override
    public List<DesignModel> findByFinder(Finder finder) {
        return designModelDAO.findByFinder(finder);
    }

    @Override
    public DesignModel findById(String id) {
        return designModelDAO.findOne(id);
    }

    @Override
    public Page<DesignModel> findByFinderAndPage(Finder finder, Page<DesignModel> page) {
        return designModelDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public DesignModel create(DesignModel designModel) {
        designModel.setCreateTime(DateUtil.getCurrentDate());
        designModel.setCreateUser(SecurityContextUtil.getUserSessionInfo().getAccountName());
        return designModelDAO.save(designModel);
    }

    @Override
    @Transactional
    public void delete(String id) {
        designModelDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(DesignModel designModel) {
        designModelDAO.delete(designModel);
    }

    @Override
    @Transactional
    public DesignModel patchUpdate(DesignModel designModel) {
        designModel.setUpdateTime(DateUtil.getCurrentDate());
        designModel.setUpdateUser(SecurityContextUtil.getUserSessionInfo().getAccountName());

        DesignModel entity = findById(designModel.getId());
        ClassUtil.merge(entity, designModel);
        return designModelDAO.save(entity);
    }

    @Override
    @Transactional
    public DesignModel putUpdate(DesignModel designModel) {
        return designModelDAO.save(designModel);
    }
}