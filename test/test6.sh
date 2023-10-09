echo "Test to check interoperability of the API using curl"
make content_server ARGS="CS1 localhost:4567 weather/file1.txt"
curl localhost:4567
echo ""
echo "Test 6: (Manual Verify) Interoperability of the API using curl" >> test_results.txt