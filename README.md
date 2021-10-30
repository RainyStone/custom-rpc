主要参考  https://my.oschina.net/huangyong/blog/361751 写的简易rpc框架，基于Spring+Netty+Protostuff+ZooKeeper

一、rpc-sample-api模块：服务接口，打成单独的一个包，在进行服务端与客户端开发时，需要将该包作为依赖导入

>1、定义服务接口，如HelloService 

二、rpc-sample-service模块：rpc服务端

>1、custom.rpc.serviceImpl：存放服务接口的实现类，如HelloServiceImpl、HelloServiceImpl2

>2、custom.rpc.server：存放服务端相关代码，比如服务端启动代码等

>>a、RpcService注解：注解到服务接口的实现类上，说明该类是一个远程接口的实现

>>b、RpcServer：基于Netty实现的RPC服务器，用于发布服务，供客户端调用

>>c、RpcServerHandler：处理RPC请求

>>d、RpcBootstrap：引导程序，加载Spring配置文件spring.xml并启动服务器RpcServer

>3、resources：存放服务端相关配置文件

>>a、log4j.properties：服务端log4j配置文件

>>b、rpc.properties：服务端配置文件

>>c、spring.xml：服务端spring配置文件

三、rpc-registry模块：rpc服务注册中心，用来注册与发现服务

>1、custom.rpc.registry：服务注册与发现接口

>2、custom.rpc.registry.zookeeper：zookeeper实现服务注册与发现

四、rpc-common模块：公共组件模块

>1、custom.rpc.common.util：工具包

>2、custom.rpc.common.bean：rpc请求与响应的封装类

>3、custom.rpc.common.codec：rpc编解码器

五、rpc-sample-client模块：rpc客户端

>1、custom.rpc.client：

>>a、RpcClient：RPC客户端，用于发送RPC请求

>>b、RpcProxy：RPC代理，用于创建RPC服务代理

>2、custom.rpc.clientExample：

>>a、客户端调用服务端rpc服务的例子

>3、resources：存放客户端相关配置文件

>>a、log4j.properties：客户端端log4j配置文件

>>b、rpc.properties：客户端配置文件

>>c、spring.xml：客户端spring配置文件
