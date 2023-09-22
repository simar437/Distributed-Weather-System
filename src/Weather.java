public class Weather implements Comparable <Weather> {
    String id;
    String name;
    String state;
    String time_zone;
    String local_date_time;
    String local_date_time_full;
    String cloud;
    String wind_dir;



    String contentServerID;
    double lat, lon, air_temp,apparent_t, dewpt, press, rel_hum, wind_spd_kmh, wind_spd_kt;

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


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    int time;



    Weather() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTime_zone() {
        return time_zone;
    }

    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }

    public String getLocal_date_time() {
        return local_date_time;
    }

    public void setLocal_date_time(String local_date_time) {
        this.local_date_time = local_date_time;
    }

    public String getLocal_date_time_full() {
        return local_date_time_full;
    }

    public void setLocal_date_time_full(String local_date_time_full) {
        this.local_date_time_full = local_date_time_full;
    }

    public String getCloud() {
        return cloud;
    }

    public void setCloud(String cloud) {
        this.cloud = cloud;
    }

    public String getWind_dir() {
        return wind_dir;
    }

    public void setWind_dir(String wind_dir) {
        this.wind_dir = wind_dir;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getAir_temp() {
        return air_temp;
    }

    public void setAir_temp(double air_temp) {
        this.air_temp = air_temp;
    }

    public double getApparent_t() {
        return apparent_t;
    }

    public void setApparent_t(double apparent_t) {
        this.apparent_t = apparent_t;
    }

    public double getDewpt() {
        return dewpt;
    }

    public void setDewpt(double dewpt) {
        this.dewpt = dewpt;
    }

    public double getPress() {
        return press;
    }

    public void setPress(double press) {
        this.press = press;
    }

    public double getRel_hum() {
        return rel_hum;
    }

    public void setRel_hum(double rel_hum) {
        this.rel_hum = rel_hum;
    }

    public double getWind_spd_kmh() {
        return wind_spd_kmh;
    }

    public void setWind_spd_kmh(double wind_spd_kmh) {
        this.wind_spd_kmh = wind_spd_kmh;
    }

    public double getWind_spd_kt() {
        return wind_spd_kt;
    }

    public void setWind_spd_kt(double wind_spd_kt) {
        this.wind_spd_kt = wind_spd_kt;
    }
    public String getContentServerID() {
        return contentServerID;
    }

    public void setContentServerID(String contentServerID) {
        this.contentServerID = contentServerID;
    }

    @Override
    public int compareTo(Weather other) {
        return -Integer.compare(this.time, other.time);
    }

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
                "wind_spd_kt: " + wind_spd_kt + "\n" +
                "time: " + time + "\n" +
                "CS-ID: " + contentServerID + "\n"
                ;
    }
}
