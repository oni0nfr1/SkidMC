# SkidMC 1.0.0 API 리팩터링 계획

> 기준일: 2026-07-18
>
> 작업 브랜치: `refactor/kart-centric-api`
>
> 목표 버전: `1.0.0-beta.1`(예정)

## 목표

0.x API의 라이더 중심 상태 모델과 API·구현체 간 강한 결합을 제거하고, 카트의 수명 주기와 타입 관계가 명확한 장기 유지 가능한 API를 만든다.

핵심 원칙은 다음과 같다.

- 탑승자와 공통 주행 상태는 `Kart`가 제공하고, 엔진 종류에 따라 달라지는 상태는 `KartEngine` typed view가 제공한다.
- 엔진은 플레이어 탑승 시점이 아니라 카트의 첫 어트리뷰트가 준비된 시점에 생성한다.
- `KartType<E>`가 엔진 타입을 표현하고, 구체 엔진 인터페이스가 대응 타코미터 타입을 보장한다.
- 장기간 보관 가능한 객체는 `Ref<T>`로 노출하고, 실제 객체 접근 시 유효성을 다시 확인한다.
- 공개 API 계약과 SkidMC 구현체를 Gradle 모듈 수준에서 분리한다.
- 안정성이 보장되지 않은 API는 기존 패키지 구조 아래의 `unstable` 패키지로 명확히 표시한다.

## 현재까지 완료한 작업

### 선행 버그 수정 및 준비

- [x] 어트리뷰트보다 늦게 passenger 정보가 도착하는 경우 탑승 처리를 완료하도록 수정
  - 커밋: `2024af0 fix(impl/events): complete mounts after late passenger updates`
- [x] 버전을 `0.1.7`로 갱신
  - 커밋: `f58eb8e chore(internal/project): bump version to 0.1.7`
- [x] `refactor/kart-centric-api` 브랜치 생성
- [x] 모드 아이콘 추가
  - 커밋: `7bef2d9 chore(internal/assets): add mod icons`
- [x] mount 테스트에서 EARLY 이벤트와 일반 탑승·관전 이벤트의 카트/엔진 상태를 검증하도록 강화
  - 커밋: `b4388c3 test(internal/events): strengthen mount event coverage`

### API 모듈

- [x] `skid-api` Gradle 모듈 기본 구조 생성
- [x] `skid-api`를 client 전용 Fabric 모듈로 구성
- [x] 루트 프로젝트에 `:skid-api` 포함
- [x] `skid-api`의 독립 client Kotlin 컴파일 확인
- [x] client API용 `clientTest` 소스셋을 구성하고 `check`에 연결
- [x] `skid` 구현체 모듈에서 `skid-api`를 임시 `compileOnly` 의존성으로 연결
- [x] 기존 0.x 중복 계약 제거 후 `modImplementation`으로 전환
- [x] 최종 배포 JAR에 API 모듈 포함
- [ ] API만 사용하는 모드의 권장 의존성 구성 확정
  - 컴파일: `modCompileOnly(project(":skid-api"))`
  - 개발 런타임: 구현체 모드 추가
  - 배포: 구현체와 API가 포함된 전체 SkidMC JAR 사용

### 새 핵심 계약

- [x] `Ref<T>`와 `get(): Optional<T>` 정의
- [x] Kotlin용 top-level inline `Ref<T>.access { ... }` 정의
- [x] `Kart<out ENGINE>` 정의
- [x] `Kart`에 다음 상태 배치
  - `alive`
  - `entity`, `saddle`, `model`
  - `rider`
  - `type`
  - `engine`
  - `position`, `velocity`
- [x] 현재 사용 계획이 없는 `direction`은 1.0.0 초기 계약에서 제외
- [x] 최소 `KartEngine` 계약 정의
  - 엔진은 독립 소유 객체가 아니라 엔진 종류에 따라 달라지는 Kart 상태의 typed view
  - 연결된 `kart`와 현재 `tachometer`를 제공
  - rider는 엔진에서 제거
