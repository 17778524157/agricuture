package com.agriculture.user.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Elasticsearch配置类
 */
@Slf4j
@Configuration
public class ElasticsearchConfig {
    
    @Value("${elasticsearch.host:192.168.10.128}")
    private String host;
    
    @Value("${elasticsearch.port:9200}")
    private int port;
    
    @Value("${elasticsearch.scheme:http}")
    private String scheme;
    
/**
 * 创建并配置Elasticsearch的RestHighLevelClient Bean
 * 该方法用于初始化Elasticsearch客户端，以便与Elasticsearch集群进行交互
 *
 * @return 配置好的RestHighLevelClient实例，用于后续的Elasticsearch操作
 */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
    // 使用RestClient.builder构建基础客户端，并指定主机、端口和协议
        RestHighLevelClient client = new RestHighLevelClient(
            RestClient.builder(new HttpHost(host, port, scheme))
        );
    // 记录客户端初始化成功的日志，包含连接信息（协议、主机和端口）
        log.info("Elasticsearch客户端初始化成功: {}://{}:{}", scheme, host, port);
    // 返回配置完成的客户端实例
        return client;
    }
}


