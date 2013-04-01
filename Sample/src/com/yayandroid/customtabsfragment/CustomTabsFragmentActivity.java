package com.yayandroid.customtabsfragment;

import com.yayandroid.customtabsfragment.TabController.FragmentRequires;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomTabsFragmentActivity extends FragmentActivity {

	private CustomTabHost tabs;
	private TabController controller;
	private final String[] TAB_TAGS = new String[] { "Tab1", "Tab2", "Tab3", "Tab4"};
	private final int[] TAB_ITEM_ICONS = new int[] { R.drawable.tab1,
			R.drawable.tab2, R.drawable.tab3, R.drawable.tab4, };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main); 

		/** Find and customize tab bar */
		tabs = (CustomTabHost) findViewById(R.id.tabHolder);
		
		// set customView to create items from it
		tabs.setCustomViewId(R.layout.custom_tab_item);
		
		// set tab count
		tabs.setCount(TAB_ITEM_ICONS.length);
		
		/** These methods are just to show how to modify your tab items */
		CustomizeTabs();
		RefreshTab(3, "Changed");
		
		/**
		 * Create a controller object and bind customTabHost with it set it also
		 * where to put fragments
		 */
		controller = new TabController(this, tabs, R.id.tabContent);
		
		// set tabTags on controller to handle with fragment changes
		controller.setTabTags(TAB_TAGS);
		
		// set a fragmentRequeires to return the right fragment on given position
		controller.setRequirementListener(new FragmentRequires() {
			@Override
			public Fragment onRequirement(int position) {
				return CustomTabFragment.newInstance(position);
			}
		});

		// Initialize fragments with position or without it
		controller.InitializeFragmentsWith(0);
		
	}

	/**
	 * Here find every tab items and customize them
	 */
	public void CustomizeTabs() {
		for (int i = 0; i < tabs.getCount(); i++) {
			View tabItem = tabs.getTabItemAtPosition(i);
			ImageView img = (ImageView) tabItem.findViewById(R.id.tabIcon);
			img.setImageResource(TAB_ITEM_ICONS[i]);
			TextView tv = (TextView) tabItem.findViewById(R.id.tabText);
			tv.setText(TAB_TAGS[i]);
		}
	}

	/**
	 * Here refresh tab on given position with given text
	 * 
	 * @param position
	 * @param text
	 */
	public void RefreshTab(int position, String text) {
		if (position < tabs.getCount()) {
			View tabItem = tabs.getTabItemAtPosition(position);
			TextView tv = (TextView) tabItem.findViewById(R.id.tabText);
			tv.setText(text);
		}
	}

}
