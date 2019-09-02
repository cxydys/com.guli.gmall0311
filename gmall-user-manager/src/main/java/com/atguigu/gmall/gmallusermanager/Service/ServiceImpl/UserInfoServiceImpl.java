package com.atguigu.gmall.gmallusermanager.Service.ServiceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.RedisUtil;
import com.atguigu.bean.UserInfo;
import com.atguigu.gmall.Service.Service.UserInfoService;
import com.atguigu.gmall.gmallusermanager.Mapper.UserInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private RedisUtil redisUtil;

    public String userKey_prefix = "user:";
    public String userinfoKey_suffix = ":info";
    public int userKey_timeOut = 60 * 60 * 24;

    /**
     * 查找所有
     *
     * @param userInfo
     * @return
     */
    @Override
    public List<UserInfo> findAll(UserInfo userInfo) {

        return userInfoMapper.select(userInfo);
    }

    /**
     * 登录功能
     *
     * @param userInfo
     * @return
     */
    @Override
    public UserInfo login(UserInfo userInfo) {
        //将密码进行加密
        //1.先获取原始的密码
        String passwd = userInfo.getPasswd();
        //将原始密码进行加密
        String newPasswd = DigestUtils.md5DigestAsHex(passwd.getBytes());
        //将新的加密的密码放入到传入的userInfo中
        userInfo.setPasswd(newPasswd);
        UserInfo info = userInfoMapper.selectOne(userInfo);
        if (info != null) {
            Jedis jedis = null;
            try {
                //获得到redis,将用户信息保存到redis中
                jedis = redisUtil.getJedis();
                //定义一个key    user:skuId:info
                String userKey = userKey_prefix + info.getId() + userinfoKey_suffix;
                //考虑用哪种数据类型   因为登录之后，就不需要再修改，所以考虑用String类型存储数据
                jedis.setex(userKey, userKey_timeOut, JSON.toJSONString(info));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
            return info;

        }
        return null;
    }

    /**
     * 认证中心
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfo verify(String userId) {
        //因为数据库中有数据了，所有已会将数据放在redis中，那么，我们就将数据从redis中获取出来就可以，操作如下
        Jedis jedis = redisUtil.getJedis();
        //定义key
        String userKey = userKey_prefix + userId + userinfoKey_suffix;
        //从key中取出userId
        String userJson = jedis.get(userKey);
        if (org.springframework.util.StringUtils.isEmpty(userJson)) {
            jedis.expire(userJson, userKey_timeOut);
            //转成对象
            UserInfo info = JSON.parseObject(userJson, UserInfo.class);
            return info;
        }
        jedis.close();
        return null;
    }

}
