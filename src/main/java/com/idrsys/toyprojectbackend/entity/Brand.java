package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "brand")
public class Brand {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_no")
    private Long no;

    @Column(name = "brand_nm", nullable = false)
    private String bName;

}
