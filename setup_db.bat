@echo off
echo Setting up Lost and Found Database...

set MYSQL_PATH="C:\xampp\mysql\bin\mysql.exe"

if not exist %MYSQL_PATH% (
    echo Error: MySQL executable not found at %MYSQL_PATH%
    echo Please make sure XAMPP is installed and MySQL path is correct.
    pause
    exit /b 1
)

%MYSQL_PATH% -u root < db_schema.sql

if %ERRORLEVEL% EQU 0 (
    echo Database setup successful! Tables created.
) else (
    echo Error setting up database. Please check MySQL is running.
)
pause