- [x] `KartTachometer`에 `text`, `rawString` 계약 이동
- [x] `KartType<out ENGINE>` 타입 토큰 정의
- [x] 구체 엔진 인터페이스가 공변 반환 타입으로 대응 타코미터 타입을 보장
- [x] 단일 타입 파라미터 전환 후 불필요해진 `XEngineKart` 계열 타입 별칭 제거
- [x] `KartType`에 엔진 메타데이터 추가
  - `engineCode`
  - `attrEngineCode`
  - `engineName`
  - `engineKind`
- [x] `EngineKind.NORMAL`, `EngineKind.DUMMY` 정의
  - `DUMMY`는 기능 미구현 엔진이 아니라 공식 밸런스 체계 밖에서 제작된 비공식 엔진을 뜻한다.

### 엔진 및 타코미터 계층

- [x] 공통 엔진 기능 인터페이스 이동
  - `DriftEngine`, `SpeedEngine`
  - `NitroEngine`, `GearlikeEngine`
  - `InstantBoostEngine`, `DualBoostEngine`
  - `DraftEngine`, `ExceedEngine`
- [x] 모든 구체 엔진 인터페이스 이동
  - 공식 엔진: X, EX, JIU, NEW, Z7, V1, A2, 1.0, PRO, RUSH+, CHARGE, SR
  - 비공식 엔진: N1, RX, KEY, GEAR, RALLY, F1, MK, BOAT
- [x] 공통 타코미터 인터페이스 이동
  - `SpeedTachometer`, `NitroTachometer`
  - `GearlikeTachometer`, `ExceedTachometer`
- [x] 모든 구체 타코미터 인터페이스 이동
- [x] 각 엔진 인터페이스가 보장 가능한 가장 구체적인 카트 타입을 반환하도록 변경

```kotlin
sealed interface NitroEngine : DriftEngine, SpeedEngine {
    override val kart: Kart<NitroEngine>
    override val tachometer: NitroTachometer?
}

interface XEngine : NitroEngine, InstantBoostEngine, DualBoostEngine, DraftEngine {
    override val kart: Kart<XEngine>
    override val tachometer: XTachometer?
}
```

## 앞으로 진행할 작업

### 1. 구체적인 KartType 정의

- [x] 모든 엔진 타입을 `KartType`의 sealed object로 정의
- [x] 기존 `KartEngine.Type`의 코드와 이름을 이전
- [x] `engineCode` 조회 함수 제공
- [x] `attrEngineCode` 조회 함수 제공
- [x] 알 수 없는 코드는 API에서 로그를 남기지 않고 `null`로 반환
- [x] 구현체에서 알 수 없는 엔진 코드를 saddle ID와 함께 WARN으로 기록
- [x] Java에서 각 타입에 접근하는 형태 확인

예정 형태:

```kotlin
data object X : KartType<XEngine>(
    engineCode = 10,
    attrEngineCode = 0,
    engineName = "x",
    engineKind = EngineKind.NORMAL,
)
```

### 2. KartRef 재설계

- [x] `KartRef`를 `Ref<Kart<*>>` 기반 계약으로 정의
- [x] saddle entity ID로 조회하고 생성 당시 UUID 비교로 ID 재사용을 차단
- [x] `get()` 호출 시 렌더 스레드 및 카트 유효성 검사
- [x] `KartRef.specify(KartType.X)` 형태로 타입이 지정된 참조 제공
- [x] `specify()` 결과의 `access` 블록에서 `Kart<XEngine>`과 `engine.tachometer: XTachometer?` 추론 확인
- [x] Java의 `Optional` 및 제네릭 API 컴파일 테스트 추가
- [ ] Java 사용 예제 추가
- [x] 기존 `handle`, `Specific`, `accessEngine` 제거 또는 마이그레이션
- [x] `StaleKartException`을 제거하고 직접 stale 객체 접근은 `IllegalStateException`으로 통일
  - `KartRef`를 통한 유효하지 않은 참조 접근은 `Optional.empty()`로 표현한다.

