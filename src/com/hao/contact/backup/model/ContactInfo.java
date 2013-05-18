package com.hao.contact.backup.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 联系人信息包装类
 * 
 * @author
 * 
 */
public class ContactInfo {
	public boolean isSelected = true;
	private int _id=0;
	private boolean hasPhoneNumber=false;
	/** MUST exist */
	private String name; // 姓名

	/** 联系人电话信息 */
	public static class PhoneInfo {
		/** 联系电话类型 */
		public int type;
		/** 联系电话 */
		public String number;
		public String label;
	}

	/** 联系人邮箱信息 */
	public static class EmailInfo {
		/** 邮箱类型 */
		public int type;
		/** 邮箱 */
		public String email;
	}

	/** 地址信息 */
	public static class PostalInfo {
		/** 邮箱类型 */
		public int type;
		/** 邮箱 */
		public String address;
	}

	/** 公司信息 */
	public static class OrganizationInfo {
		public int type;
		/** 公司名 */
		public String companyName;
		/** 职位 */
		public String jobDescription;
	}

	private List<PhoneInfo> phoneList = new ArrayList<PhoneInfo>(); // 联系号码
	private List<EmailInfo> email = new ArrayList<EmailInfo>(); // Email
	private List<PostalInfo> postal = new ArrayList<PostalInfo>(); // 地址
	private List<OrganizationInfo> organization = new ArrayList<OrganizationInfo>(); // 公司
	public int getId() {
		return _id;
	}

	public void setId(int _id) {
		this._id = _id;
	}
	/**
	 * 构造联系人信息
	 * 
	 * @param name
	 *            联系人姓名
	 */
	public ContactInfo(String name) {
		this.name = name;
	}

	/** 姓名 */
	public String getName() {
		return name;
	}

	/** 姓名 */
	public ContactInfo setName(String name) {
		this.name = name;
		return this;
	}

	/** 联系电话信息 */
	public List<PhoneInfo> getPhones() {
		return phoneList;
	}

	/** 联系电话信息 */
	public void setPhones(List<PhoneInfo> phoneList) {
		this.phoneList = phoneList;
	}

	/** 邮箱信息 */
	public List<EmailInfo> getEmail() {
		return email;
	}

	/** 邮箱信息 */
	public void setEmail(List<EmailInfo> email) {
		this.email = email;
	}

	public List<PostalInfo> getPostal() {
		return postal;
	}

	public void setPostal(List<PostalInfo> postal) {
		this.postal = postal;
	}

	public List<OrganizationInfo> getOrganization() {
		return organization;
	}

	public void setOrganization(List<OrganizationInfo> organization) {
		this.organization = organization;
	}

	@Override
	public String toString() {
		return "{name: " + name + ", number: " + phoneList + ", email: "
				+ email + ",postal: " + postal + ",organization: "
				+ organization + "}";
	}

	public boolean isHasPhoneNumber() {
		return hasPhoneNumber;
	}

	public void setHasPhoneNumber(boolean hasPhoneNumber) {
		this.hasPhoneNumber = hasPhoneNumber;
	}


}