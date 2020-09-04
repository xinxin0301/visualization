package com.sbr.visualization.controlpanel.componenttypemanage.service.impl;

import com.sbr.common.entity.Tree;
import com.sbr.common.finder.Finder;
import com.sbr.common.finder.Sorter;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.visualization.controlpanel.componentmanage.dao.ComponentManageDAO;
import com.sbr.visualization.controlpanel.componentmanage.model.ComponentManage;
import com.sbr.visualization.controlpanel.componenttypemanage.dao.ComponentTypeManageDAO;
import com.sbr.visualization.controlpanel.componenttypemanage.model.ComponentTypeManage;
import com.sbr.visualization.controlpanel.componenttypemanage.service.IComponentTypeManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 描述：组件类型管理 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-23 15:31:49
 */
@Service
@Transactional(readOnly = true)
public class ComponentTypeManageServiceImpl implements IComponentTypeManageService {

    @Autowired
    private ComponentTypeManageDAO componentTypeManageDAO;

    @Autowired
    private ComponentManageDAO componentManageDAO;

    @Override
    public List<ComponentTypeManage> findByFinder(Finder finder) {
        finder.appendSorter("sortIndex", Sorter.SortType.ASC);
        return componentTypeManageDAO.findByFinder(finder);
    }

    @Override
    public ComponentTypeManage findById(String id) {
        return componentTypeManageDAO.findOne(id);
    }

