# Glasscal - 글래스모피즘 캘린더 앱 개발 가이드

## 프로젝트 개요
글래스모피즘(Glassmorphism) 디자인을 적용한 Android 기반 캘린더 애플리케이션

### 핵심 기능
- 월간 캘린더 Grid 뷰 (글래스모피즘 디자인)
- 날짜별 할일 관리 (제목, 내용, 이미지)
- 할일 미리보기 (텍스트 말줄임 처리)
- 이미지 배경 (불투명 효과)
- OAuth 로그인 (Google, Github)
- 로컬 데이터 저장 (Firebase 확장 가능)

## 기술 스택

### Core
- **언어**: Kotlin
- **최소 SDK**: 24 (Android 7.0)
- **타겟 SDK**: 36
- **View System**: ViewBinding

### 아키텍처
- **패턴**: MVVM (Model-View-ViewModel)
- **데이터 레이어**: Room Database (로컬 저장)
- **UI 레이어**: Jetpack Compose 또는 XML Layouts

### 필수 라이브러리
```gradle
// Architecture Components
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0"
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.7.0"
implementation "androidx.lifecycle:lifecycle-runtime-ktx:2.7.0"

// Room Database
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"

// Coroutines
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3"

// Material Design
implementation "com.google.android.material:material:1.11.0"

// Image Loading - Coil
implementation "io.coil-kt:coil:2.5.0"

// BlurView for Glassmorphism effect
implementation "com.github.Dimezis:BlurView:version-2.0.3"

// Activity/Fragment KTX
implementation "androidx.activity:activity-ktx:1.8.2"
implementation "androidx.fragment:fragment-ktx:1.6.2"

// Firebase (추후)
// implementation "com.google.firebase:firebase-firestore-ktx"
// implementation "com.google.firebase:firebase-auth-ktx"
```

**주의사항**: BlurView 라이브러리를 사용하려면 settings.gradle.kts에 JitPack repository를 추가해야 합니다:
```gradle
repositories {
    google()
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```

## 프로젝트 구조

```
app/src/main/java/com/example/glasscal/
├── data/
│   ├── local/
│   │   ├── entity/        # Room Entity 클래스
│   │   ├── dao/           # Room DAO 인터페이스
│   │   └── database/      # Room Database 클래스
│   ├── model/             # 데이터 모델
│   └── repository/        # Repository 패턴
├── ui/
│   ├── calendar/          # 캘린더 화면
│   ├── task/              # 할일 등록/수정/목록 화면
│   │   ├── AddTaskBottomSheet.kt      # 할일 추가/수정
│   │   └── TaskListBottomSheet.kt     # 할일 목록 표시
│   ├── auth/              # 로그인 화면
│   ├── settings/          # 설정 화면
│   └── adapter/           # RecyclerView Adapters
│       ├── CalendarAdapter.kt         # 캘린더 그리드
│       └── TaskListAdapter.kt         # 할일 목록
├── viewmodel/             # ViewModel 클래스
├── util/                  # 유틸리티 클래스
└── MainActivity.kt
```

## 데이터 모델

