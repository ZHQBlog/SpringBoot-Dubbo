package com.zhq.api.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 购物车信息类
 */
public class CartItem implements Serializable {

    private Long productId;
    private Integer count;
    private Date updateTime;

    public CartItem() {
    }

    public CartItem(Long productId, Integer count, Date updateTime) {
        this.productId = productId;
        this.count = count;
        this.updateTime = updateTime;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
