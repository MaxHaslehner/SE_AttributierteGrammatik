#!/bin/bash
java -jar Coco.jar Tasks.atg -frames frm -o src
javac -d cls src/Scanner.java src/Parser.java src/Tasks.java
jar cfmv Tasks.jar Tasks.mf -C cls .