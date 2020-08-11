package com.sbr.visualization.controlpanel.designmode.controller;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.controlpanel.designmode.model.DesignModel;
import com.sbr.visualization.controlpanel.designmode.service.IDesignModelService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：设计模型控制层
 *
 * @author DESKTOP-212O9VU 2020-06-02 10:54:32
 */
@RestController
@RequestMapping("/visualization/api")
public class DesignModelController extends BaseController {

    @Autowired
    private IDesignModelService designModelService;

    /**
     * <p>查询所有数据模型</p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    @GetMapping("/v1/design-models")
    public List<DesignModel> findAllDesignModel(HttpServletRequest req, HttpServletResponse res) {
        List<DesignModel> designModelList = new ArrayList<>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, DesignModel.class);
        Page<DesignModel> page = PageFactory.createFromRequest(req);
        page = designModelService.findByFinderAndPage(finder, page);
        designModelList = page.getContent();
        fillResponseWithPage(res, page);
        return designModelList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    @GetMapping("/v1/design-models/{id}")
    public DesignModel findById(@PathVariable("id") String id) {
        return designModelService.findById(id);
    }

    /**
     * <p>新增设计模型</p>
     *
     * @param designModel 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    @PostMapping("/v1/design-models")
    public DesignModel create(@Valid @RequestBody DesignModel designModel) {
        return designModelService.create(designModel);
    }

    /**
     * <p>删除设计模型</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    @DeleteMapping("/v1/design-models/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        designModelService.delete(id);
        infoJson.setSuccess(true);
        infoJson.setDescription("删除成功！");
        return infoJson;
    }

    /**
     * <p>更新设计模型</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-02 10:54:32
     */
    @PatchMapping("/v1/design-models/{id}")
    public DesignModel update(@PathVariable("id") String id, @RequestBody DesignModel designModel) throws Exception {
        designModel.setId(id);
        return designModelService.patchUpdate(designModel);
    }


    /**
     * @param designModel 设计模型对象
     * @return com.sbr.springboot.json.InfoJson 成功、失败
     * @Author zxx
     * @Description //TODO 批量删除设计模型
     * @Date 11:36 2020/6/3
     **/
    @DeleteMapping("/v1/design-model/batchs")
    public InfoJson batchDelete(@RequestBody DesignModel designModel) {
        InfoJson infoJson = new InfoJson();
        if (designModel.getIds() != null && designModel.getIds().size() > 0) {
            //获取ID集合，循环删除
            List<String> idList = designModel.getIds();
            idList.forEach(id -> {
                designModelService.delete(id);
            });
            infoJson.setSuccess(true);
            infoJson.setDescription("删除成功！");
        }
        return infoJson;
    }

}