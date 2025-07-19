# Fortify SCA 분석 요청 웹 애플리케이션

이 프로젝트는 Spring Boot를 사용하여 Fortify SCA(Static Code Analysis) 분석을 요청하고 그 결과를 관리하는 웹 애플리케이션입니다. Git 리포지토리의 코드를 가져와 Fortify SCA 분석을 수행하고, PDF 및 XML 형식의 보고서를 생성합니다.

## 🛠️ 기술 스택

*   **언어:** Java 21
*   **프레임워크:** Spring Boot 3.4.3
*   **빌드 도구:** Gradle
*   **템플릿 엔진:** Thymeleaf
*   **데이터베이스:** MariaDB
*   **버전 관리:** Git (JGit 라이브러리 사용)
*   **운영체제:** Any (Linux/macOS/Windows)

## 🚀 주요 기능

*   **분석 Job 생성:** Git 리포지토리 URL과 브랜치를 지정하여 새로운 Fortify SCA 분석 Job을 생성합니다.
*   **비동기 분석 실행:** 생성된 Job은 백그라운드에서 비동기적으로 Fortify SCA 분석 파이프라인을 실행합니다.
*   **분석 상태 조회:** Job ID를 통해 분석 진행 상태 및 결과 보고서 URL을 조회할 수 있습니다.
*   **보고서 생성:** Fortify SCA 분석 완료 후 PDF 및 XML 형식의 보고서를 자동으로 생성합니다.

## 📦 프로젝트 구조

```
src/main/java/com/fortify/web/
├── config/             # 애플리케이션 설정 (비동기, 웹 MVC 등)
├── controller/         # REST API 컨트롤러
├── domain/             # JPA 엔티티 및 도메인 객체 (AnalysisJob, AnalysisJobStatus 등)
├── dto/                # 데이터 전송 객체 (AnalysisJobDto)
├── repository/         # Spring Data JPA 리포지토리
├── service/            # 비즈니스 로직
│   └── pipeline/       # 분석 파이프라인 관련 인터페이스 및 구현체
│       └── impl/       # 파이프라인 컴포넌트 구현체 (WorkspaceManagerImpl, GitClientImpl 등)
└── WebApplication.java # 메인 애플리케이션
```

## ⚙️ 설정

`src/main/resources/application.properties` 파일에서 다음 설정을 구성할 수 있습니다.

```properties
# Fortify SCA Analysis Settings
fortify.workspace.root=/tmp/fortify/workspaces
fortify.executable.path=/opt/Fortify/Fortify_SCA_and_Apps_23.2.0/bin/sourceanalyzer
fortify.report-generator.executable.path=/opt/Fortify/Fortify_SCA_and_Apps_23.2.0/bin/ReportGenerator
```

*   `fortify.workspace.root`: Fortify SCA 분석을 위한 임시 작업 공간이 생성될 루트 디렉터리입니다.
*   `fortify.executable.path`: `sourceanalyzer` 실행 파일의 절대 경로입니다.
*   `fortify.report-generator.executable.path`: `ReportGenerator` 실행 파일의 절대 경로입니다.

## 🏃‍♂️ 실행 방법

1.  **Fortify SCA 설치:** `sourceanalyzer` 및 `ReportGenerator` 실행 파일이 시스템에 설치되어 있고, `application.properties`에 올바른 경로가 설정되어 있는지 확인합니다.
2.  **데이터베이스 설정:** `application.properties`에 MariaDB 연결 정보를 올바르게 설정합니다.
3.  **빌드:** 프로젝트 루트 디렉터리에서 다음 Gradle 명령어를 실행하여 애플리케이션을 빌드합니다.
    ```bash
    ./gradlew clean build
    ```
4.  **실행:** 빌드 완료 후 다음 명령어로 애플리케이션을 실행합니다.
    ```bash
    java -jar build/libs/web-0.0.1-SNAPSHOT.jar
    ```
    또는 IDE(IntelliJ IDEA, VS Code 등)에서 `WebApplication.java`를 실행합니다.

## 💡 API 엔드포인트

애플리케이션이 실행되면 다음 REST API 엔드포인트를 사용할 수 있습니다.

### 1. 분석 Job 생성

*   **URL:** `/api/analysis`
*   **메서드:** `POST`
*   **설명:** 새로운 Fortify SCA 분석 Job을 생성하고 비동기적으로 실행합니다.
*   **요청 본문 (JSON):**
    ```json
    {
      "repoUrl": "string",
      "branch": "string (default: main)",
      "credentialsId": "string (optional)"
    }
    ```
*   **응답 본문 (JSON):**
    ```json
    {
      "jobId": "UUID",
      "status": "PENDING | RUNNING | COMPLETED | FAILED",
      "createdAt": "date-time"
    }
    ```

### 2. Job 진행 상태 및 결과 URL 조회

*   **URL:** `/api/analysis/{jobId}`
*   **메서드:** `GET`
*   **설명:** 특정 Job ID에 대한 진행 상태 및 생성된 보고서의 URL을 조회합니다.
*   **경로 변수:**
    *   `jobId`: 조회할 Job의 UUID
*   **응답 본문 (JSON):**
    ```json
    {
      "jobId": "UUID",
      "status": "PENDING | RUNNING | COMPLETED | FAILED",
      "startedAt": "date-time",
      "finishedAt": "date-time (nullable)",
      "reportPdfUrl": "string (nullable)",
      "reportXmlUrl": "string (nullable)",
      "errorMessage": "string (nullable)"
    }
    ```

## 🧹 워크스페이스 정리 정책

*   **설명:** 7일이 지난 워크스페이스는 자동으로 삭제됩니다.
*   **Cron 표현식:** `0 0 * * *` (매일 자정)

---
