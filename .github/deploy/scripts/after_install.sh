#!/bin/bash
cd /app
latest_jar=$(ls -t hi-meow-*.jar | head -1)
ln -sf $latest_jar myapp.jar
chmod +x /app/myapp.jar

# systemd 서비스 파일이 없으면 생성
if [ ! -f /etc/systemd/system/myapp.service ]; then
  cat > /etc/systemd/system/myapp.service << 'EOF'
[Unit]
Description=Spring Boot Application
After=network.target

[Service]
Type=simple
User=ec2-user
Environment="SPRING_PROFILES_ACTIVE=prod"
WorkingDirectory=/app
ExecStart=/usr/bin/java -jar /app/myapp.jar
Restart=always

[Install]
WantedBy=multi-user.target
EOF

  # 서비스 등록 및 자동 시작 설정
  systemctl daemon-reload
  systemctl enable myapp
fi 