### 3. API provider와 구현체 연결

- [x] API가 구현체를 찾기 위한 custom Fabric entrypoint 계약 정의
- [x] entrypoint 이름을 `skid-api-provider`로 확정
- [x] 구현체 Fabric 메타데이터에 `skid-api-provider` 등록
- [x] `SkidApiProvider`가 카트 조회 최소 기능만 제공하도록 설계
- [x] provider가 없으면 첫 API 접근 시 명확한 오류로 실패
- [x] provider가 둘 이상이면 첫 API 접근 시 명확한 오류로 실패
- [x] `skid` 모듈에 provider 구현 추가
- [x] API 코드에서 구현체의 `internal` 패키지를 직접 import하지 않도록 정리

### 4. 구현체를 카트 중심 수명 주기로 전환

- [x] 카트의 실제 엔진 어트리뷰트를 `KartType`으로 변환하는 internal resolver 추가
- [x] 엔진 생성 시점을 플레이어 탑승 시점에서 카트의 첫 어트리뷰트 준비 시점으로 이동
- [x] `KartEngine.rider`를 제거하고 `Kart.rider: Player?`로 이전
- [x] `KartEngine.tachometer`가 구체 엔진별 타코미터 타입을 반환하도록 재정의
- [x] 기존 구현 엔진이 새 API 엔진 인터페이스를 구현하도록 변경
- [x] 기존 카트 구현이 `Kart<E>`와 `KartType<E>` 관계를 보장하도록 변경
- [x] 엔진 생성 매핑과 구현 클래스 정보는 구현체 모듈의 factory에 유지
- [x] `currentLap`과 `maxLap`을 `api.kart.unstable`의 `Kart<*>` 확장 프로퍼티로 제공
  - 엔진 공통 계약에서는 제거
  - 1.1.0에서 레이스 상태 API를 추가할 때 해당 계약으로 이전
- [ ] 카트 제거 시 엔진·타코미터·참조가 올바르게 무효화되는지 검증

### 5. 카트 접근 유틸리티 이전

- [x] `KartSaddle.kart`를 새 `KartRef` 기반으로 제공
- [x] `Player.ridingKart`를 새 `KartRef` 기반으로 제공
- [x] 로컬 플레이어의 탑승/관전 상태 API 이전
- [x] `MountType` 이전 및 새 동기화 모델에 맞춰 검토
- [x] `Minecraft.kartEngineType`을 `KartRef`의 `KartType` 접근으로 대체
- [x] `TachometerRef` 제거 후 `KartRef`를 통한 타코미터 접근으로 통합
- [x] 기존 typealias 이름 마이그레이션
  - `KartSaddleEntity` → `KartSaddle`
  - `KartMainEntity` → `KartMain`

### 6. 새 엔티티 동기화와 탑승 관계 추적

- [x] 소환된 카트를 pending으로 추적하고 `KartType` 해석 후 ready로 전환
- [x] Vanilla 1.21.5의 초기 엔티티 추적에서도 별도 `SetPassengersPacket`이 전달됨을 확인
  - pairing bundle은 일반적으로 `AddEntity` → entity data → attributes → passengers 순서로 처리
  - passenger 엔티티의 pairing bundle도 기존 vehicle의 passenger 관계를 다시 전달할 수 있음
- [x] 어트리뷰트와 탑승 관계 중 어느 쪽이 먼저 도착해도 ready 탑승 상태로 전환
  - 기존 카트 재동기화에서는 attributes → passengers 순서가 일반적
  - 데이터팩 기반 최초 생성에서는 passengers → attributes 순서도 가능
