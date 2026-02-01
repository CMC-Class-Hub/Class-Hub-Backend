package com.cmc.classhub.onedayClass.domain;

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

    private String locationDescription; // 위치 안내 (상세 길안내)

    private Integer price; // 정가

    private String material; // 준비물/재료

    private String parkingInfo; // 주차안내

    private String guidelines; // 주의사항

    private String policy; // 규정 (취소/노쇼 규정)

    private String classCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OnedayClassStatus status; // 모집중 / 마감 / 종료 / 삭제

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "oneday_class_id")
    private List<Session> sessions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean isDeleted = false;//삭제 여부

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "oneday_class_id")
    private List<CLASS_IMAGE> images = new ArrayList<>();

    public void delete() {
        this.isDeleted = true;
    }

    public void restore() {
        this.isDeleted = false;
    }

    public boolean isDeleted() {
        return this.isDeleted;
    }

    @Builder
    public OnedayClass(Long instructorId, String title, String description,
            String location, String locationDescription, Integer price,
            String material, String parkingInfo, String guidelines, String policy, String classCode) {
        this.instructorId = instructorId;
        this.title = title;
        this.description = description;
         this.location = location;
        this.locationDescription = locationDescription;
        this.price = price;
        this.material = material;
        this.parkingInfo = parkingInfo;
        this.guidelines = guidelines;
        this.policy = policy;
        this.status = OnedayClassStatus.RECRUITING;
        this.createdAt = LocalDateTime.now();
        this.classCode = (classCode != null && !classCode.isEmpty()) ? classCode : generateInitialClassCode();
    }
    public void updateImages(List<String> imageUrls) {
        this.images.clear();
        if (imageUrls == null) return;
        imageUrls.forEach(url -> this.images.add(CLASS_IMAGE.of(url)));
    }

    public void reserveSession(Long sessionId) {
        Session session = this.sessions.stream()
                .filter(s -> s.getId().equals(sessionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 클래스에 속한 세션이 아닙니다."));

        // 세션 상태 및 인원 검증 후 증가
        session.join();
    }

    // 모집 시작
    public void openOnedayClass() {
        if (this.status == OnedayClassStatus.DELETED) {
            throw new IllegalStateException("삭제된 클래스는 공개할 수 없습니다.");
        }
        this.status = OnedayClassStatus.RECRUITING;
    }

    // 모집 마감
    public void closeOnedayClass() {
        if (this.status != OnedayClassStatus.RECRUITING && this.status != OnedayClassStatus.FULL) {
            throw new IllegalStateException("모집중인 클래스만 마감할 수 있습니다.");
        }
        this.status = OnedayClassStatus.CLOSED;
    }

    public void update(String title, String description, String location, String locationDescription,
            Integer price, String material, String parkingInfo, String guidelines, String policy) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.locationDescription = locationDescription;
        this.price = price;
        this.material = material;
        this.parkingInfo = parkingInfo;
        this.guidelines = guidelines;
        this.policy = policy;
    }

    public void clearSessions() {
        this.sessions.clear();
    }

    // 세션 추가
    public void addSession(Session session) {
        this.sessions.add(session);
    }


    private String generateInitialClassCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

}
