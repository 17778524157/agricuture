package com.agriculture.mall.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * Canal配置类
 */
@Configuration
@Slf4j
public class CanalConfig {

    @Value("${canal.server.host:192.168.10.128}")
    private String canalHost;

    @Value("${canal.server.port:11111}")
    private int canalPort;

    @Value("${canal.destination:example}")
    private String destination;

    @Value("${canal.username:}")
    private String username;

    @Value("${canal.password:}")
    private String password;

    /**
     * Canal连接器Bean
     */
    @Bean
    public CanalConnector canalConnector() {
        CanalConnector connector = CanalConnectors.newSingleConnector(
            new InetSocketAddress(canalHost, canalPort),
            destination,
            username,
            password
        );
        
        log.info("Canal连接器配置完成: {}:{}", canalHost, canalPort);
        return connector;
    }
}
