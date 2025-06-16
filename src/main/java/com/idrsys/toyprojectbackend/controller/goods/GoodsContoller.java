package com.idrsys.toyprojectbackend.controller.goods;

import com.idrsys.toyprojectbackend.dto.goods.GoodsDto;
import com.idrsys.toyprojectbackend.dto.goods.GoodsRequestDto;
import com.idrsys.toyprojectbackend.dto.goods.GoodsSearchDto;
import com.idrsys.toyprojectbackend.excel.upload.UploadExcel;
import com.idrsys.toyprojectbackend.repository.goods.GoodsItemRepository;
import com.idrsys.toyprojectbackend.repository.goods.GoodsRepository;
import com.idrsys.toyprojectbackend.repository.goods.GoodsRepositoryCustom;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Autowired
    private GoodsItemRepository goodsItemRepository;

//    @Autowired
//    private GoodsItemRepositoryCustom goodsItemRepositoryCustom;

    @GetMapping("/goodsList")
    public Page<GoodsSearchDto> goodsList(@RequestParam(name = "values",required = false, defaultValue = "") String values,
                                 @PageableDefault(size = Integer.MAX_VALUE) Pageable pageable) {

        return goodsRepositoryCustom.goodsSearch(values, pageable);
    }
    @GetMapping("/goodsList/goodsDetail")
    public List<GoodsDto> goodsDetail(@RequestParam(name = "no",required = false) Long no) {

        return goodsRepositoryCustom.goodsDetail(no);
    }

    @PostMapping("/addGoods/excel")
    public void addGoods(@RequestBody MultipartFile multipartFile, HttpServletResponse responseo) throws ReflectiveOperationException, IOException {
        List<GoodsRequestDto> goodsReqDto = UploadExcel.readExcel(multipartFile, GoodsRequestDto.class);

        GoodsRequestDto goodsRequestDto = goodsReqDto.getFirst();
//        return ResponseEntity.;
    }

//    @GetMapping("/goodsList/goodsOrder")
//    public List<GoodsItem> goodsOrder(@RequestParam(name = "no",required = false) Long no,
//                                      @RequestParam(name = "optVal1",required = false) String optVal1,
//                                      @RequestParam(name = "optVal2",required = false) String optVal2) {
//
//        Goods goods = goodsRepository.findById(no).orElseThrow(IllegalArgumentException::new);
//
//        return goodsItemRepository.findByOptVal1AndOptVal2AndGoods(optVal1, optVal2, goods);
//    }
}
