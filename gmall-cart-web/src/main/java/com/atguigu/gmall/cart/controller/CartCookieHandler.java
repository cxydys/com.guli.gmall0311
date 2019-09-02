package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.bean.CartInfo;
import com.atguigu.bean.SkuInfo;
import com.atguigu.gmall.Service.Service.ManageService;
import config.CookieUtil;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Component
public class CartCookieHandler {

    // 定义购物车名称
    private String cookieCartName = "CART";
    // 设置cookie 过期时间
    private int COOKIE_CART_MAXAGE = 7 * 24 * 3600;

    @Reference
    private ManageService manageService;

    /**
     * @param request
     * @param response
     * @param skuId
     * @param userId
     * @param skuNum
     */
    public void addCart(HttpServletRequest request, HttpServletResponse response, String skuId, String userId, int skuNum) {
        //1.判断cookie中是否有该商品 通过skuId 去cookie 中循环比较
        //2.没有的话，直接添加，没有的话，和之前的商品相加
        //3.将商品放入到cookie中
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        ArrayList<CartInfo> cartInfoList = new ArrayList<>();
        boolean ifExist = false;
        //存在
        if (StringUtils.isNotEmpty(cookieValue)) {
            List<CartInfo> cartInfos = JSON.parseArray(cookieValue, CartInfo.class);
            for (CartInfo cartInfo : cartInfos) {
                if (cartInfo.getSkuId().equals(skuId)) {
                    //数量相加
                    cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum);
                    cartInfo.setCartPrice(cartInfo.getCartPrice());
                    ifExist = true;
                }
            }
        }
        //不存在
        if (!ifExist) {
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            CartInfo cartInfo = new CartInfo();
            CartInfo cartInfo1 = new CartInfo();
            cartInfo1.setSkuId(skuId);
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setUserId(userId);
            cartInfo1.setSkuNum(skuNum);
            //放入集合中
            cartInfoList.add(cartInfo1);
        }
        //写入cookie中
        CookieUtil.setCookie(request, response, cookieCartName, JSON.toJSONString(cartInfoList), COOKIE_CART_MAXAGE, true);

    }

    /**
     * 在未登录状态下，从cookie中获取数据
     *
     * @param request
     * @return
     */
    public List<CartInfo> getCartList(HttpServletRequest request) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        if (StringUtils.isNotEmpty(cookieValue)) {
            cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
        }
        return cartInfoList;
    }

    /**
     * 删除cookie中的数据
     * @param request
     * @param response
     */
    public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request,response,cookieCartName);
    }
}
