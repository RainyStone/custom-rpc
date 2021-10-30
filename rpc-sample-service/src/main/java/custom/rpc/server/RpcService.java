package custom.rpc.sample.service.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//服务注解，标注在服务实现类上
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {
    //实现类实现的接口
    Class<?> value();
    //服务版本号，用于区分同一个服务接口的不同实现类
    String version() default "";
}
