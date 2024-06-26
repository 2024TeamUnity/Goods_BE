package com.unity.goods.global.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import com.unity.goods.domain.oauth.handler.OAuth2FailHandler;
import com.unity.goods.domain.oauth.handler.OAuth2SuccessHandler;
import com.unity.goods.domain.oauth.repository.CustomAuthorizationRequestRepository;
import com.unity.goods.domain.oauth.service.OAuth2UserService;
import com.unity.goods.global.exception.AuthenticationEntryPointHandler;
import com.unity.goods.global.jwt.JwtAuthenticationFilter;
import com.unity.goods.global.jwt.JwtTokenProvider;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;
  private final OAuth2UserService oAuth2UserService;
  private final CustomAuthorizationRequestRepository authorizationRequestRepository;
  private final OAuth2SuccessHandler successHandler;
  private final OAuth2FailHandler failHandler;

  private final AuthenticationEntryPointHandler authenticationEntryPointHandler;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors
            .configurationSource(request -> {
              CorsConfiguration config = new CorsConfiguration();

              config.setAllowedOrigins(List.of("https://goods-trade.vercel.app", "https://apic.app",
                  "http://localhost:5173"));
              config.setAllowedMethods(Collections.singletonList("*"));
              config.setAllowCredentials(true);
              config.setAllowedHeaders(Collections.singletonList("*"));
              config.setMaxAge(3600L);

              config.setExposedHeaders(Arrays.asList("Authorization", "Set_Cookie"));
              return config;
            }))

        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(configurer -> configurer
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
            .requestMatchers(anyRequest()).permitAll()
            .requestMatchers(requestAuthenticated()).authenticated()
        )
        // OAuth2
        .oauth2Login(oauth2 -> oauth2
            .authorizationEndpoint(e -> e
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(authorizationRequestRepository))
            .redirectionEndpoint(e -> e.baseUri("/api/oauth/code/*"))
            .userInfoEndpoint(e -> e.userService(oAuth2UserService))
            .successHandler(successHandler)
            .failureHandler(failHandler))
        // 인가되지 않은 사용자 접근 에러 핸들링
        .exceptionHandling(exceptionHandling ->
            exceptionHandling
                .authenticationEntryPoint(authenticationEntryPointHandler))
        // JWT Filter
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter.class)
    ;
    return http.build();
  }

  // 모든 사용자 접근 가능 경로
  private RequestMatcher[] anyRequest() {
    List<RequestMatcher> requestMatchers = List.of(
        antMatcher("/"),
        antMatcher("/oauth/*"),
        antMatcher(POST, "/api/member/signup"), // 회원가입
        antMatcher(POST, "/api/member/login"),  // 로그인
        antMatcher(POST, "/api/member/reissue"), // 토큰 재발급
        antMatcher(GET, "/api/member/badge"), // 배지 조회
        antMatcher(GET, "/api/member/{sellerId}/profile"), // 판매자 정보 조회
        antMatcher(POST, "/api/email/**"), // 이메일 인증
        antMatcher(POST, "/api/member/find"), // 비밀번호 찾기
        antMatcher(POST, "/api/goods/search"), // 검색
        antMatcher(GET, "/api/goods"), // 첫 화면
        antMatcher(POST, "/api/goods"), // 클러스터러
        antMatcher(GET, "/api/goods/**"), // 상품 상세 페이지
        antMatcher(GET, "/api/goods/sell-list/**"),
        antMatcher("/api/chat/**"),
        antMatcher("/ws/**")
    );
    return requestMatchers.toArray(RequestMatcher[]::new);
  }

  // 유저, 관리자 모두 접근 가능
  private RequestMatcher[] requestAuthenticated() {
    List<RequestMatcher> requestMatchers = List.of(
        antMatcher(POST, "/api/member/logout"), // 로그아웃
        antMatcher(PUT, "/api/member/resign"), // 회원탈퇴
        antMatcher(PUT, "/api/member/password"), // 비밀번호 변경
        antMatcher(PUT, "/api/member/trade-password"), // 거래 비밀번호 변경
        antMatcher(GET, "/api/member/profile"), // 회원정보 조회
        antMatcher(PUT, "/api/member/profile"), // 회원정보 수정
        antMatcher(POST, "/api/goods/new"),
        antMatcher("/api/goods/**"),
        antMatcher("/api/trade/**"),
        antMatcher("/api/point/**"),
        antMatcher("/api/notification/**")
    );
    return requestMatchers.toArray(RequestMatcher[]::new);
  }

}
