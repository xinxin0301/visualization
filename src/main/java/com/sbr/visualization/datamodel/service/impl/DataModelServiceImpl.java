package com.sbr.visualization.datamodel.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbr.common.exception.SBRException;
import com.sbr.common.finder.Finder;
import com.sbr.common.page.Page;
import com.sbr.common.util.ClassUtil;
import com.sbr.common.util.StringUtil;
import com.sbr.springboot.json.InfoJson;
import com.sbr.springboot.rest.exception.RestIllegalArgumentException;
import com.sbr.visualization.config.EsConfig;
import com.sbr.visualization.constant.CommonConstant;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodel.service.IDataModelService;
import com.sbr.visualization.datamodelattribute.dao.DataModelAttributeDAO;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datasourcemanage.dao.DatasourceManageDAO;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.filter.dao.FilterDAO;
import com.sbr.visualization.filter.model.Filter;
import com.sbr.visualization.mappingdata.dao.MappingDataDAO;
import com.sbr.visualization.mappingdata.model.MappingData;
import com.sbr.visualization.util.DataBaseUtil;
import com.sbr.visualization.util.ElasticsearchUtil;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Connection;
import java.util.*;

/**
 * 描述：数据模型管理 服务实现层
 *
 * @author DESKTOP-212O9VU
 * @date 2020-06-12 15:20:26
 */
