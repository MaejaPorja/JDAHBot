FROM maven:3.6.3-jdk-11-openj9

COPY . /usr/project/jdah-bot
WORKDIR /usr/project/jdah-bot

RUN ls -la

RUN mvn -B package --file pom.xml
RUN ls -la
RUN cp -r /usr/project/jdah-bot/target/* /usr/target/jdah-bot
RUN rm -rf /usr/project/jdah-bot

ENTRYPOINT ["java", "-jar", "/usr/target/jdah-bot/jdahbot-0.0.1-SNAPSHOT.jar"]