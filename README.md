# Backend

## Java Version

Java 21

## 로컬 개발 환경

- Spring Boot 애플리케이션: IntelliJ IDEA에서 실행
- MySQL 8.4: Docker Compose로 실행

### 사전 준비

- Java 21
- Docker Desktop
- IntelliJ IDEA

## MySQL Docker 설정

### 1. 환경변수 파일 생성

프로젝트 루트에서 `.env.example`을 복사해 `.env`를 생성합니다.

#### Windows PowerShell

```powershell
Copy-Item .env.example .env
```

#### macOS / Linux

```bash
cp .env.example .env
```

생성한 `.env`에 로컬 DB 설정을 입력합니다. 애플리케이션 접속 계정으로 `root`를 사용하지 않습니다.

```dotenv
DB_HOST=localhost
DB_PORT=3306
DB_NAME=mashup4
DB_USERNAME=mashup4
DB_PASSWORD=mashup4
DB_ROOT_PASSWORD=root
```

`.env`에는 로컬 비밀번호가 포함되므로 Git에 커밋하지 않습니다.

### 2. MySQL 실행

프로젝트 루트에서 다음 명령을 실행합니다.

```bash
docker compose up -d
```

컨테이너 상태를 확인합니다.

```bash
docker compose ps
```

MySQL 컨테이너가 `healthy` 상태가 되면 실행 준비가 완료된 것입니다. 문제가 발생하면 로그를 확인합니다.

```bash
docker compose logs -f mysql
```

로그 확인은 `Ctrl+C`로 종료할 수 있으며, 컨테이너 자체는 계속 실행됩니다.

### 3. IntelliJ 환경변수 설정

IntelliJ에서 다음 메뉴로 이동합니다.

```text
Run → Edit Configurations... → Mashup4BackendApplication
```

`Environment variables`가 보이지 않으면 다음 메뉴에서 추가합니다.

```text
Modify options → Environment variables
```

`Environment variables` 오른쪽의 파일 선택 버튼에서 프로젝트 루트의 `.env`를 선택합니다.

```text
$PROJECT_DIR$/.env
```

`.env` 파일 선택 기능이 없는 IntelliJ 버전에서는 다음 값을 직접 입력합니다.

```text
DB_HOST=localhost;DB_PORT=3306;DB_NAME=mashup4;DB_USERNAME=mashup4;DB_PASSWORD=mashup4
```

`DB_ROOT_PASSWORD`는 MySQL 컨테이너 초기화에만 사용되므로 Spring Boot 실행 환경변수에는 필요하지 않습니다.

환경변수를 수정했다면 실행 중인 Spring Boot 애플리케이션을 종료한 후 다시 실행해야 합니다.

### 4. Spring Boot 실행

MySQL 컨테이너가 `healthy` 상태인지 확인한 뒤 IntelliJ에서 `Mashup4BackendApplication`을 실행합니다.

기본 DB 접속 주소는 다음과 같습니다.

```text
jdbc:mysql://localhost:3306/mashup4
```



## Health Check API 경로

`GET /api/health`

http://localhost:8080/api/health

## 실행 방법

### Windows

```bash
.\gradlew bootRun
```

### macOS / Linux

```bash
./gradlew bootRun
```
