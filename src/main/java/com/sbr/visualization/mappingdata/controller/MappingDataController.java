package com.sbr.visualization.mappingdata.controller;

import com.sbr.common.finder.Finder;
import com.sbr.common.finder.FinderFactory;
import com.sbr.common.page.Page;
import com.sbr.common.page.PageFactory;
import com.sbr.springboot.controller.BaseController;
import com.sbr.springboot.json.InfoJson;
import com.sbr.visualization.mappingdata.model.MappingData;
import com.sbr.visualization.mappingdata.service.IMappingDataService;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
* 描述：映射数据表控制层
* @author DESKTOP-212O9VU 2020-06-15 16:23:30
*/
@RestController
@RequestMapping("/visualization/api")
public class MappingDataController extends BaseController{

    @Autowired
    private IMappingDataService mappingDataService;

    /**
    * <p>根据Id 查询</p>
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    @GetMapping(value = "/v1/mapping-datas")
    public List<MappingData> findAllMappingData(HttpServletRequest req, HttpServletResponse res){
        List<MappingData> mappingDataList = new ArrayList<MappingData>();
        Finder finder = FinderFactory.createFromRequestParamAccordingEntityClass(req, MappingData.class);
        Page<MappingData> page = PageFactory.createFromRequest(req);
        page = mappingDataService.findByFinderAndPage(finder,page);
        mappingDataList = page.getContent();
        fillResponseWithPage(res, page);
        return mappingDataList;
    }

    /**
    * <p>根据Id 查询</p>
    * @param id 主键
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    @GetMapping(value = "/v1/mapping-datas/{id}")
    public MappingData findById(@PathVariable("id") String id) {
        return mappingDataService.findById(id);
    }

    /**
    * <p>新增映射数据表</p>
    * @param mappingData 需要新增的数据
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    @PostMapping(value = "/v1/mapping-datas")
    public MappingData create(@Valid @RequestBody MappingData mappingData) {
        return mappingDataService.create(mappingData);
    }

    /**
    * <p>删除映射数据表</p>
    * @param id 主键
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    @DeleteMapping(value = "/v1/mapping-datas/{id}")
    public void deleteById(@PathVariable("id") String id) throws Exception {
        mappingDataService.delete(id);
    }

    /**
    * <p>更新映射数据表</p>
    * @param id 主键
    * @return 实体
    * @author DESKTOP-212O9VU 2020-06-15 16:23:30
    */
    @PatchMapping(value = "/v1/mapping-datas/{id}")
    public MappingData update (@PathVariable("id") String id,@RequestBody MappingData mappingData) throws Exception {
        mappingData.setId(id);
        return mappingDataService.patchUpdate(mappingData);
    }


    /**
     * <p>批量新增映射数据表</p>
     * @param list 需要新增的数据集合
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 16:23:30
     */
    @PostMapping(value = "/v1/mapping-batch-datas")
    public InfoJson batchCreate(@RequestBody List<MappingData> list) {
        return mappingDataService.batchCreate(list);
    }


    /**
     * <p>批量修改映射数据表</p>
     * @param list 需要修改的数据集合
     * @return 实体
     * @author DESKTOP-212O9VU 2020-06-15 16:23:30
     */
    @PatchMapping(value = "/v1/mapping-batch-datas")
    public InfoJson batchPatch(@RequestBody List<MappingData> list) {
        return mappingDataService.batchPatch(list);
    }

}