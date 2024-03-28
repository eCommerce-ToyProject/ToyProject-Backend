package com.idrsys.toyprojectbackend.repository;

import com.idrsys.toyprojectbackend.dto.MemberDto;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberDto> memberOrdering(String id);
}
