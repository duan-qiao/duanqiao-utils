package cn.edu.seu.sky.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author xiaotian on 2022/5/10
 */
public class BeanUtils {

    public static <S, T> T copy(S source, Supplier<T> target) {
        T t = target.get();
        org.springframework.beans.BeanUtils.copyProperties(source, t);
        return t;
    }

    public static <S, T> List<T> copyList(List<S> sources, Supplier<T> targets) {
        return copyList(sources, targets, null);
    }

    public static <S, T> List<T> copyList(List<S> sources, Supplier<T> targets, BiConsumer<S, T> callBack) {
        List<T> list = new ArrayList<>(sources.size());
        for (S source : sources) {
            T target = targets.get();
            org.springframework.beans.BeanUtils.copyProperties(source, target);
            if (callBack != null) {
                callBack.accept(source, target);
            }
            list.add(target);
        }
        return list;
    }

}
