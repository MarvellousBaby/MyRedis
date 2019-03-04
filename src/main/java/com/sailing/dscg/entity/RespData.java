package com.sailing.dscg.entity;

import com.sailing.dscg.common.RespCodeEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class RespData<T> implements Serializable {

	private Integer code;//返回码，见NodeStateEnum
	private String reason;//返回说明
	private T data;//返回内容，json数据

	public void setRespCode(RespCodeEnum respCodeEnum){
		this.code = respCodeEnum.getCode();
		this.reason = respCodeEnum.getText();
	}
}
