FROM maven:3.6.3-jdk-11-openj9

COPY src /usr/src/jdah-bot
WORKDIR /usr/src/jdah-bot

RUN mvn -B package --file pom.xml
COPY target/* /usr/target/jdah-bot
RUN rm -rf /usr/src/jdah-bot

ENTRYPOINT ["java", "-jar", "/usr/target/jdah-bot/jdahbot-0.0.1-SNAPSHOT.jar"]