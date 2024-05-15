package com.unity.goods.domain.point.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.verify;

import com.unity.goods.domain.member.entity.Member;
import com.unity.goods.domain.member.repository.MemberRepository;
import com.unity.goods.domain.point.dto.PointChargeDto.PointChargeRequest;
import com.unity.goods.domain.point.dto.PointChargeDto.PointChargeResponse;
import com.unity.goods.domain.point.entity.Point;
import com.unity.goods.domain.point.repository.PointRepository;
import com.unity.goods.domain.point.type.PaymentStatus;
import com.unity.goods.global.jwt.UserDetailsImpl;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

  @InjectMocks
  private PointService pointService;

  @Mock
  private MemberRepository memberRepository;

  @Mock
  private PointRepository pointRepository;

  @Test
  @DisplayName("기존 충전했던 사람 포인트 충전")
  void chargePointTest() {
    // given
    Member member = Member.builder()
        .id(1L)
        .email("test@email.com")
        .build();

    Point point = Point.builder()
        .member(member)
        .balance(1000L)
        .build();

    UserDetailsImpl userDetails = new UserDetailsImpl(member);

    given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));
    given(pointRepository.findByMember(any(Member.class))).willReturn(Optional.of(point));

    // when
    PointChargeResponse pointChargeResponse = pointService.chargePoint(userDetails,
        PointChargeRequest.builder().price("5000").build());

    // then
    assertEquals(PaymentStatus.SUCCESS.toString(), pointChargeResponse.getPaymentStatus());
    ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
    verify(pointRepository).save(pointCaptor.capture());
    Point savedPoint = pointCaptor.getValue();
    assertEquals(6000L, savedPoint.getBalance());
  }

  @Test
  @DisplayName("처음 충전하는 사람 포인트 충전")
  void newChargePointTest() {
    // given
    Member member = Member.builder()
        .id(1L)
        .email("test@email.com")
        .build();

    UserDetailsImpl userDetails = new UserDetailsImpl(member);

    given(memberRepository.findByEmail(any(String.class))).willReturn(Optional.of(member));
    given(pointRepository.findByMember(any(Member.class))).willReturn(Optional.empty());

    // when
    PointChargeResponse pointChargeResponse = pointService.chargePoint(userDetails,
        PointChargeRequest.builder().price("5000").build());

    // then
    assertEquals(PaymentStatus.SUCCESS.toString(), pointChargeResponse.getPaymentStatus());
    ArgumentCaptor<Point> pointCaptor = ArgumentCaptor.forClass(Point.class);
    verify(pointRepository).save(pointCaptor.capture());
    Point savedPoint = pointCaptor.getValue();
    assertEquals(5000L, savedPoint.getBalance());
  }

}