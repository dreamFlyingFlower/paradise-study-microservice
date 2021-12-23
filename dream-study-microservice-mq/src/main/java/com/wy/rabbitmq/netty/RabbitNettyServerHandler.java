package com.wy.rabbitmq.netty;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * RabbitMQ服务器处理方法
 *
 * @author 飞花梦影
 * @date 2021-12-23 15:16:22
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class RabbitNettyServerHandler extends SimpleChannelInboundHandler<Object> {

	private static final String TYPE_CONSUMER = "consumer";

	private static final String TYPE_PRODUCER = "producer";

	private static LinkedBlockingDeque<String> msgs = new LinkedBlockingDeque<>();

	private static List<ChannelHandlerContext> ctxs = new ArrayList<>();

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object data) throws Exception {
		JSONObject clientMsg = getData(data);
		String type = clientMsg.getString("type");
		switch (type) {
		case TYPE_PRODUCER:
			producer(clientMsg);
			break;
		case TYPE_CONSUMER:
			consumer(ctx);
			break;
		}
	}

	private void consumer(ChannelHandlerContext ctx) {
		// 保存消费者连接
		ctxs.add(ctx);
		// 主动拉取mq服务器端缓存中没有被消费的消息
		String data = msgs.poll();
		if (StringUtils.isEmpty(data)) {
			return;
		}
		// 将该消息发送给消费者
		byte[] req = data.getBytes();
		ByteBuf firstMSG = Unpooled.buffer(req.length);
		firstMSG.writeBytes(req);
		ctx.writeAndFlush(firstMSG);
	}

	private void producer(JSONObject clientMsg) {
		// 缓存生产者投递消息
		String msg = clientMsg.getString("msg");
		msgs.offer(msg);
		// 需要将该消息推送消费者
		ctxs.forEach((ctx) -> {
			// 将该消息发送给消费者
			String data = msgs.poll();
			if (data == null) {
				return;
			}
			byte[] req = data.getBytes();
			ByteBuf firstMSG = Unpooled.buffer(req.length);
			firstMSG.writeBytes(req);
			ctx.writeAndFlush(firstMSG);
		});
	}

	private JSONObject getData(Object data) throws UnsupportedEncodingException {
		ByteBuf buf = (ByteBuf) data;
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String body = new String(req, "UTF-8");
		return JSONObject.parseObject(body);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}
}