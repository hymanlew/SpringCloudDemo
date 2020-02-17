package com.hyman.cloudeureka7001;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 该类的作用只是测试 feign 负载请求时，需要进行权限验证的逻辑。没有其他作用。
 */
@RestController
@RequestMapping("/eureka")
public class FeignController {

    /**
     * hystrix 默认超时时间为 1 秒，但是在 provider-dept-8001-hystrix 服务的配置文件中已经定义了全局的 hystrix 配置，所以这里不单独设置了。
     */
    @RequestMapping("/discovery/{id}")
    public Object discovery(@PathVariable("id") Integer id){
      return "eureka service" + id;
    }

}
