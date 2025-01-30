# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Build Service & Dependencies
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM veupathdb/alpine-dev-base:jdk23-gradle8.11 AS prep

ARG GITHUB_USERNAME
ARG GITHUB_TOKEN

WORKDIR /workspace

RUN apk add --no-cache npm \
  && npm install -gs raml2html raml2html-modern-theme

# copy files required to build dev environment and fetch dependencies
COPY build.gradle.kts settings.gradle.kts ./

# download raml tools (these never change)
RUN gradle install-raml-4-jax-rs install-raml-merge
# download project dependencies in advance
RUN gradle download-dependencies

# copy raml over for merging, then perform code and documentation generation
COPY api.raml ./
COPY schema schema
RUN gradle generate-jaxrs generate-raml-docs

# copy remaining files
COPY src/ src

# build the project
RUN gradle clean test shadowJar


# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
#
#   Run the service
#
# # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
FROM amazoncorretto:23.0.2-alpine3.21

LABEL service="example-service"

RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/America/New_York /etc/localtime \
    && echo "America/New_York" > /etc/timezone

ENV JAVA_HOME=/opt/jdk \
    PATH=/opt/jdk/bin:$PATH \
    JVM_MEM_ARGS="-Xms32M -Xmx256M" \
    JVM_ARGS=""

COPY --from=prep /workspace/build/libs/service.jar /service.jar

# Add this for full SSL logging: -Djavax.net.debug=all
CMD java -jar -XX:+CrashOnOutOfMemoryError $JVM_MEM_ARGS $JVM_ARGS /service.jar
