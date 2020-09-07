package com.sbr.visualization.controlpanel.designmode.controller;

import com.sbr.common.entity.Tree;
import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.springboot.rest.exception.RestIllegalArgumentException;
import com.sbr.visualization.controlpanel.componenttypemanage.model.ComponentTypeManage;
import com.sbr.visualization.controlpanel.designmode.model.DesignModel;
import com.sbr.visualization.controlpanel.designmode.service.IDesignModelService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
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


    /**
     * @return java.util.List<com.sbr.common.entity.Tree>
     * @Author 张鑫鑫
     * @Description //TODO 大屏设计模型树
     * @Date 16:13 2020/9/7
     * @Param [req, res]
     **/
    @GetMapping(value = "/v1/design-model/trees")
    public List<Tree> trees(HttpServletRequest req, HttpServletResponse res) {
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, DesignModel.class);
        List<DesignModel> designModelList = designModelService.findByFinder(finder);
        return designModelService.constructTree(designModelList);
    }

    /**
     * @return com.sbr.visualization.controlpanel.designmode.model.DesignModel
     * @Author 张鑫鑫
     * @Description //TODO 新增大屏设计模型树
     * @Date 16:16 2020/9/7
     * @Param [designModel]
     **/
    @PostMapping(value = "/v1/design-model/trees")
    public DesignModel createTree(@Valid @RequestBody DesignModel designModel) {
        //父节点为空，设置父节点为NULL
        if (designModel.getParent() == null || StringUtils.isEmpty(designModel.getParent().getId())) {
            designModel.setParent(null);
        }
        return designModelService.create(designModel);
    }


    /**
     * @return com.sbr.visualization.controlpanel.componenttypemanage.model.ComponentTypeManage
     * @Author 张鑫鑫
     * @Description //TODO 编辑大屏设计模型树
     * @Date 16:17 2020/9/7
     * @Param [id, componentTypeManage]
     **/
    @PatchMapping(value = "/v1/design-model/trees/{id}")
    public DesignModel updateTree(@PathVariable("id") String id, @RequestBody DesignModel designModel) throws Exception {
        designModel.setId(id);
        //上级节点不能选择自己
        if (designModel.getParent() != null && StringUtils.isNotEmpty(designModel.getParent().getId())) {
            if (designModel.getParent().getId().equals(id)) {
                throw new RestIllegalArgumentException("上级类型不能选择自己!");
            }
            //上级节点不能选择为自己的子节点
            List<DesignModel> designModelList = new ArrayList<>();
            designModelService.structureChildrenId(designModelList, id);
            if (designModelList != null && designModelList.size() > 0) {
                designModelList.stream().forEach(designModel1 -> {
                    if (designModel1.getId().equals(id)) {
                        throw new RestIllegalArgumentException("上级类型不能选择自己的子节点!");
                    }
                });
            }
        }
        return designModelService.patchUpdate(designModel);
    }


    /**
     * @Author 张鑫鑫
     * @Description //TODO 删除
     * @Date 16:21 2020/9/7
     * @Param [id]
     * @return com.sbr.springboot.json.InfoJson
     **/
    @DeleteMapping(value = "/v1/design-model/trees/{id}")
    public InfoJson deleteTreeById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        if (StringUtils.isNotEmpty(id)) {
            List<DesignModel> designModelList = new ArrayList<>();
            designModelService.structureChildrenId(designModelList, id);
            if (designModelList != null && designModelList.size() > 0) {
                infoJson.setSuccess(false);
                infoJson.setDescription("当前节点下存在子节点不允许删除，请先删除子节点！");
            } else {
                designModelService.delete(id);
                infoJson.setSuccess(true);
                infoJson.setDescription("删除成功！");
            }
        }
        return infoJson;
    }

}