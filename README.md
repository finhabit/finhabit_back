# finhabit_back
2학기 프로젝트 Finhabit 백엔드 레포

---

## 🛠️ 로컬 개발 환경 설정

### 최근 변경사항 (2024.12.01)

> 포트 충돌 해결을 위해 MySQL 포트가 `3306` → `13306`으로 변경되었습니다.

팀원들이 최신 코드를 pull 받은 후 아래 단계를 따라주세요:

#### 1️⃣ 기존 Docker 컨테이너 중지 및 제거

```bash
# 기존 컨테이너 중지 및 제거
docker-compose down

# 또는 특정 컨테이너만 제거하려면
docker stop finhabit-mysql-local finhabit-app-local
docker rm finhabit-mysql-local finhabit-app-local
```

#### 2️⃣ 서브모듈 업데이트

```bash
# config 서브모듈 최신화
git submodule update --init --recursive
```

#### 3️⃣ Docker 컨테이너 재시작

```bash
# 로컬 환경용 docker-compose로 컨테이너 실행
docker-compose -f docker-compose.local.yml up -d
```

#### 4️⃣ MySQL Workbench 연결 설정 변경

MySQL Workbench에서 새로운 포트로 연결 설정을 변경해야 합니다:

| 항목 | 값 |
|------|-----|
| Hostname | `localhost` |
| Port | `13306` (기존 3306에서 변경됨) |
| Username | `teamF` |
| Password | `finhabit_pw` |
| Default Schema | `finhabit_db` |

---

### 📋 전체 과정 요약 (Quick Guide)

```bash
# 1. 기존 컨테이너 정리
docker-compose down

# 2. 최신 코드 pull
git pull origin main

# 3. 서브모듈 업데이트
git submodule update --init --recursive

# 4. 새 컨테이너 실행
docker-compose -f docker-compose.local.yml up -d
```

그 후 **MySQL Workbench에서 포트를 `13306`으로 변경**하여 연결하세요.
