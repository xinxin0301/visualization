package com.sbr.visualization.datasourcemanage.service;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;

import java.util.List;

/**
 * <p>数据源管理 服务实现层接口</p>
 *
 * @author DESKTOP-212O9VU  2020-06-11 15:07:07
 */

public interface IDatasourceManageService {
    /**
     * <p>根据查询条件查询</p>
     *
     * @param finder 查询条件
     * @return 数据的集合
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    public List<DatasourceManage> findByFinder(Finder finder);

    /**
     * <p>根据Id查询实体</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    public DatasourceManage findById(String id);

    /**
     * <p>根据查询条件查询 带分页</p>
     *
     * @param finder 查询条件
     * @param page   分页信息
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    public Page<DatasourceManage> findByFinderAndPage(Finder finder, Page<DatasourceManage> page);

    /**
     * <p>新增数据源管理</p>
     *
     * @param datasourceManage 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    public DatasourceManage create(DatasourceManage datasourceManage);

    /**
     * <p>根据id删除数据源管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    public void delete(String id);

    /**
     * <p>根据实体删除数据源管理</p>
     *
     * @param datasourceManage 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    public void deleteByEntity(DatasourceManage datasourceManage);

    /**
     * <p>更新部分数据</p>
     *
     * @param datasourceManage 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    public DatasourceManage patchUpdate(DatasourceManage datasourceManage);

    /**
     * <p>更新全部数据</p>
     *
     * @param datasourceManage 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    public DatasourceManage putUpdate(DatasourceManage datasourceManage);

}
