package com.unity.goods.domain.point.controller;

import com.unity.goods.domain.point.dto.PointBalanceDto.PointBalanceResponse;
import com.unity.goods.domain.point.dto.PointChargeDto;
import com.unity.goods.domain.point.dto.PointChargeDto.PointChargeResponse;
import com.unity.goods.domain.point.service.PointService;
import com.unity.goods.global.jwt.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/point")
public class PointController {

  private final PointService pointService;

  @PostMapping("/charge")
  public ResponseEntity<?> chargePoint(
      @AuthenticationPrincipal UserDetailsImpl member,
      @Valid @RequestBody PointChargeDto.PointChargeRequest pointChargeRequest
  ) {
    PointChargeResponse pointChargeResponse = pointService.chargePoint(member, pointChargeRequest);
    return ResponseEntity.ok(pointChargeResponse);
  }

  @GetMapping("/balance")
  public ResponseEntity<?> getBalance(@AuthenticationPrincipal UserDetailsImpl member) {
    PointBalanceResponse pointBalanceResponse = pointService.getBalance(member);
    return ResponseEntity.ok(pointBalanceResponse);
  }

}
