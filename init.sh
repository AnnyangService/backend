#!/bin/bash

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🔄 Git 저장소 최신화 중...${NC}"
git fetch origin
git checkout main
git pull origin main

# 환경 변수 설정 (기본값: development)
export SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-dev}

echo -e "${BLUE}🚀 로컬 개발 환경 구성을 시작합니다...${NC}"
echo -e "활성화된 프로파일: $SPRING_PROFILES_ACTIVE"

# Docker 컨테이너 실행 여부 확인
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker가 실행되고 있지 않습니다. Docker를 실행해주세요.${NC}"
    exit 1
fi

# 이전 컨테이너 정리 (선택적)
if [ "$1" == "clean" ]; then
    echo -e "${BLUE}🧹 이전 컨테이너 정리 중...${NC}"
    docker-compose down -v
fi

echo -e "${BLUE}🐳 Docker 컨테이너 구성 중...${NC}"
docker-compose up -d

# 컨테이너 상태 확인
echo -e "${BLUE}🔍 서비스 상태 확인 중...${NC}"
docker-compose ps

echo -e "${GREEN}✨ 개발 환경 구성이 완료되었습니다!${NC}"
echo -e "${GREEN}📝 서비스 접속 정보:${NC}"
echo -e "- Spring 애플리케이션: http://localhost:8080"
echo -e "- MariaDB: localhost:3306"
echo -e "  - 데이터베이스: hi_meow"
echo -e "  - 사용자: admin"
echo -e "  - 비밀번호: 1234"

# 로그 확인을 위한 안내
echo -e "\n로그를 확인하려면 다음 명령어를 사용하세요:"
echo -e "- Spring 로그: ${BLUE}docker-compose logs -f spring-app${NC}"
echo -e "- MariaDB 로그: ${BLUE}docker-compose logs -f mariadb${NC}"
