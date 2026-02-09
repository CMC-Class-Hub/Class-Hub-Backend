# JRE 17 사용
FROM eclipse-temurin:17-jre-alpine

# 작업 디렉토리 설정
WORKDIR /app

# KST 타임존 데이터 설치 + Asia/Seoul 설정
RUN apk add --no-cache tzdata \
  && cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
  && echo "Asia/Seoul" > /etc/timezone

# JVM도 KST로 강제 (로그/LocalDateTime 등에 영향)
ENV TZ=Asia/Seoul
ENV JAVA_TOOL_OPTIONS="-Duser.timezone=Asia/Seoul"

# 빌드된 JAR 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 실행 명령어
ENTRYPOINT ["java","-jar","app.jar"]