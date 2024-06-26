package com.unity.goods.domain.notification.service;

import static com.unity.goods.domain.notification.type.NotificationType.CHAT_RECEIVED;
import static com.unity.goods.domain.notification.type.NotificationType.POINT_RECEIVED;
import static com.unity.goods.global.exception.ErrorCode.NOTIFICATION_SENDING_ERROR;
import static com.unity.goods.global.exception.ErrorCode.USER_NOT_FOUND;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.unity.goods.domain.member.entity.Member;
import com.unity.goods.domain.member.exception.MemberException;
import com.unity.goods.domain.member.repository.MemberRepository;
import com.unity.goods.domain.notification.dto.FcmTokenDto;
import com.unity.goods.domain.notification.entity.NotificationLog;
import com.unity.goods.domain.notification.exception.FCMException;
import com.unity.goods.domain.notification.repository.NotificationRepository;
import com.unity.goods.global.jwt.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
@Slf4j
public class FcmService {

  private final MemberRepository memberRepository;
  private final NotificationRepository notificationRepository;

  public void registerFcmToken(UserDetailsImpl member, FcmTokenDto fcmTokenDto) {
    Member savedMember = memberRepository.findByEmail(member.getUsername())
        .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

    savedMember.setFcmToken(fcmTokenDto.getFcmToken());

    memberRepository.save(savedMember);
    log.info("[FcmService] : {} fcm 토큰 저장 완료", savedMember.getEmail());
  }

  public void sendNotification(String token, String title, String body) {
    Message message = Message.builder()
        .setToken(token)
        .setNotification(Notification.builder()
            .setTitle(title)
            .setBody(body)
            .build())
        .build();

    try{
      FirebaseMessaging.getInstance().send(message);
      log.info("[FcmService] : {} 메시지 수신 완료", token);
    } catch (Exception e) {
      log.error("[FcmService] : fcm 서버 메시지 요청 과정 중 에러 발생 {}", e.getMessage());
      throw new FCMException(NOTIFICATION_SENDING_ERROR);
    }
  }

  public void sendChatNotification(Long receiverId, String chatMessage) {
    Member member = memberRepository.findById(receiverId)
        .orElseThrow(() -> new MemberException(USER_NOT_FOUND));
    if(member.getFcmToken() == null){
      return;
    }

    NotificationLog notification = NotificationLog.builder()
        .receiverId(receiverId)
        .notificationType(CHAT_RECEIVED)
        .member(member)
        .build();
    notificationRepository.save(notification);

    sendNotification(member.getFcmToken(), CHAT_RECEIVED.getTitle(), chatMessage);
  }

  public void sendPointReceivedNotification(Member receiver) {
    if(receiver.getFcmToken() == null){
      return;
    }

    NotificationLog notification = NotificationLog.builder()
        .receiverId(receiver.getId())
        .notificationType(POINT_RECEIVED)
        .member(receiver)
        .build();
    notificationRepository.save(notification);

    sendNotification(receiver.getFcmToken(), POINT_RECEIVED.getTitle(), POINT_RECEIVED.getBody());
  }
}
