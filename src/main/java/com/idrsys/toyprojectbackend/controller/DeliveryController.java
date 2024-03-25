package com.idrsys.toyprojectbackend.controller;

import com.idrsys.toyprojectbackend.dto.DeliveryDto;
import com.idrsys.toyprojectbackend.repository.DeliveryRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DeliveryController {

    @Autowired
    private DeliveryRepositoryCustom deliveryRepositoryCustom;

    @GetMapping("/deliveryList")
    public Page<DeliveryDto> delivery(@RequestParam(name = "id", required = false) Long id,
                                      Pageable pageable) {

        return deliveryRepositoryCustom.deliverySearch(id, pageable);
    }

}
