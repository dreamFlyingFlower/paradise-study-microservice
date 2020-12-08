package com.wy.verify.image;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

import com.wy.verify.VerifyEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote 验证码实体类
 * @author ParadiseWY
 * @date 2019年9月23日
 */
@Getter
@Setter
public class ImageVerifyEntity extends VerifyEntity {

	private static final long serialVersionUID = 1L;

	private BufferedImage bufferedImage;

	/**
	 * 构造函数
	 * @param bi 图形数据
	 * @param code 验证码
	 * @param seconds 多少秒之后过期
	 */
	public ImageVerifyEntity(BufferedImage bufferedImage, String code, int seconds) {
		super(code, seconds);
		this.bufferedImage = bufferedImage;
	}

	public ImageVerifyEntity(BufferedImage bufferedImage, String code, LocalDateTime expireTime) {
		super(code, expireTime);
		this.bufferedImage = bufferedImage;
	}
}