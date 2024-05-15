package com.unity.goods.domain.point.service;

import static com.unity.goods.global.exception.ErrorCode.USER_NOT_FOUND;

import com.unity.goods.domain.member.entity.Member;
import com.unity.goods.domain.member.exception.MemberException;
import com.unity.goods.domain.member.repository.MemberRepository;
import com.unity.goods.domain.point.dto.PointChargeDto.PointChargeRequest;
import com.unity.goods.domain.point.dto.PointChargeDto.PointChargeResponse;
import com.unity.goods.domain.point.entity.Point;
import com.unity.goods.domain.point.repository.PointRepository;
import com.unity.goods.domain.point.type.PaymentStatus;
import com.unity.goods.global.jwt.UserDetailsImpl;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

  private final MemberRepository memberRepository;
  private final PointRepository pointRepository;

  public PointChargeResponse chargePoint(UserDetailsImpl member,
      PointChargeRequest pointChargeRequest) {

    Member findMember = memberRepository.findByEmail(member.getUsername())
        .orElseThrow(() -> new MemberException(USER_NOT_FOUND));

    // 이전에 포인트 충전을 한 적이 있는 사람인지 확인
    Optional<Point> optionalPoint = pointRepository.findByMember(findMember);
    Point chargePoint;
    if (optionalPoint.isPresent()) {
      chargePoint = optionalPoint.get();
      chargePoint.setBalance(
          chargePoint.getBalance() + Long.parseLong(pointChargeRequest.getPrice()));
    } else {
      chargePoint = Point.builder()
          .member(findMember)
          .balance(Long.parseLong(pointChargeRequest.getPrice()))
          .build();
    }

    pointRepository.save(chargePoint);
    return PointChargeResponse.builder().paymentStatus(String.valueOf(PaymentStatus.SUCCESS))
        .build();
  }
}
