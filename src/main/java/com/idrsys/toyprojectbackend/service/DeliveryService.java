package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.delivery.AddDeliveryDto;
import com.idrsys.toyprojectbackend.dto.delivery.UpdateDeliveryDto;
import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.repository.delivery.DeliveryRepository;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
public class DeliveryService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Transactional(rollbackFor = {Exception.class})
    public boolean createDelivery(AddDeliveryDto addDeliveryDto){

        Member member = memberRepository.findById(addDeliveryDto.getMemberId()).orElseThrow(IllegalAccessError::new);

        try{
            deliveryRepository.save(buildDelivery(addDeliveryDto, member));
            return true;
        }catch (DataAccessException e){
            log.info("error : "+e);
            return false;
        }

    }

    @Transactional(rollbackFor = {Exception.class})
    public Delivery createDeliveryWithOrder(AddDeliveryDto addDeliveryDto){

        Member member = memberRepository.findById(addDeliveryDto.getMemberId()).orElseThrow(IllegalAccessError::new);

        try{
            Delivery delivery = deliveryRepository.save(buildDelivery(addDeliveryDto, member));
            return delivery;
        }catch (DataAccessException e){
            log.info("error : "+e);
            return null;
        }

    }

    private Delivery buildDelivery(AddDeliveryDto addDeliveryDto, Member member){
        return  Delivery.builder()
                .dlivPlc(addDeliveryDto.getDlivPlc())
                .member(member)
                .zipCode(addDeliveryDto.getZipCode())
                .detailAddress(addDeliveryDto.getDetailAddress())
                .designation(addDeliveryDto.getDesignation())
                .dlivCreateDate(LocalDateTime.now())
                .deleted(false)
                .build();
    }


    @Transactional(rollbackFor = {Exception.class})
    public boolean updateDelivery(UpdateDeliveryDto updateDeliveryDto){

        Delivery delivery = deliveryRepository.findById(updateDeliveryDto.getDlivNo()).orElseThrow(IllegalAccessError::new);


        Delivery deliveryList = Delivery.builder()
                .dlivNo(updateDeliveryDto.getDlivNo())
                .dlivPlc(updateDeliveryDto.getDlivPlc())
                .member(delivery.getMember())
                .zipCode(updateDeliveryDto.getZipCode())
                .detailAddress(updateDeliveryDto.getDetailAddress())
                .designation(updateDeliveryDto.getDesignation())
                .dlivCreateDate(delivery.getDlivCreateDate())
                .dlivDeletedDt(delivery.getDlivDeletedDt())
                .dlivChangeDt(LocalDateTime.now())
                .deleted(delivery.isDeleted())
                .build();
        try{
            deliveryRepository.save(deliveryList);
            return true;
        }catch (DataAccessException e){
            log.info("error : "+e);
            return false;
        }

    }

    @Transactional(rollbackFor = {Exception.class})
    public boolean deleteDelivery(Long dlivNo){

        Delivery delivery = deliveryRepository.findById(dlivNo).orElseThrow(IllegalAccessError::new);

        Delivery deliveryList = Delivery.builder()
                .dlivNo(delivery.getDlivNo())
                .dlivPlc(delivery.getDlivPlc())
                .member(delivery.getMember())
                .zipCode(delivery.getZipCode())
                .detailAddress(delivery.getDetailAddress())
                .designation(delivery.getDesignation())
                .dlivCreateDate(delivery.getDlivCreateDate())
                .dlivDeletedDt(LocalDateTime.now())
                .dlivChangeDt(delivery.getDlivChangeDt())
                .deleted(true)
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
