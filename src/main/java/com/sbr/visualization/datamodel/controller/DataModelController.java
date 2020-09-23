package com.sbr.visualization.datamodel.controller;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.ms.feign.system.dictionary.api.DictionaryFeignClient;
import com.sbr.ms.feign.system.organization.api.OrganizationFeignClient;
import com.sbr.ms.feign.system.organization.model.Organization;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodel.service.IDataModelService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 描述：数据模型管理控制层
 *
 * @author DESKTOP-212O9VU 2020-06-12 15:20:26
 */
@RestController
@RequestMapping("/visualization/api")
public class DataModelController extends BaseController {

    @Autowired
    private IDataModelService dataModelService;

    @Autowired
    private OrganizationFeignClient organizationFeignClient;

    @Autowired
    private DictionaryFeignClient dictionaryFeignClient;

    /**
     * <p>分页查询</p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-12 15:20:26
     */
    @GetMapping(value = "/v1/data-models")
    public List<DataModel> findAllDataModel(HttpServletRequest req, HttpServletResponse res) {
        List<DataModel> dataModelList = new ArrayList<>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, DataModel.class);
        Page<DataModel> page = PageFactory.createFromRequest(req);
        page = dataModelService.findByFinderAndPage(finder, page);
        dataModelList = page.getContent();
        fillResponseWithPage(res, page);
        return dataModelList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-12 15:20:26
     */
    @GetMapping(value = "/v1/data-models/{id}")
    public DataModel findById(@PathVariable("id") String id) {
        return dataModelService.findById(id);
    }

    /**
     * <p>新增数据模型管理</p>
     *
     * @param dataModel 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-12 15:20:26
     */
    @PostMapping(value = "/v1/data-models")
    public DataModel create(@Valid @RequestBody DataModel dataModel) {
        return dataModelService.create(dataModel);
    }

    /**
     * <p>删除数据模型管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-12 15:20:26
     */
    @DeleteMapping(value = "/v1/data-models/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        return dataModelService.deleteDataModel(id);
    }

    /**
     * <p>更新数据模型管理</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-12 15:20:26
     */
    @PatchMapping(value = "/v1/data-models/{id}")
    public DataModel update(@PathVariable("id") String id, @RequestBody DataModel dataModel) throws Exception {
        dataModel.setId(id);
        return dataModelService.patchUpdate(dataModel);
    }

    /**
     * @param tablename 数据表名称
     * @param id        模型Id
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author zxx
     * @Description //TODO 根据表名获取字段名、字段类型
     * @Date 10:20 2020/6/15
     **/
    @GetMapping(value = "/v1/fields/{tablename}/{id}")
    public InfoJson getfieldsByTableName(@PathVariable(value = "tablename") String tablename, @PathVariable(value = "id") String id) throws Exception {
        return dataModelService.getfieldsByTableName(tablename, id);
    }

    /**
     * @param id 数据模型ID
     * @return java.util.List<java.lang.String>
     * @Author zxx
     * @Description //TODO 获取当前数据库下的所有表、或者es下所有索引
     * @Date 16:24 2020/6/11
     **/
    @GetMapping(value = "/v1/datasource-tables/{id}")
    public List<Map<String, Object>> getDatasourseManage(@PathVariable(value = "id") String id) throws Exception {
        return dataModelService.getDatasourseManageAdnTables(id);
    }

    /**
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author zxx
     * @Description //TODO 根据数据模型ID，表关系JSON，查询数据
     * @Date 14:17 2020/6/24
     * @Param dataModel
     **/
    @PostMapping(value = "/v1/data-model/datas/{id}")
    public InfoJson getDataByDataModel(@RequestBody DataModel dataModel, @PathVariable(value = "id") String id) throws Exception {
        dataModel.setId(id);
        return dataModelService.getDataByDataModel(dataModel);
    }

    /**
     * @param dataModel 数据模型
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author zxx
     * @Description //TODO 根据数据模型ID，字段属性查询属性数据,默认500条
     * @Date 10:43 2020/6/28
     **/
    @PostMapping(value = "/v1/data-model/fields/{id}")
    public List<Map<String, String>> getDataByfield(@RequestBody DataModel dataModel, @PathVariable(value = "id") String id) throws Exception {
        dataModel.setId(id);
        return dataModelService.getDataByfield(dataModel);
    }

    /**
     * 查询机构单位类型
     * @return
     */
    @GetMapping(value = "/v1/org-type")
    public List<String> getOrgType() {
        List<String> stringList = new ArrayList<>();
        //获取所有组织机构
        List<Organization> organizationList = organizationFeignClient.findAllOrganization();
        Set<Integer> collect = organizationList.stream().map(organization -> organization.getOrgType()).collect(Collectors.toSet());
        collect.forEach(integer -> {
            String org_type = dictionaryFeignClient.findByGroupAndKey("org_type", integer + "");
            stringList.add(org_type);
        });
        return stringList;
    }

}