# Distributed Weather System
- Implemented a distributed weather system using Java Sockets.
- The system consists of an Aggregation Server, Content Server and a Client.
- The Aggregation Server is responsible for aggregating the data from the Content Servers and sending it to the Client.
- The Content Server is responsible for sending the data to the Aggregation Server.
- The Client gets the data from the Aggregation Server.
- The Aggregation Server and the Content Server are synchronized using the Lamport Clock.

# Aggregation Server Architecture
- The Aggregation Server is a multithreaded server.
- When a client (GET Request) connects to the Aggregation Server, a new thread is created to handle the client.
- When a Content Server (PUT Request) connects to the Aggregation Server, it is handled directly by the main thread 
and for data removal, a new thread is created.
- The Aggregation Server is backed up after every PUT request from the Content Server and restored after data expulsion.
- The Aggregation Server is backed up in a file named ```backup{NUMBER}.txt``` in the directory ```AS backup```.
- The Aggregation Server is restored from the backup file with the highest number from the directory ```AS backup```.
- The old backup files are deleted after every 20 iterations.
  - i.e., If the Aggregation Server is backed up 21 times, then the first backup file is deleted.
- The Aggregation Server stores the data in a ```HashMap<String, PriorityQueue<Weather>>```, where the key is the Station ID and the value is a ```PriorityQueue``` of ```Weather``` objects.

# API Specification
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
- The above will create a directory named ```CS1 backup``` and store the backup files in it
- Supports all three URL formats specified in the Assignment description 

Note: The Content Server deletes the backup files after successful execution to avoid namespace clutter, you can comment out the line ```b.destroyBackup();``` at line 165 in the ```ContentServer.java``` if you want to keep the backup files. 
### Run Client
```
make client ARGS="localhost:1234 ID12345"
```
- Supports all three URL formats specified in the Assignment description
- ID is optional

### Run test
Make sure that the Aggregation Server is running on Port 4567 for ```test1```, do not run the Aggregation Server for ```test2```
```
make test1
```
and
```
make test2
```
```test1``` will run test 1 - 9, this will also wait for 30 seconds before running the next test case such that the previous data is removed from the Aggregation Server
```test2``` will only run test 10
Change the shell as per availability, I am using ```bash``` 

# Lamport Clock and its tie breaking mechanism
- Synced Content Server(s) with the Aggregation Server using GET request.
- Maintained ```HashMap<String, PriorityQueue<Weather>>```, representing Station ID and the corresponding weather object.
- Overridden the ```compateTo``` method in ```Weather``` to maintain order in the ```PriorityQueue``` using the lamport time-stamp sent by the content server.
- If two weather objects have the same lamport time-stamp, then the one with the higher PID is chosen as the latest data.
# Test Cases
See ```test_results.txt``` in the root of the project for the test results (test 1 - 9)
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
- Run multiple Clients after 30-second intervals each
- Each time a new client is run, the output should be different
### Test Case 6
- Test to check interoperability of the API using curl
- Run the Content Server with the file ```weather/file1.txt```
- Run the Client using curl
### Test Case 7
- Test to check if newer CS is updating the data and if the AS removes old data after 30 seconds
- Run the Content Server with the files ```weather/file1.txt weather/file2.txt```
- Run the Client with ID ```IDS60901``` (Output from the first file)
- Run the Content Server with the file ```weather/file3.txt``` (this file also has the same Station ID as file1)
- Run the Client with ID ```IDS60901``` (Output from the second file)
- Sleep for 30 seconds
- Run the Client with ID ```IDS60901``` (Output from the first file)
- C2 should get different output from C1 and C3
### Test Case 8
- Test for infinite GETs (Load testing) for 100 seconds
- Run the Content Server with the file ```weather/file1.txt```
- Run multiple Clients in the background for 100 seconds
### Test Case 9
- Send POST using curl and check for Lamport clock tie breaking using custom request headers
- Send a POST request using curl with ```time: 0```
- Send another POST request using curl with ```time: 0```
- The second POST request should be used as the latest data
### Test Case 10
- Test to check if the Aggregation Server is backing up and restoring the data
- Run the Aggregation Server in the background
- Run the Content Server with the file ```weather/file1.txt```
- Kill the Aggregation Server
- Run the Aggregation Server
- Run the Client
- Sleep for 30 seconds
- Run the Client
- The output of the second client should be different from the first client

# What is working (Everything)
- GET requests
- PUT requests
- Interoperability for GET requests
- Lamport Clock and tie breaking mechanism
- Aggregation Server Backup and restore
- Content Server Backup and restore
- All the HTTP status codes
- Data expulsion after 30 seconds in the Aggregation Server
- Sending newer data to the Aggregation Server from the Content Server after 28 seconds
- Removing data older than 20 updates from the Aggregation Server Backup
- Expulsion of data from the Aggregation Server after restoring from the backup after 30 seconds of inactivity from the time of receiving the last update from the Content Server
  - For e.g., If the Aggregation Server receives an update from the Content Server at 0, and it crashes at 10, still the data will be removed from the Aggregation Server at 30
- Files with the same Station ID
- Files with the different Station ID
- Multiple Content Servers
- Multiple Clients
- Syncing the Content Server with the Aggregation Server

Note: I did not attempt the bonus question