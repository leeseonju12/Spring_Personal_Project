# Framer(정적 프론트) + Spring MVC/Tomcat(API 서버) 전환 제안

## 목표

- Framer 도메인에서 게시판 조회/작성/수정/삭제를 제어
- Spring MVC + Tomcat은 JSP 화면 서버가 아니라 **REST API 서버**로 사용
- DB는 MySQL(MyBatis) 그대로 유지

## 권장 아키텍처

```text
[Framer Published Site]
        |
   HTTPS + JSON
        |
[Spring API Server (/api/v1/**)]
        |
      MyBatis
        |
      MySQL
```

## 이번 반영 코드

### 1) 게시판/게시글 API 분리

- `GET /api/v1/boards` : 게시판 목록
- `GET /api/v1/boards/{boardId}/articles` : 게시글 목록(검색/페이징)
- `GET /api/v1/boards/{boardId}/articles/{id}` : 게시글 상세
- `POST /api/v1/boards/{boardId}/articles` : 게시글 작성
- `PUT /api/v1/boards/{boardId}/articles/{id}` : 게시글 수정
- `DELETE /api/v1/boards/{boardId}/articles/{id}?memberId=1` : 게시글 삭제

> 기존 `/api/v1/articles` 엔드포인트는 레거시 호환용으로 유지할 수 있습니다.

### 2) Framer 도메인 기반 CORS 제어

- `/api/**`만 CORS 허용
- 허용 도메인을 `application.yml`의 `app.frontend.allowed-origins`로 관리
- 예시: `https://your-framer-domain.framer.website`

### 3) API와 JSP 인터셉터 분리

- 기존 `BeforeActionInterceptor`가 `/api/**`에 개입하지 않도록 제외 처리
- API 호출이 JSP 세션 흐름에 불필요하게 의존하지 않게 구성

## Framer 연동 예시

```ts
const API_BASE = "https://api.your-domain.com";

export async function getBoards() {
  const res = await fetch(`${API_BASE}/api/v1/boards`);
  if (!res.ok) throw new Error("게시판 목록 조회 실패");
  return res.json();
}

export async function getArticles(boardId: number, page = 1) {
  const url = `${API_BASE}/api/v1/boards/${boardId}/articles?page=${page}`;
  const res = await fetch(url);
  if (!res.ok) throw new Error("게시글 목록 조회 실패");
  return res.json();
}

export async function createArticle(boardId: number, input: { memberId: number; title: string; body: string }) {
  const res = await fetch(`${API_BASE}/api/v1/boards/${boardId}/articles`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(input),
  });
  if (!res.ok) throw new Error("게시글 작성 실패");
  return res.json();
}
```

## 동작 테스트 방법(curl)

```bash
# 1) 게시판 목록
curl -i "http://localhost:8081/api/v1/boards"

# 2) 게시글 목록
curl -i "http://localhost:8081/api/v1/boards/1/articles?page=1&pageSize=10"

# 3) 게시글 작성
curl -i -X POST "http://localhost:8081/api/v1/boards/1/articles" \
  -H "Content-Type: application/json" \
  -d '{"memberId":1,"title":"Framer 작성 테스트","body":"본문"}'

# 4) 게시글 수정
curl -i -X PUT "http://localhost:8081/api/v1/boards/1/articles/1" \
  -H "Content-Type: application/json" \
  -d '{"memberId":1,"title":"수정 제목","body":"수정 본문"}'

# 5) 게시글 삭제
curl -i -X DELETE "http://localhost:8081/api/v1/boards/1/articles/1?memberId=1"

# 6) CORS Preflight
curl -i -X OPTIONS "http://localhost:8081/api/v1/boards/1/articles" \
  -H "Origin: https://your-framer-domain.framer.website" \
  -H "Access-Control-Request-Method: POST"
```

## 운영 시 추가 권장

- 인증은 `memberId` 전달 방식에서 JWT/세션 기반으로 고도화
- 프론트(`app.domain`)와 API(`api.domain`) 도메인 분리
- dev/stage/prod 환경별 `allowed-origins`, DB, API URL 분리
