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
package scout.edu.mit.ll.nics.android.roc;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import scout.edu.mit.ll.nics.android.MainActivity;
import scout.edu.mit.ll.nics.android.R;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.ReportOnConditionData;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.payload.IncidentPayload;
import scout.edu.mit.ll.nics.android.api.payload.IncidentTypePayload;
import scout.edu.mit.ll.nics.android.api.payload.OrganizationPayload;

public class ReportOnConditionFragment extends Fragment
{
	public static boolean isViewingRoc;
	public static boolean gotWeatherData;

	private ScrollView outermostScrollView;
	private SwipeRefreshLayout swipeRefreshLayout;

	private TextView incidentInfoHeader;
	private LinearLayout incidentInfoSection;
	private ImageView incidentInfoHeaderErrorView;

	private TextView rocIncidentInfoHeader;
	private LinearLayout rocIncidentInfoSection;
	private ImageView rocIncidentInfoHeaderErrorView;

	private TextView vegFireIncidentScopeHeader;
	private LinearLayout vegFireIncidentScopeSection;
	private ImageView vegFireIncidentScopeHeaderErrorView;

	private TextView weatherInfoHeader;
	private LinearLayout weatherInfoSection;
	private ImageView weatherInfoHeaderErrorView;

	private TextView threatsEvacsHeader;
	private LinearLayout threatsEvacsSection;
	private ImageView threatsEvacsHeaderErrorView;

	private TextView resourceCommitmentHeader;
	private LinearLayout resourceCommitmentSection;
	private ImageView resourceCommitmentHeaderErrorView;

	private TextView otherInfoHeader;
	private LinearLayout otherInfoSection;
	private ImageView otherInfoHeaderErrorView;

	private TextView emailHeader;
	private LinearLayout emailSection;
	private ImageView emailHeaderErrorView;

	private boolean shouldValidate;

	//================================================
	// ROC Form Info Fields
	//================================================
	TextView incidentNameLabel;
	ImageButton incidentNameInfoButton;
	AutoCompleteTextView incidentNameTextView;
	ProgressBar loadingIncidentInfoProgressBar;
	TextView messageTextView;
	Spinner reportTypeSpinner;
	LinearLayout reportTypeLayout;


	//================================================
	// Incident Info Fields
	//================================================
	AutoCompleteTextView incidentNumberTextView;
	ImageButton incidentNumberInfoButton;
	TextView incidentTypeTextView;
	ImageButton incidentTypeInfoButton;
	Spinner incidentTypeSpinner;
	// Decimal Degree Minutes Edit Text:
	EditText incidentLatitudeDegreesEditText;
	EditText incidentLatitudeMinutesEditText;
	EditText incidentLatitudeMinutesFractionEditText;
	EditText incidentLongitudeDegreesEditText;
	EditText incidentLongitudeMinutesEditText;
	EditText incidentLongitudeMinutesFractionEditText;
	ImageButton incidentLatLongLocationInfoButton;
	ImageButton incidentLocateButton;
	ImageButton incidentLocateInfoButton;
	ProgressBar incidentLocateProgressBar;
	AutoCompleteTextView incidentStateTextView;
	// State Variables
	List<IncidentTypeSpinnerAdapter.SpinnerItem<String>> incidentTypeSpinnerItems;
	Set<String> incidentTypeSelectedItems;
	//================================================
	// ROC Incident Info Fields
	//================================================
	Spinner rocInitialCountySpinner;
	AutoCompleteTextView rocAdditionalCountiesTextView;
	AutoCompleteTextView rocLocationTextView;
	ImageButton incidentLocationInfoButton;
	EditText rocLocationStreetEditText;
	EditText rocLocationCrossStreetEditText;
	EditText rocLocationNearestCommunity;
	EditText rocLocationDistanceFromNearestCommunityEditText;
	Spinner rocLocationDirectionFromNearestCommunitySpinner;
	Spinner rocDPASpinner;
	Spinner rocOwnershipSpinner;
	AutoCompleteTextView rocJurisdictionTextView;
	ImageButton incidentJurisdictionInfoButton;
	// The layout containing the start time and start date fields
	LinearLayout rocStartDateTimeLayout;
	TextView rocStartTimeTextView;
	TextView rocStartDateTextView;
	// Input Validation Error Views
	ImageView rocCountyErrorView;
	ImageView rocDPAErrorView;
	ImageView rocOwnershipErrorView;
	ImageView rocLocationDirectionFromNearestCommunityErrorView;
	//================================================
	// Vegetation Fire Incident Scope Fields
	//================================================
	EditText vegFireAcreageEditText;
	Spinner vegFireRateOfSpreadSpinner;
	TextView vegFireFuelTypeLabelTextView;
	CheckBox vegFireFuelTypeGrassCheckBox;
	CheckBox vegFireFuelTypeBrushCheckBox;
	CheckBox vegFireFuelTypeTimberCheckBox;
	CheckBox vegFireFuelTypeOakWoodlandCheckBox;
	CheckBox vegFireFuelTypeOtherCheckBox;
	TextView vegFireOtherFuelTypeLabelTextView;
	EditText vegFireOtherFuelTypeEditText;
	EditText vegFirePercentContainedEditText;
	// Input Validation Error Views
	ImageView vegFireRateOfSpreadErrorView;
	//================================================
	// Weather Information Fields
	//================================================
	EditText weatherTempEditText;
	EditText weatherRelativeHumidityEditText;
	EditText weatherWindSpeedEditText;
	//EditText weatherWindDirectionEditText;
	Spinner weatherWindDirectionSpinner;
	EditText weatherGustsEditText;
	// Input Validation Error Views
	ImageView weatherWindDirectionError;
	//================================================
	// Threats & Evacuations Fields
	//================================================
	Spinner threatsEvacsSpinner;
	TextView threatsEvacsLabelTextView;
	LinearLayout threatsEvacsListLinearLayout;
	ImageButton threatsEvacsAddButton;
	Spinner threatsStructuresSpinner;
	TextView threatsStructuresLabelTextView;
	LinearLayout threatsStructuresListLinearLayout;
	ImageButton threatsStructuresAddButton;
	Spinner threatsInfrastructureSpinner;
	TextView threatsInfrastructureLabelTextView;
	LinearLayout threatsInfrastructureListLinearLayout;
	ImageButton threatsInfrastructureAddButton;
	// Input Validation Error Views
	ImageView threatsEvacsErrorView;
	ImageView threatsStructuresErrorView;
	ImageView threatsInfrastructureErrorView;

	LinearLayout threatsEvacsListErrorView;
	LinearLayout threatsStructuresListErrorView;
	LinearLayout threatsInfrastructureListErrorView;

	//================================================
	// Resource Commitment Fields
	//================================================
	Spinner calFireIncidentSpinner;
	CheckBox calFireResourcesNoneCheckBox;
	CheckBox calFireResourcesAirCheckBox;
	CheckBox calFireResourcesGroundCheckBox;
	CheckBox calFireResourcesAirAndGroundCheckBox;
	CheckBox calFireResourcesAirAndGroundAugmentedCheckBox;
	CheckBox calFireResourcesAgencyRepOrderedCheckBox;
	CheckBox calFireResourcesAgencyRepAssignedCheckBox;
	CheckBox calFireResourcesContinuedCheckBox;
	CheckBox calFireResourcesSignificantAugmentationCheckBox;
	CheckBox calFireResourcesVlatOrderCheckBox;
	CheckBox calFireResourcesVlatAssignedCheckBox;
	CheckBox calFireResourcesNoDivertCheckBox;
	CheckBox calFireResourcesLatAssignedCheckBox;
	CheckBox calFireResourcesAllReleasedCheckBox;


	// Input Validation Error Views
	ImageView calFireIncidentErrorView;
	//================================================
	// Other Significant Info Fields
	//================================================
	LinearLayout otherInfoListLinearLayout;
	ImageButton otherInfoAddButton;
	//================================================
	// Email Fields
	//================================================
	TextView emailTextView;
	//================================================


	//================================================
	// ROC Submission Fields
	//================================================
	private LinearLayout submitButtonLayout;
	private Button submitButton;
	private Button cancelButton;
	//================================================


	private View mRootView;
	public static ReportOnConditionFragment rocFragment;
	private DataManager mDataManager;

	private MainActivity mContext;

	private String[] allIncidentNames;


	// These two member variables hold the current data for the form
	private ReportOnConditionData rocData;
	private IncidentPayload incidentData;
	// The data for the last ROC submitted for the incident (may be null)
	private ReportOnConditionData lastRocData;


	// Whether or not the new ROC form will create a new incident
	private boolean creatingNewIncident = false;


	// ROC Form Type Identifiers:
	private final int ROC_NONE = 0; // No ROC form
	private final int ROC_NEW = 1;// NEW ROC form
	private final int ROC_UPDATE = 2;// UPDATE ROC form
	private final int ROC_FINAL = 3; // FINAL ROC form
	private final int ROC_NON_FINAL = 5; // Either a NEW or an UPDATE


	// The report-type of the latest ROC submitted for an incident
	private int currentIncidentLastRocSubmitted = ROC_NONE;

	// The ROC report type that the current fragment state represents
	private int currentReportType = ROC_NONE;

	// Can be called when viewing an ROC
	// Executes a network request to pull the latest ROCs from the server
	// Then fills the form with the latest ROC data
	public void refreshRequested()
	{
		// NOTE: Call this whenever we invoke a programmatic refresh
		// swipeRefreshLayout.setRefreshing(true);

		Log.e("ROC","ROC - swipeRefreshLayout - onRefresh called");

		// ------------------------------------------------------------------------
		// Hiding all of the form fields
		// ------------------------------------------------------------------------
		reportTypeLayout.setVisibility(View.GONE);
		collapseAllSections();

		// Hiding all headers:
		incidentInfoHeader.setVisibility(View.GONE);
		rocIncidentInfoHeader.setVisibility(View.GONE);
		vegFireIncidentScopeHeader.setVisibility(View.GONE);
		weatherInfoHeader.setVisibility(View.GONE);
		threatsEvacsHeader.setVisibility(View.GONE);
		resourceCommitmentHeader.setVisibility(View.GONE);
		otherInfoHeader.setVisibility(View.GONE);
		emailHeader.setVisibility(View.GONE);


		// ------------------------------------------------------------------------
		// Showing the message and in-progress spinner
		// ------------------------------------------------------------------------
		messageTextView.setVisibility(View.VISIBLE);
		loadingIncidentInfoProgressBar.setVisibility(View.VISIBLE);
		messageTextView.setText("Fetching latest ROC data for incident...");



		new Handler().postDelayed(new Runnable() {
			@Override
			public void run ()
			{
				swipeRefreshLayout.setRefreshing(false);
			}
		}, 3000);


		// --------------------------------------------
				// request the ROC info for that incident, show the progress bar and a message letting the user know.
				messageTextView.setVisibility(View.VISIBLE);
				loadingIncidentInfoProgressBar.setVisibility(View.VISIBLE);
				messageTextView.setText("Fetching latest ROC data for incident...");

				// Setting it as an undefined value
				currentIncidentLastRocSubmitted = -1;




				// Building the success and error callbacks:
				Handler.Callback callback = new Handler.Callback() {
					@Override
					public boolean handleMessage (Message msg)
					{
						getActivity().runOnUiThread(new Runnable()
						{
							@Override
							public void run ()
							{
								// ------------------------------------------------------------------------
								// Hide the message and in-progress spinner
								// ------------------------------------------------------------------------
								messageTextView.setVisibility(View.GONE);
								loadingIncidentInfoProgressBar.setVisibility(View.GONE);

								setupFormForViewingMode(mDataManager.getActiveIncident());

								//ReportOnConditionData rocData = (incidentData != null) ? mDataManager.getLastReportOnCondition(incidentData.getIncidentId()) : null;
								//gotIncidentROCData(rocData);
							}
						});
						return true;
					}
				};

				mDataManager.pullLatestReportOnConditionForIncident(rocData.incidentname, callback, callback);
		// --------------------------------------------
	}


