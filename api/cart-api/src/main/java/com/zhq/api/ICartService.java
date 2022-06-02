package com.zhq.api;

import com.zhq.common.pojo.ResultBean;

/**
 * 购物车接口
 */
public interface ICartService {
    ResultBean add(String key, Long productId, Integer count);
    ResultBean updateCount(String key, Long productId, Integer count);
    ResultBean del(String key, Long productId);
    ResultBean list(String key);

    //合并购物车
    ResultBean merge(String noLoginKey, String loginKey);
}
