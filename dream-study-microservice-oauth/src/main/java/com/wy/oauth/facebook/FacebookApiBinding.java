package com.wy.oauth.facebook;

import java.util.List;

import com.wy.model.facebook.FacebookBrief;
import com.wy.model.facebook.FacebookBriefs;
import com.wy.model.facebook.ProfileFacebook;
import com.wy.oauth.ApiBinding;

/**
 * 向Facebook发送请求获得用户的信息
 *
 * @author 飞花梦影
 * @date 2021-07-01 20:11:52
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class FacebookApiBinding extends ApiBinding {

	private static final String GRAPH_API_BASE_URL = "https://graph.facebook.com/v2.12";

	public FacebookApiBinding(String accessToken) {
		super(accessToken);
	}

	public ProfileFacebook getProfile() {
		return restTemplate.getForObject(GRAPH_API_BASE_URL + "/me", ProfileFacebook.class);
	}

	public List<FacebookBrief> getFacebookBriefs() {
		return restTemplate.getForObject(GRAPH_API_BASE_URL + "/me/feed", FacebookBriefs.class).getFacebookBriefs();
	}
}