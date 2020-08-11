package com.sbr.visualization.config;

import com.sbr.visualization.datasourcemanage.model.DatasourceManage;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.stereotype.Component;

/**
 * @ClassName EsConfig
 * @Description TODO ElasticSearch配置
 * @Author zxx
 * @Version 1.0
 */
@Component
public class EsConfig {

    /**
     * @param datasourceManage 数据源参数
     * @return org.elasticsearch.client.RestHighLevelClient
     * @Author zxx
     * @Description //TODO 获取ElasticSearch连接
     * @Date 14:21 2020/6/12
     **/
    public RestHighLevelClient getEsHighInit(DatasourceManage datasourceManage) {
        RestClientBuilder http = RestClient.builder(new HttpHost(datasourceManage.getDatabaseAddress(), datasourceManage.getPort(), "http"))
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        requestConfigBuilder.setConnectTimeout(700000);//连接超时时间
                        requestConfigBuilder.setSocketTimeout(600000); //socket连接超时时间
                        requestConfigBuilder.setConnectionRequestTimeout(100000);//连接超时
                        return requestConfigBuilder;
                    }
                });
        return new RestHighLevelClient(http);

    }


    public RestHighLevelClient getEsHighInit1(DatasourceManage datasourceManage) {
        Header[] defaultHeaders = new Header[]{new BasicHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6ImFkbWluIiwiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3QiLCJzdWIiOiJhZG1pbiIsImV4cCI6MTkwMjIxMDc1OX0.DXNoc2TdMVS83C9X5jMUV5ImwEjFtrH_92Wf-4jh9S8")};
        RestClientBuilder http = RestClient.builder(new HttpHost(datasourceManage.getDatabaseAddress(), datasourceManage.getPort(), "https")).setDefaultHeaders(defaultHeaders)
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        requestConfigBuilder.setConnectTimeout(700000);//连接超时时间
                        requestConfigBuilder.setSocketTimeout(600000); //socket连接超时时间
                        requestConfigBuilder.setConnectionRequestTimeout(100000);//连接超时
                        return requestConfigBuilder;
                    }
                });
        return new RestHighLevelClient(http);

    }
}
