echo "Test to check if newer CS is updating the data and if the AS removes old data after 30 seconds"
make content_server ARGS="CS1 localhost:4567 weather/file1.txt weather/file2.txt" &
sleep 2
c1=$(make client ARGS="localhost:4567 IDS60901")
make content_server ARGS="CS2 localhost:4567 weather/file3.txt" &
sleep 2
c2=$(make client ARGS="localhost:4567 IDS60901")
sleep 30
c3=$(make client ARGS="localhost:4567 IDS60901")
if [ "$c1" != "$c2" ] && [ "$c1" = "$c3" ]; then
    echo "Test 7 Passed" >> test_results.txt
else
    echo "Test 7 Failed" >> test_results.txt
fi