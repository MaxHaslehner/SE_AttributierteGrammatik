set JAVA_HOME="C:\dev.tools\jdk19"
set PATH=%JAVA_HOME%\bin

java -jar Coco.jar Tasks.atg -frames frm -o src
javac.exe -Xlint:-removal -d cls src\Parser.java src\Scanner.java src\Tasks.java
jar cfmv Tasks.jar Tasks.mf -C cls .
pause