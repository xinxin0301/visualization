package com.sbr.visualization.controlpanel.componentmanage.service.impl;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.Sorter;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.springboot.rest.exception.RestIllegalArgumentException;
import com.sbr.visualization.controlpanel.componentmanage.dao.ComponentManageDAO;
import com.sbr.visualization.controlpanel.componentmanage.model.ComponentManage;
import com.sbr.visualization.controlpanel.componentmanage.service.IComponentManageService;
import com.sbr.visualization.controlpanel.componenttypemanage.dao.ComponentTypeManageDAO;
import com.sbr.visualization.controlpanel.componenttypemanage.model.ComponentTypeManage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 描述：元组件管理 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-23 14:58:48
 */
@Service
@Transactional(readOnly = true)
public class ComponentManageServiceImpl implements IComponentManageService {

    @Autowired
    private ComponentManageDAO componentManageDAO;

    @Autowired
    private ComponentTypeManageDAO componentTypeManageDAO;

    @Override
    public List<ComponentManage> findByFinder(Finder finder) {
        return componentManageDAO.findByFinder(finder);
    }

    @Override
    public ComponentManage findById(String id) {
        return componentManageDAO.findOne(id);
    }

    @Override
    public Page<ComponentManage> findByFinderAndPage(Finder finder, Page<ComponentManage> page) {
        finder.appendSorter("sortIndex", Sorter.SortType.ASC);
        return componentManageDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public ComponentManage create(ComponentManage componentManage) {
        return componentManageDAO.save(componentManage);
    }

    @Override
    @Transactional
    public void delete(String id) {
        componentManageDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(ComponentManage componentManage) {
        componentManageDAO.delete(componentManage);
    }

    @Override
    @Transactional
    public ComponentManage patchUpdate(ComponentManage componentManage) {
        ComponentManage entity = findById(componentManage.getId());

        if (componentManage.getComponentTypeManage() != null && StringUtils.isNotEmpty(componentManage.getComponentTypeManage().getId())) {
            ComponentTypeManage typeManageDAOOne = componentTypeManageDAO.findOne(componentManage.getComponentTypeManage().getId());
            if (typeManageDAOOne == null) {
                throw new RestIllegalArgumentException("组件类型ID不合法");
            }
            entity.setComponentTypeManage(typeManageDAOOne);
        } else {
            entity.setComponentTypeManage(null);
        }

        ClassUtil.merge(entity, componentManage);
        return componentManageDAO.save(entity);
    }

    @Override
    @Transactional
    public ComponentManage putUpdate(ComponentManage componentManage) {
        return componentManageDAO.save(componentManage);
    }

    @Override
    public ComponentManage findByComponentCode(String componentCode) {
        return componentManageDAO.findByComponentCode(componentCode);
    }

    @Override
    public ComponentManage findByChartCode(String chartCode) {
        return componentManageDAO.findByChartCode(chartCode);
    }
}