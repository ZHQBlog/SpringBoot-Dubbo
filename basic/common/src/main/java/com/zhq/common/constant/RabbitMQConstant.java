package com.zhq.common.constant;

/**
 * 设置常量，将重复使用的字符串，整合起来
 */
public interface RabbitMQConstant {
    String BACKGROUD_EXCHANGE = "backgroud-exchange";

    String  BACKGROUD_PRODUCT_UPDATE_QUEUE= "background-product-update-queue";
    String  BACKGROUD_PRODUCT_DELETE_QUEUE= "background-product-delete-queue";

    String  BACKGROUD_PRODUCT_UPDATE_ITEM_QUEUE= "background-product-update-item-queue";
}
