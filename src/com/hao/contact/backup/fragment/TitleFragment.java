package com.hao.contact.backup.fragment;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.hao.contact.backup.R;
import com.hao.contact.backup.model.FileNameSelector;

public class TitleFragment extends ListFragment {
	boolean mDualPane;
	int mCurCheckPostion = 0;
	private File[] mVcfFiles;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getListView().setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				new AlertDialog.Builder(getActivity()).setMessage("要删除此文件吗？")
						.setTitle("删除文件")
						.setPositiveButton("确定", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								getVcfFiles()[position].delete();
								setAdapter();
							}
						}).setNegativeButton("取消", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// 无实现
							}
						}).create().show();

				return false;
			}
		});
		setAdapter();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	// 刷新
	private void setAdapter() {
		mVcfFiles = getVcfFiles();
		String[] fileNames = getVcfFileName(mVcfFiles);
		if (fileNames != null) {
			setListAdapter(new ArrayAdapter<String>(getActivity(),
					android.R.layout.simple_list_item_activated_1, fileNames));
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		showDetails(position);
	}

	private void showDetails(int index) {
		Intent intent = new Intent();
		intent.setClass(getActivity(), DetailsActivity.class);
		intent.putExtra("path", mVcfFiles[index].getAbsolutePath());
		startActivity(intent);
	}

	public File[] getVcfFiles() {
		File file = new File(FileNameSelector.ROOT_PATH);
		File[] files = null;
		if (file != null) {
			files = file.listFiles(new FileNameSelector("vcf"));
		}
		return files;
	}

	/**
	 * 得到以mp3结尾的文件
	 */
	public String[] getVcfFileName(File[] files) {
		String[] names = null;
		if (files != null) {
			names = new String[files.length];
			for (int i = 0; i < files.length; ++i) {
				Log.i("wu0wu", files[i].getName());
				names[i] = files[i].getName();
			}
		}
		return names;
	}
}