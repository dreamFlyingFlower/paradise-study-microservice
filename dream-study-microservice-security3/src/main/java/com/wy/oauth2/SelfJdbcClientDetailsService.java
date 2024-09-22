//package com.wy.oauth2;
//
//import javax.sql.DataSource;
//
//import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
//
///**
// * 自定义数据库处理OAuth2数据
// * 
// * @author 飞花梦影
// * @date 2022-09-13 16:18:07
// * @git {@link https://github.com/dreamFlyingFlower }
// */
//public class SelfJdbcClientDetailsService extends JdbcClientDetailsService {
//
//	private static final String SELECT_CLIENT_DETAILS_SQL =
//			"select client_id, client_secret, resource_ids, scope, authorized_grant_types, web_server_redirect_uri, "
//					+ "authorities, access_token_validity, refresh_token_validity, additional_information, autoapprove "
//					+ "from oauth_client_details where client_id = ? and archived = 0 ";
//
//	public SelfJdbcClientDetailsService(DataSource dataSource) {
//		super(dataSource);
//		setSelectClientDetailsSql(SELECT_CLIENT_DETAILS_SQL);
//	}
//}