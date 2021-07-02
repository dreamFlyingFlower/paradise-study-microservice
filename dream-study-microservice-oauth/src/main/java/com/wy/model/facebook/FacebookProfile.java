package com.wy.model.facebook;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * facebook用户个人信息实体类
 *
 * @author 飞花梦影
 * @date 2021-07-01 20:19:19
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacebookProfile {

	private String id;

	private String name;
}