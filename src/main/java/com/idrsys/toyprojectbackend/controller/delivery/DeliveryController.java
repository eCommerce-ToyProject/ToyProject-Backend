package com.idrsys.toyprojectbackend.controller.delivery;

import com.idrsys.toyprojectbackend.dto.delivery.AddDeliveryDto;
import com.idrsys.toyprojectbackend.dto.delivery.DeliveryDto;
import com.idrsys.toyprojectbackend.dto.delivery.UpdateDeliveryDto;
import com.idrsys.toyprojectbackend.dto.orders.SearchOrderDto;
import com.idrsys.toyprojectbackend.excel.excel.ExcelFile;
import com.idrsys.toyprojectbackend.excel.excel.onesheet.OneSheetExcelFile;
import com.idrsys.toyprojectbackend.excel.upload.UploadExcel;
import com.idrsys.toyprojectbackend.repository.delivery.DeliveryRepositoryCustom;
import com.idrsys.toyprojectbackend.repository.memebr.MemberRepository;
import com.idrsys.toyprojectbackend.service.DeliveryService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    @GetMapping("/deliveryList/Excel")
    public void deliveryListExcel(@RequestParam(name = "id", required = false) String id,
                                               Pageable pageable, HttpServletResponse response) throws IOException {
        List<DeliveryDto> deliveryDtoList =  deliveryRepositoryCustom.deliverySearchList(id, pageable);
        ExcelFile excelFile = new OneSheetExcelFile<>(deliveryDtoList, DeliveryDto.class);
        excelFile.write(response.getOutputStream());
    }

    @PostMapping("/createDelivery")
    public boolean createDelivery(@RequestBody AddDeliveryDto addDeliveryDto){
        return deliveryService.createDelivery(addDeliveryDto);
    }

    @PostMapping("/createDelivery/Excel")
    public void createDeliveryExcel(@RequestBody MultipartFile multipartFile, HttpServletResponse response) throws IOException, ReflectiveOperationException {

        List<AddDeliveryDto> deliveryDtoList = UploadExcel.readExcel(multipartFile, AddDeliveryDto.class);
        deliveryDtoList.forEach(deliveryDto -> {
            deliveryService.createDelivery(deliveryDto);
        });
    }

//    @PostMapping("uri")
//    public void methodName(@RequestBody MultipartFile multipartFile, HttpServletResponse response) throws IOException, ReflectiveOperationException {
//
//        List<DtoClass> deliveryDtoList = UploadExcel.readExcel(multipartFile, DtoClass.class);
//
//        // 세이브 로직
//    }

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
