package com.atguigu.gmall.gmallusermanager.Service.ServiceImpl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.bean.UserAddress;
import com.atguigu.gmall.Service.Service.UserAddressService;
import com.atguigu.gmall.gmallusermanager.Mapper.UserAddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Autowired
    private UserAddressMapper userAddressMapper;

    @Override
    public List<UserAddress> getUserAddressList(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }
}
