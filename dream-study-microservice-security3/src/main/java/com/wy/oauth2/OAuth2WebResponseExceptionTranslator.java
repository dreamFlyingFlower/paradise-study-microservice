package com.wy.oauth2;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.DefaultThrowableAnalyzer;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.web.util.ThrowableAnalyzer;
import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * 自定义异常处理器
 *
 * @author 飞花梦影
 * @date 2022-06-17 17:43:08
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public class OAuth2WebResponseExceptionTranslator implements WebResponseExceptionTranslator<OAuth2Exception> {

	private ThrowableAnalyzer throwableAnalyzer = new DefaultThrowableAnalyzer();

	@Override
	public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
		Throwable[] causeChain = this.throwableAnalyzer.determineCauseChain(e);
		Exception ase =
				(OAuth2Exception) this.throwableAnalyzer.getFirstThrowableOfType(OAuth2Exception.class, causeChain);
		if (ase != null) {
			return this.handleOAuth2Exception((OAuth2Exception) ase);
		}
		ase = (AuthenticationException) this.throwableAnalyzer.getFirstThrowableOfType(AuthenticationException.class,
				causeChain);
		if (ase != null) {
			return this.handleOAuth2Exception(new UnauthorizedException(e.getMessage(), e));
		}
		ase = (AccessDeniedException) this.throwableAnalyzer.getFirstThrowableOfType(AccessDeniedException.class,
				causeChain);
		if (ase instanceof AccessDeniedException) {
			return this.handleOAuth2Exception(new ForbiddenException(ase.getMessage(), ase));
		}

		ase = (HttpRequestMethodNotSupportedException) this.throwableAnalyzer
				.getFirstThrowableOfType(HttpRequestMethodNotSupportedException.class, causeChain);
		return ase instanceof HttpRequestMethodNotSupportedException
				? this.handleOAuth2Exception(new MethodNotAllowed(ase.getMessage(), ase))
				: this.handleOAuth2Exception(
						new ServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e));

	}

	private ResponseEntity<OAuth2Exception> handleOAuth2Exception(OAuth2Exception e) throws IOException {
		int status = e.getHttpErrorCode();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		if (status == HttpStatus.UNAUTHORIZED.value() || e instanceof InsufficientScopeException) {
			headers.set("WWW-Authenticate", String.format("%s %s", "Bearer", e.getSummary()));
		}
		// OAuth2Exception 转换restResponse过程
		RestOAuth2Exception restOAuth2Exception = new RestOAuth2Exception(e.getMessage(), e);

		ResponseEntity<OAuth2Exception> response =
				new ResponseEntity<>(restOAuth2Exception, headers, HttpStatus.valueOf(status));
		return response;
	}

	public void setThrowableAnalyzer(ThrowableAnalyzer throwableAnalyzer) {
		this.throwableAnalyzer = throwableAnalyzer;
	}

	private static class MethodNotAllowed extends OAuth2Exception {

		private static final long serialVersionUID = -6452596643306509989L;

		public MethodNotAllowed(String msg, Throwable t) {
			super(msg, t);
		}

		@Override
		public String getOAuth2ErrorCode() {
			return "method_not_allowed";
		}

		@Override
		public int getHttpErrorCode() {
			return 405;
		}
	}

	private static class UnauthorizedException extends OAuth2Exception {

		private static final long serialVersionUID = 555334648584786119L;

		public UnauthorizedException(String msg, Throwable t) {
			super(msg, t);
		}

		@Override
		public String getOAuth2ErrorCode() {
			return "unauthorized";
		}

		@Override
		public int getHttpErrorCode() {
			return 401;
		}
	}

	private static class ServerErrorException extends OAuth2Exception {

		private static final long serialVersionUID = -4929552195161161221L;

		public ServerErrorException(String msg, Throwable t) {
			super(msg, t);
		}

		@Override
		public String getOAuth2ErrorCode() {
			return "server_error";
		}

		@Override
		public int getHttpErrorCode() {
			return 500;
		}
	}

	private static class ForbiddenException extends OAuth2Exception {

		private static final long serialVersionUID = -307866925016081902L;

		public ForbiddenException(String msg, Throwable t) {
			super(msg, t);
		}

		@Override
		public String getOAuth2ErrorCode() {
			return "access_denied";
		}

		@Override
		public int getHttpErrorCode() {
			return 403;
		}
	}
}