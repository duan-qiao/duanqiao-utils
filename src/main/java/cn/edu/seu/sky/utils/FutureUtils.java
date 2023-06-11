package cn.edu.seu.sky.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author xiaotian on 2022/9/16
 */
public class FutureUtils {

    public static <T, R> FutureBuilder<T, R> newBuilder() {
        return new FutureBuilder<>();
    }

    public static class FutureBuilder<T, R> {

        private List<T> taskList;

        private Function<T, R> function;

        private Executor executor = ForkJoinPool.commonPool();

        public List<R> supplyAsync() {
            List<CompletableFuture<R>> futures = taskList.stream().map(t -> toFuture(t, executor)).collect(Collectors.toList());
            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            return allOf.thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList())).join();
        }

        public Map<T, R> supplyAsyncMap() {
            Map<T, CompletableFuture<R>> futures = taskList.stream().collect(
                    Collectors.toMap(Function.identity(), t -> toFuture(t, executor))
            );
            CompletableFuture<Void> allOf = CompletableFuture.allOf(
                    futures.values().toArray(new CompletableFuture[0])
            );
            return allOf.thenApply(x -> futures.entrySet().stream().collect(
                    Collectors.toMap(Map.Entry::getKey, e -> e.getValue().join())
            )).join();
        }

        private CompletableFuture<R> toFuture(T t, Executor executor) {
            return CompletableFuture.supplyAsync(() -> function.apply(t), executor);
        }

        public FutureBuilder<T, R> taskList(List<T> taskList) {
            this.taskList = taskList;
            return this;
        }

        public FutureBuilder<T, R> function(Function<T, R> function) {
            this.function = function;
            return this;
        }

        public FutureBuilder<T, R> executor(Executor executor) {
            this.executor = executor;
            return this;
        }
    }

    public static void main(String[] args) {
        List<String> taskIds = new ArrayList<>();
        taskIds.add("1");
        taskIds.add("2");
        taskIds.add("3");

        List<Double> values = FutureUtils.<String, Double>newBuilder()
                .taskList(taskIds)
                .function(Double::valueOf)
                .executor(ForkJoinPool.commonPool())
                .supplyAsync();
        System.out.println(values);
    }
}
