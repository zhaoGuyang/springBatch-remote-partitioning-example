package com.kailing.bootbatch.partitionjob.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.integration.amqp.inbound.AmqpInboundChannelAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by kl on 2018/3/1.
 * Content :远程分区通讯
 */
@Configuration
//注入配置文件属性
@ConfigurationProperties(prefix = "spring.rabbit")
public class IntegrationConfiguration {
    private String host;
    private Integer port=5672;
    private String username;
    private String password;
    private String virtualHost;
    private int connRecvThreads=5;
    private int channelCacheSize=10;
//配置连接的ActiveMQ服务器，采用连接池
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(connRecvThreads);
        executor.initialize();
        connectionFactory.setExecutor(executor);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setChannelCacheSize(channelCacheSize);
        return connectionFactory;
    }

   @Bean
    @Profile({"slave","mixed"})
    public AmqpInboundChannelAdapter inbound(SimpleMessageListenerContainer listenerContainer) {
        AmqpInboundChannelAdapter adapter = new AmqpInboundChannelAdapter(listenerContainer);
        adapter.setOutputChannelName("inboundRequests");
        adapter.afterPropertiesSet();
        return adapter;
    }
//监听partition.requests队列
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueueNames("partition.requests");
        //设置autoStartUp为false表示SimpleMessageListenerContainer没有启动,让spring来管理
        container.setAutoStartup(false);

        return container;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getVirtualHost() {
        return virtualHost;
    }

    public void setVirtualHost(String virtualHost) {
        this.virtualHost = virtualHost;
    }

    public int getConnRecvThreads() {
        return connRecvThreads;
    }

    public void setConnRecvThreads(int connRecvThreads) {
        this.connRecvThreads = connRecvThreads;
    }

    public int getChannelCacheSize() {
        return channelCacheSize;
    }

    public void setChannelCacheSize(int channelCacheSize) {
        this.channelCacheSize = channelCacheSize;
    }
}
