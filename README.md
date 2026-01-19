# Class Hub Backend


### 🌿 브랜치 전략
```text
main → develop → feature/*
                ├─ feature/login
                └─ feature/reservation
```

- **feature/***: 기능 개발 브랜치 (예: `feature/login`, `feature/reservation`)
    - 기능 구현 후 **PR로 develop에 머지**
- **develop**: 통합/검증 브랜치
    - 코드리뷰 완료된 기능을 모아 **배포 전 최종 검증**
- **main**: 운영 배포 브랜치
    - **직접 push 금지** (PR merge만 허용)


---

### 🔄 CI/CD 동작 규칙

### CI (Continuous Integration)
- 트리거: main 또는 develop 브랜치에 **PR 생성**
    - `feature/* → develop`
    - `develop → main`
- 수행 작업
    - ✅ Test (단위 테스트)
    - ✅ Build (Spring Boot JAR 생성)

⚠️ CI가 통과해야 PR을 merge할 수 있도록 **브랜치 보호 규칙(Require status checks)** 을 적용

### CD (Continuous Deployment)
- 트리거: main 브랜치에 **push**
- 수행 작업
  - ✅ Build (Spring Boot JAR 생성)
  - ✅ Deploy 
    - Docker 이미지 빌드 
    - ECR에 이미지 Push 
    - EC2에서 최신 이미지 Pull 후 컨테이너 재시작

