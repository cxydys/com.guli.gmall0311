package com.atguigu.gmall.item.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.bean.SkuImage;
import com.atguigu.bean.SkuInfo;
import com.atguigu.bean.SkuSaleAttrValue;
import com.atguigu.bean.SpuSaleAttr;
import com.atguigu.gmall.Service.Service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {

    @Reference
    private ManageService manageService;

    @RequestMapping("{skuId}.html")
    public String skuInfoPage(@PathVariable(value = "skuId") String skuId, Map<String, Object> map) {

        SkuInfo skuInfo = manageService.getSkuInfo(skuId);

        List<SkuImage> skuImageList = manageService.getSkuImageBySkuId(skuId);

        /**
         * 根据spuId查询销售属性
         */
        List<SpuSaleAttr> spuSaleAttrList = manageService.getSpuSaleAttrListCheckBySku(skuInfo);
        map.put("spuSaleAttrList", spuSaleAttrList);
        map.put("skuInfo", skuInfo);
        map.put("skuImageList", skuImageList);
        /**
         * 查询销售属性值拼接成一个spuId
         */
        List<SkuSaleAttrValue> skuSaleAttrValueList = manageService.getSkuSaleAttrValueListBySpu(skuInfo.getSpuId());
        String key = "";
        HashMap<String, String> objectObjectHashMap = new HashMap<>();
        for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
            SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
            // 第一次拼接： key=118
            // 第二次拼接： key=118|
            // 第三次拼接： key=118|120
            // 第四次拼接： key=""
            // 什么时候拼接|
            if (key.length() > 0) {
                key += "|";
            }
            key += skuSaleAttrValue.getSaleAttrValueId();
            // 拼接规则：skuId 与 下一个skuId 不相等的时候，不拼接！ 当拼接到集合末尾则不拼接
            if ((i + 1) == skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i + 1).getSkuId())) {
                //放入map中
                objectObjectHashMap.put(key, skuSaleAttrValue.getSkuId());
                //清空key
                key = "";
            }
        }
        // 将map 转换为json 字符串
        String valuesSkuJson = JSON.toJSONString(objectObjectHashMap);
        map.put("valuesSkuJson", valuesSkuJson);
        return "item";

    }
}
