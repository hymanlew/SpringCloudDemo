package com.hyman.cloudconfigclient3355;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 分布式系统面临的配置问题：
 * 微服务意味着要将单体应用中的业务拆分成一个个子服务，每个服务的粒度相对较小，因此系统中会出现大量的服务。由于每个服务都需要必
 * 要的配置信息才能运行，所以一套集中的，动态的配置管理设施是必不可少的。我们每个微服务自己带着一个 application.yml，上百个配
 * 置文件的管理就很复杂了，SpringCloud 提供了 ConfigServer 来解决这个问题。
 *
 * SpringCloud Config 为微服务架构中的微服务提供集中化的外部配置支持，配置服务器为各个不同微服务应用的所有环境提供了一个中心化
 * 的外部配置。它也分为服务端和客户端。
 * 服务端也称为分布式配置中心，是一个独立的微服务应用，用来连接配置服务器并为客户端提供获取配置信息，加密/解密信息等访问接口。
 * 客户端则是通过指定的配置中心来管理应用资源，以及与业务相关的配置内容，并在启动的时候从配置中心获取和加载配置信息。
 *
 * 配置服务器默认采用 git 来存储配置信息，这样有助于对环境配置进行版本管理，并且可以通过 git 客户端工具来方便的管理和访问配置内容。
 *
 * 作用：
 * 1，集中管理配置文件。
 * 2，不同环境不同配置，动态化的配置更新，分环境部署（例如 dev/test/prod/release）。
 * 3，运行期间动态调整配置，不再需要在每个服务部署的机器上编写配置文件，服务会向配置中心统一拉取配置自己的信息。
 * 4，当配置发生变动时，服务不需要重启即可感知配置的变化并应用新的配置。
 * 5，将配置信息以 REST 接口的形式暴露。
 */
@SpringBootApplication
public class CloudConfigClient3355Application {

	public static void main(String[] args) {
		SpringApplication.run(CloudConfigClient3355Application.class, args);
	}

}