- [ ] 어트리뷰트와 탑승 관계의 두 도착 순서에 대한 회귀 테스트 추가
- [ ] 레이스 도중 접속 후 즉시 관전하는 시나리오 회귀 테스트 추가
- [x] 엔진 준비 전에는 일반 탑승·관전 이벤트가 발행되지 않도록 보장
- [x] 엔진 준비 후 대기 중인 탑승·관전 상태를 완료하도록 보장

### 7. 이벤트 API 재정비

- [x] 준비된 `Kart`를 제공하는 공개 이벤트 진입점을 `KartRef`로 통일
  - EARLY, 공통 종료 및 첫 어트리뷰트 갱신은 준비 전 관계도 식별할 수 있도록 saddle 엔티티를 제공
  - 준비 완료 이벤트에서는 콜백이 끝날 때까지 참조가 유효한 `Kart`로 해석됨
- [x] 소환 이벤트를 조기 감지와 준비 완료 시점으로 분리
  - `SUMMON_EARLY`
  - `SUMMON`
- [x] 탑승 이벤트 시점 재정의
  - `MOUNT_EARLY`
  - `MOUNT`
  - EARLY 또는 ready 시작점을 하나의 `DISMOUNT`로 정리
- [x] `SYNC`/`UNSYNC` 계열 이벤트 도입 잠정 보류
  - 이름만 기존 EARLY/준비 완료 단계를 바꾸는 이벤트로는 도입하지 않음
  - 향후 서버의 실제 `SUMMON`/`REMOVE`, `MOUNT`/`DISMOUNT` 수명 주기와 클라이언트의 정보 접근 가능 기간을 구분할 수 있을 때 재검토
  - `SYNC`는 필요한 엔티티·어트리뷰트·관계 정보를 읽을 수 있게 된 시점, `UNSYNC`는 더 이상 읽을 수 없게 된 시점으로 쌍을 이루어야 함
  - 현재 Vanilla 패킷만으로는 실제 서버 생성·삭제와 클라이언트 추적 시작·종료를 신뢰성 있게 구분할 수 없으므로 효과적인 구현이 불가능함
- [x] 관전 이벤트가 준비된 `Kart`, `KartEngine`, `KartTachometer` 계약과 일치하도록 변경
- [x] `SPECTATE_EARLY`와 `SPECTATE` 시작점을 하나의 `SPECTATE_END`로 정리
- [x] `KartMountEvents`와 이벤트 생성 기반을 `skid-api`로 이전
- [x] 준비된 카트 수명 주기의 `KartSummonEvents`를 `skid-api`로 이전
- [x] 모든 콜백에 현재 `KartRef`를 제공하도록 `KartTachometerEvents`를 정리해 `skid-api`로 이전
- [x] 제거·하차·관전 종료 이벤트 순서 보장
- [x] modifier별 어트리뷰트 이벤트만 `api.events.unstable`에 배치하기로 결정
- [x] `MountTest`를 새 이벤트 구조로 전환

### 8. 어트리뷰트 API 정리

- [x] 엔진 타입 어트리뷰트의 기준 엔티티를 플레이어에서 카트로 완전히 전환
- [x] `RiderAttrEvents` 제거
- [x] 플레이어 기준 `realKartEngine`, `selectedKartEngine` 제거
- [x] 카트 기준 조회 결과를 `KartType`으로 변경
- [x] `AttrModifierSnapshot`은 stable, `KnownAttrModId`는 `api.attr.unstable`에 이전하기로 결정
- [x] 나머지 어트리뷰트 접근 API는 정리 후 stable API로 제공하기로 결정
- [x] `KART_INFO_ATTR_KEY`와 `getKartInfo()` 명칭으로 카트 정보 조회 API 통일
- [x] `AttrModifierSnapshot`이 전달받은 Map을 불변 복사하도록 변경
- [x] stable `KartAttrEvents.UPDATE`를 Vanilla 값 적용 후 발행
- [x] modifier별 이벤트를 `api.events.unstable`로 분리하고 이전 값을 함께 제공
  - 새 스냅샷에 존재하는 modifier의 추가·변경만 발행하고 제거는 별도 의미로 처리하지 않음
