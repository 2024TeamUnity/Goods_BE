package com.unity.goods.domain.point.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PointChargeDto {

  @Getter
  @Setter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PointChargeRequest {

    @NotBlank(message = "상품 가격을 입력해주세요.")
    @Pattern(regexp = "^[1-9][0-9]*$", message = "가격은 0또는 (-)로 시작하지 않는 숫자로 입력해야 합니다.")
    private String price;

  }

  @Getter
  @Builder
  public static class PointChargeResponse {
    private String paymentStatus;
  }

}
