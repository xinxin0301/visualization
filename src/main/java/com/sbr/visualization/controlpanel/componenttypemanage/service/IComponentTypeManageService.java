package com.sbr.visualization.controlpanel.componenttypemanage.service;

import com.sbr.common.entity.Tree;
import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.visualization.controlpanel.componenttypemanage.model.ComponentTypeManage;

import java.util.List;

/**
* <p>组件类型管理 服务实现层接口</p>
* @author DESKTOP-212O9VU  2020-06-23 15:31:49
*/

public interface IComponentTypeManageService {
    /**
    *
    * <p>根据查询条件查询</p>
    * @param finder 查询条件
    * @return 数据的集合
    * @author DESKTOP-212O9VU 2020-06-23 15:31:49
    */
    public List<ComponentTypeManage> findByFinder(Finder finder);

    /**
    *
    * <p>根据Id查询实体</p>
    * @param id 主键
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:31:49
    */
    public ComponentTypeManage findById(String id);

    /**
    *
    * <p>根据查询条件查询 带分页</p>
    * @param finder 查询条件
    * @param page 分页信息
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:31:49
    */
    public Page<ComponentTypeManage> findByFinderAndPage(Finder finder, Page<ComponentTypeManage> page);

    /**
    *
    * <p>新增组件类型管理</p>
    * @param componentTypeManage 需要新增的数据
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:31:49
    */
    public ComponentTypeManage create(ComponentTypeManage componentTypeManage);

    /**
    *
    * <p>根据id删除组件类型管理</p>
    * @param id 主键
    * @author DESKTOP-212O9VU 2020-06-23 15:31:49
    */
    public void delete(String id);

    /**
    *
    * <p>根据实体删除组件类型管理</p>
    * @param componentTypeManage 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:31:49
    */
    public void deleteByEntity(ComponentTypeManage componentTypeManage);

    /**
    *
    * <p>更新部分数据</p>
    * @param componentTypeManage 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:31:49
    */
    public ComponentTypeManage patchUpdate(ComponentTypeManage componentTypeManage);

    /**
    *
    * <p>更新全部数据</p>
    * @param componentTypeManage 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:31:49
    */
    public ComponentTypeManage putUpdate(ComponentTypeManage componentTypeManage);

    /**
     * @Author zxx
     * @Description //TODO 构建树结构
     * @Date 15:47 2020/6/3
     * @param typeManages 组件类型集合
     * @return java.util.List<com.sun.source.tree.Tree>
     **/
    List<Tree> constructTree(List<ComponentTypeManage> typeManages);


    /**
     * @Author zxx
     * @Description //TODO  构建树结构
     * @Date 15:57 2020/6/3
     * @param componentTypeManage 组件类型对象
     * @return com.sun.source.tree.Tree
     **/
    Tree constructTree(ComponentTypeManage componentTypeManage);

    /**
     * @Author zxx
     * @Description //TODO 获取所有子节点
     * @Date 16:13 2020/6/3
     * @Param
     * @param childTypeManage 组件类型集合
     * @param id 节点ID
     * @return java.util.List<com.sbr.visualize.platform.controlpanel.componenttypemanage.model.ComponentTypeManage>
     **/
    List<ComponentTypeManage> structureChildrenId(List<ComponentTypeManage> childTypeManage, String id);

    /**
     * @Author zxx
     * @Description //TODO 构建树结构
     * @Date 15:47 2020/6/3
     * @param typeManages 组件类型集合
     * @return java.util.List<com.sun.source.tree.Tree>
     **/
    List<Tree> constructTreeAndComponent(List<ComponentTypeManage> typeManages);

    /**
     * @Author zxx
     * @Description //TODO 构建子节点，包含元组件信息
     * @Date 16:26 2020/6/24
     * @Param
     * @param componentTypeManage
     * @return com.sbr.common.entity.Tree
     **/
    Tree constructTreeAndComponent(ComponentTypeManage componentTypeManage);
}
