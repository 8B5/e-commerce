@echo off
REM Terminal encoding setup for Korean Windows environment

echo Setting up terminal encoding for better Korean support...

REM Set UTF-8 code page
chcp 65001 > nul

REM Check current code page
echo Current code page: 
chcp

REM Set environment variables for better UTF-8 support
setx JAVA_TOOL_OPTIONS "-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8" > nul 2>&1
setx GRADLE_OPTS "-Dfile.encoding=UTF-8 -Dconsole.encoding=UTF-8" > nul 2>&1

echo.
echo Terminal encoding setup completed!
echo.
echo Recommendations:
echo 1. Use Windows Terminal instead of Command Prompt for better Unicode support
echo 2. Set your terminal font to a font that supports Korean (e.g., D2Coding, Consolas)
echo 3. Restart your terminal for changes to take effect
echo.
echo To install Windows Terminal:
echo   winget install Microsoft.WindowsTerminal
echo.
echo To check if encoding is working:
echo   echo Test Korean: 한글 테스트
echo.

pause