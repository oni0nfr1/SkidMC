# 내부 불변식 주석 작성 규칙

SkidMC는 네트워크 패킷의 도착 순서, Minecraft 엔티티의 수명 주기, 카트 상태와 공개 API 참조의 유효성이 서로 연결되어 있다. 내부 상태의 전제가 깨지면 원인과 떨어진 HUD, 이벤트 또는 API 접근 코드에서 문제가 나타날 수 있으므로, 중요한 내부 계약을 코드 가까이에 명시하고 일관된 방식으로 검증한다.

이 문서는 내부 불변식과 관련된 주석, 런타임 검증, 로그 및 복구 정책의 작성 기준을 정의한다.

## 표식

내부 계약 주석에는 다음 표식을 사용한다. 표식은 저장소 전체에서 검색할 수 있도록 영문 대문자와 콜론을 그대로 유지한다.

| 표식 | 의미 |
| --- | --- |
| `INVARIANT:` | 객체나 상태가 유효한 동안 항상 유지되어야 하는 조건 |
| `REQUIRES:` | 함수가 호출되기 전에 호출자가 만족해야 하는 조건 |
| `ENSURES:` | 함수가 정상적으로 종료된 뒤 보장되는 조건 |
| `THREADING:` | 호출 또는 상태 접근이 허용되는 스레드와 동시성 조건 |
| `RECOVERY:` | 외부 상태로 인해 계약을 만족하지 못했을 때의 로그와 복구 동작 |

`SECURITY:`는 권한, 신뢰 경계 또는 악의적인 입력 처리처럼 실제 보안과 관련된 경우에만 사용한다. 일반적인 내부 상태 일관성은 `INVARIANT:`로 표현한다.

```bash
rg 'INVARIANT:|REQUIRES:|ENSURES:|THREADING:|RECOVERY:'
```

## 작성 원칙

1. 조건은 긍정형이며 검사 가능한 문장으로 작성한다.
2. “올바르게 처리한다”처럼 결과를 판단할 수 없는 표현은 피한다.
3. 상태의 생성, 변경, 제거와 월드 또는 연결 종료까지 전체 수명 주기를 포함한다.
4. 코드가 실제로 보장하지 않는 조건을 주석에 먼저 선언하지 않는다.
5. 불변식이 변경되면 관련 구현, 테스트와 주석을 같은 커밋에서 갱신한다.
6. 같은 계약을 여러 위치에 복사하지 않는다. 상태 선언부를 정본으로 삼고 함수에는 해당 함수가 유지하는 부분만 작성한다.
7. 자명한 지역 변수나 단순 조회에는 계약 표식을 붙이지 않는다.

좋은 불변식은 다음처럼 상태의 유효 범위를 명확히 한다.

```text
INVARIANT:
- 저장된 rider ID는 현재 client level에서 Player로 확인된 적이 있다.
- 하나의 rider ID는 최대 하나의 saddle ID에만 속한다.
- 이전 client level의 entity ID는 남아 있지 않는다.
```

다음과 같은 표현은 피한다.

```text
INVARIANT:
- passenger가 올바르게 관리된다.
- 문제가 생기지 않아야 한다.
```

## 작성 위치

### 상태 선언부

여러 함수가 함께 유지하는 불변식은 해당 상태를 소유하는 필드, 클래스 또는 object 선언부에 작성한다. 상태 선언부의 주석이 전체 불변식의 정본이다.

```kotlin
/**
 * INVARIANT:
 * - 저장된 rider ID는 현재 client level에서 Player로 확인된 적이 있다.
 * - 저장되기 전에 해당 관계의 MOUNT_EARLY 호출이 정상적으로 완료됐다.
 * - 제거된 엔티티와 이전 client level의 ID는 남아 있지 않는다.
 *
 * THREADING:
 * - 렌더 스레드에서만 접근하고 변경한다.
 *
 * RECOVERY:
 * - 저장된 rider 엔티티가 없으면 WARN을 기록하고 내부 관계를 제거한다.
 * - Player가 필요한 이벤트는 생략한다.
 */
private val handledPassengerIdsByKartId =
    mutableMapOf<Int, MutableSet<Int>>()
```

### 상태 변경 함수

상태를 변경하는 함수에는 호출 전제와 정상 종료 후의 결과를 작성한다. 상태 선언부의 전체 불변식을 반복하지 않고 함수가 담당하는 전이만 명시한다.

```kotlin
/**
 * handled 관계에서 rider를 제거합니다.
 *
 * ENSURES:
 * - 정상 종료 시 riderId는 kartId의 handled set에 존재하지 않는다.
 * - set이 비면 kartId 키도 제거된다.
 *
 * @return 실제 handled 관계가 제거되었으면 `true`
 */
private fun removeHandledPassenger(kartId: Int, riderId: Int): Boolean
```

