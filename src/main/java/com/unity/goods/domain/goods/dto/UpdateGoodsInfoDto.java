package com.unity.goods.domain.goods.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.unity.goods.domain.goods.entity.Goods;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class UpdateGoodsInfoDto {

  @Getter
  @Setter
  @Builder
  public static class UpdateGoodsInfoRequest {

    @NotBlank(message = "상품명을 입력해주세요.")
    private String goods_name;

    @NotBlank(message = "상품 가격을 입력해주세요.")
    @Pattern(regexp = "^[1-9][0-9]*$", message = "가격은 0으로 시작하지 않는 숫자로 입력해야 합니다.")
    private String price;

    @NotBlank(message = "상품 설명을 작성해주세요.")
    private String description;


    private List<String> images_to_delete;
    private List<MultipartFile> goods_image_files;

    @NotNull
    private Double lat;

    @NotNull
    private Double lng;

    @NotBlank
    private String address;

    private String user_defined_location;

    public void updateGoodsEntity(Goods goods) {
      goods.setGoodsName(this.goods_name);
      goods.setPrice(Long.parseLong(this.price));
      goods.setDescription(this.description);
      goods.setLat(this.lat);
      goods.setLng(this.lng);
      goods.setAddress(this.address + " " + this.user_defined_location);
    }

  }

  @Getter
  @Builder
  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public static class UpdateGoodsInfoResponse {

    private String sellerName;
    private String goodsName;
    private String price;
    private String description;
    private String address;
    private LocalDateTime updatedAt;

    public static UpdateGoodsInfoResponse fromGoods(Goods goods) {
      return UpdateGoodsInfoResponse.builder()
          .sellerName(goods.getMember().getNickname())
          .goodsName(goods.getGoodsName())
          .price(String.valueOf(goods.getPrice()))
          .description(goods.getDescription())
          .address(goods.getAddress())
          .updatedAt(goods.getUpdatedAt())
          .build();
    }
  }


}
