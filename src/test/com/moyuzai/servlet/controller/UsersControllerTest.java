package com.moyuzai.servlet.controller;

import com.moyuzai.servlet.entity.User;
import com.moyuzai.servlet.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by xiang on 2017/7/30.
 */
@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml","classpath:spring/spring-web.xml"})

public class UsersControllerTest {

    private final Logger logger= LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @Resource
    private JedisPool jedisPool;

    @org.junit.Test
    public void test() {
        //从连接池中获取jedis实例
        Jedis jedis = jedisPool.getResource();
        //设置键值对
        jedis.set("admin", "root");
        //根据key查询
        String admin = jedis.get("admin");
        //打印
        System.out.println("admin:" + admin);
        //关闭jedis实例
        jedis.close();
    }

}