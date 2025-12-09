# Glasscal 개발 과정 및 기술적 고민

> **글래스모피즘 디자인을 적용한 Android 캘린더 애플리케이션**
> Android 기반 일정 관리 앱 개발 프로젝트

---

## 📑 목차

1. [프로젝트 개요](#1-프로젝트-개요)
2. [기술 스택](#2-기술-스택)
3. [주요 기능](#3-주요-기능)
4. [기술적 고민과 해결 방법](#4-기술적-고민과-해결-방법)
5. [아키텍처 설계](#5-아키텍처-설계)
6. [배운 점과 느낀 점](#6-배운-점과-느낀-점)

---

## 1. 프로젝트 개요

### 1.1 프로젝트 소개

**Glasscal**은 현대적인 **글래스모피즘(Glassmorphism)** 디자인을 적용한 Android 캘린더 애플리케이션입니다.

- **개발 기간**: 약 2-3주
- **개발 인원**: 1명 (개인 프로젝트)
- **플랫폼**: Android (최소 SDK 24, 타겟 SDK 36)

### 1.2 개발 동기

- 기존 캘린더 앱들의 단조로운 디자인에서 벗어나 **시각적으로 아름다운 UI** 구현
- 최신 디자인 트렌드인 **글래스모피즘** 적용 경험
- **오프라인 우선** 설계와 **클라우드 동기화**의 조화
- Android 네이티브 개발 역량 강화

### 1.3 프로젝트 목표

✅ 유리처럼 투명하고 우아한 UI/UX 제공
✅ 직관적인 일정 관리 기능
✅ 로컬 저장소와 클라우드 동기화 지원
✅ 부드러운 애니메이션과 상호작용

---

## 2. 기술 스택

### 2.1 개발 환경

| 항목 | 내용 |
|------|------|
| **언어** | Kotlin |
| **IDE** | Android Studio |
| **빌드 도구** | Gradle (Kotlin DSL) |
| **최소 SDK** | 24 (Android 7.0) |
| **타겟 SDK** | 36 |

### 2.2 핵심 라이브러리

#### UI & 디자인
- **BlurView** (eightbitlab): 글래스모피즘의 핵심인 블러 효과 구현
- **Material Components**: Google Material Design 구성 요소
- **CardView**: 카드 형태의 레이아웃
- **Coil**: 이미지 로딩 및 캐싱

#### 아키텍처 & 데이터
- **Room Database**: 로컬 데이터 저장 (SQLite 기반)
- **ViewModel & LiveData**: MVVM 아키텍처 구현
- **Kotlin Coroutines & Flow**: 비동기 처리 및 반응형 데이터 스트림

#### 네트워크 & 동기화
- **Retrofit**: REST API 통신
- **OkHttp**: HTTP 클라이언트
- **Gson**: JSON 직렬화/역직렬화

#### 기타
- **RecyclerView**: 효율적인 리스트 표시
- **Material BottomSheet**: 하단 시트 UI 패턴
- **SharedPreferences**: 간단한 설정 저장

---

## 3. 주요 기능

### 3.1 기능 목록

| 기능 | 설명 | 구현 상태 |
|------|------|-----------|
| **월간 캘린더 뷰** | 7x5~6 그리드 형태의 캘린더 | ✅ |
| **할일 관리** | 날짜별 할일 추가, 수정, 삭제 | ✅ |
| **이미지 첨부** | 할일에 이미지 추가 및 배경 표시 | ✅ |
| **할일 미리보기** | 캘린더 셀에 할일 제목과 개수 표시 | ✅ |
| **클라우드 동기화** | 서버와 데이터 동기화 | ✅ |
| **글래스모피즘 디자인** | 블러 효과와 반투명 UI | ✅ |
| **애니메이션** | 부드러운 화면 전환 및 상호작용 | ✅ |

### 3.2 화면 구성

```
┌─────────────────────────────────┐
│    MainActivity (Navigation)    │
└─────────────────────────────────┘
           │
           ├─── CalendarFragment (메인 캘린더)
           │         │
           │         ├─── TaskListBottomSheet (할일 목록)
           │         │         │
           │         │         └─── AddTaskBottomSheet (할일 추가/수정)
           │         │
           │         └─── AddTaskBottomSheet (새 할일 추가)
           │
           └─── SettingsFragment (설정 및 동기화)
```

---

## 4. 기술적 고민과 해결 방법

### 4.1 글래스모피즘 디자인 구현

#### 🤔 고민: "어떻게 진짜 유리처럼 보이게 만들까?"

글래스모피즘의 핵심은 **블러(Blur) 효과**입니다. 단순히 투명도만 조절하면 유리 느낌이 나지 않습니다.

#### 💡 해결 방법

**1. BlurView 라이브러리 사용**

Android 기본 API로는 실시간 블러 구현이 어렵습니다. `eightbitlab.com.blurview.BlurView` 라이브러리를 사용했습니다.

```kotlin
// CalendarFragment.kt
private fun setupBlurView() {
    val decorView = requireActivity().window.decorView as? ViewGroup
    decorView?.let {
        val windowBackground: Drawable? = decorView.background

        binding.headerBlurView.setupWith(decorView, RenderScriptBlur(requireContext()))
            .setFrameClearDrawable(windowBackground)
            .setBlurRadius(20f)              // 블러 반경
            .setBlurAutoUpdate(true)          // 자동 업데이트
    }
}
```

**2. 계층 구조 설계**

```xml
<FrameLayout>
    <!-- 1. 배경 이미지/그라디언트 -->
    <ImageView android:src="@drawable/background" />

    <!-- 2. 블러 뷰 (배경을 흐리게) -->
    <BlurView android:background="@drawable/bg_calendar_cell" />

    <!-- 3. 콘텐츠 (텍스트, 아이콘 등) -->
    <LinearLayout>
        <TextView />
    </LinearLayout>
</FrameLayout>
```

**3. 그라디언트와 투명도 조합**

```xml
<!-- bg_calendar_cell.xml -->
<layer-list>
    <!-- 그림자 레이어 -->
    <item>
        <shape android:shape="rectangle">
            <solid android:color="#33004585" />  <!-- 20% 불투명 -->
            <corners android:radius="24dp" />
        </shape>
    </item>

    <!-- 유리 배경 레이어 -->
    <item>
        <shape android:shape="rectangle">
            <gradient
                android:angle="270"
                android:startColor="#80FFFFFF"    <!-- 50% 흰색 -->
                android:endColor="#33FFFFFF"      <!-- 20% 흰색 -->
                android:type="linear" />
            <corners android:radius="24dp" />
            <stroke
                android:width="1.5dp"
                android:color="#CCFFFFFF" />       <!-- 밝은 테두리 -->
        </shape>
    </item>
</layer-list>
```

**4. 배경 그라디언트 선택**

처음에는 오렌지/노란색 톤을 사용했으나, 유리 느낌이 나지 않았습니다.
→ **파란색 → 보라색 → 인디고** 계열로 변경하여 자연스러운 밝은 톤 구현

```xml
<!-- colors.xml -->
<color name="gradient_start">#E3F2FD</color>    <!-- Light blue -->
<color name="gradient_middle">#F3E5F5</color>   <!-- Light purple -->
<color name="gradient_end">#E8EAF6</color>      <!-- Light indigo -->
```

#### 📊 결과

✅ 실시간 블러 효과로 진짜 유리 느낌 구현
✅ 성능 최적화 (BlurView의 하드웨어 가속 활용)
✅ 일관된 디자인 시스템 구축

---

### 4.2 Bottom Sheet 선택 이유

#### 🤔 고민: "할일 목록을 어떻게 보여줄까?"

옵션:
- ❌ 새로운 Activity 열기 → 화면 전환이 부담스럽고 컨텍스트 유지 어려움
- ❌ Dialog 사용 → 작은 화면에서 답답함
- ✅ **Bottom Sheet** → 모달 UI이면서 컨텍스트 유지, 자연스러운 UX

#### 💡 해결 방법

**BottomSheetDialogFragment 활용**

```kotlin
// TaskListBottomSheet.kt
class TaskListBottomSheet : BottomSheetDialogFragment() {

    private fun setupBottomSheetBehavior() {
        dialog?.setOnShowListener { dialogInterface ->
            val bottomSheetDialog = dialogInterface as? BottomSheetDialog
            val bottomSheet = bottomSheetDialog?.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet
            )

            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = STATE_EXPANDED       // 완전히 펼쳐진 상태
                behavior.skipCollapsed = true         // collapsed 상태 건너뛰기
            }
        }
    }
}
```

**장점:**
1. **컨텍스트 유지**: 캘린더 화면이 뒤에 보이면서 사용자가 어느 날짜의 할일인지 인지 가능
2. **자연스러운 애니메이션**: 하단에서 슬라이드 업
3. **백 제스처 지원**: 아래로 스와이프하면 닫힘
4. **유연한 높이**: 콘텐츠에 따라 자동으로 높이 조절

#### 📊 결과

✅ 직관적인 UX
✅ 부드러운 화면 전환
✅ Material Design 가이드라인 준수

---

### 4.3 이미지와 데이터 로컬 저장

#### 🤔 고민: "이미지를 어떻게 저장하고 관리할까?"

옵션:
- ❌ 이미지를 Base64로 인코딩하여 DB에 저장 → DB 크기 급증, 성능 저하
- ❌ 이미지를 내부 저장소에 복사 → 중복 저장, 용량 낭비
- ✅ **Content URI 사용 + Persistable Permission** → 원본 참조, 효율적

#### 💡 해결 방법

**1. Room Database 설계**

```kotlin
// Task.kt (Entity)
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String,
    val content: String,
    val date: Long,           // timestamp

    val imageUri: String?,    // 이미지 URI를 문자열로 저장

    val createdAt: Long,
    val updatedAt: Long
)
```

**2. Persistable URI Permission 획득**

```kotlin
// AddTaskBottomSheet.kt
private val imagePickerLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        result.data?.data?.let { uri ->
            try {
                // 영구 권한 부여
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                selectedImageUri = uri
                showImagePreview(uri)
            } catch (e: SecurityException) {
                // 권한 실패 처리
                Snackbar.make(
                    binding.root,
                    "이미지 권한 설정에 문제가 있습니다.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }
}
```

**3. ACTION_OPEN_DOCUMENT 사용**

```kotlin
private fun openImagePicker() {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
        addCategory(Intent.CATEGORY_OPENABLE)
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
    }
    imagePickerLauncher.launch(intent)
}
```

**왜 `ACTION_OPEN_DOCUMENT`를 사용했나?**
- `ACTION_GET_CONTENT`: 임시 권한만 부여 (앱 재시작 시 권한 소멸)
- `ACTION_OPEN_DOCUMENT`: 영구 권한 부여 가능 (앱 재시작 후에도 유지)

**4. 이미지 로딩 최적화**

```kotlin
// Coil 라이브러리 사용
binding.ivImagePreview.load(uri) {
    crossfade(true)                  // 부드러운 전환
    allowHardware(false)             // BlurView와 호환성 위해 비활성화
    placeholder(R.drawable.bg_calendar_cell)
    error(R.drawable.bg_calendar_cell)
}
```

#### 📊 결과

✅ 메모리 효율적 (원본 참조)
✅ 앱 재시작 후에도 이미지 유지
✅ 빠른 로딩 속도 (Coil 캐싱)

---

### 4.4 월 변경 애니메이션 구현

#### 🤔 고민: "월이 바뀔 때 어떻게 부드럽게 전환할까?"

단순히 데이터만 교체하면 갑작스러운 변화로 사용자 경험이 좋지 않습니다.

#### 💡 해결 방법

**1. 리소스 애니메이션 정의**

```xml
<!-- res/anim/fade_in.xml -->
<alpha xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="300"
    android:fromAlpha="0.0"
    android:toAlpha="1.0" />

<!-- res/anim/scale_up.xml -->
<scale xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="150"
    android:fromXScale="1.0"
    android:fromYScale="1.0"
    android:toXScale="0.95"
    android:toYScale="0.95"
    android:pivotX="50%"
    android:pivotY="50%" />

<!-- res/anim/scale_down.xml -->
<scale xmlns:android="http://schemas.android.com/apk/res/android"
    android:duration="150"
    android:fromXScale="0.95"
    android:fromYScale="0.95"
    android:toXScale="1.0"
    android:toYScale="1.0"
    android:pivotX="50%"
    android:pivotY="50%" />
```

**2. 셀 클릭 애니메이션**

```kotlin
// CalendarAdapter.kt
cellCard.setOnClickListener {
    if (calendarDay.isCurrentMonth) {
        // 클릭 애니메이션
        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
        val scaleDown = AnimationUtils.loadAnimation(context, R.anim.scale_down)

        cellCard.startAnimation(scaleUp)
        cellCard.postDelayed({
            cellCard.startAnimation(scaleDown)
            onDateClick(calendarDay)
        }, 150)
    }
}
```

**3. BottomSheet 등장 애니메이션**

```kotlin
// AddTaskBottomSheet.kt
private fun setupAnimations() {
    val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
    binding.root.startAnimation(fadeIn)
}
```

**4. RecyclerView 아이템 변경 애니메이션**

```kotlin
// CalendarFragment.kt
private fun updateCalendar(year: Int, month: Int, tasks: List<Task>) {
    val calendarDays = CalendarUtils.generateCalendarDays(year, month, tasks)

    // DiffUtil이 자동으로 변경된 아이템만 애니메이션 적용
    calendarAdapter.submitList(calendarDays) {
        binding.rvCalendar.scrollToPosition(0)
    }
}
```

#### 📊 결과

✅ 부드러운 화면 전환
✅ 시각적 피드백 제공
✅ 사용자 경험 향상

---

### 4.5 클라우드 동기화 아키텍처

#### 🤔 고민: "어떻게 여러 기기에서 데이터를 공유할까?"

요구사항:
- 로그인 없이 간단하게 사용
- 고유 ID로 데이터 식별
- 로컬 우선, 클라우드는 백업 용도

#### 💡 해결 방법

**1. 동기화 ID 시스템**

```kotlin
// SyncPreferences.kt
class SyncPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)

    fun getSyncId(): String? = prefs.getString("sync_id", null)

    fun setSyncId(id: String) {
        prefs.edit().putString("sync_id", id).apply()
    }

    fun generateNewSyncId(): String {
        val id = UUID.randomUUID().toString()
        setSyncId(id)
        return id
    }
}
```

**2. REST API 설계**

```kotlin
// CloudSyncService.kt
interface CloudSyncService {
    @GET("{id}")
    suspend fun getData(@Path("id") syncId: String): Response<SyncData>

    @GET("status/{id}")
    suspend fun getSyncStatus(@Path("id") syncId: String): Response<SyncStatus>

    @POST("{id}")
    suspend fun syncData(
        @Path("id") syncId: String,
        @Body data: SyncData
    ): Response<SyncData>
}
```

**3. Repository 패턴**

```kotlin
// CloudSyncRepository.kt
class CloudSyncRepository(
    private val apiService: CloudSyncService,
    private val syncPreferences: SyncPreferences
) {
    suspend fun syncToCloud(tasks: List<Task>): Result<String> {
        return try {
            val syncId = syncPreferences.getSyncId()
                ?: syncPreferences.generateNewSyncId()

            val syncData = SyncData(
                syncId = syncId,
                tasks = tasks,
                lastSyncTime = System.currentTimeMillis()
            )

            val response = apiService.syncData(syncId, syncData)

            if (response.isSuccessful) {
                syncPreferences.setLastSyncTime(System.currentTimeMillis())
                Result.success(syncId)
            } else {
                Result.failure(Exception("동기화 실패"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

**4. 동기화 플로우**

```
[로컬 DB] ←→ [Repository] ←→ [Retrofit] ←→ [서버 API]
     ↓                                           ↓
[Room Database]                            [Cloud Storage]
```

**5. 충돌 해결 전략**

- **단순 덮어쓰기**: 마지막 동기화가 승리 (Last Write Wins)
- 향후 개선: 타임스탬프 기반 병합

#### 📊 결과

✅ 로그인 없이 간편한 동기화
✅ 고유 ID로 다중 기기 지원
✅ 오프라인 우선 설계

---

## 5. 아키텍처 설계

### 5.1 MVVM 패턴 적용

```
┌─────────────────────────────────────────────┐
│                    View                     │
│  (Fragment, Activity, BottomSheet)          │
└─────────────────┬───────────────────────────┘
                  │ observe
                  ↓
┌─────────────────────────────────────────────┐
│                ViewModel                    │
│  (LiveData, StateFlow, 비즈니스 로직)        │
└─────────────────┬───────────────────────────┘
                  │ call
                  ↓
┌─────────────────────────────────────────────┐
│                Repository                   │
│  (데이터 소스 통합)                          │
└────────┬────────────────────┬───────────────┘
         │                    │
         ↓                    ↓
┌────────────────┐    ┌──────────────────┐
│  Local Source  │    │  Remote Source   │
│ (Room Database)│    │  (Retrofit API)  │
└────────────────┘    └──────────────────┘
```

### 5.2 데이터 흐름

**할일 추가 예시:**

```
1. AddTaskBottomSheet (사용자 입력)
          ↓
2. ViewModel.insertTask(task)
          ↓
3. Repository.insertTask(task)
          ↓
4. Room Database에 저장
          ↓
5. LiveData 업데이트
          ↓
6. CalendarFragment UI 자동 갱신
```

### 5.3 주요 클래스 다이어그램

```kotlin
// ViewModel
class CalendarViewModel : ViewModel() {
    private val repository: TaskRepository

    val currentYear: StateFlow<Int>
    val currentMonth: StateFlow<Int>
    val monthTasks: StateFlow<List<Task>>

    fun insertTask(task: Task)
    fun updateTask(task: Task)
    fun deleteTask(task: Task)
    fun navigateToNextMonth()
    fun navigateToPreviousMonth()
}

// Repository
class TaskRepository(private val taskDao: TaskDao) {
    fun getAllTasks(): Flow<List<Task>>
    fun getTasksByDate(date: Long): Flow<List<Task>>
    suspend fun insert(task: Task)
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
}

// DAO
@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY date ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE date = :date")
    fun getTasksByDate(date: Long): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
```

---

## 6. 배운 점과 느낀 점

### 6.1 기술적 성장

#### ✅ Kotlin Coroutines & Flow 숙달
- 비동기 작업을 간결하게 처리
- Flow를 통한 반응형 프로그래밍 이해
- `collect`, `combine`, `map` 등 연산자 활용

#### ✅ Room Database 활용
- Entity, DAO, Database 설계
- LiveData/Flow와의 통합
- 마이그레이션 전략 학습

#### ✅ Material Design 구현
- BottomSheet, CardView, FAB 등 컴포넌트 사용
- Material 3 디자인 시스템 적용
- 일관된 디자인 언어 유지

#### ✅ 네트워크 통신
- Retrofit을 사용한 REST API 통신
- 에러 핸들링 및 재시도 로직
- JSON 직렬화/역직렬화

### 6.2 디자인 및 UX

#### 🎨 글래스모피즘 트렌드 이해
- 단순 투명도 ≠ 글래스모피즘
- 블러 효과가 핵심
- 배경, 그라디언트, 테두리의 조화

#### 📱 모바일 UX 설계
- Bottom Sheet의 효과적인 사용
- 제스처 기반 인터랙션
- 시각적 피드백의 중요성

### 6.3 개발 프로세스

#### 🔄 반복적 개선
- 초기 디자인 → 피드백 → 수정 → 재평가
- 노란색 배경 → 파란/보라 그라디언트
- 작은 indicator → 테두리 강조

#### 🧪 테스트의 중요성
- 다양한 Android 버전 테스트
- 권한 처리 예외 상황 대응
- 네트워크 실패 시나리오 검증

### 6.4 어려웠던 점

#### 😓 BlurView 성능 최적화
- 초기: 모든 셀에 BlurView → 렉 발생
- 해결: BlurView 개수 최소화, 하드웨어 가속 활용

#### 😓 이미지 권한 관리
- Android 버전별 권한 차이 (READ_EXTERNAL_STORAGE vs READ_MEDIA_IMAGES)
- persistable URI permission 개념 이해

#### 😓 동기화 충돌 해결
- 여러 기기에서 동시 수정 시 충돌
- 현재: 간단한 덮어쓰기 전략
- 향후: CRDT 또는 타임스탬프 기반 병합 고려

### 6.5 개선 방향

#### 🚀 추가하고 싶은 기능
- ✏️ **위젯 지원**: 홈 화면에 캘린더 위젯
- 🔔 **알림 기능**: 할일 시간에 푸시 알림
- 🎨 **테마 커스터마이징**: 사용자가 색상 선택
- 📊 **통계 화면**: 할일 완료율, 월별 통계
- 🔍 **검색 기능**: 할일 제목/내용 검색
- 🏷️ **태그/카테고리**: 할일 분류 기능

#### 🔧 기술적 개선
- 단위 테스트 및 UI 테스트 추가
- CI/CD 파이프라인 구축
- 성능 모니터링 (Firebase Performance)
- 에러 추적 (Crashlytics)

### 6.6 느낀 점

> **"디자인과 기능의 균형이 중요하다"**

처음에는 화려한 디자인에 집중했지만, 사용자 경험이 더 중요하다는 것을 깨달았습니다.
아름다운 UI도 중요하지만, **직관적이고 편리한 기능**이 우선되어야 합니다.

> **"작은 디테일이 큰 차이를 만든다"**

애니메이션, 색상, 간격 등 작은 요소들이 모여 전체적인 완성도를 결정합니다.
사용자는 이런 디테일을 무의식적으로 느끼고 평가합니다.

> **"오픈소스 커뮤니티의 힘"**

BlurView, Coil, Room 등 훌륭한 라이브러리 덕분에 빠르게 개발할 수 있었습니다.
오픈소스에 기여하고 싶다는 동기부여가 되었습니다.

---

## 📚 참고 자료

- [Material Design Guidelines](https://m3.material.io/)
- [BlurView Library](https://github.com/Dimezis/BlurView)
- [Android Architecture Components](https://developer.android.com/topic/architecture)
- [Glassmorphism Design Trend](https://uxdesign.cc/glassmorphism-in-user-interfaces-1f39bb1308c9)
- [Room Database Guide](https://developer.android.com/training/data-storage/room)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

## 🎬 마무리

Glasscal 프로젝트를 통해 Android 네이티브 개발의 전반적인 과정을 경험했습니다.
디자인, 아키텍처, 데이터 관리, 네트워크 통신까지 다양한 영역을 다루며 많은 것을 배웠습니다.

앞으로도 사용자에게 가치를 제공하는 앱을 만들기 위해 계속 학습하고 개선해 나가겠습니다.

감사합니다! 🙏
