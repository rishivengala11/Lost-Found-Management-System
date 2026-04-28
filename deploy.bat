@echo off
echo Deploying Lost_Found to Tomcat...

set TARGET_DIR="C:\xampp\tomcat\webapps\Lost_Found"

if not exist %TARGET_DIR% mkdir %TARGET_DIR%

echo Copying files to %TARGET_DIR%...
xcopy /s /y /i "css" %TARGET_DIR%\css
xcopy /s /y /i "js" %TARGET_DIR%\js
xcopy /s /y /i "WEB-INF" %TARGET_DIR%\WEB-INF
xcopy /y "index.html" %TARGET_DIR%
xcopy /y "report-lost.html" %TARGET_DIR%
xcopy /y "report-found.html" %TARGET_DIR%
xcopy /y "search.html" %TARGET_DIR%
xcopy /y "matches.html" %TARGET_DIR%
xcopy /y "login.html" %TARGET_DIR%
xcopy /y "admin-dashboard.html" %TARGET_DIR%
xcopy /y "my-items.html" %TARGET_DIR%

echo Deployment complete!
echo Please restart Tomcat to apply changes.
pause
