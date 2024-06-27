package com.wy.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * @apiNote
 * @author ParadiseWY
 * @date 2019年9月23日
 */
@Getter
@Setter
public class UserQq extends SocialUser {
	private Integer ret;// 返回码,0正确

	private String msg;// 如果ret<0,会有相应的错误信息提示,返回数据全部用UTF-8编码

	private String nickname;// 用户在QQ空间的昵称

	private String figureurl;// 大小为30×30像素的QQ空间头像URL

	private String figureurl_1;// 大小为50×50像素的QQ空间头像URL

	private String figureurl_2;// 大小为100×100像素的QQ空间头像URL

	private String figureurl_qq_1;// 40×40的QQ头像URL

	private String figureurl_qq_2;// 100×100的QQ头像URL.不是所有用户都有100x100的头像,但40x40一定有

	private String gender;// 性别,如果获取不到则默认返回"男"

	private String is_yellow_vip;// 是否为黄钻用户,0不是,1是

	private String vip;// 是否为vip用户,0不是,1是

	private String yellow_vip_level;// 黄钻等级

	private String level;// vip等级

	private String is_yellow_year_vip;// 黄钻年限

	private String openId;
}