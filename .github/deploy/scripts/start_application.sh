#!/bin/bash
# systemd 서비스가 등록되어 있는지 확인하고 시작
if systemctl list-unit-files | grep -q myapp.service; then
  systemctl start myapp
else
  echo "myapp 서비스가 등록되어 있지 않습니다. after_install.sh 스크립트에서 서비스 등록이 제대로 되었는지 확인하세요."
  exit 1
fi 