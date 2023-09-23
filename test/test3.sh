echo "Checking sleep mechanism"
echo "---------------------------------------------------------------------------"

make content_server ARGS="localhost:4567 weather/file1.txt" &
sleep 30
make client ARGS="localhost:4567 IDS60901"
echo "---------------------------------------------------------------------------"
echo "If status 404 is received then it's working fine."
echo "---------------------------------------------------------------------------"
