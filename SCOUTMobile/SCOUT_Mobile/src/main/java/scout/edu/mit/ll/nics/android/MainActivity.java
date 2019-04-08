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
package scout.edu.mit.ll.nics.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.gson.Gson;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.UserData;
import scout.edu.mit.ll.nics.android.api.payload.CollabroomPayload;
import scout.edu.mit.ll.nics.android.api.payload.OrganizationPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.fragments.ChatListFragment;
import scout.edu.mit.ll.nics.android.fragments.MapMarkupLocationPickerFragment;
import scout.edu.mit.ll.nics.android.roc.ReportOnConditionActionFragment;
import scout.edu.mit.ll.nics.android.fragments.FormFragment;
import scout.edu.mit.ll.nics.android.fragments.GeneralMessageFragment;
import scout.edu.mit.ll.nics.android.fragments.MapMarkupFragment;
import scout.edu.mit.ll.nics.android.fragments.OverviewFragment;
import scout.edu.mit.ll.nics.android.roc.ReportOnConditionFragment;
import scout.edu.mit.ll.nics.android.fragments.SimpleReportListFragment;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.Constants.NavigationOptions;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener
{    //, UncaughtExceptionHandler {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final String STATE_BACK_STACK = "back_stack";
	private static final String STATE_REPORT_OPENED_FROM_MAP = "report_opened_from_map";
	private static final String STATE_IS_EDIT_SIMPLE_REPORT = "is_edit_simple_report";
	private static final String STATE_IS_VIEW_SIMPLE_REPORT = "is_view_simple_report";

	private int mLastPosition = NavigationOptions.OVERVIEW.getValue();
	private FragmentManager mFragmentManager;
	private Stack<Integer> mBackStack;

	private OverviewFragment mOverviewFragment;
	public MapMarkupFragment mMapMarkupFragment;
	private MapMarkupLocationPickerFragment mMapMarkupLocationPickerFragment;

	private SimpleReportListFragment mSimpleReportListFragment;
	public GeneralMessageFragment mSimpleReportFragment;

	private ReportOnConditionActionFragment mReportOnConditionActionFragment;
	private ReportOnConditionFragment mReportOnConditionFragment;
	//OES828 TODO - Add ReportOnConditionActionFragment

	//	private ChatFragment mChatFragment;
	private ChatListFragment mChatFragment;

	private FormFragment mUserInfoFragment;

	private static Context mContext;

	private boolean mIsBackKey = false;
	public boolean mEditSimpleReport = false;
	public boolean mViewSimpleReport = false;
	//OES828 TODO - edit and view booleans for ROCs
	public boolean mViewMapLocationPicker = false;

	public boolean mViewReportOnConditionAction = false;

	private DataManager mDataManager;

	public String mOpenedSimpleReportPayload;
	public long mOpenedSimpleReportId;
	//OES828 TODO - store the current ROC payload and ROC ID

	private boolean mMapMarkupOpenTablet = false;

	private String[] navDropdownOptions;

	protected boolean mPreventNavigation = false;

	private TextView mBreadcrumbTextView;
	private String mCurBreadcrumbText;
	private boolean mReportOpenedFromMap;
	private String[] mOrgArray;

	public static MenuItem LowDataModeIcon;


	static private Handler invalidSessionIDHandler;
	// If set to true, we have triggered an invalid session id warning
	static boolean invalidSessionID = false;


	// If set to true, the dialog is currently visible, so don't overlay another
	static boolean invalidSessionIDDialogVisible = false;
	// If set to true, invalid session id warning was triggered from a SR upload
	static boolean invalidSessionIDFromSR = false;
	// If set to true, we are currently attempting to re-authenticate, don't show another dialog
	static boolean invalidSessionIDReauthenticating = false;


	// Contains the actual code that is executed upon invalid session ID
	static Runnable invalidSessionIDRunnable;
	// Counts how many times the runnable has been invoked
	// This allows us to only run the network validation X times
	static int invalidSessionIDCounter = 0;
	// How often to check for invalid session ID (in msec)
	static final int invalidSessionIDDelay = 1000;


	private String[] navOptionsAsArray ()
	{
		NavigationOptions[] states = NavigationOptions.values();
		String[] names = new String[states.length];

		for (int i = 0; i < states.length; i++)
		{
			names[i] = states[i].getLabel(this);
		}

		return names;
	}

	public static Context getAppContext ()
	{
		return mContext;
	}

	@Override
	protected void onCreate (Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		mContext = this;
		mDataManager = DataManager.getInstance(getApplicationContext(), this);

		if (mDataManager.getTabletLayoutOn())
		{
			setContentView(R.layout.activity_main_tablet);
		}
		else
		{
			setContentView(R.layout.activity_main);
		}

//		Thread.setDefaultUncaughtExceptionHandler(this);

		if (mDataManager.isMDTEnabled())
		{
			mDataManager.getLocationSource();
		}

		mFragmentManager = getSupportFragmentManager();
		mBackStack = new Stack<Integer>();

		// Set up the action bar to show a dropdown list.
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setTitle(R.string.app_name);
		navDropdownOptions = navOptionsAsArray();

//		actionBar.setBackgroundDrawable(new ColorDrawable(0xffffffff));


		Intent intent = getIntent();

		if (intent != null && savedInstanceState == null)
		{
			int position = intent.getIntExtra(STATE_SELECTED_NAVIGATION_ITEM, NavigationOptions.OVERVIEW.getValue());
			if (mDataManager.isLoggedIn())
			{
				onNavigationItemSelected(position, -1);
				mLastPosition = position;
			}
			else
			{
				onNavigationItemSelected(NavigationOptions.LOGOUT.getValue(), -1);
				mLastPosition = NavigationOptions.LOGOUT.getValue();
			}

			mOpenedSimpleReportPayload = intent.getStringExtra("sr_edit_json");
			if (mOpenedSimpleReportPayload != null)
			{
				mViewSimpleReport = true;
				mEditSimpleReport = false;
			}

			//OES828 TODO - Create ROC Payload, mark that we are viewing and not editing

			if (position != NavigationOptions.OVERVIEW.getValue())
			{
				mBackStack = new Stack<Integer>();
				mBackStack.add(NavigationOptions.OVERVIEW.getValue());
			}

			if (intent.getBooleanExtra("showOrgSelector", false))
			{
				showOrgSelector();
			}
		}

		if (invalidSessionIDHandler == null)
		{
			invalidSessionIDHandler = new Handler();
			invalidSessionIDRunnable = new Runnable()
			{
				@Override
				public void run ()
				{
					invalidSessionIDCounter++;
					// Only check against the server every 5 times
					// This is meant to reduce the amount of network requests
					if (!invalidSessionID && invalidSessionIDCounter >= 5)
					{
						invalidSessionIDCounter = 0;
						RestClient.validateSessionID();
					}


					// Enable this to monitor status every time this runs (every 1 second)
					//Log.e("USIDDEFECT","ISID: " + invalidSessionID + ", dialogVisible: " + invalidSessionIDDialogVisible + ", reauthing: " + invalidSessionIDReauthenticating);


					if (invalidSessionID && !invalidSessionIDDialogVisible && !invalidSessionIDReauthenticating)
					{
						invalidSessionIDDialogVisible = true;
						MainActivity.this.showInvalidSessionIDDialog(invalidSessionIDFromSR);
						// This dialog will either re-login or exit, but don't show another unless it's triggered again
						invalidSessionID = false;
						invalidSessionIDFromSR = false;
					}
					else
					{
						invalidSessionIDHandler.postDelayed(this, invalidSessionIDDelay);
					}
				}
			};
		}
	}

	private void showOrgSelector ()
	{
		Builder mDialogBuilder = new AlertDialog.Builder(this);
		mDialogBuilder.setTitle(R.string.select_an_organization);
		mDialogBuilder.setMessage(null);
		mDialogBuilder.setPositiveButton(null, null);
		HashMap<String, OrganizationPayload> orgMap = mDataManager.getOrganizations();
		if (orgMap != null)
		{
			mOrgArray = new String[orgMap.size()];
			orgMap.keySet().toArray(mOrgArray);
			Arrays.sort(mOrgArray);
			mDialogBuilder.setItems(mOrgArray, orgSelected);
			mDialogBuilder.create().show();
		}
	}

	DialogInterface.OnClickListener orgSelected = new DialogInterface.OnClickListener()
	{
		@Override
		public void onClick (DialogInterface dialog, int which)
		{
			OrganizationPayload selectedOrg = mDataManager.getOrganizations().get(mOrgArray[which]);

			mDataManager.setCurrentOrganization(selectedOrg);

			TextView orgTextView = (TextView) findViewById(R.id.selectedOrg);

			if (orgTextView != null)
			{
				orgTextView.setText(selectedOrg.getName());
			}
			Log.e(Constants.nics_DEBUG_ANDROID_TAG, selectedOrg.toJsonString());

			RestClient.switchOrgs(selectedOrg.getOrgid());
		}
	};

	/**
	 * Backward-compatible version of {@link ActionBar#getThemedContext()} that
	 * simply returns the {@link android.app.Activity} if
	 * <code>getThemedContext</code> is unavailable.
	 */
	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	private Context getActionBarThemedContextCompat ()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
		{
			return getSupportActionBar().getThemedContext();
		}
		else
		{
			return this;
		}
	}

	@Override
	protected void onResume ()
	{
		super.onResume();

		if (mDataManager.isMDTEnabled())
		{
			mDataManager.getLocationSource();
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && !((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE))
		{
			mBreadcrumbTextView = (TextView) findViewById(R.id.breadcrumbTextView);
			mBreadcrumbTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			mBreadcrumbTextView.setGravity(Gravity.CENTER_HORIZONTAL);
			mBreadcrumbTextView.setTextSize(20);
		}
		else
		{
			TextView other = (TextView) findViewById(R.id.breadcrumbTextView);
			other.setText("");
			other.setHeight(0);
			mBreadcrumbTextView = new TextView(this);
			mBreadcrumbTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mBreadcrumbTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
			mBreadcrumbTextView.setTextSize(18);
			if (!mDataManager.getTabletLayoutOn())
			{
				getSupportActionBar().setCustomView(mBreadcrumbTextView);
				getSupportActionBar().setDisplayShowCustomEnabled(true);
			}
		}

		if (mLastPosition != NavigationOptions.OVERVIEW.getValue())
		{
			restoreViewTitle();

		}
		else
		{
			clearViewTitle();
		}

		DataManager.CustomCommand invalidSessionIDCommand;

		invalidSessionIDCommand = new DataManager.CustomCommand()
		{
			@Override
			public void performAction ()
			{
				Log.v("USIDDEFECT", "session id command action invoked");
				MainActivity.invalidSessionID = true;
			}
		};

		mDataManager.setInvalidSessionsIDCommand(invalidSessionIDCommand);


		DataManager.CustomCommand invalidSRSessionIDCommand;

		invalidSRSessionIDCommand = new DataManager.CustomCommand()
		{
			@Override
			public void performAction ()
			{
				Log.v("USIDDEFECT", "SR session id command action invoked");
				MainActivity.invalidSessionIDFromSR = true;
				MainActivity.invalidSessionID = true;
			}

		};

		mDataManager.setInvalidSRSessionsIDCommand(invalidSRSessionIDCommand);


		invalidSessionIDHandler.postDelayed(invalidSessionIDRunnable, invalidSessionIDDelay);
	}

	// Restores the view title using current fragments to deduce title
	public void restoreViewTitle ()
	{
		mBreadcrumbTextView.setText(mCurBreadcrumbText);
	}

	public void setViewTitle (String title, boolean requiresRoom)
	{
		if (mBreadcrumbTextView != null)
		{
			CollabroomPayload collabRoom = mDataManager.getSelectedCollabRoom();

			// IF we haven't selected a room
			if (collabRoom.getName().equals(getString(R.string.no_selection)) && requiresRoom)
			{
				mCurBreadcrumbText = "No Room Selected";
			}
			else
			{
				mCurBreadcrumbText = title;
			}

			mBreadcrumbTextView.setText(mCurBreadcrumbText);
			if (!mCurBreadcrumbText.isEmpty())
			{
				mBreadcrumbTextView.setVisibility(View.VISIBLE);
			}
		}
	}

	public void clearViewTitle ()
	{
		mCurBreadcrumbText = "";
		setBreadcrumbText("");
		mBreadcrumbTextView.setVisibility(View.GONE);
	}

	@Override
	public void onRestoreInstanceState (Bundle savedInstanceState)
	{

		if (savedInstanceState.containsKey(STATE_BACK_STACK))
		{
			mBackStack = new Stack<Integer>();
			mBackStack.addAll(savedInstanceState.getIntegerArrayList(STATE_BACK_STACK));
		}

		if (savedInstanceState.containsKey(STATE_REPORT_OPENED_FROM_MAP))
		{
			mReportOpenedFromMap = savedInstanceState.getBoolean(STATE_REPORT_OPENED_FROM_MAP);
		}

		if (savedInstanceState.containsKey(STATE_IS_EDIT_SIMPLE_REPORT))
		{
			mEditSimpleReport = savedInstanceState.getBoolean(STATE_IS_EDIT_SIMPLE_REPORT);
		}

		if (savedInstanceState.containsKey(STATE_IS_VIEW_SIMPLE_REPORT))
		{
			mViewSimpleReport = savedInstanceState.getBoolean(STATE_IS_VIEW_SIMPLE_REPORT);
		}

		//OES828 TODO - retrieve the state booleans from a savedinstancestate

		if (savedInstanceState.containsKey("sr_edit_json"))
		{
			mOpenedSimpleReportPayload = savedInstanceState.getString("sr_edit_json");
		}

		if (savedInstanceState.containsKey("sr_edit_id"))
		{
			mOpenedSimpleReportId = savedInstanceState.getLong("sr_edit_id");
		}

		//OES828 TODO - retrieve the Currently active ROC from savedinstance

		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM))
		{
			mLastPosition = savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM);
			if (mDataManager.getTabletLayoutOn())
			{
				onNavigationItemSelected(NavigationOptions.OVERVIEW.getValue(), -1);
			}
			onNavigationItemSelected(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM), -1);
		}
	}

	@Override
	public void onSaveInstanceState (Bundle outState)
	{
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, mLastPosition);

		ArrayList<Integer> list = new ArrayList<Integer>(mBackStack);
		outState.putIntegerArrayList(STATE_BACK_STACK, list);

		outState.putBoolean(STATE_REPORT_OPENED_FROM_MAP, mReportOpenedFromMap);
		outState.putBoolean(STATE_IS_EDIT_SIMPLE_REPORT, mEditSimpleReport);
		outState.putBoolean(STATE_IS_VIEW_SIMPLE_REPORT, mViewSimpleReport);

		//OES828 TODO - Store the ROC state

		if (mSimpleReportFragment != null)
		{
			mOpenedSimpleReportPayload = mSimpleReportFragment.getPayload().toJsonString();
			mOpenedSimpleReportId = mSimpleReportFragment.getReportId();
		}

		//OES828 TODO - if ROC fragment is not null, store the ROC payload and ROC ID

		if (mOpenedSimpleReportPayload != null)
		{
			outState.putString("sr_edit_json", mOpenedSimpleReportPayload);
			outState.putLong("sr_edit_id", mOpenedSimpleReportId);
		}

		//OES828 TODO - if ROC fragment is not null, store the ROC payload and ROC ID
		// FIXME - what's the difference between the above two calls?
	}

	@Override
	public boolean onCreateOptionsMenu (Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_logout, menu);
		LowDataModeIcon = menu.findItem(R.id.action_low_data_mode);
		if (mDataManager.getLowDataMode())
		{
			LowDataModeIcon.setIcon(R.drawable.stat_sys_r_signal_1_cdma);
		}
		else
		{
			LowDataModeIcon.setIcon(R.drawable.stat_sys_r_signal_4_cdma);
		}

		return true;
	}

	// Copies a simple report's details to a new simple report for submission
	public void addSimpleReportToDetailView (boolean isCopy)
	{
		if (mSimpleReportFragment == null)
		{
			mSimpleReportFragment = new GeneralMessageFragment();
		}
		if (mDataManager.getTabletLayoutOn() && mMapMarkupOpenTablet)
		{
			animateFragmentReplace(R.id.container2, mSimpleReportFragment, false);
		}
		else
		{
			animateFragmentReplace(R.id.container, mSimpleReportFragment, false);
		}
		mFragmentManager.executePendingTransactions();

		if (isCopy)
		{
			mSimpleReportFragment.populate(mSimpleReportFragment.getFormString(), -1L, true);
		}
		else
		{
			mSimpleReportFragment.populate(mDataManager.getIncidentInfoJson(), -1L, true);
		}
		mOpenedSimpleReportPayload = mSimpleReportFragment.getPayload().toJsonString();
		mOpenedSimpleReportId = -1;

		mEditSimpleReport = true;
		mViewSimpleReport = false;
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item)
	{
		// Handle item selection
		switch (item.getItemId())
		{
			case android.R.id.home:
				onNavigationItemSelected(NavigationOptions.OVERVIEW.getValue(), -1);
				break;
			case R.id.action_settings:
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
				if (mDataManager.getSelectedCollabRoom().getCollabRoomId() == -1)
				{
					intent.putExtra("hideGarCollab", true);
				}

				intent.putExtra("currentServer", mDataManager.getServer());
				startActivityForResult(intent, 1001);
				break;

			case R.id.action_logout:
				onNavigationItemSelected(NavigationOptions.LOGOUT.getValue(), -1);
				break;

			case R.id.action_help:
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://public.nics.ll.mit.edu/nicshelp/"));
				startActivity(browserIntent);
				break;

			case R.id.action_about:
				intent = new Intent(MainActivity.this, AboutActivity.class);
				startActivity(intent);
				break;
			case R.id.addSimpleReportOption:
				addSimpleReportToDetailView(false);
				break;
			case R.id.markAllSrAsRead:
				mSimpleReportListFragment.MarkAllMessagesAsRead();
				break;
			case R.id.copyGeneralMessageOption:
				addSimpleReportToDetailView(true);
				break;
			case R.id.refreshSimpleReportOption:
				mDataManager.requestSimpleReports();
				break;
			case R.id.refreshChatMessagesOption:
				mDataManager.requestChatHistory(mDataManager.getActiveIncidentId(), mDataManager.getSelectedCollabRoom().getCollabRoomId());
				break;
			case R.id.refreshMapOption:
				mDataManager.requestMarkupUpdate();
				break;
			case R.id.action_switch_orgs:
				showOrgSelector();
				break;
			case R.id.action_low_data_mode:
				mDataManager.setLowDataMode(!mDataManager.getLowDataMode());
				LowDataModeIcon.setIcon(mDataManager.getLowDataMode() ? R.drawable.stat_sys_r_signal_1_cdma : R.drawable.stat_sys_r_signal_4_cdma);
				break;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onActivityResult (int requestCode, int resultCode, Intent data)
	{
		if (requestCode == 500)
		{
			BluetoothLRF.getInstance(this, null).findBT();
		}

		if (requestCode == 1001 && data != null)
		{
			if (data.getBooleanExtra("logoutAndClear", false))
			{
				mDataManager.setCurrentIncidentData(null, -1, "");
				mDataManager.setSelectedCollabRoom(null);
				onNavigationItemSelected(NavigationOptions.LOGOUT.getValue(), 0);
			}
		}

		if (mEditSimpleReport || mViewSimpleReport)
		{
			if (mSimpleReportFragment == null && mOpenedSimpleReportPayload != null)
			{
				SimpleReportPayload payload = new Gson().fromJson(mOpenedSimpleReportPayload, SimpleReportPayload.class);
				payload.parse();

				openSimpleReport(payload, mEditSimpleReport);
			}

			if (mSimpleReportFragment != null)
			{
				mSimpleReportFragment.onActivityResult(requestCode, resultCode, data);
			}
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	public boolean onNavigationItemSelected (final int position, long id)
	{
		// When the given dropdown item is selected, show its contents in the container view

		boolean prompt = false;

		if (position == mLastPosition && id == -2)
		{
			if (mEditSimpleReport)
			{
				mEditSimpleReport = false;
			}
		}

		// Prompting the user that their report progress will be lost if they continue
		if (!isEditReport())
		{
			mPreventNavigation = false;
		}
		else if (position == mLastPosition && position == id)
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);


			String title = getString(R.string.exit_report_draft_dialog_title);
			String message = getString(R.string.exit_report_draft_dialog_msg);

			if (mEditSimpleReport)
			{
				prompt = true;
			}

			if (prompt)
			{
				builder.setTitle(title);
				builder.setMessage(message);


				// Exit without saving button
				builder.setNeutralButton(R.string.exit_report_draft_dialog_ok,
						new DialogInterface.OnClickListener()
						{
							public void onClick (DialogInterface dialog, int id)
							{
								mPreventNavigation = false;
								mEditSimpleReport = false;
								onNavigationItemSelected(position, id);
							}
						});

				// Save draft and close button
				builder.setPositiveButton(R.string.exit_report_draft_dialog_save, new DialogInterface.OnClickListener()
				{
					public void onClick (DialogInterface dialog, int id)
					{
						mPreventNavigation = false;
						mEditSimpleReport = false;
						onNavigationItemSelected(position, id);

						// This saves a simpleReport draft, but this code is only ever called from a simpleReport
						// SimpleReport draft applies to GeneralMessages (which have been renamed to Field Reports)
						mSimpleReportFragment.saveDraft();
					}
				});

				// Continue editing button
				builder.setNegativeButton(R.string.exit_report_draft_dialog_cancel, new DialogInterface.OnClickListener()
				{

					public void onClick (DialogInterface dialog, int id)
					{
						dialog.dismiss();
						mPreventNavigation = true;

						onNavigationItemSelected(mLastPosition, -1);
					}
				});

				final AlertDialog alertdialog = builder.create();

				// Changing the dialog text button text sizes
				alertdialog.setOnShowListener(new DialogInterface.OnShowListener()
				{
					@Override
					public void onShow (DialogInterface dialog)
					{
						Button btnPositive = alertdialog.getButton(Dialog.BUTTON_POSITIVE);
						btnPositive.setTextSize(13);
						Button btnNegative = alertdialog.getButton(Dialog.BUTTON_NEGATIVE);
						btnNegative.setTextSize(13);
						Button btnNeutral = alertdialog.getButton(Dialog.BUTTON_NEUTRAL);
						btnNeutral.setTextSize(13);

						// Setting the padding to center-justify the button text
						btnPositive.setPadding( 2, 1, 2, 10);
						btnNeutral.setPadding( 2, 1, 2, 10);
						btnNegative.setPadding( 2,1, 2, 10);
					}

				});

				alertdialog.show();
			}
		}


		// Creating the view fragment
		if (!prompt && !mPreventNavigation)
		{
			Fragment fragment = null;
			Fragment fragment2 = null;
			Fragment fragmentOverview = null;

			Fragment currentFragment = mFragmentManager.findFragmentById(R.id.container);
			Fragment currentFragment2 = mFragmentManager.findFragmentById(R.id.container2);

			if (mMapMarkupFragment != null && !mMapMarkupFragment.isVisible())
			{
				mMapMarkupFragment.removeMapFragment();
			}

			mDataManager.requestMarkupRepeating(mDataManager.getCollabroomDataRate(), false);
			mDataManager.requestChatMessagesRepeating(mDataManager.getCollabroomDataRate(), false);
			mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), false);
			mDataManager.requestReportOnConditionRepeating(mDataManager.getIncidentDataRate(), false);

			//OES828 TODO - request ROC reports repeating (use getIncidentDataRate() as update frequency)

			boolean showIncidentName = false;

			String viewTitle = null;
			// Whether or not a user must be in a room to access the full functionality of the current view
			// If so, the view title will display "No Room Selected" indicating that the user should select a room and
			// then come back to the current view
			// However, not all views require a room, so this boolean is used to denote which views require a room
			boolean requiresRoom = false;

			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "New view is: " + navDropdownOptions[position]);

			switch (NavigationOptions.values()[position])
			{
				case OVERVIEW:
					if (mOverviewFragment == null)
					{
						mOverviewFragment = new OverviewFragment();
					}
					fragmentOverview = mOverviewFragment;
					viewTitle = "";
					break;
				case GENERALMESSAGE:
					if (currentFragment != null && currentFragment2 != null)
					{
						if (currentFragment == mSimpleReportFragment && currentFragment2 == mSimpleReportListFragment)
						{
							break;
						}
					}
					if (mSimpleReportListFragment == null)
					{
						mSimpleReportListFragment = new SimpleReportListFragment();
					}
					fragment2 = mSimpleReportListFragment;
					viewTitle = getString(R.string.fragment_title_field_report);

					//if using tablet view and map is closed, set detail view on left side
					if (mDataManager.getTabletLayoutOn() && !mMapMarkupOpenTablet)
					{

						SimpleReportPayload payload = new Gson().fromJson(mOpenedSimpleReportPayload, SimpleReportPayload.class);
						if (payload == null)
						{
							payload = mDataManager.getLastSimpleReportPayload();
						}
						if (payload == null)
						{
							addSimpleReportToDetailView(false);
						}
						else
						{
							payload.parse();
							openSimpleReport(payload, mEditSimpleReport);
						}

						mBackStack.clear();
					}
					// If we're viewing or editing a simple report, populate the fragment with the data
					else if ((mViewSimpleReport || mEditSimpleReport) && mOpenedSimpleReportPayload != null)
					{
						SimpleReportPayload payload = new Gson().fromJson(mOpenedSimpleReportPayload, SimpleReportPayload.class);
						payload.parse();

						openSimpleReport(payload, this.mEditSimpleReport);
						if (!mMapMarkupOpenTablet && mDataManager.getTabletLayoutOn())
						{
							fragment = mSimpleReportListFragment;
							mBackStack.clear();
						}
						else
						{
							fragment2 = mSimpleReportFragment;
						}
					}
					mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), false);
					showIncidentName = true;

					break;

				case ROCACTIONFORM:
					//OES828 TODO - finish this section
					// TODO - if we're already on the destination page, don't do anything
					//--------------------------------------------------------------------
					// if the destination page fragment is null, create it
					//--------------------------------------------------------------------
					if (mReportOnConditionActionFragment == null)
					{
						mReportOnConditionActionFragment = new ReportOnConditionActionFragment();
					}
					//--------------------------------------------------------------------

					//--------------------------------------------------------------------
					// Set the view title
					//--------------------------------------------------------------------
					viewTitle = "Report on Condition";
					//--------------------------------------------------------------------

					//--------------------------------------------------------------------
					// Assign fragment to the destination page fragment
					//--------------------------------------------------------------------
					fragment = mReportOnConditionActionFragment;
					//--------------------------------------------------------------------

					//--------------------------------------------------------------------
					// Assign any data we need to set up the fragment (like fragment2, etc...)
					//--------------------------------------------------------------------
					// nothing for now
					//--------------------------------------------------------------------
					break;


				case ROCFORM:
					//OES828 TODO - finish this section
					// TODO - if we're already on the destination page, don't do anything
					// TODO - if the destination page fragment is null, create it
					// TODO - Set the view title
					// TODO - assign fragment to the destination page fragment
					// TODO - assign any data we need to set up the fragment (like fragment2, etc...)

					// TODO - if we're already on the destination page, don't do anything
					//--------------------------------------------------------------------
					// if the destination page fragment is null, create it
					//--------------------------------------------------------------------
					if(mReportOnConditionFragment == null)
					{
						mReportOnConditionFragment = new ReportOnConditionFragment();
					}
					//--------------------------------------------------------------------

					//--------------------------------------------------------------------
					// Set the view title
					//--------------------------------------------------------------------
					viewTitle = "Report on Condition";
					//--------------------------------------------------------------------

					//--------------------------------------------------------------------
					// assign fragment to the destination page fragment
					//--------------------------------------------------------------------
					fragment = mReportOnConditionFragment;
					//--------------------------------------------------------------------

					//--------------------------------------------------------------------
					// assign any data we need to set up the fragment (like fragment2, etc...)
					//--------------------------------------------------------------------
					// nothing for now
					// TODO - check if we are going to VIEW, or create a NEW, UPDATE, or FINAL ROC
					// TODO - Set the mReportOnConditionFragment's payload
					// that'll update it internally.
					//--------------------------------------------------------------------
					break;

					/*//OES828 TODO - if ROC fragment is not null, store the ROC payload and ROC ID
					//TODO create the ROC form fragment
					if (currentFragment != null && currentFragment2 != null)
					{
						// If we're already on the page, stop.
						//FIXME: add ROCFORM fragment
						//if(currentFragment == mSimpleReportFragment && currentFragment2 ==  mSimpleReportListFragment)
						//{
						//	break;
						//}
					}
					if (mReportOnConditionActionFragment == null)
					{
						mReportOnConditionActionFragment = new ReportOnConditionActionFragment();
					}

					//viewTitle = getString(R.string.fragment_title_field_report);

					//if using tablet view and map is closed
					//set detail view on left side
					fragment = mReportOnConditionActionFragment;*/
					/*if(mDataManager.getTabletLayoutOn() && !mMapMarkupOpenTablet)
					{

						SimpleReportPayload payload = new Gson().fromJson(mOpenedSimpleReportPayload, SimpleReportPayload.class);
						if(payload == null)
						{
							payload = mDataManager.getLastSimpleReportPayload();
						}
						if(payload == null)
						{
							addSimpleReportToDetailView(false);
						}
						else
						{
							payload.parse();
							openSimpleReport(payload, mEditSimpleReport);
						}

						mBackStack.clear();
					}
					else if((mViewSimpleReport || mEditSimpleReport) && mOpenedSimpleReportPayload != null)
					{
						SimpleReportPayload payload = new Gson().fromJson(mOpenedSimpleReportPayload, SimpleReportPayload.class);
						payload.parse();

						openSimpleReport(payload, this.mEditSimpleReport);
						if(!mMapMarkupOpenTablet && mDataManager.getTabletLayoutOn())
						{
							fragment = mSimpleReportListFragment;
							mBackStack.clear();
						}
						else
						{
							fragment2 = mSimpleReportFragment;
						}
					}*/
					//TODO - dataManager request roc forms
					//mDataManager.requestSimpleReportRepeating(mDataManager.getIncidentDataRate(), false);
					//showIncidentName = true;
				//	break;

				case MAPCOLLABORATION:
					if (mMapMarkupFragment == null)
					{
						mMapMarkupFragment = new MapMarkupFragment();
					}
					fragment = mMapMarkupFragment;
					viewTitle = getString(R.string.fragment_title_map);

					if (mDataManager.getTabletLayoutOn())
					{
						mMapMarkupOpenTablet = !mMapMarkupOpenTablet;
					}
					requiresRoom = true;
					break;
				case USERINFO:
					if (mUserInfoFragment == null)
					{
						Bundle args = new Bundle();
						args.putString(FormFragment.SCHEMA_FILE, "userinfo_schema.json");

						mUserInfoFragment = new FormFragment();
						mUserInfoFragment.setArguments(args);
					}
					fragment2 = mUserInfoFragment;
					break;
				case CHATLOG:
					if (currentFragment2 != null && mDataManager.getTabletLayoutOn())
					{
						if (currentFragment2 == mChatFragment)
						{
							break;
						}
					}
					if (mChatFragment == null)
					{
//						mChatFragment = new ChatFragment();
						mChatFragment = new ChatListFragment();
					}
					mDataManager.requestChatMessagesRepeating(mDataManager.getCollabroomDataRate(), true);
					fragment2 = mChatFragment;
					viewTitle = getString(R.string.fragment_title_chat);

					if (mDataManager.getTabletLayoutOn())
					{
						if (currentFragment != null && currentFragment != mMapMarkupFragment)
						{
							animateFragmentRemove(currentFragment, false);
							mFragmentManager.executePendingTransactions();
						}
					}
					requiresRoom = true;

					break;

				case SELECTINCIDENT:
					mBackStack.clear();
					Intent selectIncidentIntent = new Intent(MainActivity.this, LoginActivity.class);
					selectIncidentIntent.putExtra("hideSplash", true);
					selectIncidentIntent.putExtra("showIncidentSelect", true);
					startActivity(selectIncidentIntent);
					finish();
					break;

				case LOGOUT:
					mDataManager.setLoggedIn(false);
					mDataManager.requestLogout();
					mBackStack.clear();

					EncryptedPreferences userPreferences = new EncryptedPreferences(this.getSharedPreferences(Constants.nics_USER_PREFERENCES, 0));
					userPreferences.savePreferenceBoolean(Constants.nics_AUTO_LOGIN, false);

