package com.sbr.visualization.util;

import com.sbr.springboot.context.SpringContextUtils;
import com.sbr.visualization.bigscreendata.model.BigAttributeData;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.config.EsConfig;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.AliasMetaData;
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

/**
 * @ClassName ElasticsearchUtil
 * @Description TODO Elasticsearch工具类
 * @Author zxx
 * @Version 1.0
 */
public class ElasticsearchUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataBaseUtil.class);

    private static EsConfig esConfig = SpringContextUtils.getBean(EsConfig.class);


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
        Map<String, Aggregation> aggregationMap = ElasticsearchUtil.buildAggregationBuilder(allDataModelAttribute, measureDataList, yAll, modelDAOOne, datasourceManage, bigScreenData);
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
        //获取ES
        RestHighLevelClient esHighInit = esConfig.getEsHighInit(datasourceManage);
        //创建搜索器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建分组聚合
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms(dimensionsList.get(0).getFieldsName()).field(dimensionsList.get(0).getFieldsName()).size(bigScreenData.getLimit());
        //构建条件
        getTermsAggregationBuilder(dimensionsList, bigScreenData, termsAggregationBuilder, 1, value, measureList);
        //条件放入查询器
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        //设置索引
        SearchRequest searchRequest = new SearchRequest(dataModel.getIndexes());
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
}
