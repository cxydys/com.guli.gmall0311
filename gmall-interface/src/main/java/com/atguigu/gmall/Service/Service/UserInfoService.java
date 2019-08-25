package com.atguigu.gmall.Service.Service;

import com.atguigu.bean.UserInfo;

import java.util.List;

public interface UserInfoService {
    List<UserInfo> findAll(UserInfo userInfo);
}