- [x] modifier 이벤트 registry를 구현 모듈의 immutable Map으로 이동
- [x] API 모듈에서 내부 로거와 구현 클래스 의존성 제거

### 9. 패키지 안정성 표시

- [ ] 안정 API와 unstable API의 기준 문서화
- [x] 기존 전체 패키지 구조는 유지
- [x] 불안정한 기능만 해당 영역 아래 `unstable` 하위 패키지로 이동
- [x] 저장소 전체에서 `unstable` 사용을 `grep`으로 확인할 수 있는 구조 유지
- [ ] unstable API의 호환성 보장 범위를 README와 KDoc에 명시

### 10. 빌드, 테스트 및 릴리스

- [ ] `skid-api` 전체 빌드 및 API JAR 내용 검증
- [ ] `skid`가 새 API만 사용하도록 전환
- [ ] `skid-test`를 새 API 계약으로 이전
- [x] Kotlin 타입 추론 테스트 추가
  - `KartRef.specify(KartType.X).access { ... }`
  - 중간·구체 엔진 타입에서 대응 타코미터 타입 추론
- [x] Java API 사용 테스트 추가
- [x] `skid` 구현 모듈의 `clientTest` 소스셋을 구성하고 `check`에 연결
- [x] 실제 provider resolver에서 동일 ID의 UUID 재사용을 거부하는 회귀 테스트 추가
- [ ] 엔티티 패킷 순서 조합별 mount/spectate 테스트 추가
- [x] 기존 `skid` 모듈의 중복 API 소스 제거
- [x] 배포 JAR에 `skid-api`가 정확히 한 번 포함되는지 확인
- [ ] 버전을 `1.0.0-beta.1`로 갱신
- [ ] 0.x → 1.0.0 마이그레이션 문서 작성
  - 1.0.0-beta API 계약이 더 확정될 때까지 보류
- [ ] refactor/fix/test 커밋을 목적별로 분리
- [ ] 브랜치 push 및 draft PR 작성

## 1.0.0 API 변동사항

### Kart가 상태의 중심이 됨

0.x에서는 엔진이 rider와 tachometer를 소유하고 탑승 시점에 엔진이 만들어졌다. 1.0.0에서는 카트 어트리뷰트만 준비되면 엔진을 생성할 수 있으며, 탑승 여부와 무관하게 카트 타입과 엔진에 접근할 수 있다.

```kotlin
// 0.x 개념
engine.rider
engine.tachometer

// 1.0.0
kart.rider
kart.engine
kart.engine.tachometer
kart.type
```

### 엔진 인터페이스가 대응 타코미터 타입을 표현함

```kotlin
interface Kart<out E : KartEngine>

interface XEngine : KartEngine {
    override val kart: Kart<XEngine>
    override val tachometer: XTachometer?
}
```

`Kart<XEngine>`만 지정하면 `kart.engine.tachometer`가 `XTachometer?`로 추론된다. Kart의 엔진 타입 파라미터는 공변이므로 구체 카트를 `Kart<NitroEngine>` 같은 공통 기능 타입으로 사용할 수 있다. 엔진과 타코미터 타입을 별도로 반복해서 명시하거나 이를 묶는 타입 별칭은 제공하지 않는다.

### KartType이 KartEngine.Type을 대체함

`KartEngine.Type` enum과 구현 클래스 매핑을 공개 API에서 제거한다. 새 `KartType<E>`는 다음을 함께 표현한다.

- 엔진 인터페이스 타입
- 명령용 엔진 코드
- 어트리뷰트용 엔진 코드
- 표준 엔진 이름
- `EngineKind`

구현 클래스와 생성 방법은 구현체 모듈의 책임으로 남긴다.

### 엔진 인터페이스가 대응 Kart 타입을 좁힘

