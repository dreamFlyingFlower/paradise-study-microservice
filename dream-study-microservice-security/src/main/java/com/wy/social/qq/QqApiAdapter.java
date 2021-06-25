package com.wy.social.qq;

import org.springframework.social.connect.ApiAdapter;
import org.springframework.social.connect.ConnectionValues;
import org.springframework.social.connect.UserProfile;

import com.wy.entity.UserQq;
import com.wy.social.IToken;

/**
 * spring提供的适配器,构建参数连接服务器
 * 
 * @auther 飞花梦影
 * @date 2019-09-23 23:59:28
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QqApiAdapter implements ApiAdapter<IToken<UserQq>> {

	/**
	 * qq服务是否可用,默认一直都是可用的
	 */
	@Override
	public boolean test(IToken<UserQq> api) {
		return true;
	}

	/**
	 * 从api中构建连接参数设置到ConnectionValues中
	 */
	@Override
	public void setConnectionValues(IToken<UserQq> api, ConnectionValues values) {
		UserQq userInfo = api.getUserInfo();
		// 设置用户名
		values.setDisplayName(userInfo.getNickname());
		// 设置头像
		values.setImageUrl(userInfo.getFigureurl_1());
		// 设置个人主页
		values.setProfileUrl(null);
		// 用户在服务提供商里的唯一标识
		values.setProviderUserId(userInfo.getOpenId());
	}

	/**
	 * 获取个人主页信息
	 * 
	 * @param api token信息等
	 * @return
	 */
	@Override
	public UserProfile fetchUserProfile(IToken<UserQq> api) {
		return null;
	}

	/**
	 * 更新个人主页上的信息
	 * 
	 * @param api token信息
	 * @param message 需要进行更新的信息
	 */
	@Override
	public void updateStatus(IToken<UserQq> api, String message) {
	}
}