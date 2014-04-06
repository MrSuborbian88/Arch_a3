%ECHO OFF
%ECHO Starting ECS System
PAUSE
%ECHO ECS Monitoring Console
START "MUSEUM ENVIRONMENTAL CONTROL SYSTEM CONSOLE" /NORMAL java bin/edu/cmu/a3/ECSConsole %1
%ECHO Starting Temperature Controller Console
START "TEMPERATURE CONTROLLER CONSOLE" /MIN /NORMAL java bin/edu/cmu/a3/TemperatureController %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY CONTROLLER CONSOLE" /MIN /NORMAL java bin/edu/cmu/a3/HumidityController %1
START "TEMPERATURE SENSOR CONSOLE" /MIN /NORMAL java bin/edu/cmu/a3/TemperatureSensor %1
%ECHO Starting Humidity Sensor Console
START "HUMIDITY SENSOR CONSOLE" /MIN /NORMAL java bin/edu/cmu/a3/HumiditySensor %1
