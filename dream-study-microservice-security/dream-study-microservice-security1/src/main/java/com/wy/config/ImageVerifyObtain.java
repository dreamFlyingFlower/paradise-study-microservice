package com.wy.config;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.verify.VerifyEntity;
import com.wy.verify.VerifyGenerator;
import com.wy.verify.code.ImageVerifyEntity;

/**
 * 自定义验证码生成器,需要配置验证器的相关参数,如name,conditiononmissbean等
 * 
 * @author 飞花梦影
 * @date 2019-09-23 09:40:56
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Configuration
@ConditionalOnMissingBean(name = "imageVerifyGenerator")
public class ImageVerifyObtain {

	@Bean
	VerifyGenerator imageVerifyGenerator() {
		return (request) -> {
			return generateVerify(request);
		};
	}

	public VerifyEntity generateVerify(ServletWebRequest request) {
		int width = ServletRequestUtils.getIntParameter(request.getRequest(), "width", 150);
		int height = ServletRequestUtils.getIntParameter(request.getRequest(), "height", 20);
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		Random random = new Random();
		g.setColor(getRandColor(200, 250));
		g.fillRect(0, 0, width, height);
		g.setFont(new Font("Times New Roman", Font.ITALIC, 20));
		g.setColor(getRandColor(160, 200));
		for (int i = 0; i < 155; i++) {
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int xl = random.nextInt(12);
			int yl = random.nextInt(12);
			g.drawLine(x, y, x + xl, y + yl);
		}
		String sRand = "";
		for (int i = 0; i < 8; i++) {
			String rand = String.valueOf(random.nextInt(10));
			sRand += rand;
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
			g.drawString(rand, 13 * i + 6, 16);
		}
		g.dispose();
		return new ImageVerifyEntity(image, sRand, 1000);
	}

	private Color getRandColor(int fc, int bc) {
		Random random = new Random();
		if (fc > 255) {
			fc = 255;
		}
		if (bc > 255) {
			bc = 255;
		}
		int r = fc + random.nextInt(bc - fc);
		int g = fc + random.nextInt(bc - fc);
		int b = fc + random.nextInt(bc - fc);
		return new Color(r, g, b);
	}
}