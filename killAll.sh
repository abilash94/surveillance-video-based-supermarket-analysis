sudo lsof -t -i tcp:8000 | xargs kill -9
sudo lsof -t -i tcp:10002 | xargs kill -9
