package com.idrsys.toyprojectbackend.repository.memebr;

import com.idrsys.toyprojectbackend.dto.member.MemberDto;

import java.util.List;

public interface MemberRepositoryCustom {

    List<MemberDto> memberOrdering(String id);
}