@Service
@Transactional(readOnly = true)
public class DataModelServiceImpl implements IDataModelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataModelServiceImpl.class);

    @Autowired
    private DataModelDAO dataModelDAO;

    @Autowired
    private DatasourceManageDAO datasourceManageDAO;

    @Autowired
    private EsConfig esConfig;

    @Autowired
    private DataModelAttributeDAO dataModelAttributeDAO;

    @Autowired
    private MappingDataDAO mappingDataDAO;

    @Autowired
    private FilterDAO filterDAO;

    @Override
    public List<DataModel> findByFinder(Finder finder) {
        return dataModelDAO.findByFinder(finder);
    }

    @Override
    public DataModel findById(String id) {
        return dataModelDAO.findOne(id);
    }

    @Override
    public Page<DataModel> findByFinderAndPage(Finder finder, Page<DataModel> page) {
        return dataModelDAO.findByFinderAndPage(finder, page);
    }

    @Override
    @Transactional
    public DataModel create(DataModel dataModel) {
        return dataModelDAO.save(dataModel);
    }

    @Override
    @Transactional
    public void delete(String id) {
        dataModelDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByEntity(DataModel dataModel) {
        dataModelDAO.delete(dataModel);
    }

    @Override
    @Transactional
    public DataModel patchUpdate(DataModel dataModel) {
        DataModel entity = findById(dataModel.getId());
        if (entity == null) {
            throw new RestIllegalArgumentException("数据模型ID不合法");
        }

        if (dataModel.getDatasourceManage() != null && StringUtils.isNotEmpty(dataModel.getDatasourceManage().getId())) {
            //获取数据源
            DatasourceManage datasourseManage = datasourceManageDAO.findOne(dataModel.getDatasourceManage().getId());
            if (datasourseManage == null) {
                throw new RestIllegalArgumentException("数据源ID不合法");
            }
            entity.setDatasourceManage(datasourseManage);
        } else {
            dataModel.setDatasourceManage(null);
        }
        ClassUtil.merge(entity, dataModel);
        return dataModelDAO.saveAndFlush(entity);
    }

    @Override
    @Transactional
    public DataModel putUpdate(DataModel dataModel) {
        return dataModelDAO.save(dataModel);
    }

    /**
     * @param tablename 表名、索引
     * @param id        模型ID
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author zxx
     * @Description //TODO 获取表下所有字段名类型、索引一样
     * @Date 13:43 2020/8/4
     **/
    @Override
    public InfoJson getfieldsByTableName(String tablename, String id) throws Exception {
        InfoJson infoJson = new InfoJson();
        List<Map<String, Object>> resultList = new ArrayList<>();
        DataModel dataModel = dataModelDAO.getOne(id);
        DatasourceManage datasourceManage = dataModel.getDatasourceManage();
        switch (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName()) {
            case CommonConstant.MYSQL://MySQL
                infoJson = buildMysql(tablename, resultList, datasourceManage, infoJson);
                return infoJson;
            case CommonConstant.ES://Elasticsearch
                infoJson = buildElasticsearchFields(tablename, resultList, datasourceManage, infoJson);
                return infoJson;
        }
        return infoJson;
    }

    /**
     * @param tablename        索引名称
     * @param resultList       存放结果
     * @param datasourceManage ES数据源
     * @return void
     * @Author zxx
     * @Description //TODO Elasticsearch获取索引的字段列表
     * @Date 14:14 2020/8/3
     **/
    private InfoJson buildElasticsearchFields(String tablename, List<Map<String, Object>> resultList, DatasourceManage datasourceManage, InfoJson infoJson) {
        GetMappingsResponse getMappingResponse = null;
        try {
            RestHighLevelClient esHighInit = esConfig.getEsHighInit(datasourceManage);
            GetMappingsRequest getMappings = new GetMappingsRequest().indices(tablename);
            getMappingResponse = esHighInit.indices().getMapping(getMappings, RequestOptions.DEFAULT);
            //获取Mapping
            Map<String, MappingMetaData> allMappings = getMappingResponse.mappings();
            Set<String> set = new HashSet<>();
            for (Map.Entry<String, MappingMetaData> indexValue : allMappings.entrySet()) {
                Map<String, Object> mapping = indexValue.getValue().sourceAsMap();
                Iterator<Map.Entry<String, Object>> entries = mapping.entrySet().iterator();
                entries.forEachRemaining(stringObjectEntry -> {
                    if (stringObjectEntry.getKey().equals("properties")) {
                        Map<String, Object> value = (Map<String, Object>) stringObjectEntry.getValue();
                        for (Map.Entry<String, Object> ObjectEntry : value.entrySet()) {
                            String key = ObjectEntry.getKey();
                            //去重，如果有重复的过滤
                            if (!set.contains(key)) {
                                Map<String, Object> value1 = (Map<String, Object>) ObjectEntry.getValue();
                                Map<String, Object> resultMap = new HashMap<>();
                                resultMap.put("NAME", key);
                                resultMap.put("VALUE", value1.get("type"));
                                resultMap.put("COMMENT", "");
                                set.add(key);
                                resultList.add(resultMap);
                            }
                        }
                    }
                });
            }
            infoJson.setCode("200");
            infoJson.setSuccess(true);
            infoJson.setData(resultList);
            return infoJson;
        } catch (Exception e) {
            infoJson.setSuccess(false);
            infoJson.setCode("500");
            infoJson.setDescription(e.getMessage());
            return infoJson;
        }
    }


    /**
     * @param tablename        表名
     * @param resultList       存放结果
     * @param datasourceManage 数据源
     * @return void
     * @Author zxx
     * @Description //TODO MySQL表字段列表
     * @Date 14:14 2020/8/3
     **/
    private InfoJson buildMysql(String tablename, List<Map<String, Object>> resultList, DatasourceManage datasourceManage, InfoJson infoJson) {
        try {
            //获取数据库连接
            Connection connection = DataBaseUtil.mysqlConnect(datasourceManage);
            //获取当前数据表下面的所有字段
            LinkedHashMap<String, Object> columnNames = DataBaseUtil.getColumnNames(tablename, connection, "no");
            //获取字段注释
            List<String> columnComments = DataBaseUtil.getColumnComments(tablename, connection);
            //遍历字段、属性类型、别名
            final int[] i = {-1};
            columnNames.forEach((k, v) -> {
                Map<String, Object> map = new HashMap<>();
                i[0]++;
                for (int i1 = 0; i1 < columnComments.size(); i1++) {
                    map.put("NAME", k);
                    map.put("VALUE", v);
                    map.put("COMMENT", columnComments.get(i[0]));
                    continue;
                }
                resultList.add(map);
            });
            infoJson.setData(resultList);
            infoJson.setSuccess(true);
            infoJson.setCode("200");
            return infoJson;
        } catch (Exception e) {
            LOGGER.error("MySql查询字段列表失败：", e);
            infoJson.setSuccess(false);
            infoJson.setCode("500");
            infoJson.setDescription(e.getMessage());
            return infoJson;
        }
    }


    /**
     * @param dataModel 数据模型对象
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO 获取数据
     * @Date 13:43 2020/8/4
     **/
    @Override
    public InfoJson getDataByDataModel(DataModel dataModel) throws Exception {
        InfoJson infoJson = new InfoJson();
        Map<String, Object> resultMap = new HashMap<>();
        DataModel dataModelDAOOne = dataModelDAO.findOne(dataModel.getId());
        if (dataModelDAOOne == null) {
            infoJson.setSuccess(false);
            infoJson.setDescription("当前数据模型不存在！");
            return infoJson;
        }
        //获取数据源
        DatasourceManage datasourceManage = datasourceManageDAO.findOne(dataModelDAOOne.getDatasourceManage().getId());
        switch (datasourceManage.getDatabaseTypeManage().getDatabaseTypeName()) {
            case CommonConstant.MYSQL://Mysql
                this.buildMySql(dataModel, dataModelDAOOne, datasourceManage, infoJson, resultMap);
                break;
            case CommonConstant.ES://Elasticsearch
                this.buildElasticsearch(dataModel, dataModelDAOOne, datasourceManage, infoJson, resultMap);

        }
        return infoJson;
    }

    /**
     * @param dataModel        前台传递模型第项
     * @param dataModelDAOOne  根据ID查询模型对象
     * @param datasourceManage 数据源对象
     * @param infoJson         返回JSON
     * @param resultMap        结果数据拼装对象
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO Elasticsearch获取数据
     * @Date 11:21 2020/8/4
     **/
    private InfoJson buildElasticsearch(DataModel dataModel, DataModel dataModelDAOOne, DatasourceManage datasourceManage, InfoJson infoJson, Map<String, Object> resultMap) {
        //获取字段别名
        List<String> fieldList = new ArrayList<>();
        //字段名称
        List<String> fieldsAliasList = new ArrayList<>();
        //存放结果
        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map1 = new HashMap<>();

        //处理查询字段
        List<DataModelAttribute> dataModelAttributes = dataModelAttributeDAO.findByDataModelId(dataModel.getId());
        Collections.sort(dataModelAttributes, Comparator.comparing(DataModelAttribute::getModelType).thenComparing(DataModelAttribute::getSortIndex));
        dataModelAttributes.forEach(dataModelAttribute -> {
            fieldList.add(dataModelAttribute.getFieldsAlias());//字段别名
            fieldsAliasList.add(dataModelAttribute.getFieldsName());//字段名称
        });
        SearchResponse searchResponse = null;
        try {
            //获取es
            RestHighLevelClient esHighInit = esConfig.getEsHighInit(datasourceManage);
            SearchSourceBuilder source = new SearchSourceBuilder();
            SearchRequest searchRequest = new SearchRequest(dataModelDAOOne.getIndexes());
            //获取es 查询条件，查询过滤器处理条件
            List<Filter> filterList = filterDAO.findByDataModelId(dataModelDAOOne.getId());
            if (filterList != null && fieldList.size() > 0) {
                BoolQueryBuilder boolQueryBuilder = ElasticsearchUtil.elasticsearchGetData(filterList);
                source.query(boolQueryBuilder);
            }
            source.size(500);
            searchRequest.source(source);
            searchResponse = esHighInit.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            LOGGER.error("Elasticsearch获取数据失败:", e);
            infoJson.setDescription("Elasticsearch获取数据失败：" + e.getMessage());
            infoJson.setCode("500");
            infoJson.setSuccess(false);
            return infoJson;
        }
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> map = new LinkedHashMap<>();
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            for (String s : fieldsAliasList) {
                Object value = sourceAsMap.get(s);
                if (value == null) {
                    map.put(s, null);
                } else {
                    map.put(s, value);
                }
            }
            mapList.add(map);
        }
        map1.put("columns", fieldList);//设置列属性
        map1.put("rows", mapList);//设置值
        resultMap.put("data", map1);
        infoJson.setSuccess(true);
        infoJson.setData(resultMap);
        return infoJson;
    }


    /**
     * @param dataModel        传递过来的当前数据模型
     * @param dataModelDAOOne  查询出来的数据模型
     * @param datasourceManage 数据源
     * @param infoJson         Infojson
     * @param resultMap        结果Map
     * @return com.sbr.springboot.json.InfoJson
     * @Author zxx
     * @Description //TODO MySql 处理方式
     * @Date 9:38 2020/8/4
     **/
    private InfoJson buildMySql(DataModel dataModel, DataModel dataModelDAOOne, DatasourceManage datasourceManage, InfoJson infoJson, Map<String, Object> resultMap) throws IOException {
        //获取字段属性SQL
        List<String> fieldList = new ArrayList<>();
        //处理查询字段
        List<DataModelAttribute> dataModelAttributes = dataModelAttributeDAO.findByDataModelId(dataModel.getId());
        Collections.sort(dataModelAttributes, Comparator.comparing(DataModelAttribute::getModelType).thenComparing(DataModelAttribute::getSortIndex));

        //拼接字段名，如果重复的话拼接表名()
        dataModelAttributes.forEach(dataModelAttribute -> {
            if (dataModelAttribute.getIsHide() == 2) {//不隐藏
                if (fieldList.contains(dataModelAttribute.getFieldsAlias())) {
                    fieldList.add(dataModelAttribute.getFieldsAlias() + "(" + dataModelAttribute.getTableName() + ")");
                } else {
                    fieldList.add(dataModelAttribute.getFieldsAlias());
                }
            }
        });

        List<Map<String, String>> datas = null;
        List list = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String sqlParam = dataModelDAOOne.getSqlParam();
        if (sqlParam != null && StringUtils.isNotEmpty(sqlParam)) {
            list = objectMapper.readValue(sqlParam, List.class);
        }

        //TODO 等于NULL说明没有查询条件，判断是否有单位权限
        //构建出单位条件
        String substring = "";
        String orgDataAuthority = DataBaseUtil.buildOrgDataAuthority(dataModelDAOOne, new String(), list, objectMapper);
        if(orgDataAuthority!=null && StringUtils.isNotEmpty(orgDataAuthority)){
            substring = orgDataAuthority.substring(6);
        }
        //截取SQL拼接条件 截取掉LIMIT 0,500
        String orgAuthoritySql = dataModelDAOOne.getSqlStr().substring(0, dataModelDAOOne.getSqlStr().length() - 11);
        if (sqlParam != null && StringUtils.isNotEmpty(sqlParam) && !sqlParam.equals("[]")) {
            if (substring != null && StringUtils.isNotEmpty(substring)) {
                dataModelDAOOne.setSqlStr(orgAuthoritySql + "AND" + substring + " LIMIT 0,500");
            }
        } else {
            if (substring != null && StringUtils.isNotEmpty(substring)) {
                dataModelDAOOne.setSqlStr(orgAuthoritySql + "WHERE" + substring + " LIMIT 0,500");
            }
        }

        try {
            //查询结果
            datas = DataBaseUtil.getDatas(datasourceManage, dataModelDAOOne.getSqlStr(), list);
        } catch (Exception e) {
            LOGGER.error("DataModelServiceImpl数据模型查询数据错误:", e);
            throw new SBRException("DataModelServiceImpl数据模型查询数据错误", e);
        }

        //结果数据,处理关联映射问题，如果映射了去找对应映射的值
        List<Map<String, String>> finalDatas = datas;
        dataModelAttributes.forEach(dataModelAttribute -> {
            if (dataModelAttribute.getMappingManage() != null) {
                //找到绑定的映射数据
                List<MappingData> mappingDataList = mappingDataDAO.findByMappingManageId(dataModelAttribute.getMappingManage().getId());
                mappingDataList.forEach(mappingData -> {
                    //查询出来数据，与映射原始数据比较，如果相同，取映射值返回
                    for (Map<String, String> data : finalDatas) {
                        if (data.get(dataModelAttribute.getRandomAlias()) != null && data.get(dataModelAttribute.getRandomAlias()).equals(mappingData.getOriginalData())) {
                            data.put(dataModelAttribute.getRandomAlias(), mappingData.getMappingData());
                            continue;
                        }
                    }
                });
            }
        });
        Map<String, Object> map = new HashMap<>();
        map.put("columns", fieldList);//设置列属性
        map.put("rows", datas);//设置值
        resultMap.put("sql", dataModelDAOOne.getSqlShow());//展示SQL字符串
        resultMap.put("data", map);
        infoJson.setSuccess(true);
        infoJson.setData(resultMap);
        return infoJson;
    }


    @Override
    public List<Map<String, String>> getDataByfield(DataModel dataModel) throws Exception {
        DataModel dataModelDAOOne = dataModelDAO.findOne(dataModel.getId());
        List<Map<String, String>> datas = null;
        if (dataModelDAOOne == null) {
            throw new SBRException("数据模型ID不合法");
        }
        try {
            switch (dataModelDAOOne.getDatasourceManage().getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL:
                    datas = getDataByFuleldAndMySql(dataModel, dataModelDAOOne);
                    break;
                case CommonConstant.ES:
                    datas = getDataByFuleldAndEs(dataModel, dataModelDAOOne);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("DataModelServiceImpl,查询属性值結果错误:", e);
        }
        return datas;
    }

    @Override
    @Transactional
    public InfoJson deleteDataModel(String id) {
        InfoJson infoJson = new InfoJson();
        DataModel dataModel = dataModelDAO.findOne(id);
        if (dataModel == null) {
            infoJson.setSuccess(false);
            infoJson.setDescription("当前数据模型不存在！");
            return infoJson;
        }
        //删除过滤器
        List<Filter> filterList = filterDAO.findByDataModelId(id);
        if (filterList != null && filterList.size() > 0) {
            filterDAO.delete(filterList);
        }
        //删除数据模型属性
        List<DataModelAttribute> dataModelAttributeList = dataModelAttributeDAO.findByDataModelId(id);
        if (dataModelAttributeList != null && dataModelAttributeList.size() > 0) {
            dataModelAttributeDAO.delete(dataModelAttributeList);
        }
        //删除数据模型
        dataModelDAO.delete(id);

        infoJson.setSuccess(true);
        infoJson.setDescription("删除成功！");
        return infoJson;
    }

    /**
     * @param dataModel
     * @param dataModelDAOOne
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
     * @Author zxx
     * @Description //TODO 根据字段名查询数据
     * @Date 16:00 2020/9/1
     **/
    private List<Map<String, String>> getDataByFuleldAndEs(DataModel dataModel, DataModel dataModelDAOOne) throws IOException {
        List<Map<String, String>> list = null;
        try {
            list = new ArrayList<>();
            RestHighLevelClient esHighInit = esConfig.getEsHighInit(dataModelDAOOne.getDatasourceManage());
            SearchRequest searchRequest = new SearchRequest("" + dataModel.getIndexes() + "");
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            TermsAggregationBuilder aggregationBuilders = AggregationBuilders.terms(dataModel.getField()).field(dataModel.getField()).size(500);
            searchSourceBuilder.aggregation(aggregationBuilders);
            searchRequest.source(searchSourceBuilder);
            SearchResponse search = esHighInit.search(searchRequest, RequestOptions.DEFAULT);
            Map<String, Aggregation> asMap = search.getAggregations().getAsMap();
            ParsedStringTerms stringTerms = (ParsedStringTerms) asMap.get(dataModel.getField());
            List<? extends Terms.Bucket> buckets = stringTerms.getBuckets();
            for (Terms.Bucket bucket : buckets) {
                String keyAsString = bucket.getKeyAsString();
                Map<String, String> map = new HashMap<>();
                map.put(dataModel.getField(), keyAsString);
                list.add(map);
            }
        } catch (IOException e) {
            LOGGER.error("DataModelServiceImpl,Elasticsearch根据字段名查询字段列表错误：", e);
        }
        return list;
    }

    /**
     * @param dataModel
     * @param dataModelDAOOne
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
     * @Author zxx
     * @Description //TODO MySql 查询数据
     * @Date 15:10 2020/9/1
     * @Param
     **/
    public List<Map<String, String>> getDataByFuleldAndMySql(DataModel dataModel, DataModel dataModelDAOOne) throws Exception {
        StringBuffer sqlbuffer = new StringBuffer(" SELECT DISTINCT " + dataModel.getField() + " FROM " + dataModel.getTableName() + " LIMIT 0,500");
        DatasourceManage datasourceManage = dataModelDAOOne.getDatasourceManage();
        List<Map<String, String>> datas = DataBaseUtil.getDatas(datasourceManage, sqlbuffer.toString(), null);
        return datas;
    }


    /**
     * @param id 数据模型ID
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.Object>>
     * @Author zxx
     * @Description //TODO 获取数据源下所有表、索引
     * @Date 13:42 2020/8/4
     **/
    @Override
    public List<Map<String, Object>> getDatasourseManageAdnTables(String id) throws Exception {
        List<Map<String, Object>> mySqlTableNames = null;
        //根据数据模型ID，获取当前数据源
        DatasourceManage datasourseManage = dataModelDAO.getOne(id).getDatasourceManage();
        if (datasourseManage != null) {
            switch (datasourseManage.getDatabaseTypeManage().getDatabaseTypeName()) {
                case CommonConstant.MYSQL://MySql获取当前数据源库下的所有表
                    mySqlTableNames = DataBaseUtil.getMySqlTableNames(datasourseManage);
                    break;

                case CommonConstant.ES://获取ES下所有索引
                    RestHighLevelClient esHighInit = esConfig.getEsHighInit(datasourseManage);
                    mySqlTableNames = ElasticsearchUtil.getIndices(esHighInit);
                    break;

            }
        }
        return mySqlTableNames;
    }
}