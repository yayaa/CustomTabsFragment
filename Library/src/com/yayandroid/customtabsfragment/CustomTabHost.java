package com.yayandroid.customtabsfragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import com.yayandroid.customtabsfragment_library.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CustomTabHost extends HorizontalScrollView {

	private LinearLayout tabLayout;
	private Runnable mTabSelector;
	private OnTabReselectedListener mTabReselectedListener;
	private OnTabSelectedListener mTabSelectedListener;

	private int mMaxTabWidth;
	private int mSelectedTabIndex;
	private int mLastSelectedTabIndex = -1;
	private int mCustomViewId = -1;
	private int mTabCount = 0;

	public CustomTabHost(Context context) {
		super(context);
		Init();
	}

	public CustomTabHost(Context context, AttributeSet attrs) {
		super(context, attrs);
		Init();
	}

	public CustomTabHost(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Init();
	}

	/**
	 * Interface for a callback when the selected tab has been reselected.
	 */
	public interface OnTabReselectedListener {
		/**
		 * Callback when the selected tab has been reselected.
		 * 
		 * @param position
		 *            Position of the current center item.
		 */
		void onTabReselected(int position);
	}

	public void setOnTabReselectedListener(OnTabReselectedListener listener) {
		mTabReselectedListener = listener;
	}

	/**
	 * Interface for a callback when a tab has selected.
	 */
	public interface OnTabSelectedListener {
		/**
		 * Callback when the selected tab has been reselected.
		 * 
		 * @param position
		 *            Position of the current center item.
		 */
		void onTabSelected(int position);
	}

	public void setOnTabSelectedListener(OnTabSelectedListener listener) {
		mTabSelectedListener = listener;
	}

	public void Init() {
		setHorizontalScrollBarEnabled(false);
		tabLayout = new LinearLayout(getContext());
		addView(tabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT,
				WRAP_CONTENT));
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
		setFillViewport(lockedExpanded);

		final int childCount = tabLayout.getChildCount();
		if (childCount > 1
				&& (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
			if (childCount > 2) {
				mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
			} else {
				mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
			}
		} else {
			mMaxTabWidth = -1;
		}

		final int oldWidth = getMeasuredWidth();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int newWidth = getMeasuredWidth();

		if (lockedExpanded && oldWidth != newWidth) {
			// Recenter the tab display if we're at a new (scrollable) size.
			setCurrentItem(mSelectedTabIndex);
		}
	}

	private void animateToTab(final int position) {
		final View tabView = tabLayout.getChildAt(position);
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
		mTabSelector = new Runnable() {
			public void run() {
				final int scrollPos = tabView.getLeft()
						- (getWidth() - tabView.getWidth()) / 2;
				smoothScrollTo(scrollPos, 0);
				mTabSelector = null;
			}
		};
		post(mTabSelector);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mTabSelector != null) {
			// Re-post the selector we saved
			post(mTabSelector);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
	}

	public void addTab(int index) {

		final CustomTabView tabView = new CustomTabView(getContext());
		if (getCustomViewId() != -1) {
			tabView.SetCustomView();
			tabView.setTag(index);
			tabView.mIndex = index;
			tabView.setFocusable(true);
			tabView.setOnClickListener(mTabClickListener);

			tabLayout.addView(tabView, new LinearLayout.LayoutParams(0,
					WRAP_CONTENT, 1));
		}
	}

	public void setCount(int count) {
		this.mTabCount = count;
		notifyDataSetChanged();
		setCurrentItem(0);
	}

	public int getCount() {
		return mTabCount;
	}

	public void notifyDataSetChanged() {
		tabLayout.removeAllViews();

		for (int i = 0; i < mTabCount; i++)
			addTab(i);

		if (mSelectedTabIndex > mTabCount) {
			mSelectedTabIndex = mTabCount - 1;
		}

		setCurrentItem(mSelectedTabIndex);

		requestLayout();
	}

	public void setCurrentItem(int item) {
		mSelectedTabIndex = item;

		final int tabCount = tabLayout.getChildCount();
		for (int i = 0; i < tabCount; i++) {
			final View child = tabLayout.getChildAt(i);
			final boolean isSelected = (i == item);
			child.setSelected(isSelected);
			if (isSelected) {
				animateToTab(item);
			}
		}

		if (mLastSelectedTabIndex == mSelectedTabIndex) {
			if (mTabReselectedListener != null)
				mTabReselectedListener.onTabReselected(mSelectedTabIndex);
		} else {
			if (mTabSelectedListener != null)
				mTabSelectedListener.onTabSelected(mSelectedTabIndex);
		}

	}

	private final OnClickListener mTabClickListener = new OnClickListener() {
		public void onClick(View view) {

			CustomTabView tabView = (CustomTabView) view;
			mLastSelectedTabIndex = mSelectedTabIndex;
			mSelectedTabIndex = tabView.getIndex();
			setCurrentItem(mSelectedTabIndex);

		}
	};

	private class CustomTabView extends RelativeLayout {
		private int mIndex;

		public CustomTabView(Context context) {
			super(context);
			// To use viewpagerindicator's theme:
			// super(context, null, R.attr.vpiTabPageIndicatorStyle);
		}

		public void SetCustomView() {
			if (getCustomViewId() != -1) {
				LayoutInflater inflater = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View v = inflater.inflate(getCustomViewId(), null);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
				lp.addRule(RelativeLayout.CENTER_IN_PARENT);
				this.addView(v, lp);
				this.setBackgroundResource(R.drawable.tab_selector);
			}
		}

		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			// Re-measure if we went beyond our maximum size.
			if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
				super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth,
						MeasureSpec.EXACTLY), heightMeasureSpec);
			}
		}

		public int getIndex() {
			return mIndex;
		}
	}

	public View getTabItemAtPosition(int position) {
		return tabLayout.getChildAt(position);
	}

	public int getCustomViewId() {
		return mCustomViewId;
	}

	public void setCustomViewId(int mCustomViewId) {
		this.mCustomViewId = mCustomViewId;
	}

	public int getSelectedTabPosition() {
		return mSelectedTabIndex;
	}

	public int getLastSelectedTabPosition() {
		return mLastSelectedTabIndex;
	}

}
