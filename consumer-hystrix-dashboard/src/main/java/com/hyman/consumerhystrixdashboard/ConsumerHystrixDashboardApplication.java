package com.hyman.consumerhystrixdashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;

/**
 * 除了隔离依赖服务的调用以外，Hystrix 还提供了准实时的调用监控（Hystrix Dashboard），Hystrix会持续地记录所有通过 Hystrix 发
 * 起的请求的执行信息，并以统计报表和图形的形式展现给用户，包括每秒执行多少请求多少成功多少失败等。
 * Netflix 通过 Hystrix-metrics-event-stream 项目实现了对以上指标的监控。springcloud 也提供了对 Hystrix Dashboard 的整合，对
 * 监控内容转化成可视化界面。
 * localhost:9001/hystrix，或是 localhost:9001/hystrix.stream
 *
 * 并且要注意 HystrixDashboard 服务监控，在被监控的微服务中必须是引入了 actuator jar 包，才能被监控。
 */
@EnableHystrixDashboard
@SpringBootApplication
public class ConsumerHystrixDashboardApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerHystrixDashboardApplication.class, args);
	}

}
