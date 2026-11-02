# 20261102 기능 개발 기록 (2차, claude/hopeful-mccarthy-6y31xt)

- 기준: main 1e1e7f3 (PR 23 머지) 위에서 새로 작업
- 범위: Obsidian `ShoppingMall - TODO` 노트의 "4. 기능 개발" 섹션 전체 (사용자 요청, 20261102)
- 제외(사용자 결정·설정 필요): 멀티 디바이스 정책, 회원 탈퇴·상품 삭제 정책(soft delete), 비밀번호 찾기(메일 의존성), 토스 승인 소유자 확인, Redis 상품 캐시(Redis 캐시 설정 필요)
- 손대지 않은 것: build.gradle, application.yml, docker-compose. 새 설정값은 모두 `@Value` 기본값으로 코드에 두어 설정 파일 없이 동작
- 테스트: `./gradlew test` **191개 전부 통과** (시작 133개 → 58개 추가)
- 프론트: `tsc --noEmit`, `next build` 통과. eslint 에러는 기존 2건(order, toss-test의 set-state-in-effect)만 남음

## 1. 커밋 목록 (오래된 순)

| # | 커밋 | 종류 | 내용 |
|---|---|---|---|
| 1 | fa19483 | fix | 로그인 실패 메시지 통일 (401 LOGIN_FAILED) |
| 2 | 6c97497 | feat | 로그아웃 시 access token 블랙리스트 (Redis) |
| 3 | 0096498 | feat | 서버 컴포넌트(profile, orders) 401 → 재발급 후 복귀 |
| 4 | bcccfc4 | feat | 토스 타임아웃 + 네트워크 오류 시 결제 조회로 상태 확인, 중복 RestClient 빈 정리 |
| 5 | cf2c84e | feat | 주문 취소·환불 (토스 결제 취소 API) |
| 6 | 6be9325 | feat | 오래된 PENDING 주문 만료 + 재고 복구 스케줄러 |
| 7 | dada8ae | fix | orderUid 충돌 시 재생성 |
| 8 | 5ed22ad | feat | 상품 목록 페이지네이션(서버 검색·정렬) + N+1 해결 |
| 9 | c40f2e2 | feat | 찜하기 |
| 10 | 2171b3a | fix | order_status를 MySQL ENUM이 아니라 VARCHAR로 저장 |

## 2. 변경 상세 (문제 → 해결 → 택하지 않은 대안 → 검증)

### 2-1. 로그인 실패 메시지 통일 (fa19483)
- 문제: 이메일 없음 400 "Email not found", 비밀번호 불일치 400 "User password unmatched" → 응답으로 가입 여부를 알 수 있음
- 해결: 둘 다 401 LOGIN_FAILED(U006) "Invalid email or password". 이메일이 없을 때도 더미 해시로 BCrypt 비교를 한 번 수행해 응답 시간 차이로도 드러나지 않게 함
- 택하지 않은 대안: 메시지만 통일(이메일 없음은 BCrypt를 건너뛰어 응답이 수십 ms 빨라서 시간으로 구분 가능)
- 검증: AuthServiceTest(이메일 없음에도 matches 호출), AuthControllerTest(401 메시지)

### 2-2. access token 블랙리스트 (6c97497)
- 문제: 로그아웃해도 access token은 만료(현재 7일)까지 유효
- 해결: access token에 jti 추가 → 로그아웃 시 Redis `blacklist:access:{jti}`에 남은 만료 시간만큼 TTL로 저장 → 필터가 블랙리스트 토큰은 인증하지 않음. 토큰 추출은 `TokenResolver`로 필터·컨트롤러 공용
- Redis 장애 시: 블랙리스트 확인 실패를 "인증 안 함"으로 처리(fail-closed, 401). 500을 내지 않고, 로그아웃한 토큰이 통과하지도 않음
- 택하지 않은 대안: 토큰 전체 문자열을 키로 저장(키가 길고 jti로 충분), fail-open(장애 중 로그아웃 토큰 통과)
- 기존 Redis 설정·StringRedisTemplate만 사용 (Redis 설정 변경 없음)
- 검증: JwtAuthenticationFilterTest 4건(정상, 블랙리스트, refresh 토큰, Redis 장애), AuthServiceTest·AuthControllerTest, JwtProviderTest(jti·남은 시간)

