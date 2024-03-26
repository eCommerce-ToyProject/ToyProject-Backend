package com.idrsys.toyprojectbackend.service;

import com.idrsys.toyprojectbackend.entity.GoodsItem;
import com.idrsys.toyprojectbackend.repository.GoodsItemRepository;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//@Builder
@Service
public class GoodsItemService {

    @Autowired
    private GoodsItemRepository goodsItemRepository;

    public Long updateQty(GoodsItem item, Long quantity) {
        Long updatedIQty = item.getIQty() - quantity;
        Long updatedISaveQty = item.getISaveQty();

        if (updatedIQty < 0) {
            updatedISaveQty = item.getISaveQty() + updatedIQty;
            if (updatedISaveQty < 0) {
                throw new RuntimeException("상품 재고가 부족합니다. " + item.getNo());
            }
            updatedIQty = 0L;
        }

        GoodsItem updatedItem = GoodsItem.builder()
                .no(item.getNo())
                .name(item.getName())
                .optVal1(item.getOptVal1())
                .optVal2(item.getOptVal2())
                .iQty(updatedIQty)
                .iAmtAdd(item.getIAmtAdd())
                .iSaveQty(updatedISaveQty)
                .goods(item.getGoods())
                .orderItems(item.getOrderItems())
                .build();

        goodsItemRepository.save(updatedItem);
        return updatedItem.getNo();
    }
}
