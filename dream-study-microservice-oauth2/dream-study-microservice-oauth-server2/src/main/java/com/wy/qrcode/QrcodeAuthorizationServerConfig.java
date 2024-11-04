package com.wy.qrcode;

/**
 * 二维码授权登录
 * 
 * <pre>
 * 1.打开网页，发起授权申请/未登录被重定向到登录页面
 * 2.选择二维码登录，页面从后端请求二维码
 * 3.页面渲染二维码图片，并轮询请求，获取二维码的状态
 * 4.事先登录过APP的手机扫描二维码，然后APP请求服务器端的API接口，把用户认证信息传递到服务器中
 * 5.后端收到APP的请求后更改二维码状态，并把用户认证信息写入session
 * 6.页面得到扫码确认的响应，并跳转回之前未登录的地址
 * 
 * 在这个流程中用户认证信息写入session后前端在重定向时能获取到认证信息是因为现在的认证服务引入了spring session data redis,
 * 并且在application.yml中配置了server.servlet.session.cookie.domain: cdhttp.cn属性(演示环境域名),
 * 这里是指定spring session的顶级域名,在该域名下的子域名服务共享session,如果是在开发阶段可以配置为server.servlet.session.cookie.domain: 127.0.0.1,
 * 这时候前端访问使用127.0.0.1:5173,认证服务使用127.0.0.1:8080,端口可以不一样,但是认证服务和前端必须使用同一域名
 * </pre>
 *
 * @author 飞花梦影
 * @date 2024-11-04 14:18:58
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QrcodeAuthorizationServerConfig {

}