package com.idrsys.toyprojectbackend.controller.delivery;

import com.idrsys.toyprojectbackend.dto.delivery.AddDeliveryDto;
import com.idrsys.toyprojectbackend.dto.delivery.DeliveryDto;
import com.idrsys.toyprojectbackend.dto.delivery.UpdateDeliveryDto;
import com.idrsys.toyprojectbackend.repository.delivery.DeliveryRepositoryCustom;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import com.idrsys.toyprojectbackend.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/updateDelivery")
    public boolean updateDelivery(@RequestBody UpdateDeliveryDto updateDeliveryDto){
        return deliveryService.updateDelivery(updateDeliveryDto);
    }
    @PutMapping("/deleteDelivery/{no}")
    public boolean deleteDelivery(
//            @RequestParam(name = "id", required = false) Long delNo
            @PathVariable Long no
    ){
        return deliveryService.deleteDelivery(no);
    }

}
