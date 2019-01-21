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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;

public class ReportOnConditionViewFragment extends Fragment
{

	private TextView section1Header;
	private LinearLayout section1Layout;

	private AutoCompleteTextView testAutoCompleteTextView;

	private LinearLayout testThreatLayout;

	private LinearLayout emptyFocusableLayout;


	private ImageButton testAddThreatButton;


	//private FormFragment mFormFragment;
	//private LinearLayout mFormButtons;

	//OES828 TODO - edit and view booleans for simple reports

	//private Button mViewROCButton;
	//private Button mNewROCButton;
	//private Button mUpdateROCButton;
	//private Button mFinalROCButton;

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

		mRootView = inflater.inflate(R.layout.fragment_reportonconditionview, container, false);
		//mRootView = container.findViewById(R.id.reportOnConditionActionFragment);

		//mViewROCButton = (Button) mRootView.findViewById(R.id.reportOnConditionViewButton);
		//mNewROCButton = (Button) mRootView.findViewById(R.id.reportOnConditionNewButton);
		//mUpdateROCButton = (Button) mRootView.findViewById(R.id.reportOnConditionUpdateButton);
		//mFinalROCButton = (Button) mRootView.findViewById(R.id.reportOnConditionFinalButton);


		//mFormButtons = (LinearLayout) mRootView.findViewById(R.id.reportOnConditionActionButtons);

		//mViewROCButton.setOnClickListener(onActionButtonClick);
		//mNewROCButton.setOnClickListener(onActionButtonClick);
		//mUpdateROCButton.setOnClickListener(onActionButtonClick);
		//mFinalROCButton.setOnClickListener(onActionButtonClick);

		section1Layout = (LinearLayout) mRootView.findViewById(R.id.rocSection1);
		section1Header = (TextView) mRootView.findViewById(R.id.rocSection1Header);
		section1Header.setOnClickListener(toggleCollapsibleSection );

		mDataManager = DataManager.getInstance(getActivity());

		testThreatLayout = (LinearLayout) mRootView.findViewById(R.id.rocThreatsSection);
		addNewThreatItem(true);

		testAddThreatButton = (ImageButton) mRootView.findViewById(R.id.rocThreatNewSection);
		testAddThreatButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick (View v)
			{
				addNewThreatItem(false);
			}
		});


		testAutoCompleteTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.testAutoComplete);
		// Use android.R.layout.simple_dropdown_item_1line for single-line autocomplete
		//
		testAutoCompleteTextView.setAdapter(newAutoCompleteAdapter(testAutoCompleteArray));

		testAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View view, boolean hasFocus)
			{
				if(hasFocus)
				{
					testAutoCompleteTextView.showDropDown();
				}
			}
		});


		//FIXME - this doesn't work to show autocomplete when the user erases all text...
		testAutoCompleteTextView.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after)
			{
				Log.e("hullo","autoComplete Text onTextChanged( + " + s + ", " + start + ", " + count + ", " +  after + ")");
				testAutoCompleteTextView.showDropDown();
			}

			@Override
			public void afterTextChanged(Editable e) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
		});


		emptyFocusableLayout = (LinearLayout) mRootView.findViewById(R.id.rocEmptyFocusableLayout);


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

		inflater.inflate(R.menu.generalmessage_copy, menu);
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();

		((ViewGroup) mRootView.getParent()).removeView(mRootView);
	}

/*	private OnClickListener onActionButtonClick = new OnClickListener()
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
	};*/

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}


	public OnClickListener toggleCollapsibleSection = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			if(v == section1Header)
			{
				boolean collapse = section1Layout.getVisibility() == View.VISIBLE;
				section1Layout.setVisibility(collapse ? View.GONE : View.VISIBLE);
				section1Header.setCompoundDrawablesWithIntrinsicBounds(0, 0, collapse ? R.drawable.down_arrow : R.drawable.up_arrow, 0);



				//TODO update the downward facing open icon (maybe rotate it, or just swap it)
			}
		}
	};

	private String[] testAutoCompleteArray =
			{
					"canned message 1",
					"canned message 2",
					"canned message 3. This is an example of a really long message that won't fit on one line, so it may be truncated or it may wrap around. 1",
					"canned message 3. This is an example of a really long message that won't fit on one line, so it may be truncated or it may wrap around. 2",
					"canned message 4. This is an example of a really long message that won't fit on one line, so it may be truncated or it may wrap around. 1"
			};
	private ArrayAdapter<String> testAutoCompleteArrayAdapter;


	// Adds a new threat item to the linearlayout, called every time a user presses the "+" button.
	private void addNewThreatItem(boolean first)
	{
		LinearLayout newLayout = new LinearLayout(mContext);
		newLayout.setOrientation(LinearLayout.HORIZONTAL);
		LinearLayout.LayoutParams newLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		newLayout.setLayoutParams(newLayoutParams);

		//------------------------------------------------------------------------------------
		// Text View
		//------------------------------------------------------------------------------------

		AutoCompleteTextView textView = new AutoCompleteTextView(mContext);
		LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT, 3.0f);
		textView.setLayoutParams(textViewParams);

		// Setting up the autocomplete
		textView.setAdapter(newAutoCompleteAdapter(testAutoCompleteArray));


		// Make the dropdown view show up when the textview is initially focused
		textView.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View view, boolean hasFocus)
			{
				if(hasFocus)
				{
					((AutoCompleteTextView)view).showDropDown();
				}
			}
		});




		//------------------------------------------------------------------------------------
		// Image Button
		//------------------------------------------------------------------------------------

		ImageButton button = new ImageButton(mContext);
		button.setImageResource(R.drawable.fire_origin2);

		// Make the button remove the view when clicked
		button.setOnClickListener(new OnClickListener() {
				  @Override
				  public void onClick (View v)
				  {
					  // Getting the LinearLayout that holds the AutoCompleteTextView and the ImageButton
					  LinearLayout containingLayout = (LinearLayout) v.getParent();

					  // Request focus an invisible dummy layout
					  // Otherwise, if the layout we're removing is focused
					  // (or if the textview inside that layout is focused)
					  // The focus can jump around randomly, and if the focus lands on
					  // an AutoCompleteTextView, an autocomplete dialog may be shown instantaneously
					  // This leads to confusing UX, so just focus on the add new section button.
					  if(v.isFocused() || containingLayout.isFocused())
					  {
						  emptyFocusableLayout.requestFocus();
					  }

					  testThreatLayout.removeView(containingLayout);
				  }
			  }
		);

		newLayout.addView(textView);
		newLayout.addView(button);




		testThreatLayout.addView(newLayout);
	}

	private void removeThreatItem(int index)
	{
		// TODO - don't remove if we only have one item.
	}

	private ArrayAdapter<String> newAutoCompleteAdapter(String[] autoCompleteOptions)
	{
		return new ArrayAdapter<String>(mContext, R.layout.auto_complete_list_item, R.id.item, autoCompleteOptions);
	}



}
