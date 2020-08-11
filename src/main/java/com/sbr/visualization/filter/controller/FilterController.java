package com.sbr.visualization.filter.controller;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.filter.model.Filter;
import com.sbr.visualization.filter.service.IFilterService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 描述：过滤器控制层
 *
 * @author DESKTOP-212O9VU 2020-06-30 11:18:23
 */
@RestController
@RequestMapping("/visualization/api")
public class FilterController extends BaseController {

    @Autowired
    private IFilterService filterService;

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-30 11:18:23
     */
    @GetMapping(value = "/v1/filters")
    public List<Filter> findAllFilter(HttpServletRequest req) {
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, Filter.class);
        return filterService.findByFinder(finder);
    }

    /**
     * <p>根据Id 查询</p>
     *
     * @param id 主键
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-30 11:18:23
     */
    @GetMapping(value = "/v1/filters/{id}")
    public Filter findById(@PathVariable("id") String id) {
        return filterService.findById(id);
    }

    /**
     * <p>新增过滤器</p>
     *
     * @param filter 需要新增的数据
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-30 11:18:23
     */
    @PostMapping(value = "/v1/filters")
    public Filter create(@RequestBody Filter filter) {
        return filterService.create(filter);
    }

    /**
     * <p>删除过滤器</p>
     *
     * @param id 主键
     * @author DESKTOP-212O9VU 2020-06-30 11:18:23
     */
    @DeleteMapping(value = "/v1/filters/{id}")
    public void deleteById(@PathVariable("id") String id) throws Exception {
        filterService.delete(id);
    }

    /**
     * <p>更新过滤器</p>
     *
     * @param filters 数据集合
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-30 11:18:23
     */
    @PatchMapping(value = "/v1/filters/{id}")
    public InfoJson update(@RequestBody List<Filter> filters,@PathVariable(value = "id")String id) throws Exception {
        return filterService.patchUpdate(filters,id);
    }

    /**
     * @Author zxx
     * @Description //TODO 根据数据模型ID，查询过滤器
     * @Date 14:41 2020/6/30
     * @param id 数据模型ID
     * @return java.util.List<com.sbr.visualization.filter.model.Filter>
     **/
    @GetMapping(value = "/v1/filters/data-model/{id}")
    public List<Filter> findByDataModelId(@PathVariable("id") String id) {
        return filterService.findByDataModelId(id);
    }

}