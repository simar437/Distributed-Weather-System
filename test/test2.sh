echo "Test for multiple clients"
echo "---------------------------------------------------------------------------"

make content_server ARGS="CS1 localhost:4567 weather/file1.txt" &

c1=$(make client ARGS="localhost:4567 IDS60901") &
c2=$(make client ARGS="localhost:4567 IDS60901") &
c3=$(make client ARGS="localhost:4567 IDS60901") &
c4=$(make client ARGS="localhost:4567 IDS60901") &


if [ "$c1" = "$c2" ] && [ "$c2" = "$c3" ] && [ "$c3" = "$c4" ]; then
    echo "Test 2 Passed" >> test_results.txt
else
    echo "Test 2 Failed" >> test_results.txt
fi
echo "---------------------------------------------------------------------------"
