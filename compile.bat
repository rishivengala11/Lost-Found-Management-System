@echo off
echo Compiling Java files...

REM Adjust these paths if your Tomcat is installed elsewhere
REM Common paths: "C:\Program Files\Apache Software Foundation\Tomcat 9.0\lib\servlet-api.jar"
REM or XAMPP: "C:\xampp\tomcat\lib\servlet-api.jar"

set CLASSPATH="C:\xampp\tomcat\lib\servlet-api.jar";"WEB-INF\classes";"WEB-INF\lib\javax.mail.jar";"WEB-INF\lib\activation.jar";.

if not exist "WEB-INF\classes" mkdir "WEB-INF\classes"

javac -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\util\DBConnection.java
javac -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\model\LostItem.java
javac -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\model\FoundItem.java
javac -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\dao\LostItemDAO.java
javac -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\dao\FoundItemDAO.java
javac -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\servlet\*.java

echo Compilation complete.
pause
