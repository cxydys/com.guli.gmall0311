package com.atguigu.gmalll.passport.config;

import io.jsonwebtoken.*;

import java.util.Map;

public class JwtUtil {

    //加密的方法
    //key为公共部分   Map为私有部分(存放的是用户信息)   salt为本机的ip地址，便于安全系数更加的高
    public static String encode(String key, Map<String, Object> param, String salt) {
        if (salt != null) {
            key += salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256, key);

        jwtBuilder = jwtBuilder.setClaims(param);

        String token = jwtBuilder.compact();
        return token;

    }

    /**
     * 解密的方法
     *
     * @param token
     * @param key
     * @param salt
     * @return
     */
    public static Map<String, Object> decode(String token, String key, String salt) {
        Claims claims = null;
        if (salt != null) {
            key += salt;
        }
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            return null;
        }
        return claims;
    }

}