```kotlin
interface XEngine : NitroEngine {
    override val kart: Kart<XEngine>
    override val tachometer: XTachometer?
}
```

엔진에서 카트로 이동하더라도 구체 엔진과 타코미터 타입을 잃지 않는다. 중간 기능 인터페이스도 자신이 보장할 수 있는 범위까지 타입을 좁힌다.

### 참조 API가 Optional 기반으로 변경됨

```kotlin
interface Ref<T> {
    fun get(): Optional<T>
}

inline fun <T : Any, R> Ref<T>.access(block: T.() -> R): R?
```

- Java에서는 `get()`과 `Optional` 사용을 권장한다.
- Kotlin에서는 실제 객체 사용 범위를 블록 안으로 유도하는 `access` 사용을 권장한다.
- 기존 nullable `handle`과 예외 기반 stale 접근은 제거할 예정이다.

### KartRef의 타입 지정 방식이 변경됨

기존의 런타임 클래스 기반 `specify(engine)`와 `accessEngine` 대신 타입 토큰을 사용한다.

```kotlin
kartRef.specify(KartType.X).access {
    engine              // XEngine
    engine.tachometer    // XTachometer?
}
```

### TachometerRef가 KartRef로 통합됨

타코미터는 엔진 종류에 따라 달라지는 현재 화면 상태이므로 별도 장기 참조 객체를 제공하지 않고, 유효한 `KartRef`에서 `kart.engine.tachometer`로 접근하는 방향으로 변경한다.

### 랩 정보는 임시 unstable API로 제공됨

1.0.0에서는 `Kart<*>.currentLap`과 `Kart<*>.maxLap`을 `api.kart.unstable` 확장
프로퍼티로 제공한다. 이 값들은 카트 정보 어트리뷰트에서 직접 읽으며, 1.1.0에서 레이스
상태 API가 추가되면 그 계약으로 이전할 예정이다.

### API와 구현체가 분리됨

- `skid-api`: 공개 계약, 타입 토큰, 참조 및 이벤트 API
- `skid`: 패킷 처리, 상태 추적, 엔진·타코미터 구현, provider 구현
- 배포용 SkidMC JAR: API와 구현체를 함께 포함

API는 custom Fabric entrypoint를 통해 구현체를 찾으며 구현체의 internal 클래스에 직접 의존하지 않는다.

### 일부 API는 unstable 패키지로 이동함

이벤트 시점이나 원시 어트리뷰트처럼 아직 변경 가능성이 큰 기능은 기존 영역 아래의 `unstable` 패키지로 이동할 수 있다. 이를 통해 소비자 프로젝트에서 다음과 같이 불안정 API 사용 여부를 빠르게 확인할 수 있다.

```shell
rg 'io\.github\.oni0nfr1\.skid\..*\.unstable' src
```

### 호환성

1.0.0은 0.x와 소스 및 바이너리 호환되지 않는 대규모 API 변경이다. 기존 모드는 최소한 다음 항목을 수정해야 한다.

- `KartEngine.Type` 사용을 `KartType`으로 변경
- `KartEngine.rider`와 `KartEngine.tachometer` 접근을 `Kart`로 이동
- `KartRef.handle`과 `accessEngine`을 `get()`/`access()`/`specify()`로 변경
- `TachometerRef` 사용을 `KartRef` 기반 접근으로 변경
- 변경된 이벤트 이름, 시점 및 콜백 인자에 맞게 리스너 수정
- 플레이어 기준 엔진 어트리뷰트 조회를 카트 기준 조회로 변경

## 아직 확정하지 않은 사항

- 알 수 없는 엔진 코드를 nullable로만 처리할지 `Unknown` 타입을 둘지
- 이벤트 API를 1.0.0부터 stable로 둘지 `unstable.events`에서 시작할지
- 원시 어트리뷰트 API의 공개 범위
- provider가 제공할 최소 조회 기능과 초기화 실패 처리 방식
