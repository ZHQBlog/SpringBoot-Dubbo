package com.zhq.handler;

import com.zhq.api.search.ISearchService;
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
    private ISearchService searchService;

    @RabbitListener(queues = RabbitMQConstant.BACKGROUD_PRODUCT_UPDATE_QUEUE)
    public void processAddOrUpdate(Long productId){
        searchService.updateById(productId);
    }

    @RabbitListener(queues = RabbitMQConstant.BACKGROUD_PRODUCT_DELETE_QUEUE)
    public void processDelete(Long productId){
        searchService.deleteById(productId);
    }
}
