package com.atguigu.gmall.gmallusermanager.Controller;

import com.atguigu.bean.UserInfo;
import com.atguigu.gmall.Service.Service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("findAll")
    public List<UserInfo> findAll(UserInfo userInfo) {
        return userInfoService.findAll(userInfo);
    }

}
