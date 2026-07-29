[20260729]
### 컴파일 및 디버깅 관련 문제
1. **치명** `CartItem` 컴파일 실패: `CartItemResponseDTO` import 누락으로 `cannot find symbol` 발생 → DTO import 추가 필요.
2. **중간** 장바구니 합계 계산 NPE 위험: `isEmpty()`가 null 체크보다 먼저 실행됨 → null 체크를 앞에 두도록 조건 순서 변경 필요.
3. **중간** 전역 예외/응답 포맷 미적용 상태에서 디버깅 비효율: 예외 타입별 응답 형식 불일치로 원인 추적 비용 증가 → `@RestControllerAdvice` + 공통 에러 포맷 매핑 필요.
4. **경미** Validation 실패 응답 비표준 가능성: 필드 에러 구조가 엔드포인트마다 달라질 수 있음 → `field/reason/rejectedValue` 고정 포맷 필요.
5. **경미** 주문/재고 로직 사전 테스트 포인트 부재: 동시성/재고 차감 오류 발견이 늦어질 위험 → 주문 생성/재고 차감/취소 복구 테스트를 초기부터 포함 필요.

### 구현 기능 관련 문제점
1. **치명** 주문 라인아이템 구조 부재: `Order.productId` 단일 컬럼 + 비어 있는 `OrderDetail`로 다건 상품 주문 처리 곤란 → `Order`-`OrderDetail` 관계 및 FK 명시 필요.
2. **치명** 찜 기능 모델 미완성: `Like`가 `id`만 보유해 사용자-상품 매핑 불가 → `userId`, `productId`, 유니크 제약 필요.
3. **치명** PK 전략 불일치: `User/Product`의 `String id`와 `IDENTITY` 전략 충돌 가능 → 정수 PK 또는 UUID 전략으로 통일 필요.
4. **중간** 최근 본 상품 확장성 부족: 단일 `recentWatchingProductId`로 목록/정렬 기능 한계 → 별도 이력 테이블 설계 필요.
5. **중간** 주문 이력 재현성 부족: 주문 시점 가격/수량 스냅샷 부재 → `OrderDetail`에 주문 당시 금액/수량 컬럼 필요.
6. **중간** 재고 무결성 경계 부재: 결제/취소 시점 재고 정합성 깨질 가능성 → 재고 차감/복구를 하나의 트랜잭션 경계로 처리 필요.
7. **중간** 운영 표준 계층 미완성: 공통 에러코드/응답계약/로깅 추적 미통일 → API 응답/예외/traceId 표준화 필요.
8. **중간** 직렬화/시간대 정책 부재: 날짜 포맷·타임존이 환경별로 달라질 수 있음 → Jackson ISO-8601 포맷 및 타임존 정책 고정 필요.
9. **경미** CORS 정책 산재 위험: 컨트롤러 단위 분산 설정 시 운영 실수 가능 → 글로벌 CORS + 프로파일별 허용 도메인 분리 필요.

### 다음 개발 기능
1. **Global Config 최소 골격 구현**: `ApiResponse<T>`, `ErrorResponse`, `ErrorCode`, `GlobalExceptionHandler`, Validation 에러 매핑을 먼저 고정.
2. **주문 코어 도메인 구현**: `Order`-`OrderDetail` 관계, 주문 상태, 주문 시점 가격/수량 스냅샷 컬럼 도입.
3. **장바구니→주문 전환 API 구현**: CartItem 다건 주문 생성, 총액 검증, 주문 저장 처리.
4. **재고 처리 구현**: 결제 시 재고 차감/취소 시 복구, 동시성 충돌 대응(락/버전 전략).
5. **찜/최근본 정규화 구현**: `Like(userId, productId)` 유니크 제약 및 `recent_watching(userId, productId, viewedAt)` 이력 테이블 추가.