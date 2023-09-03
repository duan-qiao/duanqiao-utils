package cn.edu.seu.sky.beans;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coordinate {
    /**
     * 经度 -180°到180°
     */
    private double lng;
    /**
     * 纬度 -90°到90°
     */
    private double lat;

    public String toLatLng() {
        return lat + "," + lng;
    }

    public String toLngLat() {
        return lng + "," + lat;
    }

    public static Coordinate fromLngLat(String location) {
        if (StringUtils.isBlank(location)) {
            return null;
        }
        String[] split = location.split(",");
        Preconditions.checkArgument(split.length == 2);
        return new Coordinate(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    }

    public static Coordinate fromLatLng(String location) {
        if (StringUtils.isBlank(location)) {
            return null;
        }
        String[] split = location.split(",");
        Preconditions.checkArgument(split.length == 2);
        return new Coordinate(Double.parseDouble(split[1]), Double.parseDouble(split[0]));
    }
}
