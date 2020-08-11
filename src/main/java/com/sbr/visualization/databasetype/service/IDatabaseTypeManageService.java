package com.sbr.visualization.databasetype.service;

import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.visualization.databasetype.model.DatabaseTypeManage;

import java.util.List;

/**
 * <p>数据库类型管理 服务实现层接口</p>
 *
 * @author DESKTOP-212O9VU  2020-06-11 14:48:01
 */

public interface IDatabaseTypeManageService {
    /**
     * <p>根据查询条件查询</p>
     *
     * @param finder 查询条件
     * @return 数据的集合
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    public List<DatabaseTypeManage> findByFinder(Finder finder);

    /**
     * <p>根据Id查询实体</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    public DatabaseTypeManage findById(String id);

    /**
     * <p>根据查询条件查询 带分页</p>
     *
     * @param finder 查询条件
     * @param page   分页信息
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    public Page<DatabaseTypeManage> findByFinderAndPage(Finder finder, Page<DatabaseTypeManage> page);

    /**
     * <p>新增数据库类型管理</p>
     *
     * @param databaseTypeManage 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    public DatabaseTypeManage create(DatabaseTypeManage databaseTypeManage);

    /**
     * <p>根据id删除数据库类型管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    public void delete(String id);

    /**
     * <p>根据实体删除数据库类型管理</p>
     *
     * @param databaseTypeManage 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    public void deleteByEntity(DatabaseTypeManage databaseTypeManage);

    /**
     * <p>更新部分数据</p>
     *
     * @param databaseTypeManage 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    public DatabaseTypeManage patchUpdate(DatabaseTypeManage databaseTypeManage);

    /**
     * <p>更新全部数据</p>
     *
     * @param databaseTypeManage 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    public DatabaseTypeManage putUpdate(DatabaseTypeManage databaseTypeManage);

}
