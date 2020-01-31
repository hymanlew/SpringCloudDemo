package com.hyman.cloudapi.service;

import com.hyman.cloudapi.entity.Department;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 使用实现 FallbackFactory 的方式统一对 service 接口层的熔断机制进行处理，从而避免对方法使用 @HystrixCommand 强耦合的方式进
 * 行操作。实现业务执行方法与熔断机制的解耦。
 *
 * 要特别注意：自定义的 FallbackFactory 实现类不能放在 @ComponentScan 扫描的当前包及其子包下（即主启动类所在的包及其子包）。否
 * 则此自定义配置类就会被所有的 FeignClient 客户端所共享，也就是说这就达不到特殊化定制的目的了。
 */
// 这个注解必须有，并且很重要。
@Component
public class HystrixFallback implements FallbackFactory<DeptClientService> {

    @Override
    public DeptClientService create(Throwable throwable) {
        return new DeptClientService() {

            @Override
            public Department findById(Integer id) {
                // 注意，在使用 lombok 时，即要导入 jar 包，也要安装其插件，并且还要设置 IDEA 开启 annotation。
                return new Department().setName("异常信息！熔断！");
            }

            @Override
            public List<Department> findall() {
                return null;
            }

            @Override
            public boolean save(Department department) {
                return false;
            }

            @Override
            public Object discovery() {
                return null;
            }
        };
    }
}
