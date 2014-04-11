%ECHO OFF
%ECHO Starting ECS System
PAUSE

SET CLASSPATH=bin

%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java edu.cmu.a3.ECSConsole %1

REM Original Sensors:
%ECHO Starting Temperature Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java edu.cmu.a3.TemperatureController %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY CONTROLLER CONSOLE" /MIN /NORMAL java edu.cmu.a3.HumidityController %1
START "TEMPERATURE SENSOR CONSOLE" /MIN /NORMAL java edu.cmu.a3.TemperatureSensor %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY SENSOR CONSOLE" /MIN /NORMAL java edu.cmu.a3.HumiditySensor %1

REM System A Components:
START "DOOR SENSOR" /MIN /NORMAL java edu.cmu.a3.SystemA.DoorSensor %1
START "MOTION SENSOR" /MIN /NORMAL java edu.cmu.a3.SystemA.MotionSensor %1
START "WINDOW SENSOR" /MIN /NORMAL java edu.cmu.a3.SystemA.WindowSensor %1
START "INTRUSION ALARM CONTROLLER" /MIN /NORMAL java edu.cmu.a3.SystemA.IntrusionAlarmControl %1
START "SECURITY MONITOR" /MIN /NORMAL java edu.cmu.a3.SystemA.SecurityMonitor %1

REM System B Components:
START "FIRE SENSOR" /MIN /NORMAL java edu.cmu.a3.SystemB.FireSensor %1
START "FIRE ALARM CONTOLLER" /MIN /NORMAL java edu.cmu.a3.SystemB.FireAlarmController %1
START "SPRINKLER CONTROLLER" /MIN /NORMAL java edu.cmu.a3.SystemB.SprinklerController %1
START "FIRE CONSOLE" /MIN /NORMAL java edu.cmu.a3.SystemB.FireConsole %1


