@ECHO OFF

CALL mvn clean package
echo.
curl --output postgres.jar https://jdbc.postgresql.org/download/postgresql-42.7.8.jar
echo.
docker build -t fishmaps-payara .
echo.
docker-compose up