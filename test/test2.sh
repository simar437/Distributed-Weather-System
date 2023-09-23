echo "Test for multiple clients"
echo "---------------------------------------------------------------------------"

make content_server ARGS="localhost:4567 weather/file1.txt" &

c1=$(make client ARGS="localhost:4567 IDS60901") &
c2=$(make client ARGS="localhost:4567 IDS60901") &
c3=$(make client ARGS="localhost:4567 IDS60901") &
c4=$(make client ARGS="localhost:4567 IDS60901") &


if [ "$c1" = "$c2" ] && [ "$c2" = "$c3" ] && [ "$c3" = "$c4" ]; then
    echo "Correct"
else
    echo "Incorrect"
fi
echo "---------------------------------------------------------------------------"
