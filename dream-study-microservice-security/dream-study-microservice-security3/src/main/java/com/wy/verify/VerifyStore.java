package com.wy.verify;

import org.springframework.web.context.request.ServletWebRequest;

/**
 * 生成验证码之后将验证码存入到session或redis或其他方式.手机和pc端处理方式不一样,手机端没有cookie
 * 
 * @auther 飞花梦影
 * @date 2019-09-29 00:02:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public interface VerifyStore {

	/**
	 * 来源类型
	 * 
	 * @return 类型,pc或mobile或自定义其他
	 */
	String requestSource();

	/**
	 * 生成存储的key
	 * 
	 * @return 生成的key
	 */
	String generateKey(ServletWebRequest webRequest);

	/**
	 * 存储
	 * 
	 * @param webRequest 请求和响应
	 * @param value 需要进行存储的值
	 */
	void store(ServletWebRequest webRequest, Object value);

	/**
	 * 获得存储的值
	 * 
	 * @param webRequest 请求和响应
	 * @return 值
	 */
	Object getStore(ServletWebRequest webRequest);

	/**
	 * 删除存储的值
	 * 
	 * @param webRequest 请求和响应
	 */
	void removeStore(ServletWebRequest webRequest);
}