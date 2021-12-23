package com.wy.rabbitmq.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * RabbitMQ消息生产者
 * 
 * @author 飞花梦影
 * @date 2021-12-23 15:23:30
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class RabbitNettyProducer {

	public static void main(String[] args) {
		int port = 9008;
		RabbitNettyProducer client = new RabbitNettyProducer();
		try {
			client.connect(port, "127.0.0.1");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void connect(int port, String host) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap client = new Bootstrap();
		try {
			client.group(group).channel(NioSocketChannel.class)
					/**
					 * ChannelOption.TCP_NODELAY:禁止使用Nagle算法,对应于套接字选项中的TCP_NODELAY
					 * 
					 * Nagle:将小数据包组装为大数据包发送,而不是输入一次发送一次.该算法提高了网络的有效负载,但是造成了延时
					 * 
					 * TCP_CORK:和TCP_NODELAY相对应,是需要等到发送的数据量最大的时候, 一次性发送数据,适用于文件传输
					 */
					.option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new RabbitNettyProducerHandler());
							// 演示LineBasedFrameDecoder编码器
							// ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
							// ch.pipeline().addLast(new StringDecoder());
						}
					});
			ChannelFuture future = client.connect(host, port).sync();
			future.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
}