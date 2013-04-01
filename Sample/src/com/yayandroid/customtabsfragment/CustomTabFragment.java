package com.yayandroid.customtabsfragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CustomTabFragment extends Fragment {

	private static final String KEY_CONTENT = "CustomTabFragment";
	private final int[] COLOR = new int[] { Color.BLACK, Color.BLUE,
			Color.GREEN, Color.DKGRAY };
	private int contentId;

	public static CustomTabFragment newInstance(int content) {
		CustomTabFragment fragment = new CustomTabFragment();
		fragment.contentId = content;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if ((savedInstanceState != null)
				&& savedInstanceState.containsKey(KEY_CONTENT)) {
			contentId = savedInstanceState.getInt(KEY_CONTENT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_item, container, false);

		if (contentId < COLOR.length) {
			view.setBackgroundColor(COLOR[contentId]);

			TextView tv = (TextView) view.findViewById(R.id.fragmentText);
			tv.setText(String.valueOf(contentId));
		} else {
			view.setBackgroundColor(Color.CYAN);

			TextView tv = (TextView) view.findViewById(R.id.fragmentText);
			tv.setText("Sub Fragment");
		}

		Button btn = (Button) view.findViewById(R.id.fragmentButton);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AddSubFragment();
			}

		});

		Button btn2 = (Button) view.findViewById(R.id.fragmentButton2);
		btn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent act = new Intent(getActivity(), SampleActivity.class);
				getActivity().startActivity(act);
			}

		});

		return view;
	}

	/**
	 * Add a sub fragment with custom animation and put it to backStack, so user
	 * can navigate to former with back button
	 */
	public void AddSubFragment() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		fm.beginTransaction();
		Fragment fragTwo = CustomTabFragment.newInstance(5);
		ft.setCustomAnimations(R.anim.slide_in_left,
				R.anim.slide_out_left, R.anim.slide_in_right,
				R.anim.slide_out_right);
		ft.replace(R.id.tabContent, fragTwo);
		ft.addToBackStack(String.valueOf(5));
		ft.commit();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_CONTENT, contentId);
	}

}