### 2-3. 서버 컴포넌트 토큰 재발급 (0096498)
- 문제: profile·orders 페이지는 서버에서 쿠키를 전달해 조회하므로 access token 만료 시 authFetch처럼 재발급하지 못하고 "로그인이 필요합니다"만 보임
- 해결: 401이면 `/auth/refresh?next=현재경로`로 redirect → 클라이언트 페이지가 POST /auth/reissue 후 `next?reissued=1`로 복귀. 복귀 후에도 401이면 로그인 안내(무한 반복 방지). next는 같은 사이트 내부 경로만 허용(오픈 리다이렉트 방지). 재발급 요청은 진행 중 Promise를 공유해 React StrictMode의 effect 두 번 실행에도 한 번만 호출
- 택하지 않은 대안: Next.js middleware에서 재발급(서버 간 호출 결과 쿠키를 브라우저와 다음 요청 양쪽에 다시 심어야 해서 복잡)
- 검증: tsc, next build (자동화된 E2E 없음 → 수동 확인 필요)

### 2-4. 토스 타임아웃·결제 상태 확인 (bcccfc4)
- 문제: RestClient 타임아웃 없음. 네트워크 오류 시 500 + 주문 PENDING. 토스는 승인했는데 응답만 유실되면 돈은 나갔는데 주문은 미결제
- 해결
  1. connect 5초 / read 30초 (`toss.payments.connect-timeout-ms`, `read-timeout-ms`로 변경 가능, 기본값은 코드)
  2. 승인 중 ResourceAccessException이면 GET /v1/payments/orders/{orderId}로 조회: DONE → 결제 완료, ABORTED·EXPIRED → 실패 + 재고 복구, 그 외(조회 실패·결제 없음·진행 중) → PENDING 유지 + 502 TOSS_SERVER_ERROR (사용자가 같은 paymentKey로 다시 승인 가능)
  3. 쓰지 않던 RestClientConfig(URL 하드코딩 중복 빈)와 빈 TossPaymentService 삭제
- 택하지 않은 대안: 네트워크 오류를 바로 FAILED 처리(승인된 결제를 실패로 만들 수 있음)
- 검증: OrderServiceTest 4건(오류 후 DONE·조회 실패·결제 없음·ABORTED), TossClientTest(MockRestServiceServer로 경로·바디 확인)

### 2-5. 주문 취소·환불 (cf2c84e)
- 해결
  1. POST /orders/{orderId}/cancel (본인 주문, 행 잠금). 바디 `{"cancelReason": "..."}` 선택(최대 200자, 없으면 기본 사유)
  2. PENDING → CANCELED. PAID → 토스 POST /v1/payments/{paymentKey}/cancel(Idempotency-Key `cancel-{orderUid}`) 성공 후 CANCELED. 둘 다 재고 복구
  3. 토스 거절·CANCELED가 아닌 결과 → 400 PAYMENT_CANCEL_FAILED(PA002), 네트워크 오류 → 502. 주문은 PAID 유지 → 다시 요청 가능(같은 Idempotency-Key라 이중 환불 없음)
  4. 승인 시 paymentKey 저장(승인 응답·조회 응답 모두), OrderStatus에 CANCELED, 주문 응답에 orderStatus 추가
  5. 주문 내역 화면에 상태 표시와 취소 버튼
- 주의: 이 변경 전에 결제된 주문은 paymentKey가 없어 API 환불 불가(400) → 토스 개발자센터에서 수동 환불
- 택하지 않은 대안: 부분 취소(지금 UI·주문 수정 정책과 맞지 않음, 필요해지면 cancelAmount 추가)
- 검증: OrderServiceTest 8건, OrderControllerTest 3건, TossClientTest(Idempotency-Key 헤더)

