package com.zhq.config;

import com.zhq.common.constant.RabbitMQConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置消息队列
 */
@Configuration
public class RabbitMQConfig {
    //声明队列
    @Bean
    public Queue getQueue(){
        return new Queue(RabbitMQConstant.BACKGROUD_PRODUCT_UPDATE_ITEM_QUEUE);
    }

    //声明交换机
    @Bean
    public TopicExchange getTopicExchange(){
        return new TopicExchange(RabbitMQConstant.BACKGROUD_EXCHANGE);
    }

    //建立绑定关系，自动注入参数
    @Bean
    public Binding getBinding(Queue getQueue, TopicExchange getTopicExchange){
        return BindingBuilder.bind(getQueue).to(getTopicExchange).with("product.add");
    }
}
