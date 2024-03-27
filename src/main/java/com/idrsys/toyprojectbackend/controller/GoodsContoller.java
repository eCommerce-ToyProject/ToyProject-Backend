package com.idrsys.toyprojectbackend.controller;

import com.idrsys.toyprojectbackend.dto.GoodsDto;
import com.idrsys.toyprojectbackend.dto.GoodsSearchDto;
import com.idrsys.toyprojectbackend.entity.Goods;
import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.repository.GoodsItemRepository;
import com.idrsys.toyprojectbackend.repository.GoodsRepository;
import com.idrsys.toyprojectbackend.repository.GoodsRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/goods")
public class GoodsContoller {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private GoodsRepositoryCustom goodsRepositoryCustom;

    @Autowired
    private GoodsItemRepository goodsItemRepository;

//    @Autowired
//    private GoodsItemRepositoryCustom goodsItemRepositoryCustom;

    @GetMapping("/goodsList")
    public Page<GoodsSearchDto> goodsList(@RequestParam(name = "values",required = false, defaultValue = "") String values,
                                 Pageable pageable) {

        return goodsRepositoryCustom.goodsSearch(values, pageable);
    }
    @GetMapping("/goodsList/goodsDetail")
    public List<GoodsDto> goodsDetail(@RequestParam(name = "no",required = false) Long no) {

        return goodsRepositoryCustom.goodsDetail(no);
    }
}
