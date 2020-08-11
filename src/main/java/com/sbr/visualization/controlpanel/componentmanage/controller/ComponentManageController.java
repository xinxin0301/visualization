package com.sbr.visualization.controlpanel.componentmanage.controller;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.springboot.rest.exception.RestResourceAlreadyExistException;
import com.sbr.visualization.controlpanel.componentmanage.model.ComponentManage;
import com.sbr.visualization.controlpanel.componentmanage.service.IComponentManageService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：元组件管理控制层
 *
 * @author DESKTOP-212O9VU 2020-06-23 14:58:48
 */
@RestController
@RequestMapping("/visualization/api")
public class ComponentManageController extends BaseController {

    @Autowired
    private IComponentManageService componentManageService;

    /**
     * <p>分页查询/p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 14:58:48
     */
    @GetMapping(value = "/v1/component-manages")
    public List<ComponentManage> findAllComponentManage(HttpServletRequest req, HttpServletResponse res) {
        List<ComponentManage> componentManageList = new ArrayList<>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, ComponentManage.class);
        Page<ComponentManage> page = PageFactory.createFromRequest(req);
        page = componentManageService.findByFinderAndPage(finder, page);
        componentManageList = page.getContent();
        fillResponseWithPage(res, page);
        return componentManageList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 14:58:48
     */
    @GetMapping(value = "/v1/component-manages/{id}")
    public ComponentManage findById(@PathVariable("id") String id) {
        return componentManageService.findById(id);
    }

    /**
     * <p>新增元组件管理</p>
     *
     * @param componentManage 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 14:58:48
     */
    @PostMapping(value = "/v1/component-manages")
    public ComponentManage create(@Valid @RequestBody ComponentManage componentManage) {

        if (StringUtils.isNotEmpty(componentManage.getChartCode())) {
            ComponentManage componentManage1 = componentManageService.findByChartCode(componentManage.getChartCode());
            if (componentManage1 != null) {
                throw new RestResourceAlreadyExistException("图表代码重复!");
            }
        }
        return componentManageService.create(componentManage);
    }

    /**
     * <p>删除元组件管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-23 14:58:48
     */
    @DeleteMapping(value = "/v1/component-manages/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        ComponentManage componentManage = componentManageService.findById(id);
        if(componentManage == null){
            infoJson.setSuccess(false);
            infoJson.setDescription("刪除组件不存在！");
            return infoJson;
        }
        componentManageService.delete(id);
        infoJson.setSuccess(true);
        infoJson.setDescription("刪除成功！");
        return infoJson;
    }

    /**
     * <p>更新元组件管理</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 14:58:48
     */
    @PatchMapping(value = "/v1/component-manages/{id}")
    public ComponentManage update(@PathVariable("id") String id, @RequestBody ComponentManage componentManage) throws Exception {
        componentManage.setId(id);

        if (StringUtils.isNotEmpty(componentManage.getChartCode())) {
            ComponentManage componentManage1 = componentManageService.findByChartCode(componentManage.getChartCode());
            if (componentManage1 != null && !componentManage1.getId().equals(id)) {
                throw new RestResourceAlreadyExistException("图表代码重复!");
            }
        }
        return componentManageService.patchUpdate(componentManage);
    }

}