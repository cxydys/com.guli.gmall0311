package com.atguigu.gmall.Service.Service;

import com.atguigu.bean.CartInfo;

import java.util.List;

public interface CartService {
    void addCart(String skuId,String userId, Integer skuNum);

    /**
     * 根据用户id查询购物车中的数据
     * @param userId
     * @return
     */
    List<CartInfo> getCartList(String userId);

    /**
     * 合并购物车
     * @param cartListOK
     * @param userId
     * @return
     */
    List<CartInfo> mergeToCartList(List<CartInfo> cartListOK, String userId);
}
