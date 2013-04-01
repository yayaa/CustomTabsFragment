package com.yayandroid.customtabsfragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.yayandroid.customtabsfragment.CustomTabHost.OnTabReselectedListener;
import com.yayandroid.customtabsfragment.CustomTabHost.OnTabSelectedListener;

public class TabController implements OnTabSelectedListener,
		OnTabReselectedListener {

	private CustomTabHost tabs;
	private FragmentActivity act;
	private FragmentRequires requirementListener;
	private String[] tags;
	private int contentId;

	/**
	 * Interface to reach this class to send necessary fragment
	 * 
	 * @author Yahya BAYRAMOÐLU
	 * 
	 */
	public interface FragmentRequires {
		public Fragment onRequirement(int position);
	}

	/**
	 * Constructor
	 * 
	 * @param act
	 *            : to use fragmentActivity while getting
	 *            supportedFragmentManager
	 * @param tabs
	 *            : to combine controller and tabHost
	 * @param contentId
	 *            : to put fragments on it
	 */
	public TabController(FragmentActivity act, CustomTabHost tabs, int contentId) {
		this.act = act;
		this.tabs = tabs;
		this.contentId = contentId;
	}

	/**
	 * Initialize fragments with 0
	 */
	public void InitializeFragments() {
		tabs.setOnTabSelectedListener(this);
		tabs.setOnTabReselectedListener(this);
	}

	/**
	 * Initialize fragments with given position
	 * 
	 * @param position
	 */
	public void InitializeFragmentsWith(int position) {
		tabs.setOnTabSelectedListener(this);
		tabs.setOnTabReselectedListener(this);

		tabs.setCurrentItem(position);
	}

	/**
	 * Tab items' tags to set them to fragments, so they can be find back easily
	 * 
	 * @param tags
	 */
	public void setTabTags(String[] tags) {
		this.tags = tags;
	}

	/**
	 * Can get fragment tag as public, in case of needy
	 * 
	 * @param position
	 * @return
	 */
	public String getFragmentTag(int position) {
		if (tags != null && position < tags.length)
			return tags[position];
		else
			return "";
	}

	/**
	 * sets a RequirementListener, so TabController can ask for a fragment for
	 * given position to put it on
	 * 
	 * @param requirementListener
	 */
	public void setRequirementListener(FragmentRequires requirementListener) {
		this.requirementListener = requirementListener;
	}

	@Override
	public void onTabSelected(int position) {
		FragmentManager fm = act.getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		// Clear stack if there are sub fragments in it
		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}

		// Remove last visible fragment
		int old = tabs.getLastSelectedTabPosition();
		if (old != -1) {
			Fragment oldFragment = fm.findFragmentByTag(getFragmentTag(old));
			if (oldFragment != null)
				ft.detach(oldFragment);
		}

		// Attach new fragment
		Fragment currentFragment = fm
				.findFragmentByTag(getFragmentTag(position));
		if (currentFragment != null) {
			// Find already created fragment
			ft.attach(currentFragment);
		} else {
			// Create fragment in first time
			AddFragment(ft, position);
		}

		ft.commit();
		act.getSupportFragmentManager().executePendingTransactions();
	}

	@Override
	public void onTabReselected(int position) {
		FragmentManager fm = act.getSupportFragmentManager();

		// Clear stack if there are sub fragments in it
		for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
			fm.popBackStack();
		}
	}

	private void AddFragment(FragmentTransaction ft, int position) {
		if (getRequirementListener() != null) {
			Fragment frg = getRequirementListener().onRequirement(position);
			ft.add(contentId, frg, getFragmentTag(position));
		}
	}

	public FragmentRequires getRequirementListener() {
		return requirementListener;
	}

}
