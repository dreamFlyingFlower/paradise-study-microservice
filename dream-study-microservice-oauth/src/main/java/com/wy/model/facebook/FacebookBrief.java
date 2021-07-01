package com.wy.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * facebook个人主页信息实体类
 *
 * @author 飞花梦影
 * @date 2021-07-01 20:21:18
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacebookBrief {

	private String id;

	private String story;

	private String message;
}