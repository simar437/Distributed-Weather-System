echo "Test - 3: Checking sleep mechanism"
echo "---------------------------------------------------------------------------"

make content_server ARGS="CS1 localhost:4567 weather/file1.txt" &
c1=$(make client ARGS="localhost:4567 IDS60901") &
sleep 30
c2=$(make client ARGS="localhost:4567 IDS60901") &

sleep 2

if [ "$c1" = "$c2" ]; then
    echo "Test - 3: Incorrect Output"
else
    echo "Test - 3: Correct Output (Passed)"
fi
echo "---------------------------------------------------------------------------"