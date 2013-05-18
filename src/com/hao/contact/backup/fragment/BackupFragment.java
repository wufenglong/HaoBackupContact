package com.hao.contact.backup.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hao.contact.backup.R;
import com.hao.contact.backup.WflAdapter;
import com.hao.contact.backup.model.ContactHandler;
import com.hao.contact.backup.model.ContactInfo;

/**
 * 
 * 备份： 1.先最快获得contact_id和displayName表，用于显示 2.动态获得phone信息，动态刷新到list中。
 * 3.根据用户选择的contact_id表，分别读取所有信息，备份
 * 
 * 
 * 
 * @author wufenglong
 * @since 2013-05-18
 * */
public class BackupFragment extends Fragment {
	List<ContactInfo> allConatcts = null;
	private ContactHandler mContactHandler;
	protected ProgressDialog mProgressDialog;// 正在备份进度条
	private final static int SHOW_PROGRESS_DIALOG = 0;
	private final static int DISMISS_PROGRESS_DIALOG = 1;
	private final static int REFRESH_PROGRESS_DIALOG = 2;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_PROGRESS_DIALOG:
				showProgressDialog("提示", "正在备份中...");
				break;
			case DISMISS_PROGRESS_DIALOG:
				mProgressDialog.dismiss();
				Toast.makeText(getActivity(), "备份完成", Toast.LENGTH_LONG).show();
				break;
			case REFRESH_PROGRESS_DIALOG:
				mProgressDialog.setProgress(msg.arg1);
				mProgressDialog.setMessage("正在备份:" + msg.obj);
			default:
				break;
			}
		}
	};
	private ListView listView;
	private ContentResolver cr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i("wu0wu", "onCreate");
		cr = getActivity().getContentResolver();
		// 获取联系人处理实例
		mContactHandler = ContactHandler.getInstance();
		allConatcts = mContactHandler.getAllDisplayName(getActivity(), cr);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshAdapter();
	}

	protected void showProgressDialog(String title, String message) {
		// 创建ProgressDialog对象
		mProgressDialog = new ProgressDialog(getActivity());
		// 设置进度条风格，风格为圆形，旋转的
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		// 设置ProgressDialog 标题
		mProgressDialog.setTitle(title);
		// 设置ProgressDialog 提示信息
		mProgressDialog.setMessage(message);
		// 设置ProgressDialog 的进度条是否不明确
		mProgressDialog.setIndeterminate(false);
		// 设置ProgressDialog 是否可以按退回按键取消
		mProgressDialog.setCancelable(false);
		// 让ProgressDialog显示
		mProgressDialog.show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.bakcup_fragment_layout, null);
		Button btnBackup = (Button) v.findViewById(R.id.backup_btn);
		btnBackup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				backupContacts();
			}
		});
		listView = (ListView) v.findViewById(R.id.backup_list);
		refreshAdapter();
		return v;
	}

	private void refreshAdapter() {
		final WflAdapter adapter = new WflAdapter(getActivity(), allConatcts);
		listView.setAdapter(adapter);
	}

	private void backupContacts() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
				List<ContactInfo> selectedContact = new ArrayList<ContactInfo>();
				int count = 0;
				int allSise = allConatcts.size();
				for (int i = 0; i < allSise; i++) {
					if (allConatcts.get(i).isSelected) {
						Log.i("wu0wu", allConatcts.get(i).getName());
						Message msg = new Message();
						msg.arg1 = count * 100 / allSise;
						msg.what = REFRESH_PROGRESS_DIALOG;
						msg.obj = allConatcts.get(i).getName();
						mHandler.sendMessage(msg);
						selectedContact.add(refreshContactInfo(allConatcts
								.get(i)));
						count++;
					}
				}
				mContactHandler.backupContacts(getActivity(), selectedContact);
				Message msg = new Message();
				msg.what = DISMISS_PROGRESS_DIALOG;
				mHandler.sendMessage(msg);
			}
		}).start();

	}

	private ContactInfo refreshContactInfo(ContactInfo contact) {
		return mContactHandler.getContactInfo(getActivity(), contact, cr);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
