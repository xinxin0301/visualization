package com.sbr.visualization.databasetype.controller;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.databasetype.model.DatabaseTypeManage;
import com.sbr.visualization.databasetype.service.IDatabaseTypeManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：数据库类型管理控制层
 *
 * @author DESKTOP-212O9VU 2020-06-11 14:48:01
 */
@RestController
@RequestMapping("/visualization/api")
public class DatabaseTypeManageController extends BaseController {

    @Autowired
    private IDatabaseTypeManageService databaseTypeManageService;

    /**
     * <p>分页查询所有数据源类型</p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    @GetMapping(value = "/v1/database-manages")
    public List<DatabaseTypeManage> findAllDatabaseTypeManage(HttpServletRequest req, HttpServletResponse res) {
        List<DatabaseTypeManage> databaseTypeManageList = new ArrayList<DatabaseTypeManage>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, DatabaseTypeManage.class);
        Page<DatabaseTypeManage> page = PageFactory.createFromRequest(req);
        page = databaseTypeManageService.findByFinderAndPage(finder, page);
        databaseTypeManageList = page.getContent();
        fillResponseWithPage(res, page);
        return databaseTypeManageList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    @GetMapping(value = "/v1/database-manages/{id}")
    public DatabaseTypeManage findById(@PathVariable("id") String id) {
        return databaseTypeManageService.findById(id);
    }

    /**
     * <p>新增数据库类型管理</p>
     *
     * @param databaseTypeManage 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    @PostMapping(value = "/v1/database-manages")
    public DatabaseTypeManage create(@Valid @RequestBody DatabaseTypeManage databaseTypeManage) {
        return databaseTypeManageService.create(databaseTypeManage);
    }

    /**
     * <p>删除数据库类型管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    @DeleteMapping(value = "/v1/database-manages/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        databaseTypeManageService.delete(id);
        infoJson.setSuccess(true);
        infoJson.setDescription("删除成功！");
        return infoJson;
    }

    /**
     * <p>更新数据库类型管理</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-11 14:48:01
     */
    @PatchMapping(value = "/v1/database-manages/{id}")
    public DatabaseTypeManage update(@PathVariable("id") String id, @RequestBody DatabaseTypeManage databaseTypeManage) throws Exception {
        databaseTypeManage.setId(id);
        return databaseTypeManageService.patchUpdate(databaseTypeManage);
    }

}