//					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//					intent.putExtra("hideSplash", true);
//			        startActivity(intent);

//					if(id == -1) {
//						finish();
//					} else {
					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
					intent.putExtra("hideSplash", true);
					startActivity(intent);
//					}
					break;

				default:
					break;
			}

			try
			{
				if (fragment != null)
				{
					if (mDataManager.getTabletLayoutOn())
					{
						if (mMapMarkupOpenTablet)
						{
							animateFragmentReplace(R.id.container, fragment, true);
						}
						else
						{
							if (fragment == mMapMarkupFragment)
							{
								checkForOpenReportsAndSwapContainer();
							}
						}
					}
					else
					{
						animateFragmentReplace(R.id.container, fragment, true);
					}
				}
				if (fragment2 != null)
				{
					if (mDataManager.getTabletLayoutOn())
					{
						animateFragmentReplace(R.id.container2, fragment2, true);
					}
					else
					{
						animateFragmentReplace(R.id.container, fragment2, true);
					}
				}
				if (fragmentOverview != null)
				{
					if (mDataManager.getTabletLayoutOn())
					{
						animateFragmentReplace(R.id.containerOverview, fragmentOverview, true);
					}
					else
					{
						animateFragmentReplace(R.id.container, fragmentOverview, true);
					}
				}
			}
			catch (Exception ex)
			{
				Log.e("nics", ex.toString());
			}

			// Have to wait for view to exist before populating data
			if (position == NavigationOptions.USERINFO.getValue())
			{
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable()
				{
					@Override
					public void run ()
					{
						UserData data = new UserData(mDataManager.getUserPayload());
						mUserInfoFragment.populate(data.toJsonString(), false);
					}
				}, 200);
			}

			if (position != NavigationOptions.MAPCOLLABORATION.getValue())
			{
				mDataManager.stopPollingMarkup();
			}

			if (position != mLastPosition && !mIsBackKey)
			{
				mBackStack.push(mLastPosition);
				mLastPosition = position;
			}
			else
			{
				mIsBackKey = false;
				mLastPosition = position;
			}

			//Set the view title depending on what view we are in
			if (mBreadcrumbTextView != null)
			{
				if (position == NavigationOptions.OVERVIEW.getValue())
				{
					clearViewTitle();
				}
				else
				{
					if (viewTitle != null)
					{
						setViewTitle(viewTitle, requiresRoom);
					}

					if (showIncidentName)
					{
						mDataManager.setCurrentNavigationView(NavigationOptions.values()[position].toString());
					}
				}
			}
		}
		return true;
	}

	void animateFragmentReplace (int container, Fragment frag, boolean stateLose)
	{
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
		transaction.replace(container, frag);
		if (stateLose)
		{
			transaction.commitAllowingStateLoss();
		}
		else
		{
			transaction.commit();
		}
	}

	void animateFragmentRemove (Fragment frag, boolean stateLose)
	{
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
		transaction.remove(frag);
		if (stateLose)
		{
			transaction.commitAllowingStateLoss();
		}
		else
		{
			transaction.commit();
		}
	}

	@Override
	public void onAttachFragment (Fragment fragment)
	{
		super.onAttachFragment(fragment);
		if (fragment.getTag() != null)
		{
			try
			{
				mLastPosition = Integer.valueOf(fragment.getTag());
			}
			catch (NumberFormatException e)
			{
			}
		}
	}

	@Override
	public void onBackPressed ()
	{

		if (mDataManager.getTabletLayoutOn())
		{
			tabletBackButtonPressed();
			return;
		}

		mIsBackKey = true;
		if (mViewMapLocationPicker)
		{
			mMapMarkupLocationPickerFragment.BackButtonPressed();
		}
		else if (mEditSimpleReport || mViewSimpleReport)
		{
			mViewSimpleReport = false;
			mOpenedSimpleReportPayload = null;
			mOpenedSimpleReportId = -1;
			if (mReportOpenedFromMap)
			{
				mReportOpenedFromMap = false;
				mBackStack.pop();
				onNavigationItemSelected(NavigationOptions.MAPCOLLABORATION.getValue(), -1);
			}
			else
			{
				onNavigationItemSelected(NavigationOptions.GENERALMESSAGE.getValue(), NavigationOptions.GENERALMESSAGE.getValue());
			}
		}

		//OES828 TODO - handle if we're backing out of a ROC form ( go back to the Action Menu)

		else if (mBackStack.size() > 0)
		{
			onNavigationItemSelected(mBackStack.pop(), -1);
		}
		else
		{
			if (mDataManager.isLoggedIn())
			{
				mLastPosition = NavigationOptions.OVERVIEW.getValue();
			}
			else
			{
				mLastPosition = NavigationOptions.LOGOUT.getValue();
			}
			mIsBackKey = false;
			moveTaskToBack(true);
		}
	}

	void tabletBackButtonPressed ()
	{
		Fragment currentFragment2 = mFragmentManager.findFragmentById(R.id.container2);

		if (currentFragment2 == mSimpleReportFragment)
		{
			mViewSimpleReport = false;
			mOpenedSimpleReportPayload = null;
			onNavigationItemSelected(NavigationOptions.GENERALMESSAGE.getValue(), -1);
		}
	}

	// Used for Tablet views only
	// This is in charge of opening reports for viewing
	void checkForOpenReportsAndSwapContainer ()
	{
		Fragment currentFragment = mFragmentManager.findFragmentById(R.id.container);
		Fragment currentFragment2 = mFragmentManager.findFragmentById(R.id.container2);

		if (currentFragment2 == mSimpleReportFragment && currentFragment2 != null)
		{
			animateFragmentRemove(currentFragment2, false);
			mFragmentManager.executePendingTransactions();

			animateFragmentReplace(R.id.container2, mSimpleReportListFragment, false);
			SimpleReportPayload payload = mSimpleReportFragment.getPayload();
			if (payload == null)
			{
				payload = mDataManager.getLastSimpleReportPayload();
			}
			openSimpleReport(payload, mEditSimpleReport);
		}
		else if (currentFragment2 == mSimpleReportListFragment && currentFragment2 != null)
		{
			if (mSimpleReportFragment == null)
			{
				mSimpleReportFragment = new GeneralMessageFragment();
			}

			SimpleReportPayload payload = mSimpleReportFragment.getPayload();
			if (payload == null)
			{
				payload = mDataManager.getLastSimpleReportPayload();
			}
			openSimpleReport(payload, mEditSimpleReport);
		}
		//OES828 TODO - check if currentFragment is ROC action window and ROC viewer
		else
		{
			if (currentFragment == mMapMarkupFragment)
			{
				animateFragmentRemove(currentFragment, false);
				mFragmentManager.executePendingTransactions();
			}
		}
	}


	// Populates the Simple Report fragment details with our current Simple Report data
	public void openSimpleReport (SimpleReportPayload simpleReportPayload, boolean editable)
	{
		if (mSimpleReportFragment == null)
		{
			mSimpleReportFragment = new GeneralMessageFragment();
		}

		if (!mDataManager.getTabletLayoutOn() || !mMapMarkupOpenTablet)
		{
			if (mSimpleReportFragment != mFragmentManager.findFragmentById(R.id.container))
			{
				animateFragmentReplace(R.id.container, mSimpleReportFragment, false);
				mFragmentManager.executePendingTransactions();
			}
		}
		else
		{
			animateFragmentReplace(R.id.container2, mSimpleReportFragment, false);
			mFragmentManager.executePendingTransactions();
		}

		simpleReportPayload.setDraft(editable);
		mOpenedSimpleReportPayload = simpleReportPayload.toJsonString();
		mSimpleReportFragment.setPayload(simpleReportPayload, editable);

		if (editable)
		{
			mEditSimpleReport = true;
			mViewSimpleReport = false;
		}
		else
		{
			mEditSimpleReport = false;
			mViewSimpleReport = true;
		}
	}

	//OES828 TODO - need a function to populate the ROC Viewer fragment and ROC Action Fragment with the current data / state

	@Override
	protected void onPause ()
	{
		super.onPause();

		if (invalidSessionIDHandler != null)
		{
			invalidSessionIDHandler.removeCallbacks(invalidSessionIDRunnable);
		}
	}

	@Override
	protected void onDestroy ()
	{
		super.onDestroy();

		mDataManager.stopPollingAlarms();
	}

	public void showLRFError (String name)
	{
		final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(getString(R.string.disconnected_device, name));
		alertDialog.setMessage(getString(R.string.disconnected_device_desc));
		alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener()
		{

			@Override
			public void onClick (DialogInterface dialog, int which)
			{
				alertDialog.dismiss();
			}
		});
		alertDialog.show();
	}

	public boolean isEditReport ()
	{
		return mEditSimpleReport;
	}

	public boolean isViewReport ()
	{
		return mViewSimpleReport;
	}

