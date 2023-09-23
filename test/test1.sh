echo "Test to check if newer CS is updating the data"
echo "---------------------------------------------------------------------------"
make content_server ARGS="localhost:4567 weather/file1.txt" &
sleep 2
c1=$(make client ARGS="localhost:4567 IDS60901")
make content_server ARGS="localhost:4567 weather/file3.txt" &
sleep 2
c2=$(make client ARGS="localhost:4567 IDS60901")
if [ "$c1" != "$c2" ]; then
    echo "Correct"
else
    echo "Incorrect"
fi
echo "---------------------------------------------------------------------------"
