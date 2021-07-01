package com.wy.model.facebook;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 主页集合
 *
 * @author 飞花梦影
 * @date 2021-07-01 20:23:04
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FacebookBriefs {

	private List<FacebookBrief> facebookBriefs;
}