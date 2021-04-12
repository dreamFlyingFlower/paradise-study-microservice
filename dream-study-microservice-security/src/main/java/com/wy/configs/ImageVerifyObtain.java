package com.wy.configs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.verify.VerifyEntity;
import com.wy.verify.VerifyGenerator;
import com.wy.verify.image.ImageVerifyEntity;

/**
 * @apiNote 自定义更高级的验证码生成器,需要配置验证器的相关参数,如name,conditiononmissbean等
 * @apiNote 当spring扫包的时候,若是先扫到当前类,那么就会直接将该类实例加入到上下文中,
 *          如本类和{@link com.wy.verify.VerifyConfig#imageVerifyGenerator}中的同名方法,
 *          若是先扫本类,会先加载本类实例到上下文中,再继续扫到VerifyConfig类,
 *          由于VerifyConfig中的同名方法有@ConditionalOnMissingBean注解,而且上下文中已经有了实例,将不再加载.
 *          但是若是先扫了VerifyConfig的同名方法,那么就会先加载VerifyConfig的到上下文中,再继续扫到本类,
 *          由于上下文中已经有该类的实例,而本类方法上又没有@ConditionalOnMissingBean注解,
 *          则spring将会再次实例化本类,则上下文环境中将会出现同名方法,此时程序会报错
 * @author ParadiseWY
 * @date 2019年9月23日
 */
@Configuration
public class ImageVerifyObtain {

	@Bean
	public VerifyGenerator imageVerifyGenerator() {
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
			g.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110),
					20 + random.nextInt(110)));
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