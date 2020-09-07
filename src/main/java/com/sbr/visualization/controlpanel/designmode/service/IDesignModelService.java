package com.sbr.visualization.controlpanel.designmode.service;

import com.sbr.common.entity.Tree;
import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.visualization.controlpanel.designmode.model.DesignModel;

import java.util.List;

/**
 * <p>设计模型 服务实现层接口</p>
 *
 * @author DESKTOP-212O9VU  2020-06-02 10:54:32
 */

public interface IDesignModelService {
    /**
     * <p>根据查询条件查询</p>
     *
     * @param finder 查询条件
     * @return 数据的集合
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    public List<DesignModel> findByFinder(Finder finder);

    /**
     * <p>根据Id查询实体</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    public DesignModel findById(String id);

    /**
     * <p>根据查询条件查询 带分页</p>
     *
     * @param finder 查询条件
     * @param page   分页信息
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    public Page<DesignModel> findByFinderAndPage(Finder finder, Page<DesignModel> page);

    /**
     * <p>新增设计模型</p>
     *
     * @param designModel 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    public DesignModel create(DesignModel designModel);

    /**
     * <p>根据id删除设计模型</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    public void delete(String id);

    /**
     * <p>根据实体删除设计模型</p>
     *
     * @param designModel 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    public void deleteByEntity(DesignModel designModel);

    /**
     * <p>更新部分数据</p>
     *
     * @param designModel 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    public DesignModel patchUpdate(DesignModel designModel);

    /**
     * <p>更新全部数据</p>
     *
     * @param designModel 需要更新的数据实体
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    public DesignModel putUpdate(DesignModel designModel);

    /**
     * @Author 张鑫鑫
     * @Description //TODO 大屏设计模型树
     * @Date 16:07 2020/9/7
     * @Param [typeManages]
     * @return java.util.List<com.sbr.common.entity.Tree>
     **/
    List<Tree> constructTree(List<DesignModel> designModelList);

    Tree constructTree(DesignModel designModel);

    List<DesignModel> structureChildrenId(List<DesignModel> designModelList, String id);
}
