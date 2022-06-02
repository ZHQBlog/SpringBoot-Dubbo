package com.zhq.cartservice.service;

import com.zhq.api.ICartService;
import com.zhq.api.pojo.CartItem;
import com.zhq.api.vo.CartItemVO;
import com.zhq.common.pojo.ResultBean;
import com.zhq.entity.Product;
import com.zhq.mapper.ProductMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 实现购物车的服务
 * 使用redis保存商品信息（购物车信息可以允许部分丢失）
 */
@Service
@DubboService
public class CartService implements ICartService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired(required = false)
    private ProductMapper productMapper;

    @Override
    public ResultBean add(String key, Long productId, Integer count) {
        //根据key查找购物车
        Map<Long, CartItem> map = (Map<Long, CartItem>) redisTemplate.opsForValue().get(key);

        //购物车不存在
        if (map == null){
            map = new HashMap<>();
            map.put(productId, new CartItem(productId, count, new Date()));
            //将购物车的商品保存到redis中
            redisTemplate.opsForValue().set(key, map);
            //刷新有效期
            redisTemplate.expire(key, 30, TimeUnit.DAYS);

            return ResultBean.success(String.valueOf(map.size()));
        }

        //购物车存在，并且商品存在购物车中
        CartItem cartItem = map.get(productId);
        if (cartItem != null){
            cartItem.setCount(cartItem.getCount()+count);
            cartItem.setUpdateTime(new Date());
            //保存到reids中， 刷新有效期
            redisTemplate.opsForValue().set(key, map);
            redisTemplate.expire(key, 30, TimeUnit.DAYS);

            return ResultBean.success(String.valueOf(map.size()));
        }

        //商品不存在购物车中
        map.put(productId, new CartItem(productId, count, new Date()));
        //保存到reids中， 刷新有效期
        redisTemplate.opsForValue().set(key, map);
        redisTemplate.expire(key, 30, TimeUnit.DAYS);

        return ResultBean.success(String.valueOf(map.size()));
    }

    @Override
    public ResultBean updateCount(String key, Long productId, Integer count) {
        //根据key查找购物车
        Map<Long, CartItem> map = (Map<Long, CartItem>) redisTemplate.opsForValue().get(key);

        if (map != null){
            //遍历更新数量
            CartItem cartItem = map.get(productId);
            if (cartItem != null){
                cartItem.setCount(count);
                cartItem.setUpdateTime(new Date());

                //保存到reids中， 刷新有效期
                redisTemplate.opsForValue().set(key, map);
                redisTemplate.expire(key, 30, TimeUnit.DAYS);

                return ResultBean.success(String.valueOf(map.size()));
            }
        }
        return ResultBean.error("更新失败！");
    }

    @Override
    public ResultBean del(String key, Long productId) {
        //根据key查找购物车
        Map<Long, CartItem> map = (Map<Long, CartItem>) redisTemplate.opsForValue().get(key);

        if (map != null){
            CartItem cartItem = map.get(productId);
            if (cartItem != null){
                map.remove(cartItem);

                //保存到reids中， 刷新有效期
                redisTemplate.opsForValue().set(key, map);
                redisTemplate.expire(key, 30, TimeUnit.DAYS);

                return ResultBean.success(String.valueOf(map.size()));
            }
        }
        return ResultBean.error("删除失败！");
    }

    @Override
    public ResultBean list(String key) {
        //根据key查找购物车
        Map<Long, CartItem> map = (Map<Long, CartItem>) redisTemplate.opsForValue().get(key);

        if (map == null){
            return ResultBean.error("当前购物车没有商品！");
        }

        Map<Long, CartItemVO> cartItemHashMap = new HashMap<>(map.size());
        for (Map.Entry<Long, CartItem> entry : map.entrySet()) {
            CartItemVO cartItemVO = new CartItemVO();
            cartItemVO.setCount(entry.getValue().getCount());
            cartItemVO.setUpdateTime(entry.getValue().getUpdateTime());

            //准备好的热门商品添加到redis中，查询时查询redis
            StringBuilder stringBuilder = new StringBuilder("product:").append(entry.getKey());
            Product product = (Product) redisTemplate.opsForValue().get(stringBuilder.toString());
            //不是热门商品，reids中不存在，查询数据库
            if (product == null){
                product = productMapper.selectByPrimaryKey(entry.getKey());
                redisTemplate.opsForValue().set(stringBuilder.toString(), product);
                redisTemplate.expire(stringBuilder.toString(), 60, TimeUnit.MINUTES);
            }
            cartItemVO.setProduct(product);
            cartItemHashMap.put(cartItemVO.getProduct().getId(), cartItemVO);
        }

        //按照时间排序
        List<Map.Entry<Long, CartItemVO>> list = new ArrayList<>(cartItemHashMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Long, CartItemVO>>() {
            @Override
            public int compare(Map.Entry<Long, CartItemVO> o1, Map.Entry<Long, CartItemVO> o2) {
                return (int) (o2.getValue().getUpdateTime().getTime()-o1.getValue().getUpdateTime().getTime());
            }
        });

        return new ResultBean(200, list);
    }

    //合并购物车
    @Override
    public ResultBean merge(String noLoginKey, String loginKey) {
        //根据key查找购物车
        Map<Long, CartItem> noLoginCart = (Map<Long, CartItem>) redisTemplate.opsForValue().get(noLoginKey);

        if (noLoginCart == null){
            return ResultBean.error("不存在未登录的购物车！");
        }

        //获取登录的购物车
        Map<Long, CartItem> loginCart = (Map<Long, CartItem>) redisTemplate.opsForValue().get(loginKey);
        if (loginCart == null){
            //不存在已经登录的购物车，直接替换成未登录的购物车
            redisTemplate.opsForValue().set(loginKey, noLoginCart);
            redisTemplate.expire(loginKey, 30, TimeUnit.DAYS);

            return ResultBean.success(String.valueOf(noLoginCart.size()));
        }
        //两者都存在，合并两个购物车
        for (Map.Entry<Long, CartItem> entry : noLoginCart.entrySet()) {
            if (loginCart.get(entry.getKey()) == null){
                loginCart.put(entry.getKey(), entry.getValue());
            }else {
                CartItem logiCartItem = loginCart.get(entry.getKey());
                CartItem noLoginCartItem = noLoginCart.get(entry.getKey());
                logiCartItem.setCount(logiCartItem.getCount()+noLoginCartItem.getCount());
                loginCart.put(entry.getKey(), logiCartItem);
            }
        }
        //写回
        redisTemplate.opsForValue().set(loginKey, loginCart);
        redisTemplate.expire(loginKey, 30, TimeUnit.DAYS);
        redisTemplate.delete(noLoginKey);

        return ResultBean.success(String.valueOf(loginCart.size()));
    }
}
