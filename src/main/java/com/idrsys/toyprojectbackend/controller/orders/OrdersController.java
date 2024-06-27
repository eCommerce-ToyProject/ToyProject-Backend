package com.idrsys.toyprojectbackend.controller.orders;

import com.idrsys.toyprojectbackend.dto.orders.AddOrdersDto;
import com.idrsys.toyprojectbackend.dto.orders.SearchOrderDto;
import com.idrsys.toyprojectbackend.excel.excel.ExcelFile;
import com.idrsys.toyprojectbackend.excel.excel.onesheet.OneSheetExcelFile;
import com.idrsys.toyprojectbackend.excel.excel.onesheet.OneSheetExcelFileSwitchRC;
import com.idrsys.toyprojectbackend.excel.utils.DataFormatterUtil;
import com.idrsys.toyprojectbackend.repository.orders.OrdersRepositoryCustom;
import com.idrsys.toyprojectbackend.service.OrderFacade;
import com.idrsys.toyprojectbackend.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private OrdersRepositoryCustom ordersRepositoryCustom;

    @PostMapping("/createOrder")
    public boolean addOrder(@RequestBody AddOrdersDto addOrdersDto){
        if(addOrdersDto.getOptVal2().isBlank()){
            addOrdersDto.setOptVal2(null);
        }

        if(addOrdersDto.getOptVal1().isBlank()) {
            addOrdersDto.setOptVal1(null);
        }
        return orderService.createOrder(addOrdersDto);
    }
    @GetMapping("/myOrderList")
    public Page<SearchOrderDto> myOrder(@RequestParam(name = "id",required = false) String id, Pageable pageable){
        return ordersRepositoryCustom.ordersList(id,pageable);
    }

    @GetMapping("/myOrderTest")
    public Page<SearchOrderDto> myOrderTest(@RequestParam(name = "id",required = false) String id, Pageable pageable){
        return ordersRepositoryCustom.ordersPage(id,pageable);
    }

    @GetMapping("/myOrder/Excel")
    public void myOrderExcel(@RequestParam(name = "id",required = false) String id, Pageable pageable, HttpServletResponse response) throws IOException {
        List<SearchOrderDto> orderList = ordersRepositoryCustom.orderList(id, pageable);
        DataFormatterUtil.scanAndRegisterMappings(SearchOrderDto.class);
        ExcelFile excelFile = new OneSheetExcelFileSwitchRC(orderList, SearchOrderDto.class);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + "테스트" + ".xlsx");
        excelFile.write(response.getOutputStream());
    }

}
