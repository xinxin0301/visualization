package com.sbr.visualization.controlpanel.componentmanage.dao;

import com.sbr.ms.springdata.repository.IBaseRepository;
import com.sbr.visualization.controlpanel.componentmanage.model.ComponentManage;

import java.util.List;

/**
* 描述：元组件管理DAO
* @author DESKTOP-212O9VU
* @date 2020-06-23 14:58:48
*/
public interface ComponentManageDAO extends IBaseRepository<ComponentManage,String>{

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
     * @Description //TODO 根据组件类型ID，获取元组件
     * @Date 16:28 2020/6/24
     * @param id 组件类型ID
     * @return java.util.List<com.sbr.visualization.controlpanel.componentmanage.model.ComponentManage>
     **/
    List<ComponentManage> findByComponentTypeManageId(String id);

    /**
     * @Author zxx
     * @Description //TODO 根据图表代码查询，元组件
     * @Date 10:05 2020/6/28
     * @param chartCode 图表代码
     * @return com.sbr.visualization.controlpanel.componentmanage.model.ComponentManage
     **/
    ComponentManage findByChartCode(String chartCode);
}
