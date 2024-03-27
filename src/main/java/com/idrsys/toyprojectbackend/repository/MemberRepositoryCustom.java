package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.MemberOrderDto;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberOrderDto> memberOrdering(String id);
}
