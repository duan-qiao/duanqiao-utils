package cn.edu.seu.sky.utils;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * @author xiaotian
 */
public class Tables<R, C, V> {

    final HashBasedTable<R, C, V> table;

    public static <R, C, V> Tables<R, C, V> create() {
        return new Tables<>();
    }

    public static <R, C, V> Tables<R, C, V> create(Map<R, Map<C, V>> map) {
        Tables<R, C, V> table = create();
        map.forEach((r, m) -> m.forEach((c, v) -> table.put(r, c, v)));
        return table;
    }

    public void compute(Map<R, Map<C, V>> map, BiFunction<V, V, V> merge) {
        map.forEach((r, m) ->
                m.forEach((c, v) ->
                        {
                            V oldValue = table.get(r, c);
                            this.table.put(r, c, merge.apply(oldValue, v));
                        }
                ));
    }

    Tables() {
        this.table = HashBasedTable.create();
    }

    public V put(R rowKey, C columnKey, V value) {
        return table.put(rowKey, columnKey, value);
    }

    public void putAll(R rowKey, Map<C, V> map) {
        map.forEach((key, value) -> table.put(rowKey, key, value));
    }

    public Set<Table.Cell<R, C, V>> cellSet() {
        return table.cellSet();
    }

    public Map<C, V> row(R rowKey) {
        return table.row(rowKey);
    }

    public Map<R, V> column(C columnKey) {
        return table.column(columnKey);
    }

    public Map<R, Map<C, V>> rowMap() {
        return table.rowMap();
    }

    public V remove(R rowKey, C columnKey) {
        return table.remove(rowKey, columnKey);
    }

    public boolean containsRow(R rowKey) {
        return table.containsRow(rowKey);
    }

    public boolean containsColumn(C columnKey) {
        return table.containsColumn(columnKey);
    }

    public V get(R rowKey, C columnKey) {
        return table.get(rowKey, columnKey);
    }

    public V getOrDefault(R rowKey, C columnKey, V defaultValue) {
        V v;
        return ((v = get(rowKey, columnKey)) != null || contains(rowKey, columnKey))
                ? v
                : defaultValue;
    }

    public Map<C, V> rowOrDefault(R rowKey, Map<C, V> defaultValue) {
        Map<C, V> map;
        return ((map = row(rowKey)) != null)
                ? map
                : defaultValue;
    }

    public boolean contains(R rowKey, C columnKey) {
        return table.contains(rowKey, columnKey);
    }

    void merge(R row, C column, V value,
               BiFunction<? super V, ? super V, ? extends V> mergeFunction) {
        Objects.requireNonNull(mergeFunction);
        Objects.requireNonNull(value);

        V oldValue = get(row, column);
        if (oldValue == null) {
            put(row, column, value);
        } else {
            V newValue = mergeFunction.apply(oldValue, value);
            if (newValue == null) {
                remove(row, column);
            } else {
                put(row, column, newValue);
            }
        }
    }
}
