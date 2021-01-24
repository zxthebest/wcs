package com.work.wcs.model;

import java.io.Serializable;

/**
 * 
 * <P>
 * 组织机构
 * </P>
 * 
 * @author 周欣(13667212859)
 * @date 2017年12月12日 上午9:14:55
 */
public class GroupInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String giGroupid;// 组织id

	private String giParentgroupid;// 父组织id

	private String giGroupname;// 组织名称

	private String giAddress;// 组织地址

	private String giAddress2;// 组织地址2

	private String giContactname;// 组织联系人

	private String giContactphone;// 组织电话号码

	private String giCheckfiles;// 组织校验文件

	private String giCreatetime;// 组织创建时间

	private String giUpdatetime;// 组织更新时间

	private String giCreateUserId;// 创建者id

	private String giEditUserId;// 修改者id

	private Integer giLevel;// 组织层级

	private Integer giType;// 组织类型:1、设备提供商（原生产厂商）；2、广告商；3、客户公司；31、客户自建公司；32、客户自建部门；4、平台合作商

	private Integer giForbidden;// 禁用标志

	private String giRemark;// 备注

	private String giProvince;// 省

	private String giCity;// 市

	private Integer giDel;// 删除标记

	public String getGiGroupid() {
		return giGroupid;
	}

	public void setGiGroupid(String giGroupid) {
		this.giGroupid = giGroupid == null ? null : giGroupid.trim();
	}

	public String getGiParentgroupid() {
		return giParentgroupid;
	}

	public void setGiParentgroupid(String giParentgroupid) {
		this.giParentgroupid = giParentgroupid == null ? null : giParentgroupid.trim();
	}

	public String getGiGroupname() {
		return giGroupname;
	}

	public void setGiGroupname(String giGroupname) {
		this.giGroupname = giGroupname == null ? null : giGroupname.trim();
	}

	public String getGiAddress() {
		return giAddress;
	}

	public void setGiAddress(String giAddress) {
		this.giAddress = giAddress == null ? null : giAddress.trim();
	}

	public String getGiContactname() {
		return giContactname;
	}

	public void setGiContactname(String giContactname) {
		this.giContactname = giContactname == null ? null : giContactname.trim();
	}

	public String getGiContactphone() {
		return giContactphone;
	}

	public void setGiContactphone(String giContactphone) {
		this.giContactphone = giContactphone == null ? null : giContactphone.trim();
	}

	public String getGiCheckfiles() {
		return giCheckfiles;
	}

	public void setGiCheckfiles(String giCheckfiles) {
		this.giCheckfiles = giCheckfiles == null ? null : giCheckfiles.trim();
	}

	public String getGiCreatetime() {
		return giCreatetime;
	}

	public void setGiCreatetime(String giCreatetime) {
		this.giCreatetime = giCreatetime;
	}

	public Integer getGiLevel() {
		return giLevel;
	}

	public void setGiLevel(Integer giLevel) {
		this.giLevel = giLevel;
	}

	public Integer getGiType() {
		return giType;
	}

	public void setGiType(Integer giType) {
		this.giType = giType;
	}

	public Integer getGiDel() {
		return giDel;
	}

	public void setGiDel(Integer giDel) {
		this.giDel = giDel;
	}

	public String getGiUpdatetime() {
		return giUpdatetime;
	}

	public void setGiUpdatetime(String giUpdatetime) {
		this.giUpdatetime = giUpdatetime;
	}

	public String getGiCreateUserId() {
		return giCreateUserId;
	}

	public void setGiCreateUserId(String giCreateUserId) {
		this.giCreateUserId = giCreateUserId;
	}

	public String getGiEditUserId() {
		return giEditUserId;
	}

	public void setGiEditUserId(String giEditUserId) {
		this.giEditUserId = giEditUserId;
	}

	public Integer getGiForbidden() {
		return giForbidden;
	}

	public void setGiForbidden(Integer giForbidden) {
		this.giForbidden = giForbidden;
	}

	public String getGiRemark() {
		return giRemark;
	}

	public void setGiRemark(String giRemark) {
		this.giRemark = giRemark;
	}

	public String getGiAddress2() {
		return giAddress2;
	}

	public void setGiAddress2(String giAddress2) {
		this.giAddress2 = giAddress2;
	}

	public String getGiProvince() {
		return giProvince;
	}

	public void setGiProvince(String giProvince) {
		this.giProvince = giProvince;
	}

	public String getGiCity() {
		return giCity;
	}

	public void setGiCity(String giCity) {
		this.giCity = giCity;
	}
}