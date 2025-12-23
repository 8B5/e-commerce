@echo off
REM Git Hook 설정 스크립트 (Windows)
REM README.md 자동 업데이트를 위한 pre-commit hook 설정

echo Git Hook 설정 중...

REM .git/hooks 디렉토리 확인
if not exist ".git\hooks" (
    echo Error: Git 저장소가 아닙니다.
    exit /b 1
)

REM pre-commit hook 생성
(
echo #!/bin/bash
echo.
echo # README.md 자동 업데이트 pre-commit hook
echo.
echo echo "README.md 업데이트 확인 중..."
echo.
echo # Java 파일이 변경되었는지 확인
echo if git diff --cached --name-only ^| grep -E '\.\(java^|properties\)$' ^> /dev/null; then
echo     echo "Java 파일 변경 감지. README.md 업데이트 중..."
echo.    
echo     # Gradle 태스크 실행
echo     ./gradlew updateReadme --quiet
echo.    
echo     # README.md가 변경되었다면 스테이징에 추가
echo     if [ -n "$\(git diff README.md\)" ]; then
echo         echo "README.md가 업데이트되었습니다."
echo         git add README.md
echo     fi
echo fi
echo.
echo echo "README.md 업데이트 완료."
) > .git\hooks\pre-commit

echo Git Hook 설정 완료!
echo 이제 Java 파일을 커밋할 때마다 README.md가 자동으로 업데이트됩니다.

pause