### Task Entity
```kotlin
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val date: Long,  // Unix timestamp
    val imageUri: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

## UI/UX 가이드라인

### 글래스모피즘 디자인 원칙
1. **배경 블러**: `backdrop-filter: blur(10px)` 효과
2. **반투명**: `alpha = 0.7-0.9` 범위
3. **테두리**: 얇은 흰색/밝은 테두리
4. **그림자**: 부드러운 shadow 효과
5. **색상**: 밝은 배경에 흰색/파스텔 톤

### 캘린더 셀 디자인
- Grid Layout (7열 x 5-6행)
- 각 셀: 반투명 배경 + 블러 효과
- 할일 미리보기: 최대 2줄, 말줄임(...)
- 이미지 배경: 불투명도 0.3-0.5

### 할일 등록 모달
- BottomSheet 또는 Dialog
- 입력 필드: 제목, 내용, 이미지 첨부
- 이미지 선택: Gallery/Camera

## 개발 순서 (할일 목록)

### Phase 1: 기본 설정 및 데이터 레이어 ✅ 완료
- [x] Gradle 의존성 추가 (Room, ViewModel, Coroutines, Coil)
- [x] Room Database 설정
  - [x] Task Entity 생성
  - [x] TaskDao 인터페이스 생성
  - [x] AppDatabase 클래스 생성
- [x] Repository 패턴 구현
  - [x] TaskRepository 생성
- [x] ViewModel 생성
  - [x] CalendarViewModel 생성

### Phase 2: UI 레이아웃 구현 ✅ 완료
- [x] 캘린더 메인 화면 레이아웃
  - [x] Grid 레이아웃 (RecyclerView with GridLayoutManager)
  - [x] 캘린더 셀 아이템 레이아웃
  - [x] 글래스모피즘 스타일 적용 (drawable, styles)
- [x] 할일 등록 화면
  - [x] BottomSheet 레이아웃
  - [x] 제목, 내용, 이미지 입력 필드
- [x] 플로팅 액션 버튼
  - [x] 메뉴 아이템 (로그인, 설정)

### Phase 3: 캘린더 기능 구현 ✅ 완료
- [x] 캘린더 데이터 로직
  - [x] 월별 날짜 계산 유틸리티
  - [x] 날짜별 할일 그룹핑
- [x] RecyclerView Adapter 구현
  - [x] CalendarAdapter 생성
  - [x] ViewHolder with 글래스모피즘 디자인
- [x] 할일 미리보기 표시
  - [x] 텍스트 말줄임 처리
  - [x] 이미지 배경 적용 (Coil)

### Phase 4: 할일 관리 기능 ✅ 완료
- [x] 할일 추가 기능
  - [x] BottomSheet 표시
  - [x] 이미지 선택 (Gallery)
  - [x] 데이터 저장
- [x] 할일 수정/삭제 기능
  - [x] 셀 클릭 시 할일 목록 표시
  - [x] 할일 목록에서 수정/삭제
  - [x] 수정 모드에서 삭제 버튼
- [x] 할일 목록 조회
  - [x] 날짜별 필터링
  - [x] TaskListBottomSheet로 목록 표시
  - [x] LiveData/Flow 연동

### Phase 5: UI 폴리싱 ✅ 완료
- [x] 글래스모피즘 디자인 기본 적용
  - [x] 반투명 레이어
  - [x] 테두리 및 그림자
- [x] 글래스모피즘 고급 효과
  - [x] 배경 블러 효과 (BlurView 라이브러리 사용)
  - [x] 애니메이션 추가 (클릭, 페이드, 슬라이드)
  - [x] 다크모드 대응 (colors-night.xml, drawable-night)

### Phase 6: 로그인/설정 화면 (디자인만) ✅ 완료
- [x] 로그인 화면 레이아웃
  - [x] OAuth 버튼 (Google, Github)
  - [x] 글래스모피즘 디자인 적용
- [x] 설정 화면 레이아웃
  - [x] 사용자 프로필 (더미)
  - [x] 데이터 관리 옵션
  - [x] 앱 정보 표시

### Phase 7: 네비게이션 및 통합 ✅ 완료
- [x] MainActivity 업데이트
- [x] Navigation Component 설정
- [x] Fragment 간 이동 구현
- [x] FAB 메뉴 기능 구현

## 최근 변경 사항

### 2025-12-03: 할일 목록 확인 기능 구현 ✅

**구현 내용**: 날짜 클릭 시 해당 날짜의 모든 할일을 리스트로 표시하는 기능 추가

**변경 사항**:

1. **TaskListBottomSheet 추가** (`ui/task/TaskListBottomSheet.kt`)
   - 날짜 클릭 시 해당 날짜의 모든 할일을 리스트로 표시
   - "+ 새 할일" 버튼으로 새 할일 추가 가능
   - 할일 클릭으로 수정, 롱 클릭으로 삭제
   - 빈 상태 메시지 표시 ("등록된 할일이 없습니다")

2. **TaskListAdapter 추가** (`ui/adapter/TaskListAdapter.kt`)
   - RecyclerView로 할일 목록 표시
   - 할일 제목, 내용, 이미지 썸네일, 생성 시간 표시
   - 클릭/롱클릭 이벤트 처리
   - DiffUtil을 사용한 효율적인 리스트 업데이트

3. **CalendarFragment 수정**
   - `onDateClick()` 메서드를 수정하여 `TaskListBottomSheet` 열도록 변경
   - 기존: 날짜 클릭 → 바로 할일 추가
   - 변경: 날짜 클릭 → 할일 목록 표시 → "+ 새 할일" 버튼으로 추가

4. **AddTaskBottomSheet 수정**
   - 수정 모드에서 삭제 버튼 표시 (`btnDelete`)
   - `deleteTask()` 메서드 추가
   - 삭제 확인 다이얼로그 추가 (MaterialAlertDialog)

**새로운 레이아웃 파일**:
- `bottom_sheet_task_list.xml`: 할일 목록 BottomSheet 레이아웃
- `item_task_list.xml`: 할일 목록 아이템 레이아웃

**주요 파일 변경**:
- `CalendarFragment.kt:138-144` - TaskListBottomSheet 열기
- `TaskListBottomSheet.kt` - 전체 파일 (신규)
- `TaskListAdapter.kt` - 전체 파일 (신규)
- `AddTaskBottomSheet.kt:146, 278-290` - 삭제 기능 추가
- `bottom_sheet_add_task.xml:159-167` - 삭제 버튼 추가

**사용자 플로우**:
1. 날짜 클릭 → `TaskListBottomSheet` 열림
2. 할일 목록 표시 (없으면 빈 상태 메시지)
3. "+ 새 할일" 버튼 클릭 → `AddTaskBottomSheet` 열림
4. 할일 항목 클릭 → 수정 모드로 `AddTaskBottomSheet` 열림
5. 할일 항목 롱 클릭 → 삭제 확인 다이얼로그

**개발자 노트**:
- `TaskListBottomSheet`는 `CalendarViewModel.getTasksByDate()`를 사용하여 특정 날짜의 할일 조회
- LiveData를 observe하여 실시간으로 할일 목록 업데이트
- 삭제 기능은 두 곳에서 가능: 1) 수정 화면의 삭제 버튼, 2) 할일 목록의 롱 클릭

### 2025-12-02: 할일 저장 및 자동 새로고침 개선

**문제점**: CalendarViewModel의 monthTasks가 초기화 시점에만 설정되어, 월 변경 시 할일 목록이 자동으로 업데이트되지 않음

**해결 방법**:
- `monthTasks`를 `LiveData`에서 `StateFlow`로 변경
- `combine` 연산자를 사용하여 `currentYear`와 `currentMonth`가 변경될 때마다 자동으로 할일 목록 새로고침
- CalendarFragment도 StateFlow 수집 방식으로 변경

**관련 파일**:
- `CalendarViewModel.kt:32-49` - monthTasks StateFlow 구현
- `CalendarFragment.kt:97-116` - StateFlow 수집 로직

### 2. 이미지 갤러리 접근 권한 요청 추가
**문제점**: 이미지 선택 시 권한 요청 없이 갤러리에 접근하여 Android 13+ 에서 오류 발생 가능

**해결 방법**:
- Android 13 (API 33) 이상: `READ_MEDIA_IMAGES` 권한 요청
- Android 12 이하: `READ_EXTERNAL_STORAGE` 권한 요청
- `ActivityResultContracts.RequestPermission()` 사용
- `ACTION_OPEN_DOCUMENT` 사용 및 영구 URI 권한 부여 (`takePersistableUriPermission`)

**관련 파일**:
- `AddTaskBottomSheet.kt:3-14` - 권한 관련 import 추가
- `AddTaskBottomSheet.kt:56-71` - 권한 요청 launcher 추가
- `AddTaskBottomSheet.kt:145-191` - 권한 확인 및 요청 로직

**사용자 경험**:
1. "이미지 선택" 버튼 클릭
2. 권한이 없으면 권한 요청 다이얼로그 표시
3. 권한 거부 시 Snackbar로 안내 메시지 표시
4. 권한 승인 시 갤러리 앱 실행

### 3. 설정 화면 단순화
**변경 사항**: 알림 및 다크모드 스위치 제거

**이유**:
- 알림 기능: 추후 Firebase Cloud Messaging과 함께 구현 예정
- 다크모드: 시스템 설정에 자동으로 따르도록 구현되어 있음 (values-night 리소스)

**관련 파일**:
- `fragment_settings.xml:121-127` - 앱 설정 섹션 단순화
- `SettingsFragment.kt:46-48` - 스위치 리스너 제거

**현재 설정 화면 구성**:
- 사용자 프로필 (로그인 버튼)
- 앱 설정 (추후 구현 예정 안내)
- 데이터 관리 (동기화, 내보내기, 삭제)
- 정보 (앱 버전)

## 구현된 기능 상세 설명

### 글래스모피즘 효과 구현
1. **BlurView 라이브러리**: Dimezis의 BlurView를 사용하여 실시간 배경 블러 효과 구현
   - 캘린더 셀에 적용 (`item_calendar_cell.xml`)
   - 헤더에 적용 (`fragment_calendar.xml`)
   - 블러 반경: 20f (조정 가능)

2. **애니메이션**
   - `scale_up.xml`: 셀 클릭 시 확대 효과
   - `scale_down.xml`: 셀 클릭 해제 시 축소 효과
   - `fade_in.xml`: BottomSheet 페이드 인 효과
   - `slide_up.xml`: BottomSheet 슬라이드 업 효과 (추후 활용 가능)

3. **다크모드 대응**
   - `values-night/colors.xml`: 다크모드 색상 정의
   - `drawable-night/bg_gradient_main.xml`: 다크모드 그라디언트 배경
   - 시스템 설정에 따라 자동 전환

### 사용법
- **BlurView 초기화** (Fragment/Activity에서):
```kotlin
val decorView = requireActivity().window.decorView as? ViewGroup
decorView?.let {
    val windowBackground: Drawable? = decorView.background

    blurView.setupWith(decorView, RenderScriptBlur(requireContext()))
        .setFrameClearDrawable(windowBackground)
        .setBlurRadius(20f)
        .setBlurAutoUpdate(true)
}
```

- **애니메이션 적용** (ViewHolder/Fragment에서):
```kotlin
val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
view.startAnimation(scaleUp)
```

## 추후 작업 사항 (백엔드 개발 후)

### Firebase 연동
- [ ] Firebase 프로젝트 설정
- [ ] Firebase Authentication
  - [ ] Google OAuth 연동
  - [ ] Github OAuth 연동
- [ ] Firestore Database
  - [ ] Task 데이터 동기화
  - [ ] 로컬-원격 동기화 로직
- [ ] Firebase Storage
  - [ ] 이미지 업로드/다운로드

### 고급 기능
- [ ] 푸시 알림 (할일 리마인더)
- [ ] 위젯 (홈화면 캘린더)
- [ ] 공유 기능 (할일 공유)
- [ ] 검색 기능
- [ ] 필터링/정렬

## 코딩 컨벤션

### Kotlin 스타일
- camelCase for variables/functions
- PascalCase for classes
- UPPER_SNAKE_CASE for constants

### 파일 네이밍
- Activity: `*Activity.kt`
- Fragment: `*Fragment.kt`
- ViewModel: `*ViewModel.kt`
- Adapter: `*Adapter.kt`
- Repository: `*Repository.kt`

### 주석
- 복잡한 로직에는 주석 필수
- 공개 API에는 KDoc 작성

## 테스트 전략
- Unit Test: ViewModel, Repository
- UI Test: Espresso (선택사항)

## 참고 자료
- [Material Design - Glassmorphism](https://m3.material.io/)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
