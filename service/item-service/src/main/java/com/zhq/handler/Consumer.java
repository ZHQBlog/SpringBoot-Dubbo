package com.zhq.handler;

import com.zhq.api.IitemService;
import com.zhq.common.constant.RabbitMQConstant;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听消息队列中的消息
 */
@Component
public class Consumer {

    @Autowired
    private IitemService itemService;

    //监听消息
    @RabbitListener(queues = RabbitMQConstant.BACKGROUD_PRODUCT_UPDATE_ITEM_QUEUE)
    public void processAddOrUpdate(Long productId){
        itemService.createHTMLById(productId);
    }
}
