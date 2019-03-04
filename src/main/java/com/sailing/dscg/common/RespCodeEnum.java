package com.sailing.dscg.common;

public enum RespCodeEnum {
	SUCCESS(200,"成功"),
	FAIL(100,"操作失败"),
    EXCEPTION(500,"异常"),
	UNADMIN(101,"账号未登录或登陆已超时"),
	BAN_USER(102,"账号已禁止，请联系管理员"),
	ADDBACKLIST(103,"您的账号和Ip均已被加入黑名单，请联系管理员"),
	BACKLISTIPIN(104,"用户Ip已进入黑名单，请联系管理员"),
	BACKLISTUSERIN(105,"用户账号已进入黑名单，请联系管理员"),
	PARAM_FAIL(106,"参数校验失败"),


	EXCEPTION_300(300,"新增用户成功"),
	EXCEPTION_301(301,"新增用户失败"),
	EXCEPTION_302(302,"该登陆账号不存在!"),
	EXCEPTION_303(303,"该登陆账号已存在"),
	EXCEPTION_304(304,"更新信息成功"),
	EXCEPTION_305(305,"更新信息异常"),
    EXCEPTION_306(306,"登陆账户或密码错误！"),

    CODE_7300(7300,"监控ip地址已存在"),
	CODE_7301(7301,"删除的为当前登陆用户");


	private int code;
	private String text;

	//私有构造
	private RespCodeEnum(int code, String  text){
		this.code = code;
		this.text = text;
	}

	/**
	 * 获得字符串码值
	 * @return
	 */
	@Override
	public String toString() {
		return String.valueOf ( this.code );
	}

	/**
	 * 获得整型码值
	 * @return
	 */
	public int getCode(){
		return code;
	}
	/**
	 * 获得描述
	 * @return
	 */
	public String getText(){ return text; }
}
