#!/bin/bash

# Git Hook 설정 스크립트
# README.md 자동 업데이트를 위한 pre-commit hook 설정

echo "Git Hook 설정 중..."

# .git/hooks 디렉토리 확인
if [ ! -d ".git/hooks" ]; then
    echo "Error: Git 저장소가 아닙니다."
    exit 1
fi

# pre-commit hook 생성
cat > .git/hooks/pre-commit << 'EOF'
#!/bin/bash

# README.md 자동 업데이트 pre-commit hook

echo "README.md 업데이트 확인 중..."

# Java 파일이 변경되었는지 확인
if git diff --cached --name-only | grep -E '\.(java|properties)$' > /dev/null; then
    echo "Java 파일 변경 감지. README.md 업데이트 중..."
    
    # Gradle 태스크 실행
    ./gradlew updateReadme --quiet
    
    # README.md가 변경되었다면 스테이징에 추가
    if [ -n "$(git diff README.md)" ]; then
        echo "README.md가 업데이트되었습니다."
        git add README.md
    fi
fi

echo "README.md 업데이트 완료."
EOF

# pre-commit hook 실행 권한 부여
chmod +x .git/hooks/pre-commit

echo "Git Hook 설정 완료!"
echo "이제 Java 파일을 커밋할 때마다 README.md가 자동으로 업데이트됩니다."