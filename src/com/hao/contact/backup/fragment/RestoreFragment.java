package com.hao.contact.backup.fragment;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.hao.contact.backup.R;
import com.hao.contact.backup.model.FileNameSelector;

public class RestoreFragment extends ListFragment {
	boolean mDualPane;
	int mCurCheckPostion = 0;
	private File[] mVcfFiles;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.restore_fragment_layout, null);
	}

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
		setListAdapter(new WflRestoreAdapter(getActivity(),
				getVcfFileName(mVcfFiles)));
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
	public ArrayList<String> getVcfFileName(File[] files) {
		ArrayList<String> names = new ArrayList<String>();
		if (files != null) {
			for (int i = 0; i < files.length; ++i) {
				Log.i("wu0wu", files[i].getName());
				names.add(files[i].getName());
			}
		}
		return names;
	}

	public class WflRestoreAdapter extends BaseAdapter {
		protected static final int REFRESH = 0;
		ArrayList<String> mDataSource = null;
		private LayoutInflater mInflater;

		public WflRestoreAdapter(Context context, ArrayList<String> dataSource) {
			this.mDataSource = dataSource;
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mDataSource.size();
		}

		@Override
		public Object getItem(int position) {
			return mDataSource.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getItemViewType(int position) {
			// if (mDataSource.get(position).isSelected) {
			// return 0;
			// }
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 1;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View v = null;
			View[] holder = null;
			if (convertView == null) {
				v = mInflater.inflate(R.layout.list_item1, parent, false);
				holder = new View[1];
				holder[0] = v.findViewById(R.id.describ1);
				v.setTag(holder);
			} else {
				v = convertView;
			}
			final String item = mDataSource.get(position);
			holder = (View[]) v.getTag();

			if (item != null) {
				TextView tvDescribe1 = (TextView) holder[0];
				tvDescribe1.setText(item);
			}

			return v;
		}

		@Override
		public boolean isEnabled(int position) {
			// TODO Auto-generated method stub
			return true;
		}

	}
}