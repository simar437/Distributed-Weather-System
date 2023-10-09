echo "Send POST using curl and check for Lamport clock tie breaking"
text1='[{
          "id": "IDS60901",
          "name": "First",
          "state": "SA",
          "time_zone": "CST",
          "lat": -34.9,
          "lon": 138.6,
          "local_date_time": "15/04:00pm",
          "local_date_time_full": "20230715160000",
          "air_temp": 13.3,
          "apparent_t": 9.5,
          "cloud": "Partly cloudy",
          "dewpt": 5.7,
          "press": 1023.9,
          "rel_hum": 60,
          "wind_dir": "S",
          "wind_spd_kmh": 15,
          "wind_spd_kt": 8,
          "time": 1,
          "contentServerID": 1
        }
]'

request1="Lamport-Clock: 0
CS-ID: 1
Content-Type: application/json
Content-Length: ${#text1}

${text1}"

text2='[{
         "id": "IDS60901",
         "name": "Second",
         "state": "SA",
         "time_zone": "CST",
         "lat": -34.9,
         "lon": 138.6,
         "local_date_time": "15/04:00pm",
         "local_date_time_full": 20230715160000,
         "air_temp": 13.3,
         "apparent_t": 9.5,
         "cloud": "Partly cloudy",
         "dewpt": 5.7,
         "press": 1023.9,
         "rel_hum": 60,
         "wind_dir": "S",
         "wind_spd_kmh": 15,
         "wind_spd_kt": 8,
         "time": 1,
         "contentServerID": 2
       }
]'

request2="Lamport-Clock: 0
CS-ID: 2
Content-Type: application/json
Content-Length: ${#text2}

${text2}"

curl -X PUT  localhost:4567/weather.json -H "$request1" &
sleep 2
curl -X PUT localhost:4567/weather.json -H "$request2" &
sleep 2
c1=$(make client ARGS="localhost:4567 IDS60901")
# check c1 contains the word "name: Second"
if [[ $c1 == *"name: Second"* ]]; then
    echo "Test 9 Passed" >> test_results.txt
else
    echo "Test 9 Failed" >> test_results.txt
fi