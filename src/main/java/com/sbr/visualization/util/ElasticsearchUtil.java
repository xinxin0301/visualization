package com.sbr.visualization.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbr.springboot.context.SpringContextUtils;
import com.sbr.visualization.bigscreendata.model.BigAttributeData;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.config.EsConfig;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import com.sbr.visualization.filter.dao.FilterDAO;
import com.sbr.visualization.filter.model.Filter;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.max.ParsedMax;
import org.elasticsearch.search.aggregations.metrics.min.ParsedMin;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName ElasticsearchUtil
 * @Description TODO Elasticsearch工具类
 * @Author zxx
 * @Version 1.0
 */
public class ElasticsearchUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseUtil.class);

    private static EsConfig esConfig = SpringContextUtils.getBean(EsConfig.class);

    private static FilterDAO filterDAO = SpringContextUtils.getBean(FilterDAO.class);


    /**
     * @param bigScreenData         大屏数据对象
     * @param modelDAOOne           数据模型
     * @param yAll                  总度量大屏属性
     * @param allDataModelAttribute 总维度模型属性
     * @param datasourceManage      数据源
     * @param measureDataList       度量模型属性
     * @return java.util.List<java.util.Map < java.lang.String, java.lang.String>>
     * @Author zxx
     * @Description //TODO
     * @Date 15:10 2020/8/6
     * @Param
     **/
    public static List<Map<String, String>> buildElasticsearch(BigScreenData bigScreenData, DataModel modelDAOOne, List<BigAttributeData> yAll, List<DataModelAttribute> allDataModelAttribute, DatasourceManage datasourceManage, List<DataModelAttribute> measureDataList) throws IOException {
        List<Map<String, String>> mapList = new ArrayList<>();
        //构建es查询条件、聚合
        Map<String, Aggregation> aggregationMap = ElasticsearchUtil.buildAggregationBuilder(allDataModelAttribute, measureDataList, yAll, modelDAOOne, datasourceManage, bigScreenData);
        //构建es结果数据
        ElasticsearchUtil.recursionElasticSearchData(allDataModelAttribute, aggregationMap, yAll, measureDataList, mapList, new LinkedHashMap<>(), new HashMap<>());
        return mapList;
    }


    /**
     * @param dimensionsList   维度数据模型属性
     * @param measureList      度量数据模型属性
     * @param value            度量大屏数据模型
     * @param dataModel        数据模型
     * @param datasourceManage 数据源
     * @param bigScreenData    大屏对象
     * @return java.util.Map<java.lang.String, org.elasticsearch.search.aggregations.Aggregation>
     * @Author zxx
     * @Description //TODO 拼装ES聚合条件，请求数据
     * @Date 15:10 2020/8/5
     **/
    public static Map<String, Aggregation> buildAggregationBuilder(List<DataModelAttribute> dimensionsList, List<DataModelAttribute> measureList, List<BigAttributeData> value, DataModel dataModel, DatasourceManage datasourceManage, BigScreenData bigScreenData) throws IOException {
        //设置索引
        SearchRequest searchRequest = new SearchRequest(dataModel.getIndexes());
        //创建搜索器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //处理ES条件查询
        List<Filter> filterListAll = new ArrayList<>();
        List<Filter> filterList = filterDAO.findByDataModelId(dataModel.getId());
        if (filterList != null && filterList.size() > 0) {
            //添加数据模型过滤器
            filterListAll.addAll(filterList);
            if (bigScreenData.getFilterList() != null) {
                //添加大屏过滤器
                filterList.addAll(bigScreenData.getFilterList());
            }
        } else if (bigScreenData.getFilterList() != null) {
            filterList.addAll(bigScreenData.getFilterList());
        }
        //处理es条件
        if (filterListAll != null && filterListAll.size() > 0) {
            BoolQueryBuilder boolQueryBuilder = ElasticsearchUtil.elasticsearchGetData(filterListAll);
            if (boolQueryBuilder != null) {
                //设置查询条件
                searchSourceBuilder.query(boolQueryBuilder);
            }
        }
        //获取ES
        RestHighLevelClient esHighInit = esConfig.getEsHighInit(datasourceManage);
        //构建分组聚合
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(dimensionsList.get(0).getFieldsName()).field(dimensionsList.get(0).getFieldsName()).size(bigScreenData.getLimit());
        //构建条件
        getTermsAggregationBuilder(dimensionsList, bigScreenData, termsAggregationBuilder, 1, value, measureList);
        //条件放入查询器
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = esHighInit.search(searchRequest, RequestOptions.DEFAULT);
        Map<String, Aggregation> asMap = search.getAggregations().getAsMap();
        return asMap;
    }


    /**
     * @param dimensionsList          维度集合
     * @param bigScreenData           大屏对象
     * @param termsAggregationBuilder
     * @return org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder
     * @Author zxx
     * @Description //TODO 递归构建TermsAggregationBuilder条件
     * @Date 10:06 2020/8/6
     **/
    private static void getTermsAggregationBuilder(List<DataModelAttribute> dimensionsList, BigScreenData bigScreenData, TermsAggregationBuilder termsAggregationBuilder, int j, List<BigAttributeData> value, List<DataModelAttribute> measureList) {
        //聚合termsAgg需要一层一层添加，第一个是第一层，第二个应该在第一层下，第三个应该在第二层下
        if (j <= dimensionsList.size()) {
            for (int i = j; i <= dimensionsList.size(); i++) {
                //最后一个维度，拼装度量条件终止
                if (j == dimensionsList.size()) {
                    if (value != null && value.size() > 0) {
                        //构建计算聚合
                        for (BigAttributeData bigAttributeData : value) {
                            for (DataModelAttribute dataModelAttribute : measureList) {
                                if (bigAttributeData.getId().equals(dataModelAttribute.getId())) {
                                    //获取聚合类型,统计只能放在最后的AggregationBuilder里面
                                    String aggregator = bigAttributeData.getAggregator();
                                    if ("SUM".equals(aggregator)) {
                                        termsAggregationBuilder.subAggregation(AggregationBuilders.sum(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                                        continue;
                                    } else if ("COUNT".equals(aggregator)) {
                                        termsAggregationBuilder.subAggregation(AggregationBuilders.count(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                                        continue;
                                    } else if ("AVG".equals(aggregator)) {
                                        termsAggregationBuilder.subAggregation(AggregationBuilders.avg(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                                        continue;
                                    } else if ("MAX".equals(aggregator)) {
                                        termsAggregationBuilder.subAggregation(AggregationBuilders.max(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                                        continue;
                                    } else if ("MIN".equals(aggregator)) {
                                        termsAggregationBuilder.subAggregation(AggregationBuilders.min(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                    break;
                } else {
                    //如果不是最后第一，接着递归拼装
                    if (i < dimensionsList.size()) {
                        TermsAggregationBuilder termsAggregationBuilder1 = AggregationBuilders.terms(dimensionsList.get(i).getFieldsName()).field(dimensionsList.get(i).getFieldsName()).size(bigScreenData.getLimit());
                        termsAggregationBuilder.subAggregation(termsAggregationBuilder1);
                        getTermsAggregationBuilder(dimensionsList, bigScreenData, termsAggregationBuilder1, j + 1, value, measureList);
                        break;
                    }
                }
            }
        }
    }

    /**
     * @param dimensionsList 维度数据模型属性
     * @param asMap          es结果
     * @param value          度量大屏数据模型
     * @param measureList    度量数据模型属性
     * @param mapList        存放结果List
     * @param mapData        存放结果Map
     * @return void
     * @Author zxx
     * @Description //TODO 递归构建Elasticsearch数据结果
     * @Date 14:29 2020/8/5
     **/
    public static void recursionElasticSearchData(List<DataModelAttribute> dimensionsList, Map<String, Aggregation> asMap, List<BigAttributeData> value, List<DataModelAttribute> measureList, List<Map<String, String>> mapList, Map<String, String> mapData, Map<String, String> map) {
        for (int i = 0; i < dimensionsList.size(); i++) {
            List<? extends Terms.Bucket> buckets = null;

            //判断类型，当前值事String 还是 数值 ，表格可能存在维度数值
            Aggregation aggregation1 = asMap.get(dimensionsList.get(i).getFieldsName());
            if (aggregation1 != null && aggregation1 instanceof ParsedStringTerms) {
                ParsedStringTerms ieventpriAgg = (ParsedStringTerms) asMap.get(dimensionsList.get(i).getFieldsName());
                buckets = ieventpriAgg.getBuckets();
            } else if (aggregation1 != null && aggregation1 instanceof ParsedLongTerms) {
                ParsedLongTerms ieventpriAgg = (ParsedLongTerms) asMap.get(dimensionsList.get(i).getFieldsName());
                buckets = ieventpriAgg.getBuckets();
            }

            if (buckets != null) {
                for (Terms.Bucket bucket : buckets) {
                    //如果Map是上次存放数据的旧的Map，重新初始化
                    if (mapData == null || mapData.size() >= dimensionsList.size()) {
                        mapData = new LinkedHashMap<>();

                        //这里如果一个节点下存在多个，无法获取上一个节点数据，在map里面取值
                        if (map != null && map.size() > 0) {
                            for (Map.Entry<String, String> objectEntry : map.entrySet()) {
                                if (!objectEntry.getKey().equals(dimensionsList.get(i))) {
                                    mapData.put(objectEntry.getKey(), objectEntry.getValue());
                                }
                            }
                        }
                    }
                    if (bucket.getDocCount() > 1) {
                        map.put(dimensionsList.get(i).getRandomAlias(), bucket.getKeyAsString());
                    }
                    mapData.put(dimensionsList.get(i).getRandomAlias(), bucket.getKeyAsString());
                    System.out.println(bucket.getKeyAsString());
                    System.out.println(bucket.getAggregations().get(dimensionsList.get(i).getFieldsName()));

                    //判断是否取统计值
                    boolean flag = false;
                    if (measureList != null && measureList.size() > 0) {
                        for (DataModelAttribute modelAttribute : measureList) {
                            Aggregation aggregation = bucket.getAggregations().get(modelAttribute.getFieldsName());
                            if (aggregation != null) {
                                flag = true;
                            }
                        }
                    }

                    //如果有度量的话，处理度量结果
                    if (flag) {
                        //遍历大屏度量对象，匹配获取统计结果
                        for (BigAttributeData bigAttributeData : value) {
                            //遍历度量数据模型属性
                            for (DataModelAttribute modelAttribute : measureList) {
                                if (bigAttributeData.getId().equals(modelAttribute.getId())) {
                                    // TODO 提取聚合值
                                    Double valuedata = 0.0;
                                    if (("SUM").equalsIgnoreCase(bigAttributeData.getAggregator())) {
                                        ParsedSum sum = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = sum.getValue();
                                    } else if (("COUNT").equalsIgnoreCase(bigAttributeData.getAggregator())) {
                                        ValueCount count = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = Double.valueOf(count.getValue());
                                    } else if (("AVG").equalsIgnoreCase(bigAttributeData.getAggregator())) {
                                        ParsedAvg avg = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = avg.getValue();
                                    } else if (("MAX").equalsIgnoreCase(bigAttributeData.getAggregator())) {
                                        ParsedMax max = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = max.getValue();
                                    } else if (("MIN").equalsIgnoreCase(bigAttributeData.getAggregator())) {
                                        ParsedMin min = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = min.getValue();
                                    }
                                    mapData.put(modelAttribute.getRandomAlias(), String.valueOf(valuedata));
                                    System.out.println(valuedata);
                                }
                            }
                        }
                        mapList.add(mapData);
                        mapData = null;
                    } else if (measureList == null && i + 1 == dimensionsList.size()) {//普通表格里面可能不会存在度量，特殊处理
                        mapList.add(mapData);
                        mapData = null;
                    }
                    recursionElasticSearchData(dimensionsList, bucket.getAggregations().getAsMap(), value, measureList, mapList, mapData, map);
                }
            }
        }
    }


    /**
     * @param esHighInit 操作 ElasticSearch 连接
     * @return java.util.Set<java.lang.String>
     * @Author zxx
     * @Description //TODO  获取当前 ElasticSearch 所有索引列表
     * @Date 14:48 2020/6/12
     **/
    public static List<Map<String, Object>> getIndices(RestHighLevelClient esHighInit) throws IOException {
        try {
            List<Map<String, Object>> resultList = new ArrayList<>();
            GetAliasesRequest request = new GetAliasesRequest();
            GetAliasesResponse alias = esHighInit.indices().getAlias(request, RequestOptions.DEFAULT);
            Map<String, Set<AliasMetaData>> map = alias.getAliases();
            map.forEach((k, v) -> {
                if (!k.startsWith(".")) {
                    Map map1 = new HashMap();
                    map1.put("tableName", k);
                    map1.put("tableAlias", k);
                    resultList.add(map1);
                }
            });
            return resultList;
        } catch (Exception e) {
            LOGGER.error("获取ElasticSearch索引列表失败", e);
            throw e;
        }
    }


    /**
     * @param operator  连接条件是AND must 还是 OR should
     * @param type      查询类型
     * @param name      查询字段
     * @param value     查询值
     * @param boolQuery 查询对象
     * @return void
     * @Author zxx
     * @Description //TODO elasticSearch 查询条件
     * @Date 14:07 2020/9/1
     **/
    public static void elasticSearchSearchCondition(String operator, String type, String name, Object value, BoolQueryBuilder boolQuery) {
        if (operator == null) {
            List<String> valueList = (List<String>) ((List) value).stream().collect(Collectors.toList());
            boolQuery.must(QueryBuilders.termsQuery(name, valueList));
        } else {
            if (operator.equals("AND")) {//包含以下所有条件
                switch (type) {
                    case "include"://包含
                        boolQuery.must(QueryBuilders.wildcardQuery(name, "*" + value + "*"));
                        break;
                    case "start"://开始于
                        boolQuery.must(QueryBuilders.wildcardQuery(name, value + "*"));
                        break;
                    case "end"://結束于
                        boolQuery.must(QueryBuilders.wildcardQuery(name, "*" + value));
                        break;
                    case "notIncluded"://不包含
                        boolQuery.mustNot(QueryBuilders.wildcardQuery(name, "*" + value + "*"));
                        break;
                    case "equal"://等于
                        boolQuery.must(QueryBuilders.termQuery(name, value));
                        break;
                    case "ineq"://不等于
                        boolQuery.mustNot(QueryBuilders.termQuery(name, value));
                        break;
                    case "null"://等于NULL
                        boolQuery.must(QueryBuilders.existsQuery(name));
                        break;
                    case "notNull"://不等于NULL
                        boolQuery.mustNot(QueryBuilders.existsQuery(name));
                        break;
                    case "gt"://大于
                        boolQuery.must(QueryBuilders.rangeQuery(name).gt(value));
                        break;
                    case "gte"://大于等于
                        boolQuery.must(QueryBuilders.rangeQuery(name).gte(value));
                        break;
                    case "lt"://小于
                        boolQuery.must(QueryBuilders.rangeQuery(name).lt(value));
                        break;
                    case "lte"://小于等于
                        boolQuery.must(QueryBuilders.rangeQuery(name).lte(value));
                        break;
                }
            } else if (operator.equals("OR")) {//包含以下任意条件
                switch (type) {
                    case "include"://包含
                        boolQuery.should(QueryBuilders.wildcardQuery(name, "*" + value + "*"));
                        break;
                    case "start"://开始于
                        boolQuery.should(QueryBuilders.wildcardQuery(name, value + "*"));
                        break;
                    case "end"://結束于
                        boolQuery.should(QueryBuilders.wildcardQuery(name, "*" + value));
                        break;
                    case "notIncluded"://不包含
                        boolQuery.should(new BoolQueryBuilder().mustNot(QueryBuilders.wildcardQuery(name, "*" + value + "*")));
                        break;
                    case "equal"://等于
                        boolQuery.should(QueryBuilders.termQuery(name, value));
                        break;
                    case "ineq"://不等于
                        boolQuery.should(new BoolQueryBuilder().mustNot(QueryBuilders.termQuery(name, value)));
                        break;
                    case "null"://等于NULL
                        boolQuery.should(QueryBuilders.existsQuery(name));
                        break;
                    case "notNull"://不等于NULL
                        boolQuery.should(new BoolQueryBuilder().mustNot(QueryBuilders.existsQuery(name)));
                        break;
                    case "gt"://大于
                        boolQuery.should(QueryBuilders.rangeQuery(name).gt(value));
                        break;
                    case "gte"://大于等于
                        boolQuery.should(QueryBuilders.rangeQuery(name).gte(value));
                        break;
                    case "lt"://小于
                        boolQuery.should(QueryBuilders.rangeQuery(name).lt(value));
                        break;
                    case "lte"://小于等于
                        boolQuery.should(QueryBuilders.rangeQuery(name).lte(value));
                        break;
                }
            }
        }
    }


    /**
     * @param filterList 过滤器集合
     * @return void
     * @Author zxx
     * @Description //TODO 数据模型获取所有数据
     * @Date 13:52 2020/9/1
     **/
    public static BoolQueryBuilder elasticsearchGetData(List<Filter> filterList) throws IOException {
        BoolQueryBuilder boolQuery = null;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            boolQuery = new BoolQueryBuilder();
            if (filterList != null && filterList.size() > 0) {
                for (Filter filter : filterList) {

                    //文本条件
                    if (filter.getTextMatch() != null && StringUtils.isNotBlank(filter.getTextMatch())) {
                        Map textMatchMap = objectMapper.readValue(filter.getTextMatch(), Map.class);
                        String operator = (String) textMatchMap.get("operator");
                        List<Map<String, Object>> value = (List<Map<String, Object>>) textMatchMap.get("value");
                        for (Map<String, Object> map : value) {
                            ElasticsearchUtil.elasticSearchSearchCondition(operator, (String) map.get("type"), (String) map.get("name"), map.get("value"), boolQuery);
                        }
                    }

                    //列表条件
                    if (filter.getListMatch() != null && StringUtils.isNotBlank(filter.getListMatch())) {
                        Map listMatchMap = objectMapper.readValue(filter.getListMatch(), Map.class);
                        String name = (String) listMatchMap.get("name");
                        List<String> list = (List<String>) listMatchMap.get("list");
                        ElasticsearchUtil.elasticSearchSearchCondition(null, null, name, list, boolQuery);
                    }

                    //时间条件查询
                    if (filter.getDate() != null && StringUtils.isNotBlank(filter.getDate())) {
                        Map dateMap = objectMapper.readValue(filter.getDate(), Map.class);
                        String value = (String) dateMap.get("value");
                        String name = (String) dateMap.get("name");
                        Map<String, Object> map = (Map) dateMap.get("detail");
                        elasticSearchDateCondition(name, value, map, boolQuery);
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.error("构建Elasticsearch查询条件错误：", e);
            throw e;
        }
        return boolQuery;
    }


    /**
     * @param name      字段
     * @param value     条件值
     * @param map       自定义时间范围
     * @param boolQuery
     * @return void
     * @Author zxx
     * @Description //TODO es时间查询过滤
     * @Date 17:00 2020/9/1
     * @Param
     **/
    private static void elasticSearchDateCondition(String name, String value, Map<String, Object> map, BoolQueryBuilder boolQuery) {
        //获取时间范围
        Map<String, Object> dateEsRange = getDateEsRange(value, map);
        String minDate = (String) dateEsRange.get("minDate");
        String maxDate = (String) dateEsRange.get("maxDate");
        boolQuery.must(QueryBuilders.rangeQuery(name).gte(minDate).lte(maxDate));
    }


    /**
     * @param value 取值范围
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Author zxx
     * @Description //TODO 获取es查询时间条件
     * @Date 15:19 2020/9/2
     **/
    private static Map<String, Object> getDateEsRange(String value, Map<String, Object> map) {
        switch (value) {
            case "beforeyesterday"://前天
                map.put("minDate", "now-2d/d");
                map.put("maxDate", "now-2d/d");
                break;
            case "yesterday"://昨天
                map.put("minDate", "now-1d/d");
                map.put("maxDate", "now-1d/d");
                break;
            case "today"://今天
                map.put("minDate", "now/d");
                map.put("maxDate", "now/d");
                break;
            case "cus"://自定义
                break;
        }
        return map;
    }


}
