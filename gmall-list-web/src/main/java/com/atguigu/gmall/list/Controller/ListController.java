package com.atguigu.gmall.list.Controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.bean.*;
import com.atguigu.gmall.Service.Service.ListService;
import com.atguigu.gmall.Service.Service.ManageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Controller
public class ListController {

    @Reference
    private ListService listService;

    @Reference
    private ManageService manageService;

    @RequestMapping("list.html")
    public String getList(SkuLsParams skuLsParams, Map<String, Object> map) {

        // 每页显示两条数据：
        skuLsParams.setPageSize(2);

        SkuLsResult skuLsResult = listService.search(skuLsParams);
        List<SkuLsInfo> skuLsInfoList = skuLsResult.getSkuLsInfoList();
        map.put("skuLsInfoList", skuLsInfoList);

        //获取平台属性值和平台属性值
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        List<BaseAttrInfo> baseAttrInfoList = null;
        //根据平台属性值id获取平台属性值和平台属性值
        /*if (skuLsParams.getCatalog3Id() != null) {
            List<BaseAttrInfo> attrList = manageService.getAttrList(skuLsParams.getCatalog3Id());
        } else {
        }*/
        baseAttrInfoList = manageService.getAttrList(attrValueIdList);

        map.put("baseAttrInfoList", baseAttrInfoList);
        System.out.println(JSON.toJSONString(skuLsResult));


        //编写一个方法，用来记录查询的条件
        String urlParam = makeUrlParam(skuLsParams);
        map.put("urlParam", urlParam);

        // 声明一个面包屑集合
        ArrayList<BaseAttrValue> baseAttrValueArrayList = new ArrayList<>();

        //保存面包屑
        map.put("baseAttrValueArrayList", baseAttrValueArrayList);

        //保存搜索的关键字
        map.put("keyword",skuLsParams.getKeyword());

        map.put("totalPages",skuLsResult.getTotalPages());
        map.put("pageNo",skuLsParams.getPageNo());

        //集合在遍历过程中，要删除对应的数据(用迭代器)
        for (Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator(); iterator.hasNext(); ) {
            //获取平台属性对象
            BaseAttrInfo baseAttrInfo = iterator.next();
            //获取平台属性值对象
            List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
            for (BaseAttrValue baseAttrValue : attrValueList) {
                if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
                    for (String valueId : skuLsParams.getValueId()) {
                        if (valueId.equals(baseAttrValue.getId())) {

                            iterator.remove();

                            //构成面包屑
                            BaseAttrValue baseAttrValueed = new BaseAttrValue();
                            baseAttrValueed.setValueName(baseAttrInfo.getAttrName() + ":" + baseAttrValue.getValueName());
                            baseAttrValueArrayList.add(baseAttrValueed);

                            //重新制作urlParam参数
                         String newUrlParam =   makeUrlParam(skuLsParams,valueId);
                            //保存面包屑的URL参数
                            baseAttrValueed.setUrlParam(newUrlParam);
                        }
                    }
                }
            }

        }
        return "list";
    }

    /**
     * 该方法用来记录查询的条件
     *
     * @param skuLsParams
     * @return
     */
    private String makeUrlParam(SkuLsParams skuLsParams,String... excludeValueIds) {
        String urlParam = "";
        //list.html?keyword=手机
        if (skuLsParams.getKeyword() != null && skuLsParams.getKeyword().length() > 0) {
            if (urlParam.length() > 0) {
                urlParam += "&";
            }
            urlParam += "keyword=" + skuLsParams.getKeyword();
        }
        //list.html?catalog3Id=61
        if (skuLsParams.getCatalog3Id() != null && skuLsParams.getCatalog3Id().length() > 0) {
            if (urlParam.length() > 0) {
                urlParam += "&";
            }
            urlParam += "catalog3Id=" + skuLsParams.getCatalog3Id();
        }
        // 平台属性值Id
        // href="list.html?keyword=?&valueId=?"
        //list.html?catalog3Id=61&valueId120
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
            for (Object valueId : skuLsParams.getValueId()) {

                if(excludeValueIds!=null && excludeValueIds.length>0){
                    String excludeValueId = excludeValueIds[0];
                    if(excludeValueIds.equals(valueId)){
                      continue;
                    }
                }
                if (urlParam.length() > 0) {
                    urlParam += "&";
                }
                urlParam += "valueId=" + valueId;
            }
        }

        return urlParam;
    }
}
