package com.hyman.myrule;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 要特别注意：自定义的 Ribbon 负载配置算法类不能放在 @ComponentScan 扫描的当前包及其子包下（即主启动类所在的包及其子包）。否
 * 则此自定义配置类就会被所有的 Ribbon 客户端所共享，也就是说这就达不到特殊化定制的目的了。
 */
@Configuration
public class MyRule {

    // 显示声明一个自定义的负载算法，它将会替换掉原有系统中的负载算法（需要重新启动所有的服务）。
    @Bean
    public IRule myRule(){
        return new MyConfigRule();
    }
}
