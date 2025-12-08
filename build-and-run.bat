@echo off
echo Building RevCart Application...

echo.
echo Step 1: Building Backend JAR...
cd backend
call mvn clean package -DskipTests
cd ..

echo.
echo Step 2: Building Docker Images...
docker-compose build

echo.
echo Step 3: Starting Containers...
docker-compose up -d

echo.
echo Done! Application is starting...
echo Frontend: http://localhost
echo Backend: http://localhost:8080/api
echo.
echo Check status: docker-compose ps
echo View logs: docker-compose logs -f
