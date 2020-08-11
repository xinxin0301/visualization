package com.sbr.visualization.controlpanel.controlpanelclassification.controller;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.controlpanel.controlpanelclassification.model.ControlPanelClassification;
import com.sbr.visualization.controlpanel.controlpanelclassification.service.IControlPanelClassificationService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：控制面板分类表控制层
 *
 * @author DESKTOP-212O9VU 2020-06-23 15:01:08
 */
@RestController
@RequestMapping("/visualization/api")
public class ControlPanelClassificationController extends BaseController {

    @Autowired
    private IControlPanelClassificationService controlPanelClassificationService;

    /**
     * <p>根据Id 查询</p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 15:01:08
     */
    @GetMapping(value = "/v1/control-panel-classifications")
    public List<ControlPanelClassification> findAllControlPanelClassification(HttpServletRequest req, HttpServletResponse res) {
        List<ControlPanelClassification> controlPanelClassificationList = new ArrayList<>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, ControlPanelClassification.class);
        Page<ControlPanelClassification> page = PageFactory.createFromRequest(req);
        page = controlPanelClassificationService.findByFinderAndPage(finder, page);
        controlPanelClassificationList = page.getContent();
        fillResponseWithPage(res, page);
        return controlPanelClassificationList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 15:01:08
     */
    @GetMapping(value = "/v1/control-panel-classifications/{id}")
    public ControlPanelClassification findById(@PathVariable("id") String id) {
        return controlPanelClassificationService.findById(id);
    }

    /**
     * <p>新增控制面板分类表</p>
     *
     * @param controlPanelClassification 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 15:01:08
     */
    @PostMapping(value = "/v1/control-panel-classifications")
    public ControlPanelClassification create(@Valid @RequestBody ControlPanelClassification controlPanelClassification) {
        return controlPanelClassificationService.create(controlPanelClassification);
    }

    /**
     * <p>删除控制面板分类表</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-23 15:01:08
     */
    @DeleteMapping(value = "/v1/control-panel-classifications/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        controlPanelClassificationService.delete(id);
        infoJson.setSuccess(true);
        infoJson.setDescription("刪除成功！");
        return infoJson;
    }

    /**
     * <p>更新控制面板分类表</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 15:01:08
     */
    @PatchMapping(value = "/v1/control-panel-classifications/{id}")
    public ControlPanelClassification update(@PathVariable("id") String id, @RequestBody ControlPanelClassification controlPanelClassification) throws Exception {
        controlPanelClassification.setId(id);
        return controlPanelClassificationService.patchUpdate(controlPanelClassification);
    }

}