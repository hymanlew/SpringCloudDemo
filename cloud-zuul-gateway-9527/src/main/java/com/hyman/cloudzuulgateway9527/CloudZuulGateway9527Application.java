package com.hyman.cloudzuulgateway9527;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 在决定以一组微服务来构建自己的应用时，就需要确定应用客户端如何与微服务交互。
 * 1，在单体式程序中，通常只有一组冗余的或者负载均衡的服务提供点。当客户端发起一次 REST 调用获取数据时，负载均衡器将请求路由给 N 个相同的应用程序实例（集群）中的其中之一。然后应用程序会查询各种数据库表，并将响应返回给客户端。
 * 2，而在微服务架构中，每一个微服务暴露一组细粒度的服务提供点。此时客户端访问这些服务时有几种方式：
 *
 * 2.1，客户端与微服务直接通信：
 * 从理论上讲，客户端可以直接向每个微服务发送请求。每个微服务都有一个开放的端点，该 URL 映射到微服务的负载均衡器，由后者负责在可用实例之间分发请求。当客户端请求数据时，会在微服务之间逐一的发送请求（如果需要调用多个微服务的话）。
 * 但这种方法存在挑战和局限。问题之一是客户端需求和每个微服务暴露的细粒度 API 不匹配（例如，客户端需要在多个服务之间发送 N 个独立请求）。然而客户端通过 LAN 发送许多请求，这在公网上可能会很低效，在移动网络上就根本不可行。并且该
 * 方法还使得客户端代码非常复杂。
 * 客户端直接调用微服务的另一个问题是，部分服务使用的协议对 web 并不友好。一个服务可能使用 Thrift 二进制 RPC，而另一个服务可能使用 AMQP 消息传递协议。不管哪种协议对于浏览器或防火墙都不够友好，最好是内部使用。在防火墙之外，应用
 * 程序应该使用诸如 HTTP 和 WebSocket 之类的协议。
 * 该方法的第三个缺点是，它会使得微服务难以重构。随着时间推移，我们可能想要更改系统拆分成服务的方式。例如合并两个服务，或者将一个服务拆分成两个或更多服务。然而如果客户端与微服务直接通信，那么执行这类重构就非常困难了。
 * 由于上述三种问题的原因，客户端直接与服务器端通信的方式很少在实际中使用。
 *
 * 2.2，使用 API 网关构建微服务：
 * 通常来说，使用 API 网关是更好的解决方式。API 网关是一个服务器，也可以说是进入系统的唯一节点。这与面向对象设计模式中的 Facade 外观模式很像。API 网关封装内部系统的架构，并且提供 API 给各个客户端。它还可以具备授权、监控、负载均
 * 衡、缓存、请求分片和管理、静态响应处理等功能。
 * API 网关负责服务请求路由、组合及协议转换。客户端的所有请求都首先经过 API 网关，然后由它将请求路由到合适的微服务。API 网关经常会通过调用多个微服务并合并结果来处理一个请求。它可以在 web 协议（如 HTTP 与 WebSocket）与内部使用
 * 的非 web 友好协议之间转换。它还能为每个客户端提供一个定制的 API，即它会向客户端暴露一个粗粒度的 API 来分别代表和指向各个微服务，并通过调用各个服务并合并结果来处理请求。
 *
 * API 网关的优点是，它封装了应用程序的内部结构。客户端只需要同网关交互，而不必调用特定的服务。它为每一类客户端提供了特定的 API，这减少了客户端与应用程序间的交互次数，还简化了客户端代码。
 * API 网关的缺点是，它增加了一个我们必须开发、部署和维护的高可用组件。还有一个风险是，它变成了开发瓶颈。为了暴露每个微服务的端点，开发人员必须更新 API 网关。API网关的更新过程要尽可能地简单，这很重要；否则为了更新网关，开发人员
 * 将不得不排队等待。不过虽然有这些不足，但对于大多数现实世界的应用程序而言，使用 API 网关是合理的。
 *
 * 对于大多数应用程序而言，API 网关的性能和可扩展性都非常重要。因此将 API 网关构建在一个支持异步、I/O 非阻塞的平台上是合理的。有多种不同的技术可以实现一个可扩展的 API 网关。在 JVM 上可以使用一种基于 NIO 的框架，比如 Netty、Vertx、
 * Spring Reactor 或 JBoss Undertow 中的一种。一个非常流行的非 JVM 选项是 Node.js，它是一个基于 Chrome JavaScript 引擎构建的平台。另一个方法是使用 NGINX Plus。NGINX Plus 提供了一个成熟的、可扩展的、高性能 web 服务器和一个易于
 * 部署的、可配置可编程的反向代理。它可以管理身份验证、访问控制、负载均衡请求、缓存响应，并提供应用程序可感知的健康检查和监控。
 *
 * 使用响应式编程模型：
 * API 网关通过简单地将请求路由给合适的后端服务来处理部分请求，而通过调用多个后端服务并合并结果来处理其它请求。对于部分请求，比如产品详情相关的多个请求，它们对后端服务的请求是独立于其它请求的。为了最小化响应时间，API 网关应该并
 * 发执行独立请求。然而，有时候，请求之间存在依赖。在将请求路由到后端服务之前，API 网关可能首先需要调用身份验证服务验证请求的合法性。关于 API 组合，另一个有趣的例子是 Netflix Video Grid。
 * 使用传统的异步回调方法编写 API 组合代码会让你迅速坠入回调地狱。代码会变得混乱、难以理解且容易出错。一个更好的方法是使用响应式方法，以一种声明式样式编写 API 网关代码。响应式抽象概念的例子有 Scala 中的 Future、Java 8 中的 CompletableFuture
 * 和 JavaScript 中的 Promise。Netflix 创建了 RxJava for JVM，专门用于他们的 API 网关。此外还有 RxJS for JavaScript，它既可以在浏览器中运行，也可以在 Node.js 中运行。使用响应式方法能让你编写简单但高效的 API 网关代码。
 *
 * 服务调用：
 * 基于微服务的应用程序是一个分布式系统，必须使用一种进程间通信机制。有两种类型的进程间通信机制可供选择。一种是使用异步的、基于消息传递的机制。有些实现使用诸如 JMS 或 AMQP 那样的消息代理，而其它的实现（如 Zeromq）则没有代理，
 * 服务间直接通信。另一种进程间通信类型是诸如 HTTP 或 Thrift 那样的同步机制。通常一个系统会同时使用异步和同步两种类型。它甚至还可能使用同一类型的多种实现。总之，API 网关需要支持多种通信机制。
 *
 * 服务发现：
 * API 网关需要知道它与之通信的每个微服务的位置（IP 地址和端口）。在传统的应用程序中，或许可以硬连线这个位置，但在现代的、基于云的微服务应用程序中，这并不是一个容易解决的问题。基础设施服务（如消息代理）通常会有一个静态位置，可以
 * 通过 OS 环境变量指定。但是确定一个应用程序服务的位置没有这么简单。应用程序服务的位置是动态分配的，而且单个服务的一组实例也会随着自动扩展或升级而动态变化。总之像系统中的其它服务客户端一样，API 网关需要使用系统的服务发现机制，
 * 可以是服务器端发现（Consul + nginx），也可以是客户端发现（Eureka，zk）。如果使用客户端发现，那么 API 网关必须能够查询服务注册中心，这是一个包含所有微服务实例及其位置的数据库。
 *
 * 处理局部失败：
 * 在实现 API 网关时，还需要处理局部失败的问题。该问题出现在所有的分布式系统中。当一个服务调用另一个服务，而后者响应慢或不可用的时候，就会出现这个问题。API 网关不能因为无限期地等待下游服务而阻塞。不过如何处理失败取决于特定的场景
 * 以及哪个服务失败。或返回一个特定的内容，或返回一个错误信息，或返回缓存数据。缓存数据可以由 API 网关自己缓存，也可以存储在像 Redis 或 Memcached 之类的外部缓存中。通过返回默认数据或者缓存数据，API 网关可以确保系统故障不影响用户体验。
 * 在编写代码调用远程服务方面，Netflix Hystrix 有大用处，它会暂停超出特定阈限的调用。它实现了一个断路器（circuit breaker）模式，可以防止客户端对无响应的服务进行不必要的等待。如果服务的错误率超出了设定的阈值，那么 Hystrix 会启动断路
 * 器，所有请求会立即失败并持续一定时间。Hystrix 允许用户定义一个请求失败后的后援操作，比如从缓存读取数据，或者返回一个默认值。
 *
 * 服务之间的交互必须通过进程间通信（IPC）来实现。当为某个服务选择 IPC 时，首先需要考虑服务之间的交互问题。客户端和服务器之间有很多的交互模式，我们可以从两个维度进行归类。
 * 第一个维度是一对一还是一对多：前者是每个客户端请求有一个服务实例来响应。后者是每个客户端请求有多个服务实例来响应。
 * 第二个维度是这些交互式是同步模式还是异步模式：前者是客户端请求需要服务端即时响应，甚至可能由于等待而阻塞。后者是客户端请求不会阻塞进程，服务端的响应可以是非即时的。
 * 每个服务都是以上这些模式的组合。对某些服务，一个 IPC 机制就足够了；而对另外一些服务则需要多种 IPC 机制组合。
 *
 * 一对一的交互模式有以下几种方式：
 * 通知（也就是常说的单向请求）：一个客户端请求发送到服务端，但是并不期望服务端响应。
 * 请求/响应：一个客户端向服务器端发起请求，等待响应，客户端期望此响应即时到达。在一个基于线程的应用中，等待过程可能造成线程阻塞。
 * 请求/异步响应：客户端发送请求到服务端，服务端异步响应请求。客户端不会阻塞，而且被设计成默认响应不会立刻到达。
 *
 * 一对多的交互模式有以下几种方式：
 * 发布/ 订阅模式：客户端发布通知消息，被零个或者多个感兴趣的服务消费。
 * 发布/异步响应模式：客户端发布请求消息，然后等待从感兴趣服务发回的响应。
 *
 * 定义 API：
 * API 是服务端和客户端之间的契约。无论选择了何种 IPC 机制，重点是使用某种交互定义语言（IDL）来准确定义服务的 API。在开发服务之前，要定义服务接口并与客户端开发者共同讨论，后续只需要迭代 API 定义。这样的设计能够大幅提升服务的可用
 * 度。即 API 定义实质上依赖于选定的 IPC 机制。如果使用消息机制，API 则由消息频道（channel）和消息类型构成；如果选择使用 HTTP 机制，API 则由 URL 和请求、响应格式构成。后面将会详细描述 IDL。
 *
 * 不断进化的 API：
 * 服务的 API 会随着时间而不断变化。在单体应用中，经常会直接修改 API 并更新所有的调用者。但是在基于微服务的应用中，即使所有的 API 的使用者都在同一应用中，这种做法也困难重重，通常不能强制让所有客户端都与服务保持同步更新。此外还可
 * 能会增量部署服务的新版本，这时旧版本会与新版本同时运行。了解这些问题的处理策略至关重要。对 API 变化的处理方式与变化的大小有关。有的变化很小，并且可以兼容之前的版本；比如给请求或响应增加属性。在设计客户端和服务时，很有必要遵循
 * 健壮性原则。服务更新版本后，使用旧版 API 的客户端应该继续使用。服务为缺失的请求属性提供默认值，客户端则忽略任何额外的响应。使用 IPC 机制和消息格式能够让你轻松改进 API。
 * 然而有时候 API 需要进行大规模改动，并且不兼容旧版本。鉴于不能强制让所有客户端立即升级，支持旧版 API 的服务还要再运行一段时间。如果你使用的是诸如 REST 这样的基于 HTTP 机制的 IPC，一种方法就是将版本号嵌入到 URL 中，每个服务实例
 * 可以同时处理多个版本。另一种方法是部署不同实例，每个实例处理一个版本的请求。
 *
 * 处理局部失败：
 * 我们了解到，分布式系统普遍存在局部失败的问题。由于客户端和服务端是独立的进程，服务端可能无法及时响应客户端请求。服务端可能会因为故障或者维护而暂时不可用。服务端也可能会由于过载，导致对请求的响应极其缓慢。为了预防这种问题，设
 * 计服务时候必须要考虑部分失败的问题。Netfilix 提供了一个比较好的解决方案，具体的应对措施包括：
 * 1，网络超时：在等待响应时，不设置无限期阻塞，而是采用超时策略。使用超时策略可以确保资源不被无限期占用。
 * 2，限制请求的次数：可以为客户端对某特定服务的请求设置一个访问上限。如果请求已达上限，就要立刻终止请求服务。
 * 3，断路器模式：记录成功和失败请求的数量。如果失效率超过一个阈值，触发断路器使得后续的请求立刻失败。如果大量的请求失败，就可能是这个服务不可用，再发请求也无意义。在一个失效期后，客户端可以再试，如果成功，关闭此断路器。
 * 4，提供回滚：当一个请求失败后可以进行回滚逻辑。例如，返回缓存数据或者一个系统默认值。
 * Netflix Hystrix 是一个实现相关模式的开源库。如果使用 JVM，推荐使用Hystrix。而如果使用非 JVM 环境，你可以使用类似功能的库。
 *
 * IPC 技术：
 * 现在有很多不同的 IPC 技术。服务间通信可以使用同步的请求/响应模式，比如基于 HTTP 的 REST 或者 Thrift。也可以选择异步的、基于消息的通信模式，比如 AMQP 或者 STOMP。还可以选择 JSON 或者 XML 这种可读的、基于文本的消息格式。当然
 * 也还有效率更高的二进制格式，比如 Avro 和 Protocol Buffer。在讨论同步的 IPC 机制之前，我们先了解异步的 IPC 机制。
 *
 * 1，基于消息的异步通信：
 * 有多种消息系统可供选择，最好选择支持多编程语言的。有的消息系统支持 AMQP 和 STOMP 这样的标准协议，有的则支持专利协议。也有大量的开源消息系统可用，譬如 RabbitMQ、Apache Kafka、Apache ActiveMQ 和 NSQ。宏观上它们都支持一
 * 些消息和渠道格式，并且努力提升可靠性、高性能和可扩展性。然而，细节上它们的消息模型却大相径庭。
 *
 * 使用消息机制有很多优点：
 * 解耦客户端和服务端：客户端只需要将消息发送到正确的渠道。客户端完全不需要了解具体的服务实例，更不需要一个发现机制来确定服务实例的位置。
 * 消息缓冲：在 HTTP 这样的同步请求/响应协议中，所有的客户端和服务端必须在交互期间保持可用。而在消息模式中，消息中间人将所有写入渠道的消息按照队列方式管理，直到被消费者处理。
 * 客户端-服务端的灵活交互：消息机制支持以上说的所有交互模式。
 * 清晰的进程间通信：基于 RPC 的通信机制试图让唤醒远程服务端像调用本地服务一样，然而由于物理定律和可能的局部失败，这二者大不相同。消息机制能让这些差异直观明确，开发者不会产生安全错觉。
 *
 * 然而，消息机制也有自己的缺点：
 * 额外的操作复杂性：消息系统需要单独安装、配置和部署。消息broker（代理）必须高可用，否则系统可靠性将会受到影响。
 * 实现基于请求/响应交互模式的复杂性：每个请求消息必须包含一个回复渠道 ID 和相关 ID。服务端发送一个包含相关 ID 的响应消息到渠道中，使用相关 ID 来将响应对应到发出请求的客户端。而此时使用一个直接支持请求/响应的 IPC 机制会更容易些。
 *
 * 2，基于请求/响应的同步 IPC：
 * 使用同步的、基于请求/响应的 IPC 机制的时候，客户端向服务端发送请求，服务端处理请求并返回响应。一些客户端会由于等待服务端响应而被阻塞，而另外一些客户端可能使用异步的、基于事件驱动的客户端代码，这些代码可能通过 Future 或者 Rx Observable
 * 封装。然而与使用消息机制不同，客户端需要响应及时返回。这个模式中有很多可选的协议，但最常见的两个协议是 REST 和 Thrift。首先我们来了解 REST。
 *
 * REST（RESTful）：
 * REST 基于 HTTP 协议，其核心概念是资源典型地代表单一业务对象或者一组业务对象，业务对象包括“消费者”或“产品”。REST 使用 HTTP 协议来控制资源，通过 URL 实现（即 get，post，put 等方法）。REST 提供了一系列架构系统参数作为整体使
 * 用，强调组件交互的扩展性、接口的通用性、组件的独立部署、以及减少交互延迟的中间件，它强化安全，也能封装遗留系统。你写的所谓 API 并非都是 RESTful，它有一个成熟度模型，具体包含以下四个层次：
 * Level 0：本层级的 Web 服务只是使用 HTTP 作为传输方式，实际上只是远程方法调用（RPC）的一种具体形式。SOAP 和 XML-RPC 都属于此类。
 * Level 1：本层级的 API 引入了资源的概念。要执行对资源的操作，客户端发出指定要执行的操作和任何参数的 POST 请求。
 * Level 2：本层级的 API 使用 HTTP 语法来执行操作，譬如 GET 表示获取、POST 表示创建、PUT 表示更新。如有必要，请求参数和主体指定操作的参数。这能够让服务影响 web 基础设施服务，如缓存 GET 请求。
 * Level 3：本层级的 API 基于 HATEOAS（Hypertext As The Engine Of Application State）原则设计，基本思想是在 GET请求返回的资源信息中包含链接，这些链接能够被执行该资源允许的操作。例如，客户端通过订单资源中包含的链接取消某一订单，
 * GET 请求被发送去获取该订单。HATEOAS 的优点包括无需在客户端代码中写入硬链接的 URL。此外由于资源信息中包含可允许操作的链接，客户端无需猜测在资源的当前状态下执行何种操作。
 *
 * 使用基于 HTTP 的协议有如下好处：
 * HTTP 非常简单并且大家都很熟悉。可以使用浏览器扩展（比如 Postman）或者 curl 之类的命令行来测试 API。
 * 内置支持请求/响应模式的通信。
 * HTTP 对防火墙友好。
 * 不需要中间代理，简化了系统架构。
 *
 * 不足之处包括：
 * 只支持请求/响应模式交互。尽管可以使用 HTTP 通知，但是服务端必须一直发送 HTTP 响应。
 * 由于客户端和服务端直接通信（没有代理或者缓冲机制），在交互期间必须都保持在线。
 * 客户端必须知道每个服务实例的 URL。即客户端必须使用服务实例发现机制。
 *
 * 开发者社区最近重新认识到了 RESTful API 接口定义语言的价值，于是诞生了包括 RAML 和 Swagger 在内的服务框架。Swagger 这样的 IDL 允许定义请求和响应消息的格式，而 RAML 允许使用 JSON Schema 这种独立的规范。对于描述 API，IDL 通常
 * 都有工具从接口定义中生成客户端存根和服务端框架。
 *
 * Thrift：
 * Apache Thrift 是一个很有趣的 REST 的替代品，实现了多语言 RPC 客户端和服务端调用。Thrift 提供了一个 C 风格的 IDL 定义 API。通过 Thrift 编译器能够生成客户端存根和服务端框架。编译器可以生成多种语言的代码，包括 C++、Java、Python、PHP、
 * Ruby, Erlang 和 Node.js。
 * Thrift 接口由一个或多个服务组成，服务定义与 Java 接口类似，是一组强类型方法的集合。Thrift 能够返回（可能无效）值，也可以被定义为单向。返回值的方法能够实现交互的请求/响应模式。客户端等待响应，可能会抛出异常。单向方法与交互的通知模
 * 式相对应。服务端不会发送响应。Thrift 支持 JSON、二进制和压缩二进制等多种消息格式。由于解码更快，二进制比 JSON 更高效；压缩二进制格式可以提供更高级别的压缩效率；同时 JSON 则易读。Thrift 也能够让你选择传输协议，包括原始 TCP 和 HTTP。
 * 原始 TCP 比 HTTP 更高效，然而 HTTP 对于防火墙、浏览器和使用者来说更友好。
 *
 * 消息格式：
 * 了解 HTTP 和 Thrift 后，我们要考虑消息格式的问题。如果使用消息系统或者 REST，就需要选择消息格式。像 Thrift 这样的 IPC 机制可能只支持少量消息格式，或许只支持一种格式。无论哪种情况，使用跨语言的消息格式非常重要。即便你现在使用单一
 * 语言实现微服务，但很有可能未来需要用到其它语言。目前有文本和二进制这两种主要的消息格式。
 *
 * 1，文本格式包括 JSON 和 XML。这种格式的优点在于不仅可读，而且是自描述的。
 * 在 JSON 中，对象的属性是名称-值对的集合。与此类似，在 XML 中属性则表示为命名的元素和值。消费者能够从中选择感兴趣的值同时忽略其它部分。相应地，对消息格式的小幅度修改也能容易地向后兼容。
 * XML 的文档结构由 XML schema 定义。随着时间发展，开发者社区意识到 JSON 也需要一个类似的机制。方法之一是使用 JSON Schema，要么独立使用，要么作为 Swagger 这类 IDL 的一部分。
 * 文本消息格式的一大缺点是消息会变得冗长，特别是 XML。由于消息是自描述的，所以每个消息都包含属性和值。另外一个缺点是解析文本的负担过大。所以，你可能需要考虑使用二进制格式。
 *
 * 2，二进制的格式也有很多。如果使用的是 Thrift RPC，那可以使用二进制 Thrift。如果选择消息格式，常用的还包括 Protocol Buffers 和 Apache Avro，二者都提供类型 IDL 来定义消息结构。差异之处在于 Protocol Buffers 使用添加标记的字段（tagged
 * fields），而 Avro 消费者需要了解模式来解析消息。
 *
 * 总结：微服务必须使用进程间通信机制来交互。在设计服务的通信模式时，你需要考虑几个问题：服务如何交互，每个服务如何标识 API，如何升级 API，以及如何处理局部失败。微服务架构异步消息机制和同步请求/响应机制这两类 IPC 机制可用。
 *
 *
 * Netflix API 网关（zuul）是一个很好的 API 网关实例。其主要作用有：身份验证，压力测试，金丝雀测试，动态路由，服务迁移，负载减载，安全，静态响应处理，主动/主动交通管理。并且其规则引擎允许用基本上任何JVM语言编写规则和过滤器，并内
 * 置对Java和Groovy的支持。所有的路由，默认是 Hystrix隔离模式（ExecutionIsolationStrategy）的信号量。如果要更换为线程模式，则可以将 zuul.ribbonIsolationStrategy 更改为THREAD。
 * Zuul 包含了对请求的路由和过滤两个最主要的功能：
 * 其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验，服务聚合等功能的基础。
 * 即提供：代理，路由，过滤，三大功能。
 *
 * 需要注意的是，Zuul starter不包括发现客户机（即不包含服务发现功能），因此对于基于服务id的路由，还需要在类路径上提供一个服务发现的中间件（例如 Eureka）。
 * Zuul 与 Eureka 整合：将 Zuul 自身注册为 Eureka 服务治理下的应用，同时从 Eureka 中获得其他微服务的消息（Zuul 会自动将 Eureka 中已知的每个服务的路由添加到/<serviceId>），也即以后的访问微服务都是通过 Zuul 跳转后获得。
 * 使用时，直接调用 zuulIP:port/其他某个微服务的 application-name/微服务路径，即可。即 zuul 默认会代理和映射所有注册到 eureka 中的微服务。当然也可以配置 routes 属性指定服务映射关系。
 * 并且可以用 zuulIP:port/routes 查看当前网关所代理的所有服务。
 *
 * Zuul 的高可用可以分两种场景讨论：
 * 1，Zuul客户端也注册到了Eureka Server上。此时，Zuul的高可用很简单，只需将多个Zuul节点注册到Eureka Server上，就可实现Zuul的高可用。此时，Zuul的高可用与其他微服务的高可用没什么区别。Zuul客户端会自动从Eureka Server中查询Zuul Server
 *    的列表，并使用Ribbon负载均衡地请求Zuul集群。这种场景一般用于Sidecar。
 * 2，Zuul客户端未注册到Eureka Server上。这种场景在现实中更常见，例如 Zuul客户端是一个手机APP，我们不可能让所有的手机终端都注册到Eureka Server上。此时，就需要借助一个额外的负载均衡器来实现Zuul的高可用，例如Nginx、HAProxy、F5等。
 */
@SpringBootApplication
@EnableZuulProxy
public class CloudZuulGateway9527Application {

	public static void main(String[] args) {
		SpringApplication.run(CloudZuulGateway9527Application.class, args);
	}

	/**
	 * 除了在配置文件中配置的几种路由规则。也可以指定为使用 version 版本号的映射路径。例如有一个服务为 mic-dept-v1（v1为版本号），则请求路径为：
	 * zuulIP:port/v1/mic-dept/微服务路径
	 */
	@Bean
	public PatternServiceRouteMapper serviceRouteMapper() {
		return new PatternServiceRouteMapper(
			"(?<name>^.+)-(?<version>v.+$)",
			"${version}/${name}");
	}

	/**
	 * 启用跨源请求。
	 * 默认情况下，Zuul 将所有跨源请求（cor）路由到服务。如果想让 Zuul 处理这些请求，可以自定义 WebMvcConfigurer bean 来完成。
	 * @return
	 */
	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/path-1/**")
						.allowedOrigins("https://allowed-origin.com")
						.allowedMethods("GET", "POST");
			}
		};
	}
}
