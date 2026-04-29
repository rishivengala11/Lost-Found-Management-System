@echo off
echo Compiling Java files...

set CLASSPATH="C:\xampp\tomcat\lib\servlet-api.jar";"WEB-INF\classes";"WEB-INF\lib\javax.mail.jar";"WEB-INF\lib\activation.jar";.

if not exist "WEB-INF\classes" mkdir "WEB-INF\classes"

javac --release 17 -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\util\DBConnection.java
javac --release 17 -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\model\LostItem.java
javac --release 17 -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\model\FoundItem.java
javac --release 17 -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\dao\LostItemDAO.java
javac --release 17 -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\dao\FoundItemDAO.java
javac --release 17 -d "WEB-INF\classes" -cp %CLASSPATH% WEB-INF\classes\com\lostandfound\servlet\*.java
echo Compilation complete.
pause