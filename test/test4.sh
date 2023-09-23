echo "Multiple Clients and multiple Content Servers"
echo "---------------------------------------------------------------------------"


make client ARGS="localhost:4567"
echo "---------------------------------------------------------------------------"
make content_server ARGS="localhost:4567 weather/file1.txt" &
make client ARGS="localhost:4567"
echo "---------------------------------------------------------------------------"

make content_server ARGS="localhost:4567 weather/file2.txt" &
make client ARGS="localhost:4567"
echo "---------------------------------------------------------------------------"
make content_server ARGS="localhost:4567 weather/file3.txt" &
make client ARGS="localhost:4567"
