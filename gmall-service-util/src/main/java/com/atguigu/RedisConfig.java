package com.atguigu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration  //这个注解的意思是将配置文件都放到IOC容器里面
public class RedisConfig {

    @Value("${spring.redis.host:disable}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Value("${spring.redis.timeOut:10000}")
    private int timeOut;


    /*
   <bean id = "redisUtil" class="com.atguigu.gmall0311.config.RedisUtil">
   </bean>
    */
    @Bean
    public RedisUtil getRedisUtil(){
        if ("disable".equals(host)){
            return null;
        }
        RedisUtil redisUtil = new RedisUtil();
        redisUtil.initJedisPool(host,port,timeOut,database);
        return redisUtil;
    }


}
