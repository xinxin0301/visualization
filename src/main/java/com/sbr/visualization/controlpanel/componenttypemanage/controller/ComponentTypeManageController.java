package com.sbr.visualization.controlpanel.componenttypemanage.controller;

import com.sbr.common.entity.Tree;
import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.springboot.rest.exception.RestIllegalArgumentException;
import com.sbr.visualization.controlpanel.componenttypemanage.model.ComponentTypeManage;
import com.sbr.visualization.controlpanel.componenttypemanage.service.IComponentTypeManageService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：组件类型管理控制层
 *
 * @author DESKTOP-212O9VU 2020-06-23 15:31:49
 */
@RestController
@RequestMapping("/visualization/api")
public class ComponentTypeManageController extends BaseController {

    @Autowired
    private IComponentTypeManageService componentTypeManageService;

    /**
     * <p>根据Id 查询</p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 15:31:49
     */
    @GetMapping(value = "/v1/component-type-manages")
    public List<ComponentTypeManage> findAllComponentTypeManage(HttpServletRequest req, HttpServletResponse res) {
        List<ComponentTypeManage> componentManageList = new ArrayList<>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, ComponentTypeManage.class);
        Page<ComponentTypeManage> page = PageFactory.createFromRequest(req);
        page = componentTypeManageService.findByFinderAndPage(finder, page);
        componentManageList = page.getContent();
        fillResponseWithPage(res, page);
        return componentManageList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 15:31:49
     */
    @GetMapping(value = "/v1/component-type-manages/{id}")
    public ComponentTypeManage findById(@PathVariable("id") String id) {
        return componentTypeManageService.findById(id);
    }

    /**
     * <p>新增组件类型管理</p>
     *
     * @param componentTypeManage 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 15:31:49
     */
    @PostMapping(value = "/v1/component-type-manages")
    public ComponentTypeManage create(@Valid @RequestBody ComponentTypeManage componentTypeManage) {
        //父节点为空，设置父节点为NULL
        if (componentTypeManage.getParent() == null || StringUtils.isEmpty(componentTypeManage.getParent().getId())) {
            componentTypeManage.setParent(null);
        }
        return componentTypeManageService.create(componentTypeManage);
    }

    /**
     * <p>删除组件类型管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-23 15:31:49
     */
    @DeleteMapping(value = "/v1/component-type-manages/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        if (StringUtils.isNotEmpty(id)) {
            List<ComponentTypeManage> typeManageList = new ArrayList<>();
            componentTypeManageService.structureChildrenId(typeManageList, id);
            if (typeManageList != null && typeManageList.size() > 0) {
                infoJson.setSuccess(false);
                infoJson.setDescription("当前节点下存在子节点不允许删除，请先删除子节点！");
            }else{
                componentTypeManageService.delete(id);
                infoJson.setSuccess(true);
                infoJson.setDescription("删除成功！");
            }
        }
        return infoJson;
    }

    /**
     * <p>更新组件类型管理</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 15:31:49
     */
    @PatchMapping(value = "/v1/component-type-manages/{id}")
    public ComponentTypeManage update(@PathVariable("id") String id, @RequestBody ComponentTypeManage componentTypeManage) throws Exception {
        componentTypeManage.setId(id);

        //上级节点不能选择自己
        if (componentTypeManage.getParent() != null && StringUtils.isNotEmpty(componentTypeManage.getParent().getId())) {
            if (componentTypeManage.getParent().getId().equals(id)) {
                throw new RestIllegalArgumentException("上级类型不能选择自己!");
            }

            //上级节点不能选择为自己的子节点
            List<ComponentTypeManage> componentTypeManageList = new ArrayList<>();
            componentTypeManageService.structureChildrenId(componentTypeManageList, id);
            if (componentTypeManageList != null && componentTypeManageList.size() > 0) {
                componentTypeManageList.stream().forEach(componentTypeManage1 -> {
                    if (componentTypeManage1.getId().equals(id)) {
                        throw new RestIllegalArgumentException("上级类型不能选择自己的子节点!");
                    }
                });
            }

        }
        return componentTypeManageService.patchUpdate(componentTypeManage);
    }


    /**
     * @param req
     * @param res
     * @return java.util.List<com.sun.source.tree.Tree>
     * @Author zxx
     * @Description //TODO 组件类型树查询
     * @Date 15:46 2020/6/3
     **/
    @GetMapping(value = "/v1/component-type-manages/trees")
    public List<Tree> trees(HttpServletRequest req, HttpServletResponse res) {
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, ComponentTypeManage.class);
        List<ComponentTypeManage> typeManages = componentTypeManageService.findByFinder(finder);
        return componentTypeManageService.constructTree(typeManages);
    }


    /**
     * @Author zxx
     * @Description //TODO 获取所有组件类型，包含组件类型下的元组件
     * @Date 16:21 2020/6/24
     * @return
     **/
    @GetMapping(value = "/v1/component-type-manages/tree/components")
    public List<Tree> treesAndComponent(HttpServletRequest req, HttpServletResponse res){
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, ComponentTypeManage.class);
        List<ComponentTypeManage> typeManages = componentTypeManageService.findByFinder(finder);
        return componentTypeManageService.constructTreeAndComponent(typeManages);
    }


}