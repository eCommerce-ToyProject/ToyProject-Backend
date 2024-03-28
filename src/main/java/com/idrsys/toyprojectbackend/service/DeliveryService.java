package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.AddDeliveryDto;
import com.idrsys.toyprojectbackend.dto.DeliveryDto;
import com.idrsys.toyprojectbackend.dto.UpdateDeliveryDto;
import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.repository.DeliveryRepository;
import com.idrsys.toyprojectbackend.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DeliveryService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Transactional
    public boolean createDelivery(AddDeliveryDto addDeliveryDto){

        Member member = memberRepository.findById(addDeliveryDto.getMemberId()).orElseThrow(IllegalAccessError::new);

        Delivery delivery = Delivery.builder()
                .delPlc(addDeliveryDto.getDelPlc())
                .member(member)
                .zipCode(addDeliveryDto.getZipCode())
                .detailAddress(addDeliveryDto.getDetailAddress())
                .designation(addDeliveryDto.getDesignation())
                .build();
        try{
            deliveryRepository.save(delivery);
            return true;
        }catch (DataAccessException e){
            log.info("error : "+e);
            return false;
        }

    }

    @Transactional
    public boolean updateDelivery(UpdateDeliveryDto updateDeliveryDto){

        Delivery delivery = deliveryRepository.findById(updateDeliveryDto.getDelNo()).orElseThrow(IllegalAccessError::new);


        Delivery deliveryList = Delivery.builder()
                .delNo(updateDeliveryDto.getDelNo())
                .delPlc(updateDeliveryDto.getDelPlc())
                .member(delivery.getMember())
                .zipCode(updateDeliveryDto.getZipCode())
                .detailAddress(updateDeliveryDto.getDetailAddress())
                .designation(updateDeliveryDto.getDesignation())
                .build();
        try{
            deliveryRepository.save(deliveryList);
            return true;
        }catch (DataAccessException e){
            log.info("error : "+e);
            return false;
        }

    }


}
