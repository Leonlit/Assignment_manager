call mvn clean compile package
call jpackage --name Assignment_manager_1.1.0_win_installer --description "A simple assignment manager for students" --vendor "leonlit" --app-version 1.1.0 --input target --dest deployment --main-jar Assignment_manager-1.1.0.jar
