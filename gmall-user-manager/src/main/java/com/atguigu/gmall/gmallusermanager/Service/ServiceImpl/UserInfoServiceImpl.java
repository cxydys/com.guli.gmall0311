package com.atguigu.gmall.gmallusermanager.Service.ServiceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.bean.UserInfo;
import com.atguigu.gmall.Service.Service.UserInfoService;
import com.atguigu.gmall.gmallusermanager.Mapper.UserInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    /**
     * 查找所有
     * @param userInfo
     * @return
     */
    @Override
    public List<UserInfo> findAll(UserInfo userInfo) {
        return userInfoMapper.select(userInfo);
    }

}
