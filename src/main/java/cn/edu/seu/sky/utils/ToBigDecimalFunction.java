package cn.edu.seu.sky.utils;

import java.math.BigDecimal;

/**
 * @author xiaotian
 */
@FunctionalInterface
public interface ToBigDecimalFunction<T> {
    /**
     * Applies this function to the given argument.
     *
     * @param value the function argument
     * @return the function result
     */
    BigDecimal applyAsBigDecimal(T value);
}
