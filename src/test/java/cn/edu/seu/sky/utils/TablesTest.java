package cn.edu.seu.sky.utils;

import cn.edu.seu.sky.BaseTest;
import cn.edu.seu.sky.model.Metrics;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiaotian on 2023/6/11
 */
public class TablesTest extends BaseTest {

    @Test
    public void test1() {
        List<Metrics> metrics = buildData();
        Map<Integer, Map<String, BigDecimal>> map = metrics.stream().collect(
                Collectors.groupingBy(Metrics::getDate,
                        Collectors.groupingBy(Metrics::getMetric,
                                MyCollectors.summingBigDecimal(Metrics::getValue))));
        Tables<Integer, String, BigDecimal> table = Tables.create(map);

        Assertions.assertEquals(new BigDecimal(2), table.get(20230601, "A"));
        Assertions.assertEquals(BigDecimal.TEN, table.getOrDefault(20230610, "A", BigDecimal.TEN));
        Assertions.assertTrue(table.contains(20230603, "A"));
        Assertions.assertTrue(table.containsRow(20230602));
        Assertions.assertTrue(table.containsColumn("C"));
    }

    @Test
    public void test2() {
        List<Metrics> metrics = buildData();
        Tables<String, Integer, BigDecimal> table = metrics.stream().collect(
                MyCollectors.toTable(
                        Metrics::getMetric,
                        Metrics::getDate,
                        Metrics::getValue,
                        (e1, e2) -> e2, Tables::create)
        );
        System.out.println(table);
    }

    private List<Metrics> buildData() {
        Metrics metrics1 = new Metrics("A", 20230601, BigDecimal.ONE);
        Metrics metrics2 = new Metrics("A", 20230601, BigDecimal.ONE);
        Metrics metrics3 = new Metrics("B", 20230601, BigDecimal.ONE);
        Metrics metrics4 = new Metrics("B", 20230601, BigDecimal.TEN);
        Metrics metrics5 = new Metrics("C", 20230602, BigDecimal.ONE);
        Metrics metrics6 = new Metrics("A", 20230603, BigDecimal.ZERO);
        return Lists.newArrayList(metrics1, metrics2, metrics3, metrics4, metrics5, metrics6);
    }
}