### 외부 진입점

패킷 핸들러, Mixin 주입점, Fabric 이벤트 콜백처럼 호출 시점이 중요한 함수에는 `REQUIRES:`와 `THREADING:`을 작성한다.

```kotlin
/**
 * REQUIRES:
 * - Vanilla의 passenger 처리가 끝난 TAIL 시점에 호출된다.
 *
 * ENSURES:
 * - 정상 종료 시 실제 Player가 확인된 관계만 handled 상태에 포함된다.
 * - 패킷에서 제거된 관계는 내부 탑승 상태에서도 제거된다.
 *
 * THREADING:
 * - 렌더 스레드에서 호출된다.
 */
fun onEntityMountPacket(...)
```

### 수명 주기 종료 함수

연결 종료, 월드 전환 또는 객체 무효화 함수에는 이벤트 발행 순서와 최종 정리 상태를 명시한다.

```text
ENSURES:
- DISMOUNT가 REMOVE보다 먼저 발행된다.
- REMOVE 콜백이 끝날 때까지 Kart는 유효하다.
- 종료 후 handled, pending, active 관계가 모두 비어 있다.
```

## 런타임 검증과 복구

주석만으로 불변식을 보장하지 않는다. 위반 원인과 복구 가능성에 따라 런타임 동작을 선택한다.

### 즉시 실패

호출 순서나 중복 초기화처럼 구현 코드만으로 결정되며 계속 실행하면 상태 손상이 확대되는 경우 `check`, `require` 또는 명시적인 예외를 사용한다.

```kotlin
check(!engineInitialized) { "kart engine is already initialized" }
```

### 로그 후 복구

패킷 순서, 엔티티 추적 종료 또는 다른 모드의 개입처럼 외부 상태에 의해 발생할 수 있고 안전한 정리가 가능한 경우 로그를 남기고 복구한다.

```kotlin
val rider = level.getEntity(riderId) as? Player
if (rider == null) {
    LOGGER.warn(
        "Handled kart passenger is missing during dismount: saddleId={}, riderId={}; " +
            "cleaning the relation without firing DISMOUNT",
        saddleId,
        riderId,
    )
}

KartManager.dismountRider(riderId)
removeHandledPassenger(saddleId, riderId)
```

복구 로그에는 가능한 범위에서 다음 내용을 포함한다.

- 위반된 상태와 처리 단계
- 관련 entity 또는 kart ID
- 생략된 동작
- 수행한 복구 동작

복구 가능한 단일 관계 오류에는 일반적으로 `WARN`을 사용한다. 동일한 전이에서 반복 로그가 발생하지 않도록 상태를 정리해야 한다.

### 정상적인 대기 상태

초기 동기화 중 아직 rider 엔티티가 생성되지 않은 경우처럼 설계상 허용된 상태에는 경고를 남기지 않는다. 관계를 처리 완료로 기록하지 않고 이후 입력에서 재시도한다.

```text
rider 엔티티가 아직 없음
→ handled 상태에 추가하지 않음
→ 동일 관계를 다시 관찰하면 재시도
→ WARN을 남기지 않음
```

## 이벤트와 내부 상태

이벤트 발행 여부와 내부 상태 정리는 분리해서 판단한다.

- 이벤트 계약에 필요한 객체가 없으면 해당 이벤트를 발행하지 않는다.
- 이벤트를 발행할 수 없어도 제거가 확인된 내부 관계는 반드시 정리한다.
- 이벤트 콜백에서 조회할 수 있어야 하는 상태는 콜백이 끝날 때까지 유효하게 유지한다.
- 하나의 상태 전이에 같은 이벤트가 두 번 발행되지 않도록 완료 상태를 별도로 추적한다.
- 이벤트 콜백이 예외를 던질 수 있는 경로에서는 필수 정리가 보장되어야 하는지 검토하고, 필요한 경우 `try`/`finally`를 사용한다.

## 리뷰 체크리스트

내부 상태나 수명 주기 코드를 변경할 때 다음 항목을 확인한다.

- [ ] 불변식이 상태 선언부에 정의되어 있는가?
- [ ] 모든 변경 경로가 불변식을 유지하는가?
- [ ] 엔티티 제거, 연결 종료 및 월드 전환 경로가 포함되어 있는가?
- [ ] 정상적인 pending 상태와 불변식 위반이 구분되는가?
- [ ] 위반 시 실패 또는 복구 정책이 명확한가?
- [ ] 복구 후 같은 경고가 반복되지 않는가?
- [ ] 이벤트 발행 순서와 콜백 중 상태 유효성이 유지되는가?
- [ ] 스레드 제약이 필요한 상태에 `THREADING:`이 작성되어 있는가?
- [ ] 관련 순서 조합과 제거 경로를 테스트하는가?
