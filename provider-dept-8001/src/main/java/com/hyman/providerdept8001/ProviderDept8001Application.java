package com.hyman.providerdept8001;

import org.mybatis.spring.annotation.MapperScan;
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
 * 解决方案有两种：
 * 1，超时机制，通过网络请求其他服务时，都必须设置超时。正常情况下，一个远程调用对应一个线程/进程，去请求时一般在几十毫秒内就返
 * 回了。当依赖的服务不可用，或因为网络问题，响应时间变得很长或几十秒时，这个线程/进程就会得不到释放。而线程进程则对应了系统资源，
 * 如果大量的线程得不到释放，则服务资源就会被耗尽，从而导致服务不可用。所以必须为每个请求设置超时。以防止资源的耗竭。
 *
 * 2，断路器，这才是彻底的解决方案。它就如同家里的电闸，当依赖的服务有大量超时时，再让新的请求去访问已经没有太大的意义，只会无谓
 * 的消耗现有资源。如果我们设置了超时时间为 1 秒，短时间内如果有大量请求在 1 秒内得不到响应，则就往往意味着异常。此时就没有必要
 * 让更新的请求去访问了，此时就应该用断路器避免资源的浪费。
 *
 * 断路器可以实现快速失败，如果它在一段时间内侦测到许多类似的错误（如超时），则就会强迫其之后的多个调用快速失败，不再请求所依赖
 * 的服务。从而可以使应用程序继续执行而不用等待修正错误或继续浪费资源。断路器也可以使应用程序能够诊断错误是否已经修正，如果已修
 * 正，则应用程序会再次尝试调用操作。
 * 断路器模式就像是那些容易导致错误的操作的一种代理，它能够记录最近调用发生错误的次数，然后决定使用允许操作继续，或者立即返回错误。
 *
 *
 * Hystrix 断路器：
 * 它就是一个用于处理分布式系统的延迟和容错的开源库。在分布式系统中许多依赖不可避免的会调用失败，比如超时，异常等。Hystrix 能够
 * 保证在一个依赖出问题时，不会导致整体的服务失败，避免级联故障，以提高分布式系统的弹性。
 * 断路器本身是一种开关装置，当某个服务单元发生故障后，通过断路器的故障监控（如果保险丝）向调用方返回一个符合预期的可处理的备选
 * 响应（fallback），而不是长时间的等待或抛出调用方无法处理的异常，这样就保证了服务调用方的线程不会被长时间，不必要地占用，从而
 * 避免了故障在分布式系统中的蔓延及至雪崩。
 *
 * Hystrix 具体的功能有：接近实时的监控（监控），服务熔断（断路器的状态），服务降级，服务限流（分流），自我修复（断路状态的切换）等等。
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
 *
 * 可以使用 /health 和 /hystrix.stream 路径来检查 hystrix 的启动状态和相关信息（依赖于 actuator 包）。
 * 其中 /hystrix.stream 访问时，如果出现 404，则需要检查是否配置了 home-page-url-path 属性。如果有则要配置 instanceUrlSuffix。
 *
 *
 * @EnableDiscoveryClient 注解是通用性的服务，可以支持其他的服务发现组件。
 * @EnableEurekaClient 注解是专属的 eureka 服务发现，不支持其他的服务发现。
 */
@EnableHystrix
@EnableCircuitBreaker
@MapperScan(basePackages = "com.hyman.providerdept8001.dao")
@EnableEurekaClient
@EnableDiscoveryClient
@SpringBootApplication
public class ProviderDept8001Application {

	public static void main(String[] args) {
		SpringApplication.run(ProviderDept8001Application.class, args);
	}

}
