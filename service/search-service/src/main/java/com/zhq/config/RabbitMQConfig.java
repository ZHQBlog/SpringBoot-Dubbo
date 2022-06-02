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

    //声明交换机
    @Bean
    public TopicExchange getTopicExchange(){
        return new TopicExchange(RabbitMQConstant.BACKGROUD_EXCHANGE);
    }

    //声明队列（添加商品）
    @Bean
    public Queue getUpdateQueue(){
        return new Queue(RabbitMQConstant.BACKGROUD_PRODUCT_UPDATE_QUEUE);
    }

    //建立绑定关系
    @Bean
    public Binding getUpdateBinding(Queue getUpdateQueue, TopicExchange getTopicExchange){
        return BindingBuilder.bind(getUpdateQueue).to(getTopicExchange).with("product.add");
    }

    //声明队列（删除商品）
    @Bean
    public Queue getDeleteQueue(){
        return new Queue(RabbitMQConstant.BACKGROUD_PRODUCT_DELETE_QUEUE);
    }

    //建立绑定关系
    @Bean
    public Binding getDeleteBinding(Queue getDeleteQueue, TopicExchange getTopicExchange){
        return BindingBuilder.bind(getDeleteQueue).to(getTopicExchange).with("product.delete");
    }
}
