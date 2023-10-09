echo "Test to check if the AS removes old data after restarting"
echo "Starting AS..."
make aggregation_server &
sleep 2
pid=$(pgrep -f "out AggregationServer")
make content_server ARGS="CS1 localhost:4567 weather/file1.txt"
sleep 2
echo "Closing AS..."
kill "$pid"
sleep 2
echo "Re-Starting AS..."
make aggregation_server &
sleep 2
pid=$(pgrep -f "out AggregationServer")
c1=$(make client ARGS="localhost:4567")
echo "30 seconds sleep between client 1 and 2"
sleep 30
c2=$(make client ARGS="localhost:4567")
echo "PID2: $pid"
kill "$pid"
sleep 3
echo "c1: $c1"
echo "c2: $c2"
if [ "$c1" = "$c2" ]; then
    echo "Test 10 failed"
else
    echo "Test 10 passed"
fi