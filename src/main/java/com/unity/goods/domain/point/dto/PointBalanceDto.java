package com.unity.goods.domain.point.dto;

import lombok.Builder;
import lombok.Getter;

public class PointBalanceDto {

  @Getter
  @Builder
  public static class PointBalanceResponse {
    private String price;
  }

}
