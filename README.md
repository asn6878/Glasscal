# Glasscal - 글래스모피즘 캘린더 앱

Android 기반의 글래스모피즘(Glassmorphism) 디자인을 적용한 현대적인 캘린더 애플리케이션입니다.

## 📱 주요 기능

### 현재 구현된 기능
- ✅ **월간 캘린더 뷰**: 글래스모피즘 디자인이 적용된 Grid 형태의 캘린더 (블러 효과가 우아하게 유리처럼 적용된 디자인)
- ✅ **할일 관리**: 날짜별 할일 추가, 수정, 삭제
- ✅ **할일 목록 확인**: 날짜 클릭 시 해당 날짜의 모든 할일을 리스트로 표시
- ✅ **이미지 첨부**: 할일에 이미지를 추가하고 배경으로 표시
- ✅ **할일 미리보기**: 캘린더 셀에서 할일 제목과 개수 확인
- ✅ **로컬 데이터 저장**: Room Database를 사용한 오프라인 저장
- ✅ **클라우드 동기화**: localhost:8080 서버와 데이터 동기화
  - 동기화 상태 확인 (동기화 완료 여부 및 최신 동기화 날짜 표시)
  - 로컬 데이터를 클라우드에 업로드
  - 클라우드 데이터 가져오기 (로컬 데이터 덮어쓰기)
  - 고유 동기화 ID 자동 생성 및 관리
- ✅ **설정 화면**: 데이터 동기화 및 관리

## 🎨 디자인 특징

### 글래스모피즘 (Glassmorphism)
- 반투명 배경 (alpha 0.7-0.9)
- 부드러운 그림자와 테두리
- 밝은 색상의 그라디언트 배경
- 레이어드 UI 구조

## 🏗️ 기술 스택

- **언어**: Kotlin
- **최소 SDK**: 24 (Android 7.0)
- **타겟 SDK**: 36
- **아키텍처**: MVVM (Model-View-ViewModel)

### 주요 라이브러리
- 동적으로 필요한 라이브러리를 추가하고 사용된 라이브러리를 추가할 듯

## 📁 프로젝트 구조

```
app/src/main/java/com/example/glasscal/
├── data/
│   ├── api/               # Retrofit API 서비스
│   │   ├── CloudSyncService.kt   # 동기화 API 인터페이스
│   │   └── RetrofitClient.kt     # Retrofit 클라이언트
│   ├── local/
│   │   ├── entity/        # Room Entity (Task)
│   │   ├── dao/           # Room DAO
│   │   ├── database/      # AppDatabase
│   │   └── SyncPreferences.kt    # 동기화 설정 저장
│   ├── model/             # 데이터 모델 (CalendarDay, SyncData)
│   └── repository/        # TaskRepository, CloudSyncRepository
├── ui/
│   ├── calendar/          # 캘린더 화면
│   ├── task/              # 할일 등록/수정 BottomSheet
│   ├── settings/          # 설정 화면 (동기화 기능)
│   └── adapter/           # RecyclerView Adapter
├── viewmodel/             # CalendarViewModel
├── util/                  # CalendarUtils
└── MainActivity.kt
```

## 📖 사용 방법

### 캘린더 및 할일 관리
1. **캘린더 보기**: 앱 실행 시 현재 월의 캘린더가 표시됩니다
2. **월 이동**: 상단의 이전/다음 버튼으로 월을 이동할 수 있습니다
3. **할일 목록 확인**: 날짜를 클릭하면 해당 날짜의 할일 목록이 BottomSheet로 표시됩니다
4. **할일 추가**: 할일 목록 BottomSheet 상단의 "+ 새 할일" 버튼으로 할일을 추가할 수 있습니다
5. **할일 수정/삭제**: 할일 목록에서 항목을 클릭하면 수정할 수 있고, 수정 화면에서 삭제 버튼으로 삭제할 수 있습니다
6. **이미지 추가**: 할일에 이미지를 추가하면 해당 날짜 셀의 배경으로 표시됩니다

### 클라우드 동기화
7. **설정 접근**: 우측 하단 FAB 버튼으로 설정 화면에 접근합니다
8. **동기화 상태 확인**: 설정 화면 상단에서 현재 동기화 상태를 확인할 수 있습니다
   - 동기화 완료: "클라우드 데이터 동기화 완료! - 최신 동기화 날짜[2025-12-03]"
   - 동기화 필요: 붉은 글씨로 "클라우드 동기화 필요"
9. **데이터 동기화**: "동기화" 버튼을 눌러 로컬 데이터를 클라우드에 업로드합니다
   - 최초 동기화 시 고유 ID가 자동 생성됩니다
   - **이 ID를 반드시 기억하세요!** (다른 기기에서 데이터를 가져올 때 필요)
