package com.hyman.providerdepthystrix8001;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * 在分布式系统中可能会面临的问题：
 * 复杂分布式体系结构中的应用程序有数十个依赖关系，每个依赖关系在某些时候将不可避免地失败。
 *
 * 服务雪崩：
 * 多个微服务之间调用的时候，假设服务 A 调用服务 B和 C，而 B和 C又调用其他的微服务，这就是所谓的‘扇出’。如果扇出的链路上某个微
 * 服务的调用响应时间过长或不可用，那么对 A 的调用就会占用越来越多的系统资源，进而引起系统崩溃，所谓的‘雪崩效应’。
 * 对于高流量的应用来说，单一的后端依赖可能会导致所有服务器上的所有资源在几秒内饱和。比失败更糟糕的是，这些应用程序还可能导致服
 * 务之间的延迟增加，备份队列，线程和其他系统资源紧张，导致整个系统发生更多的级联故障。这些都表示需要对故障和延迟进行隔离和管理，
 * 以便单个依赖的失败而影响到整个系统。
 *
 * Hystrix 断路器：
 * 它就是一个用于处理分布式系统的延迟和容错的开源库。在分布式系统中许多依赖不可避免的会调用失败，比如超时，异常等。Hystrix 能够
 * 保证在一个依赖出问题时，不会导致整体的服务失败，避免级联故障，以提高分布式系统的弹性。
 * 断路器本身是一种开关装置，当某个服务单元发生故障后，通过断路器的故障监控（如果保险丝）向调用方返回一个符合预期的可处理的备选
 * 响应（fallback），而不是长时间的等待或抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会被长时间，不必要地占用，从而
 * 避免了故障在分布式系统中的蔓延及至雪崩。
 *
 * Hystrix 具体的功能有：服务降级，服务熔断，服务限流，接近实时的监控等等。
 *
 * 服务熔断：
 * 熔断机制是应对雪崩效应的一种微服务链路保护机制。当扇出链路的某个微服务不可用或响应时间太长时，会进行服务的降级，进而熔断该节
 * 点微服务的调用，快速返回‘错误’的响应信息，即返回一个符合预期的可处理的备选响应（fallback）。当检测到该节点微服务调用响应正常
 * 后再恢复其调用链路。在 springCloud 框架中熔断机制是通过 Hystrix 实现，它会监控微服务间调用的状况，当失败的调用到达一定阈值
 * 后（默认是 5 秒内 20 次调用失败），就会启动熔断机制（熔断的注解是@HystrixCommand）。
 *
 * 服务降级：整体资源快不够了，忍痛将某些服务先关掉，待渡过难关，再开启回来。即资源抢占和分配的问题。服务降级处理是在客户端微服
 * 务中实现完成的，与提供者服务端没有关系。
 * 所谓降级，一般是从整体负荷考虑。就是当某个服务熔断之后，服务器将不再被调用，而是由客户端微服务直接准备一个本地的 fallback 回
 * 调，返回一个信息。这样做，虽然服务水平下降，但好歹可用，比直接挂掉要强。
 * 即当服务端 provider 已经 down 掉了，但是由于使用 hystrix fallback 做了服务降级处理，让客户端在服务端不可用时也能获得提示信
 * 息，而不会挂起耗死服务器。
 *
 * 有了熔断机制而后又有服务降级，是因为：熔断机制是直接作用在具体的方法了，而这就造成了强耦合。也违反了 IOC,AOP 面向切面的思想，
 * 所以就使用熔断加 fallbackFactory 接口的方式实现解耦合，达到了服务降级的目的。
 */

@EnableHystrix			// 启用熔断机制功能
@EnableEurekaClient
@EnableDiscoveryClient
@EnableCircuitBreaker	// 开启对熔断机制功能的支持
@SpringBootApplication
public class ProviderDeptHystrix8001Application {

	public static void main(String[] args) {
		SpringApplication.run(ProviderDeptHystrix8001Application.class, args);
	}

}
