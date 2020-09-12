package com.hyman.nacosconfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 如果 eurekaServer 设置了登录密码，就必须关闭Spring Security的 CSRF 验证。否则户端就连接不上。
 *
 * 当某个服务添加了权限认证后，则此服务在 eureka 中注册实例会出现在 unavailable-replicas 里面，这是因为加入了安全认证模块后，
 * 默认会开启 CSRF 跨站脚本攻击，需要禁用它，
 * 参考：https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-eureka-server.html#_securing_the_eureka_server
 */
@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // 这样配置后，打开 Eureka 控制台页面会先要跳到登录页面做登录认证才能访问。
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 关闭csrf
        //http.csrf().disable();

        http.csrf().ignoringAntMatchers("/eureka/**");
        super.configure(http);
    }
}
