package cn.edu.seu.sky.utils;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author xiaotian on 2023/6/11
 */
@Slf4j
@Builder
public class FuncGrays<T, R> {

    private String bizType;

    private Function<T, R> origin;

    private Function<T, R> target;

    private BiFunction<R, R, String> compare;

    private Integer percent;

    private Integer comparePercent;

    private Strategy strategy;

    public R apply(T request) {
        if (strategy.hitGray()) {
            R targetResult = target.apply(request);
            asyncCompare(request, null, targetResult);
            return targetResult;
        }
        R originResult = origin.apply(request);
        asyncCompare(request, originResult, null);
        return originResult;
    }

    private void asyncCompare(T request, R originResult, R targetResult) {
        if (GrayUtil.isGrayRequest(comparePercent, GrayUtil.TEN_THOUSAND)) {
            ThreadPoolExecutor executor = AppContextUtil.getBean("compareExecutor", ThreadPoolExecutor.class);
            CompletableFuture.runAsync(() -> compare(request, originResult, targetResult), executor);
        }
    }

    private void compare(T request, R originResult, R targetResult) {
        if (originResult == null) {
            originResult = origin.apply(request);
        }
        if (targetResult == null) {
            targetResult = target.apply(request);
        }
        CompareResult result = CompareResult.builder()
                .bizType(bizType)
                .request(JSON.toJSONString(request))
                .originResult(JSON.toJSONString(originResult))
                .targetResult(JSON.toJSONString(targetResult))
                .compareResult(compare.apply(originResult, targetResult))
                .build();
        System.out.println(JSON.toJSONString(result));
    }

    @Builder
    public static class CompareResult {

        private String bizType;

        private String request;

        private String originResult;

        private String targetResult;

        private String compareResult;
    }

    @Builder
    public static class Strategy {

        private Long userId;

        private List<Long> whitelistUsers;

        private List<Long> blacklistUsers;

        private Integer cityId;

        private List<Integer> includeCities;

        private String token;

        private List<String> includeTokens;

        private Integer percent;

        public boolean hitGray() {
            try {
                if (userId == null) {
                    return false;
                }
                if (!CollectionUtils.isEmpty(whitelistUsers) && whitelistUsers.contains(userId)) {
                    return true;
                }
                if (!CollectionUtils.isEmpty(blacklistUsers) && blacklistUsers.contains(userId)) {
                    return false;
                }
                if (!CollectionUtils.isEmpty(includeTokens) && !includeTokens.contains(token)) {
                    return false;
                }
                if (!CollectionUtils.isEmpty(includeCities) && !includeCities.contains(cityId)) {
                    return false;
                }
                return GrayUtil.isGrayRequestById(userId, percent, GrayUtil.TEN_THOUSAND);

            } catch (Exception e) {
                log.error("hitGray fail", e);
                return false;
            }
        }
    }
}
