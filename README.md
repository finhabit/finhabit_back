# ☘️ finhabit_back 

## Project Overview☘️
> 맞춤형 금융 교육 및 서비스 제공 플랫폼
- 목적 : 작은 미션과 금융 지식을 통한 돈 관리 습관 형성 및 경제 문해력 향상
- 주요 기능 : 금융 지식 카드, 퀴즈, 미션, 가계부
- 대상 :  금융 지식이 필요하고 소비 습관 개선이 필요한 일반인

<br>

## Contributors☘️
|                             김나은<br/>([@naeuun](https://github.com/naeuun))                          |                            이영서<br/>([@136lee](https://github.com/136lee))                          |
|:--------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------:|
| <img width="200" alt="image" src="https://github.com/user-attachments/assets/de348bcb-5519-4978-923c-9060e45d664c" /> |<img width="200" alt="image" src="https://github.com/user-attachments/assets/cbd43047-725a-4bd7-b9bd-a89eddb54ca7" />
|     `배포`<br/>`금융지식`<br/>`퀴즈`<br/>`마이페이지`      |     `로그인/회원가입`<br/>`미션`<br/>`가계부`<br/>`알림`  |

<br>

## Tech Stack☘️

**Collaboration**

![Figma](https://img.shields.io/badge/figma-E0474C?style=for-the-badge&logo=figma&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)
![Github](https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white)

**Backend**

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white)

**Database**

![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![AWS RDS](https://img.shields.io/badge/AWS_RDS-527FFF?style=for-the-badge&logo=amazon-rds&logoColor=white)

**DevOps**

![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-2088FF?style=for-the-badge&logo=github-actions&logoColor=white)

**Documentation**

![Swagger](https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

**Code Quality**

![Spotless](https://img.shields.io/badge/Spotless-2C2255?style=for-the-badge&logo=gradle&logoColor=white)
![Lombok](https://img.shields.io/badge/Lombok-BC4521?style=for-the-badge&logo=lombok&logoColor=white)

<br>

## Development Architecture☘️
```
Client (React/Flutter)
         ↓
    Spring Boot API
         ↓
    Spring Security (인증/인가)
         ↓
    Spring Data JPA
         ↓
    MySQL (AWS RDS)
         ↓
    Docker + AWS (배포)
```

<br>

## Key Features☘️

### 1. 금융 지식 카드
- 일일 금융 지식 제공
- 사용자 수준별 금융 상식 학습

### 2. 퀴즈
- 금융 지식 확인 퀴즈
- 모아보기 제공

### 3. 미션
- 일일 금융 습관 미션

### 4. 가계부
- 수입/지출 관리
- 소비 패턴 분석

<br>

## API Documentation☘️

API 문서는 Swagger를 통해 제공됩니다. 

- **배포 Swageer 주소**:  [**🔗 finhabit.shop/swagger-ui**](https://www.finhabit.shop/swagger-ui/index.html)

<br>

## Code Style & Quality☘️

### Spotless (Google Java Format - AOSP)
```bash
# 코드 스타일 체크
./gradlew spotlessCheck

# 코드 자동 포맷팅
./gradlew spotlessApply
```

**적용된 규칙:**
- Google Java Format (AOSP 스타일)
- Import 정렬
- 사용하지 않는 import 제거
- 줄 끝 공백 제거
- 파일 끝 개행 추가

> **⚠️ 주의**:  PR 생성 전 반드시 `spotlessApply`를 실행해주세요!

<br>

## Database Configuration☘️

### AWS RDS MySQL 연결

**환경별 설정:**
- **Local**: `application-local.yml` (Local MySQL)
- **Prod**: `application-prod.yml` (AWS RDS)

> **🔒 보안**:  DB 접속 정보는 `config` submodule에서 관리되며, 환경변수로 주입됩니다. 

<br>

## Branch Strategy☘️

```
- main (배포용)
- BACKEND (개발용)
- feature/#이슈번호 (작업용)
```

### Workflow
1. `feature` 브랜치에서 작업
2. **코드 포맷팅 적용** (`./gradlew spotlessApply`)
3. `BACKEND` 브랜치로 PR 생성
4. 코드 리뷰 후 merge
5. 배포 시 `main` 브랜치로 merge

<br>

## Deployment☘️

### 배포 환경

- **서버**: AWS EC2
- **데이터베이스**: AWS RDS (MySQL 8.0)
- **컨테이너**: Docker
- **도메인**: `https://www.finhabit.shop`
<br>

## Contributing☘️

자세한 협업 가이드는 [CONTRIBUTING. md](CONTRIBUTING.md)를 참고해주세요. 

<br>


## Project Structure☘️

```
📁 finhabit_back
|-- 📁 . github
|   |-- 📁 workflows
|-- 📁 src
|   |-- 📁 main
|   |   |-- 📁 java
|   |   |   |-- 📁 com. ll. finhabit
|   |   |   |   |-- 📁 domain
|   |   |   |   |   |-- 📁 auth
|   |   |   |   |   |-- 📁 mission
|   |   |   |   |   |-- 📁 quiz
|   |   |   |   |   |-- 📁 finance
|   |   |   |   |   |-- 📁 ledger
|   |   |   |   |-- 📁 global
|   |   |   |   |   |-- 📁 common
|   |   |   |   |   |-- 📁 security
|   |   |   |   |   |-- 📁 exception
|   |   |   |   |   |-- 📁 interceptor
|   |   |   |   |   |-- 📁 resolver
|   |   |   |   |-- |📁 config
|   |   |-- 📁 resources
|   |   |   |-- application.yml
|   |-- 📁 test
|   |   |-- 📁 java
|-- 📁 config (submodule)
|   |-- application-*. yml
|-- 📁 deploy
|   |-- docker-compose files
|-- 📁 gradle
|-- build.gradle
|-- settings.gradle
|-- Dockerfile
|-- docker-compose.local.yml
|-- docker-compose.dev.yml
|-- . gitignore
|-- .dockerignore
|-- README.md
|-- CONTRIBUTING.md
|-- HELP.md
```

<br>
