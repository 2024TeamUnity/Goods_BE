package com.unity.goods.domain.email.service;

import static com.unity.goods.global.exception.ErrorCode.EMAIL_SEND_ERROR;

import com.unity.goods.domain.email.dto.EmailVerificationDto.EmailVerificationRequest;
import com.unity.goods.domain.email.exception.EmailException;
import com.unity.goods.domain.email.type.EmailSubjects;
import com.unity.goods.global.service.RedisService;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

  private final static Long VERIFICATION_EXPIRED_AT = 60 * 3 * 1000L; // 3분
  private final static String FROM = "Goods";

  private final RedisService redisService;
  private final MailSender mailSender;

  public void sendVerificationEmail(
      EmailVerificationRequest emailVerificationRequest) {

    String verificationNumber = createVerificationNumber();

    SimpleMailMessage verificationEmail = createVerificationEmail(
        emailVerificationRequest.getEmail(), verificationNumber);

    try {
      mailSender.send(verificationEmail);
      redisService.setDataExpire(emailVerificationRequest.getEmail(), verificationNumber,
          VERIFICATION_EXPIRED_AT);
      log.info("[sendVerificationEmail] : 이메일 전송 완료. 인증 번호 redis 저장");

    } catch (RuntimeException e) {
      log.debug("[sendVerificationEmail] : 이메일 전송 과정 중 에러 발생");
      throw new EmailException(EMAIL_SEND_ERROR);

    }

  }

  public String createVerificationNumber() {

    SecureRandom random = new SecureRandom();
    int randomNumber = random.nextInt(900000) + 100000;
    log.info("[createVerificationNumber] : 인증 번호 생성 완료");

    return String.valueOf(randomNumber);
  }

  public SimpleMailMessage createVerificationEmail(String emailAddress, String verificationNumber) {

    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(FROM);
    message.setTo(emailAddress);
    message.setSubject(EmailSubjects.SEND_VERIFICATION_CODE.getTitle());
    message.setText(
        "안녕하세요. 중고거래 마켓 " + FROM + "입니다.\n\n"
            + "인증 번호는 [" + verificationNumber + "] 입니다.\n\n"
            + "인증 번호를 입력하고 인증 완료 버튼을 눌러주세요.");

    log.info("[createVerificationEmail] : 인증 이메일 생성 완료");
    return message;
  }

}
