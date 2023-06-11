package cn.edu.seu.sky.utils;

/**
 * @author xiaotian on 2023/4/1
 */
public final class GrayUtil {

    public static final int TEN_THOUSAND = 10000;

    private GrayUtil() {
    }

    public static boolean isGrayRequest(int numerator, int denominator) {
        if (numerator <= 0) {
            return false;
        }
        if (numerator >= denominator) {
            return true;
        }
        return (System.nanoTime() % (long) denominator) < numerator;
    }

    public static boolean isGrayRequestById(Long id, int grayPercent, int denominator) {
        if (id == null) {
            return isGrayRequest(grayPercent, denominator);
        }
        if (grayPercent <= 0) {
            return false;
        }
        if (grayPercent >= denominator) {
            return true;
        }
        return (id % (long) denominator) < grayPercent;
    }

    public static boolean isGrayRequestByCoordinate(double lon, double lat, int grayPercent, int denominator) {
        if (grayPercent <= 0) {
            return false;
        }
        if (grayPercent >= denominator) {
            return true;
        }
        int hashCode = String.valueOf(lon + lat).hashCode();
        return (hashCode % denominator) < grayPercent;
    }
}
