package com.example.demo.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
	
	public static final String QUEUE_NAME = "myQueue";
    public static final String EXCHANGE_NAME = "myExchange";
    

    public static final String SECOND_QUEUE_NAME = "candleStickQueue";
    public static final String SECOND_EXCHANGE_NAME = "candleStickExchange";
    
    //FIRST QUEUE
    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME, false);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("routing.key.#");
    }
    
    //Second Queues
    @Bean
    public Queue secondQueue() {
        return new Queue(SECOND_QUEUE_NAME, false);
    }
    
    @Bean
    public TopicExchange secondExchange() {
        return new TopicExchange(SECOND_EXCHANGE_NAME);
    }
    
    
    @Bean
    public Binding secondBinding(Queue secondQueue, TopicExchange secondExchange) {
        return BindingBuilder.bind(secondQueue).to(secondExchange).with("routing.key.#");
    }
    
   //JSON CONVERTER
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());  
        return rabbitTemplate;
    }
}
