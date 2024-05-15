package com.unity.goods.domain.point.config;

import com.siot.IamportRestClient.IamportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IamPortConfig {

  @Value("${imp.api.key}")
  private String apikey;

  @Value("${imp.api.secretkey}")
  private String secretkey;

  @Bean
  public IamportClient iamportClient() {
    return new IamportClient(apikey, secretkey);
  }

}
