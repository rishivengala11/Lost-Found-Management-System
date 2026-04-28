@echo off
curl -c cookies.txt -X POST -d "action=login&username=rishi11&password=rishhi0011" http://localhost:8080/Lost_Found/api/auth -v
echo ---
curl -b cookies.txt http://localhost:8080/Lost_Found/api/auth?action=session -v
