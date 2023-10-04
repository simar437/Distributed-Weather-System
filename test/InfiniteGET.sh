echo "Infinite loop of GETs"
while true; do
    make client ARGS="localhost:4567"
    sleep 1
done