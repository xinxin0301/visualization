import com.sbr.VisualizationApplication;
import com.sbr.visualization.bigscreendata.model.BigAttributeData;
import com.sbr.visualization.bigscreendata.model.BigScreenData;
import com.sbr.visualization.config.EsConfig;
import com.sbr.visualization.datamodel.dao.DataModelDAO;
import com.sbr.visualization.datamodel.model.DataModel;
import com.sbr.visualization.datamodelattribute.dao.DataModelAttributeDAO;
import com.sbr.visualization.datamodelattribute.model.DataModelAttribute;
import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.ParsedAvg;
import org.elasticsearch.search.aggregations.metrics.max.ParsedMax;
import org.elasticsearch.search.aggregations.metrics.min.ParsedMin;
import org.elasticsearch.search.aggregations.metrics.sum.ParsedSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

/**
 * @ClassName Test
 * @Description TODO
 * @Author zxx
 * @Date DATE{TIME}
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = VisualizationApplication.class)
public class Test {

    @Autowired
    private DataModelDAO dataModelDAO;

    @Autowired
    private DataModelAttributeDAO dataModelAttributeDAO;

    @Autowired
    private EsConfig esConfig;


    @org.junit.Test
    public void test1() {
        List<String> param = new ArrayList<>();
        param.add("1");
        param.add("2");
        param.add("3");
        String sql = "select * from where id =?, and name =? ,and cid = ?";

        String[] split = sql.split("'?'");
        System.out.println(split);
    }


    @org.junit.Test
    public void test2() throws Exception {
        DataModel dataModel = dataModelDAO.findOne("0550114d0f734bb795240e3e67da0ce3");
        List<String> params = new ArrayList<>();
        //params.add("%河南1%");
        //List<Map<String, String>> datas = getDatas(dataModel.getDatasourceManage(), "SELECT * FROM test where 省份 like ?", params);

//        params.add("河南");
//        params.add("上海");
        //List<Map<String, String>> datas = getDatas(dataModel.getDatasourceManage(), "SELECT * FROM test where 省份 in (?,?)", params);

        params.add("河%");
        params.add("%河南%");
        List<Map<String, String>> datas = getDatas(dataModel.getDatasourceManage(), "SELECT * FROM test", null);
        System.out.println(datas);
    }


    private static final String SQL = "SELECT * FROM ";// 数据库操作


    public static List<Map<String, String>> getDatas(DatasourceManage datasourceManage, String sql, List<String> params) throws Exception {
        Statement st = null;
        ResultSet rs = null;
        Connection connection = null;
        List<Map<String, String>> mapList = null;
        PreparedStatement stmt = null;
        try {
            mapList = new ArrayList<>();
            //获取MySql连接
            connection = mysqlConnect(datasourceManage);
            //赋值条件参数
            if (params != null && params.size() > 0) {
                stmt = connection.prepareStatement(sql);
                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }
                rs = stmt.executeQuery();
            } else {
                st = connection.createStatement();
                rs = st.executeQuery(sql);
            }

            ResultSetMetaData data = rs.getMetaData();
            int columnCount = data.getColumnCount();
            while (rs.next()) {
                Map<String, String> map = new LinkedHashMap<String, String>();
                for (int i = 0; i < columnCount; i++) {
                    map.put(data.getColumnLabel(i + 1), rs.getString(i + 1));
                }
                mapList.add(map);
            }

        } catch (Exception e) {
            throw e;
        } finally {
            //关闭连接
            closeConnection(connection);
        }
        return mapList;
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }

    public static Connection mysqlConnect(DatasourceManage datasourceManage) throws SQLException, ClassNotFoundException {
        Connection connection = null;
        try {
            //声明数据源（jdbc:mysql://localhost:端口号/数据库名）
            String url = "jdbc:mysql://" + datasourceManage.getDatabaseAddress() + ":" + datasourceManage.getPort() + "/" + datasourceManage.getDatabaseName() +
                    "?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false";
            //数据库账号
            String user = datasourceManage.getUsername();
            //数据库密码
            String password = datasourceManage.getPwd();
            //加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");
            //创建数据库连接Connection conn=DriverManager.getConnection(数据源, 数据库账号, 数据库密码);
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            throw e;
        }
        return connection;
    }


    @org.junit.Test
    public void test3() throws IOException {
        DatasourceManage datasourceManage = new DatasourceManage();
        datasourceManage.setDatabaseAddress("127.0.0.1");
        datasourceManage.setPort(9200);
        RestHighLevelClient esHighInit = esConfig.getEsHighInit(datasourceManage);
       /* GetAliasesRequest request = new GetAliasesRequest();
        GetAliasesResponse alias = esHighInit.indices().getAlias(request, RequestOptions.DEFAULT);
        Map<String, Set<AliasMetaData>> map = alias.getAliases();
        map.forEach((k, v) -> {
            Map map1 = new HashMap();
            map1.put("tableName", k);
            resultList.add(map1);
        });*/
        List<Map<String, Object>> resultList = new ArrayList<>();
        GetMappingsRequest getMappings = new GetMappingsRequest().indices("event20200521_v22");
        GetMappingsResponse getMappingResponse = esHighInit.indices().getMapping(getMappings, RequestOptions.DEFAULT);
        Map<String, MappingMetaData> allMappings = getMappingResponse.mappings();
        MappingMetaData indexMapping = allMappings.get("event20200521_v22");
        Map<String, Object> mapping = indexMapping.sourceAsMap();
        Iterator<Map.Entry<String, Object>> entries = mapping.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            Map<String, Object> value = (Map<String, Object>) entry.getValue();
            for (Map.Entry<String, Object> ObjectEntry : value.entrySet()) {
                String key = ObjectEntry.getKey();
                Map<String, Object> value1 = (Map<String, Object>) ObjectEntry.getValue();
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("NAME", key);
                resultMap.put("VALUE", value1.get("type"));
                resultList.add(resultMap);
            }
        }
    }

    @org.junit.Test
    public void test() throws IOException {
        DatasourceManage datasourceManage = new DatasourceManage();
        datasourceManage.setDatabaseAddress("127.0.0.1");
        datasourceManage.setPort(9200);
        RestHighLevelClient esHighInit = esConfig.getEsHighInit(datasourceManage);

        //模糊查询
        /*SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        WildcardQueryBuilder taskName = QueryBuilders.wildcardQuery("taskName", "2*");
        sourceBuilder.query(taskName);
        SearchRequest searchRequest = new SearchRequest("zhangxinxin*");
        searchRequest.source(sourceBuilder);*/

        //开始于
        /*SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        WildcardQueryBuilder taskName = QueryBuilders.wildcardQuery("taskName", "2*");
        sourceBuilder.query(taskName);
        SearchRequest searchRequest = new SearchRequest("zhangxinxin*");
        searchRequest.source(sourceBuilder);*/

        //结束于
        /*SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        WildcardQueryBuilder taskName = QueryBuilders.wildcardQuery("taskName", "*2");
        sourceBuilder.query(taskName);
        SearchRequest searchRequest = new SearchRequest("zhangxinxin*");
        searchRequest.source(sourceBuilder);*/

        //等于
        /*SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        TermsQueryBuilder taskName = QueryBuilders.termsQuery("taskName", "任务名称2");
        sourceBuilder.query(taskName);
        SearchRequest searchRequest = new SearchRequest("zhangxinxin*");
        searchRequest.source(sourceBuilder);*/

        List<DataModelAttribute> DimensionsList = new ArrayList<>();

        DataModelAttribute dataModelAttribute = new DataModelAttribute();
        dataModelAttribute.setId("1");
        dataModelAttribute.setFieldsName("省份");
        dataModelAttribute.setRandomAlias("SDF2TQSFASSF123FSAF");

        DataModelAttribute dataModelAttribute1 = new DataModelAttribute();
        dataModelAttribute1.setId("2");
        dataModelAttribute1.setFieldsName("配置");
        dataModelAttribute1.setRandomAlias("12asdasdasdASDASD3");

        DimensionsList.add(dataModelAttribute);
        DimensionsList.add(dataModelAttribute1);


        List<DataModelAttribute> measureList = new ArrayList<>();
        DataModelAttribute measure1 = new DataModelAttribute();
        measure1.setId("a13ea32dd884433dbe9be04f8e132f64");
        measure1.setFieldsName("折扣");
        measure1.setRandomAlias("SG3IDKIZCACD5K9DJU");

        DataModelAttribute measure2 = new DataModelAttribute();
        measure2.setId("c1ad6fc1b4a846e29bdc596a5e98fa7b");
        measure2.setRandomAlias("SG9W0ZSGINKMQTPXJX");
        measure2.setFieldsName("销售额");
        measureList.add(measure1);
        measureList.add(measure2);


        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        TermsAggregationBuilder field = AggregationBuilders.terms("省份").field("省份");
        TermsAggregationBuilder field1 = AggregationBuilders.terms("配置").field("配置");
        SumAggregationBuilder field2 = AggregationBuilders.sum("销售额").field("销售额");
        SumAggregationBuilder field3 = AggregationBuilders.sum("折扣").field("折扣");
        field.subAggregation(field1.subAggregation(field2).subAggregation(field3));

        searchSourceBuilder.aggregation(field);

        SearchRequest searchRequest = new SearchRequest("test");
        searchRequest.source(searchSourceBuilder);

        SearchResponse search = esHighInit.search(searchRequest, RequestOptions.DEFAULT);
        Map<String, Aggregation> asMap = search.getAggregations().getAsMap();
        ParsedStringTerms ieventpriAgg = (ParsedStringTerms) asMap.get("省份");
        List<? extends Terms.Bucket> buckets = ieventpriAgg.getBuckets();
        if (buckets != null) {
            for (Terms.Bucket bucket : buckets) {//循环第一层
                ParsedStringTerms cdipAgg = (ParsedStringTerms) bucket.getAggregations().getAsMap().get("配置");
                List<? extends Terms.Bucket> buckets1 = cdipAgg.getBuckets();
                for (Terms.Bucket bucket1 : buckets1) {//循环第二层
                    ParsedSum aaa = bucket1.getAggregations().get("销售额");
                    ParsedSum bbb = bucket1.getAggregations().get("折扣");

                    System.out.println("省份：" + bucket.getKeyAsString());
                    System.out.println("配置：" + bucket1.getKeyAsString());
                    System.out.println("销售额：" + aaa.getValue());
                    System.out.println("折扣：" + bbb.getValue());
                }
            }
        }

        List<BigAttributeData> value = new ArrayList<>();
        BigAttributeData bigAttributeData = new BigAttributeData();
        bigAttributeData.setAggregator("SUM");
        bigAttributeData.setId("c1ad6fc1b4a846e29bdc596a5e98fa7b");
        value.add(bigAttributeData);


        BigAttributeData bigAttributeData1 = new BigAttributeData();
        bigAttributeData1.setAggregator("SUM");
        bigAttributeData1.setId("a13ea32dd884433dbe9be04f8e132f64");
        value.add(bigAttributeData1);

        List<Map<String, Object>> mapList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        recursionElasticSearchData(DimensionsList, asMap, value, measureList, mapList, map);
        System.out.println(mapList);


        /*ParsedStringTerms ieventpriAgg1 = (ParsedStringTerms) asMap.get(DimensionsList.get(0).getFieldsName());
        List<? extends Terms.Bucket> buckets11 = ieventpriAgg1.getBuckets();
        if (buckets11 != null) {
            for (Terms.Bucket bucket : buckets11) {//循环第一层
                ParsedStringTerms cdipAgg = (ParsedStringTerms) bucket.getAggregations().getAsMap().get(DimensionsList.get(1).getFieldsName());
                List<? extends Terms.Bucket> buckets1 = cdipAgg.getBuckets();
                for (Terms.Bucket bucket1 : buckets1) {//循环第二层
                    for (DataModelAttribute modelAttribute : measureList) {
                        Sum aggregation = bucket1.getAggregations().get(modelAttribute.getFieldsName());
                        System.out.println(aggregation.getValue());
                    }
                    System.out.println("省份：" + bucket.getKeyAsString());
                    System.out.println("配置：" + bucket1.getKeyAsString());
                }
            }
        }*/


    }

    /*public void aaa(List<DataModelAttribute> dimensionsList, Map<String, Aggregation> asMap, List<BigAttributeData> value, List<Map<String, Object>> mapList, Map<String, Object> mapData) {
        for (DataModelAttribute dataModelAttribute : dimensionsList) {
            ParsedStringTerms ieventpriAgg = (ParsedStringTerms) asMap.get(dataModelAttribute.getFieldsName());
            if (ieventpriAgg != null) {
                List<? extends Terms.Bucket> buckets = ieventpriAgg.getBuckets();
                for (Terms.Bucket bucket : buckets) {
                    mapData.put(dataModelAttribute.getRandomAlias(), bucket.getKeyAsString());
                    System.out.println(bucket.getKeyAsString());
                    if (bucket.getAggregations().getAsMap().size() == value.size()) {
                        for (BigAttributeData bigAttributeData : value) {
                            Double valuedata = 0.0;
                            DataModelAttribute daoOne = dataModelAttributeDAO.findOne(bigAttributeData.getId());
                            if (bigAttributeData.getAggregator().equals("SUM")) {
                                ParsedSum sum = bucket.getAggregations().get(daoOne.getFieldsName());
                                valuedata = sum.getValue();
                            } else if (bigAttributeData.getAggregator().equals("COUNT")) {
                                ValueCount count = bucket.getAggregations().get(daoOne.getFieldsName());
                                valuedata = Double.valueOf(count.getValue());
                            } else if (bigAttributeData.getAggregator().equals("AVG")) {
                                ParsedAvg avg = bucket.getAggregations().get(daoOne.getFieldsName());
                                valuedata = avg.getValue();
                            } else if (bigAttributeData.getAggregator().equals("MAX")) {
                                ParsedMax max = bucket.getAggregations().get(daoOne.getFieldsName());
                                valuedata = max.getValue();
                            } else if (bigAttributeData.getAggregator().equals("MIN")) {
                                ParsedMin min = bucket.getAggregations().get(daoOne.getFieldsName());
                                valuedata = min.getValue();
                            }
                            mapData.put(daoOne.getRandomAlias(), valuedata);
                            System.out.println(valuedata);
                        }
                        mapList.add(mapData);
                        mapData = new HashMap<>();
                    }
                    aaa(dimensionsList, bucket.getAggregations().getAsMap(), value, mapList, mapData);
                }
            }
        }
    }*/


    public void recursionElasticSearchData(List<DataModelAttribute> dimensionsList, Map<String, Aggregation> asMap, List<BigAttributeData> value, List<DataModelAttribute> measureList, List<Map<String, Object>> mapList, Map<String, Object> mapData) {
        for (DataModelAttribute dataModelAttribute : dimensionsList) {
            ParsedStringTerms ieventpriAgg = (ParsedStringTerms) asMap.get(dataModelAttribute.getFieldsName());
            if (ieventpriAgg != null) {
                List<? extends Terms.Bucket> buckets = ieventpriAgg.getBuckets();
                for (Terms.Bucket bucket : buckets) {
                    //如果Map是上次存放数据的旧的Map，重新初始化
                    if (mapData.size() >= dimensionsList.size()) {
                        mapData = new HashMap<>();
                    }
                    mapData.put(dataModelAttribute.getRandomAlias(), bucket.getKeyAsString());
                    System.out.println(bucket.getKeyAsString());
                    if (bucket.getAggregations().getAsMap().size() == value.size()) {
                        //遍历大屏度量对象
                        for (BigAttributeData bigAttributeData : value) {
                            //遍历度量数据模型属性
                            for (DataModelAttribute modelAttribute : measureList) {
                                if (bigAttributeData.getId().equals(modelAttribute.getId())) {
                                    // TODO 提取聚合值
                                    Double valuedata = 0.0;
                                    if (bigAttributeData.getAggregator().equals("SUM")) {
                                        ParsedSum sum = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = sum.getValue();
                                    } else if (bigAttributeData.getAggregator().equals("COUNT")) {
                                        ValueCount count = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = Double.valueOf(count.getValue());
                                    } else if (bigAttributeData.getAggregator().equals("AVG")) {
                                        ParsedAvg avg = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = avg.getValue();
                                    } else if (bigAttributeData.getAggregator().equals("MAX")) {
                                        ParsedMax max = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = max.getValue();
                                    } else if (bigAttributeData.getAggregator().equals("MIN")) {
                                        ParsedMin min = bucket.getAggregations().get(modelAttribute.getFieldsName());
                                        valuedata = min.getValue();
                                    }
                                    mapData.put(modelAttribute.getRandomAlias(), valuedata);
                                    System.out.println(valuedata);
                                }
                            }
                        }
                        mapList.add(mapData);
                        mapData = new HashMap<>();
                    }
                    recursionElasticSearchData(dimensionsList, bucket.getAggregations().getAsMap(), value, measureList, mapList, mapData);
                }
            }
        }
    }


    public Map<String, Aggregation> buildAggregationBuilder(List<DataModelAttribute> dimensionsList, List<DataModelAttribute> measureList, List<BigAttributeData> value, DataModel dataModel, DatasourceManage datasourceManage, BigScreenData bigScreenData) throws IOException {
        //获取ES
        RestHighLevelClient esHighInit = esConfig.getEsHighInit(datasourceManage);
        //创建搜索器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //构建分组聚合
        TermsAggregationBuilder termsAggregationBuilder = null;
        for (DataModelAttribute dataModelAttribute : dimensionsList) {
            termsAggregationBuilder = AggregationBuilders.terms(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()).size(bigScreenData.getLimit());
            if (termsAggregationBuilder != null) {
                termsAggregationBuilder.subAggregation(AggregationBuilders.terms(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()).size(bigScreenData.getLimit()));
            }
        }
        //构建计算聚合
        for (BigAttributeData bigAttributeData : value) {
            for (DataModelAttribute dataModelAttribute : measureList) {
                if (bigAttributeData.getId().equals(dataModelAttribute.getId())) {
                    //获取聚合类型
                    String aggregator = bigAttributeData.getAggregator();
                    if ("SUM".equals(aggregator)) {
                        termsAggregationBuilder.subAggregation(AggregationBuilders.sum(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                    } else if ("COUNT".equals(aggregator)) {
                        termsAggregationBuilder.subAggregation(AggregationBuilders.count(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                    } else if ("AVG".equals(aggregator)) {
                        termsAggregationBuilder.subAggregation(AggregationBuilders.avg(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                    } else if ("MAX".equals(aggregator)) {
                        termsAggregationBuilder.subAggregation(AggregationBuilders.max(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                    } else if ("MIN".equals(aggregator)) {
                        termsAggregationBuilder.subAggregation(AggregationBuilders.min(dataModelAttribute.getFieldsName()).field(dataModelAttribute.getFieldsName()));
                    }
                }
            }
        }
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        SearchRequest searchRequest = new SearchRequest(dataModel.getIndexes());
        searchRequest.source(searchSourceBuilder);
        SearchResponse search = esHighInit.search(searchRequest, RequestOptions.DEFAULT);
        Map<String, Aggregation> asMap = search.getAggregations().getAsMap();
        return asMap;
    }


    @org.junit.Test
    public void aaa() throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {

        /*URL someUrl = new URL("https://192.168.7.130:9200");
        System.out.println(someUrl);
        HttpURLConnection connection = (HttpURLConnection) someUrl.openConnection();
        System.out.println(connection);
        TrustModifier.relaxHostChecking(connection);*/

        /*DatasourceManage datasourceManage = new DatasourceManage();
        datasourceManage.setDatabaseAddress("192.168.7.130");
        datasourceManage.setPort(9200);
        RestHighLevelClient esHighInit = esConfig.getEsHighInit1(datasourceManage);
        SearchRequest searchRequest = new SearchRequest("property");
        SearchResponse search = esHighInit.search(searchRequest, RequestOptions.DEFAULT);
        for (SearchHit hit : search.getHits().getHits()) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            System.out.println(sourceAsMap);
        }*/
    }


    @org.junit.Test
    public void nnn(){
        List<Integer> integerList = new ArrayList<>(4);
        integerList.add(1);
        integerList.add(2);
        integerList.add(3);
        integerList.add(4);
        System.out.println(integerList);

        int i = integerList.indexOf(3);
        integerList.set(i,10);
        System.out.println(integerList);


    }
}
