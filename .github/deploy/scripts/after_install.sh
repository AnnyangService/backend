#!/bin/bash

cat > /etc/systemd/system/api-server.service << 'EOF'
[Unit]
Description=Spring Boot Application
After=network.target

[Service]
Type=simple
User=ec2-user
Environment="SPRING_PROFILES_ACTIVE=prod"
WorkingDirectory=/app
ExecStart=/usr/bin/java -jar hi-meow-api-server.jar
Restart=always

[Install]
WantedBy=multi-user.target
EOF

# 서비스 등록 및 자동 시작 설정
systemctl daemon-reload
systemctl enable api-server