package com.unity.goods.domain.member.entity;

import static com.unity.goods.domain.member.type.SocialType.SERVER;
import static com.unity.goods.domain.member.type.Status.ACTIVE;
import static com.unity.goods.domain.member.type.Status.INACTIVE;
import static com.unity.goods.domain.member.type.Status.RESIGN;

import com.unity.goods.domain.goods.entity.Goods;
import com.unity.goods.domain.goods.entity.Wishlist;
import com.unity.goods.domain.member.dto.SignUpDto.SignUpRequest;
import com.unity.goods.domain.member.type.Role;
import com.unity.goods.domain.member.type.SocialType;
import com.unity.goods.domain.member.type.Status;
import com.unity.goods.domain.model.BaseEntity;
import com.unity.goods.domain.trade.entity.Trade;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Member extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;
  @Setter
  private String nickname;

  @Column(unique = true)
  private String email;
  private String password;

  @Column(unique = true)
  private String phoneNumber;
  @Setter
  private String profileImage;

  @Builder.Default
  private double star = 0;

  @Enumerated(EnumType.STRING)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private Status status = INACTIVE;

  private String tradePassword;

  @Enumerated(EnumType.STRING)
  private SocialType socialType;

  @Column(nullable = false)
  @Builder.Default
  private Long balance = 0L;

  @Column
  private String fcmToken;

  @OneToMany(mappedBy = "member")
  @Builder.Default
  private List<Goods> goodsList = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  @Builder.Default
  private List<Wishlist> wishList = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  @Builder.Default
  private List<Trade> tradeList = new ArrayList<>();

  @OneToMany(mappedBy = "member")
  @Builder.Default
  private List<Badge> badgeList = new ArrayList<>();

  public static Member fromSignUpRequest(SignUpRequest signUpRequest, String imageUrl) {

    return Member.builder()
        .nickname(signUpRequest.getNick_name())
        .email(signUpRequest.getEmail())
        .password(signUpRequest.getPassword())
        .phoneNumber(signUpRequest.getPhone_number())
        .profileImage(imageUrl)
        .role(Role.USER)
        .status(ACTIVE)
        .tradePassword(signUpRequest.getTrade_password())
        .socialType(SERVER)
        .build();
  }

  public void resignStatus() {
    this.status = RESIGN;
  }

  public void changePassword(String password) {
    this.password = password;
  }

}
