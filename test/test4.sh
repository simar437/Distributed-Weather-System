echo "Multiple Clients and multiple Content Servers"
echo "---------------------------------------------------------------------------"


make client ARGS="localhost:4567"
echo "---------------------------------------------------------------------------"
make content_server ARGS="CS1 localhost:4567 weather/file1.txt" &
make client ARGS="localhost:4567"
echo "---------------------------------------------------------------------------"

make content_server ARGS="CS2 localhost:4567 weather/file2.txt" &
make client ARGS="localhost:4567"
echo "---------------------------------------------------------------------------"
make content_server ARGS="CS3 localhost:4567 weather/file3.txt" &
make client ARGS="localhost:4567"

echo "Test 4: (Manual Verify) Multiple Clients and Content Servers" >> test_results.txt