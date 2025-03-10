@echo off
set JAVA_HOME=%CD%\jre-21
"%JAVA_HOME%\bin\java" --module-path "%CD%\javafx-sdk-23.0.2\lib" --add-modules javafx.controls,javafx.fxml -jar "%CD%\HCA_Beta_1_0.jar"
pause