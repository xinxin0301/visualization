package com.sbr.visualization.controlpanel.controlpanelclassification.service;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.visualization.controlpanel.controlpanelclassification.model.ControlPanelClassification;

import java.util.List;

/**
* <p>控制面板分类表 服务实现层接口</p>
* @author DESKTOP-212O9VU  2020-06-23 15:01:08
*/

public interface IControlPanelClassificationService {
    /**
    *
    * <p>根据查询条件查询</p>
    * @param finder 查询条件
    * @return 数据的集合
    * @author DESKTOP-212O9VU 2020-06-23 15:01:08
    */
    public List<ControlPanelClassification> findByFinder(Finder finder);

    /**
    *
    * <p>根据Id查询实体</p>
    * @param id 主键
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:01:08
    */
    public ControlPanelClassification findById(String id);

    /**
    *
    * <p>根据查询条件查询 带分页</p>
    * @param finder 查询条件
    * @param page 分页信息
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:01:08
    */
    public Page<ControlPanelClassification> findByFinderAndPage(Finder finder, Page<ControlPanelClassification> page);

    /**
    *
    * <p>新增控制面板分类表</p>
    * @param controlPanelClassification 需要新增的数据
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:01:08
    */
    public ControlPanelClassification create(ControlPanelClassification controlPanelClassification);

    /**
    *
    * <p>根据id删除控制面板分类表</p>
    * @param id 主键
    * @author DESKTOP-212O9VU 2020-06-23 15:01:08
    */
    public void delete(String id);

    /**
    *
    * <p>根据实体删除控制面板分类表</p>
    * @param controlPanelClassification 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:01:08
    */
    public void deleteByEntity(ControlPanelClassification controlPanelClassification);

    /**
    *
    * <p>更新部分数据</p>
    * @param controlPanelClassification 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:01:08
    */
    public ControlPanelClassification patchUpdate(ControlPanelClassification controlPanelClassification);

    /**
    *
    * <p>更新全部数据</p>
    * @param controlPanelClassification 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 15:01:08
    */
    public ControlPanelClassification putUpdate(ControlPanelClassification controlPanelClassification);

}
