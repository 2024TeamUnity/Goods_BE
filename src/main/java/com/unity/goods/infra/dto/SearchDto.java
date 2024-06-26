package com.unity.goods.infra.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.unity.goods.infra.document.GoodsDocument;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class SearchDto {

  @Getter
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class SearchRequest {
    private String keyword;
  }

  @Getter
  @Builder
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class SearchedGoods {

    private long goodsId;
    private String sellerNickName;
    private String goodsName;
    private Long price;
    private String tradeSpot;
    private String thumbnailUrl;
    private double lat;
    private double lng;
    private Integer uploadedBefore;

    public static SearchedGoods fromGoodsDocument(GoodsDocument goodsDocument) {

      int before;
      if(goodsDocument.getUploadedBefore() == 0) {
        before = 60 * 60;
      } else {
        before = (int)Instant.now().getEpochSecond() - goodsDocument.getUploadedBefore();
      }

      return SearchedGoods.builder()
          .goodsId(goodsDocument.getId())
          .sellerNickName(goodsDocument.getSellerNickName())
          .goodsName(goodsDocument.getGoodsName())
          .price(goodsDocument.getPrice())
          .tradeSpot(goodsDocument.getAddress())
          .thumbnailUrl(goodsDocument.getThumbnailUrl())
          .uploadedBefore(before)
          .lat(goodsDocument.getLocation().lat())
          .lng(goodsDocument.getLocation().lon())
          .build();
    }
  }
}
