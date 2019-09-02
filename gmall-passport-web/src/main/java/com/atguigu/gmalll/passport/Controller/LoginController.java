package com.atguigu.gmalll.passport.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.UserInfo;
import com.atguigu.gmall.Service.Service.UserInfoService;
import com.atguigu.gmalll.passport.config.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Value("${token.key}")
    private String key;

    @Reference
    private UserInfoService userInfoService;

    @RequestMapping("index")
    public String index(HttpServletRequest request) {
        String originUrl = request.getParameter("originUrl");
        //保存originUrl，将他放到域中
        request.setAttribute("originUrl", originUrl);
        return "index";
    }

    /**
     * 登录功能
     * 1.用户输入用户名和密码，传递到后台，再传递到数据库，进行对比，
     * 2.登录成功以后，将数据放入到redis中，并生成一个Token，放入到前台的cookie中
     *
     * @param userInfo
     * @return
     */
    @RequestMapping("login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request) {
        //调用服务层
        UserInfo info = userInfoService.login(userInfo);
        if (info != null) {
            //生成一个Token
            HashMap<String, Object> map = new HashMap<>();
            map.put("userId", info.getId());
            map.put("nickName", info.getNickName());
            String salt = request.getHeader("X-forwarded-for");
            String token = JwtUtil.encode(key, map, salt);
            return token;
        } else {
            return "fail";
        }
    }

    /**
     * 认证中心
     *
     * @return
     */
    @RequestMapping("verify")
    @ResponseBody
    public String verify(HttpServletRequest request) {
        String token = request.getParameter("token");
        String salt = request.getParameter("salt");
        //解密，取出token
        Map<String, Object> map = JwtUtil.decode(token, key, salt);
        if (map != null && map.size() > 0) {
            String userId = (String) map.get("userId");
            UserInfo userInfo = userInfoService.verify(userId);
            if (userInfo != null) {
                return "success";
            }
        }
        return "fail";
    }
}
