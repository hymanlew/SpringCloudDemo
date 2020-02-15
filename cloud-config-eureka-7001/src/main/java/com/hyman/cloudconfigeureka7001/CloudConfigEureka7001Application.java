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
 *
 服务发现组件的功能（包含服务注册，发现，注册中心）：
 服务注册表，是一个记录当前可用服务实例的网络信息的数据库，是服务发现机制的核心。是包含服务实例的网络地址的数据库。服务注册表需要高可用而且随时更新。
 服务注册和注销，该组件提供了查询 API 和管理 API，前者可以获取可用的服务实例，后者实现注册和注销（即服务注册注销）。服务实例必须在注册表中注册和注销。
 健康检查，心跳机制。

 服务实例的网络位置都是动态分配的。由于扩展、失败和升级，服务实例会经常动态改变，因此客户端代码需要使用更加复杂的服务发现机制。服务发现有两种方式：
 1，客户端发现（Eureka，zk），该模式下客户端决定相应服务实例的网络位置，并且对请求实现负载均衡。客户端查询服务注册表（它包含了所有的可用服务实例），然后使用负载均衡算法从中选择一个实例，并发出请求。服务实例的网络位置在启动
 时被记录到服务注册表，等实例终止时被删除。服务实例的注册信息通常使用心跳机制来定期刷新。
 客户端发现模式优缺点兼有。该模式相对直接，除了服务注册外，其它部分无需变动。且由于客户端知晓可用的服务实例，能针对特定应用实现智能负载均衡，比如使用哈希一致性。这种模式的缺点就是客户端与服务注册绑定，要针对服务端用到的每个
 编程语言和框架，实现客户端的服务发现逻辑。
 2，服务端发现（Consul + nginx），客户端通过负载均衡器向某个服务提出请求，负载均衡器查询服务注册表，并将请求转发到可用的服务实例。如同客户端发现，服务实例在服务注册表中注册或注销。
 AWS Elastic Load Balancer（ELB）是服务端发现路由的例子，ELB 通常均衡来自互联网的外部流量，也可用来负载均衡 VPC（Virtual private cloud）的内部流量。客户端使用 DNS 通过 ELB 发出请求（HTTP 或 TCP），ELB 在已注册的 EC2 实例或
 ECS 容器之间负载均衡。这里并没有单独的服务注册表，相反 EC2 实例和 ECS 容器注册在 ELB。HTTP 服务器与 NGINX 这样的负载均衡起也能用作服务端的发现均衡器。在更复杂的实现中，需要使用 HTTP API 或 DNS 来动态配置 NGINX Plus。
 服务端发现最大的优点是客户端无需关注发现的细节，只需要简单地向负载均衡器发送请求，这减少了编程语言框架需要完成的发现逻辑。并且某些部署环境免费提供这一功能（nginx）。但缺点是，除非负载均衡器由部署环境提供，否则会成为一个需
 要配置和管理的高可用系统组件。

 服务注册的方式：
 1，自注册，服务实例自己在服务注册表中注册和注销。另外如果需要的话，一个服务实例也要发送心跳来保证注册信息不会过时（例如 Eureka）。该模式优点是相对简单，无需其它系统组件。缺点是把服务实例和服务注册表耦合，必须在每个编程语言
 和框架内实现注册代码。
 2，第三方注册，是采用管理服务实例注册的其它系统组件，将服务与服务注册表解耦合。即服务实例不需要向服务注册表注册；而是由被称为服务注册器的另一个系统模块会处理。服务注册器会通过查询部署环境或订阅事件的方式来跟踪运行实例的更改。
 一旦侦测到有新的可用服务实例，会向注册表注册此服务。服务管理器也负责注销终止的服务实例。
 其优点是服务与服务注册表解耦合，无需为每个编程语言和框架实现服务注册逻辑；服务实例是通过一个专有服务以中心化的方式进行管理。它的不足之处在于，除非该服务内置于部署环境，否则需要配置和管理一个高可用的系统组件。
 Registrator 是一个开源的服务注册项目，它能够自动注册和注销被部署为 Docker 容器的服务实例。Registrator 支持包括 etcd 和 Consul 在内的多种服务注册表。
 *
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
