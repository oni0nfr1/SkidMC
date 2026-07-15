# SkidMC

KartRider: Minecraft 클라이언트 모드 개발용 Fabric API  
A client-side Fabric API for KartRider: Minecraft mod development

[한국어](#한국어) · [English](#english)

## 한국어

SkidMC는 **KartRider: Minecraft**용 클라이언트 모드와 커스텀 HUD 개발을 지원하는 Fabric API입니다.
엔티티 메타데이터와 액션바 메시지를 직접 처리하지 않고도 카트, 라이더, 엔진 및 타코미터 정보를 구조화된 API로 사용할 수 있습니다.

### 주요 기능

- 카트 생성, 제거, 탑승, 하차 및 관전 상태 이벤트
- 카트와 라이더의 메타데이터 및 어트리뷰트 변경 감지
- 공식 엔진과 호환용 엔진 타입 식별
- 드리프트, 부스터, 듀얼 부스터, 드래프트 및 순간 부스터 상태 조회
- 타코미터 액션바 파싱
  - 속도
  - 부스터 개수와 게이지
  - RPM과 기어
  - 익시드 및 차저 게이지
  - F1 ERS 및 마리오 카트 터보 게이지
- Fabric 스타일 이벤트 API
- Kotlin 및 Java에서 사용할 수 있는 공개 API

### 대상 사용자

SkidMC는 주로 모드 개발자를 위한 API이며, 단독으로 새로운 콘텐츠나 눈에 띄는 게임플레이 기능을 추가하지 않습니다.
일반 사용자는 다른 모드가 SkidMC를 필수 의존성으로 요구할 때 설치하면 됩니다.

### 요구사항

- Minecraft 1.21.5
- Fabric Loader 0.18.4 이상
- Fabric API
- Fabric Language Kotlin 1.13.8+kotlin.2.3.0 이상
- 클라이언트 측 설치

### 설치

SkidMC와 필수 의존성을 Minecraft 인스턴스의 `mods` 폴더에 넣으세요.
SkidMC를 요구하는 모드가 있다면 해당 모드도 함께 설치해야 합니다.

---

## English

SkidMC is a client-side Fabric API for developing mods and custom HUDs for **KartRider: Minecraft**.
It provides structured access to kart, rider, engine, and tachometer data without requiring each mod to process entity metadata and action-bar messages directly.

### Features

- Events for kart spawning, removal, mounting, dismounting, and spectating
- Kart and rider metadata and attribute updates
- Identification of official and compatibility engine types
- Access to drifting, nitro, dual boost, draft, and instant boost states
- Tachometer action-bar parsing for:
  - Speed
  - Nitro count and gauge
  - RPM and gear
  - Exceed and charger gauges
  - F1 ERS and Mario Kart turbo gauge
- Fabric-style event APIs
- Public APIs usable from Kotlin and Java

### Who is this for?

SkidMC is primarily intended for mod developers and does not add significant standalone content or gameplay features.
Regular players only need to install it when another mod lists SkidMC as a required dependency.

### Requirements

- Minecraft 1.21.5
- Fabric Loader 0.18.4 or later
- Fabric API
- Fabric Language Kotlin 1.13.8+kotlin.2.3.0 or later
- Client-side installation

### Installation

Place SkidMC and its required dependencies in the Minecraft instance's `mods` directory.
If another mod requires SkidMC, install that mod alongside it.

---

## Links

- [Source code](https://github.com/oni0nfr1/SkidMC)
- [Issue tracker](https://github.com/oni0nfr1/SkidMC/issues)
- [Commit convention](COMMIT_CONVENTION.md)

## License

SkidMC is licensed under the MIT License.
