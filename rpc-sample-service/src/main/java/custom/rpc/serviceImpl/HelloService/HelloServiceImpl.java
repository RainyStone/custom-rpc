package custom.rpc.sample.service.serviceImpl.HelloService;

import custom.rpc.sample.api.HelloService;
import custom.rpc.sample.api.Person;
import custom.rpc.sample.service.server.RpcService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();
    }
}
