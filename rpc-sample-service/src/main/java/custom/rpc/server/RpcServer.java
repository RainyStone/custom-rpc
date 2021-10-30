package custom.rpc.sample.service.server;

import custom.rpc.common.bean.RpcRequest;
import custom.rpc.common.bean.RpcResponse;
import custom.rpc.common.codec.RpcDecoder;
import custom.rpc.common.codec.RpcEncoder;
import custom.rpc.common.util.StringUtil;
import custom.rpc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
//RPC服务器，用于发布服务，供客户端调用
//ApplicationContextAware接口，便于直接获取spring配置文件中所有引用到的bean对象
//InitializingBean接口，为bean提供了初始化方法的方式，它只包括afterPropertiesSet方法，凡是继承该接口的类，在初始化bean的时候都会执行该方法。
public class RpcServer implements ApplicationContextAware,InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

    private String serviceAddress;

    private ServiceRegistry serviceRegistry;

    //存放 服务名 与 服务对象 之间的映射关系
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcServer(String serviceAddress, ServiceRegistry serviceRegistry) {
        this.serviceAddress = serviceAddress;
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        //扫描带有RpcService注解的类并初始化handlerMap对象
        Map<String,Object> serviceBeanMap=ctx.getBeansWithAnnotation(RpcService.class);
        if(MapUtils.isNotEmpty(serviceBeanMap)){
            for(Object serviceBean:serviceBeanMap.values()){
                RpcService rpcService=serviceBean.getClass().getAnnotation(RpcService.class);
                String serviceName=rpcService.value().getName();//服务接口名
                String serviceVersion= rpcService.version();
                if(StringUtil.isNotEmpty(serviceVersion)){
                    serviceName+="-"+serviceBean;
                }
                handlerMap.put(serviceName,serviceBean);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        try {
            //创建并初始化Netty服务端Bootstrap对象
            ServerBootstrap bootstrap=new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline=channel.pipeline();
                    pipeline.addLast(new RpcDecoder(RpcRequest.class));//解码RPC请求
                    pipeline.addLast(new RpcEncoder(RpcResponse.class));//编码RPC响应
                    pipeline.addLast(new RpcServerHandler(handlerMap));//处理RPC请求
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG,1024);
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);
            //获取RPC服务器的IP地址与端口号
            String[] addressArray=StringUtil.split(serviceAddress, ":");
            String ip=addressArray[0];
            int port=Integer.parseInt(addressArray[1]);
            //启动RPC服务器
            ChannelFuture future=bootstrap.bind(ip,port).sync();
            //注册RPC服务地址
            if(serviceRegistry!=null){
                for (String interfaceName : handlerMap.keySet()) {
                    serviceRegistry.register(interfaceName, serviceAddress);
                    LOGGER.debug("register service: {} => {}", interfaceName, serviceAddress);
                }
            }
            LOGGER.debug("server started on port {}", port);
            // 关闭 RPC 服务器
            future.channel().closeFuture().sync();
        }finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
