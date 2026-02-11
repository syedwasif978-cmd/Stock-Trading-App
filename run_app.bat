@echo off
echo ============================================
echo Stock Trading App - Build & Run
echo ============================================
echo.
echo Building application...
call mvn clean install
if %errorlevel% neq 0 (
    echo Build failed!
    pause
    exit /b %errorlevel%
)

echo.
echo Running application...
cd target
for /r %%i in (*.jar) do java -jar "%%i"
pause
