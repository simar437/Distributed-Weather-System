echo "Test for infinite GETs (Load testing) for 100 seconds"
make content_server ARGS="CS1 localhost:4567 weather/file1.txt"
echo "Starting Infinite GET"
bash test/infiniteGET.sh &
sleep 2
pid=$(pgrep -f "bash test/infiniteGET.sh")
sleep 100
echo "Closing Infinite GET"
kill "$pid"
sleep 2
echo "Test 8: (Manual Verify) Load testing" >> test_results.txt