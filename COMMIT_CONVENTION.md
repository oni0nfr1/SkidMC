# 커밋 규칙

이 프로젝트의 커밋 메시지는 [Conventional Commits](https://www.conventionalcommits.org/)를 기반으로 작성한다.
공개 API 변경과 구현 변경을 쉽게 구분할 수 있도록 scope에 변경 계층과 영역을 함께 명시한다.

## 기본 형식

```text
<type>(<layer>/<area>): <description>
```

예시:

```text
feat(api/events): add kart spectate events
fix(impl/tachometer): parse charge gauge correctly
refactor(internal/kart): simplify kart lifecycle tracking
```

- `type`은 변경의 성격을 나타낸다.
- `layer`는 변경이 외부에 미치는 영향을 나타낸다.
- `area`는 변경된 기능 또는 구성 요소를 나타낸다.
- `description`은 명령형 현재 시제로 간결하게 작성하며, 첫 글자는 소문자로 쓰고 마침표를 붙이지 않는다.

## Layer

### `api`

다른 모드에서 직접 사용하는 공개 API의 추가, 수정 또는 제거에 사용한다.
공개 클래스, 인터페이스, 함수, 프로퍼티, 이벤트 및 그 계약의 변경이 포함된다.

```text
feat(api/events): add kart removal event
fix(api/kart): correct KartRef nullability contract
refactor(api/tachometer)!: replace handle with access
```

### `impl`

공개 API의 형태는 유지되지만, API를 통해 관찰되는 동작이나 결과가 달라지는 구현 변경에 사용한다.
새로운 엔진 지원, 파싱 결과 변경, 이벤트 호출 시점 수정 등이 포함된다.

```text
feat(impl/engine): support charge engine metadata
fix(impl/events): prevent duplicate mount events
```

### `internal`

공개 API와 외부에서 관찰되는 동작에 영향을 주지 않는 내부 변경에 사용한다.
내부 구조 정리, 빌드 설정, 테스트 및 개발 도구 변경 등이 포함된다.

```text
refactor(internal/events): simplify callback dispatch
perf(internal/tachometer): cache parser patterns
build(internal/gradle): update publishing configuration
test(internal/kart): cover stale reference handling
```

## Type

주로 다음 type을 사용한다.

| Type | 용도 |
| --- | --- |
| `feat` | 새로운 기능 또는 동작 추가 |
| `fix` | 잘못된 동작 수정 |
| `refactor` | 외부 동작을 바꾸지 않는 코드 구조 개선 |
| `perf` | 성능 개선 |
| `docs` | 문서 변경 |
| `test` | 테스트 추가 또는 수정 |
| `build` | 빌드 시스템이나 의존성 변경 |
| `ci` | CI 설정 및 스크립트 변경 |
| `chore` | 위 분류에 해당하지 않는 유지보수 작업 |

`feat`와 `fix`를 내부 코드의 추가·수정이라는 이유만으로 사용하지 않는다.
외부 동작이 변하지 않는 구조 개선은 `refactor(internal/...)`처럼 실제 변경 성격에 맞는 type을 사용한다.

## Area

`area`에는 변경 대상을 나타내는 짧고 일관된 이름을 사용한다.
필요한 경우 새 영역을 추가할 수 있다.

```text
attr
engine
events
kart
tachometer
gradle
publishing
```

여러 영역에 걸친 변경이라면 커밋을 나누는 것을 우선한다.
분리하기 어렵다면 변경의 중심이 되는 영역 하나를 선택한다.

## 호환성을 깨는 변경

기존 사용자의 수정이 필요한 변경에는 type 또는 scope 뒤에 `!`를 붙인다.
변경 내용과 마이그레이션 방법이 단순하지 않다면 본문과 `BREAKING CHANGE` footer에 자세히 기록한다.

```text
refactor(api/kart)!: replace KartRef handle with access

BREAKING CHANGE: Replace direct handle access with KartRef.access().
```

호환성을 깨는 변경은 커밋 제목만 읽어도 영향 범위를 알 수 있도록 반드시 `api` layer를 명시한다.

## 본문과 Footer

제목만으로 이유나 영향을 충분히 설명할 수 없을 때는 빈 줄 다음에 본문을 작성한다.
이슈를 연결할 때는 footer를 사용한다.
`internal` 변경을 Modrinth 변경사항에 포함해야 한다면 `Release-Note` footer에 사용자에게 보여 줄 문구를 작성한다.

```text
fix(impl/events): delay mount event until metadata is ready

The early event could expose a kart before its engine metadata was applied.

Closes #42
```

```text
fix(internal/gradle): correct release artifact selection

Release-Note: Fixed an issue that could publish the development JAR.
```

## Modrinth 변경사항 작성 기준

Modrinth 버전 변경사항에는 다음 커밋을 기본적으로 포함한다.

1. `api`의 `feat`, `fix`, `refactor`
2. `impl`의 `feat`, `fix`
3. `Release-Note` footer가 있는 `internal` 커밋

API 변경을 먼저, 관찰 가능한 구현 변경을 다음에, 내부 변경을 마지막에 작성한다.
각 분류 안에서는 커밋 순서를 유지한다.
`internal` 커밋은 type과 관계없이 기본적으로 제외하고, `Release-Note` footer가 있을 때만 해당 footer의 문구를 포함한다.
`docs`, `test`, `build`, `ci`, `chore`와 사용자에게 영향이 없는 내부 정리는 기본적으로 생략한다.
호환성을 깨는 변경은 다른 항목보다 먼저 배치하고 `BREAKING`으로 명확히 표시한다.
