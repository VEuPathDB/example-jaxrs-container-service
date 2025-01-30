#
# Help/Usage
#

.PHONY: default
default:
	@echo "Please choose one of:"
	@echo ""
	@echo "  make install-dev-env"
	@echo "    Checks the development environment for initial build dependencies "
	@echo "    and downloads them if possible."
	@echo ""
	@echo "  make raml-gen-code"
	@echo "    Generates Java classes representing API interfaces as "
	@echo "    defined in api.raml and child types."
	@echo ""
	@echo "  make raml-gen-docs"
	@echo "    Generates HTML documentation files from the RAML API definition "
	@echo "    files."
	@echo ""
	@echo "  make compile"
	@echo "    Compiles the existing code in 'src/'."
	@echo ""
	@echo "  make test"
	@echo "    Compiles the existing code in 'src/' and runs unit tests."
	@echo ""
	@echo "  make jar"
	@echo "    Compiles a 'fat jar' from this project and its dependencies."
	@echo ""
	@echo "  make docker"
	@echo "    Builds a runnable docker image for this service"
	@echo ""

#
# Development environment setup / teardown
#

.PHONY: install-dev-env
install-dev-env:
	@./gradlew check-env
	@npm i -g raml2html@7.8.0
	@npm i -g raml2html-modern-theme@1.0.8

.PHONY: clean
clean:
	@./gradlew clean
	@rm -rf .bin .gradle

#
# Code & Doc Generation
#

.PHONY: raml-gen-code
raml-gen-code:
	./gradlew generate-jaxrs

.PHONY: raml-gen-docs
raml-gen-docs:
	./gradlew generate-raml-docs

#
# Build & Test Targets
#

.PHONY: compile
compile:
	./gradlew clean compileJava

.PHONY: test
test:
	./gradlew clean test

.PHONY: jar
jar: build/libs/service.jar

.PHONY: docker
docker:
	./gradlew build-docker --stacktrace

#
# File based targets
#

build/libs/service.jar: build.gradle.kts
	./gradlew clean test generate-raml-docs shadowJar
