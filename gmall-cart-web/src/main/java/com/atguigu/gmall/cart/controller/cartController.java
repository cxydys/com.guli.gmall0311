package com.atguigu.gmall.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.LoginRequire;
import com.atguigu.bean.CartInfo;
import com.atguigu.bean.SkuInfo;
import com.atguigu.gmall.Service.Service.CartService;
import com.atguigu.gmall.Service.Service.ListService;
import com.atguigu.gmall.Service.Service.ManageService;
import config.CookieUtil;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
public class cartController {

    @Reference
    private CartService cartService;

    @Autowired
    private CartCookieHandler cartCookieHandler;

    @Reference
    private ManageService manageService;


    @RequestMapping("addToCart")
    @LoginRequire(autoRedirect = false)
    public String cartController(HttpServletRequest request, HttpServletResponse response) {
        //获取前台传过来的skuNum和skuId
        String skuNum = request.getParameter("skuNum");
        String skuId = request.getParameter("skuId");
        //再获取userId
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            //登录了 ,将商品添加到数据库中，并且在redis中保存一份
            cartService.addCart(skuId, userId, Integer.parseInt(skuNum));

        } else {
            //未登录,将商品信息放到cookie中
            cartCookieHandler.addCart(request, response, skuId, userId, Integer.parseInt(skuNum));


        }
        //通过skId查询skuInfo
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        request.setAttribute("skuNum", skuNum);
        request.setAttribute("skuInfo", skuInfo);
        return "success";
    }

    @RequestMapping("cartList")
    @LoginRequire(autoRedirect = false)
    public String cartList(HttpServletRequest request, HttpServletResponse response) {
        List<CartInfo> cartInfoList = new ArrayList<>();
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            //进行合并
            //查看cookie中是否有数据
            List<CartInfo> cartListOK = cartCookieHandler.getCartList(request);
            if (cartListOK != null && cartListOK.size() > 0) {
                //合并购物车
                cartInfoList = cartService.mergeToCartList(cartListOK, userId);
                //删除未登录的数据
                cartCookieHandler.deleteCartCookie(request, response);

            } else {
                //从redis中获取数据-mysql
                cartInfoList = cartService.getCartList(userId);
            }

        } else {
            cartInfoList = cartCookieHandler.getCartList(request);
        }
        //从coookie中获取数据
        request.setAttribute("cartInfoList", cartInfoList);

        return "cartList";
    }

}
