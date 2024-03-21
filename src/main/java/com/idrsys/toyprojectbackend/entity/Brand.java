package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "brand")
public class Brand {

    @Id
    @Column(name = "brand_no")
    private Long no;

    @Column(name = "brand_nm", nullable = false)
    private String bName;

}
