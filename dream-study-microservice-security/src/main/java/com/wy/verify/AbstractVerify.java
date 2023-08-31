package com.wy.verify;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;

import com.wy.common.AuthException;
import com.wy.enums.VerifyType;
import com.wy.lang.StrHelper;

/**
 * 验证码校验主要类,可以使用短信和验证码
 * 
 * @auther 飞花梦影
 * @date 2021-06-22 23:25:32
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public abstract class AbstractVerify<C extends VerifyEntity> implements VerifyHandler {

	@Autowired
	private VerifyHandlerFactory verifyHandlerFactory;

	/**
	 * 系统所有 {@link VerifyGenerator} 的实现
	 */
	@Autowired
	private Map<String, VerifyGenerator> verifyGenerators;

	/**
	 * 系统所有{@link VerifyStore}的实现
	 */
	@Autowired
	private Map<String, VerifyStore> verifyStores;

	/**
	 * 执行验证程序
	 */
	@Override
	public void generate(ServletWebRequest servletWebRequest, String type) {
		boolean check = VerifyType.check(type);
		if (!check) {
			throw new AuthException("验证码类型错误");
		}
		// 生成验证码
		VerifyEntity entity = generateVerify(servletWebRequest, type);
		// 将验证码存入到session中
		save(servletWebRequest, entity);
		// 将验证码进行处理,发送到页面或手机或其他自定义处理
		handler(servletWebRequest, entity);
	}

	/**
	 * 生成校验码
	 * 
	 * @param request
	 * @return
	 */
	private VerifyEntity generateVerify(ServletWebRequest request, String type) {
		String obtainVerify = verifyHandlerFactory.getVerifyInfo(type).getObtainVerify();
		VerifyGenerator verifyGenerator = verifyGenerators.get(obtainVerify);
		if (verifyGenerator == null) {
			throw new AuthException("验证码生成器" + obtainVerify + "不存在");
		}
		return verifyGenerator.generateVerify(request);
	}

	/**
	 * 请求头中必须带上请求来源,pc或mobile
	 * 
	 * @param webRequest 请求和响应
	 * @return 存储类型
	 */
	private VerifyStore getVerifyStore(ServletWebRequest webRequest) {
		String requestSource = webRequest.getRequest().getHeader("requestSource");
		if (StrHelper.isBlank(requestSource)) {
			requestSource = webRequest.getRequest().getParameter("requestSource");
			if (StrHelper.isBlank(requestSource)) {
				throw new AuthException("请求中缺少requestSource参数");
			}
		}
		for (Map.Entry<String, VerifyStore> entry : verifyStores.entrySet()) {
			if (Objects.equals(entry.getValue().requestSource(), requestSource)) {
				return entry.getValue();
			}
		}
		throw new AuthException("未知的请求来源");
	}

	/**
	 * 保存校验码
	 * 
	 * @param request 请求头
	 * @param verifyEntity 此处不可直接存入verifyEntity,因为bufferedimage没有序列化,开启redis之后会报错
	 */
	private void save(ServletWebRequest request, VerifyEntity verifyEntity) {
		VerifyEntity entity = new VerifyEntity(verifyEntity.getVerityCode(), verifyEntity.getExpireTime());
		getVerifyStore(request).store(request, entity);
	}

	@Override
	public boolean verify(ServletWebRequest request, String type) {
		VerifyInfo verifyInfo = verifyHandlerFactory.getVerifyInfo(type);
		Object sessionVerifyEntity = getVerifyStore(request).getStore(request);
		if (Objects.isNull(sessionVerifyEntity)) {
			throw new AuthException("验证码不存在");
		}
		VerifyEntity verifyEntity = (VerifyEntity) sessionVerifyEntity;
		String requestCode;
		try {
			requestCode = ServletRequestUtils.getStringParameter(request.getRequest(), verifyInfo.getVerifyType());
		} catch (ServletRequestBindingException e) {
			throw new AuthException("获取验证码的值失败");
		}
		if (StrHelper.isBlank(requestCode)) {
			throw new AuthException("验证码的值不能为空");
		}
		if (verifyEntity.isExpried()) {
			getVerifyStore(request).removeStore(request);
			throw new AuthException("验证码已过期");
		}
		if (!Objects.equals(verifyEntity.getVerityCode(), requestCode)) {
			throw new AuthException("验证码不匹配");
		}
		getVerifyStore(request).removeStore(request);
		return true;
	}

	/**
	 * 发送校验码,由子类实现
	 * 
	 * @param request 请求和响应
	 * @param entity 验证实体对象
	 */
	protected abstract void handler(ServletWebRequest request, VerifyEntity entity);
}