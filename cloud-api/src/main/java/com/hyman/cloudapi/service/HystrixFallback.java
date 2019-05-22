package com.hyman.cloudapi.service;

import com.hyman.cloudapi.entity.Department;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 使用实现 FallbackFactory 的方式统一对 service 接口层的熔断机制进行处理，从而避免对方法使用 @HystrixCommand 强耦合的方式进
 * 行操作。实现业务执行方法与熔断机制的解耦。
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
