# Crew Wiki Asset Migration

`crew-wiki-next/client`의 폰트 자산을 Compose Multiplatform 리소스로 옮긴 기록입니다.

## 현재 기준 경로

- 공용 UI 리소스: `shared/src/commonMain/composeResources`
- Android 앱 전용 리소스: `androidApp/src/main/res`

이번 단계에서는 공용 UI에서 바로 쓸 수 있도록 `composeResources/font` 기준으로 정리했습니다.

## 복사 대상

| 원본 | KMP 대상 | 상태 | 비고 |
| --- | --- | --- | --- |
| `public/fonts/BMHANNAProOTF.otf` | `shared/src/commonMain/composeResources/font/bm_hanna_pro.otf` | 사용 가능 | Compose Multiplatform 폰트로 생성 확인 |
| `Pretendard-1/public/variable/PretendardVariable.ttf` | `shared/src/commonMain/composeResources/font/pretendard_variable.ttf` | 사용 가능 | Variable font 원본 |
| `Pretendard-1/public/static/Pretendard-Regular.otf` | `shared/src/commonMain/composeResources/font/pretendard_regular.otf` | 사용 가능 | 기본 본문 폰트 |
| `Pretendard-1/public/static/Pretendard-Medium.otf` | `shared/src/commonMain/composeResources/font/pretendard_medium.otf` | 사용 가능 | Medium weight |
| `Pretendard-1/public/static/Pretendard-SemiBold.otf` | `shared/src/commonMain/composeResources/font/pretendard_semibold.otf` | 사용 가능 | SemiBold weight |
| `Pretendard-1/public/static/Pretendard-Bold.otf` | `shared/src/commonMain/composeResources/font/pretendard_bold.otf` | 사용 가능 | Bold weight |

## 검증 결과

- `composeResources/font` 아래의 `ttf`, `otf`는 Compose Multiplatform 폰트 리소스로 인식됩니다.
- 생성된 accessor에서 아래 리소스를 확인했습니다.
  - `Res.font.bm_hanna_pro`
  - `Res.font.pretendard_variable`
  - `Res.font.pretendard_regular`
  - `Res.font.pretendard_medium`
  - `Res.font.pretendard_semibold`
  - `Res.font.pretendard_bold`

## 적용 상태

- `CrewWikiTheme`에서 기본 타이포그래피는 `Pretendard` 정적 weight 세트를 사용합니다.
- `BMHANNA`는 display typography에 연결했습니다.
- `pretendard_variable.ttf`는 리소스로 추가만 되어 있고, 현재 테마에서는 직접 사용하지 않습니다.

## 다음 작업 후보

1. 화면별 텍스트 스타일을 정리해서 `BMHANNA`와 `Pretendard` 사용 기준 고정
2. 웹 아이콘 자산을 `composeResources` 기준으로 이관
