package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.dto.delivery.AddDeliveryDto;
import com.idrsys.toyprojectbackend.dto.delivery.UpdateDeliveryDto;
import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.repository.delivery.DeliveryRepository;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DeliveryService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Transactional(rollbackFor = {Exception.class})
    public boolean createDelivery(AddDeliveryDto addDeliveryDto){

        Member member = memberRepository.findById(addDeliveryDto.getMemberId()).orElseThrow(IllegalAccessError::new);
        Delivery delivery = existingDelivery(addDeliveryDto, member, "CreateOnlyDelivery");

        if (delivery != null && delivery.isDeleted()) {
            deliveryRepository.save(updateBuildDelivery(delivery));
            return true;
        }else if(delivery != null && !delivery.isDeleted()){
            deliveryRepository.save(updateBuildDelivery(delivery));
            return true;
        }

        try{
            deliveryRepository.save(buildDelivery(addDeliveryDto, member));
            return true;
        }catch (Exception e){
            log.error("Error occurred during createDeliveryWithOrder: {}", e.getMessage());
            throw e;
        }

    }

    @Transactional(rollbackFor = {Exception.class})
    public Delivery createDeliveryWithOrder(AddDeliveryDto addDeliveryDto){

        Member member = memberRepository.findById(addDeliveryDto.getMemberId()).orElseThrow(IllegalAccessError::new);

        Delivery delivery = existingDelivery(addDeliveryDto, member, "createWithOrder");
        if (delivery != null) {
            return deliveryRepository.save(updateBuildDelivery(delivery));
        }

        try{
            return deliveryRepository.save(buildDelivery(addDeliveryDto, member));
        }catch (Exception e){
            log.error("Error occurred during createDeliveryWithOrder: {}", e.getMessage());
            throw e;
        }

    }

    private Delivery existingDelivery(AddDeliveryDto addDeliveryDto, Member member, String Case){
        switch (Case){
            case "CreateOnlyDelivery":
                return deliveryRepository.findByDlivPlcAndMemberAndZipCodeAndDetailAddressAndDesignation(
                        addDeliveryDto.getDlivPlc(), member, addDeliveryDto.getZipCode(),
                        addDeliveryDto.getDetailAddress(), addDeliveryDto.getDesignation());
            case "createWithOrder":
                return deliveryRepository.findByDlivPlcAndMemberAndZipCodeAndDetailAddressAndDesignationAndDeleted(
                        addDeliveryDto.getDlivPlc(), member, addDeliveryDto.getZipCode(),
                        addDeliveryDto.getDetailAddress(), addDeliveryDto.getDesignation(), true);
            default:
                log.info("error: 배송지 데이터가 정상적이지 않습니다." );
                return null;
        }

    }

    private Delivery updateBuildDelivery(Delivery delivery){
        return  Delivery.builder()
                .dlivNo(delivery.getDlivNo())
                .dlivPlc(delivery.getDlivPlc())
                .member(delivery.getMember())
                .zipCode(delivery.getZipCode())
                .detailAddress(delivery.getDetailAddress())
                .designation(delivery.getDesignation())
                .dlivCreateDate(delivery.getDlivCreateDate())
                .dlivDeletedDt(null)
                .dlivChangeDt(LocalDateTime.now())
                .deleted(false)
                .build();
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
                .dlivDeletedDt(null)
                .dlivChangeDt(LocalDateTime.now())
                .deleted(false)
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
