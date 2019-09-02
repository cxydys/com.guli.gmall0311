package com.atguigu.gmall.manage.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.SkuInfo;
import com.atguigu.bean.SkuLsInfo;
import com.atguigu.gmall.Service.Service.ListService;
import com.atguigu.gmall.Service.Service.ManageService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class saveSkuInfoController {

    @Reference
    private ManageService manageService;

    @Reference
    private ListService listService;

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

    @RequestMapping("onSale")
    public String onSale(String skuId) {
        //创建要保存的对象
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        //通过skuId获取skuInfo对象
        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
        //将查到的数据拷贝给要保存的对象
        BeanUtils.copyProperties(skuInfo, skuLsInfo);
        listService.saveSkuInfo(skuLsInfo);
        return "ok";
    }
}
