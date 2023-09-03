package cn.edu.seu.sky.utils;

import cn.edu.seu.sky.beans.Coordinate;

import static java.lang.Math.toRadians;

public class MileageUtil {

    /**
     * 赤道半径，也通常作为地球半径使用
     */
    private static final double EQUATORIAL_RADIUS = 6378137;

    /**
     * 计算两经纬度点之间的平面直线距离(单位米)--美团算法
     *
     * @param lng1 起点经度
     * @param lat1 起点纬度
     * @param lng2 重点经度
     * @param lat2 终点纬度
     * @return 平面距离
     */
    public static double planeDistance(double lng1, double lat1, double lng2, double lat2) {
        // 经度差值
        double lngDiff = lng1 - lng2;
        // 纬度差值
        double latDiff = lat1 - lat2;
        // 平均纬度
        double averageLat = (lat1 + lat2) / 2.0;
        // 东西距离
        double eastWestDistance = toRadians(lngDiff) * EQUATORIAL_RADIUS * Math.cos(toRadians(averageLat));
        // 南北距离
        double northSouthDistance = EQUATORIAL_RADIUS * toRadians(latDiff);
        // 用平面的矩形对角距离公式计算总距离
        return Math.sqrt(Math.pow(eastWestDistance, 2) + Math.pow(northSouthDistance, 2));
    }

    public static double planeDistance(Coordinate point1, Coordinate point2) {
        return planeDistance(point1.getLng(), point1.getLat(), point2.getLng(), point2.getLat());
    }

    /**
     * 根据经纬度计算曲面直线距离(单位米)--Haversine公式
     *
     * @param lng1 起点经度
     * @param lat1 起点纬度
     * @param lng2 重点经度
     * @param lat2 终点纬度
     * @return 曲面距离
     */
    public static double curveDistance(double lng1, double lat1, double lng2, double lat2) {
        // 角度转弧度
        double radLng1 = toRadians(lng1);
        double radLat1 = toRadians(lat1);
        double radLng2 = toRadians(lng2);
        double radLat2 = toRadians(lat2);

        // 经度差值
        double lngDiff = radLng1 - radLng2;
        // 纬度差值
        double latDiff = radLat1 - radLat2;
        return 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(latDiff / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(lngDiff / 2), 2))) * EQUATORIAL_RADIUS;
    }

    public static double curveDistance(Coordinate point1, Coordinate point2) {
        return curveDistance(point1.getLng(), point1.getLat(), point2.getLng(), point2.getLat());
    }

    public static double getDistancePointToLine(Coordinate point, Coordinate point1, Coordinate point2) {
        // 线段起终点距离
        double a = planeDistance(point1, point2);
        // 点到线段起点距离
        double b = planeDistance(point, point1);
        // 点到线段终点距离
        double c = planeDistance(point, point2);

        // 钝角三角形，离起点距离长，这时候最短距离为到终点距离
        if (b * b >= c * c + a * a) {
            return c;
        }
        // 钝角三角形，离终点距离长，这时候最短距离为到起点距离
        if (c * c >= b * b + a * a) {
            return b;
        }
        // 锐角三角形，这时候最短距离为垂直到线段的距离，利用海伦公式计算
        double l = (a + b + c) / 2;
        return 2 * Math.sqrt(l * (l - a) * (l - b) * (l - c)) / a;
    }

//    public static Coordinate getFootPointToLine(Coordinate point, Coordinate point1, Coordinate point2) {
//        double A = pnt2.y - pnt1.y;     //y2-y1
//        double B = pnt1.x - pnt2.x;     //x1-x2;
//        double C = pnt2.x * pnt1.y - pnt1.x * pnt2.y;     //x2*y1-x1*y2
//        if (A * A + B * B < 1e-13) {
//            return pnt1;   //pnt1与pnt2重叠
//        } else if (Math.abs(A * point.x + B * point.y + C) < 1e-13) {
//            return point;   //point在直线上(pnt1_pnt2)
//        } else {
//            double x = (B * B * point.x - A * B * point.y - A * C) / (A * A + B * B);
//            double y = (-A * B * point.x + A * A * point.y - B * C) / (A * A + B * B);
//            Coordinate fpoint = new Coordinate();
//            fpoint.x = x;
//            fpoint.y = y;
//            return fpoint;
//        }
//    }

    public static void main(String[] args) {
        Coordinate point1 = new Coordinate(118.68562876964337, 31.903402794422327);
        Coordinate point2 = new Coordinate(118.802987, 31.869157);
        System.out.println(planeDistance(point1, point2));
        System.out.println(curveDistance(point1, point2));
    }
}
