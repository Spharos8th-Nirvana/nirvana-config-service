# nirvana-config-service

## 서비스 개요

- 목적: Spring Cloud Config Server. [`nirvana-config-repo`](https://github.com/Spharos8th-Nirvana/nirvana-config-repo)(Private)의 설정 파일을 읽어 각 서비스에 전달합니다.
- 담당자 / 백업 리뷰어: <!-- 확정 후 기입 -->
- 소유 데이터와 운영 책임: 자체 DB 없음. 설정 파일 원본은 `nirvana-config-repo`가 관리합니다.
- API 문서: Spring Cloud Config 표준 엔드포인트 (`/{application}/{profile}[/{label}]`)
- 기본 포트 / 헬스체크: `8888` / `GET /actuator/health`

## 기술 스택

Java 21 · Spring Boot 4.1.2 · Spring Cloud 2025.1.4 · Gradle (Groovy DSL)

## 로컬 실행

- 사전 요구사항: JDK 21 (없으면 Gradle toolchain이 자동으로 내려받습니다)
- 환경변수 (`.env.example` 참고, 실제 값은 커밋 금지)

| 키 | 필수 | 용도 | 기본값 |
| --- | --- | --- | --- |
| `CONFIG_REPO_USERNAME` | 예 | config-repo 접근용 GitHub 사용자명 | - |
| `CONFIG_REPO_TOKEN` | 예 | config-repo **읽기 전용** fine-grained 토큰 (각자 발급) | - |
| `CONFIG_REPO_URI` | 아니요 | 설정 저장소 주소 | `nirvana-config-repo` |
| `CONFIG_REPO_LABEL` | 아니요 | 읽을 브랜치 | `develop` |
| `CONFIG_REPO_CLONE_ON_START` | 아니요 | 기동 시 저장소 접근 확인 | `true` |
| `SERVER_PORT` | 아니요 | 서버 포트 | `8888` |

```bash
export CONFIG_REPO_USERNAME=<github-id>
export CONFIG_REPO_TOKEN=<token>
./gradlew bootRun
```

- 확인: `curl localhost:8888/booking-service/local` → `version`이 config-repo의 최신 커밋 SHA와 같으면 정상입니다.
- 테스트: `./gradlew test` (실제 config-repo에 접근하지 않습니다)
- Windows PowerShell에서는 `.\gradlew.bat`를 사용합니다.

### 토큰 발급

GitHub → Settings → Developer settings → Fine-grained tokens → Resource owner `Spharos8th-Nirvana`, Repository `nirvana-config-repo`만 선택, Permissions → Contents: **Read-only**.

- Organization 설정에 따라 토큰이 **관리자 승인 대기** 상태로 생성될 수 있습니다. 승인 전에는 `not authorized` 오류로 기동이 실패하므로 org 관리자에게 승인을 요청하세요.
- 토큰이 틀리거나 비어 있으면 `clone-on-start` 설정 때문에 서버가 기동 단계에서 `not authorized`로 종료됩니다.

## 클라이언트 서비스 연결

각 서비스에 `spring-cloud-starter-config`를 추가하고 `application.yml`에 다음을 둡니다.

```yaml
spring:
  application:
    name: booking-service   # config-repo 파일 이름과 동일
  profiles:
    active: local
  config:
    import: "optional:configserver:http://localhost:8888"
```

실행 순서: config-service → eureka → gateway → 각 서비스

개발과 기여 방법은 [CONTRIBUTING.md](CONTRIBUTING.md)를 참고하세요.
