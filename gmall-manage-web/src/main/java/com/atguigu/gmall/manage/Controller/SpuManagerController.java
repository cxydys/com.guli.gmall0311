package com.atguigu.gmall.manage.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.bean.*;
import com.atguigu.gmall.Service.Service.ManageService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
public class SpuManagerController {

    @Reference
    private ManageService manageService;

    /**
     * 查询销售属性
     *
     * @return
     */
    @RequestMapping("baseSaleAttrList")
    public List<BaseSaleAttr> baseSaleAttrList() {
        return manageService.getBaseSaleAttrList();
    }

    /**
     * 保存数据
     *
     * @param spuInfo
     * @return
     */
    @RequestMapping("saveSpuInfo")
    public String saveSpuInfo(@RequestBody SpuInfo spuInfo) {
        manageService.saveSpuInfo(spuInfo);
        return "success";
    }

    /**
     * http://localhost:8082/spuImageList?spuId=62
     * 根据spuId查询图片
     *
     * @param spuImage
     * @return
     */
    @RequestMapping("spuImageList")
    public List<SpuImage> getSpuImageList(SpuImage spuImage) {
        return manageService.getSpuImageList(spuImage);
    }

    /**
     * http://localhost:8082/spuSaleAttrList?spuId=62
     * 根据spuId查询销售属性集合
     *
     * @param spuId
     * @return
     */
    @RequestMapping("spuSaleAttrList")
    public List<SpuSaleAttr> getspuSaleAttrList(String spuId) {
        return manageService.getSpuSaleAttrList(spuId);
    }
}
