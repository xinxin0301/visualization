package com.sbr.visualization.controlpanel.reportformmanage.service;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.visualization.controlpanel.reportformmanage.model.ReportFormManage;

import java.util.List;

/**
* <p>报表管理 服务实现层接口</p>
* @author DESKTOP-212O9VU  2020-06-23 09:53:25
*/

public interface IReportFormManageService {
    /**
    *
    * <p>根据查询条件查询</p>
    * @param finder 查询条件
    * @return 数据的集合
    * @author DESKTOP-212O9VU 2020-06-23 09:53:25
    */
    public List<ReportFormManage> findByFinder(Finder finder);

    /**
    *
    * <p>根据Id查询实体</p>
    * @param id 主键
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 09:53:25
    */
    public ReportFormManage findById(String id);

    /**
    *
    * <p>根据查询条件查询 带分页</p>
    * @param finder 查询条件
    * @param page 分页信息
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 09:53:25
    */
    public Page<ReportFormManage> findByFinderAndPage(Finder finder, Page<ReportFormManage> page);

    /**
    *
    * <p>新增报表管理</p>
    * @param reportFormManage 需要新增的数据
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 09:53:25
    */
    public ReportFormManage create(ReportFormManage reportFormManage);

    /**
    *
    * <p>根据id删除报表管理</p>
    * @param id 主键
    * @author DESKTOP-212O9VU 2020-06-23 09:53:25
    */
    public void delete(String id);

    /**
    *
    * <p>根据实体删除报表管理</p>
    * @param reportFormManage 实体
    * @author DESKTOP-212O9VU 2020-06-23 09:53:25
    */
    public void deleteByEntity(ReportFormManage reportFormManage);

    /**
    *
    * <p>更新部分数据</p>
    * @param reportFormManage 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 09:53:25
    */
    public ReportFormManage patchUpdate(ReportFormManage reportFormManage);

    /**
    *
    * <p>更新全部数据</p>
    * @param reportFormManage 需要更新的数据实体
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-23 09:53:25
    */
    public ReportFormManage putUpdate(ReportFormManage reportFormManage);

}
