package com.sbr.visualization.datasourcemanage.controller;

import com.sbr.common.finder.Filter;
import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.databasetype.service.IDatabaseTypeManageService;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodel.service.IDataModelService;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.datasourcemanage.service.IDatasourceManageService;
import com.sbr.visualization.handler.service.HandlerService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：数据源管理控制层
 *
 * @author DESKTOP-212O9VU 2020-06-011 15:07:07
 */
@RestController
@RequestMapping("/visualization/api")
public class DatasourceManageController extends BaseController {

    @Autowired
    private IDatasourceManageService datasourseManageService;

    @Autowired
    private IDatabaseTypeManageService databaseTypeManageService;

    @Autowired
    private IDataModelService dataModelService;

    @Autowired//策略处理器接口
    private HandlerService handlerService;

    /**
     * <p>根据Id 查询</p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    @GetMapping(value = "/v1/datasource-manages")
    public List<DatasourceManage> findAllDatasourseManage(HttpServletRequest req, HttpServletResponse res) {
        List<DatasourceManage> databaseTypeManageList = new ArrayList<DatasourceManage>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, DatasourceManage.class);
        Page<DatasourceManage> page = PageFactory.createFromRequest(req);
        page = datasourseManageService.findByFinderAndPage(finder, page);
        databaseTypeManageList = page.getContent();
        fillResponseWithPage(res, page);
        return databaseTypeManageList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    @GetMapping(value = "/v1/datasource-manages/{id}")
    public DatasourceManage findById(@PathVariable("id") String id) {
        return datasourseManageService.findById(id);
    }

    /**
     * <p>新增数据源管理</p>
     *
     * @param datasourceManage 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    @PostMapping(value = "/v1/datasource-manages")
    public DatasourceManage create(@Valid @RequestBody DatasourceManage datasourceManage) {
        return datasourseManageService.create(datasourceManage);
    }

    /**
     * <p>删除数据源管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    @DeleteMapping(value = "/v1/datasource-manages/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        Finder finder = new Finder();
        finder.appendFilter("datasourceManage.id", id, Filter.OperateType.OPERATE_EQUAL);
        List<DataModel> dataModels = dataModelService.findByFinder(finder);
        if (dataModels != null && dataModels.size() > 0) {
            infoJson.setSuccess(false);
            infoJson.setDescription("当前数据源，正在被数据模型使用不能删除！");
        } else {
            datasourseManageService.delete(id);
            infoJson.setSuccess(true);
            infoJson.setDescription("删除成功！");
        }
        return infoJson;
    }

    /**
     * <p>更新数据源管理</p>
     *
     * @param id               主键
     * @param datasourceManage 数据源对象
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 15:07:07
     */
    @PatchMapping(value = "/v1/datasource-manages/{id}")
    public DatasourceManage update(@PathVariable("id") String id, @RequestBody DatasourceManage datasourceManage) throws Exception {
        datasourceManage.setId(id);
        return datasourseManageService.patchUpdate(datasourceManage);
    }

    /**
     * @param datasourceManage 数据源对象
     * @return com.sbr.springboot.json.InfoJson 成功、失败信息
     * @Author zxx
     * @Description //TODO 测试连接数据库
     * @Date 15:43 2020/6/2
     **/
    @PostMapping(value = "/v1/connect-tests")
    public InfoJson connectTest(@Valid @RequestBody DatasourceManage datasourceManage) {
        InfoJson infoJson = null;
        //调用策略模式接口
        if (datasourceManage.getDatabaseTypeManage() != null && StringUtils.isNotEmpty(datasourceManage.getDatabaseTypeManage().getId())) {
            infoJson = handlerService.verificationDatabaseConnect(datasourceManage);
        }
        return infoJson;
    }


}