### 2-6. PENDING 주문 만료 (6be9325)
- 문제: 재고를 주문 생성 시 잡으므로 결제창 이탈 시 재고가 계속 묶임
- 해결: OrderExpirationScheduler가 60초마다(첫 실행 60초 후) 30분 지난 PENDING 주문을 최대 100건 조회 → 주문마다 별도 트랜잭션·행 잠금으로 처리. 만료 전 토스 결제 조회
  - DONE → 결제 완료로 복구 (승인 응답만 유실된 경우)
  - NOT_FOUND_PAYMENT / ABORTED / EXPIRED / CANCELED → EXPIRED + 재고 복구
  - 조회 실패·결제 진행 중 → 그대로 두고 다음 주기에 재시도
- 설정(모두 기본값 있음): `order.pending-expiration-minutes`(30), `order.pending-expiration.check-interval-ms`(60000), `order.pending-expiration.initial-delay-ms`(60000), `scheduling.enabled=false`로 끄기
- 택하지 않은 대안: 토스 조회 없이 만료(응답만 유실된 결제를 만료시켜 재고를 다시 팔 위험), 주문 전체를 한 트랜잭션(하나 실패 시 전부 롤백, 잠금 시간 증가)
- 주의: 서버를 여러 대 띄우면 각 서버에서 스케줄러가 돌아 같은 주문을 동시에 처리하려 함 → 행 잠금과 PENDING 재확인으로 중복 처리는 막히지만, 배포 단계에서 ShedLock 등 단일 실행 장치 검토 필요
- 검증: OrderServiceTest 6건, OrderExpirationSchedulerTest(실패해도 다음 주문 진행), OrderRepositoryTest(오래된 PENDING만 조회)

### 2-7. orderUid 충돌 재생성 (dada8ae)
- 해결: 저장 전 existsByOrderUid로 확인, 사용 중이면 최대 5번 다시 뽑음. 동시 충돌은 unique 제약에 맡김
- 검증: OrderServiceTest(첫 번째 충돌 시 두 번째 값으로 저장)

### 2-8. 상품 목록 페이지네이션 + N+1 (5ed22ad)
- 해결
  1. GET /products?keyword=&minPrice=&maxPrice=&sort=LATEST|NAME|PRICE_ASC|PRICE_DESC&page=0&size=20 → `{content, page, size, totalElements, totalPages, hasNext}`. size 1~100으로 보정, 잘못된 sort 400
  2. 검색은 상품명 부분 일치(대소문자 무시), % _는 '!'로 이스케이프 (역슬래시는 MySQL 기본 모드에서 문자열 이스케이프로 해석되어 SQL 오류가 나므로 사용 안 함)
  3. @BatchSize(100)를 Product·Order·Cart 컬렉션과 Product 엔티티에 적용 → 목록 조회 시 상세·항목을 행마다 조회하지 않고 IN 쿼리 하나로 (테스트로 쿼리 3개 이하 확인, @BatchSize를 빼면 실패함)
  4. 장바구니 항목에 productName·productPrice, 주문 항목에 주문 시점 productName 스냅샷 → 장바구니·결제·주문 내역 화면이 상품 전체 목록을 받지 않음. 스냅샷 이전 주문은 상품 단건 조회로 이름 표시
  5. 프론트: 상품 페이지 서버 검색·정렬·이전/다음(300ms 디바운스), 메인은 최신 8개, 결제 페이지는 결제할 상품만 단건 조회
- 호환성: GET /products의 data가 배열 → 페이지 객체로 바뀜 (프론트 5곳 모두 수정함)
- 택하지 않은 대안: default_batch_fetch_size 전역 설정(application.yml 변경 필요), fetch join + 페이징(컬렉션 fetch join은 메모리 페이징 경고 HHH90003004)
- 검증: ProductSearchRepositoryTest 5건(필터·대소문자·이스케이프·페이징·N+1), ProductServiceTest 2건(보정·이스케이프), ProductControllerTest 2건(파라미터·잘못된 sort)

