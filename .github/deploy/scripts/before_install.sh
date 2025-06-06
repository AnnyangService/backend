#!/bin/bash

# 애플리케이션 디렉토리 생성
if [ ! -d /app ]; then
  mkdir -p /app
fi

# 기존 애플리케이션 파일 삭제
rm -rf /app/hi-meow-api-server.jar

# 서비스가 존재하면 중지
if systemctl list-unit-files | grep -q api-server.service; then
  systemctl stop api-server || true
fi