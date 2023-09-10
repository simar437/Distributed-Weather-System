public class Weather {
    String id, name, state, time_zone,local_date_time,
    local_date_time_full, cloud, wind_dir;
    double lat, lon, air_temp,apparent_t, dewpt, press, rel_hum, wind_spd_kmh, wind_spd_kt;

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
}