//	@Override
//	public void uncaughtException(Thread thread, Throwable ex) {
//		String error = ex.getClass() + " - " + ex.getLocalizedMessage() + "\n";
//		
//		StackTraceElement[] traceArray = ex.getStackTrace();
//		for(StackTraceElement element : traceArray) {
//			if(element.getClassName().contains("nics")) {
//				error += element.getClassName() + " - Line: " + element.getLineNumber() + "\n";
//			}
//		}
//
//		mDataManager.addPersonalHistory(error);
//		Log.e("nicsError", error);
//		ex.printStackTrace();
//		System.exit(1);
//
//	}

	public void setBreadcrumbText (CharSequence charSequence)
	{
		mBreadcrumbTextView.setText(charSequence);
	}


	public void showInvalidSessionIDDialog (boolean FRMessage)
	{
		Context context = this;
		Log.v("USIDDEFECT", "showInvalidSessionIDDialog invoked");
		//Showing a failure dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		String title = mContext.getString(R.string.invalid_session_title);

		String message = mContext.getString(R.string.invalid_session_body);
		if (FRMessage)
		{
			message = mContext.getString(R.string.invalid_session_body_FR);
		}

		builder.setTitle(title);
		builder.setMessage(message);
		// Relogin button
		builder.setPositiveButton(mContext.getString(R.string.invalid_session_relogin),
				new DialogInterface.OnClickListener()
				{
					public void onClick (DialogInterface dialog, int id)
					{
						dialog.dismiss();

						invalidSessionIDReauthenticating = true;
						// Create the command to perform after logging in
						DataManager.CustomCommand cmd = new DataManager.CustomCommand()
						{
							@Override
							public void performAction ()
							{
								// Need to call this AFTER we have successfully logged in.
								RestClient.setSendingSimpleReports(false);
								MainActivity.invalidSessionIDReauthenticating = false;
							}
						};
						mDataManager.requestRelogin(cmd);
						invalidSessionIDHandler.postDelayed(invalidSessionIDRunnable, invalidSessionIDDelay);
						invalidSessionIDDialogVisible = false;

					}
				});
		// Close app button
		builder.setNegativeButton(mContext.getString(R.string.invalid_session_okay),
				new DialogInterface.OnClickListener()
				{
					public void onClick (DialogInterface dialog, int id)
					{
						dialog.dismiss();
						// Must fully close the application, just calling finish() is insufficient as MainActivity won't be
						// properly instantiated next time. The following call does work, however.
						android.os.Process.killProcess(android.os.Process.myPid());
					}
				});

		final AlertDialog alertdialog = builder.create();

		alertdialog.getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL);

		// Changing the dialog text button text sizes
		alertdialog.setOnShowListener(new DialogInterface.OnShowListener()
		{
			@Override
			public void onShow (DialogInterface dialog)
			{
				Button btnPositive = alertdialog.getButton(Dialog.BUTTON_POSITIVE);
				btnPositive.setTextSize(13);
				Button btnNegative = alertdialog.getButton(Dialog.BUTTON_NEGATIVE);
				btnNegative.setTextSize(13);
			}

		});

		alertdialog.setCancelable(false);

		alertdialog.show();
	}

}
