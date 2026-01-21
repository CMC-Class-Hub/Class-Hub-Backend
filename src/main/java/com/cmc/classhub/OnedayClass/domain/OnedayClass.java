package com.cmc.classhub.OnedayClass.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "oneday_classes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OnedayClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long instructorId;

    @Column(nullable = false)
    private String title;

    private String description; // 수업 소개

    private String location; // 수업 장소

    private Integer price; // 정가

    private Integer deposit; // 보증금

    private String material; // 준비물/재료

    private String policy; // 규정 (취소/노쇼 규정)

    private String shareCode; // URL 슬러그로 사용할 고유 코드

    private boolean isDeleted = false; // 삭제 플래그

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnedayClassStatus status; // 모집중 / 마감 / 종료 / 삭제

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "oneday_class_id")
    private List<Session> sessions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public OnedayClass(Long instructorId, String title, String description,
                       String location, Integer price, Integer deposit,
                       String material, String policy) {
        this.instructorId = instructorId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.price = price;
        this.deposit = deposit;
        this.material = material;
        this.policy = policy;
        this.status = OnedayClassStatus.RECRUITING;
        this.createdAt = LocalDateTime.now();
        this.shareCode = generateInitialShareCode();
    }

    // 모집 시작
    public void openOnedayClass() {
        validateNotDeleted();
        this.status = OnedayClassStatus.RECRUITING;
    }

    // 모집 마감
    public void closeOnedayClass() {
        validateNotDeleted();
        this.status.validateClosable();
        this.status = OnedayClassStatus.CLOSED;
    }

    // 세션 추가
    public void addSession(Session session) {
        this.status.validateAddable();
        this.sessions.add(session);
    }

    // 세션 수정
    public void updateSession(Long sessionId, Session updatedSession) {
        Session target = this.sessions.stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 세션이 존재하지 않습니다."));
        target.update(updatedSession);
    }

    public void delete() {
        this.isDeleted = true;
    }

    private void validateNotDeleted() {
        if (this.isDeleted) {
            throw new IllegalStateException("삭제된 클래스입니다.");
        }
    }

    private String generateInitialShareCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }


}
