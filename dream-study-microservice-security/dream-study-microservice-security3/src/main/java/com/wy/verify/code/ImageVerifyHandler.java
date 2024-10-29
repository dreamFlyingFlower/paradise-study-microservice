package com.wy.verify.code;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.verify.AbstractVerify;
import com.wy.verify.VerifyEntity;

import dream.flying.flower.result.ResultException;

/**
 * 图片验证码处理,直接写到相应中即可
 * 
 * @auther 飞花梦影
 * @date 2019-09-24 23:23:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Component
public class ImageVerifyHandler extends AbstractVerify<ImageVerifyEntity> {

	@Override
	protected void handler(ServletWebRequest request, VerifyEntity entity) {
		try {
			ImageIO.write(((ImageVerifyEntity) entity).getBufferedImage(), "JPEG",
					request.getResponse().getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ResultException("验证码生成失败,请重试");
		}
	}
}