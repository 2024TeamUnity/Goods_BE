package com.unity.goods.domain.trade.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.unity.goods.domain.trade.entity.Trade;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PointTradeHistoryDto {

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class PointTradeHistoryResponse {
    private String goodsId;
    private String tradePoint;
    private LocalDateTime tradeAt;
    private String tradePurpose;
    private String balanceAfterTrade;
  }

  public static PointTradeHistoryResponse fromTrade(Trade trade) {
    return PointTradeHistoryResponse.builder()
        .goodsId(String.valueOf(trade.getGoods().getId()))
        .tradePoint(String.valueOf(trade.getTradePoint()))
        .tradeAt(trade.getTradedAt())
        .tradePurpose(String.valueOf(trade.getTradePurpose()))
        .balanceAfterTrade(String.valueOf(trade.getBalanceAfterTrade()))
        .build();
  }

}
