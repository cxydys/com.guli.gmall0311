package com.atguigu.gmall.manage.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.SkuInfo;
import com.atguigu.gmall.Service.Service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class saveSkuInfoController {

    @Reference
    private ManageService manageService;

    /**
     * http://localhost:8082/saveSkuInfo
     * 保存sku中的所有的数据
     *
     * @param skuInfo
     * @return
     */
    @RequestMapping("saveSkuInfo")
    public String saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        if (skuInfo != null) {
            manageService.saveSkuInfo(skuInfo);
        }

        return "success";
    }
}
