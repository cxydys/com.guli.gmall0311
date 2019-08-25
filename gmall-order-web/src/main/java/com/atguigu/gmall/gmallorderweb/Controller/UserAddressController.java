package com.atguigu.gmall.gmallorderweb.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.UserAddress;
import com.atguigu.gmall.Service.Service.UserAddressService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserAddressController {
    @Reference
    private UserAddressService userAddressService;

    @RequestMapping("getUserAddressList")
    public List<UserAddress> getUserAddressList(String userId){

        return userAddressService.getUserAddressList(userId);
    }
}
