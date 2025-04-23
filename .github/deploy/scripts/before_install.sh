#!/bin/bash
# 애플리케이션 디렉토리 생성
if [ ! -d /app ]; then
  mkdir -p /app
fi

# 서비스가 존재하면 중지
if systemctl list-unit-files | grep -q myapp.service; then
  systemctl stop myapp || true
fi 