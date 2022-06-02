package com.zhq.api.vo;

import com.zhq.entity.Product;

import java.io.Serializable;
import java.util.Date;

/**
 * 显示购物车信息类
 */
public class CartItemVO implements Serializable {
    private Product product;
    private Integer count;
    private Date updateTime;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
