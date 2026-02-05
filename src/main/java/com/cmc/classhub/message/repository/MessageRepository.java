package com.cmc.classhub.message.repository;

import com.cmc.classhub.message.domain.Message;
import com.cmc.classhub.message.domain.MessageStatus;
import com.cmc.classhub.message.domain.MessageTemplateType;
import com.cmc.classhub.message.domain.DomainType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Solapi 메시지 ID로 조회
     */
    Optional<Message> findByProviderMessageId(String providerMessageId);

    /**
     * 중복 발송 방지: 같은 테이블타입, 같은 rid, 같은 템플릿으로 오늘 이미 발송했는지 확인
     */
    boolean existsByDomainTypeAndRidAndTemplateTypeAndStatusIn(
            DomainType domainType,
            Long rid,
            MessageTemplateType templateType,
            List<MessageStatus> statuses);
}
