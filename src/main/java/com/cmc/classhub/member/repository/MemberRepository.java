package com.cmc.classhub.member.repository;

import com.cmc.classhub.member.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByPhone(String phone);
    Optional<Member> findByNameAndPhone(String name, String phone);
}
