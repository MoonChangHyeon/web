# Gemini CLI (MCP) 기본 정보 및 사용 명령어

이 문서는 현재 Gemini CLI 환경에서 사용할 수 있는 주요 명령어(도구)에 대한 기본 정보를 제공합니다. 각 명령어는 특정 작업을 자동화하고 개발 생산성을 높이는 데 사용됩니다.

## 📖 파일 시스템

-   `list_directory(path: str)`: 지정된 디렉토리의 파일 및 하위 디렉토리 목록을 보여줍니다.
    -   **사용 예시:** `list_directory(path='src/main')`
-   `read_file(absolute_path: str)`: 지정된 파일의 내용을 읽어옵니다.
    -   **사용 예시:** `read_file(absolute_path='/Users/munchanghyeon/Documents/Workspce/src/Fortify_SB/web/build.gradle')`
-   `read_many_files(paths: list[str])`: 여러 파일의 내용을 한 번에 읽어옵니다.
    -   **사용 예시:** `read_many_files(paths=['src/main/java/com/fortify/web/controller/HomeController.java', 'src/main/resources/templates/home.html'])`
-   `write_file(file_path: str, content: str)`: 지정된 파일에 내용을 씁니다. 파일이 존재하지 않으면 새로 생성합니다.
    -   **사용 예시:** `write_file(file_path='new_file.txt', content='Hello, Gemini!')`
-   `replace(file_path: str, old_string: str, new_string: str)`: 파일 내용의 특정 문자열을 다른 문자열로 교체합니다. 정확한 일치를 위해 여러 줄의 컨텍스트를 포함해야 할 수 있습니다.
    -   **사용 예시:** `replace(file_path='build.gradle', old_string='Java 17', new_string='Java 21')`
-   `glob(pattern: str)`: 특정 패턴과 일치하는 파일 경로를 찾습니다. `**`를 사용하여 하위 디렉토리를 포함할 수 있습니다.
    -   **사용 예시:** `glob(pattern='src/main/java/com/fortify/web/**/*.java')`
-   `search_file_content(pattern: str)`: 여러 파일 내용에서 특정 정규식 패턴을 검색하여 일치하는 라인을 찾습니다.
    -   **사용 예시:** `search_file_content(pattern='@GetMapping', include='**/*Controller.java')`

## ⚙️ 쉘 명령어 실행

-   `run_shell_command(command: str)`: 터미널(쉘) 명령어를 직접 실행합니다. 빌드, 테스트, 시스템 명령어 실행에 유용합니다.
    -   **사용 예시:** `run_shell_command(command='./gradlew build')`
    -   **사용 예시:** `run_shell_command(command='ls -al')`

## 🌐 정보 검색 및 라이브러리

-   `google_web_search(query: str)`: 구글 웹 검색을 통해 최신 정보를 얻습니다.
    -   **사용 예시:** `google_web_search(query='Spring Boot 3 new features')`
-   `resolve_library_id(libraryName: str)`: 라이브러리 이름을 Context7 호환 ID로 변환합니다. `get_library_docs`를 사용하기 전에 라이브러리 ID를 찾는 데 사용됩니다.
    -   **사용 예시:** `resolve_library_id(libraryName='Spring Boot')`
-   `get_library_docs(context7CompatibleLibraryID: str)`: 특정 라이브러리의 공식 문서를 가져옵니다.
    -   **사용 예시:** `get_library_docs(context7CompatibleLibraryID='/spring-projects/spring-boot')`

## 🧠 기타

-   `save_memory(fact: str)`: Gemini가 사용자에 대한 특정 사실(선호도, 설정 등)을 기억하게 합니다.
    -   **사용 예시:** `save_memory(fact='나는 항상 Java 21 버전을 사용해.')`
-   `web_fetch(prompt: str)`: 주어진 URL의 웹 페이지 내용을 가져와 요약하거나 특정 정보를 추출합니다.
    -   **사용 예시:** `web_fetch(prompt='Summarize the main points of https://spring.io/blog')`
