package com.sbr.visualization.controlpanel.designmode.service.impl;

import com.sbr.common.entity.Tree;
import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.common.util.DateUtil;
import com.sbr.platform.auth.util.SecurityContextUtil;
import com.sbr.visualization.controlpanel.componenttypemanage.model.ComponentTypeManage;
import com.sbr.visualization.controlpanel.designmode.dao.DesignModelDAO;
import com.sbr.visualization.controlpanel.designmode.model.DesignModel;
import com.sbr.visualization.controlpanel.designmode.service.IDesignModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

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

    @Override
    public List<Tree> constructTree(List<DesignModel> designModelList) {
        List<Tree> trees = new ArrayList<Tree>();
        // 所有节点id
        Set<String> designModelIds = new HashSet<String>();
        // 顶级节点
        List<DesignModel> topTypeManages = new ArrayList<DesignModel>();
        // 各节点及子节点map。
        Map<String, Set<DesignModel>> designModelMap = new HashMap<String, Set<DesignModel>>();
        for (DesignModel designModel : designModelList) {
            if (designModel == null) {
                continue;
            }
            designModelIds.add(designModel.getId());
            DesignModel parentTDesignModel = designModel.getParent();
            if (parentTDesignModel != null) {
                if (designModelMap.get(parentTDesignModel.getId()) == null) {
                    designModelMap.put(parentTDesignModel.getId(), new HashSet<DesignModel>());
                    designModelMap.get(parentTDesignModel.getId()).add(designModel);
                } else {
                    designModelMap.get(parentTDesignModel.getId()).add(designModel);
                }
            }
        }

        /*
         * 如果父节点未空或 查询出的数据中，不包含该节点的父节点，则该节点作为顶级节点进行展示
         */
        for (DesignModel designModel : designModelList) {
            DesignModel parentTypeManage = designModel.getParent();
            if (parentTypeManage == null) {
                topTypeManages.add(designModel);
            } else if (!designModelIds.contains(parentTypeManage.getId())) {
                topTypeManages.add(designModel);
            }
        }

        /*
         * 在遍历子节点时由于jpa的懒加载会频繁访问数据，因此这里提前给children set好数据，就不需要再请求数据库了。
         */
        for (DesignModel designModel : designModelList) {
            Set<DesignModel> childrenTypeManage = designModelMap.get(designModel.getId());
            if (childrenTypeManage == null) {
                designModel.setChildren(new HashSet<DesignModel>());
            } else {
                List<DesignModel> menuList = new ArrayList<DesignModel>(childrenTypeManage);
                menuList.sort(Comparator.comparing(DesignModel::getSortIndex, Comparator.nullsFirst(Integer::compareTo)));
                designModel.setTypeChildren(menuList);
            }
        }
        for (DesignModel topTypeManage : topTypeManages) {
            trees.add(constructTree(topTypeManage));
        }
        return trees;
    }

    @Override
    public Tree constructTree(DesignModel designModel) {
        Tree tree = new Tree();
        if (designModel == null) {
            return tree;
        }
        tree.setId(designModel.getId());
        tree.setText(designModel.getModelName());
        boolean isLeaf = true;
        if (designModel.getTypeChildren().size() > 0) {
            isLeaf = false;
        }
        if (designModel.getParent() != null) {
            tree.setParentId(designModel.getParent().getId());
        }
        //组装参数
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("model_name", designModel.getModelName());
        attributes.put("sort_index", designModel.getSortIndex());
        attributes.put("is_leaf", isLeaf);
        attributes.put("model_describe", designModel.getModelDescribe());
        attributes.put("model_json", designModel.getModelJson());
        attributes.put("share_flag", designModel.getShareFlag());
        attributes.put("model_type", designModel.getModelType());
        attributes.put("model_create_unit_id", designModel.getModelCreateUnitId());
        attributes.put("model_create_unit", designModel.getModelCreateUnit());
        attributes.put("model_preview_url", designModel.getModelPreviewUrl());
        if (!StringUtils.isEmpty(designModel.getParent())) {
            attributes.put("parentId", designModel.getParent().getId());
        }
        tree.setAttributes(attributes);
        for (DesignModel designModel1 : designModel.getTypeChildren()) {
            tree.getChildren().add(constructTree(designModel1));
        }
        return tree;
    }

    @Override
    public List<DesignModel> structureChildrenId(List<DesignModel> childTypeManage, String id) {
        List<DesignModel> list = designModelDAO.findAll();
        if (list != null && list.size() > 0) {
            for (DesignModel designModel : list) {
                if (designModel.getParent() != null && designModel.getParent().getId().equals(id)) {
                    structureChildrenId(childTypeManage, designModel.getId());
                    childTypeManage.add(designModel);
                }
            }
        }
        return childTypeManage;
    }
}