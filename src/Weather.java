import com.fasterxml.jackson.annotation.JsonProperty;

public class Weather implements Comparable<Weather> {
    @JsonProperty("id")
    String id;

    @JsonProperty("name")
    String name;

    @JsonProperty("state")
    String state;

    @JsonProperty("time_zone")
    String time_zone;

    @JsonProperty("local_date_time")
    String local_date_time;

    @JsonProperty("local_date_time_full")
    String local_date_time_full;

    @JsonProperty("cloud")
    String cloud;

    @JsonProperty("wind_dir")
    String wind_dir;

    @JsonProperty("contentServerID")
    String contentServerID;

    @JsonProperty("lat")
    double lat;

    @JsonProperty("lon")
    double lon;

    @JsonProperty("air_temp")
    double air_temp;

    @JsonProperty("apparent_t")
    double apparent_t;

    @JsonProperty("dewpt")
    double dewpt;

    @JsonProperty("press")
    double press;

    @JsonProperty("rel_hum")
    double rel_hum;

    @JsonProperty("wind_spd_kmh")
    double wind_spd_kmh;

    @JsonProperty("wind_spd_kt")
    double wind_spd_kt;

    @JsonProperty("time")
    int time;

    public Weather() {}


    /**
     * Copy constructor
     * @param other the other Weather object to copy
     */
    public Weather(Weather other) {
        this.id = other.id;
        this.name = other.name;
        this.state = other.state;
        this.time_zone = other.time_zone;
        this.local_date_time = other.local_date_time;
        this.local_date_time_full = other.local_date_time_full;
        this.cloud = other.cloud;
        this.wind_dir = other.wind_dir;
        this.contentServerID = other.contentServerID;
        this.lat = other.lat;
        this.lon = other.lon;
        this.air_temp = other.air_temp;
        this.apparent_t = other.apparent_t;
        this.dewpt = other.dewpt;
        this.press = other.press;
        this.rel_hum = other.rel_hum;
        this.wind_spd_kmh = other.wind_spd_kmh;
        this.wind_spd_kt = other.wind_spd_kt;
        this.time = other.time;
    }

    /**
     * Compares two Weather objects by their time and PID
     * if the time is equal, the PID is used to break the tie
     * else the time is used to sort the PriorityQueue
     *
     * @param other the other Weather object to compare to
     * @return 0 if the two objects are equal, -1 if this object is less than the other, 1 if this object is greater than the other
     */
    @Override
    public int compareTo(Weather other) {
        if (this.time == other.time) {
            return -Integer.compare(
                    AggregationServer.getPID(this.contentServerID),
                    AggregationServer.getPID(other.contentServerID)
            );
        }
        return -Integer.compare(this.time, other.time);
    }


    /**
     * @return a string representation of the Weather object
     */
    @Override
    public String toString() {
        return "id: " + id + "\n" +
                "name: " + name + "\n" +
                "state: " + state + "\n" +
                "time_zone: " + time_zone + "\n" +
                "local_date_time: " + local_date_time + "\n" +
                "local_date_time_full: " + local_date_time_full + "\n" +
                "cloud: " + cloud + "\n" +
                "wind_dir: " + wind_dir + "\n" +
                "lat: " + lat + "\n" +
                "lon: " + lon + "\n" +
                "air_temp: " + air_temp + "\n" +
                "apparent_t: " + apparent_t + "\n" +
                "dewpt: " + dewpt + "\n" +
                "press: " + press + "\n" +
                "rel_hum: " + rel_hum + "\n" +
                "wind_spd_kmh: " + wind_spd_kmh + "\n" +
                "wind_spd_kt: " + wind_spd_kt + "\n";
    }
}
