package com.unity.goods.domain.member.controller;

import static com.unity.goods.domain.member.dto.ChangePasswordDto.ChangePasswordRequest;
import static com.unity.goods.domain.member.dto.ChangeTradePasswordDto.ChangeTradePasswordRequest;
import static com.unity.goods.domain.member.dto.FindPasswordDto.FindPasswordRequest;

import com.unity.goods.domain.member.dto.LoginDto;
import com.unity.goods.domain.member.dto.MemberProfileDto.MemberProfileResponse;
import com.unity.goods.domain.member.dto.ResignDto;
import com.unity.goods.domain.member.dto.SellerProfileDto.SellerProfileResponse;
import com.unity.goods.domain.member.dto.SignUpDto;
import com.unity.goods.domain.member.dto.UpdateProfileDto.UpdateProfileRequest;
import com.unity.goods.domain.member.dto.UpdateProfileDto.UpdateProfileResponse;
import com.unity.goods.domain.member.service.MemberService;
import com.unity.goods.domain.model.TokenDto;
import com.unity.goods.domain.model.TokenDto.requestRefreshToken;
import com.unity.goods.global.jwt.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

  private final MemberService memberService;

  @PostMapping("/signup")
  public ResponseEntity<?> signUp(
      @Valid @ModelAttribute SignUpDto.SignUpRequest signUpRequest) {
    SignUpDto.SignUpResponse signUpResponse = memberService.signUp(signUpRequest);
    return ResponseEntity.ok(signUpResponse);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginDto.LoginRequest request) {
    return ResponseEntity.ok(memberService.login(request));
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletRequest request) {
    memberService.logout(request.getHeader(HttpHeaders.AUTHORIZATION));
    return ResponseEntity.ok().build();
  }

  @PutMapping("/resign")
  public ResponseEntity<?> resign(
      @RequestHeader("Authorization") String accessToken,
      @AuthenticationPrincipal UserDetailsImpl member,
      @RequestBody ResignDto.ResignRequest resignRequest) {
    memberService.resign(accessToken, member, resignRequest);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/find")
  public ResponseEntity<?> findPassword(
      @RequestBody @Valid FindPasswordRequest findPasswordRequest) {
    memberService.findPassword(findPasswordRequest);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/profile")
  public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetailsImpl member) {
    MemberProfileResponse memberProfile = memberService.getMemberProfile(member);
    return ResponseEntity.ok(memberProfile);
  }

  @GetMapping("/{sellerId}/profile")
  public ResponseEntity<?> getProfile(@PathVariable Long sellerId) {
    SellerProfileResponse sellerProfile = memberService.getSellerProfile(sellerId);
    return ResponseEntity.ok(sellerProfile);
  }

  @PutMapping("/profile")
  public ResponseEntity<?> updateProfile(
      @AuthenticationPrincipal UserDetailsImpl member,
      @Valid @ModelAttribute UpdateProfileRequest updateProfileRequest
  ) {
    UpdateProfileResponse updateProfileResponse
        = memberService.updateMemberProfile(member, updateProfileRequest);

    return ResponseEntity.ok(updateProfileResponse);
  }

  @PutMapping("/password")
  public ResponseEntity<?> changePassword(
      @AuthenticationPrincipal UserDetailsImpl member,
      @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
    memberService.changePassword(changePasswordRequest, member);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/trade-password")
  public ResponseEntity<?> changeTradePassword(
      @AuthenticationPrincipal UserDetailsImpl member,
      @RequestBody @Valid ChangeTradePasswordRequest changeTradePasswordRequest) {
    memberService.changeTradePassword(changeTradePasswordRequest, member);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reissue")
  public ResponseEntity<?> reissue(HttpServletRequest request,
      @RequestBody requestRefreshToken token) {

    TokenDto beforeToken = TokenDto.builder()
        .accessToken(request.getHeader(HttpHeaders.AUTHORIZATION))
        .refreshToken(token.getRefreshToken())
        .build();

    return ResponseEntity.ok(memberService.reissue(beforeToken));
  }
}
