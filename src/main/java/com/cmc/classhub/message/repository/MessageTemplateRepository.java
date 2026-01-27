package com.cmc.classhub.message.repository;

import com.cmc.classhub.message.domain.MessageTemplate;
import com.cmc.classhub.message.domain.MessageTemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long> {

    Optional<MessageTemplate> findByType(MessageTemplateType type);
}
