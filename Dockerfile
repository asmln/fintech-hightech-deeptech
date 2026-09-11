### Сборка всего проекта
FROM eclipse-temurin:25-jdk-noble AS builder
WORKDIR /app

# Копируем файлы сборки
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY shared/build.gradle.kts shared/
COPY balance-viewer/build.gradle.kts balance-viewer/
COPY tx-processor/build.gradle.kts tx-processor/

# Загружаем зависимости
RUN ./gradlew dependencies --no-daemon || true

# Копируем исходный код и собираем проект
COPY . .
RUN ./gradlew :tx-processor:bootJar :balance-viewer:bootJar --no-daemon
###

# Контейнер для модуля tx-processor
FROM eclipse-temurin:25-jre-noble AS tx-processor
WORKDIR /app
# Копируем собранный jar-файл из этапа сборщика
COPY --from=builder /app/tx-processor/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

# Контейнер для модуля balance-viewer
FROM eclipse-temurin:25-jre-noble AS balance-viewer
WORKDIR /app
# Копируем собранный jar-файл из этапа сборщика
COPY --from=builder /app/balance-viewer/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]