	// This method is responsible for setting up the form every time the Fragment is shown
	public void setupFragment()
	{
		rocFragment = this;
		gotWeatherData = false;

		// Ensure no member variables are null:
		setUpAllFields();


		// Setting up the pull-to-refresh functionality:
		swipeRefreshLayout.setEnabled(true);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh ()
			{
				refreshRequested();
			}
		});

		// By default, disable the swiperefresh layout
		swipeRefreshLayout.setEnabled(false);

		//------------------------------------------------------------------------------------------------
		// Retrieve all data from dataManager for field auto-fills, required fields, etc.
		allIncidentNames = mDataManager.getAllIncidentNames();
		IncidentPayload activeIncident = mDataManager.getActiveIncident();

		// This object is where we will store the final form data
		rocData = new ReportOnConditionData();

		//------------------------------------------------------------------------------------------------
		// Hide all fields:
		reportTypeLayout.setVisibility(View.GONE);
		collapseAllSections();

		// Hiding all headers:
		incidentInfoHeader.setVisibility(View.GONE);
		rocIncidentInfoHeader.setVisibility(View.GONE);
		vegFireIncidentScopeHeader.setVisibility(View.GONE);
		weatherInfoHeader.setVisibility(View.GONE);
		threatsEvacsHeader.setVisibility(View.GONE);
		resourceCommitmentHeader.setVisibility(View.GONE);
		otherInfoHeader.setVisibility(View.GONE);
		emailHeader.setVisibility(View.GONE);

		// Hide the submit button
		submitButton.setVisibility(View.INVISIBLE);

		//================================================================================================
		// If we're only viewing the ROC, set up the form for viewing
		//================================================================================================

		// If we're only viewing the incident's latest ROC:
		if(isViewingRoc)
		{
			// If we're not in an active incident:
			if(activeIncident == null)
			{
				incidentNameLabel.setVisibility(View.GONE);
				incidentNameTextView.setVisibility(View.GONE);
				messageTextView.setVisibility(View.VISIBLE);
				messageTextView.setText("You must first join an incident to view ROC data.");
				loadingIncidentInfoProgressBar.setVisibility(View.GONE);
				return;
			}

			// We only want to enable the swipe refresh if we're in viewing mode
			swipeRefreshLayout.setEnabled(true);

			setupFormForViewingMode(activeIncident);
			return;
		}

		//================================================================================================
		// Set up Field Behaviors
		//================================================================================================

		// Set up the Incident Name field to autocomplete based on incident names.
		makeAutoCompleteTextField(incidentNameTextView, allIncidentNames);

		//------------------------------------------------------------------------------------------------
		// Make Entering an Incident Name show the report type spinner
		//------------------------------------------------------------------------------------------------
		incidentNameTextView.addTextChangedListener(incidentNameTextWatcher);

		//------------------------------------------------------------------------------------------------
		// Update the form based on current context
		// (Ex: if we are in incident, pre-pop the incident field, and show the report type, etc.)
		//------------------------------------------------------------------------------------------------

		// If the user is in an active incident, show the report type field.
		if(activeIncident != null)
		{
			incidentNameTextView.setText(activeIncident.getIncidentName());
			incidentNameTextView.setEnabled(false);
		}
		else
		{
			incidentNameTextView.setText("");
			incidentNameTextView.setEnabled(true);
		}

		// NOTE - setting the text above executes incidentNameTextView.onTextChanged, which in turn calls incidentNameChanged
		// incidentNameChanged finishes setting up the form from here.

	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		Log.e("ROC","ROC - ROCFragment - onCreate");

		Log.e("ROC","ROC - ROCFragment - onCreate - savedInstanceState: " + savedInstanceState);


		super.onCreate(savedInstanceState);

		mContext = (MainActivity) getActivity();
		mDataManager = DataManager.getInstance();
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

		mRootView = inflater.inflate(R.layout.fragment_reportonconditionview, container, false);
		mDataManager = DataManager.getInstance(getActivity());

		// Defer assigning the rest of the member variables until onResume.
		// The way they should be set up depends on what incident is active, etc.. (which may change between onResume calls)

		setupFragment();

		Log.e("ROC","ROC - ROCFragment - onCreateView - savedInstanceState: " + savedInstanceState);


		// TODO - Need to implement restoring the saved state from this app
		// TODO - Not sure if it should be implemented here or in MainActivity
		/*if(savedInstanceState != null)
		{
			// Retrieving the stored ROC data:

			String draftDataJson = savedInstanceState.getString("rocDraftDataJson");

			Log.e("ROC", "ROC - ROCFragment - found a draft: " + draftDataJson);

			if(draftDataJson != null)
			{
				// TODO - do I need to set current incident payload & current ROC payload?

				ReportOnConditionData draftData = ReportOnConditionData.fromJSON(draftDataJson);

				Log.e("ROC", "ROC - ROCFragment - recovered the following ROC data: " + draftData.toJSON().toString());

				setAllFormFieldsFromRoc(draftData);
			}
		}*/

		return mRootView;
	}

	@Override
	public void onResume()
	{
		Log.e("ROC","ROC - ROCFragment - onResume");
		super.onResume();
	}

	@Override
	public void onStart()
	{
		Log.e("ROC","ROC - ROCFragment - onStart");
		super.onStart();
	}



	// TODO - Need to implement restoring the saved state from this app
	// TODO - Not sure if it should be implemented here or in MainActivity
	/*@Override
	public void onSaveInstanceState (Bundle outState)
	{
		Log.e("ROC","ROC - ROCFragment - onSaveInstanceState");

		super.onSaveInstanceState(outState);

		// If we're creating an ROC, then store the draft to restore it
		if(!isViewingRoc)
		{
			Log.e("ROC","ROC - ROCFragment - onSaveInstanceState - not viewing ROC");

			ReportOnConditionData draftData = formToReportOnConditionData();
			outState.putString("rocDraftDataJson",draftData.toJSON().toString());
		}
	}*/



	@Override
	public void onPause()
	{
		Log.e("ROC","ROC - ROCFragment - onPause");
		super.onPause();
	}

	@Override
	public void onStop()
	{
		Log.e("ROC","ROC - ROCFragment - onStop");

		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		Log.e("ROC","ROC - ROCFragment - onDestroy");
		super.onDestroy();
	}


	// Sets all of the form fields based on the values from the incident
	// if setName is true, we assign the incident name field as well
	// (Setting the incident name invokes "incidentnamechanged", and we don't always want to do that)
	protected void setAllFormFieldsFromIncident(IncidentPayload incidentData, boolean setName)
	{
		if(incidentData == null)
			return;

		// ------------------------------------------------
		// Incident Name
		// ------------------------------------------------

		if(setName)
		{
			incidentNameTextView.setText(incidentData.getIncidentName());
		}

		// ------------------------------------------------
		// Incident Type
		// ------------------------------------------------

		String incidntTypeString = "";
		for(IncidentTypePayload type : incidentData.getIncidentIncidenttypes())
		{
			incidntTypeString += type.getIncidentType().getIncidentTypeName() + "\n";

			String incidentTypeName = type.getIncidentType().getIncidentTypeName();

			if(!incidentTypeSelectedItems.contains(incidentTypeName))
			{
				incidentTypeSelectedItems.add(incidentTypeName);
			}
		}
		incidentTypeTextView.setText(incidntTypeString);

		// ------------------------------------------------
		// Incident Location
		// ------------------------------------------------
		// Converting incident lat/long to degrees and minutes:
		incidentLatitudeDegreesEditText.setText(String.valueOf(getDegree(incidentData.getLat())));
		incidentLatitudeMinutesEditText.setText(String.valueOf(getMinutesInteger(incidentData.getLat())));
		incidentLatitudeMinutesFractionEditText.setText(stringGetFraction(String.valueOf(getMinutesFraction(incidentData.getLat()))));
		// NOTE - we multiply the value by -1 because the UI has a hardcoded "-" sign in front of it
		// (all longitudes in california are in the -120 range, and the client doesn't want users to have to manually enter the "-" sign)
		incidentLongitudeDegreesEditText.setText(String.valueOf(-1 * getDegree(incidentData.getLon())));
		incidentLongitudeMinutesEditText.setText(String.valueOf(getMinutesInteger(incidentData.getLon())));
		incidentLongitudeMinutesFractionEditText.setText(stringGetFraction(String.valueOf(getMinutesFraction(incidentData.getLon()))));

		// ------------------------------------------------
		// Incident Number
		// ------------------------------------------------
		incidentNumberTextView.setText(incidentData.getIncidentNumber());
		incidentNumberTextView.setHint(R.string.incident_number_hint);
		// ------------------------------------------------
		// Incident State
		// ------------------------------------------------

		// TODO - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// TODO - incident state is not available to incident payload, but it might be embedded in the incident name:
		// TODO - For example: "CA TAB Test incident", the first two characters are "CA"
		// TODO - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// TODO - In the future, this should be added to incident payload,
		// TODO - or removed from the ROC form if it is not required to create an incident
		// TODO - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
		// Get the first three characters of the string:
		String incidentName = incidentData.getIncidentName();
		String incidentNamePrefix = incidentName .substring(0,Math.min(incidentName .length(), 3));

		if(incidentNamePrefix.equals("CA "))
		{
			incidentStateTextView.setText("CA");
		}
		// TODO - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

	}

	// Sets all of the form fields as the values from the rocData
	protected void setAllFormFieldsFromRoc(ReportOnConditionData rocData)
	{
		shouldValidate = false;
		if(rocData == null)
			return;

		this.rocData = rocData;

		incidentNameTextView.setText(rocData.incidentname);

		reportTypeLayout.setVisibility(View.VISIBLE);

		// Set report type spinner:
		if(rocData.reportType.equals("NEW"))
		{
			currentIncidentLastRocSubmitted = ROC_NONE;
			currentReportType = ROC_NEW;
		}
		else if(rocData.reportType.equals("UPDATE"))
		{
			currentIncidentLastRocSubmitted = ROC_NON_FINAL;
			currentReportType = ROC_UPDATE;
		}
		else if(rocData.reportType.equals("FINAL"))
		{
			currentIncidentLastRocSubmitted = ROC_NON_FINAL;
			currentReportType = ROC_FINAL;
		}
		else
		{
			currentIncidentLastRocSubmitted = ROC_NON_FINAL;
			currentReportType = ROC_NONE;
			// TODO - handle unknown report type, this is undefined behavior at the moment
			return;
		}

		// Setting the spinner report type options:
		String[] reportTypeOptions;
		if(currentIncidentLastRocSubmitted == ROC_NONE)
		{
			reportTypeOptions = new String[] {"Choose an ROC report type","NEW"};
		}
		else
		{
			// Therefore: (currentIncidentLastRocSubmitted = ROC_NON_FINAL)
			reportTypeOptions = new String[] {"Choose an ROC report type","UPDATE", "FINAL"};
		}
		setSpinnerOptions(reportTypeSpinner, reportTypeOptions);


		if(rocData.reportType.equals("NEW"))
		{
			// If this ROC is a NEW, then last was ROC_NONE, so "NEW" is at index 1
			reportTypeSpinner.setSelection(1);
		}
		else if(rocData.reportType.equals("UPDATE"))
		{
			// If this ROC is an UPDATE, then last was ROC_NON_FINAL, so "UPDATE" is at index 1
			reportTypeSpinner.setSelection(1);
		}
		else if(rocData.reportType.equals("FINAL"))
		{
			// If this ROC is a FINAL, then last was ROC_NON_FINAL, so "FINAL" is at index 2
			reportTypeSpinner.setSelection(2);
		}

		collapseAllSections();
		clearAllFormFields();
		clearAllFormFieldErrors();
		setupFormForCurrentReportType();

		//================================================================================================
		// filling in data from rocData:
		//================================================================================================

		//================================================
		// ROC Form Info Fields
		//================================================

		// --------- Setting the field values ---------
		//incidentName is already set above
		//reportType is already set above

		//================================================
		// Incident Info Fields
		//================================================

		// --------- Setting the field values ---------
		incidentNumberTextView.setText(rocData.incidentnumber);
		String incidentTypeString = "";
		incidentTypeSelectedItems = new HashSet<String>();
		for(String type : rocData.incidentTypes)
		{
			incidentTypeString += type + "\n";
			if(!incidentTypeSelectedItems.contains(type))
			{
				incidentTypeSelectedItems.add(type);
			}
		}
		incidentTypeTextView.setText(incidentTypeString);
		incidentTypeTextView.setHint(R.string.incident_type_box_hint);
		// Converting incident lat/long to degrees and minutes:
		incidentLatitudeDegreesEditText.setText(String.valueOf(getDegree(rocData.latitude)));
		incidentLatitudeMinutesEditText.setText(String.valueOf(getMinutesInteger(rocData.latitude)));
		incidentLatitudeMinutesFractionEditText.setText(stringGetFraction(String.valueOf(getMinutesFraction(rocData.latitude))));

		// NOTE - we multiply the value by -1 because the UI has a hardcoded "-" sign in front of it
		// (all longitudes in california are in the -120 range, and the client doesn't want users to have to manually enter the "-" sign)
		incidentLongitudeDegreesEditText.setText(String.valueOf(-1 * getDegree(rocData.longitude)));
		incidentLongitudeMinutesEditText.setText(String.valueOf(getMinutesInteger(rocData.longitude)));
		incidentLongitudeMinutesFractionEditText.setText(stringGetFraction(String.valueOf(getMinutesFraction(rocData.longitude))));

		incidentStateTextView.setText(rocData.incidentState);

		//================================================
		// ROC Incident Info Fields
		//================================================

		// --------- Setting the field values ---------
		setSpinnerValue(rocData.county, rocInitialCountySpinner);
		Log.e("ROC","ROC - setupFormForViewingMode - additional counties = \"" + rocData.additionalAffectedCounties + "\"");
		rocAdditionalCountiesTextView.setText(rocData.additionalAffectedCounties);
		rocLocationTextView.setText(rocData.location);
		rocLocationStreetEditText.setText(rocData.street);
		rocLocationCrossStreetEditText.setText(rocData.crossStreet);
		rocLocationNearestCommunity.setText(rocData.nearestCommunity);
		rocLocationDistanceFromNearestCommunityEditText.setText(rocData.milesFromNearestCommunity);
		setSpinnerValue(rocData.directionFromNearestCommunity, rocLocationDirectionFromNearestCommunitySpinner);

		setSpinnerValue(rocData.dpa, rocDPASpinner);
		setSpinnerValue(rocData.ownership, rocOwnershipSpinner);
		rocJurisdictionTextView.setText(rocData.jurisdiction);
		rocStartDateTextView.setText((new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())).format(rocData.startDate));
		rocStartTimeTextView.setText((new SimpleDateFormat("HHmm", Locale.getDefault())).format(rocData.startTime));

		//================================================
		// Vegetation Fire Incident Scope Fields
		//================================================

		// --------- Setting the field values ---------
		vegFireAcreageEditText.setText(rocData.acreage);
		setSpinnerValue(rocData.spreadRate, vegFireRateOfSpreadSpinner);

		CheckBox fuelTypes[] = {vegFireFuelTypeGrassCheckBox, vegFireFuelTypeBrushCheckBox, vegFireFuelTypeTimberCheckBox, vegFireFuelTypeOakWoodlandCheckBox, vegFireFuelTypeOtherCheckBox};
		for(CheckBox checkbox : fuelTypes)
		{
			// Final ROC doesn't have fuel types
			if(currentReportType != ROC_FINAL)
			{
				for(String fuelType : rocData.fuelTypes)
				{
					// Compare the strings in lower case to allow for different capitalizations
					if(fuelType.toLowerCase().equals(checkbox.getText().toString().toLowerCase()))
					{
						checkbox.setChecked(true);
					}
				}
			}
			// Hide them for final ROCs
			else
			{
				checkbox.setVisibility(View.GONE);
			}
		}

		if(currentReportType == ROC_FINAL)
		{
			vegFireFuelTypeLabelTextView.setVisibility(View.GONE);
		}
		else
		{
			vegFireFuelTypeLabelTextView.setVisibility(View.VISIBLE);
		}

		if(rocData.fuelTypes.contains("Other") && currentReportType != ROC_FINAL)
		{
			vegFireOtherFuelTypeEditText.setVisibility(View.VISIBLE);
			vegFireOtherFuelTypeLabelTextView.setVisibility(View.VISIBLE);
			vegFireOtherFuelTypeEditText.setText(rocData.otherFuelTypes);
		}
		else
		{
			vegFireOtherFuelTypeEditText.setVisibility(View.GONE);
			vegFireOtherFuelTypeLabelTextView.setVisibility(View.GONE);
			vegFireOtherFuelTypeEditText.setText("");
		}
		vegFirePercentContainedEditText.setText(rocData.percentContained);

		//================================================
		// Weather Information Fields
		//================================================

		// --------- Setting the field values ---------
		weatherTempEditText.setText(rocData.temperature);
		weatherRelativeHumidityEditText.setText(rocData.relHumidity);
		weatherWindSpeedEditText.setText(rocData.windSpeed);
		setSpinnerValue(rocData.windDirection, weatherWindDirectionSpinner);
		weatherGustsEditText.setText(rocData.windGusts);

		//================================================
		// Threats & Evacuations Fields &&  Other Significant Info Fields
		//================================================

		// --------- Setting the field values ---------
		setSpinnerValue(rocData.evacuations, threatsEvacsSpinner);
		setSpinnerValue(rocData.structureThreats, threatsStructuresSpinner);
		setSpinnerValue(rocData.infrastructureThreats, threatsInfrastructureSpinner);


		// Need to set up the fields:
		threatsEvacsSpinnerChanged(threatsEvacsSpinner.getSelectedItemPosition());
		threatsStructuresChanged(threatsStructuresSpinner.getSelectedItemPosition());
		threatsInfrastructureChanged(threatsInfrastructureSpinner.getSelectedItemPosition());


		// Instead of having duplicate code for each of the 4 sections, iterate through them and run it on all
		LinearLayout[] layouts = {
				threatsEvacsListLinearLayout,
				threatsStructuresListLinearLayout,
				threatsInfrastructureListLinearLayout,
				otherInfoListLinearLayout
		};

		ImageButton[] addButtons = {
				threatsEvacsAddButton,
				threatsStructuresAddButton,
				threatsInfrastructureAddButton,
				otherInfoAddButton
		};

		ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		dataList.add(rocData.evacuationsInProgress);
		dataList.add(rocData.structureThreatsInProgress);
		dataList.add(rocData.infrastructureThreatsInProgress);
		dataList.add(rocData.otherSignificantInfo);

		// For each of the 4 different LinearLayout lists
		// Add a child LinearLayout per String in the data
		for(int i = 0; i < layouts.length; i++)
		{
			for (int j = 0; j < dataList.get(i).size(); j++)
			{
				// Setting the button's onclick, so that it adds new text fields to the list:
				addButtons[i].setOnClickListener(newAddFieldListener(layouts[i], addButtons[i], null, null, null));

				// Call the addButton's onClick (this creates and adds a new linearLayout to the list)
				addButtons[i].callOnClick();

				Log.e("ROCARRAY","Called onclick for layout[" + i + "], layout children = " + layouts[i].getChildCount());
			}
		}

		// For each of the 4 different LinearLayout lists
		// Assign the AutoCompleteTextView's text to the data, disable it, and hide the remove button
		for(int i = 0; i < layouts.length; i++)
		{
			// For each of the linear layouts inside of a list
			for(int j = 0; j < layouts[i].getChildCount(); j++)
			{
				LinearLayout layout = (LinearLayout) layouts[i].getChildAt(j);

				// Iterate through layout's 2 children, find the textView and ImageButton
				for(int k = 0; k < layout.getChildCount(); k++)
				{
					View childView = layout.getChildAt(k);
					// Set the text
					if(childView instanceof AutoCompleteTextView)
					{
						((AutoCompleteTextView) childView).setText(dataList.get(i).get(j));
					}
				}
			}
		}

		// --------- Showing the linear layout lists --------
		threatsEvacsLabelTextView.setVisibility(View.VISIBLE);
		threatsStructuresLabelTextView.setVisibility(View.VISIBLE);
		threatsInfrastructureLabelTextView.setVisibility(View.VISIBLE);

		threatsEvacsListLinearLayout.setVisibility(View.VISIBLE);
		threatsStructuresListLinearLayout.setVisibility(View.VISIBLE);
		threatsInfrastructureListLinearLayout.setVisibility(View.VISIBLE);

		otherInfoListLinearLayout.setVisibility(View.VISIBLE);

		// If any of the spinners have a value of "No", hide the view:
		if(rocData.evacuations.equals("No"))
		{
			threatsEvacsListLinearLayout.setVisibility(View.GONE);
			threatsEvacsLabelTextView.setVisibility(View.GONE);
		}
		if(rocData.structureThreats.equals("No"))
		{
			threatsStructuresListLinearLayout.setVisibility(View.GONE);
			threatsStructuresLabelTextView.setVisibility(View.GONE);
		}
		if(rocData.infrastructureThreats.equals("No"))
		{
			threatsInfrastructureListLinearLayout.setVisibility(View.GONE);
			threatsInfrastructureLabelTextView.setVisibility(View.GONE);
		}

		//================================================
		// Resource Commitment Fields
		//================================================

		// --------- Setting the field values ---------
		setSpinnerValue(rocData.calfireIncident, calFireIncidentSpinner);

		// Making an array of all checkboxes to iterate over to reduce code
		CheckBox[] resourceCheckboxes = {
				calFireResourcesNoneCheckBox,
				calFireResourcesAirCheckBox,
				calFireResourcesGroundCheckBox,
				calFireResourcesAirAndGroundCheckBox,
				calFireResourcesAirAndGroundAugmentedCheckBox,
				calFireResourcesAgencyRepOrderedCheckBox,
				calFireResourcesAgencyRepAssignedCheckBox,
				calFireResourcesContinuedCheckBox,
				calFireResourcesSignificantAugmentationCheckBox,
				calFireResourcesVlatOrderCheckBox,
				calFireResourcesVlatAssignedCheckBox,
				calFireResourcesNoDivertCheckBox,
				calFireResourcesLatAssignedCheckBox,
				calFireResourcesAllReleasedCheckBox
		};


		for(CheckBox checkBox : resourceCheckboxes)
		{
			checkBox.setChecked(false);

			for(String resource : rocData.resourcesAssigned)
			{
				// Compare the strings in lower case to allow for different capitalizations
				if(resource.toLowerCase().equals(checkBox.getText().toString().toLowerCase()))
				{
					checkBox.setChecked(true);
				}
			}
		}

		//================================================
		// Email Fields
		//================================================

		// --------- Setting the field values ---------
		emailTextView.setText(rocData.email);
		shouldValidate = true;
	}

	// Sets all fields as read-only for viewing mode
	protected void lockFormFieldsForViewingMode()
	{
		//================================================
		// ROC Form Info Fields
		//================================================

		incidentNameTextView.setEnabled(false);
		reportTypeSpinner.setEnabled(false);

		//================================================
		// Incident Info Fields
		//================================================

		incidentNumberTextView.setEnabled(false);
		incidentTypeSpinner.setEnabled(false);
		incidentTypeSpinner.setVisibility(View.GONE);
		incidentLatitudeDegreesEditText.setEnabled(false);
		incidentLatitudeMinutesEditText.setEnabled(false);
		incidentLatitudeMinutesFractionEditText.setEnabled(false);
		incidentLongitudeDegreesEditText.setEnabled(false);
		incidentLongitudeMinutesEditText.setEnabled(false);
		incidentLongitudeMinutesFractionEditText.setEnabled(false);
		incidentStateTextView.setEnabled(false);

		// Hide the locate button
		incidentLocateButton.setVisibility(View.GONE);
		incidentLocateProgressBar.setVisibility(View.GONE);

		//================================================
		// ROC Incident Info Fields
		//================================================

		rocInitialCountySpinner.setEnabled(false);
		rocAdditionalCountiesTextView.setEnabled(false);
		rocLocationTextView.setEnabled(false);
		rocLocationStreetEditText.setEnabled(false);
		rocLocationCrossStreetEditText.setEnabled(false);
		rocLocationNearestCommunity.setEnabled(false);
		rocLocationDistanceFromNearestCommunityEditText.setEnabled(false);
		rocLocationDirectionFromNearestCommunitySpinner.setEnabled(false);
		rocDPASpinner.setEnabled(false);
		rocOwnershipSpinner.setEnabled(false);
		rocJurisdictionTextView.setEnabled(false);
		rocStartDateTextView.setEnabled(false);
		rocStartTimeTextView.setEnabled(false);

		//================================================
		// Vegetation Fire Incident Scope Fields
		//================================================

		CheckBox fuelTypes[] = {vegFireFuelTypeGrassCheckBox, vegFireFuelTypeBrushCheckBox, vegFireFuelTypeTimberCheckBox, vegFireFuelTypeOakWoodlandCheckBox, vegFireFuelTypeOtherCheckBox};
		vegFireAcreageEditText.setEnabled(false);
		vegFireRateOfSpreadSpinner.setEnabled(false);
		for(CheckBox checkbox : fuelTypes)
		{
			checkbox.setEnabled(false);
		}
		vegFireOtherFuelTypeEditText.setEnabled(false);
		vegFirePercentContainedEditText.setEnabled(false);

		//================================================
		// Weather Information Fields
		//================================================

		weatherTempEditText.setEnabled(false);
		weatherRelativeHumidityEditText.setEnabled(false);
		weatherWindSpeedEditText.setEnabled(false);
		weatherWindDirectionSpinner.setEnabled(false);
		weatherGustsEditText.setEnabled(false);

		//================================================
		// Threats & Evacuations Fields &&  Other Significant Info Fields
		//================================================

		// Instead of having duplicate code for each of the 4 sections, iterate through them and run it on all
		LinearLayout[] layouts = {
				threatsEvacsListLinearLayout,
				threatsStructuresListLinearLayout,
				threatsInfrastructureListLinearLayout,
				otherInfoListLinearLayout
		};

		ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		dataList.add(rocData.evacuationsInProgress);
		dataList.add(rocData.structureThreatsInProgress);
		dataList.add(rocData.infrastructureThreatsInProgress);
		dataList.add(rocData.otherSignificantInfo);

		// For each of the 4 different LinearLayout lists
		// Disable it, and hide the remove button
		for(int i = 0; i < layouts.length; i++)
		{
			// For each of the linear layouts inside of a list
			for(int j = 0; j < layouts[i].getChildCount(); j++)
			{
				LinearLayout layout = (LinearLayout) layouts[i].getChildAt(j);

				// Iterate through layout's 2 children, find the textView and ImageButton
				for(int k = 0; k < layout.getChildCount(); k++)
				{
					View childView = layout.getChildAt(k);
					// Set the text
					if(childView instanceof AutoCompleteTextView)
					{
						((AutoCompleteTextView) childView).setEnabled(false);
					}
					// Hide the remove button
					if(childView instanceof ImageButton)
					{
						childView.setVisibility(View.GONE);
					}
				}
			}
		}

		// --------- Hiding the add buttons ------------
		threatsEvacsAddButton.setVisibility(View.GONE);
		threatsInfrastructureAddButton.setVisibility(View.GONE);
		threatsStructuresAddButton.setVisibility(View.GONE);
		otherInfoAddButton.setVisibility(View.GONE);

		// --------- Disabling the fields ---------
		threatsEvacsSpinner.setEnabled(false);
		threatsStructuresSpinner.setEnabled(false);
		threatsInfrastructureSpinner.setEnabled(false);

		//================================================
		// Resource Commitment Fields
		//================================================

		// Making an array of all checkboxes to iterate over to reduce code
		CheckBox[] resourceCheckboxes = {
				calFireResourcesNoneCheckBox,
				calFireResourcesAirCheckBox,
				calFireResourcesGroundCheckBox,
				calFireResourcesAirAndGroundCheckBox,
				calFireResourcesAirAndGroundAugmentedCheckBox,
				calFireResourcesAgencyRepOrderedCheckBox,
				calFireResourcesAgencyRepAssignedCheckBox,
				calFireResourcesContinuedCheckBox,
				calFireResourcesSignificantAugmentationCheckBox,
				calFireResourcesVlatOrderCheckBox,
				calFireResourcesVlatAssignedCheckBox,
				calFireResourcesNoDivertCheckBox,
				calFireResourcesLatAssignedCheckBox,
				calFireResourcesAllReleasedCheckBox
		};


		for(CheckBox checkBox : resourceCheckboxes)
		{
			checkBox.setEnabled(false);
		}
		// --------- Disabling the fields ---------
		calFireIncidentSpinner.setEnabled(false);

		//================================================
		// Email Fields
		//================================================
		// --------- Disabling the fields ---------
		emailTextView.setEnabled(false);
	}


	// This fills the form fields and disables interactivity with the form.
	protected void setupFormForViewingMode(IncidentPayload incident)
	{
		incidentData = incident;
		// Getting the rocData for the incident:
		rocData = mDataManager.getLastReportOnCondition(incident.getIncidentId());

		// If the data is null, we don't have any ROC data to display
		if(rocData == null)
		{
			incidentNameLabel.setVisibility(View.GONE);
			incidentNameTextView.setVisibility(View.GONE);
			messageTextView.setVisibility(View.VISIBLE);
			messageTextView.setText("No ROC data found for incident: \"" + incident.getIncidentName() + "\".");
			loadingIncidentInfoProgressBar.setVisibility(View.GONE);
			return;
		}

		Log.e("ROC","ROC - setupFormForViewingMode - getLastReportOnCondition(" + incident.getIncidentId() + ") returns: " + rocData.toJSON());


		messageTextView.setVisibility(View.GONE);
		loadingIncidentInfoProgressBar.setVisibility(View.GONE);

		setAllFormFieldsFromRoc(rocData);
		setAllFormFieldsFromIncident(incidentData,true);
		lockFormFieldsForViewingMode();

		//================================================
		// ROC Submission Fields
		//================================================

		// Hide the SUBMIT button
		submitButton.setVisibility(View.GONE);
		// Set the Cancel button text to "Done"
		cancelButton.setText("Done");
	}

	// Executed each time the incidentNameTextView text changes
	// This is responsible for setting up the form with the correct options.
	protected void incidentNameChanged()
	{
		reportTypeLayout.setVisibility(View.GONE);

		String textContent = incidentNameTextView.getText().toString();

		Log.e("ROC","ROC Incident name changed: \"" + textContent + "\"");


		// If the textView is empty
		// hide the reportType view (and everything else)
		if(textContent.trim().length() == 0)
		{
			rocData.incidentname = "";
			incidentData = null;
			reportTypeLayout.setVisibility(View.GONE);
			collapseAllSections();

			// Hiding all headers:
			incidentInfoHeader.setVisibility(View.GONE);
			rocIncidentInfoHeader.setVisibility(View.GONE);
			vegFireIncidentScopeHeader.setVisibility(View.GONE);
			weatherInfoHeader.setVisibility(View.GONE);
			threatsEvacsHeader.setVisibility(View.GONE);
			resourceCommitmentHeader.setVisibility(View.GONE);
			otherInfoHeader.setVisibility(View.GONE);
			emailHeader.setVisibility(View.GONE);

			// Hiding the progress bar:
			messageTextView.setVisibility(View.GONE);
			loadingIncidentInfoProgressBar.setVisibility(View.GONE);
			return;
		}

		// Store the incident name
		rocData.incidentname = textContent;


		// Check if the text content is an existing incident
		boolean isExistingIncidentName = false;
		for(String name : allIncidentNames)
		{
			if(textContent.equals(name))
			{
				isExistingIncidentName = true;
				break;
			}
		}

		incidentData = null;

		// If the textView contains one of the incident names
		// show the reportType View and allow all three options
		if(isExistingIncidentName)
		{
			incidentData = mDataManager.getIncidentForName(rocData.incidentname);

			Log.e("ROC","ROC name \"" + textContent + "\" is a valid incident (id: " + incidentData.getIncidentId() + ").");

			creatingNewIncident = false;

			// request the ROC info for that incident, show the progress bar and a message letting the user know.
			messageTextView.setVisibility(View.VISIBLE);
			loadingIncidentInfoProgressBar.setVisibility(View.VISIBLE);
			messageTextView.setText("Fetching latest ROC data for incident...");

			// Setting it as an undefined value
			currentIncidentLastRocSubmitted = -1;

			// Building the success and error callbacks:
			Handler.Callback successCallback = new Handler.Callback() {
				@Override
				public boolean handleMessage (Message msg)
				{
					FragmentActivity activity = getActivity();

					if(activity != null)
					{
						activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run ()
							{
								ReportOnConditionData rocData = (incidentData != null) ? mDataManager.getLastReportOnCondition(incidentData.getIncidentId()) : null;
								gotIncidentROCData(rocData);
							}
						});
					}
					return true;
				}
			};

			Handler.Callback errorCallback = new Handler.Callback() {
				@Override
				public boolean handleMessage (Message msg)
				{
					FragmentActivity activity = getActivity();

					if(activity != null)
					{
						activity.runOnUiThread(new Runnable()
						{
							@Override
							public void run ()
							{
								messageTextView.setVisibility(View.VISIBLE);
								messageTextView.setText("There was an error retrieving incident ROC data. Please try again.");
								loadingIncidentInfoProgressBar.setVisibility(View.GONE);
							}
						});
					}
					return true;
				}
			};

			mDataManager.pullLatestReportOnConditionForIncident(rocData.incidentname, successCallback, errorCallback);

		}
		else
		{
			creatingNewIncident = true;
			currentIncidentLastRocSubmitted = ROC_NONE;
			gotIncidentROCData(null);
		}

		//------------------------------------------------------------------------------------------------
		// Make selecting a report type show the correct fields:
		//------------------------------------------------------------------------------------------------
		reportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			// The OnItemSelectedListener will perform a corresponding callback
			ReportOnConditionFragment fragment;
			private AdapterView.OnItemSelectedListener setFragment(ReportOnConditionFragment f)
			{
				this.fragment = f;
				return this;
			}

			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				fragment.reportTypeChanged(position);
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}
		}.setFragment(this));
	}

	// Called when we retrieve the latest ROC data for an incident when creating an incident
	// if rocData is null, we treat this as having no previous ROCs
	public void gotIncidentROCData(ReportOnConditionData latestROCData)
	{
		Log.e("ROC","ROC Got latest incident data: " + (latestROCData != null ? latestROCData.toJSON() : "null"));

		// Hide the loading incident data message & progress bar:
		messageTextView.setVisibility(View.GONE);
		loadingIncidentInfoProgressBar.setVisibility(View.GONE);

		lastRocData = null;

		// If we have previous ROC Data
		if(latestROCData != null)
		{
			// Store it.
			lastRocData = latestROCData;

			// Parse out which ROC type it is:
			if(lastRocData.reportType.equals("NEW"))
			{
				currentIncidentLastRocSubmitted = ROC_NON_FINAL;
			}
			else if(lastRocData.reportType.equals("UPDATE"))
			{
				currentIncidentLastRocSubmitted = ROC_NON_FINAL;

			}
			else if(lastRocData.reportType.equals("FINAL"))
			{
				currentIncidentLastRocSubmitted = ROC_FINAL;
			}
			// If we don't recognize the ROC report type, throw away the report
			// (treat this as though we have no prior data)
			else
			{
				lastRocData = null;
			}
		}

		if(lastRocData == null)
		{
			currentIncidentLastRocSubmitted = ROC_NONE;
		}



		// If the incident has a FINAL Roc:
		if(currentIncidentLastRocSubmitted == ROC_FINAL)
		{
			// Show an error dialog letting the user know that a FINAL ROC already exists for this incident.
			messageTextView.setVisibility(View.VISIBLE);
			messageTextView.setText("Unable to create an ROC for incident \""+ incidentNameTextView.getText() + "\". A FINAL ROC has already been submitted for this incident.");

			// Do not show any other form fields
			return;
		}


		// Showing the report type layout:
		reportTypeLayout.setVisibility(View.VISIBLE);


		// Make the Report type Spinner options be consistent with the reports that exist for an incident.
		String[] reportTypeOptions;
		if(currentIncidentLastRocSubmitted == ROC_NONE)
		{
			reportTypeOptions = new String[] {"Choose an ROC report type","NEW"};
		}
		else
		{
			// Therefore: (currentIncidentLastRocSubmitted = ROC_NON_FINAL)
			reportTypeOptions = new String[] {"Choose an ROC report type","UPDATE", "FINAL"};
		}

		setSpinnerOptions(reportTypeSpinner, reportTypeOptions);

		collapseAllSections();

		// Hiding all headers:
		incidentInfoHeader.setVisibility(View.GONE);
		rocIncidentInfoHeader.setVisibility(View.GONE);
		vegFireIncidentScopeHeader.setVisibility(View.GONE);
		weatherInfoHeader.setVisibility(View.GONE);
		threatsEvacsHeader.setVisibility(View.GONE);
		resourceCommitmentHeader.setVisibility(View.GONE);
		otherInfoHeader.setVisibility(View.GONE);
		emailHeader.setVisibility(View.GONE);
	}

	// Called whenever the report type is changed, or the incident type is changed
	public void setupVegFireRateOfSpreadSpinnerForReportTypeAndIncidentType()
	{
		shouldValidate = false;
		String requiredSuffix = "";

		// If the incident type is a vegetation fire, rate of spread is required.
		// We're going to add the suffix "(required)", to let the user know.
		if (isIncidentVegetationFire())
		{
			requiredSuffix = " (required)";
		}

		String[] rateOfSpreadSpinnerOptions;

		// ( SLOW / MODERATE / DANGEROUS / CRITICAL / STOPPED ) if NEW or UPDATE
		// ( STOPPED ) if FINAL
		if(currentReportType == ROC_FINAL)
		{
			rateOfSpreadSpinnerOptions = new String[]{"Choose an option" + requiredSuffix, "Forward spread has been stopped"};
		}
		else
		{
			rateOfSpreadSpinnerOptions = new String[]{"Choose an option" + requiredSuffix, "Slow rate of spread", "Moderate rate of spread", "Dangerous rate of spread","Critical rate of spread","Forward spread has been stopped"};
		}


		setSpinnerOptions(vegFireRateOfSpreadSpinner, rateOfSpreadSpinnerOptions);
		vegFireRateOfSpreadSpinner.setSelection(0);
		shouldValidate = true;
	}

	// Called whenever the report type is changed, or the incident type is changed
	public void setupThreatsSectionForReportTypeAndIncidentType()
	{
		shouldValidate = false;
		String requiredSuffix = "";

		// If the incident type is a vegetation fire, these options are required.
		// We're going to add the suffix "(required)", to let the user know.
		if(isIncidentVegetationFire())
		{
			requiredSuffix = " (required)";
		}

		String[] threatsSpinnerOptions;

		// ( YES / NO / MITIGATED ) if NEW or UPDATE
		// ( NO / MITIGATED ) if FINAL
		if(currentReportType == ROC_FINAL)
		{
			threatsSpinnerOptions = new String[] {"Choose an option" + requiredSuffix, "No","Mitigated"};
		}
		else
		{
			threatsSpinnerOptions = new String[] {"Choose an option" + requiredSuffix, "Yes", "No","Mitigated"};
		}

		setSpinnerOptions(threatsEvacsSpinner, threatsSpinnerOptions);
		setSpinnerOptions(threatsStructuresSpinner, threatsSpinnerOptions);
		setSpinnerOptions(threatsInfrastructureSpinner, threatsSpinnerOptions);
		threatsEvacsSpinner.setSelection(0);
		threatsStructuresSpinner.setSelection(0);
		threatsInfrastructureSpinner.setSelection(0);
		shouldValidate = true;
	}


	public void setupFormForCurrentReportType()
	{
		// If there is no report type
		if(currentReportType != ROC_NEW && currentReportType != ROC_UPDATE && currentReportType != ROC_FINAL)
		{
			// Hiding all headers:
			incidentInfoHeader.setVisibility(View.GONE);
			rocIncidentInfoHeader.setVisibility(View.GONE);
			vegFireIncidentScopeHeader.setVisibility(View.GONE);
			weatherInfoHeader.setVisibility(View.GONE);
			threatsEvacsHeader.setVisibility(View.GONE);
			resourceCommitmentHeader.setVisibility(View.GONE);
			otherInfoHeader.setVisibility(View.GONE);
			emailHeader.setVisibility(View.GONE);
			submitButton.setVisibility(View.INVISIBLE);
			return;
		}

		// Showing all section headers:
		incidentInfoHeader.setVisibility(View.VISIBLE);
		rocIncidentInfoHeader.setVisibility(View.VISIBLE);
		vegFireIncidentScopeHeader.setVisibility(View.VISIBLE);
		weatherInfoHeader.setVisibility(View.VISIBLE);
		threatsEvacsHeader.setVisibility(View.VISIBLE);
		resourceCommitmentHeader.setVisibility(View.VISIBLE);
		otherInfoHeader.setVisibility(View.VISIBLE);
		emailHeader.setVisibility(View.VISIBLE);
		submitButton.setVisibility(View.VISIBLE);


		//----------------------------------------------------------------------------
		// Hiding weather info for UPDATE or FINAL forms
		// NOTE: Change in requirements - they want weather section to be visible in all report types
		//----------------------------------------------------------------------------
		/*if(currentReportType == ROC_UPDATE || currentReportType == ROC_FINAL)
		{
			weatherInfoHeader.setVisibility(View.GONE);
		}*/

		//----------------------------------------------------------------------------
		// Hiding start date and time for UPDATE or FINAL forms
		//----------------------------------------------------------------------------

		if(currentReportType == ROC_UPDATE || currentReportType == ROC_FINAL)
		{
			rocStartDateTimeLayout.setVisibility(View.GONE);
		}
		else
		{
			rocStartDateTimeLayout.setVisibility(View.VISIBLE);
		}

		//----------------------------------------------------------------------------
		// Setting up the other info section
		//----------------------------------------------------------------------------

		if(currentReportType == ROC_FINAL)
		{
			// Make the other sig info section not have auto complete
			otherInfoAddButton.setOnClickListener(newAddFieldListener(
					otherInfoListLinearLayout,
					otherInfoAddButton,
					null,
					null,
					null));
		}
		else
		{
			// Make the other sig info section have autocomplete
			otherInfoAddButton.setOnClickListener(newAddFieldListener(
					otherInfoListLinearLayout,
					otherInfoAddButton,
					otherSignificantInfoCannedMessages,
					null,
					null));
		}





		//----------------------------------------------------------------------------
		// Hiding the fuel types for FINAL ROC
		//----------------------------------------------------------------------------
		if(currentReportType == ROC_FINAL)
		{
			vegFireFuelTypeLabelTextView.setVisibility(View.GONE);
			vegFireFuelTypeGrassCheckBox.setVisibility(View.GONE);
			vegFireFuelTypeBrushCheckBox.setVisibility(View.GONE);
			vegFireFuelTypeTimberCheckBox.setVisibility(View.GONE);
			vegFireFuelTypeOakWoodlandCheckBox.setVisibility(View.GONE);
			vegFireFuelTypeOtherCheckBox.setVisibility(View.GONE);
		}
		else
		{
			vegFireFuelTypeLabelTextView.setVisibility(View.VISIBLE);
			vegFireFuelTypeGrassCheckBox.setVisibility(View.VISIBLE);
			vegFireFuelTypeBrushCheckBox.setVisibility(View.VISIBLE);
			vegFireFuelTypeTimberCheckBox.setVisibility(View.VISIBLE);
			vegFireFuelTypeOakWoodlandCheckBox.setVisibility(View.VISIBLE);
			vegFireFuelTypeOtherCheckBox.setVisibility(View.VISIBLE);
		}
		vegFireOtherFuelTypeEditText.setVisibility(View.GONE);
		vegFireOtherFuelTypeLabelTextView.setVisibility(View.GONE);
		vegFireOtherFuelTypeEditText.setText("");

		//----------------------------------------------------------------------------
		// Changing the Resource Commitment Section
		//----------------------------------------------------------------------------
		if(currentReportType == ROC_NEW || currentReportType == ROC_UPDATE)
		{
			calFireResourcesNoneCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesAirCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesGroundCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesAirAndGroundCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesAirAndGroundAugmentedCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesAgencyRepOrderedCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesAgencyRepAssignedCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesContinuedCheckBox.setVisibility(View.GONE);
			calFireResourcesSignificantAugmentationCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesVlatOrderCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesVlatAssignedCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesNoDivertCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesLatAssignedCheckBox.setVisibility(View.VISIBLE);
			calFireResourcesAllReleasedCheckBox.setVisibility(View.GONE);
		}
		if(currentReportType == ROC_UPDATE)
		{
			calFireResourcesContinuedCheckBox.setVisibility(View.VISIBLE);
		}
		if(currentReportType == ROC_FINAL)
		{
			calFireResourcesNoneCheckBox.setVisibility(View.GONE);
			calFireResourcesAirCheckBox.setVisibility(View.GONE);
			calFireResourcesGroundCheckBox.setVisibility(View.GONE);
			calFireResourcesAirAndGroundCheckBox.setVisibility(View.GONE);
			calFireResourcesAirAndGroundAugmentedCheckBox.setVisibility(View.GONE);
			calFireResourcesAgencyRepOrderedCheckBox.setVisibility(View.GONE);
			calFireResourcesAgencyRepAssignedCheckBox.setVisibility(View.GONE);
			calFireResourcesContinuedCheckBox.setVisibility(View.GONE);
			calFireResourcesSignificantAugmentationCheckBox.setVisibility(View.GONE);
			calFireResourcesVlatOrderCheckBox.setVisibility(View.GONE);
			calFireResourcesVlatAssignedCheckBox.setVisibility(View.GONE);
			calFireResourcesNoDivertCheckBox.setVisibility(View.GONE);
			calFireResourcesLatAssignedCheckBox.setVisibility(View.GONE);
			calFireResourcesAllReleasedCheckBox.setVisibility(View.VISIBLE);
		}

		//----------------------------------------------------------------------------
		// Setting up fields that depend on the incident type
		//----------------------------------------------------------------------------
		incidentTypeChanged();
	}

	// Executed each time the reportTypeSpinner's selected item changes
	// This is responsible for setting up for the differences between NEW, UPDATE, and FINAL ROC forms.
	public void reportTypeChanged(int position)
	{
		if(currentIncidentLastRocSubmitted == ROC_NONE)
		{
			// Spinner indices
			// 0 - "Choose a Report"
			// 1 - "NEW"
			if(position == 0)
			{
				currentReportType = ROC_NONE;
			}
			if(position == 1)
			{
				currentReportType = ROC_NEW;
			}
		}
		if(currentIncidentLastRocSubmitted == ROC_NON_FINAL)
		{
			// Spinner indices
			// 0 - "Choose a Report"
			// 1 - "UPDATE"
			// 2 - "FINAL"
			if(position == 0)
			{
				currentReportType = ROC_NONE;
			}
			if(position == 1)
			{
				currentReportType = ROC_UPDATE;
			}

			if(position == 2)
			{
				currentReportType = ROC_FINAL;
			}
		}


		// Make all of the section headers visible:
		collapseAllSections();
		clearAllFormFields();
		clearAllFormFieldErrors();
		setupFormForCurrentReportType();


		//----------------------------------------------------------------------------
		// If we are NOT creating a new incident, autopopulate the incident data
		//----------------------------------------------------------------------------

		// Filling the form with data from the incident details:
		if(!creatingNewIncident && incidentData != null)
		{
			setAllFormFieldsFromIncident(incidentData,false);
		}


		//----------------------------------------------------------------------------
		// If we are NOT creating a new incident, make the incident info fields read-only:
		//----------------------------------------------------------------------------
		if(!creatingNewIncident)
		{
			incidentNumberTextView.setEnabled(false);
			incidentTypeTextView.setEnabled(false);
			incidentTypeSpinner.setEnabled(false);
			incidentTypeSpinner.setVisibility(View.GONE);
			incidentLatitudeDegreesEditText.setEnabled(false);
			incidentLatitudeMinutesEditText.setEnabled(false);
			incidentLatitudeMinutesFractionEditText.setEnabled(false);
			incidentLongitudeDegreesEditText.setEnabled(false);
			incidentLongitudeMinutesEditText.setEnabled(false);
			incidentLongitudeMinutesFractionEditText.setEnabled(false);



			incidentLocateButton.setEnabled(false);
			incidentLocateButton.setVisibility(View.GONE);
			incidentStateTextView.setEnabled(false);
			incidentLocateProgressBar.setVisibility(View.GONE);
		}
		else
		{
			// Make sure they are editable:
			incidentNumberTextView.setEnabled(true);
			incidentTypeTextView.setEnabled(true);
			incidentTypeSpinner.setEnabled(true);
			incidentTypeSpinner.setVisibility(View.VISIBLE);
			incidentLatitudeDegreesEditText.setEnabled(true);
			incidentLatitudeMinutesEditText.setEnabled(true);
			incidentLatitudeMinutesFractionEditText.setEnabled(true);
			incidentLongitudeDegreesEditText.setEnabled(true);
			incidentLongitudeMinutesEditText.setEnabled(true);
			incidentLongitudeMinutesFractionEditText.setEnabled(true);
			incidentLocateButton.setEnabled(true);
			incidentLocateButton.setVisibility(View.VISIBLE);
			incidentLocateProgressBar.setVisibility(View.GONE);
			incidentStateTextView.setEnabled(true);
		}

		//------------------------------------------------------------------------------------------------
		// Make Selecting "Other" for fuel types show the "Other text box"
		//------------------------------------------------------------------------------------------------
		vegFireFuelTypeOtherCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged (CompoundButton buttonView, boolean isChecked)
			{
				if(isChecked)
				{
					vegFireOtherFuelTypeLabelTextView.setVisibility(View.VISIBLE);
					vegFireOtherFuelTypeEditText.setVisibility(View.VISIBLE);
				}
				else
				{
					vegFireOtherFuelTypeLabelTextView.setVisibility(View.GONE);
					vegFireOtherFuelTypeEditText.setVisibility(View.GONE);
				}
			}
		});

		//------------------------------------------------------------------------------------------------
		// Making the threats & evacs Evacuations Spinner show the correct options
		//------------------------------------------------------------------------------------------------
		threatsEvacsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				threatsEvacsErrorView.setVisibility(View.GONE);
				threatsEvacsListErrorView.setVisibility(View.GONE);
				rocFragment.threatsEvacsSpinnerChanged(position);
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}
		});

		//------------------------------------------------------------------------------------------------
		// Making the threats & evacs Structures Threat Spinner show the correct options
		//------------------------------------------------------------------------------------------------
		threatsStructuresSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				threatsStructuresErrorView.setVisibility(View.GONE);
				threatsStructuresListErrorView.setVisibility(View.GONE);
				rocFragment.threatsStructuresChanged(position);
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}
		});

		//------------------------------------------------------------------------------------------------
		// Making the threats & evacs Infrastructure Threat Spinner show the correct options
		//------------------------------------------------------------------------------------------------
		threatsInfrastructureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				threatsInfrastructureErrorView.setVisibility(View.GONE);
				threatsInfrastructureListErrorView.setVisibility(View.GONE);
				rocFragment.threatsInfrastructureChanged(position);
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}
		});

		//------------------------------------------------------------------------------------------------
		// Don't let the incident type spinner ever be the 0th item:
		//------------------------------------------------------------------------------------------------
		incidentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				Log.e("ROC","ROCIncidentType - incidentTypeSpinner - onItemSelected - should be calling rocFragment.incidentTypeChanged.");
				rocFragment.incidentTypeChanged();
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}
		});

		//------------------------------------------------------------------------------------------------

		//----------------------------------------------------------------------------
		// If NEW, autopopulate start date with current date
		//----------------------------------------------------------------------------
		if(currentReportType == ROC_NEW)
		{
			Date date = Calendar.getInstance().getTime();
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
			Log.e("ROC", "Autopopulated date as: \"" + dateFormat.format(date) + "\"");
			rocStartDateTextView.setText(dateFormat.format(date));
		}


		//----------------------------------------------------------------------------
		// Populating the Email Text View
		//----------------------------------------------------------------------------
		emailTextView.setText(mDataManager.getUsername());

		//----------------------------------------------------------------------------
		// If we have incident location, autopopulate location fields:
		//----------------------------------------------------------------------------

		if(incidentData != null)
		{
			JSONObject coords = null;

			// Request location-based data using those form fields:
			// requestIncidentLocationDetailsForFormCoords();
		}



		//----------------------------------------------------------------------------
		// If we have a previous ROC, autopopulate the form fields:
		//----------------------------------------------------------------------------

		if(lastRocData != null)
		{
			// Additional Affected Counties
			rocAdditionalCountiesTextView.setText(lastRocData.additionalAffectedCounties);

			rocLocationStreetEditText.setText(lastRocData.street);
			rocLocationCrossStreetEditText.setText(lastRocData.crossStreet);
			rocLocationNearestCommunity.setText(lastRocData.nearestCommunity);
			rocLocationDistanceFromNearestCommunityEditText.setText(lastRocData.milesFromNearestCommunity);
			setSpinnerValue(lastRocData.directionFromNearestCommunity, rocLocationDirectionFromNearestCommunitySpinner);

			setSpinnerValue(lastRocData.dpa, rocDPASpinner);
			setSpinnerValue(lastRocData.ownership, rocOwnershipSpinner);
			rocJurisdictionTextView.setText(lastRocData.jurisdiction);
			setSpinnerValue(lastRocData.county, rocInitialCountySpinner);


			// Fuel Types Checkboxes
			if(lastRocData.fuelTypes.contains("Grass"))
			{
				vegFireFuelTypeGrassCheckBox.setChecked(true);
			}
			if(lastRocData.fuelTypes.contains("Brush"))
			{
				vegFireFuelTypeBrushCheckBox.setChecked(true);
			}
			if(lastRocData.fuelTypes.contains("Timber"))
			{
				vegFireFuelTypeTimberCheckBox.setChecked(true);
			}
			if(lastRocData.fuelTypes.contains("Oak Woodland"))
			{
				vegFireFuelTypeOakWoodlandCheckBox.setChecked(true);
			}
			if(lastRocData.fuelTypes.contains("Other"))
			{
				vegFireFuelTypeOtherCheckBox.setChecked(true);
				vegFireOtherFuelTypeEditText.setText(lastRocData.otherFuelTypes);
				vegFireOtherFuelTypeEditText.setVisibility(View.VISIBLE);
				vegFireOtherFuelTypeLabelTextView.setVisibility(View.VISIBLE);
			}
		}

	}


	// Returns whether the incident is a vegetation fire or not
	public boolean isIncidentVegetationFire()
	{
		return incidentTypeSelectedItems.contains("Vegetation Fire");
	}

	// Executed each time the report type is changed
	// This is responsible for;
	// a) Ensuring repot type spinner stays on item 0
	// b) Ensuring the if vegetation fire is selected, we make vegetation fire fields mandatory
	public void incidentTypeChanged()
	{
		shouldValidate = false;
		incidentTypeTextView.setError(null);
		incidentTypeSpinner.setSelection(0);

		Log.e("ROC","ROCIncidentType - incidentTypeChanged: " + incidentTypeSelectedItems);

		if(isIncidentVegetationFire())
		{
			Log.e("ROC","ROCIncidentType - Vegetation Fire found");


			// TODO - Set the hint as required for the following fields:
			vegFireAcreageEditText.setHint("(required)");
			//vegFireRateOfSpreadSpinner;
			vegFireFuelTypeLabelTextView.setHint("(required)");
			vegFireFuelTypeLabelTextView.setText("Fuel Types: (required)");
			vegFirePercentContainedEditText.setHint("(required)");
			//threatsEvacsLabelTextView;
			//threatsStructuresLabelTextView;
			//threatsInfrastructureLabelTextView;

		}
		else
		{
			Log.e("ROC","ROCIncidentType - Not a vegetation fire");


			// TODO - Remove the hint as required for the following fields:
			vegFireAcreageEditText.setHint("");
			//vegFireRateOfSpreadSpinner;
			vegFireFuelTypeLabelTextView.setText("Fuel Types:");
			vegFirePercentContainedEditText.setHint("");
			//threatsEvacsLabelTextView;
			//threatsStructuresLabelTextView;
			//threatsInfrastructureLabelTextView;

		}

		// In case:
		// the incident is now a Vegetation Fire:
		// or
		// the incident is no longer a Vegetation Fire:

		// Re-set up the rate of spread spinner.
		// (The options available depend on the current report type)
		// (Whether the field is required depends on if the incident is a vegetation fire)
		setupVegFireRateOfSpreadSpinnerForReportTypeAndIncidentType();

		// Re-set up the threat section.
		// (The fields are required IFF the incident is a vegetation fire)
		setupThreatsSectionForReportTypeAndIncidentType();
		shouldValidate = true;
	}


	public void threatsEvacsSpinnerChanged(int position)
	{
		String value = "";
		if(threatsEvacsSpinner.getSelectedItem() != null)
		{
			value = threatsEvacsSpinner.getSelectedItem().toString();
			value = value == null ? "" : value;
		}
		setupThreatSubsection(value,
				threatsEvacsLabelTextView,
				threatsEvacsListLinearLayout,
				threatsEvacsAddButton,
				threatsEvacsYesCannedMessages,
				threatsEvacsMitigatedCannedMessages,
				threatsEvacsErrorView,
				threatsEvacsListErrorView);
	}

	public void threatsStructuresChanged(int position)
	{
		String value = "";
		if(threatsStructuresSpinner.getSelectedItem() != null)
		{
			value = threatsStructuresSpinner.getSelectedItem().toString();
			value = value == null ? "" : value;
		}
		setupThreatSubsection(value,
				threatsStructuresLabelTextView,
				threatsStructuresListLinearLayout,
				threatsStructuresAddButton,
				threatsStructuresYesCannedMessages,
				threatsStructuresMitigatedCannedMessages,
				threatsStructuresErrorView,
				threatsStructuresListErrorView);

	}

	public void threatsInfrastructureChanged(int position)
	{
		String value = "";
		if(threatsInfrastructureSpinner.getSelectedItem() != null)
		{
			value = threatsInfrastructureSpinner.getSelectedItem().toString();
			value = value == null ? "" : value;
		}
		setupThreatSubsection(value,
				threatsInfrastructureLabelTextView,
				threatsInfrastructureListLinearLayout,
				threatsInfrastructureAddButton,
				threatsInfrastructureYesCannedMessages,
				threatsInfrastructureMitigatedCannedMessages,
				threatsInfrastructureErrorView,
				threatsInfrastructureListErrorView);
	}

	// This method is responsible for setting up a threats & evacs subsection
	private void setupThreatSubsection(String option, TextView label, LinearLayout subsection, ImageButton button, String[] yesCannedMessages, String[] mitigatedCannedMessages, ImageView errorView, LinearLayout listErrorView)
	{
		final int OPTION_NONE = 0;
		final int OPTION_YES = 1;
		final int OPTION_NO = 2;
		final int OPTION_MITIGATED = 3;

		int selectedOption = OPTION_NONE;

		if(option.equals("Yes"))
		{
			selectedOption = OPTION_YES;
		}
		else if(option.equals("Mitigated"))
		{
			selectedOption = OPTION_MITIGATED;
		}
		else if(option.equals("No"))
		{
			selectedOption = OPTION_NO;
		}

		// If the selected option is none or NO, Hide the field.
		if(selectedOption == OPTION_NONE || selectedOption == OPTION_NO)
		{
			label.setVisibility(View.GONE);
			subsection.setVisibility(View.GONE);
			button.setVisibility(View.GONE);
			return;
		}

		// Show the fields
		label.setVisibility(View.VISIBLE);
		subsection.setVisibility(View.VISIBLE);
		button.setVisibility(View.VISIBLE);

		// Clear the list
		subsection.removeAllViews();

		String[] autoCompleteOptions;

		// If the selected option is YES, show the correct options:
		if(selectedOption == OPTION_YES)
		{
			autoCompleteOptions = yesCannedMessages;
		}
		else
		{
			// selectedOption = OPTION_MITIGATED
			autoCompleteOptions = mitigatedCannedMessages;
		}

		button.setOnClickListener(newAddFieldListener(subsection, button, autoCompleteOptions, errorView, listErrorView));
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


	// This method populates all member field variables by obtaining references to them
	// This method then sets up the behavior of the fields
	private void setUpAllFields()
	{
		swipeRefreshLayout = (SwipeRefreshLayout) mRootView.findViewById(R.id.rocSwipeRefreshLayout);
		outermostScrollView = (ScrollView) mRootView.findViewById(R.id.rocParentScrollView);

		//================================================================================================
		//================================================================================================
		// Setting up the major section headers and LinearLayouts
		//================================================================================================
		//================================================================================================
		// Retrieving all of the Section Headers and LinearLayouts
		incidentInfoHeader = (TextView) mRootView.findViewById(R.id.rocIncidentInfoHeader);
		incidentInfoSection = (LinearLayout) mRootView.findViewById(R.id.rocIncidentInfoSection);
		rocIncidentInfoHeader = (TextView) mRootView.findViewById(R.id.rocRocIncidentInfoHeader);
		rocIncidentInfoSection = (LinearLayout) mRootView.findViewById(R.id.rocRocIncidentInfoSection);
		vegFireIncidentScopeHeader = (TextView) mRootView.findViewById(R.id.rocVegFireIncidentScopeHeader);
		vegFireIncidentScopeSection = (LinearLayout) mRootView.findViewById(R.id.rocVegFireIncidentScopeSection);
		weatherInfoHeader = (TextView) mRootView.findViewById(R.id.rocWeatherInfoHeader);
		weatherInfoSection = (LinearLayout) mRootView.findViewById(R.id.rocWeatherInfoSection);
		threatsEvacsHeader = (TextView) mRootView.findViewById(R.id.rocThreatsEvacsHeader);
		threatsEvacsSection = (LinearLayout) mRootView.findViewById(R.id.rocThreatsEvacsSection);
		resourceCommitmentHeader = (TextView) mRootView.findViewById(R.id.rocCalFireResourcesHeader);
		resourceCommitmentSection = (LinearLayout) mRootView.findViewById(R.id.rocCalFireResourcesSection);
		otherInfoHeader = (TextView) mRootView.findViewById(R.id.rocOtherInfoHeader);
		otherInfoSection = (LinearLayout) mRootView.findViewById(R.id.rocOtherInfoSection);
		emailHeader = (TextView) mRootView.findViewById(R.id.rocEmailHeader);
		emailSection = (LinearLayout) mRootView.findViewById(R.id.rocEmailSection);

		// Retrieving references to all section header error views
		incidentInfoHeaderErrorView = (ImageView) mRootView.findViewById(R.id.rocIncidentInfoHeaderError);
		rocIncidentInfoHeaderErrorView = (ImageView) mRootView.findViewById(R.id.rocRocIncidentInfoHeaderError);
		vegFireIncidentScopeHeaderErrorView = (ImageView) mRootView.findViewById(R.id.rocVegFireIncidentScopeHeaderError);
		weatherInfoHeaderErrorView = (ImageView) mRootView.findViewById(R.id.rocWeatherInfoHeaderError);
		threatsEvacsHeaderErrorView = (ImageView) mRootView.findViewById(R.id.rocThreatsEvacsHeaderError);
		resourceCommitmentHeaderErrorView = (ImageView) mRootView.findViewById(R.id.rocCalFireResourcesHeaderError);
		otherInfoHeaderErrorView = (ImageView) mRootView.findViewById(R.id.rocOtherInfoHeaderError);
		emailHeaderErrorView = (ImageView) mRootView.findViewById(R.id.rocEmailHeaderError);

		// Setting all of the section header on click listeners
		incidentInfoHeader.setOnClickListener(toggleCollapsibleSection);
		rocIncidentInfoHeader.setOnClickListener(toggleCollapsibleSection);
		vegFireIncidentScopeHeader.setOnClickListener(toggleCollapsibleSection);
		weatherInfoHeader.setOnClickListener(toggleCollapsibleSection);
		threatsEvacsHeader.setOnClickListener(toggleCollapsibleSection);
		resourceCommitmentHeader.setOnClickListener(toggleCollapsibleSection);
		otherInfoHeader.setOnClickListener(toggleCollapsibleSection);
		emailHeader.setOnClickListener(toggleCollapsibleSection);


		//================================================================================================
		//================================================================================================
		// Setting up individual section fields
		//================================================================================================
		//================================================================================================

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// ROC Form Info Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------

		// Retrieving all of the field objects
		reportTypeSpinner = (Spinner) mRootView.findViewById(R.id.rocReportType);
		incidentNameLabel = (TextView) mRootView.findViewById(R.id.rocIncidentNameLabel);
		incidentNameInfoButton = (ImageButton) mRootView.findViewById(R.id.incidentNameInfoButton);
		incidentNameTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocIncidentName);
		reportTypeLayout = (LinearLayout) mRootView.findViewById(R.id.rocReportTypeSection);
		loadingIncidentInfoProgressBar = (ProgressBar) mRootView.findViewById(R.id.rocLoadingIncidentData);
		messageTextView = (TextView) mRootView.findViewById(R.id.rocMessageTextView);

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Incident Info Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		incidentNumberTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocIncidentNumber);
		incidentNumberInfoButton = (ImageButton) mRootView.findViewById(R.id.incidentInfoButton);
		incidentTypeTextView = (TextView) mRootView.findViewById(R.id.rocIncidentTypeTextView);
		incidentTypeInfoButton = (ImageButton) mRootView.findViewById(R.id.incidentTypeInfoButton);
		incidentTypeSpinner = (Spinner) mRootView.findViewById(R.id.rocIncidentTypeSpinner);
		incidentLatLongLocationInfoButton = (ImageButton) mRootView.findViewById(R.id.latLongLocationInfoButton);
		//incidentLatitudeEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLatitude);
		//incidentLongitudeEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLongitude);
		incidentLatitudeDegreesEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLatitudeDegrees);
		incidentLatitudeMinutesEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLatitudeMinutes);
		incidentLatitudeMinutesFractionEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLatitudeMinutesFraction);
		incidentLongitudeDegreesEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLongitudeDegrees);
		incidentLongitudeMinutesEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLongitudeMinutes);
		incidentLongitudeMinutesFractionEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLongitudeMinutesFraction);

		incidentLocateButton = (ImageButton) mRootView.findViewById(R.id.rocLocateButton);
		incidentLocateInfoButton = (ImageButton) mRootView.findViewById(R.id.rocLocateInfoButton);
		incidentLocateProgressBar = (ProgressBar) mRootView.findViewById(R.id.rocLocationDataProgressBar);
		incidentStateTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocIncidentState);
		// Setting up the incident type spinner using a custom SpinnerAdapter class
		incidentTypeSpinnerItems = new ArrayList<IncidentTypeSpinnerAdapter.SpinnerItem<String>>();
		incidentTypeSelectedItems = new HashSet<String>();
		// Fill the list of spinner items
		String[] incidentTypeOptions = mContext.getResources().getStringArray(R.array.roc_incident_type_checkbox_options);
		for(String incidentType : incidentTypeOptions)
		{
			incidentTypeSpinnerItems.add(new IncidentTypeSpinnerAdapter.SpinnerItem<String>(incidentType, incidentType));
		}
		// Add the header text to the top of the spinner options
		String headerText = "Choose Incident Type(s)";
		incidentTypeSpinner.setAdapter(new IncidentTypeSpinnerAdapter<String>(mContext, headerText, incidentTypeSpinnerItems, incidentTypeSelectedItems, incidentTypeTextView, this));

		// Setting up the location button
		incidentLocateButton.setOnClickListener(locateButtonListener);
		// Setting up the fields to listen for focuschange, so they query location-based form data when no longer focused
		incidentLatitudeDegreesEditText.setOnFocusChangeListener(coordsFocusChangeListener);
		incidentLatitudeMinutesEditText.setOnFocusChangeListener(coordsFocusChangeListener);
		incidentLatitudeMinutesFractionEditText.setOnFocusChangeListener(coordsFocusChangeListener);
		incidentLongitudeDegreesEditText.setOnFocusChangeListener(coordsFocusChangeListener);
		incidentLongitudeMinutesEditText.setOnFocusChangeListener(coordsFocusChangeListener);
		incidentLongitudeMinutesFractionEditText.setOnFocusChangeListener(coordsFocusChangeListener);

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// ROC Incident Info Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		rocInitialCountySpinner = (Spinner) mRootView.findViewById(R.id.rocCounty);
		rocAdditionalCountiesTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocAdditionalCounties);
		rocLocationTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocLocation);
		incidentLocationInfoButton = (ImageButton) mRootView.findViewById(R.id.incidentLocationInfoButton);
		rocLocationTextView.setOnFocusChangeListener(validateFocusChangeLister);

		rocLocationStreetEditText = (EditText) mRootView.findViewById(R.id.rocLocationStreet);
		rocLocationStreetEditText.setOnFocusChangeListener(validateFocusChangeLister);

		rocLocationCrossStreetEditText = (EditText) mRootView.findViewById(R.id.rocLocationCrossStreet);
		rocLocationCrossStreetEditText.setOnFocusChangeListener(validateFocusChangeLister);
		rocLocationCrossStreetEditText.setOnFocusChangeListener(validateFocusChangeLister);

		rocLocationNearestCommunity = (EditText) mRootView.findViewById(R.id.rocLocationNearestCommunity);
		rocLocationNearestCommunity.setOnFocusChangeListener(validateFocusChangeLister);

		rocLocationDistanceFromNearestCommunityEditText = (EditText) mRootView.findViewById(R.id.rocLocationDistanceFromNearestCommunity);
		rocLocationDistanceFromNearestCommunityEditText.setOnFocusChangeListener(validateFocusChangeLister);

		rocLocationDirectionFromNearestCommunitySpinner = (Spinner) mRootView.findViewById(R.id.rocLocationDirectionFromNearestCommunity);
		rocDPASpinner = (Spinner) mRootView.findViewById(R.id.rocDPA);
		rocOwnershipSpinner = (Spinner) mRootView.findViewById(R.id.rocOwnership);
		rocJurisdictionTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocJurisdiction);
		rocJurisdictionTextView.setOnFocusChangeListener(validateFocusChangeLister);
		incidentJurisdictionInfoButton = (ImageButton) mRootView.findViewById(R.id.incidentJurisdictionInfoButton);

		rocStartDateTimeLayout = (LinearLayout) mRootView.findViewById(R.id.rocDateTimeLayout);

		rocStartTimeTextView = (TextView) mRootView.findViewById(R.id.rocStartTime);
		rocStartTimeTextView.setOnFocusChangeListener(validateFocusChangeLister);

		rocStartDateTextView = (TextView) mRootView.findViewById(R.id.rocStartDate);
		rocStartDateTextView.setOnFocusChangeListener(validateFocusChangeLister);

		// Retrieving the Error View field objects
		rocCountyErrorView = (ImageView) mRootView.findViewById(R.id.rocCountyError);
		rocLocationDirectionFromNearestCommunityErrorView = (ImageView) mRootView.findViewById(R.id.rocLocationDirectionFromNearestCommunityError);
		rocDPAErrorView = (ImageView) mRootView.findViewById(R.id.rocDPAError);
		rocOwnershipErrorView = (ImageView) mRootView.findViewById(R.id.rocOwnershipError);

		// Make interacting with the spinners hide the error views
		setupSpinnerErrorView(rocInitialCountySpinner,rocCountyErrorView);
		setupSpinnerErrorView(rocDPASpinner,rocDPAErrorView);
		setupSpinnerErrorView(rocLocationDirectionFromNearestCommunitySpinner, rocLocationDirectionFromNearestCommunityErrorView);
		setupSpinnerErrorView(rocOwnershipSpinner,rocOwnershipErrorView);

		// Setting the Date and Time fields to use appropriate date and time picker dialogs
		rocStartDateTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				final Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				DatePickerDialog datePicker = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener()
				{
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day)
					{
						// For some reason, month is 0-based (January = 0)
						String date = (month+1) + "/" + day + "/" + year;
						rocStartDateTextView.setText(date);
					}
				}, year, month, day);
				datePicker.show();

				rocStartDateTextView.setError(null);
			}
		});
		rocStartTimeTextView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick (View v)
			{
				final Calendar calendar = Calendar.getInstance();
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);

				TimePickerDialog timePicker = new TimePickerDialog(mContext, new TimePickerDialog.OnTimeSetListener()
				{
					@Override
					public void onTimeSet(TimePicker view, int hour, int minute)
					{
						// They want the format to be hhmm (i.e. 2300)
						String time = String.format(Locale.US,"%02d%02d",hour,minute);
						rocStartTimeTextView.setText(time);
					}
				}, hour, minute, true);
				timePicker.show();
				rocStartTimeTextView.setError(null);
			}
		});

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Vegetation Fire Incident Scope Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		vegFireAcreageEditText = (EditText) mRootView.findViewById(R.id.rocVegFireAcreage);
		vegFireAcreageEditText.setOnFocusChangeListener(validateFocusChangeLister);

		vegFireRateOfSpreadSpinner = (Spinner) mRootView.findViewById(R.id.rocVegFireRateOfSpread);
		vegFireFuelTypeLabelTextView = (TextView) mRootView.findViewById(R.id.rocVegFireFuelTypeLabel);
		vegFireFuelTypeLabelTextView.setOnFocusChangeListener(validateFocusChangeLister);

		vegFireFuelTypeGrassCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeGrass);
		vegFireFuelTypeGrassCheckBox.setOnFocusChangeListener(validateFocusChangeLister);

		vegFireFuelTypeBrushCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeBrush);
		vegFireFuelTypeBrushCheckBox.setOnFocusChangeListener(validateFocusChangeLister);

		vegFireFuelTypeTimberCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeTimber);
		vegFireFuelTypeTimberCheckBox.setOnFocusChangeListener(validateFocusChangeLister);

		vegFireFuelTypeOakWoodlandCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeOakWoodland);
		vegFireFuelTypeOakWoodlandCheckBox.setOnFocusChangeListener(validateFocusChangeLister);

		vegFireFuelTypeOtherCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeOther);
		vegFireOtherFuelTypeLabelTextView = (TextView) mRootView.findViewById(R.id.rocVegFireFuelTypeOtherLabel);
		vegFireOtherFuelTypeEditText = (EditText) mRootView.findViewById(R.id.rocVegFireFuelTypeOtherText);
		vegFirePercentContainedEditText = (EditText) mRootView.findViewById(R.id.rocVegFirePercentContained);
		vegFirePercentContainedEditText.setOnFocusChangeListener(validateFocusChangeLister);

		// Retrieving the Error View field objects
		vegFireRateOfSpreadErrorView = (ImageView) mRootView.findViewById(R.id.rocVegFireRateOfSpreadError);

		// Make interacting with the spinners hide the error views
		setupSpinnerErrorView(vegFireRateOfSpreadSpinner,vegFireRateOfSpreadErrorView);
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Weather Information Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		weatherTempEditText = (EditText) mRootView.findViewById(R.id.rocWeatherTemp);
		weatherRelativeHumidityEditText = (EditText) mRootView.findViewById(R.id.rocWeatherRelativeHumidity);
		weatherWindSpeedEditText = (EditText) mRootView.findViewById(R.id.rocWeatherWindSpeed);
		//weatherWindDirectionEditText = (EditText) mRootView.findViewById(R.id.rocWeatherWindDirection);
		weatherWindDirectionSpinner = (Spinner) mRootView.findViewById(R.id.rocWeatherWindDirection);
		weatherGustsEditText = (EditText) mRootView.findViewById(R.id.rocWeatherGusts);
		// Retrieving the Error View field objects
		weatherWindDirectionError = (ImageView) mRootView.findViewById(R.id.rocWeatherWindDirectionError);
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Threats & Evacuations Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		threatsEvacsSpinner = (Spinner) mRootView.findViewById(R.id.rocThreatsEvacuations);
		threatsEvacsListLinearLayout = (LinearLayout) mRootView.findViewById(R.id.rocThreatsEvacuationsList);
		threatsEvacsAddButton = (ImageButton) mRootView.findViewById(R.id.rocThreatsEvacuationsAddButton);
		threatsStructuresSpinner = (Spinner) mRootView.findViewById(R.id.rocThreatsStructures);
		threatsStructuresListLinearLayout = (LinearLayout) mRootView.findViewById(R.id.rocThreatsStructuresList);
		threatsStructuresAddButton = (ImageButton) mRootView.findViewById(R.id.rocThreatsStructuresAddButton);
		threatsInfrastructureSpinner = (Spinner) mRootView.findViewById(R.id.rocThreatsInfrastructure);
		threatsInfrastructureListLinearLayout = (LinearLayout) mRootView.findViewById(R.id.rocThreatsInfrastructureList);
		threatsInfrastructureAddButton = (ImageButton) mRootView.findViewById(R.id.rocThreatsInfrastructureAddButton);
		// Retrieving the section labels
		threatsEvacsLabelTextView = (TextView) mRootView.findViewById(R.id.rocThreatsEvacuationsLabel);
		threatsStructuresLabelTextView = (TextView) mRootView.findViewById(R.id.rocThreatsStructuresLabel);
		threatsInfrastructureLabelTextView = (TextView) mRootView.findViewById(R.id.rocThreatsInfrastructureLabel);
		// Retrieving the Error View field objects
		threatsEvacsErrorView = (ImageView) mRootView.findViewById(R.id.rocThreatsEvacuationsError);
		threatsStructuresErrorView = (ImageView) mRootView.findViewById(R.id.rocThreatsStructuresError);
		threatsInfrastructureErrorView = (ImageView) mRootView.findViewById(R.id.rocThreatsInfrastructureError);
		threatsEvacsListErrorView = (LinearLayout) mRootView.findViewById(R.id.rocThreatsEvacuationsListError);
		threatsStructuresListErrorView = (LinearLayout) mRootView.findViewById(R.id.rocThreatsStructuresListError);
		threatsInfrastructureListErrorView = (LinearLayout) mRootView.findViewById(R.id.rocThreatsInfrastructureListError);

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Other Significant Info Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		otherInfoListLinearLayout = (LinearLayout) mRootView.findViewById(R.id.rocOtherInfoList);
		otherInfoAddButton = (ImageButton) mRootView.findViewById(R.id.rocOtherInfoAddButton);
		// Setting up the add threat buttons
		otherInfoAddButton.setOnClickListener(newAddFieldListener(otherInfoListLinearLayout, otherInfoAddButton , otherSignificantInfoCannedMessages, null, null));

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// CAL FIRE Resources Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		calFireIncidentSpinner = (Spinner) mRootView.findViewById(R.id.rocCalFireIncident);
		calFireResourcesNoneCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesNone);
		calFireResourcesAirCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesAir);
		calFireResourcesGroundCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesGround);
		calFireResourcesAirAndGroundCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesAirAndGround);
		calFireResourcesAirAndGroundAugmentedCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesAirAndGroundAugmented);
		calFireResourcesAgencyRepOrderedCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesAgencyRepOrdered);
		calFireResourcesAgencyRepAssignedCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesAgencyRepAssigned);
		calFireResourcesContinuedCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesContinued);
		calFireResourcesSignificantAugmentationCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesSignificantAugmentation);
		calFireResourcesVlatOrderCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesVlatOrder);
		calFireResourcesVlatAssignedCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesVlatAssigned);
		calFireResourcesNoDivertCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesNoDivert);
		calFireResourcesLatAssignedCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesLatAssigned);
		calFireResourcesAllReleasedCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesAllReleased);

		// Retrieving the Error View field objects
		calFireIncidentErrorView = (ImageView) mRootView.findViewById(R.id.rocCalFireIncidentError);

		// Make interacting with the spinners hide the error views
		setupSpinnerErrorView(calFireIncidentSpinner,calFireIncidentErrorView);

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Email Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		emailTextView = (TextView) mRootView.findViewById(R.id.rocEmail);

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// ROC Submission Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		submitButtonLayout = (LinearLayout) mRootView.findViewById(R.id.rocSubmitButtonSection);
		submitButton = (Button) mRootView.findViewById(R.id.rocSubmitButton);
		cancelButton = (Button) mRootView.findViewById(R.id.rocCancelButton);

		// Setting the button behaviors:
		submitButton.setOnClickListener(submitButtonOnClickListener);
		cancelButton.setOnClickListener(cancelButtonOnClickListener);

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Info Buttons
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		incidentNumberInfoButton.setOnClickListener(incidentNumberInfoButtonOnClickListener);
		incidentNameInfoButton.setOnClickListener(incidentNameInfoButtonOnClickListener);
		incidentTypeInfoButton.setOnClickListener(incidentTypeInfoButtonOnClickListener);
		incidentLatLongLocationInfoButton.setOnClickListener(incidentLatLongLocationInfoButtonOnClickListener);
		incidentLocateInfoButton.setOnClickListener(incidentLocateInfoButtonOnClickListener);
		incidentLocationInfoButton.setOnClickListener(incidentLocationInfoButtonOnClickListener);
		incidentJurisdictionInfoButton.setOnClickListener(incidentJurisdictionInfoButtonOnClickListener);
	}

	private boolean setUIStringField( JSONObject data,String paramName, EditText editText)
	{
		try
		{
			String value = data.getString(paramName);

			if(!value.equals("null"))
			{
				editText.setText(data.getString(paramName));
				editText.setError(null);
			}
			// FIXME - If the value is "null", should the field be emptied out?
		}
		catch(Exception e)
		{
			Log.w("ROCFragment","Unable to parse field from JSONObject for field: \"" + paramName + "\"");
			return false;
		}
		return true;
	}

	// Sets the spinner to the item containing the string
	// returns true if it found and assigned it
	// returns false if string is not one of the options
	private boolean setSpinnerValue(String value, Spinner spinner)
	{
		// Iterate through the spinner's options, and select whichever one matches:
		for(int i = 0; i <  spinner.getCount(); i++)
		{
			if(spinner.getItemAtPosition(i).toString().toLowerCase().equals(value.toLowerCase()))
			{
				spinner.setSelection(i);
				// Indicate that we found the string in the spinner options
				return true;
			}
		}

		// Indicate that the string was not found as one of the spinner options
		return false;
	}

	private boolean setUIStringSpinner(JSONObject data, String paramName, Spinner spinner, ImageView errorView)
	{
		try
		{
			String value = data.getString(paramName);

			if(!value.equals("null"))
			{
				if(setSpinnerValue(value, spinner))
				{
					if(errorView != null)
						errorView.setVisibility(View.GONE);
					return true;
				}
			}
			// FIXME - If the value is "null", should the field be emptied out?
			// FIXME - If the value isn't found in the spinner, should we empty out the spinner?
		}
		catch(Exception e)
		{
			Log.w("ROCFragment","Unable to parse field from JSONObject for field: \"" + paramName + "\"");
		}
		return false;
	}

	private void setUILatLong (JSONObject data)
	{
		try
		{
			double lat = data.getDouble("latitude");
			double lon = data.getDouble("longitude");

			// Converting to DDM:
			incidentLatitudeDegreesEditText.setText(String.format(Locale.US,"%d",(int)getDegree(lat)));
			incidentLatitudeMinutesEditText.setText(String.format(Locale.US,"%d",getMinutesInteger(lat)));

			String latFraction = stringGetFraction(String.format(Locale.US,"%f",getMinutesFraction(lat)));
			if (!latFraction.equals("0000")) {
				incidentLatitudeMinutesFractionEditText.setText(latFraction);
			}

			// NOTE - we multiply the value by -1 because the UI has a hardcoded "-" sign in front of it
			// (all longitudes in california are in the -120 range, and the client doesn't want users to have to manually enter the "-" sign)
			incidentLongitudeDegreesEditText.setText(String.format(Locale.US,"%d",-1 * (int)getDegree(lon)));
			incidentLongitudeMinutesEditText.setText(String.format(Locale.US,"%d",getMinutesInteger(lon)));

			String longFraction = String.format(Locale.US,"%f", getMinutesFraction(lon));
			String longFractionString = stringGetFraction(longFraction);

			if (!longFractionString.equals("0000")) {
				incidentLongitudeMinutesFractionEditText.setText(longFractionString);
			}


			// Removing the errors:
			incidentLatitudeDegreesEditText.setError(null);
			incidentLatitudeMinutesEditText.setError(null);
			incidentLatitudeMinutesFractionEditText.setError(null);
			incidentLongitudeDegreesEditText.setError(null);
			incidentLongitudeMinutesEditText.setError(null);
			incidentLongitudeMinutesFractionEditText.setError(null);
		}
		catch(Exception e)
		{
			Log.e("ROCFragment","Exception populating latlong fields: " + e);
		}
	}

	public void populateROCLocationFields(JSONObject data)
	{
		// Populate the fields:
		Log.e("ROC","Async got back results: " + data);

		// Running this on the UI Thread:

		FragmentActivity activity = getActivity();

		if(activity == null)
			return;

		activity.runOnUiThread((new Runnable()
		{
			private JSONObject data;

			Runnable setData(JSONObject data)
			{
				this.data = data;
				return this;
			}
			@Override
			public void run ()
			{
				// Only try parsing the data if we have valid data
				if(data != null)
				{
					// Assigning the lat/long fields:
					setUILatLong(data);


					setUIStringField(data, "location", rocLocationTextView);
					setUIStringSpinner(data, "county", rocInitialCountySpinner, rocCountyErrorView);
					setUIStringField(data, "state", incidentStateTextView);

					setUIStringSpinner(data, "sra", rocOwnershipSpinner, rocOwnershipErrorView);
					setUIStringSpinner(data, "dpa", rocDPASpinner, rocDPAErrorView);

					setUIStringField(data, "jurisdiction", rocJurisdictionTextView);


					// Keep track if we failed to get any weather field:
					boolean failedToGetWeatherData = false;

					if (!setUIStringField(data, "temperature", weatherTempEditText))
						failedToGetWeatherData = true;
					if (!setUIStringField(data, "relHumidity", weatherRelativeHumidityEditText))
						failedToGetWeatherData = true;
					if (!setUIStringField(data, "windSpeed", weatherWindSpeedEditText))
						failedToGetWeatherData = true;
					if (!setUIStringSpinner(data, "windDirection", weatherWindDirectionSpinner, weatherWindDirectionError))
						failedToGetWeatherData = true;
					if(!setUIStringField(data, "windGust", weatherGustsEditText))
						failedToGetWeatherData = true;

					// If none of the weather requests failed
					if (!failedToGetWeatherData)
					{
						// Mark that we got valid weather data for the form
						gotWeatherData = true;
					}
				}

				// Show the Locate button, hide the progress bar
				// Only show the button if we are creating a new incident:
				if(creatingNewIncident)
				{
					incidentLocateButton.setVisibility(View.VISIBLE);
				}
				incidentLocateProgressBar.setVisibility(View.GONE);

			}
		}).setData(data));
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
	}

	// Used for Section Headers to collapse or expand views
	public OnClickListener toggleCollapsibleSection = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			TextView header = (TextView) v;
			LinearLayout section = null;


			if(header == incidentInfoHeader)
			{
				section = incidentInfoSection;
			}
			else if(header == rocIncidentInfoHeader)
			{
				section = rocIncidentInfoSection;
			}
			else if(header == vegFireIncidentScopeHeader)
			{
				section = vegFireIncidentScopeSection;
			}
			else if(header == weatherInfoHeader)
			{
				section = weatherInfoSection;
			}
			else if(header == threatsEvacsHeader)
			{
				section = threatsEvacsSection;
			}
			else if(header == resourceCommitmentHeader)
			{
				section = resourceCommitmentSection;
			}
			else if(header == otherInfoHeader)
			{
				section = otherInfoSection;
			}
			else if(header == emailHeader)
			{
				section = emailSection;
			}
			else
			{
				return;
			}

			// Clearing the focused view, in case we are collapsing the currently focused view
			clearFocus();

			boolean collapse = section.getVisibility() == View.VISIBLE;

			// Expanding the selected view
			section.setVisibility(collapse ? View.GONE : View.VISIBLE);
			header.setCompoundDrawablesWithIntrinsicBounds(0, 0, collapse ? R.drawable.down_arrow : R.drawable.up_arrow, 0);
		}
	};

	private void collapseAllSections()
	{
		// Collapsing all other views
		incidentInfoSection.setVisibility(View.GONE);
		rocIncidentInfoSection.setVisibility(View.GONE);
		vegFireIncidentScopeSection.setVisibility(View.GONE);
		weatherInfoSection.setVisibility(View.GONE);
		threatsEvacsSection.setVisibility(View.GONE);
		resourceCommitmentSection.setVisibility(View.GONE);
		otherInfoSection.setVisibility(View.GONE);
		emailSection.setVisibility(View.GONE);

		// Setting the icon for all views as the collapsed icon
		incidentInfoHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
		rocIncidentInfoHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
		vegFireIncidentScopeHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
		weatherInfoHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
		threatsEvacsHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
		resourceCommitmentHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
		otherInfoHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
		emailHeader.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow, 0);
	}


	private void clearAllFormFields()
	{
		// We don't want field validation while the fields are clearing
		shouldValidate = false;

		//================================================
		// Incident Info Fields
		//================================================
		incidentNumberTextView.setText("");
		incidentTypeTextView.setText("");
		incidentTypeSpinner.setSelection(0);
		// Decimal Degree Minutes Edit Text:
		incidentLatitudeDegreesEditText.setText("");
		incidentLatitudeMinutesEditText.setText("");
		incidentLatitudeMinutesFractionEditText.setText("");
		incidentLongitudeDegreesEditText.setText("");
		incidentLongitudeMinutesEditText.setText("");
		incidentLongitudeMinutesFractionEditText.setText("");
		incidentStateTextView.setText("");
		// State Variables
		incidentTypeSelectedItems.clear();
		//================================================
		// ROC Incident Info Fields
		//================================================
		rocInitialCountySpinner.setSelection(0);
		rocAdditionalCountiesTextView.setText("");
		rocLocationTextView.setText("");
		rocLocationStreetEditText.setText("");
		rocLocationCrossStreetEditText.setText("");
		rocLocationNearestCommunity.setText("");
		rocLocationDistanceFromNearestCommunityEditText.setText("");
		rocLocationDirectionFromNearestCommunitySpinner.setSelection(0);
		rocDPASpinner.setSelection(0);
		rocOwnershipSpinner.setSelection(0);
		rocJurisdictionTextView.setText("");
		rocStartTimeTextView.setText("");
		rocStartDateTextView.setText("");
		// Input Validation Error Views
		rocCountyErrorView.setVisibility(View.GONE);
		rocLocationDirectionFromNearestCommunityErrorView.setVisibility(View.GONE);
		rocDPAErrorView.setVisibility(View.GONE);
		rocOwnershipErrorView.setVisibility(View.GONE);
		//================================================
		// Vegetation Fire Incident Scope Fields
		//================================================
		vegFireAcreageEditText.setText("");
		vegFireRateOfSpreadSpinner.setSelection(0);
		vegFireFuelTypeGrassCheckBox.setChecked(false);
		vegFireFuelTypeBrushCheckBox.setChecked(false);
		vegFireFuelTypeTimberCheckBox.setChecked(false);
		vegFireFuelTypeOakWoodlandCheckBox.setChecked(false);
		vegFireFuelTypeOtherCheckBox.setChecked(false);
		//TextView vegFireOtherFuelTypeLabelTextView;
		vegFireOtherFuelTypeLabelTextView.setVisibility(View.GONE);
		vegFireOtherFuelTypeEditText.setVisibility(View.GONE);
		vegFireOtherFuelTypeEditText.setText("");
		vegFirePercentContainedEditText.setText("");
		// Input Validation Error Views
		vegFireRateOfSpreadErrorView.setVisibility(View.GONE);
		//================================================
		// Weather Information Fields
		//================================================
		weatherTempEditText.setText("");
		weatherRelativeHumidityEditText.setText("");
		weatherWindSpeedEditText.setText("");
		//weatherWindDirectionEditText.setText("");
		weatherWindDirectionSpinner.setSelection(0);
		weatherGustsEditText.setText("");
		// Input Validation Error Views
		weatherWindDirectionError.setVisibility(View.GONE);
		//================================================
		// Threats & Evacuations Fields
		//================================================
		threatsEvacsSpinner.setSelection(0);
		threatsEvacsListLinearLayout.removeAllViews();
		//ImageButton threatsEvacsAddButton;
		threatsStructuresSpinner.setSelection(0);
		threatsStructuresListLinearLayout.removeAllViews();
		//ImageButton threatsStructuresAddButton;
		threatsInfrastructureSpinner.setSelection(0);
		threatsInfrastructureListLinearLayout.removeAllViews();
		//ImageButton threatsInfrastructureAddButton;

		// Input Validation Error Views
		threatsEvacsErrorView.setVisibility(View.GONE);
		threatsStructuresErrorView.setVisibility(View.GONE);
		threatsInfrastructureErrorView.setVisibility(View.GONE);
		threatsEvacsListErrorView.setVisibility(View.GONE);
		threatsStructuresListErrorView.setVisibility(View.GONE);
		threatsInfrastructureListErrorView.setVisibility(View.GONE);
		//================================================
		// CAL FIRE Resources Fields
		//================================================
		calFireIncidentSpinner.setSelection(0);
		calFireResourcesNoneCheckBox.setChecked(false);
		calFireResourcesAirCheckBox.setChecked(false);
		calFireResourcesGroundCheckBox.setChecked(false);
		calFireResourcesAirAndGroundCheckBox.setChecked(false);
		calFireResourcesAirAndGroundAugmentedCheckBox.setChecked(false);
		calFireResourcesAgencyRepOrderedCheckBox.setChecked(false);
		calFireResourcesAgencyRepAssignedCheckBox.setChecked(false);
		calFireResourcesContinuedCheckBox.setChecked(false);
		calFireResourcesSignificantAugmentationCheckBox.setChecked(false);
		calFireResourcesVlatOrderCheckBox.setChecked(false);
		calFireResourcesVlatAssignedCheckBox.setChecked(false);
		calFireResourcesNoDivertCheckBox.setChecked(false);
		calFireResourcesLatAssignedCheckBox.setChecked(false);
		calFireResourcesAllReleasedCheckBox.setChecked(false);
		//================================================
		// Other Significant Info Fields
		//================================================
		otherInfoListLinearLayout.removeAllViews();
		//================================================
		// Email Fields
		//================================================
		emailTextView.setText("");
		//================================================

		// All fields have been cleared, now we can respect the validation logic
		shouldValidate = true;
	}


	//=======================================================================================================================
	//================================================== Canned Messages ====================================================
	//=======================================================================================================================


	private String[] threatsEvacsYesCannedMessages =
	{
			"Evacuation orders in place",
			"Evacuation center has been established",
			"Evacuation warnings have been lifted",
			"Evacuations orders remain in place",
			"Mandatory evacuations are currently underway"
	};


	private String[] threatsStructuresYesCannedMessages =
	{
			"Structures threatened",
			"Continued threat to structures",
			"Immediate structure threat, evacuations in place",
			"Damage inspection is ongoing ",
			"Inspections are underway to identify damage to critical infrastructure and structures"
	};


	private String[] threatsInfrastructureYesCannedMessages =
	{
			"Immediate structure threat, evacuations in place",
			"Damage inspection is ongoing ",
			"Inspections are underway to identify damage to critical infrastructure and structures",
			"Major power transmission lines threatened",
			"Road closures in the area"
	};


	private String[] threatsEvacsMitigatedCannedMessages =
	{
			"Evacuation warnings have been lifted"
	};


	private String[] threatsStructuresMitigatedCannedMessages =
	{
			"Structure threat mitigated",
			"Damage inspection is ongoing ",
			"Inspections are underway to identify damage to critical infrastructure and structures",
			"All threats mitigated"
	};


	private String[] threatsInfrastructureMitigatedCannedMessages =
	{
			"Damage inspection is ongoing ",
			"Inspections are underway to identify damage to critical infrastructure and structures",
			"All road closures have been lifted",
			"All threats mitigated"
	};

	private String[] otherSignificantInfoCannedMessages =
	{
			"Continued construction and improving control lines",
			"Extensive mop up in oak woodlands",
			"Crews are improving control lines",
			"Ground resources continue to mop-up and strengthen control line",
			"Suppression repair is under way",
			"Fire is in remote location with difficult access",
			"Access and terrain continue to hamper control efforts",
			"Short range spotting causing erratic fire behavior",
			"Medium range spotting observed",
			"Long range spotting observed",
			"Fire has spotted and is well established",
			"Erratic winds, record high temperatures and low humidity are influencing fuels resulting in extreme fire behavior",
			"Red Flag warning in effect in area",
			"Minimal fire behavior observed",
			"CAL FIRE and USFS in unified command",
			"CAL FIRE Type 1 Incident Management Team ordered",
			"Incident Management Team ordered",
			"FMAG application initiated",
			"FMAG has been submitted",
			"FMAG application approved",
			"No updated 209 data at time of report",
			"CAL FIRE Mission Tasking has been approved"
	};

	private TextWatcher incidentNameTextWatcher = new TextWatcher()
	{
		@Override
		public void beforeTextChanged (CharSequence s, int start, int count, int after) {}

		@Override
		public void onTextChanged (CharSequence s, int start, int before, int count) {}

		@Override
		public void afterTextChanged (Editable s)
		{
			if(!isViewingRoc)
				incidentNameChanged();
		}
	};

	//=======================================================================================================================
	//=======================================================================================================================
	//=======================================================================================================================


	// Creates an OnClickListener that adds a new AutoCompleteTextView field to a LinearLayout every time the ImageButton is pressed
	// When the user clicks the "add" button, the "errorView" will be hidden
	private OnClickListener newAddFieldListener(LinearLayout l, ImageButton b, String[] c, ImageView errorView, LinearLayout listErrorView)
	{
		OnClickListener listener = (new OnClickListener()
		{
			LinearLayout layout;
			ImageButton button;
			String[] autoCompleteArray;
			ImageView errorView;
			LinearLayout listErrorView;

			// Acts as constructor for anonymous class
			public OnClickListener init(LinearLayout l, ImageButton b, String[] c, ImageView e, LinearLayout listE)
			{
				layout = l;
				button = b;
				autoCompleteArray = c;
				errorView = e;
				listErrorView = listE;
				return this;
			}

			@Override
			public void onClick (View v)
			{
				// If the errorView has been assigned, hide it when interacting with the form
				if(errorView != null)
				{
					errorView.setVisibility(View.GONE);
				}

				// If the list error view has been assigned, hide it when interacting with the form
				if(listErrorView != null)
				{
					listErrorView.setVisibility(View.GONE);
				}

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
				textView.setHint("Enter info (required)");



				// Setting up the autocomplete
				if(autoCompleteArray != null)
				{
					makeAutoCompleteTextField(textView, autoCompleteArray);
				}

				//------------------------------------------------------------------------------------
				// Image Button
				//------------------------------------------------------------------------------------
				ImageButton button = new ImageButton(mContext);
				button.setImageResource(R.drawable.cross_symbol);

				// Make the button remove the view when clicked
				button.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick (View v)
					{
						// Getting the LinearLayout that holds the AutoCompleteTextView and the ImageButton
						LinearLayout containingLayout = (LinearLayout) v.getParent();

						layout.removeView(containingLayout);
					}
				});

				newLayout.addView(textView);
				newLayout.addView(button);

				layout.addView(newLayout);
			}
		}).init(l, b, c, errorView, listErrorView);

		return listener;
	}

	// Sets up the AutoCompleteTextView textView to use the array options as autocomplete suggestions
	// Also sets up the following behaviors:
	// Makes the autocomplete dropdown appear onfocus
	// Makes the autocomplete dropdown appear when the text is changed
	private void makeAutoCompleteTextField(AutoCompleteTextView textView, String[] options)
	{
		// If an invalid auto-complete array is given, stop
		if(options == null)
			return;

		// Set the autocomplete adapter
		textView.setAdapter(newAutoCompleteAdapter(options));

		// Make the dropdown view show up when the textview is initially focused:
		textView.setOnFocusChangeListener(new View.OnFocusChangeListener()
		{
			@Override
			public void onFocusChange (View view, boolean hasFocus)
			{
				if (hasFocus)
				{
					((AutoCompleteTextView) view).showDropDown();
				}
			}
		});

		// Setting the autocomplete threshold to 1 (so options are given when just one character is typed)
		textView.setThreshold(1);

		// Make the dropdown appear when it is clicked
		textView.setOnClickListener(new View.OnClickListener()
		{
			@Override public void onClick(View v)
			{
				((AutoCompleteTextView) v).showDropDown();
			}
		});
	}
	
	private ArrayAdapter<String> newAutoCompleteAdapter(String[] autoCompleteOptions)
	{
		return new ArrayAdapter<String>(mContext, R.layout.auto_complete_list_item, R.id.item, autoCompleteOptions);
	}


	// Sets a spinner's options to those defined by a String array.
	private void setSpinnerOptions(Spinner spinner, String[] options)
	{
		if(options == null || spinner == null)
			return;
		ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext, R.layout.support_simple_spinner_dropdown_item, options);
		spinner.setAdapter(spinnerAdapter);
	}


	// These functions hold the conversion code between Decimal Degrees and Degrees Decimal Minutes
	private double toDecimalDegrees(int degrees, double minutes)
	{

		return degrees + Math.signum(degrees) * (minutes / 60.0);
	}

	private int getDegree(double decimalDegrees)
	{
		// We want to round towards 0
		if(decimalDegrees > 0)
		{
			return (int) Math.floor(decimalDegrees);
		}
		return (int) Math.ceil(decimalDegrees);
	}

	private int getMinutesInteger(double decimalDegrees)
	{
		return (int) Math.floor(60.0 * (Math.abs(decimalDegrees - getDegree(decimalDegrees))));
	}

	private double getMinutesFraction(double decimalDegrees)
	{
		return (60.0 * (Math.abs(decimalDegrees - getDegree(decimalDegrees)))) % 1.0;
	}

	// Takes a string representing a fraction and returns only the portion to the right of the last decimal point
	// i.e.: takes the string: "0.12345" and returns "12345"
	private String stringGetFraction(String str)
	{
		if(str == null)
			return "0";

		// Get the index of the decimal point, if it exists:
		int decimalPointIndex = str.lastIndexOf('.');

		// If a decimal point was not found
		if(decimalPointIndex == -1)
		{
			// This means the string passed represents a whole number, making the fractional part = 0
			// Return the fractional part of a whole number, which is always "0"
			return "0";
		}

		// If the decimal point index is the last character in the string, that means we have a string like "123."
		if(decimalPointIndex == str.length() - 1)
		{
			// That makes the number a whole number (i.e. "2."), making the fractional part = 0
			// Return the fractional part of a whole number, which is always "0"
			return "0";
		}


		int start = decimalPointIndex + 1;
		int end = str.length();

		// We want to truncate to 4 decimal places
		if(end > start + 4)
		{
			end = start + 4;
		}

		// Return the substring to the right of the decimal point, but no more than 4 digits
		// (we want to truncate to 4 digits
		return str.substring(start,end);
	}

	private boolean isValidLatLong(int latDeg, double latMin, int lonDeg, double lonMin)
	{
		// latitude degrees in [-89,89]
		if(latDeg <= -90 || latDeg >= 90)
			return false;
		// latitude minutes in [0, 60)
		if(latMin < 0.0 || latMin >= 60.0)
			return false;
		// longitude degrees in [-179, 179]
		if(lonDeg <= -180.0 || lonDeg >= 180.0)
			return false;
		// longitude minutes in [0, 60)
		if(lonMin < 0.0 || lonMin >= 60.0)
			return false;

		return true;
	}

	private boolean isValidLatLong(double lat, double lon)
	{
		// latitude in [-90,90]
		if(lat < -90 || lat > 90)
			return false;
		// longitude in [-180, 180]
		if(lon < -180.0 || lon > 180.0)
			return false;

		return true;
	}

	// Creates an asynchronous request to request location-based data
	// and populates location-based form fields when finished
	private void requestIncidentLocationDetails(JSONObject coords)
	{
		// Asyncronously make the request
		new Thread((new Runnable() {

			private JSONObject param;

			public Runnable setParam(JSONObject param)
			{
				this.param = param;
				return this;
			}

			@Override
			public void run ()
			{
				JSONObject data;
				data = mDataManager.getROCLocationInfo(param);


				populateROCLocationFields(data);
			}
		}).setParam(coords)).start();
	}

	// Listens for the user to click on the locate button, and fills in location-based data
	private OnClickListener locateButtonListener = new OnClickListener() {

		@Override
		public void onClick (View v)
		{
			// Hide the button, show the progress bar:
			incidentLocateButton.setVisibility(View.GONE);
			incidentLocateProgressBar.setVisibility(View.VISIBLE);

			// requestIncidentLocationDetails
			// pass in null to indicate that we want to use the device's current GPS coordinates for the location
			requestIncidentLocationDetails(null);
		}
	};


	// This makes the spinner hide the error view when the user selects an item:
	private void setupSpinnerErrorView(Spinner spinner, ImageView errorView)
	{
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			ImageView errorView;

			public AdapterView.OnItemSelectedListener setErrorView(ImageView errorView)
			{
				this.errorView = errorView;
				return this;
			}

			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				errorView.setVisibility(View.GONE);
				if (shouldValidate) {
					isFormInfoValid();
				}
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}

		}.setErrorView(errorView));
	}



	// When on of the location fields is no longer focused, and we have valid coordinates in all four fields, request location-based data.
	private View.OnFocusChangeListener coordsFocusChangeListener = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange (View v, boolean hasFocus)
		{
			// If the user has finished editing the fields:
			if(!hasFocus)
			{
				requestIncidentLocationDetailsForFormCoords();
			}
		}
	};

	// Anytime a field looses focus we should validate the form so that the user always knows the form state.
	private View.OnFocusChangeListener validateFocusChangeLister = new View.OnFocusChangeListener() {
		@Override
		public void onFocusChange (View v, boolean hasFocus)
		{
			// If the user has finished editing the fields:
			if(!hasFocus)
			{
				isFormInfoValid();
			}
		}
	};

	// Reads the current form coordiantes and makes a request for location-based data using them
	void requestIncidentLocationDetailsForFormCoords()
	{
		boolean validData = false;
		//----------------------------------------------------------------
		// Check to make sure each field contains valid input:
		//----------------------------------------------------------------
		double latitude = 0;
		double longitude = 0;

		// Try parsing a double based on what is entered into the fields
		try
		{
			// Building the latitude / longitude from whatever is in the fields:
			int latDegrees = Integer.parseInt(incidentLatitudeDegreesEditText.getText().toString());
			int latMinutesWholePortion = Integer.parseInt(incidentLatitudeMinutesEditText.getText().toString());
			double latMinutesFractionPortion = Double.parseDouble("0." + incidentLatitudeMinutesFractionEditText.getText().toString());

			// NOTE - we multiply the value by -1 because the UI has a hardcoded "-" sign in front of it
			// (all longitudes in california are in the -120 range, and the client doesn't want users to have to manually enter the "-" sign)
			int lonDegrees = -1 * Integer.parseInt(incidentLongitudeDegreesEditText.getText().toString());
			int lonMinutesWholePortion = Integer.parseInt(incidentLongitudeMinutesEditText.getText().toString());
			double lonMinutesFractionPortion = Double.parseDouble("0." + incidentLongitudeMinutesFractionEditText.getText().toString());

			double latMinutes = latMinutesWholePortion + latMinutesFractionPortion;
			double lonMinutes = lonMinutesWholePortion + lonMinutesFractionPortion;

			// Do some data validation:
			if(!isValidLatLong(latDegrees, latMinutes, lonDegrees, lonMinutes))
			{
				throw new NumberFormatException();
			}

			validData = true;



			latitude = toDecimalDegrees(latDegrees, latMinutes);
			longitude = toDecimalDegrees(lonDegrees, lonMinutes);
		}
		catch(Exception e)
		{
			Log.w("ROCFragment","Warning: exception was raised attempting to convert user input lat/long."
					+ " Exception: " + e + "Lat: "
					+ incidentLatitudeDegreesEditText.getText().toString() + " deg, "
					+ incidentLatitudeMinutesEditText.getText().toString() + " . "
					+ incidentLatitudeMinutesFractionEditText.getText().toString() + " min. Lon: "
					+ incidentLongitudeDegreesEditText.getText().toString() + " deg, "
					+ incidentLongitudeMinutesEditText.getText().toString() + " . "
					+ incidentLongitudeMinutesFractionEditText.getText().toString() + " min.");
		}

		JSONObject coords = null;

		// If the form has valid lat/long, get the location data for it:
		if(validData)
		{
			try
			{
				// Build the JSON:
				coords = new JSONObject();

				//HashMap<String,String> coords = new HashMap<String, String>();

				coords.put("latitude",latitude);
				coords.put("longitude",longitude);

				Log.e("ROC","About to retrieve location data for: ("+ coords.getDouble("latitude")+"," + coords.getDouble("longitude") + ")");


			}
			catch(JSONException e)
			{
				Log.w("ROCFragment","Exception building coords. Exception: " + e);

			}
			requestIncidentLocationDetails(coords);
		}
	}

	// Clears all textview errors for a spinner:
	void clearSpinnerFieldErrors(Spinner spinner)
	{
		// We only ever assign the selectedview's error, ensure it's not null before we clear it

		View view = spinner.getSelectedView();

		if(view != null && view instanceof TextView)
		{
			((TextView)view).setError(null);
		}
	}


	// Clears the errors for a list of TextViews
	// This is for the threats and "Other Significant Info" sections
	// The exact hierarchy expected is as follows:
	// A Linear Layout containing several children LinearLayouts
	// These child LinearLayouts should each have a Button and a TextView
	// We want to clear the TextView's error
	void clearTextViewListErrors(LinearLayout layout)
	{
		// Clearing the errors of all children in the above list:
		for(int j = 0; j < layout.getChildCount(); j++)
		{
			LinearLayout childLayout = (LinearLayout) layout.getChildAt(j);

			// Iterate through the layout's children, find the textView
			for(int k = 0; k < childLayout.getChildCount(); k++)
			{
				View childView = childLayout.getChildAt(k);
				if(childView != null && childView instanceof AutoCompleteTextView)
				{
					AutoCompleteTextView textView = (AutoCompleteTextView) childView;
					textView.setError(null);
				}
			}
		}
	}

	// Hides the displayed errors (the red exclamation points) for all fields in the form
	void clearAllFormFieldErrors()
	{
		// Hide all of the section error views:
		incidentInfoHeaderErrorView.setVisibility(View.GONE);
		rocIncidentInfoHeaderErrorView.setVisibility(View.GONE);
		vegFireIncidentScopeHeaderErrorView.setVisibility(View.GONE);
		weatherInfoHeaderErrorView.setVisibility(View.GONE);
		threatsEvacsHeaderErrorView.setVisibility(View.GONE);
		resourceCommitmentHeaderErrorView.setVisibility(View.GONE);
		otherInfoHeaderErrorView.setVisibility(View.GONE);
		emailHeaderErrorView.setVisibility(View.GONE);
		// Hide any other error views that must be shown manually
		rocCountyErrorView.setVisibility(View.GONE);
		rocLocationDirectionFromNearestCommunityErrorView.setVisibility(View.GONE);
		rocDPAErrorView.setVisibility(View.GONE);
		rocOwnershipErrorView.setVisibility(View.GONE);
		vegFireRateOfSpreadErrorView.setVisibility(View.GONE);
		vegFireFuelTypeLabelTextView.setError(null);
		threatsEvacsErrorView.setVisibility(View.GONE);
		threatsInfrastructureErrorView.setVisibility(View.GONE);
		threatsStructuresErrorView.setVisibility(View.GONE);
		threatsEvacsListErrorView.setVisibility(View.GONE);
		threatsStructuresListErrorView.setVisibility(View.GONE);
		threatsInfrastructureListErrorView.setVisibility(View.GONE);
		calFireIncidentErrorView.setVisibility(View.GONE);


		//--------------------------------------------
		// Clearing out all other errors:
		//--------------------------------------------
		incidentNameTextView.setError(null);

		clearSpinnerFieldErrors(reportTypeSpinner);
		incidentTypeTextView.setError(null);
		incidentLatitudeDegreesEditText.setError(null);
		incidentLatitudeMinutesEditText.setError(null);
		incidentLatitudeMinutesFractionEditText.setError(null);
		incidentLongitudeDegreesEditText.setError(null);
		incidentLongitudeMinutesEditText.setError(null);
		incidentLongitudeMinutesFractionEditText.setError(null);
		incidentStateTextView.setError(null);
		incidentInfoHeaderErrorView.setVisibility(View.GONE);
		rocCountyErrorView.setVisibility(View.GONE);
		rocLocationTextView.setError(null);
		rocLocationDirectionFromNearestCommunityErrorView.setVisibility(View.GONE);
		rocDPAErrorView.setVisibility(View.GONE);
		rocOwnershipErrorView.setVisibility(View.GONE);
		rocJurisdictionTextView.setError(null);
		rocStartTimeTextView.setError(null);
		rocStartDateTextView.setError(null);
		rocIncidentInfoHeaderErrorView.setVisibility(View.GONE);
		vegFireAcreageEditText.setError(null);
		vegFireRateOfSpreadErrorView.setVisibility(View.GONE);
		vegFireFuelTypeLabelTextView.setError(null);
		vegFireOtherFuelTypeEditText.setError(null);
		vegFirePercentContainedEditText.setError(null);
		vegFireIncidentScopeHeaderErrorView.setVisibility(View.GONE);
		weatherTempEditText.setError(null);
		weatherRelativeHumidityEditText.setError(null);
		weatherWindSpeedEditText.setError(null);
		weatherGustsEditText.setError(null);
		weatherInfoHeaderErrorView.setVisibility(View.GONE);
		threatsEvacsErrorView.setVisibility(View.GONE);
		threatsStructuresErrorView.setVisibility(View.GONE);
		threatsInfrastructureErrorView.setVisibility(View.GONE);
		threatsEvacsListErrorView.setVisibility(View.GONE);
		threatsStructuresListErrorView.setVisibility(View.GONE);
		threatsInfrastructureListErrorView.setVisibility(View.GONE);
		threatsEvacsHeaderErrorView.setVisibility(View.GONE);
		calFireIncidentErrorView.setVisibility(View.GONE);
		resourceCommitmentHeaderErrorView.setVisibility(View.GONE);
		otherInfoHeaderErrorView.setVisibility(View.GONE);

		// Each of these linearlayouts contains children that might have errors
		clearTextViewListErrors(threatsEvacsListLinearLayout);
		clearTextViewListErrors(threatsStructuresListLinearLayout);
		clearTextViewListErrors(threatsInfrastructureListLinearLayout);
		clearTextViewListErrors(otherInfoListLinearLayout);
		//--------------------------------------------
	}

	// This method validates all form fields and shows error icons on each of the fields
	// Returns true if all required fields are filled in, and all fields have valid data, false otherwise
	private boolean isFormInfoValid()
	{
		boolean isFormValid = true;

		clearAllFormFieldErrors();

		//-------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------
		// Performing form data validation (Ensuring all required fields are filled in)
		//-------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------
		//================================================
		// ROC Form Info Fields
		//================================================
		//--------------------------------
		// incidentName
		// must not be empty, and must not be all whitespace
		// TODO- check if there are additional requirements on incident name
		//--------------------------------


		// Check if the incident name is all whitespace:
		if(incidentNameTextView.getText().toString().trim().length() == 0)
		{
			incidentNameTextView.setError("Incident name must not be empty.");
			isFormValid = false;
		}

		//--------------------------------
		// incidentType
		// at least one incident type is required
		//--------------------------------

		// Check if a report type is selected:
		if(reportTypeSpinner.getSelectedItemPosition() == 0)
		{
			((TextView) reportTypeSpinner.getSelectedView()).setError("You must select a report type.");
			isFormValid = false;
		}

		//================================================
		// Incident Info Fields
		//================================================
		boolean isIncidentInfoValid = true;
		// Only validate the incident fields if we are creating a new incident
		if(creatingNewIncident)
		{
			//--------------------------------
			// incidentNumberTextView
			// not required, no validation
			//--------------------------------
			//--------------------------------
			// incidentType
			// at least one incident type is required
			//--------------------------------
			if(incidentTypeSelectedItems.size() == 0)
			{
				incidentTypeTextView.setError("You must select at least one incident type.");
				isIncidentInfoValid = false;
				isFormValid = false;
			}
			//--------------------------------
			// location
			// lat / long all required:
			// lat degrees in [-89, 89]
			// lat minutes in [0, 60)
			// lon degrees in [-179, 179]
			// lon minutes in [0, 60)
			//--------------------------------
			//------------------
			// Latitude Degrees:
			//------------------
			try
			{
				int latDeg = Integer.parseInt(incidentLatitudeDegreesEditText.getText().toString());
				if(latDeg < -89 || latDeg > 89)
					throw new NumberFormatException();

			}
			catch(Exception e)
			{
				incidentLatitudeDegreesEditText.setError("Latitude degrees must be between -89 and 89.");
				isFormValid = false;
				isIncidentInfoValid = false;
			}
			//------------------
			// Latitude Minutes (whole number portion):
			//------------------
			try
			{
				double latMin = Double.parseDouble(incidentLatitudeMinutesEditText.getText().toString());
				if(latMin < 0 || latMin >= 60)
					throw new NumberFormatException();
			}
			catch(Exception e)
			{
				incidentLatitudeMinutesEditText.setError("Minutes must be between 0 and 59");
				isFormValid = false;
				isIncidentInfoValid = false;
			}

			//------------------
			// Latitude Minutes (fraction portion):
			// Can be any positive integer (any integer with an added decimal on the left is a valid double)
			// i.e. "12934142" with an added decimal becomes "0.12934142", which is valid
			//------------------
			try
			{
				double lonMinutesFractionPortion = Double.parseDouble("0." + incidentLatitudeMinutesFractionEditText.getText().toString());


				if(lonMinutesFractionPortion < 0)
					throw new NumberFormatException();
			}
			catch(Exception e)
			{
				incidentLatitudeMinutesFractionEditText.setError("Minutes decimal portion must be positive");
				isFormValid = false;
				isIncidentInfoValid = false;
			}

			//------------------
			// Longitude Degrees:
			//------------------
			try
			{
				// NOTE - we multiply the value by -1 because the UI has a hardcoded "-" sign in front of it
				// (all longitudes in california are in the -120 range, and the client doesn't want users to have to manually enter the "-" sign)
				int lonDeg = -1 * Integer.parseInt(incidentLongitudeDegreesEditText.getText().toString());
				if(lonDeg < -179 || lonDeg > 179)
					throw new NumberFormatException();
			}
			catch(Exception e)
			{
				incidentLongitudeDegreesEditText.setError("Longitude degrees must be between -179 and 179");
				isFormValid = false;
				isIncidentInfoValid = false;
			}
			//------------------
			// Longitude Minutes (whole number portion):
			//------------------
			try
			{
				double lonMin = Double.parseDouble(incidentLongitudeMinutesEditText.getText().toString());
				if(lonMin < 0 || lonMin >= 60)
					throw new NumberFormatException();
			}
			catch(Exception e)
			{
				incidentLongitudeMinutesEditText.setError("Minutes must be between 0 and 59.");
				isFormValid = false;
				isIncidentInfoValid = false;
			}

			//------------------
			// Longitude Minutes (fraction portion):
			// Can be any positive integer (any integer with an added decimal on the left is a valid double)
			// i.e. "12934142" with an added decimal becomes "0.12934142", which is valid
			//------------------
			try
			{
				double lonMinutesFractionPortion = Double.parseDouble("0." + incidentLongitudeMinutesFractionEditText.getText().toString());

				if(lonMinutesFractionPortion < 0)
					throw new NumberFormatException();
			}
			catch(Exception e)
			{
				incidentLongitudeMinutesFractionEditText.setError("Minutes decimal portion must be positive");
				isFormValid = false;
				isIncidentInfoValid = false;
			}

			//--------------------------------
			// incidentStateTextView
			// must not be empty or blank
			//--------------------------------
			if(incidentStateTextView.getText().toString().trim().length() == 0)
			{
				incidentStateTextView.setError("Incident state / province / region cannot be blank.");
				isFormValid = false;
				isIncidentInfoValid = false;
			}
		}
		//-----------------------------------------
		//-----------------------------------------
		// If there was an error in the incident info section
		// show the error icon on the header
		//-----------------------------------------
		//-----------------------------------------
		if(!isIncidentInfoValid)
		{
			incidentInfoHeaderErrorView.setVisibility(View.VISIBLE);
		}

		//================================================
		// ROC Incident Info Fields
		//================================================
		boolean isRocIncidentInfoValid = true;

		//--------------------------------
		// rocInitialCountySpinner
		// must have something selected
		//--------------------------------
		if(rocInitialCountySpinner.getSelectedItemPosition() == 0)
		{
			rocCountyErrorView.setVisibility(View.VISIBLE);
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocAdditionalCountiesTextView
		// not required, not validated
		//--------------------------------

		//--------------------------------
		// rocLocationTextView
		// required, no validation
		//--------------------------------
		if(rocLocationTextView.getText().toString().trim().length() == 0)
		{
			rocLocationTextView.setError("General location is required.");
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocLocationStreetEditText
		// required, no validation
		//--------------------------------
		if(rocLocationStreetEditText.getText().toString().trim().length() == 0)
		{
			rocLocationStreetEditText.setError("Street is required.");
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocLocationCrossStreetEditText
		// required, no validation
		//--------------------------------
		if(rocLocationCrossStreetEditText.getText().toString().trim().length() == 0)
		{
			rocLocationCrossStreetEditText.setError("Cross Street is required.");
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocLocationNearestCommunity
		// required, no validation
		//--------------------------------
		if(rocLocationNearestCommunity.getText().toString().trim().length() == 0)
		{
			rocLocationNearestCommunity.setError("Nearest Community is required.");
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocLocationDistanceFromNearestCommunityEditText
		// required, no validation
		//--------------------------------
		if(rocLocationDistanceFromNearestCommunityEditText.getText().toString().trim().length() == 0)
		{
			rocLocationDistanceFromNearestCommunityEditText.setError("Distance from Nearest Community is required.");
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocLocationDirectionFromNearestCommunitySpinner
		// required
		//--------------------------------
		if(rocLocationDirectionFromNearestCommunitySpinner.getSelectedItemPosition() == 0)
		{
			rocLocationDirectionFromNearestCommunityErrorView.setVisibility(View.VISIBLE);
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocDPASpinner
		// required
		//--------------------------------
		if(rocDPASpinner.getSelectedItemPosition() == 0)
		{
			rocDPAErrorView.setVisibility(View.VISIBLE);
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocOwnershipSpinner
		// required
		//--------------------------------
		if(rocOwnershipSpinner.getSelectedItemPosition() == 0)
		{
			rocOwnershipErrorView.setVisibility(View.VISIBLE);
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocJurisdictionTextView
		// required, no validation
		//--------------------------------
		if(rocJurisdictionTextView.getText().toString().trim().length() == 0)
		{
			rocJurisdictionTextView.setError("Jurisdiction is required.");
			isRocIncidentInfoValid = false;
			isFormValid = false;
		}
		//--------------------------------
		// rocStartDateTextView
		// required for NEW
		//--------------------------------
		if(currentReportType == ROC_NEW)
		{
			if (rocStartTimeTextView.getText().toString().trim().length() == 0)
			{
				rocStartTimeTextView.setError("Incident start time is required.");
				isRocIncidentInfoValid = false;
				isFormValid = false;
			}
			//--------------------------------
			// rocStartTimeTextView
			// required
			//--------------------------------
			if (rocStartDateTextView.getText().toString().trim().length() == 0)
			{
				rocStartDateTextView.setError("Incident start date is required.");
				isRocIncidentInfoValid = false;
				isFormValid = false;
			}
		}

		//-----------------------------------------
		//-----------------------------------------
		// If there was an error in the roc incident info section
		// show the error icon on the header
		//-----------------------------------------
		//-----------------------------------------
		if(!isRocIncidentInfoValid)
		{
			rocIncidentInfoHeaderErrorView.setVisibility(View.VISIBLE);
		}

		//================================================
		// Vegetation Fire Incident Scope Fields
		//================================================

		// All of the fields are required IFF the vegetation types includes "Fire (Vegetation)"
		boolean vegFireFieldsRequired = false;
		// Keep track if there was an error in the vegetation fire section
		boolean isVegFireInfoValid = true;

		if(isIncidentVegetationFire())
		{
			vegFireFieldsRequired = true;
			Log.e("ROC","Found veg fire!");
		}
		else
		{
			Log.e("ROC","Did not find veg fire!");
		}

		//--------------------------------
		// vegFireAcreageEditText
		// required if(vegFireFieldsRequired), should be a double
		//--------------------------------
		// Check if the field is empty and required
		if(vegFireAcreageEditText.getText().toString().trim().length() == 0 && vegFireFieldsRequired)
		{
			vegFireAcreageEditText.setError("Acreage is required for Vegetation Fire incidents.");
			isFormValid = false;
			isVegFireInfoValid = false;
		}
		// Try to parse it as a double
		else if(vegFireAcreageEditText.getText().toString().trim().length() > 0)
		{
			try
			{
				double vegFireAcreage = Double.parseDouble(vegFireAcreageEditText.getText().toString());

				// Only accept positive numbers
				if(vegFireAcreage < 0.0)
				{
					vegFireAcreageEditText.setError("Acreage must be greater than 0.");
					isFormValid = false;
					isVegFireInfoValid = false;
				}
			}
			catch(Exception e)
			{
				vegFireAcreageEditText.setError("Acreage is required.");
				isFormValid = false;
				isVegFireInfoValid = false;
			}
		}

		//--------------------------------
		// vegFireRateOfSpreadSpinner
		// required if(vegFireFieldsRequired)
		//--------------------------------
		if(vegFireRateOfSpreadSpinner.getSelectedItemPosition() == 0 && vegFireFieldsRequired)
		{
			vegFireRateOfSpreadErrorView.setVisibility(View.VISIBLE);
		}

		//--------------------------------
		// Checkboxes:
		// If (vegFireFieldsRequired), and not FINAL at least one must be checked:
		//--------------------------------
		if(vegFireFieldsRequired && currentReportType != ROC_FINAL)
		{
			do
			{
				if (vegFireFuelTypeGrassCheckBox.isChecked())
				{
					break;
				}
				else if (vegFireFuelTypeBrushCheckBox.isChecked())
				{
					break;
				}
				else if (vegFireFuelTypeTimberCheckBox.isChecked())
				{
					break;
				}
				else if (vegFireFuelTypeOakWoodlandCheckBox.isChecked())
				{
					break;
				}
				else if (vegFireFuelTypeOtherCheckBox.isChecked())
				{
					break;
				}

				vegFireFuelTypeLabelTextView.setError("At least one fuel type is required for Vegetation Fire incidents.");

				// If none of them are checked, we have an error:
				isFormValid = false;
				isVegFireInfoValid = false;
			}
			while (false);
		}

		// If other is checked, the other textbox is required:
		if(vegFireFuelTypeOtherCheckBox.isChecked())
		{
			if(vegFireOtherFuelTypeEditText.getText().toString().trim().length() == 0)
			{
				vegFireOtherFuelTypeEditText.setError("Other fuel type required.");
			}
		}

		//--------------------------------
		// vegFirePercentContainedEditText
		// If (vegFireFieldsRequired), required
		//--------------------------------

		// Check if the field is empty and required
		if(vegFirePercentContainedEditText.getText().toString().trim().length() == 0 && vegFireFieldsRequired)
		{
			vegFirePercentContainedEditText.setError("Percent Contained is required for Vegetation Fire incidents.");
			isFormValid = false;
			isVegFireInfoValid = false;
		}
		// Try to parse it as a double
		else if(vegFirePercentContainedEditText.getText().toString().trim().length() > 0)
		{
			try
			{
				double vegFirePercentContained = Double.parseDouble(vegFirePercentContainedEditText.getText().toString());

				// Only accept positive numbers
				if(vegFirePercentContained < 0.0)
				{
					vegFirePercentContainedEditText.setError("Percent Contained must be greater than 0.");
					isFormValid = false;
					isVegFireInfoValid = false;
				}
				else if(vegFirePercentContained > 100.0)
				{
					vegFirePercentContainedEditText.setError("Percent Contained must be less than or equal to 100.0");
					isFormValid = false;
					isVegFireInfoValid = false;
				}
			}
			catch(Exception e)
			{
				vegFirePercentContainedEditText.setError("Percent Contained should be a number.");
				isFormValid = false;
				isVegFireInfoValid = false;
			}
		}

		//-----------------------------------------
		//-----------------------------------------
		// If there was an error in the roc incident info section
		// show the error icon on the header
		//-----------------------------------------
		//-----------------------------------------
		if(!isVegFireInfoValid)
		{
			vegFireIncidentScopeHeaderErrorView.setVisibility(View.VISIBLE);
		}

		//================================================
		// Weather Information Fields
		//================================================

		boolean isWeatherInfoValid = true;

		//--------------------------------
		// weatherTempEditText
		// should be a double, not required
		//--------------------------------

		// If the field is not empty
		if(weatherTempEditText.getText().toString().trim().length() > 0)
		{
			try
			{
				double temp = Double.parseDouble(weatherTempEditText.getText().toString());
			}
			catch (Exception e)
			{
				weatherTempEditText.setError("Temperature should be a number.");
				isFormValid = false;
				isWeatherInfoValid = false;
			}
		}

		//--------------------------------
		// weatherRelativeHumidityEditText
		// should be a double, not required
		//--------------------------------
		if(weatherRelativeHumidityEditText.getText().toString().trim().length() > 0)
		{
			try
			{
				double humidity = Double.parseDouble(weatherRelativeHumidityEditText.getText().toString());

				// Should not be negative:
				if(humidity < 0.0)
				{
					weatherRelativeHumidityEditText.setError("Humidity should be greater than 0.");
					isFormValid = false;
					isWeatherInfoValid = false;
				}
			}
			catch (Exception e)
			{
				weatherRelativeHumidityEditText.setError("Humidity should be a number.");
				isFormValid = false;
				isWeatherInfoValid = false;
			}
		}

		//--------------------------------
		// weatherRelativeHumidityEditText
		// should be a double, not required
		//--------------------------------
		if(weatherWindSpeedEditText.getText().toString().trim().length() > 0)
		{
			try
			{
				double windSpeed = Double.parseDouble(weatherWindSpeedEditText.getText().toString());

				// Should not be negative:
				if(windSpeed < 0.0)
				{
					weatherWindSpeedEditText.setError("Wind Speed should be greater than 0.");
					isFormValid = false;
					isWeatherInfoValid = false;
				}
			}
			catch (Exception e)
			{
				weatherWindSpeedEditText.setError("Wind Speed should be a number.");
				isFormValid = false;
				isWeatherInfoValid = false;
			}
		}

		//--------------------------------
		// weatherWindDirectionSpinner
		// no requirements
		//--------------------------------


		//--------------------------------
		// weatherGustsEditText
		// should be a double, not required
		//--------------------------------
		if(weatherGustsEditText.getText().toString().trim().length() > 0)
		{
			try
			{
				double gusts = Double.parseDouble(weatherGustsEditText.getText().toString());

				// Should not be negative:
				if(gusts < 0.0)
				{
					weatherGustsEditText.setError("Gusts should be greater than 0.");
					isFormValid = false;
					isWeatherInfoValid = false;
				}
			}
			catch (Exception e)
			{
				weatherGustsEditText.setError("Gusts should be a number.");
				isFormValid = false;
				isWeatherInfoValid = false;
			}
		}


		//-----------------------------------------
		//-----------------------------------------
		// If there was an error in the weather info section
		// show the error icon on the header
		//-----------------------------------------
		//-----------------------------------------
		if(!isWeatherInfoValid)
		{
			weatherInfoHeaderErrorView.setVisibility(View.VISIBLE);
		}


		//================================================
		// Threats & Evacuations Fields
		// This info is only required if vegFireFieldsRequired
		// Apparently, these fields only really apply to vegetation fires
		//================================================

		boolean isThreatsInfoValid = true;

		//--------------------------------
		// threatsEvacsSpinner
		// required if vegFireFieldsRequired
		//--------------------------------
		if(vegFireFieldsRequired && threatsEvacsSpinner.getSelectedItemPosition() == 0)
		{
			threatsEvacsErrorView.setVisibility(View.VISIBLE);
			isFormValid = false;
			isThreatsInfoValid = false;
		}


		//--------------------------------
		// threatsEvacsListLinearLayout
		// required
		//--------------------------------
		// If it's a ROC_FINAL, and spinner is MITIGATED, at least one child is required:
		if((currentReportType == ROC_FINAL && threatsEvacsSpinner.getSelectedItemPosition() == 1))
		{
			if(threatsEvacsListLinearLayout.getChildCount() == 0)
			{
				threatsEvacsListErrorView.setVisibility(View.VISIBLE);
				isThreatsInfoValid = false;
				isFormValid = false;
			}
		}
		// If it's not ROC_FINAL, and spinner is YES, at least one child is required:
		if(currentReportType != ROC_FINAL && (threatsEvacsSpinner.getSelectedItemPosition() == 1))
		{
			if(threatsEvacsListLinearLayout.getChildCount() == 0)
			{
				threatsEvacsListErrorView.setVisibility(View.VISIBLE);
				isThreatsInfoValid = false;
				isFormValid = false;
			}
		}


		// For each of the text fields added, they should not be empty:
		for(int i = 0; i < threatsEvacsListLinearLayout.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) threatsEvacsListLinearLayout.getChildAt(i);

			// Iterate through layout's 2 children, find the textView
			for(int j = 0; j < layout.getChildCount(); j++)
			{
				View childView = layout.getChildAt(j);
				if(childView instanceof AutoCompleteTextView)
				{
					AutoCompleteTextView textView = (AutoCompleteTextView) childView;

					// If it's empty, show an error:
					if(textView.getText().toString().trim().length() == 0)
					{
						textView.setError("Evacuation info cannot be empty.");
						isFormValid = false;
						isThreatsInfoValid = false;
					}
				}
			}
		}


		//--------------------------------
		// threatsStructuresSpinner
		// required if vegFireFieldsRequired
		//--------------------------------
		if(vegFireFieldsRequired && threatsStructuresSpinner.getSelectedItemPosition() == 0)
		{
			threatsStructuresErrorView.setVisibility(View.VISIBLE);
			isFormValid = false;
			isThreatsInfoValid = false;
		}


		//--------------------------------
		// threatsStructuresListLinearLayout
		// required
		//--------------------------------
		// If it's a ROC_FINAL, and spinner is MITIGATED, at least one child is required:
		if((currentReportType == ROC_FINAL && threatsStructuresSpinner.getSelectedItemPosition() == 1))
		{
			if(threatsStructuresListLinearLayout.getChildCount() == 0)
			{
				threatsStructuresListErrorView.setVisibility(View.VISIBLE);
				isThreatsInfoValid = false;
				isFormValid = false;
			}
		}
		// If it's not ROC_FINAL, and spinner is YES, at least one child is required:
		if(currentReportType != ROC_FINAL && (threatsStructuresSpinner.getSelectedItemPosition() == 1))
		{
			if(threatsStructuresListLinearLayout.getChildCount() == 0)
			{
				threatsStructuresListErrorView.setVisibility(View.VISIBLE);
				isThreatsInfoValid = false;
				isFormValid = false;
			}
		}

		// For each of the text fields added, they should not be empty:
		for(int i = 0; i < threatsStructuresListLinearLayout.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) threatsStructuresListLinearLayout.getChildAt(i);

			// Iterate through layout's 2 children, find the textView
			for(int j = 0; j < layout.getChildCount(); j++)
			{
				View childView = layout.getChildAt(j);
				if(childView instanceof AutoCompleteTextView)
				{
					AutoCompleteTextView textView = (AutoCompleteTextView) childView;

					// If it's empty, show an error:

					if(textView.getText().toString().trim().length() == 0)
					{
						textView.setError("Structures Threat info cannot be empty.");
						isFormValid = false;
						isThreatsInfoValid = false;
					}
				}
			}
		}


		//--------------------------------
		// threatsInfrastructureSpinner
		// required if vegFireFieldsRequired
		//--------------------------------
		if(vegFireFieldsRequired && threatsInfrastructureSpinner.getSelectedItemPosition() == 0)
		{
			threatsInfrastructureErrorView.setVisibility(View.VISIBLE);
			isFormValid = false;
			isThreatsInfoValid = false;
		}


		//--------------------------------
		// threatsInfrastructureListLinearLayout
		// required
		//--------------------------------
		// If it's a ROC_FINAL, and spinner is MITIGATED, at least one child is required:
		if((currentReportType == ROC_FINAL && threatsInfrastructureSpinner.getSelectedItemPosition() == 1))
		{
			if(threatsInfrastructureListLinearLayout.getChildCount() == 0)
			{
				threatsInfrastructureListErrorView.setVisibility(View.VISIBLE);
				isThreatsInfoValid = false;
				isFormValid = false;
			}
		}
		// If it's not ROC_FINAL, and spinner is YES, at least one child is required:
		if(currentReportType != ROC_FINAL && (threatsInfrastructureSpinner.getSelectedItemPosition() == 1))
		{
			if(threatsInfrastructureListLinearLayout.getChildCount() == 0)
			{
				threatsInfrastructureListErrorView.setVisibility(View.VISIBLE);
				isThreatsInfoValid = false;
				isFormValid = false;
			}
		}

		// For each of the text fields added, they should not be empty:
		for(int i = 0; i < threatsInfrastructureListLinearLayout.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) threatsInfrastructureListLinearLayout.getChildAt(i);

			// Iterate through layout's 2 children, find the textView
			for(int j = 0; j < layout.getChildCount(); j++)
			{
				View childView = layout.getChildAt(j);
				if(childView instanceof AutoCompleteTextView)
				{
					AutoCompleteTextView textView = (AutoCompleteTextView) childView;

					// If it's empty, show an error:

					if(textView.getText().toString().trim().length() == 0)
					{
						textView.setError("Infrastructure Threat info cannot be empty.");
						isFormValid = false;
						isThreatsInfoValid = false;
					}
				}
			}
		}

		//-----------------------------------------
		//-----------------------------------------
		// If there was an error in the threats info section
		// show the error icon on the header
		//-----------------------------------------
		//-----------------------------------------
		if(!isThreatsInfoValid)
		{
			threatsEvacsHeaderErrorView.setVisibility(View.VISIBLE);
		}

		//================================================
		// Resource Commitment Fields
		//================================================
		boolean isResourceInfoValid = true;

		//--------------------------------
		// calFireIncidentSpinner
		// required
		//--------------------------------
		if(calFireIncidentSpinner.getSelectedItemPosition() == 0)
		{
			calFireIncidentErrorView.setVisibility(View.VISIBLE);
			isResourceInfoValid = false;
			isFormValid = false;
		}

		//-----------------------------------------
		//-----------------------------------------
		// If there was an error in the resource info section
		// show the error icon on the header
		//-----------------------------------------
		//-----------------------------------------
		if(!isResourceInfoValid)
		{
			resourceCommitmentHeaderErrorView.setVisibility(View.VISIBLE);
		}

		//================================================
		// Other Significant Info Fields
		//================================================

		boolean isOtherInfoValid = true;

		//--------------------------------
		// otherInfoListLinearLayout
		// required
		//--------------------------------

		// For each of the text fields added, they should not be empty:
		for(int i = 0; i < otherInfoListLinearLayout.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) otherInfoListLinearLayout.getChildAt(i);

			// Iterate through layout's 2 children, find the textView
			for(int j = 0; j < layout.getChildCount(); j++)
			{
				View childView = layout.getChildAt(j);
				if(childView instanceof AutoCompleteTextView)
				{
					AutoCompleteTextView textView = (AutoCompleteTextView) childView;

					// If it's empty, show an error:

					if(textView.getText().toString().trim().length() == 0)
					{
						textView.setError("Significant info cannot be empty.");
						isFormValid = false;
						isOtherInfoValid = false;
					}
				}
			}
		}

		//-----------------------------------------
		//-----------------------------------------
		// If there was an error in the other info section
		// show the error icon on the header
		//-----------------------------------------
		//-----------------------------------------
		if(!isOtherInfoValid)
		{
			otherInfoHeaderErrorView.setVisibility(View.VISIBLE);
		}

		//================================================
		// Email Fields
		//================================================
		// No Validation here
		//================================================

		//-------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------
		return isFormValid;
	}

	// This method turns the form fields into a ReportOnConditionData object
	private ReportOnConditionData formToReportOnConditionData()
	{
		ReportOnConditionData data = new ReportOnConditionData();
		//================================================
		// ROC Form Info Fields
		//================================================
		// Set current time as creation time
		data.datecreated = Calendar.getInstance().getTime();

		data.isForNewIncident = true;
		data.incidentid = -1;

		// If the incidentData exists, set the incident id, and set isForNewIncident to false (indicating that this ROC should not create an incident)
		if(incidentData != null)
		{
			data.incidentid = incidentData.getIncidentId();
			data.isForNewIncident = false;
		}

		// If we got valid weather data:
		data.weatherDataAvailable = gotWeatherData;


		// If we're creating a new incident, carefully format the incident name:
		// The incident name should be prefixed with the user's "org state" and "org prefix"
		// i.e. Taborda's org state is "CA"
		// Taborda's org prefix is "TAB"
		// Therefore, incidents created by users in the Taborda org should be prefixed with "CA TAB"

		if(data.isForNewIncident)
		{
			String incidentNameOrgPrefix = "";
			String incidentNameStatePrefix = "";
			OrganizationPayload orgPayload = mDataManager.getCurrentOrganziation();
			if(orgPayload != null)
			{
				incidentNameOrgPrefix = orgPayload.getPrefix() + " ";
				incidentNameStatePrefix = orgPayload.getState() + " ";
			}
			// Add the prefix to the incident name:
			data.incidentname = incidentNameStatePrefix + incidentNameOrgPrefix + incidentNameTextView.getText().toString();
		}
		// If we're not creating a new incident, the incident name should already have this formatting
		else
		{
			data.incidentname = incidentNameTextView.getText().toString();
		}

		data.reportType = reportTypeSpinner.getSelectedItem().toString();

		//================================================
		// Incident Info Fields
		//================================================

		data.incidentnumber = incidentNumberTextView.getText().toString();
		data.incidentTypes = new ArrayList<String>(incidentTypeSelectedItems);

		// Location data:
		// Building the latitude / longitude from whatever is in the fields:
		int latDeg = Integer.parseInt(incidentLatitudeDegreesEditText.getText().toString());
		int latMinutesWholePortion = Integer.parseInt(incidentLatitudeMinutesEditText.getText().toString());
		double latMinutesFractionPortion = Double.parseDouble("0." + incidentLatitudeMinutesFractionEditText.getText().toString());

		// NOTE - we multiply the value by -1 because the UI has a hardcoded "-" sign in front of it
		// (all longitudes in california are in the -120 range, and the client doesn't want users to have to manually enter the "-" sign)
		int lonDeg = -1 * Integer.parseInt(incidentLongitudeDegreesEditText.getText().toString());
		int lonMinutesWholePortion = Integer.parseInt(incidentLongitudeMinutesEditText.getText().toString());
		double lonMinutesFractionPortion = Double.parseDouble("0." + incidentLongitudeMinutesFractionEditText.getText().toString());

		double latMin = latMinutesWholePortion + latMinutesFractionPortion;
		double lonMin = lonMinutesWholePortion + lonMinutesFractionPortion;


		data.latitude = toDecimalDegrees(latDeg, latMin);
		data.longitude = toDecimalDegrees(lonDeg, lonMin);
		data.incidentState = incidentStateTextView.getText().toString();

		//================================================
		// ROC Incident Info Fields
		//================================================

		data.county = rocInitialCountySpinner.getSelectedItem().toString();
		data.additionalAffectedCounties = rocAdditionalCountiesTextView.getText().toString();
		data.location = rocLocationTextView.getText().toString();
		data.street = rocLocationStreetEditText.getText().toString();
		data.crossStreet = rocLocationCrossStreetEditText.getText().toString();
		data.nearestCommunity = rocLocationNearestCommunity.getText().toString();
		data.milesFromNearestCommunity = rocLocationDistanceFromNearestCommunityEditText.getText().toString();
		data.directionFromNearestCommunity = rocLocationDirectionFromNearestCommunitySpinner.getSelectedItem().toString();
		data.dpa = rocDPASpinner.getSelectedItem().toString();
		data.ownership = rocOwnershipSpinner.getSelectedItem().toString();
		data.jurisdiction = rocJurisdictionTextView.getText().toString();


		// Parsing the date / time strings:
		SimpleDateFormat startDateFormatter = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
		try
		{
			data.startDate = startDateFormatter.parse(rocStartDateTextView.getText().toString());
		}
		catch(Exception e)
		{
			Log.e("ROC","Unable to parse start date \"" + rocStartDateTextView.getText().toString() + "\" from form.");

			// If there is a previous ROC, fallback to that ROC's startDate
			if(lastRocData != null)
				data.startDate = lastRocData.startDate;
				// Otherwise, revert to current time
			else
				data.startDate = Calendar.getInstance().getTime();
		}


		SimpleDateFormat startTimeFormatter = new SimpleDateFormat("HHmm", Locale.getDefault());
		try
		{
			data.startTime = startTimeFormatter.parse(rocStartTimeTextView.getText().toString());
		}
		catch(Exception e)
		{
			Log.e("ROC","Unable to parse start time \"" + rocStartTimeTextView.getText().toString() + "\" from form.");

			// If there is a previous ROC, fallback to that ROC's startTime
			if(lastRocData != null)
				data.startTime = lastRocData.startTime;
				// Otherwise, revert to current time
			else
				data.startTime = Calendar.getInstance().getTime();
		}

		//================================================
		// Vegetation Fire Incident Scope Fields
		//================================================

		data.acreage = vegFireAcreageEditText.getText().toString();
		data.spreadRate = vegFireRateOfSpreadSpinner.getSelectedItem().toString();


		// Making an array of all checkboxes to iterate over to reduce code
		CheckBox[] fuelTypeCheckboxes = {
				vegFireFuelTypeGrassCheckBox,
				vegFireFuelTypeBrushCheckBox,
				vegFireFuelTypeTimberCheckBox,
				vegFireFuelTypeOakWoodlandCheckBox,
				vegFireFuelTypeOtherCheckBox
		};

		data.fuelTypes = new ArrayList<String>();
		for(CheckBox checkBox : fuelTypeCheckboxes)
		{
			if(checkBox.isChecked())
				data.fuelTypes.add(checkBox.getText().toString());
		}

		// Handle the other fuel type field
		if (vegFireFuelTypeOtherCheckBox.isChecked())
		{
			data.otherFuelTypes = vegFireOtherFuelTypeEditText.getText().toString();
		}
		else
		{
			data.otherFuelTypes = "";
		}

		data.percentContained = vegFirePercentContainedEditText.getText().toString();

		//================================================
		// Weather Information Fields
		//================================================

		data.temperature = weatherTempEditText.getText().toString();
		data.relHumidity = weatherRelativeHumidityEditText.getText().toString();
		data.windSpeed = weatherWindSpeedEditText.getText().toString();
		data.windDirection = weatherWindDirectionSpinner.getSelectedItem().toString();
		data.windGusts = weatherGustsEditText.getText().toString();

		//================================================
		// Threats & Evacuations Fields
		//================================================

		//----------------------------------------
		// Evacuations
		//----------------------------------------
		if(threatsEvacsSpinner.getSelectedItemPosition() != 0)
		{
			data.evacuations = threatsEvacsSpinner.getSelectedItem().toString();
		}
		else
		{
			data.evacuations = "";
		}
		data.evacuationsInProgress = new ArrayList<String>();

		// For each of the text fields added, add the text
		for(int i = 0; i < threatsEvacsListLinearLayout.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) threatsEvacsListLinearLayout.getChildAt(i);

			// Iterate through layout's 2 children, find the textView
			for(int j = 0; j < layout.getChildCount(); j++)
			{
				View childView = layout.getChildAt(j);
				if(childView instanceof AutoCompleteTextView)
				{
					data.evacuationsInProgress.add(((AutoCompleteTextView) childView).getText().toString());
				}
			}
		}
		//----------------------------------------
		// Structure Threats
		//----------------------------------------
		if(threatsStructuresSpinner.getSelectedItemPosition() != 0)
		{
			data.structureThreats = threatsStructuresSpinner.getSelectedItem().toString();
		}
		else
		{
			data.structureThreats = "";
		}
		data.structureThreatsInProgress = new ArrayList<String>();

		// For each of the text fields added, add the text
		for(int i = 0; i < threatsStructuresListLinearLayout.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) threatsStructuresListLinearLayout.getChildAt(i);

			// Iterate through layout's 2 children, find the textView
			for(int j = 0; j < layout.getChildCount(); j++)
			{
				View childView = layout.getChildAt(j);
				if(childView instanceof AutoCompleteTextView)
				{
					data.structureThreatsInProgress.add(((AutoCompleteTextView) childView).getText().toString());
				}
			}
		}

		//----------------------------------------
		// Infrastructure Threats
		//----------------------------------------
		if(threatsInfrastructureSpinner.getSelectedItemPosition() != 0)
		{
			data.infrastructureThreats = threatsInfrastructureSpinner.getSelectedItem().toString();
		}
		else
		{
			data.infrastructureThreats = "";
		}
		data.infrastructureThreatsInProgress = new ArrayList<String>();

		// For each of the text fields added, add the text
		for(int i = 0; i < threatsInfrastructureListLinearLayout.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) threatsInfrastructureListLinearLayout.getChildAt(i);

			// Iterate through layout's 2 children, find the textView
			for(int j = 0; j < layout.getChildCount(); j++)
			{
				View childView = layout.getChildAt(j);
				if(childView instanceof AutoCompleteTextView)
				{
					data.infrastructureThreatsInProgress.add(((AutoCompleteTextView) childView).getText().toString());
				}
			}
		}

		//================================================
		// Resource Commitment Fields
		//================================================

		data.calfireIncident = calFireIncidentSpinner.getSelectedItem().toString();

		// Making an array of all checkboxes to iterate over to reduce code
		CheckBox[] resourceCheckboxes = {
				calFireResourcesNoneCheckBox,
				calFireResourcesAirCheckBox,
				calFireResourcesGroundCheckBox,
				calFireResourcesAirAndGroundCheckBox,
				calFireResourcesAirAndGroundAugmentedCheckBox,
				calFireResourcesAgencyRepOrderedCheckBox,
				calFireResourcesAgencyRepAssignedCheckBox,
				calFireResourcesContinuedCheckBox,
				calFireResourcesSignificantAugmentationCheckBox,
				calFireResourcesVlatOrderCheckBox,
				calFireResourcesVlatAssignedCheckBox,
				calFireResourcesNoDivertCheckBox,
				calFireResourcesLatAssignedCheckBox,
				calFireResourcesAllReleasedCheckBox
		};

		data.resourcesAssigned = new ArrayList<String>();

		for(CheckBox checkBox : resourceCheckboxes)
		{
			if(checkBox.isChecked())
				data.resourcesAssigned.add(checkBox.getText().toString());
		}

		//================================================
		// Other Significant Info Fields
		//================================================

		data.otherSignificantInfo = new ArrayList<String>();

		// For each of the text fields added, add the text
		for(int i = 0; i < otherInfoListLinearLayout.getChildCount(); i++)
		{
			LinearLayout layout = (LinearLayout) otherInfoListLinearLayout.getChildAt(i);

			// Iterate through layout's 2 children, find the textView
			for(int j = 0; j < layout.getChildCount(); j++)
			{
				View childView = layout.getChildAt(j);
				if(childView instanceof AutoCompleteTextView)
				{
					data.otherSignificantInfo.add(((AutoCompleteTextView) childView).getText().toString());
				}
			}
		}

		//================================================
		// Email Fields
		//================================================
		data.email = emailTextView.getText().toString();

		//-------------------------------------------------------------------------------------------------
		//-------------------------------------------------------------------------------------------------
		data.sendStatus = ReportSendStatus.WAITING_TO_SEND;

		return data;
	}

	// This method removes focus from all fields
	private void clearFocus()
	{
		View currentlyFocusedView = mContext.getCurrentFocus();
		if(currentlyFocusedView != null)
		{
			currentlyFocusedView.clearFocus();
		}
	}

	// When the user presses the submit button:
	OnClickListener submitButtonOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick (View v)
		{
			// Clearing focus so the view doesn't scroll
			clearFocus();

			if(isFormInfoValid())
			{
				ReportOnConditionData data = formToReportOnConditionData();

				Log.e("ROC","ROC Form data yielded the following JSON: " + data.toJSON());
				data.sendStatus = ReportSendStatus.WAITING_TO_SEND;


				// Adding ROC Data to store and forward table so it is sent to server.
				mDataManager.addReportOnConditionToStoreAndForward(data);
				RestClient.postReportOnConditions();

				mContext.getSupportFragmentManager().beginTransaction().remove(rocFragment).commit();
				mContext.onBackPressed();
			}
		}
	};

	// When the user presses the cancel button:
	OnClickListener cancelButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick (View v)
		{
			mContext.getSupportFragmentManager().beginTransaction().remove(rocFragment).commit();
			mContext.onBackPressed();
		}
	};

	//================================================
	// Info Button On Click Listeners
	//================================================

	OnClickListener incidentNumberInfoButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			alertView(mContext.getString(R.string.incident_number_hint));
		}
	};

	OnClickListener incidentNameInfoButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			alertView(mContext.getString(R.string.incident_name_hint));
		}
	};

	OnClickListener incidentTypeInfoButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			alertView(mContext.getString(R.string.incident_type_box_hint));
		}
	};

	OnClickListener incidentLatLongLocationInfoButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			alertView(mContext.getString(R.string.lattitude_longitude_hint));
		}
	};

	OnClickListener incidentLocateInfoButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			alertView(mContext.getString(R.string.current_location_hint));
		}
	};

	OnClickListener incidentLocationInfoButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			alertView(mContext.getString(R.string.location_hint));
		}
	};

	OnClickListener incidentJurisdictionInfoButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			alertView(mContext.getString(R.string.jurisdiction_hint));
		}
	};

	private void alertView(String message ) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
		dialog.setTitle( "Information" )
				.setIcon(R.drawable.ic_launcher)
				.setMessage(message)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
					}
				}).show();
	}
}
