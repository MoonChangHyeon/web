# Fortify Spring Boot Web Application

이 프로젝트는 Spring Boot를 기반으로 하는 웹 애플리케이션입니다. Thymeleaf를 사용하여 동적 웹 페이지를 제공하며, Spring Data JPA를 통해 데이터베이스를 관리합니다.

## 1. 기능 목록

### 1.1. 메시지 관리 (CRUD)
- 메인 페이지에서 메시지 목록을 확인하고, 새로운 메시지를 등록할 수 있습니다.
- 기존 메시지를 수정하거나 삭제할 수 있습니다.
- 메시지 내용으로 검색 기능이 구현되어 있습니다.

### 1.2. 사용자 인증
- Spring Security를 이용한 기본적인 로그인 기능이 구현되어 있습니다.
- 'ADMIN' 역할을 가진 사용자만 메시지 등록/수정/삭제가 가능하도록 권한이 설정되어 있습니다.

### 1.3. Fortify SCA 연동 및 자동화
- **코드 분석 요청**: Home 화면에서 Build ID와 분석 대상 파일을 업로드하여 Fortify SCA 분석을 요청할 수 있습니다. 분석 요청 시 파일 크기 제한 검증이 이루어집니다.
- **분석 요청 현황**: 제출된 분석 요청들의 실시간 상태(대기 중, 빌드 중, 분석 중, 레포트 생성 중, 완료, 실패 등)를 UI에서 확인할 수 있습니다.
- **Fortify 설정 관리**: Fortify SCA 설치 경로, 리포트 출력 디렉토리, 업로드 디렉토리 등을 설정할 수 있습니다. 빌드 및 분석 메모리(`-Xmx`) 설정 및 최대 분석 파일 용량 제한을 설정할 수 있습니다.
- **Fortify SCA 직접 호출**: Fortify SCA의 `sourceanalyzer` 실행 파일을 Java 코드에서 직접 호출하여 분석을 수행합니다.

### 1.4. 사용자 관리 (관리자 전용)
- 관리자 페이지에서 유저 목록 조회, 상세 보기, 수정, 삭제 기능을 제공합니다.
- 유저의 역할 및 활성화/비활성화 상태를 관리할 수 있습니다.
- 유저 정보 변경 시 감사(Audit) 로그가 기록됩니다.

### 1.5. 활동 로그 (관리자 전용)
- 웹 애플리케이션 내에서 발생하는 주요 활동(예: 유저 정보 변경)을 기록하고 조회할 수 있습니다.

## 2. 기술 스택

- **백엔드:** Spring Boot, Spring Data JPA, Hibernate, MariaDB, Spring Security
- **프론트엔드:** Thymeleaf, Bootstrap
- **빌드 도구:** Gradle
- **자바 버전:** Java 21
- **정적 분석:** Fortify SCA

## 3. 개발 환경 설정

### 3.1. 필수 도구 설치

*   **Java Development Kit (JDK)**: Java 21
*   **Gradle**: (Wrapper가 포함되어 있으므로 별도 설치 불필요)
*   **MariaDB**: 데이터베이스 서버
*   **Fortify Static Code Analyzer (SCA)**: Fortify SCA 설치 및 실행 파일 경로 확인

### 3.2. 데이터베이스 설정

`src/main/resources/application.properties` 또는 `application.yml` 파일에 MariaDB 연결 정보를 설정합니다.

```properties
spring.datasource.url=jdbc:mariadb://localhost:3306/fortify_sb
spring.datasource.username=fortify
spring.datasource.password=Fortify!234
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```

### 3.3. Fortify SCA 설정

애플리케이션 실행 후, `Fortify Settings` 페이지(`http://localhost:8080/fortify-settings`)에서 다음 경로들을 설정해야 합니다:

*   **Fortify SCA Path**: `sourceanalyzer` 실행 파일이 포함된 디렉토리 (예: `/opt/fortify/Fortify_SCA_and_Apps_24.4/bin`)
*   **Report Output Directory**: FPR 파일 및 리포트가 저장될 디렉토리 (예: `/Users/youruser/fortify_results`)
*   **Fortify Uploads Directory**: 분석을 위해 업로드된 파일이 임시로 저장될 디렉토리 (예: `/Users/youruser/fortify_uploads`)
*   **Build and Analysis Memory (-Xmx)**: Fortify SCA 분석에 할당할 메모리 (예: `4G`, `8G` 등)
*   **Max Analysis File Size (MB)**: 업로드 가능한 최대 파일 크기 (MB 단위)

## 4. 애플리케이션 실행

프로젝트 루트 디렉토리(`Fortify_SB/web`)에서 다음 명령어를 실행합니다.

```bash
./gradlew bootRun
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 실행됩니다.

## 5. 사용 방법

### 5.1. 로그인

기본 계정은 다음과 같습니다:

*   **User**: `username: user`, `password: password`
*   **Admin**: `username: admin`, `password: adminpass`

### 5.2. Home 화면

*   메시지 목록을 확인하고 검색할 수 있습니다.
*   **분석 요청**: '분석 요청' 버튼을 클릭하여 Build ID와 분석 대상 파일을 업로드합니다. 업로드된 파일은 설정된 `Fortify Uploads Directory`에 저장되고, Fortify SCA 분석이 비동기적으로 시작됩니다.
*   **분석 요청 현황**: 제출된 분석 요청들의 실시간 상태(대기 중, 빌드 중, 분석 중, 레포트 생성 중, 완료, 실패 등)를 확인할 수 있습니다.

### 5.3. Fortify Settings (관리자 전용)

*   Fortify SCA 관련 경로 및 분석 설정을 관리합니다.
*   설정 변경 후 'Save' 버튼을 클릭하여 저장합니다.

### 5.4. 유저 관리 (관리자 전용)

*   내비게이션 바의 '유저 관리' 메뉴를 통해 접근합니다.
*   유저 목록을 조회하고, 검색 및 페이징 기능을 사용할 수 있습니다.
*   각 유저의 상세 정보를 확인하고, 수정, 삭제, 상태(활성화/비활성화) 토글을 수행할 수 있습니다.

### 5.5. 활동 로그 (관리자 전용)

*   내비게이션 바의 '활동 로그' 메뉴를 통해 접근합니다.
*   시스템에서 발생하는 주요 활동 기록을 조회하고 검색할 수 있습니다.