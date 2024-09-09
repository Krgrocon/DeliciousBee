# 빌드 스테이지
FROM openjdk:21-jdk-slim AS build-env

WORKDIR /app
RUN apt-get update && apt-get install -y git
# 깃허브 저장소에서 코드 가져오기 (필요한 경우 SSH Key 설정)
RUN git clone https://github.com/Krgrocon/DeliciousBee.git

WORKDIR /app/deliciousbee

# Gradle Wrapper 파일 복사 및 실행 권한 설정
COPY gradlew gradlew.bat ./
COPY gradle/wrapper ./gradle/wrapper
RUN chmod +x gradlew

# 빌드 파일 복사
COPY settings.gradle.kts build.gradle.kts ./
# Gradle 빌드
RUN ./gradlew build

# 실행 스테이지
FROM openjdk:21-jre-slim

WORKDIR /app

# 빌드 스테이지에서 빌드 결과물 복사 (경로 수정)
COPY --from=build-env /app/deliciousbee/build/libs/*.jar app.jar

# 애플리케이션 실행 명령어
ENTRYPOINT ["java","-jar","/app.jar"]