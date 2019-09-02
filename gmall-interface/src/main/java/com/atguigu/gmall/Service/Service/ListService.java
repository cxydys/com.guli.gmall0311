package com.atguigu.gmall.Service.Service;

import com.atguigu.bean.SkuLsInfo;
import com.atguigu.bean.SkuLsParams;
import com.atguigu.bean.SkuLsResult;

public interface ListService {

   public void saveSkuInfo(SkuLsInfo skuLsInfo);

    SkuLsResult search(SkuLsParams skuLsParams);

    /**
     * 点击商品进行热度排序
     * @param skuId
     */
    void updHotScore(String skuId);
}
