@echo off
REM Set remote debugging parameters
SET JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
REM Start the service
CALL ..\gradlew :llm-service:bootRun