package com.sbr.visualization.mappingmanage.controller;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datamodelattribute.service.IDataModelAttributeService;
import com.sbr.visualization.mappingmanage.model.MappingManage;
import com.sbr.visualization.mappingmanage.service.IMappingManageService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：数据映射管理控制层
 *
 * @author DESKTOP-212O9VU 2020-06-15 16:23:39
 */
@RestController
@RequestMapping("/visualization/api")
public class MappingManageController extends BaseController {

    @Autowired
    private IMappingManageService mappingManageService;

    @Autowired
    private IDataModelAttributeService dataModelAttributeService;

    /**
     * <p>分页查询映射管理</p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 16:23:39
     */
    @GetMapping(value = "/v1/mapping-manages")
    public List<MappingManage> findAllMappingManage(HttpServletRequest req, HttpServletResponse res) {
        List<MappingManage> mappingManageList = new ArrayList<MappingManage>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, MappingManage.class);
        Page<MappingManage> page = PageFactory.createFromRequest(req);
        page = mappingManageService.findByFinderAndPage(finder, page);
        mappingManageList = page.getContent();
        fillResponseWithPage(res, page);
        return mappingManageList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 16:23:39
     */
    @GetMapping(value = "/v1/mapping-manages/{id}")
    public MappingManage findById(@PathVariable("id") String id) {
        return mappingManageService.findById(id);
    }

    /**
     * <p>新增数据映射管理</p>
     *
     * @param mappingManage 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 16:23:39
     */
    @PostMapping(value = "/v1/mapping-manages")
    public MappingManage create(@RequestBody MappingManage mappingManage) {
        return mappingManageService.create(mappingManage);
    }

    /**
     * <p>删除数据映射管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-15 16:23:39
     */
    @DeleteMapping(value = "/v1/mapping-manages/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        List<DataModelAttribute> dataModelAttributeList = dataModelAttributeService.findByMappingManageId(id);
        if (dataModelAttributeList != null && dataModelAttributeList.size() > 0) {
            infoJson.setSuccess(false);
            infoJson.setDescription("当前数据映射已被使用不能删除！");
            return infoJson;
        }
        mappingManageService.delete(id);
        infoJson.setSuccess(true);
        infoJson.setDescription("删除成功");
        return infoJson;
    }

    /**
     * <p>更新数据映射管理</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 16:23:39
     */
    @PatchMapping(value = "/v1/mapping-manages/{id}")
    public MappingManage update(@PathVariable("id") String id, @RequestBody MappingManage mappingManage) throws Exception {
        mappingManage.setId(id);
        return mappingManageService.patchUpdate(mappingManage);
    }

    /**
     * @param mappingManage 映射管理实体
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 创建映射管理和多个映射数据
     * @Date 10:10 2020/6/16
     **/
    @PostMapping(value = "/v1/mapping-manage-datas")
    public MappingManage batchMappingManageAndDataSave(@RequestBody MappingManage mappingManage) {
        return mappingManageService.batchMappingManageAndDataSave(mappingManage);
    }


    /**
     * @param mappingManage 映射管理实体
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 编辑映射管理和多个映射数据
     * @Date 10:10 2020/6/16
     **/
    @PatchMapping(value = "/v1/mapping-manage-datas/{id}")
    public MappingManage batchMappingManageAndDataPatch(@RequestBody MappingManage mappingManage, @PathVariable(value = "id") String id) {
        mappingManage.setId(id);
        return mappingManageService.batchMappingManageAndDataPatch(mappingManage);
    }


    /**
     * @param id 映射管理ID
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 删除映射关联多个映射数据
     * @Date 10:10 2020/6/16
     **/
    @DeleteMapping(value = "/v1/mapping-manage-datas/{id}")
    public InfoJson deleteMappingManageAndData(@PathVariable(value = "id") String id) {
        InfoJson infoJson = new InfoJson();
        List<DataModelAttribute> dataModelAttributeList = dataModelAttributeService.findByMappingManageId(id);
        if (dataModelAttributeList != null && dataModelAttributeList.size() > 0) {
            infoJson.setSuccess(false);
            infoJson.setDescription("当前数据映射已被使用不能删除！");
            return infoJson;
        }
        mappingManageService.deleteMappingManageAndData(id);
        infoJson.setDescription("删除成功!");
        infoJson.setSuccess(true);
        return infoJson;
    }


}