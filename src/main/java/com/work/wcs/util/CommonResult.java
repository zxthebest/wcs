package com.work.wcs.util;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;

public class CommonResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final Boolean successCode = true;

	public static final Boolean failureCode = false;

	private Boolean STATUS = failureCode;

	private Integer returnCode;

	private String message;

	private T data;

	private List<T> datas;

	public CommonResult() {
		// TODO Auto-generated constructor stub
	}

	public CommonResult(Boolean success) {
		super();
		this.STATUS = success;
	}

	public CommonResult(Boolean success, String message) {
		super();
		this.STATUS = success;
		this.message = message;
	}

	public CommonResult(Boolean success, T data) {
		super();
		this.STATUS = success;
		this.data = data;
	}

	public CommonResult(Boolean success, List<T> datas) {
		super();
		this.STATUS = success;
		this.datas = datas;
	}

	public Boolean getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(Boolean sTATUS) {
		STATUS = sTATUS;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public List<T> getDatas() {
		return datas;
	}

	public void setDatas(List<T> datas) {
		this.datas = datas;
	}

	public static Boolean getSuccesscode() {
		return successCode;
	}

	public static Boolean getFailurecode() {
		return failureCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(Integer returnCode) {
		this.returnCode = returnCode;
	}

	/**
	 * 
	 * <p>
	 * List去重
	 * </p>
	 * 
	 * @param list
	 * @author 周欣(13667212859)
	 * @date 2016年12月28日 下午2:21:48
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void removeDuplicate(List list) {
		HashSet h = new HashSet(list);
		list.clear();
		list.addAll(h);

	}

}
