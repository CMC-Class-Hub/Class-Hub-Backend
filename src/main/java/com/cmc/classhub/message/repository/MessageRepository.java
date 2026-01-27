package com.cmc.classhub.message.repository;

import com.cmc.classhub.message.domain.Message;
import com.cmc.classhub.message.domain.MessageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * due(PENDING + scheduledAt <= now) 요청들을 배치로 가져오기
     */
    @Query("select m from Message m " +
           "where m.status = :status and m.scheduledAt <= :now " +
           "order by m.scheduledAt asc")
    List<Message> findDue(
            @Param("status") MessageStatus status,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    /**
     * 여러 서버/워커가 떠도 중복 처리 방지하려고,
     * PENDING인 경우에만 SENDING으로 바꾸는 원자적 업데이트
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Message m set m.status = :toStatus " +
           "where m.id = :id and m.status = :fromStatus")
    int updateStatusIfMatched(
            @Param("id") Long id,
            @Param("fromStatus") MessageStatus fromStatus,
            @Param("toStatus") MessageStatus toStatus
    );
}
