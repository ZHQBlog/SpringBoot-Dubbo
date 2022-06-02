package com.zhq.mapper;

import com.zhq.common.base.IBaseDao;
import com.zhq.entity.ProductDesc;

public interface ProductDescMapper extends IBaseDao<ProductDesc> {
    ProductDesc selectByProductId(Long productId);
}