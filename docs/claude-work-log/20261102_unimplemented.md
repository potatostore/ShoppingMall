# 20261102 미구현·보류 항목

이번 브랜치(claude/hopeful-mccarthy-6y31xt)에서 구현하지 않았거나, 점검 중 발견했지만 범위 밖이라 남겨 둔 항목.
우선순위: 높음(운영 시 데이터·돈 문제) / 중간(기능 결함·사용성) / 낮음(정리·개선).
"미확인"은 코드로 추론했지만 실행해서 확인하지 않은 항목.

## 1. 사용자 결정으로 이번 범위에서 제외

| # | 항목 | 이유 | 구현 시 제안 |
|---|---|---|---|
| 1 | 비밀번호 찾기 POST /users/find-password | 20261102 결정 2-a. 프론트 설계(이메일+이름+전화번호만 맞으면 새 비밀번호로 변경)는 세 정보를 아는 누구나 계정을 탈취할 수 있음. 안전하게 하려면 메일 인증이 필요한데 메일 의존성 추가가 필요 | spring-boot-starter-mail 추가 → 재설정 토큰(UUID)을 Redis에 TTL 10~30분으로 저장 → 메일 링크로 토큰 전달 → 토큰 확인 후 비밀번호 변경, 사용한 토큰 즉시 삭제. 그 전까지 find-password 페이지는 백엔드가 없어 비로그인 시 401(anyRequest ADMIN 규칙)이므로, 로그인 화면 링크를 숨기거나 "준비 중" 안내로 바꾸는 것을 권장 |
| 2 | 주문 취소·환불 | 20261102 결정 3 (기본 범위만) | OrderStatus에 CANCELED 추가, Order에 paymentKey 컬럼 저장(승인 성공 시), POST /orders/{orderId}/cancel: PENDING이면 바로 취소 + 재고 복구, PAID면 토스 결제 취소 API(POST /v1/payments/{paymentKey}/cancel, cancelReason) 호출 후 CANCELED + 재고 복구 |
| 3 | 상품 목록 페이지네이션 | 20261102 결정 3. 응답 형식과 프론트가 함께 바뀜 (Phase 6) | Pageable + Page 응답 또는 커서 기반. 아래 4-3번 N+1과 같이 처리 |

## 2. 이번 변경으로 새로 필요해진 후속 작업

| # | 우선순위 | 항목 | 내용 | 제안 |
|---|---|---|---|---|
| 1 | 높음 | PENDING 주문 만료 처리 | 재고를 주문 생성 시점에 잡으므로, 사용자가 결제창에서 이탈하면 PENDING 주문이 재고를 계속 붙잡음 | @Scheduled로 N분(예: 30분) 지난 PENDING 주문을 FAILED(또는 EXPIRED)로 바꾸고 재고 복구. 처리 중인 승인과 겹치지 않도록 findByOrderUidForUpdate와 같은 행 잠금 사용 |
| 2 | 높음 | 토스 호출 타임아웃·네트워크 오류 | RestClient에 타임아웃이 없음. ResourceAccessException은 500으로 빠지고 주문은 PENDING + 재고 잡힌 상태로 남음. 토스는 승인했는데 응답만 유실되면 돈은 나갔는데 주문은 PENDING인 불일치 | RestClient에 connect/read timeout 설정, 타임아웃 시 토스 조회 API(GET /v1/payments/orders/{orderId})로 실제 상태 확인 후 반영. 1번 만료 처리 전에도 이 조회를 거쳐야 함 |
| 3 | 중간 | Flyway V1 스키마 반영 (Phase 3) | 이번 변경으로 product.stock(NOT NULL), orders.order_status(문자열, 길이 20) 스키마가 바뀜 | V1__init_schema.sql은 이 브랜치 머지 이후 엔티티 기준으로 작성 |
| 4 | 중간 | 로컬 DB 정리 | ddl-auto: update는 기존 order_status 컬럼(숫자)을 바꾸지 않음. 기존 상품은 stock 0 | 작업 기록 4번 "로컬에서 확인할 것" 참고 |
| 5 | 중간 | 서버 컴포넌트의 토큰 재발급 | profile, orders 페이지(서버 컴포넌트)는 쿠키를 직접 전달하므로 access token 만료 시 authFetch처럼 재발급하지 못함 | Next.js middleware에서 만료 감지 후 재발급, 또는 401이면 클라이언트 컴포넌트로 넘겨 authFetch 사용 |
| 6 | 중간 | refresh token 멀티 디바이스·재사용 감지 (Phase 5) | 사용자당 refresh token 1개(refresh-token:{userId}) → 다른 기기 로그인 시 이전 기기는 다음 재발급에서 401. 재사용 감지(세션 강제 종료)는 이 구조에서 다른 기기 세션까지 끊어 적용하지 않음(a11cfd7) | 기기별 키(refresh-token:{userId}:{deviceId 또는 jti})로 바꾼 뒤 재사용 감지 도입. 여러 탭이 동시에 재발급하면 한쪽이 401이 될 수 있음(authFetch의 단일 Promise는 탭 내부만 보장) → 직전 토큰 짧은 유예 기간 검토 |
| 7 | 중간 | 로그아웃 후 access token 유효 (Phase 5) | 로그아웃은 refresh token 삭제 + 쿠키 만료만 함. 탈취된 access token은 만료(현재 7일)까지 유효 | access token 30분 원복이 우선. 이후 Redis 블랙리스트(jti, 남은 만료 시간 TTL) |
| 8 | 낮음 | access token 만료 30분 원복 | application.yml은 이번 작업에서 수정하지 않음 (사용자 직접 적용) | access-token-expiration-ms: 1800000 |

