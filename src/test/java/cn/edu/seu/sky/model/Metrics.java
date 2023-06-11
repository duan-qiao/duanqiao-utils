package cn.edu.seu.sky.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author xiaotian on 2023/6/11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metrics {

    private String metric;

    private Integer date;

    private BigDecimal value;
}
