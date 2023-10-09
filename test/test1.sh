echo "Test to check if newer CS is updating the data"
echo "---------------------------------------------------------------------------"
make content_server ARGS="CS1 localhost:4567 weather/file1.txt" &
c1=$(make client ARGS="localhost:4567 IDS60901")
make content_server ARGS="CS2 localhost:4567 weather/file3.txt" &
c2=$(make client ARGS="localhost:4567 IDS60901")
if [ "$c1" != "$c2" ]; then
    echo "Test 1 Passed" >> test_results.txt
else
    echo "Test 1 Failed" >> test_results.txt
fi
echo "---------------------------------------------------------------------------"

