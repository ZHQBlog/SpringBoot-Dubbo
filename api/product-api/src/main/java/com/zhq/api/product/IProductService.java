package com.zhq.api.product;

import com.github.pagehelper.PageInfo;
import com.zhq.api.vo.ProductVO;
import com.zhq.common.base.BaseService;
import com.zhq.entity.Product;

/**
 * Product类的接口，用做Dubbo中调用生产者
 */
public interface IProductService extends BaseService<Product> {
    PageInfo<Product> page(Integer pageIndex, Integer pageSize);
    Long add(ProductVO productVO);
    void update(ProductVO productVO);
    void deleteAll(Long[] productId);
    void delete(Long pId);
}
