package com.zhq.api.vo;

import com.zhq.entity.Product;
import com.zhq.entity.ProductDesc;

import java.io.Serializable;

/**
 * 拼接product和productDesc
 * 视图层的实体类，用来接收前端传递的数据
 */
public class ProductVO implements Serializable {
    private Product product;
    private ProductDesc productDesc;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductDesc getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(ProductDesc productDesc) {
        this.productDesc = productDesc;
    }
}
