package com.sbr.visualization.controlpanel.reportformmanage.service.impl;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.common.util.DateUtil;
import com.sbr.platform.auth.util.SecurityContextUtil;
import com.sbr.visualization.controlpanel.reportformmanage.dao.ReportFormManageDAO;
import com.sbr.visualization.controlpanel.reportformmanage.model.ReportFormManage;
import com.sbr.visualization.controlpanel.reportformmanage.service.IReportFormManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：报表管理 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-23 09:53:25
 */
//标记为Service类
@Service
//设置整个类的事务处理方式
@Transactional(readOnly = true)
public class ReportFormManageServiceImpl implements IReportFormManageService {

    @Autowired
    private ReportFormManageDAO reportFormManageDAO;

    @Override
    public List<ReportFormManage> findByFinder(Finder finder) {
        return reportFormManageDAO.findByFinder(finder);
    }

    @Override
    public ReportFormManage findById(String id) {
        return reportFormManageDAO.findOne(id);
    }

    @Override
    public Page<ReportFormManage> findByFinderAndPage(Finder finder, Page<ReportFormManage> page) {
        return reportFormManageDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public ReportFormManage create(ReportFormManage reportFormManage) {
        reportFormManage.setCreateUser(SecurityContextUtil.getUserSessionInfo().getAccountName());
        reportFormManage.setCreateTime(DateUtil.getCurrentDate());
        return reportFormManageDAO.save(reportFormManage);
    }

    @Override
    @Transactional
    public void delete(String id) {
        reportFormManageDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(ReportFormManage reportFormManage) {
        reportFormManageDAO.delete(reportFormManage);
    }

    @Override
    @Transactional
    public ReportFormManage patchUpdate(ReportFormManage reportFormManage) {
        ReportFormManage entity = findById(reportFormManage.getId());
        entity.setUpdateTime(DateUtil.getCurrentDate());
        entity.setUpdateUser(SecurityContextUtil.getUserSessionInfo().getAccountName());
        ClassUtil.merge(entity, reportFormManage);
        return reportFormManageDAO.save(entity);
    }

    @Override
    @Transactional
    public ReportFormManage putUpdate(ReportFormManage reportFormManage) {
        return reportFormManageDAO.save(reportFormManage);
    }
}