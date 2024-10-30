package com.wy.qrcode;

/**
 * 二维码登陆,流程如下:
 * 
 * <pre>
 * 1.用户点击二维码登陆,调用后台接口生成二维码(带参数key), 返回二维码链接、key到页面
 * 2.页面显示二维码,提示扫码,并通过此key建立websocket
 * 3.用户扫码,获取参数key,点击登陆调用后台并传递key
 * 4.后台根据微信端用户登陆状态拿到userdetail, 并在缓存中维护 key: userDetail 关联关系
 * 5.后台根据websocket:key通知对于前台页面登陆
 * 6.页面用此key登陆
 * 7.用户通过key登陆就是本文的二维码扫码登陆部分,实际过程中注意二维码超时,redis超时等处理
 * </pre>
 *
 * @author 飞花梦影
 * @date 2024-10-30 10:38:04
 * @git {@link https://github.com/dreamFlyingFlower}
 */
public class QrcodeConfig {

}
