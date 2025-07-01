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

### 1.3. Fortify SCA 연동
- `/fortify/scan/direct` 경로로 접속하면 Fortify SCA 정적 분석을 직접 실행하는 기능이 포함되어 있습니다.
- **새로운 기능: 스캔 설정 관리**
    - `buildId`, `classPath`, `filesToScan`을 포함하는 Fortify 스캔 설정을 데이터베이스에 저장하고 관리할 수 있습니다.
    - 저장된 스캔 설정을 조회, 생성, 수정, 삭제할 수 있습니다.
    - 저장된 스캔 설정을 선택하여 `sourceanalyzer` 명령어를 동적으로 생성하고 실행할 수 있습니다.

## 2. 기술 스택

- **백엔드:** Spring Boot, Spring Data JPA, Hibernate, MariaDB
- **프론트엔드:** Thymeleaf, Bootstrap
- **빌드 도구:** Gradle
- **자바 버전:** Java 21

## 3. 실행 방법

1.  **데이터베이스 설정:**
    `application.properties` 파일에 MariaDB 연결 정보를 설정합니다.
    ```properties
    spring.datasource.url=jdbc:mariadb://localhost:3306/fortify_sb
    spring.datasource.username=fortify
    spring.datasource.password=Fortify!234
    spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
    spring.jpa.hibernate.ddl-auto=update
    ```

2.  **애플리케이션 실행:**
    프로젝트 루트 디렉토리에서 다음 Gradle 명령어를 실행합니다.
    ```bash
    ./gradlew bootRun
    ```

3.  **접속:**
    웹 브라우저에서 `http://localhost:8080/` 에 접속합니다.

## 4. 새로운 기능 사용법

### 4.1. Scan Configurations (스캔 설정 관리)

Fortify SCA 스캔 설정을 생성, 조회, 수정, 삭제하고, 저장된 설정으로 스캔을 실행할 수 있습니다.

1.  **접근 방법:** 웹 애플리케이션 접속 후 상단 네비게이션 바의 "Scan Configurations" 링크를 클릭합니다.
2.  **새로운 설정 추가:**
    *   "Add New Configuration" 버튼을 클릭합니다.
    *   `Build ID`, `Classpath`, `Files to Scan` 필드를 채우고 "Save" 버튼을 클릭합니다.
3.  **설정 조회:**
    *   "Scan Configurations" 페이지에서 저장된 모든 스캔 설정 목록을 확인할 수 있습니다.
4.  **설정 수정:**
    *   목록에서 수정하려는 설정의 "Edit" 버튼을 클릭합니다.
    *   필요한 정보를 수정한 후 "Save" 버튼을 클릭합니다.
5.  **설정 삭제:**
    *   목록에서 삭제하려는 설정의 "Delete" 버튼을 클릭합니다.
6.  **스캔 실행:**
    *   목록에서 실행하려는 설정의 "Run Scan" 버튼을 클릭합니다.
    *   해당 설정에 따라 Fortify SCA 분석이 백그라운드에서 실행되며, 결과는 페이지 상단에 표시됩니다.

### 4.2. Fortify Settings (Fortify 설정 관리)

Fortify SCA 실행 파일 경로와 같은 전역 설정을 관리할 수 있습니다.

1.  **접근 방법:** 웹 애플리케이션 접속 후 상단 네비게이션 바의 "Fortify Settings" 링크를 클릭합니다.
2.  **설정:**
    *   `Sourceanalyzer Path` 필드에 Fortify SCA의 `sourceanalyzer` 실행 파일의 절대 경로를 입력합니다. (예: `/opt/fortify/bin/sourceanalyzer`)
    *   "Save Settings" 버튼을 클릭하여 설정을 저장합니다.
    *   이 설정은 스캔 설정 관리에서 스캔을 실행할 때 사용됩니다.
