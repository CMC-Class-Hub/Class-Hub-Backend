# JDK 17 사용
FROM openjdk:17

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 JAR 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 실행 명령어
ENTRYPOINT ["java","-jar","app.jar"]