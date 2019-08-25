package com.atguigu;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtil {
    /**
     * 步骤  1.创建JedisPoll 2.获取Jedis
     */

    private JedisPool jedisPool;

    public void initJedisPool(String host, int port, int timeOut ,int database) {
        // 创建配置连接池的参数类
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 设置连接池最大核心数
        jedisPoolConfig.setMaxTotal(200);
        //设置连接时的最大等待的毫秒数
        jedisPoolConfig.setMaxWaitMillis(10 * 1000);
        //最少剩余数
        jedisPoolConfig.setMinIdle(10);
        //如果到最大数，设置等待
        jedisPoolConfig.setBlockWhenExhausted(true);
        // 设置当用户获取到jedis 时，做自检看当前获取到的jedis 是否可以使用！
        jedisPoolConfig.setTestOnBorrow(true);
        //创建连接池
        jedisPool = new JedisPool(jedisPoolConfig,host,port,timeOut);

    }

    //获取Jedis
    public Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
}
