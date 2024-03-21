package com.idrsys.toyprojectbackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
@Entity
@Table(name = "category")
public class Category {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cat_cd")
    private Long cd;

    @Column(name = "cat_nm", nullable = false)
    private String cName;


}
