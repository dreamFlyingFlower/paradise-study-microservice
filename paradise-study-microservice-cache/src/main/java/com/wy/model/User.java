package com.wy.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel("测试使用类")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer userId;

	@ApiModelProperty("用户名")
	private String username;

	@ApiModelProperty("密码,md5加密")
	@JsonIgnore
	private String password;

	@ApiModelProperty("真实姓名")
	private String realname;

	private Integer departId;

	@ApiModelProperty("身份证号")
	private String idcard;

	@ApiModelProperty("出生日期")
	private Date birthday;

	@ApiModelProperty("年龄")
	private Integer age;

	@ApiModelProperty("性别,男m,女f")
	private String sex;

	@ApiModelProperty("家庭住址")
	private String address;

	@ApiModelProperty("邮件")
	private String email;

	@ApiModelProperty("工资")
	private BigDecimal salary;

	@ApiModelProperty("电话")
	private String tel;

	@ApiModelProperty("用户状态,0黑名单1正常")
	private Byte state;

	@ApiModelProperty("用户图标")
	private String userIcon;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createtime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updatetime;
}