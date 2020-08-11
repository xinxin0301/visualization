package com.sbr.visualization.controlpanel.reportformmanage.controller;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.controlpanel.reportformmanage.model.ReportFormManage;
import com.sbr.visualization.controlpanel.reportformmanage.service.IReportFormManageService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：报表管理控制层
 *
 * @author DESKTOP-212O9VU 2020-06-23 09:53:25
 */
@RestController
@RequestMapping("/visualization/api")
public class ReportFormManageController extends BaseController {

    @Autowired
    private IReportFormManageService reportFormManageService;

    /**
     * <p>根据Id 查询</p>
     *
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 09:53:25
     */
    @GetMapping(value = "/v1/report-form-manages")
    public List<ReportFormManage> findAllReportFormManage(HttpServletRequest req, HttpServletResponse res) {
        List<ReportFormManage> reportFormManageList = new ArrayList<>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, ReportFormManage.class);
        Page<ReportFormManage> page = PageFactory.createFromRequest(req);
        page = reportFormManageService.findByFinderAndPage(finder, page);
        reportFormManageList = page.getContent();
        fillResponseWithPage(res, page);
        return reportFormManageList;
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 09:53:25
     */
    @GetMapping(value = "/v1/report-form-manages/{id}")
    public ReportFormManage findById(@PathVariable("id") String id) {
        return reportFormManageService.findById(id);
    }

    /**
     * <p>新增报表管理</p>
     *
     * @param reportFormManage 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 09:53:25
     */
    @PostMapping(value = "/v1/report-form-manages")
    public ReportFormManage create(@Valid @RequestBody ReportFormManage reportFormManage) {
        return reportFormManageService.create(reportFormManage);
    }

    /**
     * <p>删除报表管理</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-23 09:53:25
     */
    @DeleteMapping(value = "/v1/report-form-manages/{id}")
    public InfoJson deleteById(@PathVariable("id") String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        reportFormManageService.delete(id);
        infoJson.setDescription("删除成功！");
        infoJson.setSuccess(true);
        return infoJson;
    }

    /**
     * <p>更新报表管理</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-23 09:53:25
     */
    @PatchMapping(value = "/v1/report-form-manages/{id}")
    public ReportFormManage update(@PathVariable("id") String id, @RequestBody ReportFormManage reportFormManage) throws Exception {
        reportFormManage.setId(id);
        return reportFormManageService.patchUpdate(reportFormManage);
    }


    /**
     * @Author zxx
     * @Description //TODO 批量删除报表管理
     * @Date 10:16 2020/6/23
     * @param reportFormManage 报表实体
     * @return com.sbr.springboot.json.InfoJson
     **/
    @DeleteMapping("/v1/report-form-manage/batchs")
    public InfoJson batchDelete(@RequestBody ReportFormManage reportFormManage) {
        InfoJson infoJson = new InfoJson();
        if (reportFormManage.getIds() != null && reportFormManage.getIds().size() > 0) {
            //获取ID集合，循环删除
            List<String> idList = reportFormManage.getIds();
            idList.forEach(id -> {
                reportFormManageService.delete(id);
            });
            infoJson.setSuccess(true);
            infoJson.setDescription("删除成功！");
        }
        return infoJson;
    }

}