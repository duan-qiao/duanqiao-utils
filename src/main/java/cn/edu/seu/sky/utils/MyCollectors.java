package cn.edu.seu.sky.utils;

import com.google.common.collect.Table;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author xiaotian
 */
public class MyCollectors {

    static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();
    static final Set<Collector.Characteristics> CH_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));

    private MyCollectors() {
    }

    @SuppressWarnings("unchecked")
    private static <I, R> Function<I, R> castingIdentity() {
        return i -> (R) i;
    }

    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Function<A, R> finisher,
                      Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }
    }

    @SuppressWarnings("all")
    public static <T> Collector<T, ?, BigDecimal>
    summingBigDecimal(ToBigDecimalFunction<? super T> mapper) {
        return new CollectorImpl<>(
                () -> new BigDecimal[1],
                (a, t) -> {
                    if (a[0] == null) {
                        a[0] = BigDecimal.ZERO;
                    }
                    a[0] = a[0].add(mapper.applyAsBigDecimal(t));
                },
                (a, b) -> {
                    a[0] = a[0].add(b[0]);
                    return a;
                },
                a -> a[0], CH_NOID);
    }

    @SuppressWarnings("all")
    public static <T, R, C, V>
    Collector<T, ?, Tables<R, C, V>> toTable(Function<? super T, ? extends R> rowMapper,
                                             Function<? super T, ? extends C> columnMapper,
                                             Function<? super T, ? extends V> valueMapper) {
        return toTable(rowMapper, columnMapper, valueMapper, throwingMerger(), Tables::new);
    }

    @SuppressWarnings("all")
    public static <T, R, C, V, M extends Tables<R, C, V>>
    Collector<T, ?, M> toTable(Function<? super T, ? extends R> rowMapper,
                               Function<? super T, ? extends C> columnMapper,
                               Function<? super T, ? extends V> valueMapper,
                               BinaryOperator<V> mergeFunction,
                               Supplier<M> tableSupplier) {
        BiConsumer<M, T> accumulator =
                (table, element) -> table.merge(
                        rowMapper.apply(element),
                        columnMapper.apply(element),
                        valueMapper.apply(element),
                        mergeFunction);
        return new CollectorImpl<>(tableSupplier, accumulator, tableMerger(mergeFunction), CH_ID);
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (v1, v2) -> {
            throw new IllegalStateException("Conflicting values " + v1 + " and " + v2);
        };
    }

    private static <R, C, V, M extends Tables<R, C, V>>
    BinaryOperator<M> tableMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Table.Cell<R, C, V> cell : m2.cellSet()) {
                m1.merge(cell.getRowKey(), cell.getColumnKey(), cell.getValue(), mergeFunction);
            }
            return m1;
        };
    }
}
