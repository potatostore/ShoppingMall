---
[20261102 ~ 20261109]
### 이번주 구현 목표
- [x] 1. (20261102 사용자 요청, claude/hopeful-mccarthy-6y31xt) origin/config/test를 작업 브랜치에 먼저 머지하고 그 위에서 작업. config/test가 main에 머지되면 PR diff는 이번 작업분만 남음. UserControllerTest 94행 컴파일 오류(createUser_inValidTest 미완성)는 @Valid 400 테스트로 완성.
- [x] 2. (Phase 1 이월) 회원가입 role 서버 고정: UserCreateDTO.signUpRole과 가입 화면 Role 입력칸 삭제, UserService가 RoleNames.USER로 저장. ADMIN 계정은 DB 직접 수정 또는 Phase 4 seed.
- [x] 3. (Phase 1 이월) Product 상세 목록 교체 버그: patchProduct·putProduct를 replaceProductDetails(clear → assignProduct → add)로 통일, @DataJpaTest 회귀 테스트(수정 전 실패 확인).
- [x] 4. (Phase 1 이월) @Valid 누락: POST /carts, POST /products에 @Valid, CartUpdateDTO·OrderUpdateDTO 리스트에 @Valid + @NotEmpty, ProductCreateDTO 상세 목록 @NotEmpty·상세 @NotBlank, UserUpdateDTO @Email.
- [x] 5. (20261008 구현 기능 관련 문제점 12번) authTossPayment 주문 상태 확인: 주문 행 비관적 잠금(findByOrderUidForUpdate) 후 PAID면 토스 미호출·현재 주문 반환(멱등), PENDING만 진행, 그 외 400 ORDER_STATUS_INVALID. OrderStatus는 @Enumerated(STRING).
- [x] 6. (명세서 5번 표 높음) 주문 가격 서버 조회: OrderItemCreateDTO·OrderItemUpdateDTO에서 curOrderItemPrice 삭제, createOrder가 Product.price로 OrderItem 생성, 주문 수정은 PENDING 주문의 수량만.
- [x] 7. (Phase 2 이월) accessDeniedHandler 추가: 403 바디를 ApiResponse로 통일, SecurityErrorResponseTest로 401·403 바디 확인.
- [x] 8. 새 기능(20261102 결정: 기본 범위): GET /users/find-id(마스킹 이메일), POST /auth/reissue(refresh token rotation, type 클레임·jti) + 로그아웃 쿠키 만료, 상품 재고(조건부 UPDATE 차감, 실패·PENDING 삭제 시 복구, 동시성 테스트), 결제 승인 후 장바구니에서 결제 상품 제거, 프론트 authFetch(401 → 재발급 → 재시도).
- [x] 9. 기타 수정: 이메일 중복 500 → 400, deleteCart readOnly 트랜잭션으로 삭제 미반영, 내 주문 단건 조회 단건 쿼리, 장바구니 같은 상품 중복 행.
- 종료 조건: ./gradlew test 128개 통과(config/test 머지 직후 82개), 프론트 tsc 통과, PR 생성 후 사용자 리뷰·머지. 상세 기록은 저장소 docs/claude-work-log/20261102_work-log.md, 미구현은 20261102_unimplemented.md.

### 컴파일 및 디버깅 관련 문제
- [x] 1. config/test 5ef71ff의 UserControllerTest 94행 String body = "" 뒤 세미콜론 누락으로 compileTestJava 실패 → 테스트 전체 실행 불가. 테스트 완성으로 해결.
- [x] 2. JwtProvider.checkValidationToken의 catch(SecurityException)이 import io.jsonwebtoken.* 상황에서 java.lang.SecurityException으로 해석됨(io.jsonwebtoken 패키지에는 SecurityException이 없음). 다른 키로 서명한 토큰이 오면 io.jsonwebtoken.security.SignatureException이 필터 밖으로 던져져 공개 API까지 모든 요청 500. jjwt SecurityException, JwtException, IllegalArgumentException을 잡도록 수정, JwtProviderTest로 재현 후 수정.
- [x] 3. CartService.deleteCart에 @Transactional이 없어 클래스 레벨 readOnly를 따름 → flush가 일어나지 않아 DELETE /carts/me가 200인데 행이 남음. 테스트 트랜잭션을 끈 CartServiceTransactionTest로 재현 후 수정.
- [x] 4. 같은 상품을 장바구니에 두 번 담으면 CartItem 2행 → patchCart의 Collectors.toMap에서 IllegalStateException(Duplicate key) → 500. 기존 항목 수량 증가로 수정, CartTest로 재현 후 수정.
- [ ] 5. (로컬 조치) ddl-auto: update는 기존 orders.order_status 컬럼(숫자)을 문자열로 바꾸지 않음 → 로컬 DB의 orders, order_item 테이블 drop 필요. product.stock 컬럼이 추가되면 기존 상품 재고는 0이므로 PATCH /products/{id}로 stock 지정 필요.
- [ ] 6. 클라우드 세션에서 Maven Central이 429를 반환해 Gradle 의존성 다운로드 실패 → 세션 로컬 init 스크립트(~/.gradle/init.d, 커밋 안 함)로 미러를 써서 빌드. 저장소 설정 변경 없음, 로컬·CI에는 영향 없음.

