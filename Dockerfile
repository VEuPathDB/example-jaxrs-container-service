# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Build Service & Dependencies
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM veupathdb/alpine-dev-base:latest AS prep

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

LABEL service="demo-service"

WORKDIR /workspace
RUN jlink --compress=2 --module-path /opt/jdk/jmods \
       --add-modules java.base,java.logging,java.xml,java.desktop,java.management,java.sql,java.naming \
       --output /jlinked \
    && apk add --no-cache git sed findutils coreutils make npm curl gawk \
    && git config --global advice.detachedHead false

ENV DOCKER=build

COPY ["gradlew", "makefile", "./"]
COPY gradle/ gradle/
RUN echo "Installing Gradle" \
    && ./gradlew wrapper \
    && make install-dev-env

COPY [ \
    "build.gradle.kts", \
    "dependencies.gradle.kts", \
    "settings.gradle.kts", \
    "service.properties", \
    "./"\
]

RUN ./gradlew dependencies --info --configuration runtimeClasspath

COPY . .

RUN mkdir -p vendor \
    && cp -n /jdbc/* vendor \
    && make jar

# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Run the service
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM foxcapades/alpine-oracle:1.3

LABEL service="demo-service"

ENV JAVA_HOME=/opt/jdk \
    PATH=/opt/jdk/bin:$PATH

COPY --from=prep /jlinked /opt/jdk
COPY --from=prep /workspace/build/libs/service.jar /service.jar

CMD java -jar /service.jar
