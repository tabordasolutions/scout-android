/*|~^~|Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 |~^~|All rights reserved.
 |~^~|
 |~^~|Redistribution and use in source and binary forms, with or without
 |~^~|modification, are permitted provided that the following conditions are met:
 |~^~|
 |~^~|-1. Redistributions of source code must retain the above copyright notice, this
 |~^~|ist of conditions and the following disclaimer.
 |~^~|
 |~^~|-2. Redistributions in binary form must reproduce the above copyright notice,
 |~^~|this list of conditions and the following disclaimer in the documentation
 |~^~|and/or other materials provided with the distribution.
 |~^~|
 |~^~|-3. Neither the name of the copyright holder nor the names of its contributors
 |~^~|may be used to endorse or promote products derived from this software without
 |~^~|specific prior written permission.
 |~^~|
 |~^~|THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 |~^~|AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 |~^~|IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 |~^~|DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 |~^~|FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 |~^~|DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 |~^~|SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 |~^~|CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 |~^~|OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 |~^~|OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*/
/**
 *
 */
package scout.edu.mit.ll.nics.android.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.PropertyType;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;
import scout.edu.mit.ll.nics.android.utils.FormType;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class ReportOnConditionActionFragment extends Fragment
{

	//private FormFragment mFormFragment;
	private LinearLayout mFormButtons;
	private Button mViewROCButton;
	private Button mNewROCButton;
	private Button mUpdateROCButton;
	private Button mFinalROCButton;

	private View mRootView;
	private DataManager mDataManager;
	//private long mReportId;

	//private DamageReportPayload mCurrentPayload;
	//private DamageReportData mCurrentData;
	//private Menu mMenu;
	//private boolean mHideCopy;
	private MainActivity mContext;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mContext = (MainActivity) getActivity();
		//mReportId = -1;
		//isDraft  = true;
		setHasOptionsMenu(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		if (container == null)
		{
			return null;
		}

		Log.e("tag", "hullo - " + container);

		mRootView = inflater.inflate(R.layout.fragment_reportonconditionaction, container, false);
		//mRootView = container.findViewById(R.id.reportOnConditionActionFragment);

		mViewROCButton = (Button) mRootView.findViewById(R.id.reportOnConditionViewButton);
		mNewROCButton = (Button) mRootView.findViewById(R.id.reportOnConditionNewButton);
		mUpdateROCButton = (Button) mRootView.findViewById(R.id.reportOnConditionUpdateButton);
		mFinalROCButton = (Button) mRootView.findViewById(R.id.reportOnConditionFinalButton);


		mFormButtons = (LinearLayout) mRootView.findViewById(R.id.reportOnConditionActionButtons);

		mViewROCButton.setOnClickListener(onActionButtonClick);
		mNewROCButton.setOnClickListener(onActionButtonClick);
		mUpdateROCButton.setOnClickListener(onActionButtonClick);
		mFinalROCButton.setOnClickListener(onActionButtonClick);

		mDataManager = DataManager.getInstance(getActivity());

		return mRootView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.damagereport_copy, menu);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		((ViewGroup) mRootView.getParent()).removeView(mRootView);
	}

	private OnClickListener onActionButtonClick = new OnClickListener()
	{

		private AlertDialog mAlertDialog;

		@Override
		public void onClick(View v)
		{
			switch (v.getId())
			{
				case R.id.reportOnConditionViewButton:
					//TODO: navigate to ROC viewer
					//mContext.onNavigationItemSelected(NavigationOptions.DAMAGESURVEY.getValue(), -2);
					break;
				case R.id.reportOnConditionNewButton:
					//TODO: navigate to ROC viewer
					//mContext.onNavigationItemSelected(NavigationOptions.DAMAGESURVEY.getValue(), -2);
					break;
				case R.id.reportOnConditionUpdateButton:
					//TODO: navigate to ROC viewer
					//mContext.onNavigationItemSelected(NavigationOptions.DAMAGESURVEY.getValue(), -2);
					break;
				case R.id.reportOnConditionFinalButton:
					//TODO: navigate to ROC viewer
					//mContext.onNavigationItemSelected(NavigationOptions.DAMAGESURVEY.getValue(), -2);
					break;
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}
}
