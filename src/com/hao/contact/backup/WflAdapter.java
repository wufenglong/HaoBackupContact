package com.hao.contact.backup;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hao.contact.backup.model.ContactInfo;

public class WflAdapter extends BaseAdapter {
	protected static final int REFRESH = 0;
	List<ContactInfo> mDataSource = null;
	private LayoutInflater mInflater;

	public WflAdapter(Context context, List<ContactInfo> dataSource) {
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = null;
		View[] holder = null;
		if (convertView == null) {
			v = mInflater.inflate(R.layout.list_item, parent, false);
			holder = new View[2];
			holder[0] = v.findViewById(R.id.describ1);
			holder[1] = v.findViewById(R.id.selectBtn);
			v.setTag(holder);
		} else {
			v = convertView;
		}
		final ContactInfo item = mDataSource.get(position);
		holder = (View[]) v.getTag();

		if (item.getName() != null) {
			TextView tvDescribe1 = (TextView) holder[0];
			tvDescribe1.setText(item.getName());
		}

		ImageView btn = (ImageView) holder[1];
		if (item.isSelected) {
			btn.setImageResource(R.drawable.select_yes);
		} else {
			btn.setImageResource(R.drawable.select_no);
		}
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				item.isSelected = !item.isSelected;
				notifyDataSetChanged();
			}
		});

		return v;
	}

	@Override
	public boolean isEnabled(int position) {
		// TODO Auto-generated method stub
		return true;
	}

}
