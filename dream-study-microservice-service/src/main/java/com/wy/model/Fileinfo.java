package com.wy.model;

import java.util.Date;

import com.wy.base.AbstractModel;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ApiModel("用户上传文件具体信息表")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Fileinfo extends AbstractModel {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("文件编号")
    private Integer fileinfoId;

    @ApiModelProperty("存储在本地的名称,规则是yyyyMMdd_文件后缀_32uuid")
    private String localName;

    @ApiModelProperty("文件本来的全名")
    private String originalName;

    @ApiModelProperty("文件类型:1图片;2音频;3视频;4文本;默认5其他")
    private Byte fileType;

    @ApiModelProperty("文件大小,单位M")
    private Double fileSize;

    @ApiModelProperty("音视频文件时长,格式为HH:mm:ss")
    private String fileTime;

    @ApiModelProperty("文件后缀,不需要点")
    private String fileSuffix;

    @ApiModelProperty("上传时间")
    private Date createtime;
}