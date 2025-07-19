# Gemini GitHub 기능 요청 가이드

이 문서는 Gemini를 통해 요청할 수 있는 GitHub 관련 기능들을 상세히 설명합니다. 각 기능의 설명, 주요 파라미터, 구체적인 사용 예시를 참고하여 GitHub 작업을 효율적으로 처리해 보세요.

## 목차
1. [레포지토리 관리](#1-레포지토리-관리)
2. [파일 및 코드 관리](#2-파일-및-코드-관리)
3. [브랜치 및 커밋 관리](#3-브랜치-및-커밋-관리)
4. [이슈 관리](#4-이슈-관리)
5. [Pull Request (PR) 관리](#5-pull-request-pr-관리)

---

### 1. 레포지토리 관리

#### **레포지토리 생성 (create_repository)**
- **기능 상세 설명:** GitHub에 새로운 레포지토리를 생성합니다. 레포지토리 이름, 설명, 공개 여부, README 파일 자동 생성 여부 등을 지정할 수 있습니다.
- **주요 파라미터:**
  - `name` (필수): 생성할 레포지토리의 이름
  - `description`: 레포지토리에 대한 간략한 설명
  - `private`: `true`로 설정 시 비공개 레포토리로 생성 (기본값: `false`)
  - `autoInit`: `true`로 설정 시 README.md 파일을 포함하여 초기화
- **사용 예시:**
  > `'my-new-project'라는 이름으로 새 레포지토리를 만들어줘. 설명은 'Gemini로 생성한 테스트 프로젝트'로 하고, 비공개(private)로 설정해줘. README 파일도 자동으로 만들어줘.`

#### **레포지토리 검색 (search_repositories)**
- **기능 상세 설명:** 특정 키워드나 조건을 사용하여 GitHub의 레포지토리를 검색합니다. 언어, 스타 개수 등으로 필터링하여 검색할 수 있습니다.
- **주요 파라미터:**
  - `query` (필수): 검색어. GitHub 검색 문법 사용 가능 (예: `language:java stars:>1000`)
- **사용 예시:**
  > `언어는 java이고 스타가 500개 이상인 'spring security' 관련 레포지토리를 찾아줘.`
  > `최근 일주일 안에 업데이트된 'tensorflow' 레포지토리를 검색해줘.`

#### **레포지토리 포크 (fork_repository)**
- **기능 상세 설명:** 다른 사용자나 조직의 레포지토리를 내 계정으로 복제(fork)합니다. 이를 통해 원본 레포지토리에 영향을 주지 않고 자유롭게 코드를 수정하거나 기여(Contribute)할 수 있습니다.
- **주요 파라미터:**
  - `owner` (필수): 원본 레포지토리의 소유자 이름
  - `repo` (필수): 원본 레포지토리의 이름
- **사용 예시:**
  > `'spring-projects/spring-boot' 레포지토리를 내 계정으로 포크해줘.`

---

### 2. 파일 및 코드 관리

#### **파일 내용 읽기 (get_file_contents)**
- **기능 상세 설명:** 특정 레포지토리, 특정 브랜치에 있는 파일의 내용을 읽어옵니다. 파일 경로를 정확히 지정해야 합니다.
- **주요 파라미터:**
  - `owner` (필수): 레포지토리 소유자
  - `repo` (필수): 레포지토리 이름
  - `path` (필수): 파일의 전체 경로
  - `branch`: 브랜치 이름 (지정하지 않으면 기본 브랜치)
- **사용 예시:**
  > `'munchanghyeon/Fortify_SB' 레포지토리의 'main' 브랜치에 있는 'build.gradle' 파일 내용을 보여줘.`

#### **파일 생성 및 수정 (create_or_update_file)**
- **기능 상세 설명:** 레포지토리에 새 파일을 만들거나 이미 존재하는 파일의 내용을 수정합니다. 수정 시에는 원본 파일의 `sha` 값이 필요할 수 있습니다.
- **주요 파라미터:**
  - `owner`, `repo`, `branch`, `path` (필수)
  - `content` (필수): 파일에 작성할 내용
  - `message` (필수): 커밋 메시지
- **사용 예시:**
  > **(파일 생성)** `'munchanghyeon/Fortify_SB' 레포지토리의 'develop' 브랜치에 'docs/guide.md' 파일을 만들고, 내용은 '# 개발 가이드'로 채워줘. 커밋 메시지는 'docs: Add development guide'로 해줘.`
  > **(파일 수정)** `'munchanghyeon/Fortify_SB' 레포지토리의 'main' 브랜치에 있는 'README.md' 파일 내용을 수정해줘. (수정할 내용을 함께 제시)`

#### **코드 검색 (search_code)**
- **기능 상세 설명:** 특정 레포지토리 또는 GitHub 전체에서 특정 코드 조각이나 키워드를 포함한 파일을 검색합니다.
- **주요 파라미터:**
  - `q` (필수): 검색할 코드 또는 키워드. `repo:` 한정자를 사용하여 특정 레포지토리로 범위를 좁힐 수 있습니다.
- **사용 예시:**
  > `레포지토리 'munchanghyeon/Fortify_SB' 내에서 '@RestController' 어노테이션을 사용하는 코드를 모두 찾아줘.`
  > `파일 이름이 'pom.xml'이고 'spring-boot-starter-web'이라는 텍스트를 포함하는 파일을 검색해줘.`

---

### 3. 브랜치 및 커밋 관리

#### **브랜치 생성 (create_branch)**
- **기능 상세 설명:** 특정 레포지토리에 새로운 브랜치를 생성합니다. 어떤 브랜치를 기반으로 생성할지 지정할 수 있으며, 지정하지 않으면 기본 브랜치를 기반으로 합니다.
- **주요 파라미터:**
  - `owner`, `repo` (필수)
  - `branch` (필수): 새로 생성할 브랜치 이름
  - `from_branch`: 기반이 될 브랜치 이름
- **사용 예시:**
  > `'munchanghyeon/Fortify_SB' 레포지토리의 'main' 브랜치를 기반으로 'feature/new-api' 브랜치를 만들어줘.`

#### **커밋 목록 보기 (list_commits)**
- **기능 상세 설명:** 특정 브랜치나 특정 파일의 커밋 히스토리를 조회합니다.
- **주요 파라미터:**
  - `owner`, `repo` (필수)
  - `sha`: 커밋 목록을 가져올 브랜치 이름 또는 커밋 SHA
  - `perPage`: 페이지 당 표시할 커밋 수
- **사용 예시:**
  > `'munchanghyeon/Fortify_SB' 레포지토리의 'develop' 브랜치에 있는 최근 10개의 커밋 로그를 보여줘.`

---

### 4. 이슈 관리

#### **이슈 생성 (create_issue)**
- **기능 상세 설명:** 버그 리포트, 기능 요청 등 새로운 이슈를 생성합니다. 제목과 본문 외에도 담당자(assignees), 라벨(labels)을 지정하여 체계적으로 관리할 수 있습니다.
- **주요 파라미터:**
  - `owner`, `repo`, `title` (필수)
  - `body`: 이슈의 상세 설명 (마크다운 지원)
  - `assignees`: 담당자 GitHub 핸들 (배열)
  - `labels`: 적용할 라벨 이름 (배열)
- **사용 예시:**
  > `'munchanghyeon/Fortify_SB' 레포지토리에 이슈를 생성해줘. 제목: '사용자 목록 조회 API 실패', 본문: 'GET /api/users 호출 시 500 에러 발생', 라벨: 'bug', 'backend', 담당자: 'munchanghyeon'`

#### **이슈 목록 보기 (list_issues)**
- **기능 상세 설명:** 레포지토리의 이슈 목록을 다양한 조건으로 필터링하여 조회합니다.
- **주요 파라미터:**
  - `owner`, `repo` (필수)
  - `state`: `open`(열린 이슈), `closed`(닫힌 이슈), `all` (기본값: `open`)
  - `labels`: 특정 라벨이 포함된 이슈만 필터링 (쉼표로 구분)
  - `sort`: 정렬 기준 (`created`, `updated`, `comments`)
- **사용 예시:**
  > `'munchanghyeon/Fortify_SB' 레포지토리에서 'enhancement' 라벨이 붙어있고 현재 열려있는(open) 이슈 목록을 보여줘.`

#### **이슈 수정 및 코멘트 추가 (update_issue, add_issue_comment)**
- **기능 상세 설명:** 기존 이슈의 제목, 본문, 상태 등을 수정하거나, 해당 이슈에 새로운 코멘트를 추가하여 논의를 이어갈 수 있습니다.
- **사용 예시:**
  > **(이슈 수정)** `'munchanghyeon/Fortify_SB' 레포지토리의 3번 이슈 상태를 'closed'로 변경해줘.`
  > **(코멘트 추가)** `'munchanghyeon/Fortify_SB' 레포지토리의 5번 이슈에 '해당 문제 테스트 환경에서 재현했습니다.' 라고 코멘트를 남겨줘.`

---

### 5. Pull Request (PR) 관리

#### **PR 생성 (create_pull_request)**
- **기능 상세 설명:** 특정 브랜치의 변경 사항을 다른 브랜치로 병합하기 위한 Pull Request를 생성합니다. 코드 리뷰를 요청하고, 변경 사항에 대해 논의하는 과정의 시작입니다.
- **주요 파라미터:**
  - `owner`, `repo`, `title` (필수)
  - `head` (필수): 변경 사항이 있는 브랜치 이름 (예: `feature/new-login`)
  - `base` (필수): 변경 사항을 반영할 타겟 브랜치 이름 (예: `develop`)
  - `body`: PR에 대한 상세 설명
- **사용 예시:**
  > `'munchanghyeon/Fortify_SB' 레포지토리에서 'feature/new-login' 브랜치의 변경 내용을 'develop' 브랜치로 병합하는 PR을 생성해줘. 제목은 'feat: 새로운 소셜 로그인 기능 추가'로 하고, 본문에는 'OAuth2를 이용한 구글, 카카오 로그인 기능을 추가했습니다.'라고 적어줘.`

#### **PR 목록 및 상세 내용 확인 (list_pull_requests, get_pull_request_files)**
- **기능 상세 설명:** 레포지토리에 현재 열려있거나 닫힌 PR 목록을 확인하고, 특정 PR에 어떤 파일들이 변경되었는지 상세 내역을 조회할 수 있습니다.
- **사용 예시:**
  > `'munchanghyeon/Fortify_SB' 레포지토리에 열려 있는(open) 모든 PR 목록을 보여줘.`
  > `'munchanghyeon/Fortify_SB' 레포지토리의 12번 PR에서 변경된 파일 목록과 변경 내용을 알려줘.`

#### **PR 리뷰 및 병합 (create_pull_request_review, merge_pull_request)**
- **기능 상세 설명:** PR에 대해 리뷰 코멘트를 남기거나, 변경 사항을 승인(Approve)할 수 있습니다. 리뷰가 완료된 PR은 타겟 브랜치에 병합(Merge)하여 변경 사항을 최종 반영합니다.
- **주요 파라미터 (`merge_pull_request`):**
  - `owner`, `repo`, `pull_number` (필수)
  - `merge_method`: `merge`, `squash`, `rebase` 중 선택
- **사용 예시:**
  > `'munchanghyeon/Fortify_SB' 레포지토리의 12번 PR에 '수고하셨습니다. 코드 확인했습니다.' 라고 리뷰 코멘트를 남기고 승인(Approve)해줘.`
  > `'munchanghyeon/Fortify_SB' 레포지토리의 12번 PR을 'squash and merge' 방식으로 병합하고, 커밋 메시지는 'feat: Add social login feature (#12)'로 해줘.`
