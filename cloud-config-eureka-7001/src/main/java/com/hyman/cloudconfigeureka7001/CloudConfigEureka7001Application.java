package com.hyman.cloudconfigeureka7001;

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
 * 传统的关系型数据库的 ACID 原则：原子性，一致性，独立性（隔离性），持久性。
 * 非关系型或分布式数据库的 CAP 原则：强一致性，高可用性，分区容错性。
 * Eureka 遵守 AP 原则，zookepeer 遵守 CP 原则。作为服务注册中心，前者比后者的优势是：
 *
 * CAP 理论指出，一个分布式系统不可能同时满足这三个要素。但又由于 P（分区容错性）在分布式系统中是必须要保证的，因此 eureka 保证
 * 的是高可用性，而 zookepeer 是保证强一致性。
 *
 * zookeeper 保证 CP：
 * 当向注册中心查询服务列表时，我们可容忍注册中心返回的是几分钟以前的注册信息，但不能接受服务直接 down 掉不可用。就是说服务注册
 * 功能对可用性的要求高于一致性。但 zk 会出现这样一种情况，当 master 主节点因为网络故障与其他节点失去联系时，剩余节点会重新进行
 * leader 选举。问题在于选举 leader 的时间太长（30~120s），且选举期间整个 zk 集群都是不可用的，这就导致在此期间注册服务瘫痪。在
 * 云部署的环境下，因网络问题使得集群失去 master 节点是较大概率发生的事，虽然服务能够最终恢复，但是漫长的选举时间导致的注册长期
 * 不可用是不能容忍的。
 *
 * eureka 保证 AP：
 * 由于 zk 的缺点因此 eureka 在设计时就优先保证可用性。它的各个节点都是平等的，几个节点挂掉不会影响正常节点的工作，剩余的节点依
 * 然可以提供注册和查询服务。而 eureka 的客户端在向某个 eureka 注册时若发现连接失败，则会自动切换至其他节点。只要有一台 eureka
 * 还在，就能保证注册服务可用（即保证可用性），只不过查询到的信息可能不是最新的（不保证强一致性）。除此之外 eureka 还有一种自我
 * 保护机制，如果在 15 分钟内超过 85% 的节点都没有正常的心跳，那么 eureka 就认为客户端与注册中心出现了网络故障。此时会出现以下
 * 几种情况：
 * 1，eureka 不再从注册列表中移除因为长时间没收到心跳而应该过期的服务。
 * 2，eureka 仍然能够接受新服务的注册和查询请求，但暂时不会被同步到其它节点上（即只是保证当前节点依然可用）。
 * 3，当网络稳定时，当前实例新的注册信息会被同步到其它节点中。
 * 因此 eureka 可以很好的应对因网络故障导致部分节点失去联系的情况，而不会像 zk 那样使整个注册服务瘫痪。
 *
 * @EnableEurekaServer, eureka 服务器端启动类，接受其它微服务注册进来。
 */
@SpringBootApplication
@EnableEurekaServer
public class CloudConfigEureka7001Application {

	public static void main(String[] args) {
		SpringApplication.run(CloudConfigEureka7001Application.class, args);
	}

}
