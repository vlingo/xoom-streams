# Generate fat-jar
#FROM maven AS maven
#WORKDIR /home/compiler
#ADD ./src ./src
#ADD ./pom.xml ./pom.xml
#RUN mvn install

# Generate a native-image
FROM oracle/graalvm-ce:latest AS graalvm
RUN gu install native-image
#COPY --from=maven /home/compiler/target/pipes-1.0-SNAPSHOT-jar-with-dependencies.jar ./pipes.jar
ADD target/pipes-1.0-SNAPSHOT-jar-with-dependencies.jar ./pipes.jar
ADD build/reflection.json ./reflection.json

RUN native-image \
    -H:ReflectionConfigurationFiles=reflection.json \
    --report-unsupported-elements-at-runtime\
    --initialize-at-build-time=org.slf4j\
    --allow-incomplete-classpath\
    --static \
    -jar pipes.jar \
    pipes

# Package into a scratch image
FROM scratch
WORKDIR /usr/bin
COPY --from=graalvm pipes ./

CMD pipes
