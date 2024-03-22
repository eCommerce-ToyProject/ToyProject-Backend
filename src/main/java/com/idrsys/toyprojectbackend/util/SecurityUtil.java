package com.idrsys.toyprojectbackend.util;

import com.idrsys.toyprojectbackend.entity.Member;
import com.idrsys.toyprojectbackend.service.MemberService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtil {


    public static String getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Member member = (Member) authentication.getPrincipal();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("No authentication information.");
        }

        return  authentication.getName();
    }
}