### 2-9. 찜하기 (c40f2e2)
- 해결
  1. Likes: user + product, (user_id, product_id) unique. PK 컬럼 이름은 기존 likes 테이블과 같은 id 유지
  2. POST·DELETE /products/{productId}/likes(멱등), GET /products/{productId}/likes/me, GET /users/me/likes(상품 함께 조회). 모두 로그인 필요
  3. 상품 삭제·회원 탈퇴 시 찜을 먼저 삭제 (새 FK 때문에 삭제가 실패하지 않도록)
  4. 프론트: 상품 상세 찜 버튼, /likes 페이지, 마이페이지 링크
- 검증: LikesServiceTest 6건, LikesRepositoryTest 3건(unique, 정렬·개수, 삭제 쿼리), LikesControllerTest 5건

### 2-10. order_status VARCHAR 고정 (2171b3a)
- 문제: Hibernate 6.2+는 MySQL에서 @Enumerated(STRING)을 `enum('PENDING','PAID','FAILED')` 타입으로 만듦. ddl-auto update는 기존 컬럼을 바꾸지 않으므로 새 상태(CANCELED, EXPIRED) 저장이 실패함
- 해결: @JdbcTypeCode(SqlTypes.VARCHAR) → `varchar(20)`. MySQL 방언으로 DDL 스크립트를 생성해 수정 전 enum, 수정 후 varchar(20) 확인
- 기존 로컬 DB: `ALTER TABLE orders MODIFY order_status VARCHAR(20) NOT NULL;` (데이터 유지)

## 3. API 변경 요약 (Obsidian API 명세서 갱신 필요)

| 구분 | API | 변경 |
|---|---|---|
| 변경 | POST /users/login | 실패 시 항상 401 "Invalid email or password" |
| 변경 | DELETE /users/logout | 현재 access token을 블랙리스트에 등록 |
| 변경 | GET /products | 페이지 객체 응답, keyword·minPrice·maxPrice·sort·page·size 파라미터 |
| 변경 | GET /carts/me | 항목에 productName, productPrice |
| 변경 | 주문 응답 전체 | orderStatus 추가, 항목에 productName |
| 변경 | POST /orders/toss/payment/auth | 네트워크 오류 시 결제 조회로 확정, 확정 불가면 502 TOSS_SERVER_ERROR |
| 추가 | POST /orders/{orderId}/cancel | 주문 취소·환불 |
| 추가 | POST·DELETE /products/{productId}/likes | 찜하기·취소 |
| 추가 | GET /products/{productId}/likes/me | 내 찜 여부 + 찜 수 |
| 추가 | GET /users/me/likes | 내 찜 목록 |

새 ErrorCode: LOGIN_FAILED(401, U006), PAYMENT_CANCEL_FAILED(400, PA002). TOSS_SERVER_ERROR는 502로 변경. OrderStatus: CANCELED, EXPIRED 추가.

## 4. 로컬에서 확인할 것

1. `ALTER TABLE orders MODIFY order_status VARCHAR(20) NOT NULL;` (PR 23 이후 테이블을 새로 만들었다면 enum 타입이라 새 상태 저장이 실패함)
2. 새 컬럼 orders.payment_key, order_item.product_name, likes.user_id·product_id는 ddl-auto가 추가. likes 테이블에 기존 행이 있으면 NOT NULL 컬럼 추가가 실패하므로 비어 있는지 확인(기존 Likes는 id만 있어 사용되지 않았음)
3. 수동 E2E
   - 로그아웃 → 같은 access token으로 GET /users/me 401
   - access token 만료(짧게 설정) 상태로 /profile, /orders 진입 → 자동 재발급 후 정상 표시
   - 주문 → 결제 → 주문 내역에서 취소·환불 → 토스 테스트 결제 내역에서 취소 확인, 재고 복구 확인
   - 주문만 만들고 결제창 닫기 → 30분 뒤(테스트 시 order.pending-expiration-minutes를 1로) EXPIRED·재고 복구 확인
   - 상품 페이지 검색·정렬·페이지 이동, 찜하기·찜 목록