### 구현 기능 관련 문제점
- [ ] 1. (높음) PENDING 주문 만료 처리 없음: 재고를 주문 생성 시 잡으므로 결제 이탈 시 재고가 계속 묶임. @Scheduled로 일정 시간 지난 PENDING을 FAILED 처리 + 재고 복구 필요(행 잠금 사용).
- [ ] 2. (높음) 토스 RestClient 타임아웃 없음, 네트워크 오류 시 500 + 주문 PENDING 유지. 토스는 승인했는데 응답만 유실되면 결제·주문 불일치 → 타임아웃 설정 + 토스 조회 API로 상태 확인 필요. RestClientConfig와 TossPaymentConfig에 RestClient 빈이 중복.
- [ ] 3. (높음, 미확인) 회원 탈퇴 시 orders.user_id FK, 상품 삭제 시 cart_item.product_id FK 위반으로 500 예상. soft delete 정책 결정 필요.
- [ ] 4. (중간) 비밀번호 찾기는 20261102 결정으로 미구현. 현재 프론트 설계(이메일+이름+전화번호로 즉시 변경)는 계정 탈취 위험 → 메일 인증 + Redis 재설정 토큰 방식 권장. 그 전까지 로그인 화면 링크 숨김 권장.
- [ ] 5. (중간) refresh token이 사용자당 1개라 다른 기기 로그인 시 이전 기기는 다음 재발급에서 401. 재사용 감지(세션 강제 종료)는 이 구조에서 다른 기기 세션까지 끊어 적용하지 않음. 기기별 키로 바꾼 뒤 Phase 5에서 결정.
- [ ] 6. (중간) 서버 컴포넌트(profile, orders 페이지)는 access token 만료 시 재발급하지 못함 → middleware 또는 클라이언트 authFetch로 이전 필요.
- [ ] 7. (중간) 토스 승인 소유권 확인, orderUid 난수 충돌, 로그인 실패 메시지로 가입 여부 노출은 미처리(각각 20260826 결정, 낮은 확률, Phase 5).
- [ ] 8. (낮음) 프론트 eslint 기존 에러 5건(login 컴포넌트 이름 소문자 rules-of-hooks 3건, set-state-in-effect 2건), toss-test 페이지 잔존, 목록 API N+1.

### 다음 한 주 동안 개발할 기능
- [ ] 1. PR 리뷰 후 머지 순서: config/test PR 먼저 main 머지 → 이 브랜치 PR 머지. 머지 후 로컬 DB orders·order_item drop, 기존 상품 stock 지정, application.yml access token 30분(1800000) 원복 후 E2E(가입 → 로그인 → 담기 → 주문 → 결제 → 승인 → 장바구니·재고 확인 → 결과 페이지 새로고침 시 PAID 유지).
- [ ] 2. Obsidian API 명세서 갱신: POST /auth/reissue, GET /users/find-id 추가, signup·orders·products 요청/응답 변경, 403 바디, 새 ErrorCode(O003, A001). 목록은 docs/claude-work-log/20261102_work-log.md 3번.
- [ ] 3. Phase 2 마무리: SecurityConfig 인가 테스트 전체(공개 200 / 비로그인 401 / USER 403 / ADMIN 200), 이번에 추가된 /auth/reissue·/users/find-id 공개 경로 포함.
- [ ] 4. Phase 3 Flyway: V1 스키마는 이번 변경(product.stock, orders.order_status 문자열) 반영 후 작성.
- [ ] 5. 구현 기능 관련 문제점 1·2번(PENDING 만료, 토스 타임아웃·상태 조회)을 주문 취소·환불 기능과 함께 설계.
