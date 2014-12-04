call cp -R conf target\conf
call cp -R docs target\docs
call copy start-balance.bat .\target\start-balance.bat
call copy start-balance.sh .\target\start-balance.sh
call mvn assembly:assembly



pause