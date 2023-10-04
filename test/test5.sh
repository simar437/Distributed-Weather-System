echo "GET all data and checking for data received after 30 seconds"
echo "Every GET should receive more data than the previous GET"
echo "---------------------------------------------------------------------------"

make content_server ARGS="CS1 localhost:4567 weather/file1.txt weather/file2.txt weather/file3.txt" &
make client ARGS="localhost:4567"
sleep 30
echo "---------------------------------------------------------------------------"
make client ARGS="localhost:4567"
sleep 30
echo "---------------------------------------------------------------------------"
make client ARGS="localhost:4567"
sleep 30
echo "---------------------------------------------------------------------------"
