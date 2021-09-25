package com.wy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wy.producer.MessageProcuder;

@Service
public class MessageService {

	@Autowired
	private MessageProcuder messageProducer;

	public void sendMessage(String msg) {
		System.out.println("--------业务处理-------");
		messageProducer.send(msg);
	}
}