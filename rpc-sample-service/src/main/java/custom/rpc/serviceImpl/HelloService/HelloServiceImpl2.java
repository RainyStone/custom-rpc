package custom.rpc.sample.service.serviceImpl.HelloService;

import custom.rpc.sample.service.server.RpcService;
import custom.rpc.sample.api.HelloService;
import custom.rpc.sample.api.Person;

@RpcService(value = HelloService.class, version = "sample.hello2")
public class HelloServiceImpl2 implements HelloService {

    @Override
    public String hello(String name) {
        return "你好! " + name;
    }

    @Override
    public String hello(Person person) {
        return "你好! " + person.getFirstName() + " " + person.getLastName();
    }
}
