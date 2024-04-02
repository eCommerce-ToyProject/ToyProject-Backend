package com.idrsys.toyprojectbackend.repository.delivery;

import com.idrsys.toyprojectbackend.entity.Delivery;
import com.idrsys.toyprojectbackend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    Delivery findByDlivPlcAndMemberAndZipCodeAndDetailAddressAndDesignation(String dlivPlc, Member member, String zipCode, String detailAddress, String designation);

//    Delivery findByDelPlcAndMember(String delPlc, Member member);

}