10. **데이터 가져오기**: "데이터 가져오기" 버튼을 눌러 클라우드에서 데이터를 가져올 수 있습니다
    - 동기화 ID 입력이 필요합니다
    - ⚠️ 경고: 기존 로컬 데이터가 모두 삭제되고 클라우드 데이터로 덮어씌워집니다
11. **모든 데이터 삭제**: 로컬 데이터와 동기화 정보를 모두 삭제합니다

## 🔐 데이터 저장 및 동기화

### 로컬 저장
- Room Database를 사용하여 기기에 데이터 저장
- 오프라인에서도 사용 가능
- 앱 삭제 시 데이터도 함께 삭제됨

### 클라우드 동기화
- localhost:8080 서버와 REST API 통신
- 고유 동기화 ID로 데이터 식별
- 여러 기기에서 동일한 ID로 데이터 공유 가능

### API 명세
- `GET {id}`: 클라우드 데이터 가져오기
- `GET status/{id}`: 동기화 상태 조회
- `POST {id}`: 클라우드 데이터 업데이트 or 동기화

## 🎨 글래스모피즘 디자인 상세 가이드

### 시각적 특징
글래스모피즘은 **유리처럼 반투명하고 빛을 투과하는 듯한 효과**를 주는 디자인 트렌드입니다.

### 핵심 구성 요소

#### 1. 반투명 배경 (Translucent Background)
- 배경색에 70-90% 투명도(alpha) 적용
- 예: `backgroundColor = Color.parseColor("#B3FFFFFF")` (70% 불투명)
- 뒤의 콘텐츠가 은은하게 비치는 효과

#### 2. 블러 효과 (Backdrop Blur) - 중요!
- **이것이 글래스모피즘의 핵심**: 배경을 흐릿하게 만드는 효과
- Android에서는 `RenderScript` 또는 `RenderEffect` API 사용
- 블러 반경: 보통 10-25px
- 배경 이미지나 콘텐츠에 가우시안 블러 적용

#### 3. 테두리 (Border)
- 얇고 밝은 색상의 테두리 (보통 1-2px)
- 흰색 또는 밝은 색에 20-40% 투명도
- 예: `strokeColor = Color.parseColor("#40FFFFFF")`

#### 4. 그림자 (Shadow)
- 부드럽고 은은한 그림자
- elevation 2-8dp 권장
- 그림자 색상도 투명도를 가진 밝은 색

#### 5. 배경 그라디언트
- 밝고 화사한 색상 조합
- 파스텔 톤 또는 비비드 톤
- 다층 레이어 구조로 깊이감 표현

### 구현 예시 코드
```kotlin
// 글래스모피즘 카드뷰 예시
<androidx.cardview.widget.CardView
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="#B3FFFFFF"
    app:cardElevation="4dp"
    app:cardCornerRadius="16dp"
    app:strokeColor="#40FFFFFF"
    app:strokeWidth="1dp">
    
    <!-- 블러 효과를 위한 커스텀 뷰 필요 -->
    <com.example.glasscal.ui.component.BlurView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:blurRadius="20dp"/>
        
</androidx.cardview.widget.CardView>
```

### Android에서의 블러 구현 방법

#### Option 1: RenderEffect (API 31+)
```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    view.setRenderEffect(
        RenderEffect.createBlurEffect(
            25f, 25f, 
            Shader.TileMode.CLAMP
        )
    )
}
```

#### Option 2: RenderScript (Deprecated but widely compatible)
```kotlin
val renderScript = RenderScript.create(context)
val blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
blurScript.setRadius(25f)
blurScript.setInput(allocationIn)
blurScript.forEach(allocationOut)
```

#### Option 3: 외부 라이브러리
- RealtimeBlurView (https://github.com/mmin18/RealtimeBlurView)
- BlurView (https://github.com/Dimezis/BlurView)

### 색상 팔레트 예시
```kotlin
object GlassColors {
    val GLASS_WHITE = Color.parseColor("#B3FFFFFF")      // 70% 불투명 흰색
    val GLASS_BORDER = Color.parseColor("#40FFFFFF")     // 25% 불투명 흰색
    val GRADIENT_START = Color.parseColor("#FFE5F5FF")   // 연한 파란 파스텔
    val GRADIENT_END = Color.parseColor("#FFFFF0F5")     // 연한 분홍 파스텔
    val SHADOW = Color.parseColor("#20000000")           // 12% 불투명 검정
}
```

### 시각적 레퍼런스
글래스모피즘의 좋은 예시:
- iOS 11+ 제어 센터의 반투명 패널
- macOS Big Sur의 메뉴바와 사이드바
- Windows 11의 Acrylic Material
- 웹: https://glassmorphism.com/

### 주의사항
- ⚠️ 배경이 너무 복잡하면 가독성 저하 → 적절한 contrast 유지
- ⚠️ 블러 연산은 성능 비용이 높음 → 최적화 필요
- ⚠️ 접근성: 시각 장애인을 위한 고대비 모드 제공

