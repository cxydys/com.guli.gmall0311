package com.atguigu.gmall.cart.serviceImpl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.RedisUtil;
import com.atguigu.bean.CartInfo;
import com.atguigu.bean.SkuInfo;
import com.atguigu.gmall.Service.Service.CartService;
import com.atguigu.gmall.Service.Service.ManageService;
import com.atguigu.gmall.cart.config.CartConst;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartInfoMapper cartInfoMapper;

    @Reference
    private ManageService manageService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 登录时添加购物车
     *
     * @param skuId
     * @param userId
     * @param skuNum
     */
    @Override
    public void addCart(String skuId, String userId, Integer skuNum) {
        //1.判断购物车中是否有该商品
        //2.没有的话，直接添加，没有的话，和之前的商品相加
        //3.将商品放入到redis中

        //引入redis
        Jedis jedis = redisUtil.getJedis();
        //定义key
        String catKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        //用哪种数据类型
        CartInfo cartInfo = new CartInfo();
        cartInfo.setSkuId(skuId);
        cartInfo.setSkuNum(skuNum);
        CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);
        if (cartInfoExist != null) {
            //数量相加
            cartInfoExist.setSkuNum(cartInfo.getSkuNum() + skuNum);
            //给价格初始化
            cartInfoExist.setCartPrice(cartInfo.getCartPrice());
            //更新数据库
            cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
            //将数据放入到redis中
            // jedis.hset(catKey, userId, JSON.toJSONString(cartInfoExist));

        } else {
            //直接添加
            SkuInfo skuInfo = manageService.getSkuInfo(skuId);
            //将商品添加到购物车里面
            CartInfo cartInfo1 = new CartInfo();
            cartInfo1.setSkuId(skuId);
            cartInfo1.setCartPrice(skuInfo.getPrice());
            cartInfo1.setSkuPrice(skuInfo.getPrice());
            cartInfo1.setSkuName(skuInfo.getSkuName());
            cartInfo1.setImgUrl(skuInfo.getSkuDefaultImg());
            cartInfo1.setUserId(userId);
            cartInfo1.setSkuNum(skuNum);
            //再添加到数据库中
            cartInfoMapper.insertSelective(cartInfo1);
            cartInfoExist = cartInfo1;
        }
        //再放入到redis中
        jedis.hset(catKey, skuId, JSON.toJSONString(cartInfoExist));
        //得到用户的key,
        String userKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        //查看用户还有多长时间过期
        Long ttl = jedis.ttl(userKey);
        //将用户的过期时间赋值给购物车
        jedis.expire(catKey, ttl.intValue());
        //关闭jedis
        jedis.close();

    }

    /**
     * 根据用户ID查询购物车数据
     *
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> getCartList(String userId) {

        List<CartInfo> cartInfoList = new ArrayList<>();
        //获取redis
        // 定义key user:userId:cart
        //从redis中获取数据
        //如果redis中没有数据，再从数据库中获取数据(查询购物车中的实时价格)

        //1.获取redis
        Jedis jedis = redisUtil.getJedis();
        //2.定义key
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        List<String> cartList = jedis.hvals(cartKey);
        if (cartList != null && cartList.size() > 0) {
            for (String cartJson : cartList) {
                //cartJson 转换为对象
                CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
                // 添加购物车数据
                cartInfoList.add(cartInfo);
            }
            // 查询的时候，按照更新的时间倒序！
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });
            return cartInfoList;
        } else {
            //从数据库中去取数据
            cartInfoList = loadCartCache(userId);
            return cartInfoList;
        }
    }

    /**
     * 合并购物车
     * @param cartListOK
     * @param userId
     * @return
     */
    @Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListOK, String userId) {
        // 获取数据库中的数据
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCurPrice(userId);
        // 合并条件 skuId 相同的时候合并
        for (CartInfo cartInfoCK : cartListOK) {
            // 声明一个boolean 类型遍历
            boolean isMatch = false;
            // 有相同的数据直接更新到数据
            for (CartInfo cartInfoDB : cartInfoListDB) {
                if (cartInfoCK.getSkuId().equals(cartInfoDB.getSkuId())){
                    // 数量相加
                    cartInfoDB.setSkuNum(cartInfoCK.getSkuNum()+cartInfoDB.getSkuNum());
                    // 更新
                    cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);

                    isMatch  = true;
                }
            }
            // 未登录的数据在数据库中没有，则直接插入数据库
            if (!isMatch){
                // 未登录的时候的userId 为null
                cartInfoCK.setUserId(userId);
                cartInfoMapper.insertSelective(cartInfoCK);
            }
        }
        // 最后再查询一次更新之后，新添加的所有数据
        List<CartInfo> cartInfoList = loadCartCache(userId);
        return cartInfoList;
    }

    /**
     * 从数据库中去取数据
     *
     * @param userId
     * @return
     */
    private List<CartInfo> loadCartCache(String userId) {
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if (cartInfoList == null || cartInfoList.size() == 0) {
            return null;
        }
        //获取redis
        Jedis jedis = redisUtil.getJedis();
        //定义key
        String cartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        //将从数据库查到的数据放入到redis中
        HashMap<String, String> map = new HashMap<>();
        for (CartInfo cartInfo : cartInfoList) {
            map.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfoList));
        }
        jedis.hmset(cartKey, map);
        return cartInfoList;
    }
}
