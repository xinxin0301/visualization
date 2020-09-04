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

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

/**
 * @ClassName EsConfig
 * @Description TODO ElasticSearch配置
 * @Author zxx
 * @Version 1.0
 */
@Component
public class EsConfig {

    @PostConstruct
    public void run() throws Exception {
        disableSslVerification();
    }


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

    //111
    //Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6ImFkbWluIiwiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3QiLCJzdWIiOiJhZG1pbiIsImV4cCI6MTkwMjIxMDc1OX0.DXNoc2TdMVS83C9X5jMUV5ImwEjFtrH_92Wf-4jh9S8

    public RestHighLevelClient getEsHighInit1(DatasourceManage datasourceManage) {
        /*Header[] defaultHeaders = new Header[]{new BasicHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6ImFkbWluIiwiaXNzIjoiaHR0cHM6Ly9sb2NhbGhvc3QiLCJzdWIiOiJhZG1pbiIsImV4cCI6MTkwMjIxMDc1OX0.DXNoc2TdMVS83C9X5jMUV5ImwEjFtrH_92Wf-4jh9S8")};
        RestClientBuilder http = RestClient.builder(new HttpHost(datasourceManage.getDatabaseAddress(), datasourceManage.getPort(), "https")).setDefaultHeaders(defaultHeaders)
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        requestConfigBuilder.setConnectTimeout(700000);//连接超时时间
                        requestConfigBuilder.setSocketTimeout(600000); //socket连接超时时间
                        requestConfigBuilder.setConnectionRequestTimeout(100000);//连接超时
                        return requestConfigBuilder;
                    }
                });*/

        /*final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials("root", "sbrAdmin123321"));*/

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
                })/*.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        httpClientBuilder.disableAuthCaching();
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                })*/;

        return new RestHighLevelClient(http);
    }


    public RestHighLevelClient getEsHighInit2() {

        RestClientBuilder http = RestClient.builder(new HttpHost("127.0.0.1", 9200, "http"))
                .setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
                    @Override
                    public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                        requestConfigBuilder.setConnectTimeout(700000);
                        requestConfigBuilder.setSocketTimeout(600000);
                        requestConfigBuilder.setConnectionRequestTimeout(100000);
                        return requestConfigBuilder;
                    }
                });
        return new RestHighLevelClient(http);

    }


    /**
     * 忽略https证书
     */
    private static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
            };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }


}
