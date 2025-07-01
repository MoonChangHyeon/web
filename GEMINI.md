# Gemini 설정: Spring Boot 웹 프로젝트

이 파일은 Gemini가 현재 프로젝트의 기술 스택과 규칙을 이해하고 일관성 있는 지원을 제공하기 위한 가이드입니다.

## 1. 📜 프로젝트 개요
- **프로젝트 타입:** Spring Boot를 이용한 웹 애플리케이션
- **주요 목적:** Thymeleaf를 사용한 동적 웹 페이지 제공 및 Spring Data JPA를 통한 데이터 관리
- **답변 언어:** 모든 코드 설명과 답변은 **한국어**로 부탁합니다.

## 2. 🛠️ 핵심 기술 스택 (Core Technologies)

### 백엔드 (Backend)
- **프레임워크:** **Spring Boot**
  - Spring MVC, Spring Security 등 Spring Boot의 표준 기능을 적극적으로 활용합니다.
  - 코드 생성 시 `@RestController`, `@Service`, `@Repository` 등 스테레오타입 어노테이션을 명확히 사용해주세요.
- **데이터베이스 연동:** **Spring Data JPA** + **Hibernate**
  - Entity, Repository 인터페이스를 중심으로 코드를 작성합니다.
  - JPQL 또는 QueryDSL 사용에 대한 질문 시, Spring Data JPA의 관점에서 답변해주세요.
- **데이터베이스:** **MariaDB**
  - SQL 쿼리 생성 시 MariaDB 표준 문법을 사용해주세요.
  - `application.yml` 또는 `application.properties` 설정 예시는 MariaDB 기준으로 작성해주세요.

### 프론트엔드 (Frontend)
- **템플릿 엔진:** **Thymeleaf**
  - HTML 파일 생성 시 Thymeleaf의 표준 문법(예: `th:text`, `th:each`)을 사용해주세요.
  - 서버에서 전달된 모델 데이터를 화면에 바인딩하는 코드를 중심으로 작성합니다.
- **CSS 프레임워크:** **Bootstrap**
  - 최신 버전의 Bootstrap (예: Bootstrap 5) 클래스를 사용하여 UI 컴포넌트를 구성해주세요.
  - HTML 구조는 Bootstrap의 그리드 시스템과 컴포넌트 가이드를 따라주세요.

### 빌드 및 개발 환경
- **빌드 도구:** **Gradle**
  - `build.gradle` (Groovy DSL) 형식을 사용해주세요.
- **개발 환경(IDE):** **Visual Studio Code (VSCode)**
  - VSCode의 "Extension Pack for Java" 및 "Spring Boot Extension Pack" 사용을 가정합니다.
  - 관련 단축키나 VSCode 내 기능에 대한 팁도 좋습니다.

## 3. ✍️ 코딩 가이드라인
- **자바 버전:** Java 21을 기준으로 코드를 작성해주세요.
- **코드 스타일:** Spring Boot의 일반적인 코딩 컨벤션을 따릅니다.
- **패키지 구조:** `com.fortify.web` 아래에 `controller`, `service`, `repository`, `domain` 등 기능별로 패키지를 구성합니다.
- **DTO 사용:** Controller와 Service 계층 간 데이터 전송 시 DTO(Data Transfer Object)를 사용하는 것을 권장합니다.

## 4. DATABASES 정보
```
spring.datasource.url=jdbc:mariadb://localhost:3306/fortify_sb
spring.datasource.username=fortify
spring.datasource.password=Fortify!234
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
```