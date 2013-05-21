package com.hao.contact.backup.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hao.contact.backup.R;
import com.hao.contact.backup.WflAdapter;
import com.hao.contact.backup.model.ContactHandler;
import com.hao.contact.backup.model.ContactInfo;

public class DetailsActivity extends Activity {
	List<ContactInfo> allConatcts = new ArrayList<ContactInfo>();
	private ContactHandler mContactHandler;
	protected ProgressDialog m_pDialog;
	private final static int SHOW_PROGRESS_DIALOG = 0;
	private final static int DISMISS_PROGRESS_DIALOG = 1;
	private final static int REFRESH_PROGRESS_DIALOG = 2;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_PROGRESS_DIALOG:
				showProgressDialog("恢复", "正在恢复中...");
				break;
			case DISMISS_PROGRESS_DIALOG:
				mProgressDialog.dismiss();
				Toast.makeText(DetailsActivity.this, "恢复完成", Toast.LENGTH_LONG)
						.show();
				break;
			case REFRESH_PROGRESS_DIALOG:
				mProgressDialog.setProgress(msg.arg1);
				mProgressDialog.setMessage("正在恢复:" + msg.obj);
				break;
			default:
				break;
			}
		}
	};
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bakcup_fragment_layout);
		// 获取联系人处理实例
		String path = getIntent().getExtras().getString("path");
		Log.i("wu0wu", "path=" + path);
		mContactHandler = ContactHandler.getInstance();
		try {
			allConatcts = mContactHandler.restoreContacts(path);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Button btnBackup = (Button) findViewById(R.id.backup_btn);
		btnBackup.setText("恢复");
		btnBackup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mHandler.sendEmptyMessage(SHOW_PROGRESS_DIALOG);
				restoreContacts();
			}
		});
		if (allConatcts.size() != 0) {
			ListView list = (ListView) findViewById(R.id.backup_list);
			final WflAdapter adapter = new WflAdapter(this, allConatcts);
			list.setAdapter(adapter);
			list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					allConatcts.get(arg2).isSelected = !allConatcts.get(arg2).isSelected;
					adapter.notifyDataSetChanged();
				}
			});
		}
	}

	protected void showProgressDialog(String title, String message) {
		// 创建ProgressDialog对象
		mProgressDialog = new ProgressDialog(DetailsActivity.this);
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

	private void restoreContacts() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				int count = 0;
				int allSize = allConatcts.size();
				for (int i = 0; i < allSize; i++) {
					if (allConatcts.get(i).isSelected) {
						Log.i("wu0wu", "restore name="
								+ allConatcts.get(i).getName());
						Message msg = new Message();
						msg.arg1 = count * 100 / allSize;
						msg.what = REFRESH_PROGRESS_DIALOG;
						msg.obj = allConatcts.get(i).getName();
						mHandler.sendMessage(msg);
						mContactHandler.addContacts(DetailsActivity.this,
								allConatcts.get(i));
						count++;
					}
				}
				Message msg = new Message();
				msg.what = DISMISS_PROGRESS_DIALOG;
				mHandler.sendMessage(msg);
			}
		}).start();

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}