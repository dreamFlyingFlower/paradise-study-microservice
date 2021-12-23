package com.wy.rabbitmq.netty;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * RabbitMQ Netty消费者处理类
 * 
 * @author 飞花梦影
 * @date 2021-12-23 15:33:47
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class RabbitNettyConsumerHandler extends SimpleChannelInboundHandler<ByteBuf> {

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		JSONObject data = new JSONObject();
		data.put("type", "consumer");
		// 生产发送数据
		byte[] req = data.toJSONString().getBytes();
		ByteBuf firstMSG = Unpooled.buffer(req.length);
		firstMSG.writeBytes(req);
		ctx.writeAndFlush(firstMSG);
	}

	/**
	 * 客户端读取到服务器端数据
	 *
	 * @param ctx
	 * @param msg
	 * @throws Exception
	 */
	@Override
	public void channelRead0(ChannelHandlerContext ctx, ByteBuf buf) throws Exception {
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		System.out.println("客户端接收到服务器端请求:" + body);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}