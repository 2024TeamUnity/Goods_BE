package com.unity.goods.domain.chat.repository;

import com.unity.goods.domain.chat.entity.ChatLog;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {

  List<ChatLog> findAllByChatRoomIdAndCheckedAndReceiverId(
      Long chatRoomId, boolean checked, Long receiverId);

  Page<ChatLog> findByChatRoomIdOrderByCreatedAtDesc(Long chatRoomId, Pageable pageable);

}