## 3. 점검 중 발견한 기존 코드 문제 (이번에 수정하지 않음)

| # | 우선순위 | 대상 | 문제 | 제안 |
|---|---|---|---|---|
| 1 | 높음 | DELETE /users/me (미확인) | orders.user_id가 users를 FK로 참조 → 주문이 있는 회원 탈퇴 시 FK 위반으로 500 예상. Cart는 userId만 저장(FK 없음)이라 탈퇴 후 장바구니가 고아로 남음 | 탈퇴 정책 결정 필요: soft delete(상태 컬럼, 개인정보 마스킹) 권장. 주문 기록은 보존 대상 |
| 2 | 높음 | DELETE /products/{id} (미확인) | cart_item.product_id가 product를 FK로 참조 → 누군가 장바구니에 담은 상품 삭제 시 500 예상 | soft delete(판매 중지 상태) 권장. order_item은 productId만 저장(FK 없음)이라 주문 기록은 영향 없음 |
| 3 | 중간 | POST /orders/toss/payment/auth | 소유권 확인 없음. orderUid와 금액을 아는 로그인 사용자는 남의 주문 승인 요청 가능(PAID면 주문 내용 응답) | 20260826 결정(선택)대로 보류. 넣는다면 컨트롤러에서 userId 전달 후 order.getUser() 비교, 불일치 404 |
| 4 | 중간 | Order.orderUid | ThreadLocalRandom 난수라 충돌 시 unique 제약 위반 500 (확률은 낮음) | 충돌 시 재시도, 또는 시간 기반 ID |
| 5 | 중간 | POST /users/login | 실패 메시지가 "Email not found" / "User password unmatched"로 갈려 가입 여부 노출 (Phase 5) | 하나의 메시지·상태코드로 통일 |
| 6 | 중간 | Toss RestClient 설정 중복 | RestClientConfig.tossClientConfig(URL 하드코딩)와 TossPaymentConfig.tossRestClient 두 빈이 같은 타입으로 존재. TossClient는 파라미터 이름(tossRestClient)으로 주입받아 동작 중 | 하나로 정리(2-2번 타임아웃 설정과 함께) |
| 7 | 낮음 | 상품·주문 목록 N+1 | getProducts·getOrders가 목록마다 상세·주문항목을 지연 로딩 | fetch join 또는 @BatchSize / default_batch_fetch_size (1-3번과 같이) |
| 8 | 낮음 | CheckConfig의 IllegalArgumentException | 엔티티 불변식 위반이 500으로 나감. 이번에 DTO 검증을 보강해 사용자 입력 경로는 대부분 400으로 막힘 | 내부 불변식용으로 유지하되, 입력 검증은 DTO에서 하는 원칙 유지 |
| 9 | 낮음 | 프론트 eslint 기존 에러 5건 | login/page.tsx 컴포넌트 이름이 소문자(signinPage)라 rules-of-hooks 3건, order/page.tsx·toss-test/page.tsx의 set-state-in-effect 2건 (main과 동일, 이번 변경과 무관) | SignInPage로 이름 변경, SDK 로드 상태는 초기값 계산 또는 onLoad 콜백으로 |
| 10 | 낮음 | toss-test 페이지 | 결제 테스트용 페이지가 앱에 포함 | 배포 전 삭제 또는 개발 환경 전용 |
| 11 | 낮음 | Swagger 접근 | anyRequest ADMIN이라 Swagger가 ADMIN 전용 (copilot-addendum 20261007 13-②) | 의도 확인. 개발용이면 local 프로파일에서만 공개 |
| 12 | 낮음 | 배포 시 CORS·쿠키 | @CrossOrigin(localhost:3000) 컨트롤러마다 하드코딩, 쿠키 Secure·SameSite=Lax는 프론트와 API가 다른 사이트가 되면 전송 안 됨 | Deploy 단계에서 CorsConfigurationSource 한 곳 + 도메인 구성 결정 |

## 4. 원래 로드맵상 남은 항목 (copilot-addendum 기준)

| # | Phase | 항목 | 상태 |
|---|---|---|---|
| 1 | Phase 2 | SecurityConfig 인가 테스트 전체(공개 200 / 비로그인 401 / USER 403 / ADMIN 200) | 이번에는 401·403 바디 형식 테스트 2건(SecurityErrorResponseTest)만 추가 |
| 2 | Phase 2 | 컨트롤러 예외 매핑 테스트, any() → eq()/verify 정리, 매직값 정리 | 이번에 추가한 테스트는 eq·verify 사용, 기존 테스트는 그대로 |
| 3 | Phase 3 | Flyway 학습 + V1 스키마 + ddl-auto validate | 미착수 (의존성·설정은 사용자 직접) |
| 4 | Phase 4 | 목데이터 seed | 미착수 |
| 5 | Phase 5 | 재발급(완료) → access 30분 원복 → 로그아웃 쿠키 만료(완료) → 블랙리스트 → 멀티 디바이스 정책 | 2번 표 6~8번 |
| 6 | Phase 6 | Redis getProduct 캐시, 페이지네이션, 상품 수량 로직(재고 완료), deleteOrder 소유권(ADMIN 전용이라 해당 없음) | 캐시·페이지네이션 미착수 |
| 7 | 문서 | Obsidian API 명세서 갱신 | 작업 기록 3번 "API 변경 요약" 반영 필요 |
| 8 | 기능 | 찜하기(Likes) | 엔티티에 id만 있음 |
