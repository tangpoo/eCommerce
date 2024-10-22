package com.potato.ecommerce.global.config.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
public class ElasticSearchConfig {

    @Value("${es.host}")
    private String host;

    @Value("${es.port}")
    private int port;

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        // Create the low-level REST client
        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "http"));

        // Create the high-level client using the low-level client
        RestClientTransport transport = new RestClientTransport(
            builder.build(), new JacksonJsonpMapper());

        // Create the new ElasticsearchClient instance
        return new ElasticsearchClient(transport);
    }

    /* SSL 인증 및 대용량 트래픽 대비 상세 설정 */
//    @Value("${es.fingerprint}")
//    private String fingerprint;
//
//    @Value("${es.id}")
//    private String id;
//
//    @Value("${es.password}")
//    private String password;

//    @Bean(name = "esClient")
//    public ElasticsearchClient esClient() throws Exception{
//
//        // SSLContext 생성 (필요 시)
//        SSLContext sslContext = SSLContextBuilder.create().loadTrustMaterial((chain, authType) -> true).build();
//
//        // Credentials for basic authentication
//        BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("username", "password"));
//
//        // IOReactorConfig 설정
//        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
//            .setIoThreadCount(Runtime.getRuntime().availableProcessors())
//            .build();
//
//        // ConnectingIOReactor 생성
//        ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
//
//        // PoolingNHttpClientConnectionManager 생성
//        PoolingNHttpClientConnectionManager connectionManager = new PoolingNHttpClientConnectionManager(ioReactor);
//        connectionManager.setMaxTotal(100);  // 최대 전체 커넥션 수
//        connectionManager.setDefaultMaxPerRoute(20);  // 각 경로당 최대 커넥션 수
//
//        // Set up the low-level client with SSL, authentication, and connection pooling
//        RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200, "https"))
//            .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
//                .setSSLContext(sslContext)
//                .setDefaultCredentialsProvider(credentialsProvider)
//                .setConnectionManager(connectionManager)
//                .setMaxConnTotal(100)  // 최대 전체 커넥션 수 설정
//                .setMaxConnPerRoute(20)  // 경로당 최대 커넥션 수 설정
//            )
//            .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
//                .setConnectTimeout(5000)  // 연결 타임아웃 설정
//                .setSocketTimeout(60000)  // 소켓 타임아웃 설정
//            );
//
//        // Set up the high-level client
//        RestClientTransport transport = new RestClientTransport(builder.build(), new JacksonJsonpMapper());
//
//        return new ElasticsearchClient(transport);
//    }
}
