package com.cmc.classhub.reservation.service;

import com.cmc.classhub.reservation.domain.Member;
import com.cmc.classhub.reservation.dto.CreateMemberRequest;
import com.cmc.classhub.reservation.dto.MemberResponseDto;
import com.cmc.classhub.reservation.dto.UpdateMemberRequest;
import com.cmc.classhub.reservation.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public List<MemberResponseDto> getAll() {
        return memberRepository.findAll()
                .stream()
                .map(MemberResponseDto::new)
                .toList();
    }

    public MemberResponseDto getById(Long id) {
        System.out.println("Students = " +  memberRepository.findById(id) + "id" + id);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다."));
        return new MemberResponseDto(member);
    }

    @Transactional
    public MemberResponseDto create(CreateMemberRequest request) {
        Member member = Member.builder()
                .name(request.getName())
                .password(request.getPassword())
                .phone(request.getPhone())
                .build();

        Member saved = memberRepository.save(member);
        return new MemberResponseDto(saved);
    }

    @Transactional
    public MemberResponseDto update(Long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("학생이 존재하지 않습니다."));

        member.updateReservationInfo(
                request.getName(),
                request.getPhone()
        );

        return new MemberResponseDto(member);
    }
}
