package com.atguigu.gmall.Service.Service;

import com.atguigu.bean.UserAddress;

import java.util.List;

public interface UserAddressService {
    List<UserAddress> getUserAddressList(String userId);
}
