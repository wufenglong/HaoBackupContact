package com.hao.contact.backup;

import java.util.ArrayList;
import java.util.List;

import com.hao.contact.backup.fragment.AboutMeFragment;
import com.hao.contact.backup.fragment.BackupFragment;
import com.hao.contact.backup.fragment.RestoreFragment;
import com.hao.contact.backup.model.ContactHandler;
import com.hao.contact.backup.model.ContactInfo;

import android.os.Bundle;
import android.os.Parcelable;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 首页用viewpage 显示两页1，备份，显示所有手机联系人，让用户勾选，有全选button
 * 2.恢复，先显示所有vcard文件，用户选其一，显示所有，用户选择恢复
 * 
 * 
 * 
 * */
public class MainActivity extends FragmentActivity {
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView t1, t2, t3;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private ArrayList<Fragment> mFragmentsList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		InitImageView();
		InitTextView();
		InitViewPager();
	}

	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		t1 = (TextView) findViewById(R.id.text1);
		t2 = (TextView) findViewById(R.id.text2);
		t3 = (TextView) findViewById(R.id.text3);
		
		t1.setOnClickListener(new MyOnClickListener(0));
		t2.setOnClickListener(new MyOnClickListener(1));
		t3.setOnClickListener(new MyOnClickListener(2));
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vPager);
		mFragmentsList = new ArrayList<Fragment>();
		mFragmentsList.add(new BackupFragment());
		mFragmentsList.add(new RestoreFragment());
		mFragmentsList.add(new AboutMeFragment());
		mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(),mFragmentsList));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor)
				.getWidth();// 获取图片宽度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}

	private void saveContact() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 获取联系人处理实例
				ContactHandler handler = ContactHandler.getInstance();
				// 获取要备份的信息
				List<ContactInfo> _infoList = handler
						.getContactInfo(MainActivity.this);
				handler.backupContacts(MainActivity.this, _infoList); // 备份联系人信息
			}
		}).start();

	}

	private void restoreContact() {
		// 获取联系人处理实例
		ContactHandler handler = ContactHandler.getInstance();
		try {
			// 获取要恢复的联系人信息
			List<ContactInfo> infoList = handler.restoreContacts();
			for (ContactInfo contactInfo : infoList) {
				// 恢复联系人
				handler.addContacts(this, contactInfo);
			}

			Toast.makeText(this, "导入联系人信息成功!", Toast.LENGTH_LONG);

		} catch (Exception e) {
			Toast.makeText(this, "导入联系人信息失败!", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

}
