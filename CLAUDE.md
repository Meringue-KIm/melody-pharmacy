# Melody Pharmacy — Claude 작업 가이드

## 프로젝트 개요
기분·상황에 맞는 노래를 처방해주는 음악 추천 웹앱.
상황(8) × 분위기(6) = 48가지 조합 → 서버에서 랜덤 추천.

## 기술 스택
- **Frontend**: React + TypeScript + Vite (`frontend/`, port 5173)
- **Backend**: Spring Boot 3 + MySQL (`backend/`, port 8081)
- **인증**: JWT + 카카오 OAuth
- **AI**: Gemini 2.5 Flash (노래 자동 추천), YouTube Data API (검증)

## 서버 실행
```powershell
# MySQL
docker start melody-pharmacy-mysql

# 백엔드 (JDK 17 필수)
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17.0.1"
cd backend && gradlew.bat bootRun

# 프론트엔드
cd frontend && npm run dev
```

## 환경변수 (배포 시 필수)
**백엔드:**
- `CORS_ALLOWED_ORIGINS` — 프론트엔드 배포 URL (쉼표 구분)
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`, `KAKAO_CLIENT_ID`, `KAKAO_REDIRECT_URI`
- `GEMINI_API_KEY`, `YOUTUBE_API_KEY`, `ADMIN_SECRET_KEY`

**프론트엔드:**
- `VITE_API_URL` — 백엔드 배포 URL
- `VITE_KAKAO_CLIENT_ID`, `VITE_KAKAO_REDIRECT_URI`

## 핵심 설계 결정
- **게스트 모드**: situations/concepts/recommend/playlists는 서버 API 직접 호출, 저장/히스토리만 localStorage
- **songCache**: `guestApi.ts` 모듈 로드 시 localStorage 데이터로 초기화 → 새로고침 후에도 저장/히스토리 복원
- **들은 곡 제외**: 동일 상황×분위기 조합 기준 (전체 기준 아님)
- **히스토리 제한**: 백엔드 50곡, 게스트 50곡

## 중요 파일
- `frontend/src/api/guestApi.ts` — 게스트 API (서버 API + localStorage 혼합)
- `frontend/src/utils/guestMode.ts` — localStorage 저장/히스토리 관리
- `backend/.../SongRepository.java` — `findRandomExcludingPlayed` 쿼리 (조합 기준)
- `backend/.../SecurityConfig.java` — CORS 설정 (환경변수)
- `backend/.../SongService.java` — recommend에서 userId null이면 게스트 처리

## 주의사항
- Bash 툴에서 `java` 명령 안 됨 → 반드시 PowerShell 사용
- `PLAYLISTS` 로컬 상수는 더 이상 guestApi에서 사용 안 함 (서버 API 사용)
- DataInitializer: `addNewSongs()`는 매 재시작마다 실행 — 멱등성 보장
- git history에 API 키 노출 이력 → 배포 전 재발급 필수
