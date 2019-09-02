package com.atguigu.gmalll.passport;

import com.atguigu.gmalll.passport.config.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallPassportWebApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void TestJWT() {
        String key = "atguigu";
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", "1001");
        map.put("nickName", "marry");
        String salt = "192.168.19.220";
        String token = JwtUtil.encode(key, map, salt);
        System.out.println("token====================" + token);

        Map<String, Object> decode = JwtUtil.decode(token, key, salt);
        System.out.println("decode+++++++++++++++" + decode);
    }

}
