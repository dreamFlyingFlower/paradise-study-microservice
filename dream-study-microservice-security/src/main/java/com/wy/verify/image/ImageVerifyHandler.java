package com.wy.verify.image;

import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.result.ResultException;
import com.wy.verify.AbstractVerify;
import com.wy.verify.VerifyEntity;

/**
 * @apiNote 图片验证码处理
 * @author ParadiseWY
 * @date 2019年9月24日
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