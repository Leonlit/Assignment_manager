#!/bin/bash
# mvn clean compile package 
jarFile=$(basename $(find -type f -name "*.jar"))
IFS='-'
read -ra fileNameArr <<< "$jarFile"
IFS=' '
appVersion="${fileNameArr[1]%.*}"
jpackage --name Assignment_manager --description "A simple assignment manager for students" --vendor "leonlit" --app-version $appVersion --input target --dest deployment --main-jar $jarFile
