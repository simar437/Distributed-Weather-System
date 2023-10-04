# Distributed Weather System
- Implemented a distributed weather system using Java Sockets.
- The system consists of an Aggregation Server, Content Server and a Client.
- The Aggregation Server is responsible for aggregating the data from the Content Servers and sending it to the Client.
- The Content Server is responsible for sending the data to the Aggregation Server.
- The Client gets the data from the Aggregation Server.
- The Aggregation Server and the Content Server are synchronized using the Lamport Clock.

# API Specification
### Aggregation Server
- ```GET /``` or ```GET /weather``` or ```GET /weather/``` - Returns the weather data for all the stations
  - For e.g. ```hostname:port/weather``` or ```localhost:4567```
- ```GET /weather/<station_id>``` - Returns the weather data for the given station ID
  - For e.g. ```hostname:port/weather/IDS60901``` or ```localhost:4567/weather/IDS60901```
- ```GET /SYNC``` - Returns an updated Lamport Clock
  - For e.g. ```hostname:port/SYNC``` or ```localhost:4567/SYNC```
- ```GET /<anything-else>``` - Returns 400 Bad Request
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
- The First argument should be the directory for the backup files of the Content Server
- In this case, the directory is ```CS1```
```
make content_server ARGS="CS1 localhost:1234 file1.txt file2.txt ..."
```
- Supports all three URL formats specified in the Assignment description
- Deletes the backup files after successfully sending all the files to the Aggregation Server
### Run Client
```
make client ARGS="localhost:1234 ID12345"
```
- Supports all three URL formats specified in the Assignment description
- ID is optional

### Run test
Make sure that the Aggregation Server is running on Port 4567
```
make test
```
Change the shell as per availability, I am using ```bash``` 

# Lamport Clock and it's tie breaking mechanism
- Synced Content Server(s) with the Aggregation Server using GET request.
- Maintained ```HashMap<String, PriorityQueue<Weather>>```, representing Station ID and the corresponding weather object.
- Overridden the ```compateTo``` method in ```Weather``` to maintain order in the ```PriorityQueue``` using the lamport time-stamp sent by the content server.
- If two weather objects have the same lamport time-stamp, then the one with the higher PID is chosen as the latest data.
# Test Cases
### Test Case 1
Test to check if newer CS is updating the data in the Aggregation Server
- Run the Content Server with the file ```weather/file1.txt```
- Run Client with ID ```IDS60901```
- Run the Content Server with the file ```weather/file3.txt```
- Run the Client with ID ```IDS60901```
- Compare the output of the two clients
- The output of the second client should be different from the first client
### Test Case 2
Test to run multiple Clients
- Run the Content Server with the file ```weather/file1.txt```
- Run multiple Clients with the same IDs in the background
- All the clients should get the same output
### Test Case 3
Test to check the sleep mechanism of the Aggregation Server
- Run the Content Server with the file ```weather/file1.txt```
- Run the Client
- Sleep for 30 seconds
- Run a new Client
- Client 2 should get no output
### Test Case 5
Sleep Mechanism of the Content Server
- Run the Content Server with the file ```weather/file1.txt weather/file2.txt weather/file3.txt```
- Run 

# What is working (Everything)
- GET requests
- PUT requests
- Interoperability
- Lamport Clock and tie breaking mechanism
- Aggregation Server Backup and restore
- Content Server Backup and restore
- All the HTTP status codes
- Data expulsion after 30 seconds in the Aggregation Server
- Sending newer data to the Aggregation Server from the Content Server after 28 seconds
- Removing data older than 20 updates from the Aggregation Server Backup
- Expulsion of data from the Aggregation Server after restoring from the backup after 30 seconds of inactivity from the time of receiving the last update from the Content Server
  - For e.g., If the Aggregation Server receives an update from the Content Server at 0, and it crashes at 10, still the data will be removed from the Aggregation Server at 30

Note: I did not attempt the bonus question
# TODO
- Test cases
