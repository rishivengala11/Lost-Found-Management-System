@echo off
set MYSQL_PATH="C:\xampp\mysql\bin\mysql.exe"
%MYSQL_PATH% -u root -e "SHOW TABLES FROM lost_found_db;"
pause