    @Override
    public Page<ComponentTypeManage> findByFinderAndPage(Finder finder, Page<ComponentTypeManage> page) {
        return componentTypeManageDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public ComponentTypeManage create(ComponentTypeManage componentTypeManage) {
        return componentTypeManageDAO.save(componentTypeManage);
    }

    @Override
    @Transactional
    public void delete(String id) {
        componentTypeManageDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(ComponentTypeManage componentTypeManage) {
        componentTypeManageDAO.delete(componentTypeManage);
    }

    @Override
    @Transactional
    public ComponentTypeManage patchUpdate(ComponentTypeManage componentTypeManage) {
        //查询当前修改组件类型
        ComponentTypeManage entity = findById(componentTypeManage.getId());
        //如果父节点ID不为空，查询父节点
        if (componentTypeManage.getParent() != null && !StringUtils.isEmpty(componentTypeManage.getParent().getId())) {
            ComponentTypeManage componentType = componentTypeManageDAO.findOne(componentTypeManage.getParent().getId());
            //设置父节点信息
            entity.setParent(componentType);
        } else {
            componentTypeManage.setParent(null);
            entity.setParent(null);
        }
        ClassUtil.merge(entity, componentTypeManage);
        return componentTypeManageDAO.save(entity);
    }

    @Override
    @Transactional
    public ComponentTypeManage putUpdate(ComponentTypeManage componentTypeManage) {
        return componentTypeManageDAO.save(componentTypeManage);
    }

    @Override
    public List<Tree> constructTree(List<ComponentTypeManage> typeManages) {
        List<Tree> trees = new ArrayList<Tree>();
        // 所有节点id
        Set<String> typeManageIds = new HashSet<String>();
        // 顶级节点
        List<ComponentTypeManage> topTypeManages = new ArrayList<ComponentTypeManage>();
        // 各节点及子节点map。
        Map<String, Set<ComponentTypeManage>> typeManageMap = new HashMap<String, Set<ComponentTypeManage>>();
        for (ComponentTypeManage typeManage : typeManages) {
            if (typeManage == null) {
                continue;
            }
            typeManageIds.add(typeManage.getId());
            ComponentTypeManage parentTypeManages = typeManage.getParent();
            if (parentTypeManages != null) {
                if (typeManageMap.get(parentTypeManages.getId()) == null) {
                    typeManageMap.put(parentTypeManages.getId(), new HashSet<ComponentTypeManage>());
                    typeManageMap.get(parentTypeManages.getId()).add(typeManage);
                } else {
                    typeManageMap.get(parentTypeManages.getId()).add(typeManage);
                }
            }
        }

        /*
         * 如果父节点未空或 查询出的数据中，不包含该节点的父节点，则该节点作为顶级节点进行展示
         */
        for (ComponentTypeManage typeManage : typeManages) {
            ComponentTypeManage parentTypeManage = typeManage.getParent();
            if (parentTypeManage == null) {
                topTypeManages.add(typeManage);
            } else if (!typeManageIds.contains(parentTypeManage.getId())) {
                topTypeManages.add(typeManage);
            }
        }

        /*
         * 在遍历子节点时由于jpa的懒加载会频繁访问数据，因此这里提前给children set好数据，就不需要再请求数据库了。
         */
        for (ComponentTypeManage typeManage : typeManages) {
            Set<ComponentTypeManage> childrenTypeManage = typeManageMap.get(typeManage.getId());
            if (childrenTypeManage == null) {
                typeManage.setChildren(new HashSet<ComponentTypeManage>());
            } else {
                List<ComponentTypeManage> menuList = new ArrayList<ComponentTypeManage>(childrenTypeManage);
                menuList.sort(Comparator.comparing(ComponentTypeManage::getSortIndex, Comparator.nullsFirst(Integer::compareTo)));
                typeManage.setTypeChildren(menuList);
            }
        }
        for (ComponentTypeManage topTypeManage : topTypeManages) {
            trees.add(constructTree(topTypeManage));
        }
        return trees;
    }

    @Override
    public Tree constructTree(ComponentTypeManage componentTypeManage) {
        Tree tree = new Tree();
        if (componentTypeManage == null) {
            return tree;
        }
        tree.setId(componentTypeManage.getId());
        tree.setText(componentTypeManage.getComponentTypeName());
        boolean isLeaf = true;
        if (componentTypeManage.getTypeChildren().size() > 0) {
            isLeaf = false;
        }
        if (componentTypeManage.getParent() != null) {
            tree.setParentId(componentTypeManage.getParent().getId());
        }
        //组装参数
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("component_type_name", componentTypeManage.getComponentTypeName());
        attributes.put("sort_index", componentTypeManage.getSortIndex());
        attributes.put("is_leaf", isLeaf);
        attributes.put("is_visible", componentTypeManage.getIsVisible());
        attributes.put("icon", componentTypeManage.getIcon());

        if (!StringUtils.isEmpty(componentTypeManage.getParent())) {
            attributes.put("parentId", componentTypeManage.getParent().getId());
        }
        tree.setAttributes(attributes);
        for (ComponentTypeManage componentTypeManage1 : componentTypeManage.getTypeChildren()) {
            tree.getChildren().add(constructTree(componentTypeManage1));
        }
        return tree;
    }


    @Override
    public List<ComponentTypeManage> structureChildrenId(List<ComponentTypeManage> childTypeManage, String id) {
        List<ComponentTypeManage> list = componentTypeManageDAO.findAll();
        if (list != null && list.size() > 0) {
            for (ComponentTypeManage typeManage : list) {
                if (typeManage.getParent() != null && typeManage.getParent().getId().equals(id)) {
                    structureChildrenId(childTypeManage, typeManage.getId());
                    childTypeManage.add(typeManage);
                }
            }
        }
        return childTypeManage;
    }

    @Override
    public List<Tree> constructTreeAndComponent(List<ComponentTypeManage> typeManages) {
        List<Tree> trees = new ArrayList<Tree>();
        // 所有节点id
        Set<String> typeManageIds = new HashSet<String>();
        // 顶级节点
        List<ComponentTypeManage> topTypeManages = new ArrayList<ComponentTypeManage>();
        // 各节点及子节点map。
        Map<String, Set<ComponentTypeManage>> typeManageMap = new HashMap<String, Set<ComponentTypeManage>>();
        for (ComponentTypeManage typeManage : typeManages) {
            if (typeManage == null) {
                continue;
            }
            typeManageIds.add(typeManage.getId());
            ComponentTypeManage parentTypeManages = typeManage.getParent();
            if (parentTypeManages != null) {
                if (typeManageMap.get(parentTypeManages.getId()) == null) {
                    typeManageMap.put(parentTypeManages.getId(), new HashSet<ComponentTypeManage>());
                    typeManageMap.get(parentTypeManages.getId()).add(typeManage);
                } else {
                    typeManageMap.get(parentTypeManages.getId()).add(typeManage);
                }
            }
        }

        /*
         * 如果父节点未空或 查询出的数据中，不包含该节点的父节点，则该节点作为顶级节点进行展示
         */
        for (ComponentTypeManage typeManage : typeManages) {
            ComponentTypeManage parentTypeManage = typeManage.getParent();
            if (parentTypeManage == null) {
                topTypeManages.add(typeManage);
            } else if (!typeManageIds.contains(parentTypeManage.getId())) {
                topTypeManages.add(typeManage);
            }
        }

        /*
         * 在遍历子节点时由于jpa的懒加载会频繁访问数据，因此这里提前给children set好数据，就不需要再请求数据库了。
         */
        for (ComponentTypeManage typeManage : typeManages) {
            Set<ComponentTypeManage> childrenTypeManage = typeManageMap.get(typeManage.getId());
            if (childrenTypeManage == null) {
                typeManage.setChildren(new HashSet<ComponentTypeManage>());
            } else {
                List<ComponentTypeManage> menuList = new ArrayList<ComponentTypeManage>(childrenTypeManage);
                menuList.sort(Comparator.comparing(ComponentTypeManage::getSortIndex, Comparator.nullsFirst(Integer::compareTo)));
                typeManage.setTypeChildren(menuList);
            }
        }
        for (ComponentTypeManage topTypeManage : topTypeManages) {
            trees.add(constructTreeAndComponent(topTypeManage));
        }
        return trees;
    }


    @Override
    public Tree constructTreeAndComponent(ComponentTypeManage componentTypeManage) {
        Tree tree = new Tree();
        if (componentTypeManage == null) {
            return tree;
        }
        tree.setId(componentTypeManage.getId());
        tree.setText(componentTypeManage.getComponentTypeName());
        boolean isLeaf = true;
        if (componentTypeManage.getTypeChildren().size() > 0) {
            isLeaf = false;
        }
        if (componentTypeManage.getParent() != null) {
            tree.setParentId(componentTypeManage.getParent().getId());
        }
        //组装参数
        Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("component_type_name", componentTypeManage.getComponentTypeName());
        attributes.put("sort_index", componentTypeManage.getSortIndex());
        attributes.put("is_leaf", isLeaf);
        attributes.put("is_visible", componentTypeManage.getIsVisible());
        attributes.put("icon", componentTypeManage.getIcon());
        List<ComponentManage> componentManageList = componentManageDAO.findByComponentTypeManageId(componentTypeManage.getId());
        Collections.sort(componentManageList, Comparator.comparing(ComponentManage::getSortIndex));

        attributes.put("component_manage", componentManageList);

        if (!StringUtils.isEmpty(componentTypeManage.getParent())) {
            attributes.put("parentId", componentTypeManage.getParent().getId());
        }
        tree.setAttributes(attributes);
        for (ComponentTypeManage componentTypeManage1 : componentTypeManage.getTypeChildren()) {
            tree.getChildren().add(constructTreeAndComponent(componentTypeManage1));
        }
        return tree;
    }


}