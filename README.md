# Compile and Run

### To compile
```
make
```
### Run Aggregation Server
```
make aggregation_server ARGS="1234"
```
- ARGS is optional for Aggregation Server (port number)
### Run Content Server
```
make content_server ARGS="localhost:1234 file1.txt file2.txt ..."
```
- Supports all three URL formats specified in the Assignment description
### Run Client
```
make client ARGS="localhost:1234 ID12345"
```
- Supports all three URL formats specified in the Assignment description
- ID is optional

### Run test
Make sure that the Aggregation Server is running
```
make test
```
Change the shell as per availability, I am using ```bash``` 

# Lamport Clock

- Synced Content Server(s) with the Aggregation Server using GET request.
- Maintained ```HashMap<String, PriorityQueue<Weather>>```, representing Station ID and the corresponding weather object.
- Overridden the ```compateTo``` method in ```Weather``` to maintain order in the ```PriorityQueue``` using the lamport time-stamp sent by the content server.

# TODO
- Documentation
- Test cases
- README