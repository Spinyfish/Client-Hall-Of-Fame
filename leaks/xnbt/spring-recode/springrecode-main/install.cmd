@echo OFF
TITLE Install and setup MCP
COLOR 0F
SETLOCAL ENABLEDELAYEDEXPANSION
CLS

:: 1.8.8 Version
SET MCP_LK=http://www.modcoderpack.com/files/mcp918.zip
SET MC_VEV=1.8.8
SET OPTIFINE_LK=http://hexeption.co.uk/optifine.zip

:: Downloading Beta MCP
:: TODO Check if there is already and tmp folder
ECHO Downloading MCP...
DEL tmp /S /Q /F
MD tmp
CD tmp
powershell "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12;(New-Object Net.WebClient).DownloadFile('%MCP_LK%', 'mcp.zip')"
ECHO.

:: Unzipping MCP
ECHO Unzipping MCP...
powershell "Expand-Archive mcp.zip -DestinationPath ."
DEL mcp.zip
ECHO.

:: Executing MCP
:mcp
runtime\bin\python\python_mcp runtime\decompile.py %*
CD ..
ECHO.
PAUSE

:Ask
ECHO Did you get an error with twitch libraries?(Y/N)
set INPUT=
set /P INPUT=Type input: %=%
If /I "%INPUT%"=="y" goto yes
If /I "%INPUT%"=="n" goto no
echo Incorrect input & goto Ask
:yes
ECHO Please rename your twitch libraries from 32 to 64
ECHO.
ECHO "twitch-platform-6.5-natives-windows-32.jar" to "twitch-platform-6.5-natives-windows-64"
ECHO "twitch-external-platform-4.5-natives-windows-32.jar" to "twitch-external-platform-4.5-natives-windows-64.jar"
ECHO.
ECHO These files can be found in at .minecraft/libraries/tv/twitch/
ECHO Please re-run the installer.
PAUSE
EXIT
:no

:: Copy the sources from temp to main
ECHO Copying Sources...
ROBOCOPY tmp\src\minecraft\ src\main\java /E
ECHO.

ECHO Rearrange..
MOVE src\main\java\Start.java src\main\java\net\minecraft\Start.java
ECHO.

ECHO Copying workspace..
ROBOCOPY tmp\jars\ workspace\ /MIR
ECHO.

:: Optifine
:AskOptifine
ECHO Would you like Optifine(Y/N)
set INPUT=
set /P INPUT=Type input: %=%
If /I "%INPUT%"=="y" goto yesOP
If /I "%INPUT%"=="n" goto noOP
echo Incorrect input & goto AskOptifine
:yesOP

ECHO == Downloading Optifine ==
MD tmp
CD tmp
powershell "(New-Object Net.WebClient).DownloadFile('%OPTIFINE_LK%', 'optifine.zip')"
ECHO.

ECHO == Unzipping Optifine ==
powershell "Expand-Archive optifine.zip -DestinationPath optifine"
DEL optifine.zip
CD ..
ECHO.

ECHO == Copying Optifine Sources ==
ROBOCOPY tmp\optifine\ src\main\java /E /is
ECHO.

ECHO == Rearrange assets ==
robocopy src\main\java\assets\ src\main\resources\assets\ /E /is /move
ECHO.

:noOP

:: Clean Up
ECHO Clean Up..
RMDIR tmp /S /Q
ECHO.

ECHO Done.
ECHO.

PAUSE