FROM openjdk:17
ADD build/libs/discord-openai-bot-1.0-SNAPSHOT-all.jar /app/openai.jar
WORKDIR /app
CMD ["java", "-jar", "openai.jar"]
