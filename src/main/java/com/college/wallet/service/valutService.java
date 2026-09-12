package com.college.wallet.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.college.wallet.exception.BusinessException;
import com.college.wallet.model.User;
import com.college.wallet.model.valuts;
import com.college.wallet.repository.UserRepository;
import com.college.wallet.repository.ValutRepository;
@Service
public  class valutService {
    @Autowired
    private ValutRepository valutRepository;
    @Autowired
    private UserRepository userRepository;
    public RestTemplate restTemplate=new RestTemplate();
    public void adddrip(valuts valut){
         String parentphonenumber=SecurityContextHolder.getContext().getAuthentication().getName();
         valut.setParent_phone_number(parentphonenumber);
      User user1=userRepository.findByPhoneNumber(parentphonenumber).orElseThrow(()->new BusinessException(null, null));
       if(user1==null){
        throw new BusinessException("No such parent phone number present",HttpStatus.NOT_FOUND);
       }
       User user2=userRepository.findByPhoneNumber(valut.getStudent_phone_number()).orElseThrow(()->new BusinessException(null, null));
       if(user2==null){
        throw new BusinessException("No such parent phone number present",HttpStatus.NOT_FOUND);
       }
        valutRepository.save(valut);
        String url="http://host.docker.internal:3000/api/adddrip/valut";
        restTemplate.postForObject(url,valut,String.class);
    }
}
