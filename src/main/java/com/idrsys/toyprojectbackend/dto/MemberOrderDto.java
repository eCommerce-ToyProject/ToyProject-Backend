package com.idrsys.toyprojectbackend.dto;

import com.idrsys.toyprojectbackend.entity.Delivery;
import lombok.*;

import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberOrderDto {

    private Long no;
    private String id;
    private String username;
    private String email;
    private String phone;
    private List<Delivery> deliveryList;
}
