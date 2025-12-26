@echo off
chcp 65001 > nul
REM Git Hook Setup Script (Windows)
REM Setup pre-commit hook for automatic README.md updates

echo Setting up Git hooks...

REM Check .git/hooks directory
if not exist ".git\hooks" (
    echo Error: Not a Git repository.
    exit /b 1
)

REM Create pre-commit hook
(
echo #!/bin/bash
echo.
echo # README.md automatic update pre-commit hook
echo.
echo echo "Checking README.md updates..."
echo.
echo # Check if Java files have been changed
echo if git diff --cached --name-only ^| grep -E '\.\(java^|properties^|yml^|yaml\)$' ^> /dev/null; then
echo     echo "Java/config files changed. Updating README.md..."
echo.    
echo     # Run Gradle task
echo     ./gradlew updateReadme --quiet
echo.    
echo     # Add README.md to staging if changed
echo     if [ -n "$\(git diff README.md\)" ]; then
echo         echo "README.md has been updated."
echo         git add README.md
echo     fi
echo fi
echo.
echo echo "README.md update completed."
) > .git\hooks\pre-commit

echo Git hooks setup completed!
echo Java files will now automatically update README.md when committed.

pause