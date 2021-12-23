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
 * RabbitMQ Netty消费者
 * 
 * @author 飞花梦影
 * @date 2021-12-23 15:35:45
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class RabbitNettyConsumer {

	public static void main(String[] args) {
		int port = 9008;
		RabbitNettyConsumer client = new RabbitNettyConsumer();
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
			client.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true)
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new RabbitNettyConsumerHandler());
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