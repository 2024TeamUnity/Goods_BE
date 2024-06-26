package com.unity.goods.domain.member.dto;

import static com.unity.goods.domain.member.entity.Badge.badgeToString;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.unity.goods.domain.member.entity.Member;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

public class MemberProfileDto {

  @Getter
  @Builder
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class MemberProfileResponse {

    private Long memberId;
    private String nickName;
    private String phoneNumber;
    private String profileImage;
    private boolean tradePasswordExists;
    private double star;
    private List<String> badgeList;

    public static MemberProfileResponse fromMember(Member member, boolean tradePasswordExists) {

      return MemberProfileResponse.builder()
          .memberId(member.getId())
          .nickName(member.getNickname())
          .phoneNumber(member.getPhoneNumber())
          .profileImage(member.getProfileImage())
          .tradePasswordExists(tradePasswordExists)
          .star(member.getStar())
          .badgeList(badgeToString(member.getBadgeList()))
          .build();
    }

  }
}
