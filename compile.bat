@echo off
echo Cleaning old .class files...

del /s /q WEB-INF\classes\*.class

set CLASSPATH=C:\xampp\tomcat\lib\servlet-api.jar;WEB-INF\lib\*;.

echo Compiling MODEL classes...
for /r WEB-INF\classes\com\lostandfound\model %%f in (*.java) do (
    javac --release 17 -cp "%CLASSPATH%" -d WEB-INF\classes "%%f"
)

echo Compiling UTIL classes...
for /r WEB-INF\classes\com\lostandfound\util %%f in (*.java) do (
    javac --release 17 -cp "%CLASSPATH%;WEB-INF\classes" -d WEB-INF\classes "%%f"
)

echo Compiling DAO classes...
for /r WEB-INF\classes\com\lostandfound\dao %%f in (*.java) do (
    javac --release 17 -cp "%CLASSPATH%;WEB-INF\classes" -d WEB-INF\classes "%%f"
)

echo Compiling SERVLET classes...
for /r WEB-INF\classes\com\lostandfound\servlet %%f in (*.java) do (
    javac --release 17 -cp "%CLASSPATH%;WEB-INF\classes" -d WEB-INF\classes "%%f"
)

echo Compilation complete.
pause