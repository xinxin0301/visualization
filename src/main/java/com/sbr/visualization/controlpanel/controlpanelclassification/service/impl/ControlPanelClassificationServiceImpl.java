package com.sbr.visualization.controlpanel.controlpanelclassification.service.impl;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.Sorter;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.visualization.controlpanel.controlpanelclassification.dao.ControlPanelClassificationDAO;
import com.sbr.visualization.controlpanel.controlpanelclassification.model.ControlPanelClassification;
import com.sbr.visualization.controlpanel.controlpanelclassification.service.IControlPanelClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：控制面板分类表 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-23 15:01:08
 */
@Service
@Transactional(readOnly = true)
public class ControlPanelClassificationServiceImpl implements IControlPanelClassificationService {

    @Autowired
    private ControlPanelClassificationDAO controlPanelClassificationDAO;

    @Override
    public List<ControlPanelClassification> findByFinder(Finder finder) {
        return controlPanelClassificationDAO.findByFinder(finder);
    }

    @Override
    public ControlPanelClassification findById(String id) {
        return controlPanelClassificationDAO.findOne(id);
    }

    @Override
    public Page<ControlPanelClassification> findByFinderAndPage(Finder finder, Page<ControlPanelClassification> page) {
        finder.appendSorter("sortIndex", Sorter.SortType.ASC);
        return controlPanelClassificationDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public ControlPanelClassification create(ControlPanelClassification controlPanelClassification) {
        return controlPanelClassificationDAO.save(controlPanelClassification);
    }

    @Override
    @Transactional
    public void delete(String id) {
        controlPanelClassificationDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(ControlPanelClassification controlPanelClassification) {
        controlPanelClassificationDAO.delete(controlPanelClassification);
    }

    @Override
    @Transactional
    public ControlPanelClassification patchUpdate(ControlPanelClassification controlPanelClassification) {
        ControlPanelClassification entity = findById(controlPanelClassification.getId());
        ClassUtil.merge(entity, controlPanelClassification);
        return controlPanelClassificationDAO.save(entity);
    }

    @Override
    @Transactional
    public ControlPanelClassification putUpdate(ControlPanelClassification controlPanelClassification) {
        return controlPanelClassificationDAO.save(controlPanelClassification);
    }
}