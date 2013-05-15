package com.hao.contact.backup.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import a_vcard.android.provider.Contacts;
import a_vcard.android.syncml.pim.VDataBuilder;
import a_vcard.android.syncml.pim.VNode;
import a_vcard.android.syncml.pim.vcard.ContactStruct;
import a_vcard.android.syncml.pim.vcard.ContactStruct.ContactMethod;
import a_vcard.android.syncml.pim.vcard.ContactStruct.PhoneData;
import a_vcard.android.syncml.pim.vcard.VCardComposer;
import a_vcard.android.syncml.pim.vcard.VCardException;
import a_vcard.android.syncml.pim.vcard.VCardParser;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

/**
 * 联系人 备份/还原操作
 * 
 * @author
 * 
 */
public class ContactHandler {
	private static String TAG = "ContactHandler";
	private static ContactHandler instance_ = new ContactHandler();

	/** 获取实例 */
	public static ContactHandler getInstance() {
		return instance_;
	}

	/**
	 * 获取联系人指定信息
	 * 
	 * @param projection
	 *            指定要获取的列数组, 获取全部列则设置为null
	 * @return
	 * @throws Exception
	 */
	public Cursor queryContact(Activity context, String[] projection) {
		// 获取联系人的所需信息
		Cursor cur = context.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, projection, null, null,
				null);
		return cur;
	}

	/**
	 * 获取联系人信息
	 * 
	 * @param context
	 * @return
	 */
	public List<ContactInfo> getContactInfo(Activity context) {
		List<ContactInfo> infoList = new ArrayList<ContactInfo>();

		Cursor contactCursor = queryContact(context, null);
		if (contactCursor == null) {
			return null;
		}
		while (contactCursor.moveToNext()) {
			// 获取联系人id号
			String id = contactCursor.getString(contactCursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			// 获取联系人姓名
			String displayName = contactCursor.getString(contactCursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

			ContactInfo info = new ContactInfo(displayName);// 初始化联系人信息

			// 设置联系人电话信息
			List<ContactInfo.PhoneInfo> phoneNumberList = getPhoneInfos(
					contactCursor, id, context);
			if (!phoneNumberList.isEmpty()) {
				info.setPhones(phoneNumberList);
			}
			// 设置email信息
			List<ContactInfo.EmailInfo> emailInfoList = getEmailInfo(
					contactCursor, id, context);
			if (!emailInfoList.isEmpty()) {
				info.setEmail(emailInfoList);
			}
			// 设置地址信息
			List<ContactInfo.PostalInfo> postalInfoList = getPostalInfo(
					contactCursor, id, context);
			if (!postalInfoList.isEmpty()) {
				info.setPostal(postalInfoList);
			}
			// // 设置公司信息
			// List<ContactInfo.OrganizationInfo> organizationInfoList =
			// getOrganizationInfo(
			// contactCursor, id, context);
			// if (!organizationInfoList.isEmpty()) {
			// info.setOrganization(organizationInfoList);
			// }
			infoList.add(info);
		}
		contactCursor.close();
		return infoList;
	}

	private List<ContactInfo.PhoneInfo> getPhoneInfos(final Cursor c,
			String id, Context context) {
		List<ContactInfo.PhoneInfo> phoneNumberList = new ArrayList<ContactInfo.PhoneInfo>();
		// 查看联系人有多少电话号码, 如果没有返回0
		int phoneCount = c.getInt(c
				.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

		if (phoneCount > 0) {
			Cursor phonesCursor = context.getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ id, null, null);
			if (phonesCursor != null) {
				while (phonesCursor.moveToNext()) {
					// 遍历所有电话号码
					String phoneNumber = phonesCursor
							.getString(phonesCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					// 对应的联系人类型
					int type = phonesCursor
							.getInt(phonesCursor
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

					// 初始化联系人电话信息
					ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
					phoneInfo.type = type;
					phoneInfo.number = phoneNumber;

					phoneNumberList.add(phoneInfo);
				}

			}
			phonesCursor.close();
		}
		return phoneNumberList;
	}

	private List<ContactInfo.EmailInfo> getEmailInfo(final Cursor c, String id,
			Context context) {
		List<ContactInfo.EmailInfo> emailList = new ArrayList<ContactInfo.EmailInfo>();
		// 获得联系人的EMAIL
		Cursor emailCur = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=" + id,
				null, null);
		if (emailCur != null) {
			while (emailCur.moveToNext()) {
				// 遍历所有的email
				String email = emailCur
						.getString(emailCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));
				int type = emailCur
						.getInt(emailCur
								.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

				// 初始化联系人邮箱信息
				ContactInfo.EmailInfo emailInfo = new ContactInfo.EmailInfo();
				emailInfo.type = type; // 设置邮箱类型
				emailInfo.email = email; // 设置邮箱地址
				emailList.add(emailInfo);
			}
		}
		emailCur.close();
		return emailList;
	}

	/* 地址 */
	private List<ContactInfo.PostalInfo> getPostalInfo(final Cursor c,
			String id, Context context) {
		List<ContactInfo.PostalInfo> postalList = new ArrayList<ContactInfo.PostalInfo>();

		Cursor postalCur = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,
				null,
				ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID
						+ "=" + id, null, null);
		if (postalCur != null) {
			while (postalCur.moveToNext()) {
				String address = postalCur
						.getString(postalCur
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));
				int type = postalCur
						.getInt(postalCur
								.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

				ContactInfo.PostalInfo postalInfo = new ContactInfo.PostalInfo();
				postalInfo.type = type;
				postalInfo.address = address;
				postalList.add(postalInfo);
			}
		}
		postalCur.close();
		return postalList;
	}

	/* 公司 */
	private List<ContactInfo.OrganizationInfo> getOrganizationInfo(
			final Cursor c, String id, Context context) {
		List<ContactInfo.OrganizationInfo> organizationList = new ArrayList<ContactInfo.OrganizationInfo>();

		// 获取该联系人组织
		Cursor organizationsCursor = context.getContentResolver().query(
				Data.CONTENT_URI,
				new String[] { Data._ID, Organization.TYPE,
						Organization.COMPANY, Organization.TITLE },
				Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"
						+ Organization.CONTENT_ITEM_TYPE + "'",
				new String[] { id }, null);
		if (organizationsCursor != null) {
			while (organizationsCursor.moveToNext()) {
				int type = organizationsCursor
						.getInt(organizationsCursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TYPE));
				String company = organizationsCursor
						.getString(organizationsCursor
								.getColumnIndex(Organization.COMPANY));
				String jobDescription = organizationsCursor
						.getString(organizationsCursor
								.getColumnIndex(Organization.TITLE));
				ContactInfo.OrganizationInfo organizationInfo = new ContactInfo.OrganizationInfo();
				organizationInfo.type = type;
				organizationInfo.companyName = company;
				organizationInfo.jobDescription = jobDescription;
				organizationList.add(organizationInfo);
			}
		}
		organizationsCursor.close();
		return organizationList;
	}

	/**
	 * 备份联系人
	 */

	public String getDateFormate() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		return formatter.format(curDate);
	}

	public void backupContacts(Activity context, List<ContactInfo> infos) {
		if (infos == null) {
			return;
		}
		try {
			Log.i(TAG, "infos.size=" + infos.size());

			String path = FileNameSelector.ROOT_PATH + "/" + getDateFormate() + ".vcf";

			OutputStreamWriter writer = new OutputStreamWriter(
					new FileOutputStream(path), "UTF-8");

			VCardComposer composer = new VCardComposer();
			for (ContactInfo info : infos) {
				ContactStruct contact = new ContactStruct();
				contact.name = info.getName();
				// 获取联系人电话信息, 添加至 ContactStruct
				List<ContactInfo.PhoneInfo> numberList = info.getPhones();
				for (ContactInfo.PhoneInfo phoneInfo : numberList) {
					contact.addPhone(phoneInfo.type, phoneInfo.number, null,
							true);
				}
				// 获取联系人Email信息, 添加至 ContactStruct
				List<ContactInfo.EmailInfo> emailList = info.getEmail();
				for (ContactInfo.EmailInfo emailInfo : emailList) {
					contact.addContactmethod(Contacts.KIND_EMAIL,
							emailInfo.type, emailInfo.email, null, true);
				}
				// 获取地址信息, 添加至 ContactStruct
				List<ContactInfo.PostalInfo> postalList = info.getPostal();
				for (ContactInfo.PostalInfo postal : postalList) {
					contact.addContactmethod(Contacts.KIND_POSTAL, postal.type,
							postal.address, null, true);
				}
				// // 获取公司信息, 添加至 ContactStruct
				// List<ContactInfo.OrganizationInfo> organizationList = info
				// .getOrganization();
				// for (ContactInfo.OrganizationInfo organization :
				// organizationList) {
				// Log.i(TAG, organization.type + ","
				// + organization.companyName + ","
				// + organization.jobDescription);
				// contact.addOrganization(organization.type,
				// organization.companyName,
				// null, false);
				// }

				String vcardString = composer.createVCard(contact,
						VCardComposer.VERSION_VCARD30_INT);
				writer.write(vcardString);
				writer.write("\n");
				Log.i(TAG, "vcardString=" + vcardString);
				writer.flush();
			}
			writer.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Log.i(TAG, "UnsupportedEncodingException=" + e.toString());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i(TAG, "FileNotFoundException=" + e.toString());
		} catch (VCardException e) {
			e.printStackTrace();
			Log.i(TAG, "VCardException=" + e.toString());
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG, "IOException=" + e.toString());
		}

		// Toast.makeText(context, "备份成功！", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获取vCard文件中的联系人信息
	 * 
	 * @return
	 */
	public List<ContactInfo> restoreContacts(String path) throws Exception {
		List<ContactInfo> contactInfoList = new ArrayList<ContactInfo>();

		VCardParser parse = new VCardParser();
		VDataBuilder builder = new VDataBuilder();

		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(path), "UTF-8"));

		String vcardString = "";
		String line;
		while ((line = reader.readLine()) != null) {
			vcardString += line + "\n";
		}
		reader.close();

		boolean parsed = parse.parse(vcardString, "UTF-8", builder);

		if (!parsed) {
			throw new VCardException("Could not parse vCard file: " + path);
		}

		List<VNode> pimContacts = builder.vNodeList;

		for (VNode contact : pimContacts) {

			ContactStruct contactStruct = ContactStruct
					.constructContactFromVNode(contact, 1);
			// 获取备份文件中的联系人电话信息
			List<PhoneData> phoneDataList = contactStruct.phoneList;
			List<ContactInfo.PhoneInfo> phoneInfoList = new ArrayList<ContactInfo.PhoneInfo>();
			for (PhoneData phoneData : phoneDataList) {
				ContactInfo.PhoneInfo phoneInfo = new ContactInfo.PhoneInfo();
				phoneInfo.number = phoneData.data;
				phoneInfo.type = phoneData.type;
				phoneInfoList.add(phoneInfo);
			}

			// 获取备份文件中的联系人邮箱信息
			List<ContactMethod> emailList = contactStruct.contactmethodList;
			List<ContactInfo.EmailInfo> emailInfoList = new ArrayList<ContactInfo.EmailInfo>();
			// 存在 Email 信息
			if (null != emailList) {
				for (ContactMethod contactMethod : emailList) {
					if (Contacts.KIND_EMAIL == contactMethod.kind) {
						ContactInfo.EmailInfo emailInfo = new ContactInfo.EmailInfo();
						emailInfo.email = contactMethod.data;
						emailInfo.type = contactMethod.type;
						emailInfoList.add(emailInfo);
					}
				}
			}
			ContactInfo info = new ContactInfo(contactStruct.name);
			info.setEmail(emailInfoList);
			info.setPhones(phoneInfoList);
			contactInfoList.add(info);
		}

		return contactInfoList;
	}

	/**
	 * 向手机中录入联系人信息
	 * 
	 * @param info
	 *            要录入的联系人信息
	 */
	public void addContacts(Activity context, ContactInfo info) {
		ContentValues values = new ContentValues();
		// 首先向RawContacts.CONTENT_URI执行一个空值插入，目的是获取系统返回的rawContactId
		Uri rawContactUri = context.getContentResolver().insert(
				RawContacts.CONTENT_URI, values);
		long rawContactId = ContentUris.parseId(rawContactUri);

		// 往data表入姓名数据
		values.clear();
		values.put(Data.RAW_CONTACT_ID, rawContactId);
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		values.put(StructuredName.GIVEN_NAME, info.getName());
		context.getContentResolver().insert(
				android.provider.ContactsContract.Data.CONTENT_URI, values);

		// 获取联系人电话信息
		List<ContactInfo.PhoneInfo> phoneList = info.getPhones();
		/** 录入联系电话 */
		for (ContactInfo.PhoneInfo phoneInfo : phoneList) {
			values.clear();
			values.put(
					android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID,
					rawContactId);
			values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
			// 设置录入联系人电话信息
			values.put(Phone.NUMBER, phoneInfo.number);
			values.put(Phone.TYPE, phoneInfo.type);
			// 往data表入电话数据
			context.getContentResolver().insert(
					android.provider.ContactsContract.Data.CONTENT_URI, values);
		}

		// 获取联系人邮箱信息
		List<ContactInfo.EmailInfo> emailList = info.getEmail();

		/** 录入联系人邮箱信息 */
		for (ContactInfo.EmailInfo email : emailList) {
			values.clear();
			values.put(
					android.provider.ContactsContract.Contacts.Data.RAW_CONTACT_ID,
					rawContactId);
			values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
			// 设置录入的邮箱信息
			values.put(Email.DATA, email.email);
			values.put(Email.TYPE, email.type);
			// 往data表入Email数据
			context.getContentResolver().insert(
					android.provider.ContactsContract.Data.CONTENT_URI, values);
		}

	}

}
