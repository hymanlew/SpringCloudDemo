package com.hyman.cloudeureka7001;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka 是一个基于 REST 的服务，用于定位服务，以实现云端中间层服务发现和故障转移。服务注册与发现对于微服务架构来说是非常重要
 * 的，有了它们只需要使用服务的标识符（即服务的名称）就可以访问到服务，而不需要修改服务调用的配置文件了。功能类似于 dubbo 的注
 * 册中心（Zookeeper）。
 *
 * Eureka 采用了 C-S 的设计架构。它包含两个组件：Eureka Server 和  Eureka Client。
 *
 * Eureka Server 作为服务注册功能的服务器，即服务注册中心。各个节点启动后，会在 server 中进行注册，这样 server 的服务注册表中
 * 将会存储所有可用服务节点的信息，该信息可以在界面中直观的看到。
 *
 * Eureka Client 是一个 java 客户端，用于简化 Eureka Server 的交互。它同时也具备一个内置的，使用轮询（round-robin）负载算法的
 * 负载均衡器。在应用启动后，将会向 Eureka Server 发送心跳（默认周期为 30 秒）。如果 Eureka Server 在多个心跳周期内没有接收到
 * 某个节点的心跳，则 server 将从服务注册表中把这个服务节点移除（默认为 90 秒）。
 * 系统中的其他微服务，使用 Eureka 的客户端连接到 Eureka server并维持心跳连接。如此系统维护人员就可通过 Eureka server 来监控
 * 系统中各个微服务是否正常运行。SpringCloud 的其他模块（zuul 等等）就可通过 Eureka Server 来发现系统中的其他微服务，并执行相
 * 关的逻辑。
 *
 * Eureka Server 提供服务注册与发现。
 * Service Provider 服务提供方将自身服务注册到 Eureka，从而使服务消费方能够找到。
 * Service Consumer 服务消费方从 Eureka 获取注册服务列表，从而能够消费服务。
 *
 * Eureka 的自我保护机制（好死不如赖活着）：
 * 某个时刻某个微服务不可用了，eureka 不会立刻清理，而是依旧会对该微服务的信息进行保存。进行留存。
 * 默认情况下，如果 EurekaServer 在一定时间内没有接收到某个微服务实例的心跳，则 EurekaServer 将注销该实例（默认 90 秒）。但是
 * 当网络分区故障发生时，微服务与 EurekaServer 之间无法正常通信，以上行为就可能非常危险（因为微服务本身是健康的，此时就不应该注
 * 销该微服务）。
 * 所以 Eureka 通过自我保护模式来解决此问题，当 EurekaServer 节点在短时间内丢失过多客户端时（可能发生了网络分区故障），那么这个
 * 节点就会进入自我保护模式。一旦进入该模式，EurekaServer 就会保护服务注册表中的信息，不再删除其中的数据（也就是不会注销任何微
 * 服务）。当网络故障恢复后它收到的心跳数重新恢复到阈值以上时，该 EurekaServer 节点会自动退出自我保护模式。
 * 综上，自我保护模式是一种应对网络异常的安全保护措施，其设计哲学就是宁可保留错误的服务注册信息（即同时保留所有微服务，健康的不
 * 健康的都会保留），也不盲目注销任何可能健康的服务实例，即好死不如赖活着。自我保护模式使得 Eureka 集群更加的健壮，稳定。
 *
 * 在 springcloud 中，可以使用 eureka.server.enable-self-preservation= false 来禁用自我保护模式。
 *
 *
 * @EnableEurekaServer, eureka 服务器端启动类，接受其它微服务注册进来。
 */
@SpringBootApplication
@EnableEurekaServer
public class CloudEureka7001Application {

	public static void main(String[] args) {
		SpringApplication.run(CloudEureka7001Application.class, args);
	}

}
