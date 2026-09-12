package com.college.wallet.service;
import com.college.wallet.dto.NotificationMessageResponse;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageService {
      @RabbitListener( queues = "smsQueue")
    public void pushSms(NotificationMessageResponse message ){
        System.out.println(message.Amount()+" is sent by "+message.SenderPhoneNumber()+" to "+message.ReceiverPhoneNumber());
    }
}
