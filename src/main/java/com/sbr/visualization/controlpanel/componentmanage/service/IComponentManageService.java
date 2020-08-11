package com.sbr.visualization.controlpanel.componentmanage.service;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.visualization.controlpanel.componentmanage.model.ComponentManage;

import java.util.List;

/**
* <p>元组件管理 服务实现层接口</p>
* @author DESKTOP-212O9VU  2020-06-23 14:58:48
*/

public interface IComponentManageService {
    /**
    *
    * <p>根据查询条件查询</p>
    * @param finder 查询条件
    * @return 数据的集合
    * @author DESKTOP-212O9VU 2020-06-23 14:58:48
    */
    public List<ComponentManage> findByFinder(Finder finder);

    /**
    *
    * <p>根据Id查询实体</p>
    * @param id 主键
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 14:58:48
    */
    public ComponentManage findById(String id);

    /**
    *
    * <p>根据查询条件查询 带分页</p>
    * @param finder 查询条件
    * @param page 分页信息
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 14:58:48
    */
    public Page<ComponentManage> findByFinderAndPage(Finder finder, Page<ComponentManage> page);

    /**
    *
    * <p>新增元组件管理</p>
    * @param componentManage 需要新增的数据
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 14:58:48
    */
    public ComponentManage create(ComponentManage componentManage);

    /**
    *
    * <p>根据id删除元组件管理</p>
    * @param id 主键
    * @author DESKTOP-212O9VU 2020-06-23 14:58:48
    */
    public void delete(String id);

    /**
    *
    * <p>根据实体删除元组件管理</p>
    * @param componentManage 实体
    * @author DESKTOP-212O9VU 2020-06-23 14:58:48
    */
    public void deleteByEntity(ComponentManage componentManage);

    /**
    *
    * <p>更新部分数据</p>
    * @param componentManage 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 14:58:48
    */
    public ComponentManage patchUpdate(ComponentManage componentManage);

    /**
    *
    * <p>更新全部数据</p>
    * @param componentManage 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 14:58:48
    */
    public ComponentManage putUpdate(ComponentManage componentManage);

    /**
     * @Author zxx
     * @Description //TODO 根据组件代码查询元组件
     * @Date 16:37 2020/6/3
     * @Param
     * @param componentCode 组件代码
     * @return com.sbr.visualize.platform.constant.WorksituationCommonConstant
     **/
    ComponentManage findByComponentCode(String componentCode);

    /**
     * @Author zxx
     * @Description //TODO 根据图表代码查询元组件
     * @Date 10:05 2020/6/28
     * @param chartCode 图表代码
     * @return com.sbr.visualization.controlpanel.componentmanage.model.ComponentManage
     **/
    ComponentManage findByChartCode(String chartCode);
}
