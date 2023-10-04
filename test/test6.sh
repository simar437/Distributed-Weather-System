echo "Test to check if the AS removes old data after restarting"
make aggregation_server &
pid=$!
echo "PID1: $pid"
make content_server ARGS="CS1 localhost:4567 weather/file1.txt"
kill -SIGINT $pid
wait $pid
make aggregation_server &
pid=$!
echo "PID2: $pid"
c1=$(make client ARGS="localhost:4567")
sleep 30
c2=$(make client ARGS="localhost:4567")
kill -SIGINT $pid
if [ "$c1" = "$c2" ]; then
    echo "Test 6 failed"
else
    echo "Test 6 passed"
fi