package com.college.wallet.config;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {
    public final String EXCHANGE="notificationExchange" ;
    public final String QUEUE="smsQueue";
   public final String ROUTING_KEY="routing.key";
   @Bean
    public DirectExchange exchange(){
        return new DirectExchange(EXCHANGE);
    }

   @Bean
   public Queue queue(){
    return new Queue(QUEUE,true);
   }
   @Bean
   public Binding binding(Queue queue,DirectExchange exchange){
    return  BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
   }
   @Bean
   public MessageConverter jsonMessageConvertor(){
    return new Jackson2JsonMessageConverter();
   }
}
