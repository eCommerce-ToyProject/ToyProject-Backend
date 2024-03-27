package com.idrsys.toyprojectbackend.controller;

import com.idrsys.toyprojectbackend.dto.AddDeliveryDto;
import com.idrsys.toyprojectbackend.dto.DeliveryDto;
import com.idrsys.toyprojectbackend.dto.UpdateDeliveryDto;
import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.repository.DeliveryRepositoryCustom;
import com.idrsys.toyprojectbackend.repository.MemberRepository;
import com.idrsys.toyprojectbackend.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {

    @Autowired
    private DeliveryRepositoryCustom deliveryRepositoryCustom;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DeliveryService deliveryService;

    @GetMapping("/deliveryList")
    public Page<DeliveryDto> delivery(@RequestParam(name = "id", required = false) String id,
                                   Pageable pageable) {
        return deliveryRepositoryCustom.deliverySearch(id, pageable);
    }

    @PostMapping("/createDelivery")
    public boolean createDelivery(@RequestBody AddDeliveryDto addDeliveryDto){
        return deliveryService.createDelivery(addDeliveryDto);
    }

    @PostMapping("/updateDelivery")
    public boolean updateDelivery(@RequestBody UpdateDeliveryDto updateDeliveryDto){
        return deliveryService.updateDelivery(updateDeliveryDto);
    }

}
