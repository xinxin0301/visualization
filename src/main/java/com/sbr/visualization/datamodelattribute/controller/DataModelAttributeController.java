package com.sbr.visualization.datamodelattribute.controller;

import com.sbr.common.finder.Filter;
import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodel.service.IDataModelService;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datamodelattribute.service.IDataModelAttributeService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：数据模型维度表控制层
 *
 * @author DESKTOP-212O9VU 2020-06-15 11:05:32
 */
@RestController
@RequestMapping("/visualization/api")
public class DataModelAttributeController extends BaseController {

    @Autowired
    private IDataModelAttributeService dataModelDimensionService;

    @Autowired
    private IDataModelService dataModelService;

    /**
     * <p>根据Id 查询</p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    @GetMapping(value = "/v1/data-model-attributes")
    public List<DataModelAttribute> findAllDataModelDimension(HttpServletRequest req, HttpServletResponse res) {
        List<DataModelAttribute> dataModelList = new ArrayList<>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, DataModelAttribute.class);
        Page<DataModelAttribute> page = PageFactory.createFromRequest(req);
        page = dataModelDimensionService.findByFinderAndPage(finder, page);
        dataModelList = page.getContent();
        fillResponseWithPage(res, page);
        return dataModelList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    @GetMapping(value = "/v1/data-model-attributes/{id}")
    public DataModelAttribute findById(@PathVariable("id") String id) {
        return dataModelDimensionService.findById(id);
    }

    /**
     * <p>新增数据模型维度表</p>
     *
     * @param dataModelDimension 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    @PostMapping(value = "/v1/data-model-attributes")
    public DataModelAttribute create(@Valid @RequestBody DataModelAttribute dataModelDimension) {
        return dataModelDimensionService.create(dataModelDimension);
    }

    /**
     * <p>删除数据模型维度表</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    @DeleteMapping(value = "/v1/data-model-attributes/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        dataModelDimensionService.delete(id);
        infoJson.setSuccess(true);
        infoJson.setDescription("删除成功！");
        return infoJson;
    }

    /**
     * <p>更新数据模型维度表</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    @PatchMapping(value = "/v1/data-model-attributes/{id}")
    public DataModelAttribute update(@PathVariable("id") String id, @RequestBody DataModelAttribute dataModelDimension) throws Exception {
        dataModelDimension.setId(id);
        return dataModelDimensionService.patchUpdate(dataModelDimension);
    }


    /**
     * <p>批量新增数据模型属性</p>
     *
     * @param list 需要新增的数据集合
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    @PostMapping(value = "/v1/data-model-batch-attributes/{id}")
    public InfoJson create(@RequestBody List<DataModelAttribute> list, @PathVariable(value = "id") String id) {
        //设置数据模型
        DataModel dataModel = dataModelService.findById(id);
        if (dataModel == null) {
            InfoJson infoJson = new InfoJson();
            infoJson.setSuccess(false);
            infoJson.setDescription("没有对应的数据模型");
            return infoJson;
        }
        if (list != null) {
            list.stream().forEach(dataModelAttribute -> {
                dataModelAttribute.setDataModel(dataModel);
            });
        }
        return dataModelDimensionService.batchCreate(list);
    }

    /**
     * <p>批量修改、新增数据模型属性，先删除在新增</p>
     *
     * @param list 需要修改的数据集合
     * @param id   数据模型ID
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 11:05:32
     */
    @PatchMapping(value = "/v1/data-model-batch-attributes/{id}")
    public InfoJson batchpatch(@RequestBody List<DataModelAttribute> list, @PathVariable(value = "id") String id) throws Exception {
        return dataModelDimensionService.batchpatch(list, id);
    }

    /**
     * @param id 数据模型ID
     * @return java.util.List<com.sbr.visualization.datamodelattribute.model.DataModelAttribute>
     * @Author zxx
     * @Description //TODO 根据数据模型ID，查询数据模型属性数据
     * @Date 14:40 2020/6/16
     **/
    @GetMapping(value = "/v1/data-model/{id}/attributes")
    public List<DataModelAttribute> findDataModelAttributeByDataModelId(@PathVariable(value = "id") String id,HttpServletRequest request) {
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(request, DataModelAttribute.class);
        finder.appendFilter("dataModel.id",id, Filter.OperateType.OPERATE_EQUAL);
        return dataModelDimensionService.findByFinder(finder);
    }


}