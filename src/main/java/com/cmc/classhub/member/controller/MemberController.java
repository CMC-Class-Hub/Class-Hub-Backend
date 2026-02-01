package com.cmc.classhub.member.controller;

import com.cmc.classhub.member.dto.CreateMemberRequest;
import com.cmc.classhub.member.dto.MemberResponseDto;
import com.cmc.classhub.member.dto.UpdateMemberRequest;
import com.cmc.classhub.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/students")
public class MemberController {

    private final MemberService MemberService;

    @GetMapping
    public List<MemberResponseDto> getAll() {
        return MemberService.getAll();
    }

    @GetMapping("/{id}")
    public MemberResponseDto getById(@PathVariable Long id) {
        return MemberService.getById(id);
    }

    @PostMapping
    public MemberResponseDto create(
            @RequestBody CreateMemberRequest request
    ) {
        return MemberService.create(request);
    }

    @PatchMapping("/{id}")
    public MemberResponseDto update(
            @PathVariable Long id,
            @RequestBody UpdateMemberRequest request
    ) {
        return MemberService.update(id, request);
    }
}
