package com.wy.filter;

import java.util.StringJoiner;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import com.alibaba.fastjson.JSONException;
import com.wy.enums.TipFormatEnum;
import com.wy.result.Result;

import lombok.extern.slf4j.Slf4j;

/**
 * 异常统一处理拦截器
 * 
 * @auther 飞花梦影
 * @date 2019-02-21 11:26:14
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestControllerAdvice
@Slf4j
public class ExceptionFilter {

	@ExceptionHandler(Throwable.class)
	public Result<?> handleException(HttpServletRequest request, Throwable throwable) {
		throwable.printStackTrace();
		log.error("主机Host:{},请求URL:{},异常原因:{}", request.getRemoteHost(), request.getRequestURL(),
				throwable.getMessage());
		// http请求中断
		if (throwable instanceof ClientAbortException) {
			return Result.error("客户端中断了请求");
		}
		// 接口不存在
		if (throwable instanceof NoHandlerFoundException) {
			return Result.error(-404, throwable.getMessage());
		}
		// http请求方式不支持
		if (throwable instanceof HttpRequestMethodNotSupportedException) {
			return Result.error(-500, throwable.getMessage());
		}
		if (throwable instanceof JSONException) {
			return Result.error("数据结构错误:" + throwable.getMessage());
		}
		// 参数绑定错误
		if (throwable instanceof BindException) {
			StringJoiner stringJoiner = new StringJoiner(";");
			// 解析原错误信息,封装后返回,此处返回非法的字段名称,原始值,错误信息
			for (FieldError error : ((BindException) throwable).getFieldErrors()) {
				stringJoiner.add(error.getDefaultMessage());
			}
			return Result.error(stringJoiner.toString());
		}
		// 实体类字段序列化异常
		if (throwable instanceof ConstraintViolationException) {
			return Result.error("实体类字段序列化异常:" + throwable.getMessage());
		}
		// 无法解析参数
		if (throwable instanceof HttpMessageNotReadableException) {
			return Result.error("参数无法正常解析:" + throwable.getMessage());
		}
		// 参数不合法
		if (throwable instanceof IllegalArgumentException) {
			return Result.error(throwable.getMessage());
		}
		// 必传参数为空
		if (throwable instanceof MissingServletRequestParameterException) {
			return Result.error(TipFormatEnum.TIP_PARAM_EMPTY
					.getMsg(((MissingServletRequestParameterException) throwable).getParameterName()));
		}
		// 方法参数验证失败
		if (throwable instanceof MethodArgumentNotValidException) {
			StringBuilder sb = new StringBuilder();
			BindingResult bindingResult = ((MethodArgumentNotValidException) throwable).getBindingResult();
			// 解析原错误信息,封装后返回,此处返回非法的字段名称,原始值,错误信息
			for (FieldError error : bindingResult.getFieldErrors()) {
				sb.append("字段:" + error.getField() + "-" + error.getRejectedValue() + ";");
			}
			return Result.error(sb.toString());
		}
		if (throwable instanceof MaxUploadSizeExceededException) {
			return Result.error("上传文件超大,最大支持:" + ((MaxUploadSizeExceededException) throwable).getMaxUploadSize());
		}
		if (throwable instanceof MultipartException) {
			return Result.error("当前网络环境较差,文件上传失败:" + throwable.getMessage());
		}
		// 数据库主键重复或unique字段重复值插入或更新
		if (throwable instanceof DuplicateKeyException) {
			return Result.error(throwable.getMessage());
		}
		return Result.error(throwable.getMessage());
	}

	/*-----------------需要springboot的spring-boot-starter-security包 -----------------*/
	/**
	 * 权限访问异常,
	 */
	// @ExceptionHandler(value = AuthenticationException.class)
	// public Object
	// AuthenticationExceptionHandler(AuthenticationException e)
	// {
	// return Result.resultErr(-500, "没有访问权限");
	// }

	/**
	 * 方法访问权限不足异常
	 */
	// @ExceptionHandler(AccessDeniedException.class)
	// public Object
	// AccessDeniedExceptionHandler(AccessDeniedException
	// exception)
	// throws Exception {
	// return Result.resultErr(-500, "没有访问权限");
	// }

	/**
	 * 非正常的权限访问异常
	 */
	// @ExceptionHandler(value = BadCredentialsException.class)
	// public Object
	// BadCredentialsExceptionHandler(BadCredentialsException e)
	// throws Exception {
	// return Result.resultErr(-500, "请求验证异常");
	// }
	/*-----------------需要springboot的spring-boot-starter-security包 -----------------*/
}