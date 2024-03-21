package com.idrsys.toyprojectbackend.controller;

import com.idrsys.toyprojectbackend.dto.GoodsSearchDto;
import com.idrsys.toyprojectbackend.entity.Goods;
import com.idrsys.toyprojectbackend.repository.GoodsRepository;
import com.idrsys.toyprojectbackend.repository.GoodsRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goods")
public class GoodsContoller {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsRepositoryCustom goodsRepositoryCustom;

    @GetMapping("/goodsList")
//    @Parameter(name = "")
    public Page<GoodsSearchDto> goodsList(@RequestParam(required = false, defaultValue = "") String values,
                                 Pageable pageable) {

        return goodsRepositoryCustom.goodsSearch(values, pageable);
    }
}
