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

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import scout.edu.mit.ll.nics.android.api.payload.IncidentPayload;
import scout.edu.mit.ll.nics.android.utils.Constants;

public class ReportOnConditionFragment extends Fragment
{
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

	//================================================
	// ROC Form Info Fields
	//================================================
	AutoCompleteTextView incidentNameTextView;
	Spinner reportTypeSpinner;
	LinearLayout reportTypeLayout;


	//================================================
	// Incident Info Fields
	//================================================
	AutoCompleteTextView incidentNumberTextView;
	TextView incidentTypeTextView;
	Spinner incidentTypeSpinner;
	//EditText incidentLatitudeEditText;
	//EditText incidentLongitudeEditText;
	// Decimal Degree Minutes Edit Text:
	EditText incidentLatitudeDegreesEditText;
	EditText incidentLatitudeMinutesEditText;
	EditText incidentLongitudeDegreesEditText;
	EditText incidentLongitudeMinutesEditText;
	ImageButton incidentLocateButton;
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
	Spinner rocDPASpinner;
	Spinner rocOwnershipSpinner;
	AutoCompleteTextView rocJurisdictionTextView;
	TextView rocStartTimeTextView;
	TextView rocStartDateTextView;
	// Input Validation Error Views
	ImageView rocCountyErrorView;
	ImageView rocDPAErrorView;
	ImageView rocOwnershipErrorView;
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
	//================================================
	// Resource Commitment Fields
	//================================================
	Spinner calFireIncidentSpinner;
	// From Previous ROC Checkboxes:
	//CheckBox calFireResourcesNoneCheckBox;
	//CheckBox calFireResourcesAirCheckBox;
	//CheckBox calFireResourcesGroundCheckBox;
	//CheckBox calFireResourcesAllReleasedCheckBox;
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
	private DataManager mDataManager;

	private MainActivity mContext;

	private String[] allIncidentNames;


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

	//private DamageReportPayload mCurrentPayload;
	//private DamageReportData mCurrentData;
	//private long mReportId;


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
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

		return mRootView;
	}

	@Override
	public void onResume()
	{

		// Ensure no member variables are null:
		setUpAllFields();


		//------------------------------------------------------------------------------------------------
		// Retrieve all data from dataManager for field auto-fills, required fields, etc.


		allIncidentNames = mDataManager.getAllIncidentNames();

		// TODO - pull all data that we need from mDataManager for autocomplete
		// (stuff like all incident names, incident numbers, etc.)
		//	ArrayList<IncidentTypePayload> activeIncidentTypes = activeIncident.getIncidentIncidenttypes();

		//	for(IncidentTypePayload type : activeIncidentTypes)
		//	{
		//		IncidentTypePayload.IncidentTypeIncidentTypePayload t = type.getIncidentType();
		//		String typeName = type.getIncidentType().getIncidentTypeName();
		//		Log.e("hullo", "Incident \"" + activeIncident.getIncidentName() + "\" got type: \"" + typeName + "\"");
		//	}
		IncidentPayload activeIncident = mDataManager.getActiveIncident();

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

		// Set up the Incident Name field to autocomplete based on incident names.
		makeAutoCompleteTextField(incidentNameTextView, allIncidentNames);

		// Hide the submit button
		submitButton.setVisibility(View.INVISIBLE);

		//================================================================================================
		// Set up Field Behaviors
		//================================================================================================


		//------------------------------------------------------------------------------------------------
		// Make Entering an Incident Name show the report type spinner
		//------------------------------------------------------------------------------------------------
		incidentNameTextView.addTextChangedListener(new TextWatcher()
		{
			// The textwatcher will perform a corresponding callback
			ReportOnConditionFragment fragment;
			public TextWatcher setFragment(ReportOnConditionFragment f)
			{
				this.fragment = f;
				return this;
			}

			@Override
			public void beforeTextChanged (CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged (CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged (Editable s)
			{
				fragment.incidentNameChanged();
			}
		}.setFragment(this));

		//------------------------------------------------------------------------------------------------
		// Make selecting a report type show the correct fields:
		//------------------------------------------------------------------------------------------------
		reportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			// The OnItemSelectedListener will perform a corresponding callback
			ReportOnConditionFragment fragment;
			public AdapterView.OnItemSelectedListener setFragment(ReportOnConditionFragment f)
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
			// The OnItemSelectedListener will perform a corresponding callback
			ReportOnConditionFragment fragment;
			public AdapterView.OnItemSelectedListener setFragment(ReportOnConditionFragment f)
			{
				this.fragment = f;
				return this;
			}

			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				threatsEvacsErrorView.setVisibility(View.GONE);
				fragment.threatsEvacsSpinnerChanged(position);
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}
		}.setFragment(this));

		//------------------------------------------------------------------------------------------------
		// Making the threats & evacs Structures Threat Spinner show the correct options
		//------------------------------------------------------------------------------------------------
		threatsStructuresSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			// The OnItemSelectedListener will perform a corresponding callback
			ReportOnConditionFragment fragment;
			public AdapterView.OnItemSelectedListener setFragment(ReportOnConditionFragment f)
			{
				this.fragment = f;
				return this;
			}

			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				threatsStructuresErrorView.setVisibility(View.GONE);
				fragment.threatsStructuresChanged(position);
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}
		}.setFragment(this));

		//------------------------------------------------------------------------------------------------
		// Making the threats & evacs Infrastructure Threat Spinner show the correct options
		//------------------------------------------------------------------------------------------------
		threatsInfrastructureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			// The OnItemSelectedListener will perform a corresponding callback
			ReportOnConditionFragment fragment;
			public AdapterView.OnItemSelectedListener setFragment(ReportOnConditionFragment f)
			{
				this.fragment = f;
				return this;
			}

			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				threatsInfrastructureErrorView.setVisibility(View.GONE);
				fragment.threatsInfrastructureChanged(position);
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}
		}.setFragment(this));

		//------------------------------------------------------------------------------------------------
		// Don't let the incident type spinner ever be the 0th item:
		//------------------------------------------------------------------------------------------------
		incidentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			ReportOnConditionFragment fragment;
			public AdapterView.OnItemSelectedListener setFragment(ReportOnConditionFragment f)
			{
				this.fragment = f;
				return this;
			}
			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{
				incidentTypeTextView.setError(null);
				fragment.incidentTypeChanged();
			}

			@Override
			public void onNothingSelected (AdapterView<?> parent) {}
		}.setFragment(this));


/*		textView.addTextChangedListener(new TextWatcher()
		{
			// Store the textView this TextWatcher is responsible for
			AutoCompleteTextView textView;
			// Assigns the textView so the TextWatcher instance keeps a reference to the textView it's responsible for
			public TextWatcher setTextView(AutoCompleteTextView v)
			{
				this.textView = v;
				return this;
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after)
			{
				//textView.showDropDown();
			}
			@Override
			public void afterTextChanged(Editable e)
			{
				//textView.showDropDown();
				//textView.callOnClick();
				textView.clearFocus();
				textView.requestFocus();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				//textView.showDropDown();
			}
		}.setTextView(textView));*/


		//------------------------------------------------------------------------------------------------

		//------------------------------------------------------------------------------------------------
		// Update the form based on current context
		// (Ex: if we are in incident, pre-pop the incident field, and show the report type, etc.)
		//------------------------------------------------------------------------------------------------

		// TODO - If an incident has an existing FINAL report type, don't allow them to choose a report type
		// TODO - and display something like "FINAL ROC exists for Incident, you cannot create any additional ROCs for this incident."

		// FIXME - If we are in an incident, should I allow them to submit an ROC for a different incident?
		// FIXME- Should I indicate whether or not the entered name corresponds to an existing incident?
		//------------------------------------------------------------------------------------------------
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


		//FIXME - make this visible IFF the user has an incident, OR if the user types something into the incident field.
	//	reportTypeLayout.setVisibility(View.VISIBLE);
//		reportTypeSpinner.setVisibility(View.VISIBLE);

		//TODO - add incident on text modified spinner.

//		reportTypeSpinner.items
//		String[] arraySpinner = new String[] {
//				"1", "2", "3", "4", "5", "6", "7"
//		};
//		Spinner s = (Spinner) findViewById(R.id.Spinner01);
//		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//				android.R.layout.simple_spinner_item, arraySpinner);
//		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		s.setAdapter(adapter);


		// If the user has an active incident, show the Report type field.
		//TODO:
		/*reportTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected (AdapterView<?> parent, View view, int position, long id)
			{

			}

			@Override
			public void onNothingSelected (AdapterView<?> parent)
			{

			}
		});*/
		// Once a report type is selected, show all of the corresponding fields.

		//------------------------------------------------------------------------------------------------
		// TODO conditionally hide or display
		// FIXME - is a new instance created every time this fragment is displayed?
		// FIXME - I do believe that this function is called each time I go into it,
		// otherwise the fields would not reset to their original state.
		// So, if a new fragment instance is created each time, I'll have to update the report type
		// post-creation

		//------------------------------------------------------------------------------------------------

		super.onResume();
	}

	// Executed each time the incidentNameTextView text changes
	// This is responsible for setting up the form with the correct options.
	protected void incidentNameChanged()
	{
		String textContent = incidentNameTextView.getText().toString();

		// If the textView is empty
		// hide the reportType view (and everything else)
		if(textContent.length() == 0)
		{
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
			return;
		}

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

		// If the textView contains one of the incident names
		// show the reportType View and allow all three options
		if(isExistingIncidentName)
		{
			// FIXME - Make sure we actually check the incident's ROCs
			creatingNewIncident = false;
			currentIncidentLastRocSubmitted = ROC_NON_FINAL;
		}
		else
		{
			creatingNewIncident = true;
			currentIncidentLastRocSubmitted = ROC_NONE;
		}


		// If the incident has a FINAL Roc:
		if(currentIncidentLastRocSubmitted == ROC_FINAL)
		{
			// TODO - display an error saying something like "Final ROC exists for incident."
			// TODO - "no additional ROCs can be submitted"
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
		//----------------------------------------------------------------------------
		if(currentReportType == ROC_UPDATE || currentReportType == ROC_FINAL)
		{
			weatherInfoHeader.setVisibility(View.GONE);
		}

		//----------------------------------------------------------------------------
		// Setting up the threats & evacs spinners to have the correct options
		//----------------------------------------------------------------------------
		// ( YES / NO / MITIGATED ) if NEW or UPDATE
		// ( NO / MITIGATED ) if FINAL
		String[] threatsSpinnerOptions;
		if(currentReportType == ROC_FINAL)
		{
			threatsSpinnerOptions = new String[] {"Choose an option (required)", "No","Mitigated"};
			// Make the other sig info section not have auto complete
			otherInfoAddButton.setOnClickListener(newAddFieldListener(
					otherInfoListLinearLayout,
					otherInfoAddButton,
					null,
					null));
		}
		else
		{
			threatsSpinnerOptions = new String[] {"Choose an option (required)", "Yes", "No","Mitigated"};
			// Make the other sig info section have autocomplete
			otherInfoAddButton.setOnClickListener(newAddFieldListener(
					otherInfoListLinearLayout,
					otherInfoAddButton,
					otherSignificantInfoCannedMessages,
					null));
		}


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

		//----------------------------------------------------------------------------
		// Setting up the Threats & Evacs Section
		//----------------------------------------------------------------------------
		setSpinnerOptions(threatsEvacsSpinner, threatsSpinnerOptions);
		setSpinnerOptions(threatsStructuresSpinner, threatsSpinnerOptions);
		setSpinnerOptions(threatsInfrastructureSpinner, threatsSpinnerOptions);
		threatsEvacsSpinner.setSelection(0);
		threatsStructuresSpinner.setSelection(0);
		threatsInfrastructureSpinner.setSelection(0);

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
			incidentLongitudeDegreesEditText.setEnabled(false);
			incidentLongitudeMinutesEditText.setEnabled(false);


			incidentLocateButton.setEnabled(false);
			incidentLocateButton.setVisibility(View.GONE);
			incidentStateTextView.setEnabled(false);
			incidentLocateProgressBar.setVisibility(View.GONE);


			// Enter sample data for now:
			// TODO - fill with the incident's appropriate data:
			// FIXME - We are assigning temp data for now
			incidentNumberTextView.setText("18 CA-XXX-000000");
			incidentTypeTextView.setText("Vegetation Fire");
			//incidentTypeSpinner.setEnabled(false);
			incidentLatitudeDegreesEditText.setText("35");
			incidentLatitudeMinutesEditText.setText("23.43");
			incidentLongitudeDegreesEditText.setText("-120");
			incidentLongitudeMinutesEditText.setText("12.50");
			incidentStateTextView.setText("CA");
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
			incidentLongitudeDegreesEditText.setEnabled(true);
			incidentLongitudeMinutesEditText.setEnabled(true);
			incidentLocateButton.setEnabled(true);
			incidentLocateButton.setVisibility(View.VISIBLE);
			incidentLocateProgressBar.setVisibility(View.GONE);
			incidentStateTextView.setEnabled(true);
		}


		//----------------------------------------------------------------------------
		// If NEW, autopopulate start date with current date
		//----------------------------------------------------------------------------
//		rocStartDateTextView.setText().
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

		Log.e("ROC","Autopopulated date as: \""+ dateFormat.format(date) + "\"");

		rocStartDateTextView.setText(dateFormat.format(date));


		//----------------------------------------------------------------------------
		// Populating the Email Text View
		//----------------------------------------------------------------------------
		emailTextView.setText(mDataManager.getUsername());

	}

	// Executed each time the report type is changed
	// This is responsible for;
	// a) Ensuring repot type spinner stays on item 0
	// b) Ensuring the if vegetation fire is selected, we make vegetation fire fields mandatory
	protected void incidentTypeChanged()
	{
		incidentTypeSpinner.setSelection(0);

		if(incidentTypeSelectedItems.contains("Fire (Vegetation)"))
		{
			vegFireAcreageEditText.setHint("(required)");
			vegFirePercentContainedEditText.setHint("(required)");
		}
		else
		{
			vegFireAcreageEditText.setHint("");
			vegFirePercentContainedEditText.setHint("");
		}
	}


	public void threatsEvacsSpinnerChanged(int position)
	{
		setupThreatSubsection(position,
				threatsEvacsLabelTextView,
				threatsEvacsListLinearLayout,
				threatsEvacsAddButton,
				threatsEvacsYesCannedMessages,
				threatsEvacsMitigatedCannedMessages,
				threatsEvacsErrorView);
	}

	public void threatsStructuresChanged(int position)
	{
		setupThreatSubsection(position,
				threatsStructuresLabelTextView,
				threatsStructuresListLinearLayout,
				threatsStructuresAddButton,
				threatsStructuresYesCannedMessages,
				threatsInfrastructureMitigatedCannedMessages,
				threatsStructuresErrorView);

	}

	public void threatsInfrastructureChanged(int position)
	{
		setupThreatSubsection(position,
				threatsInfrastructureLabelTextView,
				threatsInfrastructureListLinearLayout,
				threatsInfrastructureAddButton,
				threatsInfrastructureYesCannedMessages,
				threatsInfrastructureMitigatedCannedMessages,
				threatsInfrastructureErrorView);
	}

	// This method is responsible for setting up a threats & evacs subsection
	private void setupThreatSubsection(int position, TextView label, LinearLayout subsection, ImageButton button, String[] yesCannedMessages, String[] mitigatedCannedMessages, ImageView errorView)
	{
		final int OPTION_NONE = 0;
		final int OPTION_YES = 1;
		final int OPTION_NO = 2;
		final int OPTION_MITIGATED = 3;

		int selectedOption = OPTION_NONE;
		if(currentReportType == ROC_NEW || currentReportType == ROC_UPDATE)
		{
			// 0 - "Choose an option"
			// 1 - "Yes"
			// 2 - "No"
			// 3 - "Mitigated"
			if(position == 1)
			{
				selectedOption = OPTION_YES;
			}
			else if(position == 2)
			{
				selectedOption = OPTION_NO;
			}
			else if(position == 3)
			{
				selectedOption = OPTION_MITIGATED;
			}
		}
		else
		{
			// reportType = ROC_FINAL
			// 0 - "Choose an option"
			// 1 - "No"
			// 2 - "Mitigated"

			if(position == 1)
			{
				selectedOption = OPTION_NO;
			}
			else if(position == 2)
			{
				selectedOption = OPTION_MITIGATED;
			}
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

		String[] autoCompleteOptions ;

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

		button.setOnClickListener(newAddFieldListener(subsection, button, autoCompleteOptions, errorView));


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


	// This method populates all member field variables by obtaining references to them
	// This method then sets up the behavior of the fields
	private void setUpAllFields()
	{
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
		incidentNameTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocIncidentName);
		reportTypeLayout = (LinearLayout) mRootView.findViewById(R.id.rocReportTypeSection);

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Incident Info Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		incidentNumberTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocIncidentNumber);
		incidentTypeTextView = (TextView) mRootView.findViewById(R.id.rocIncidentTypeTextView);
		incidentTypeSpinner = (Spinner) mRootView.findViewById(R.id.rocIncidentTypeSpinner);
		//incidentLatitudeEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLatitude);
		//incidentLongitudeEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLongitude);
		incidentLatitudeDegreesEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLatitudeDegrees);
		incidentLatitudeMinutesEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLatitudeMinutes);
		incidentLongitudeDegreesEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLongitudeDegrees);
		incidentLongitudeMinutesEditText = (EditText) mRootView.findViewById(R.id.rocIncidentLongitudeMinutes);
		incidentLocateButton = (ImageButton) mRootView.findViewById(R.id.rocLocateButton);
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
		incidentTypeSpinner.setAdapter(new IncidentTypeSpinnerAdapter<String>(mContext, headerText, incidentTypeSpinnerItems, incidentTypeSelectedItems, incidentTypeTextView));

		// Setting up the location button
		incidentLocateButton.setOnClickListener(locateButtonListener);
		// Setting up the fields to listen for focuschange, so they query location-based form data when no longer focused
		incidentLatitudeDegreesEditText.setOnFocusChangeListener(coordsFocusChangeListener);
		incidentLatitudeMinutesEditText.setOnFocusChangeListener(coordsFocusChangeListener);
		incidentLongitudeDegreesEditText.setOnFocusChangeListener(coordsFocusChangeListener);
		incidentLongitudeMinutesEditText.setOnFocusChangeListener(coordsFocusChangeListener);

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// ROC Incident Info Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		rocInitialCountySpinner = (Spinner) mRootView.findViewById(R.id.rocCounty);
		rocAdditionalCountiesTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocAdditionalCounties);
		rocLocationTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocLocation);
		rocDPASpinner = (Spinner) mRootView.findViewById(R.id.rocDPA);
		rocOwnershipSpinner = (Spinner) mRootView.findViewById(R.id.rocOwnership);
		rocJurisdictionTextView = (AutoCompleteTextView) mRootView.findViewById(R.id.rocJurisdiction);
		rocStartTimeTextView = (TextView) mRootView.findViewById(R.id.rocStartTime);
		rocStartDateTextView = (TextView) mRootView.findViewById(R.id.rocStartDate);
		// Retrieving the Error View field objects
		rocCountyErrorView = (ImageView) mRootView.findViewById(R.id.rocCountyError);
		rocDPAErrorView = (ImageView) mRootView.findViewById(R.id.rocDPAError);
		rocOwnershipErrorView = (ImageView) mRootView.findViewById(R.id.rocOwnershipError);

		// Make interacting with the spinners hide the error views
		setupSpinnerErrorView(rocInitialCountySpinner,rocCountyErrorView);
		setupSpinnerErrorView(rocDPASpinner,rocDPAErrorView);
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
						// For some reason, month is 0-based (January = 1)
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
						String time = hour + ":" + minute;
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
		vegFireRateOfSpreadSpinner = (Spinner) mRootView.findViewById(R.id.rocVegFireRateOfSpread);
		vegFireFuelTypeLabelTextView = (TextView) mRootView.findViewById(R.id.rocVegFireFuelTypeLabel);
		vegFireFuelTypeGrassCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeGrass);
		vegFireFuelTypeBrushCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeBrush);
		vegFireFuelTypeTimberCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeTimber);
		vegFireFuelTypeOakWoodlandCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeOakWoodland);
		vegFireFuelTypeOtherCheckBox = (CheckBox) mRootView.findViewById(R.id.rocVegFireFuelTypeOther);
		vegFireOtherFuelTypeLabelTextView = (TextView) mRootView.findViewById(R.id.rocVegFireFuelTypeOtherLabel);
		vegFireOtherFuelTypeEditText = (EditText) mRootView.findViewById(R.id.rocVegFireFuelTypeOtherText);
		vegFirePercentContainedEditText = (EditText) mRootView.findViewById(R.id.rocVegFirePercentContained);
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

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Other Significant Info Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		otherInfoListLinearLayout = (LinearLayout) mRootView.findViewById(R.id.rocOtherInfoList);
		otherInfoAddButton = (ImageButton) mRootView.findViewById(R.id.rocOtherInfoAddButton);
		// Setting up the add threat buttons
		otherInfoAddButton.setOnClickListener(newAddFieldListener(otherInfoListLinearLayout, otherInfoAddButton , otherSignificantInfoCannedMessages, null));

		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// CAL FIRE Resources Fields
		//-----------------------------------------------------------------------------------------
		//-----------------------------------------------------------------------------------------
		// Retrieving all of the field objects
		calFireIncidentSpinner = (Spinner) mRootView.findViewById(R.id.rocCalFireIncident);
		//calFireResourcesNoneCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesNone);
		//calFireResourcesAirCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesAir);
		//calFireResourcesGroundCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesGround);
		//calFireResourcesAllReleasedCheckBox = (CheckBox) mRootView.findViewById(R.id.rocCalFireResourcesAllReleased);
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
	}

	private void setUIStringField( JSONObject data,String paramName, EditText editText)
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
		}
	}

	private void setUIStringSpinner(JSONObject data, String paramName, Spinner spinner, ImageView errorView)
	{
		try
		{
			String value = data.getString(paramName);

			if(!value.equals("null"))
			{
				// Iterate through the spinner's options, and select whichever one matches:
				for(int i = 0; i <  spinner.getCount(); i++)
				{
					if(spinner.getItemAtPosition(i).toString().toLowerCase().equals(value.toLowerCase()))
					{
						spinner.setSelection(i);
						if(errorView != null)
							errorView.setVisibility(View.GONE);
						return;
					}
				}
			}
			// FIXME - If the value is "null", should the field be emptied out?
			// FIXME - If the value isn't found in the spinner, should we empty out the spinner?
		}
		catch(Exception e)
		{
			Log.w("ROCFragment","Unable to parse field from JSONObject for field: \"" + paramName + "\"");
		}
	}

	private void setUILatLong (JSONObject data)
	{
		try
		{
			double lat = data.getDouble("latitude");
			double lon = data.getDouble("longitude");

			// Converting to DDM:
			// TODO - Should validate lat / long somewhere
			incidentLatitudeDegreesEditText.setText(String.format(Locale.US,"%d",(int)getDegree(lat)));
			incidentLatitudeMinutesEditText.setText(String.format(Locale.US,"%f",getMinutes(lat)));
			incidentLongitudeDegreesEditText.setText(String.format(Locale.US,"%d",(int)getDegree(lon)));
			incidentLongitudeMinutesEditText.setText(String.format(Locale.US,"%f",getMinutes(lon)));

			// Removing the errors:
			incidentLatitudeDegreesEditText.setError(null);
			incidentLatitudeMinutesEditText.setError(null);
			incidentLongitudeDegreesEditText.setError(null);
			incidentLongitudeMinutesEditText.setError(null);
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
		getActivity().runOnUiThread((new Runnable()
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
				// TODO Make the spinners support assignment.

				// Assigning the lat/long fields:
				setUILatLong(data);


				setUIStringField(data, "location", rocLocationTextView);
				setUIStringSpinner(data, "county", rocInitialCountySpinner, rocCountyErrorView);
				setUIStringField(data, "state", incidentStateTextView);

				setUIStringSpinner(data, "sra", rocOwnershipSpinner, rocOwnershipErrorView);
				setUIStringSpinner(data, "dpa", rocDPASpinner, rocDPAErrorView);

				setUIStringField(data, "jurisdiction", rocJurisdictionTextView);


				setUIStringField(data, "temperature", weatherTempEditText);
				setUIStringField(data, "relHumidity", weatherRelativeHumidityEditText);
				setUIStringField(data, "windSpeed", weatherWindSpeedEditText);
				//FIXME setStringField(data, "windDirection", weatherWindDirectionSpinner, weatherWindDirectionError);

				// Show the Locate button, hide the e progress bar:
				incidentLocateButton.setVisibility(View.VISIBLE);
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

			boolean collapse = section.getVisibility() == View.VISIBLE;

			collapseAllSections();

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
		//================================================
		// Incident Info Fields
		//================================================
		incidentNumberTextView.setText("");
		incidentTypeTextView.setText("");
		incidentTypeSpinner.setSelection(0);
		// Decimal Degree Minutes Edit Text:
		incidentLatitudeDegreesEditText.setText("");
		incidentLatitudeMinutesEditText.setText("");
		incidentLongitudeDegreesEditText.setText("");
		incidentLongitudeMinutesEditText.setText("");
		incidentStateTextView.setText("");
		// State Variables
		incidentTypeSelectedItems.clear();
		//================================================
		// ROC Incident Info Fields
		//================================================
		rocInitialCountySpinner.setSelection(0);
		rocAdditionalCountiesTextView.setText("");
		rocLocationTextView.setText("");
		rocDPASpinner.setSelection(0);
		rocOwnershipSpinner.setSelection(0);
		rocJurisdictionTextView.setText("");
		rocStartTimeTextView.setText("");
		rocStartDateTextView.setText("");
		// Input Validation Error Views
		rocCountyErrorView.setVisibility(View.GONE);
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
		//================================================
		// CAL FIRE Resources Fields
		//================================================
		calFireIncidentSpinner.setSelection(0);
		// Previous ROC Checkboxes
		//calFireResourcesNoneCheckBox.setChecked(false);
		//calFireResourcesAirCheckBox.setChecked(false);
		//calFireResourcesGroundCheckBox.setChecked(false);
		//calFireResourcesAllReleasedCheckBox.setChecked(false);
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

		// Input Validation Error Views
		ImageView calFireIncidentErrorView;
		//================================================
		// Other Significant Info Fields
		//================================================
		otherInfoListLinearLayout.removeAllViews();
		//ImageButton otherInfoAddButton;
		//================================================
		// Email Fields
		//================================================
		emailTextView.setText("");
		//================================================
	}


	//private String[] testAutoCompleteArray =
	//		{
	//				"canned message 1",
	//				"canned message 2",
	//				"canned message 3. This is an example of a really long message that won't fit on one line, so it may be truncated or it may wrap around. 1",
	//				"canned message 3. This is an example of a really long message that won't fit on one line, so it may be truncated or it may wrap around. 2",
	//				"canned message 4. This is an example of a really long message that won't fit on one line, so it may be truncated or it may wrap around. 1"
	//		};

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

	//=======================================================================================================================
	//=======================================================================================================================
	//=======================================================================================================================


	// Creates an OnClickListener that adds a new AutoCompleteTextView field to a LinearLayout every time the ImageButton is pressed
	// When the user clicks the "add" button, the "errorView" will be hidden
	private OnClickListener newAddFieldListener(LinearLayout l, ImageButton b, String[] c, ImageView errorView)
	{
		OnClickListener listener = (new OnClickListener()
		{
			LinearLayout layout;
			ImageButton button;
			String[] autoCompleteArray;
			ImageView errorView;

			// Acts as constructor for anonymous class
			public OnClickListener init(LinearLayout l, ImageButton b, String[] c, ImageView e)
			{
				layout = l;
				button = b;
				autoCompleteArray = c;
				errorView = e;
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
		}).init(l, b, c, errorView);

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

		// Make the dropdown view show up when the textview is modified:
		// FIXME - this fails to show the dialog when the text has changed
		/*textView.addTextChangedListener(new TextWatcher()
		{
			// Store the textView this TextWatcher is responsible for
			AutoCompleteTextView textView;
			// Assigns the textView so the TextWatcher instance keeps a reference to the textView it's responsible for
			public TextWatcher setTextView(AutoCompleteTextView v)
			{
				this.textView = v;
				return this;
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after)
			{
				//textView.showDropDown();
			}
			@Override
			public void afterTextChanged(Editable e)
			{
				//textView.showDropDown();
				//textView.callOnClick();
				textView.clearFocus();
				textView.requestFocus();
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				//textView.showDropDown();
			}
		}.setTextView(textView));*/
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

	private double getMinutes(double decimalDegrees)
	{
		return 60.0 * (Math.abs(decimalDegrees - getDegree(decimalDegrees)));
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
				//----------------------------------------------------------------
				// Check to make sure each field contains valid input:
				//----------------------------------------------------------------
				boolean validData = false;

				double latitude = 0;
				double longitude = 0;

				// Try parsing a double based on what is entered into the fields
				try
				{
					// Building the latitude / longitude from whatever is in the fields:
					int latDegrees = Integer.parseInt(incidentLatitudeDegreesEditText.getText().toString());
					double latMinutes = Double.parseDouble(incidentLatitudeMinutesEditText.getText().toString());
					int lonDegrees = Integer.parseInt(incidentLongitudeDegreesEditText.getText().toString());
					double lonMinutes = Double.parseDouble(incidentLongitudeMinutesEditText.getText().toString());

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
							+ incidentLatitudeMinutesEditText.getText().toString() + "  min. Lon: "
							+ incidentLongitudeDegreesEditText.getText().toString() + " deg, "
							+ incidentLongitudeMinutesEditText.getText().toString() + " min.");
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

						// FIXME remove this:
						Log.e("ROC","About to retrieve location data for: ("+ coords.getDouble("latitude")+"," + coords.getDouble("longitude") + ")");


					}
					catch(JSONException e)
					{
						Log.w("ROCFragment","Exception building coords. Exception: " + e);

					}
					requestIncidentLocationDetails(coords);
				}
			}
		}
	};

	// When the user presses the submit button:
	OnClickListener submitButtonOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick (View v)
		{
			boolean isFormValid = true;

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
			rocDPAErrorView.setVisibility(View.GONE);
			rocOwnershipErrorView.setVisibility(View.GONE);
			vegFireRateOfSpreadErrorView.setVisibility(View.GONE);
			vegFireFuelTypeLabelTextView.setError(null);
			threatsEvacsErrorView.setVisibility(View.GONE);
			threatsInfrastructureErrorView.setVisibility(View.GONE);
			threatsStructuresErrorView.setVisibility(View.GONE);
			calFireIncidentErrorView.setVisibility(View.GONE);


			//--------------------------------------------
			// Clearing out all other errors:
			//--------------------------------------------
			incidentNameTextView.setError(null);
			((TextView) reportTypeSpinner.getSelectedView()).setError(null);
			incidentTypeTextView.setError(null);
			incidentLatitudeDegreesEditText.setError(null);
			incidentLatitudeMinutesEditText.setError(null);
			incidentLongitudeDegreesEditText.setError(null);
			incidentLongitudeMinutesEditText.setError(null);
			incidentStateTextView.setError(null);
			incidentInfoHeaderErrorView.setVisibility(View.GONE);
			rocCountyErrorView.setVisibility(View.GONE);
			rocLocationTextView.setError(null);
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
			threatsEvacsHeaderErrorView.setVisibility(View.GONE);
			calFireIncidentErrorView.setVisibility(View.GONE);
			resourceCommitmentHeaderErrorView.setVisibility(View.GONE);
			otherInfoHeaderErrorView.setVisibility(View.GONE);

			// Each of these linearlayouts contains children that might have errors
			LinearLayout[] list = {threatsEvacsListLinearLayout, threatsStructuresListLinearLayout, threatsInfrastructureListLinearLayout, otherInfoListLinearLayout};
			// Clearing the errors of all children in the above list:
			for(int i = 0; i < list.length; i++)
			{
				for(int j = 0; j < list[i].getChildCount(); j++)
				{
					LinearLayout layout = (LinearLayout) list[i].getChildAt(j);

					// Iterate through layout's 2 children, find the textView
					for(int k = 0; k < layout.getChildCount(); k++)
					{
						View childView = layout.getChildAt(k);
						if(childView instanceof AutoCompleteTextView)
						{
							AutoCompleteTextView textView = (AutoCompleteTextView) childView;
							textView.setError(null);
						}
					}
				}
			}

			//--------------------------------------------



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
			// Only validate the incident fields if report type is ROC_NEW
			if(currentReportType == ROC_NEW)
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
				// Latitude Minutes:
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
				// Longitude Degrees:
				//------------------
				try
				{
					int lonDeg = Integer.parseInt(incidentLongitudeDegreesEditText.getText().toString());
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
				// Longitude Minutes:
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
			// required
			//--------------------------------
			if(rocStartTimeTextView.getText().toString().trim().length() == 0)
			{
				rocStartTimeTextView.setError("Incident start time is required.");
				isRocIncidentInfoValid = false;
				isFormValid = false;
			}
			//--------------------------------
			// rocStartTimeTextView
			// required
			//--------------------------------
			if(rocStartDateTextView.getText().toString().trim().length() == 0)
			{
				rocStartDateTextView.setError("Incident start date is required.");
				isRocIncidentInfoValid = false;
				isFormValid = false;
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


			if(incidentTypeSelectedItems.contains("Fire (Vegetation)"))
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
			// If (vegFireFieldsRequired), at least one must be checked:
			//--------------------------------
			if(vegFireFieldsRequired)
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
			//================================================

			boolean isThreatsInfoValid = true;

			//--------------------------------
			// threatsEvacsSpinner
			// required
			//--------------------------------
			if(threatsEvacsSpinner.getSelectedItemPosition() == 0)
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
					threatsEvacsErrorView.setVisibility(View.VISIBLE);
					isThreatsInfoValid = false;
					isFormValid = false;
				}
			}
			// If it's not ROC_FINAL, and spinner is YES or MITIGATED, at least one child is required:
			if(currentReportType != ROC_FINAL && (threatsEvacsSpinner.getSelectedItemPosition() == 1 || threatsEvacsSpinner.getSelectedItemPosition() == 3))
			{
				if(threatsEvacsListLinearLayout.getChildCount() == 0)
				{
					threatsEvacsErrorView.setVisibility(View.VISIBLE);
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
			// required
			//--------------------------------
			if(threatsStructuresSpinner.getSelectedItemPosition() == 0)
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
					threatsStructuresErrorView.setVisibility(View.VISIBLE);
					isThreatsInfoValid = false;
					isFormValid = false;
				}
			}
			// If it's not ROC_FINAL, and spinner is YES or MITIGATED, at least one child is required:
			if(currentReportType != ROC_FINAL && (threatsStructuresSpinner.getSelectedItemPosition() == 1 || threatsStructuresSpinner.getSelectedItemPosition() == 3))
			{
				if(threatsStructuresListLinearLayout.getChildCount() == 0)
				{
					threatsStructuresErrorView.setVisibility(View.VISIBLE);
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
			// required
			//--------------------------------
			if(threatsInfrastructureSpinner.getSelectedItemPosition() == 0)
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
					threatsInfrastructureErrorView.setVisibility(View.VISIBLE);
					isThreatsInfoValid = false;
					isFormValid = false;
				}
			}
			// If it's not ROC_FINAL, and spinner is YES or MITIGATED, at least one child is required:
			if(currentReportType != ROC_FINAL && (threatsInfrastructureSpinner.getSelectedItemPosition() == 1 || threatsInfrastructureSpinner.getSelectedItemPosition() == 3))
			{
				if(threatsInfrastructureListLinearLayout.getChildCount() == 0)
				{
					threatsInfrastructureErrorView.setVisibility(View.VISIBLE);
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
			if(isFormValid)
			{
				Log.e("ROC","ROC Form data is invalid");

				Toast toast = Toast.makeText(mContext, "ROC Submitted", Toast.LENGTH_SHORT);
				toast.show();

				mContext.onBackPressed();
			}
			// TODO - Push form data to store and forward table
			// Go back to the ROC Action screen
			//mContext.onBackPressed();
		}
	};

	// When the user presses the cancel button:
	OnClickListener cancelButtonOnClickListener = new OnClickListener() {
		@Override
		public void onClick (View v)
		{
			mContext.onBackPressed();
		}
	};



	//========================================================================================================
	//========================================================================================================
	// A custom Adapter to show a checkbox list
	//========================================================================================================
	//========================================================================================================

	private static class IncidentTypeSpinnerAdapter<T> extends BaseAdapter
	{
		static class SpinnerItem<T>
		{
			private String text;
			private T item;

			SpinnerItem(T t, String s)
			{
				item = t;
				text = s;
			}

			@Override
			public String toString ()
			{
				return text;
			}
		}


		private Context context;
		private Set<T> selected_items;
		private List<SpinnerItem<T>> all_items;
		private String headerText;
		// The textView to update with the selected contents
		private TextView textView;
		// The spinner that uses this adapter

		IncidentTypeSpinnerAdapter(Context c, String h, List<SpinnerItem<T>> l, Set<T> s, TextView t)
		{
			context = c;
			headerText = h;
			all_items = l;
			selected_items = s;
			textView = t;
		}

		@Override
		public int getCount()
		{
			return all_items.size() + 1;
		}

		@Override
		public Object getItem(int position)
		{
			if(position < 1)
			{
				return null;
			}

			return all_items.get(position - 1);
		}

		@Override
		public long getItemId(int position)
		{
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			final ViewHolder holder;
			if(convertView == null)
			{
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				convertView = layoutInflater.inflate(R.layout.roc_incident_type_spinner_item, parent, false);
				holder = new ViewHolder();
				holder.mTextView = (TextView) convertView.findViewById(R.id.text);
				holder.mCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}


			if(position < 1)
			{
				holder.mCheckBox.setVisibility(View.GONE);
				holder.mTextView.setText(headerText);

				holder.mTextView.setOnClickListener(null);
				holder.mTextView.setClickable(false);
				holder.mTextView.setFocusable(false);
				holder.mTextView.setFocusableInTouchMode(false);
			}
			else
			{
				final int listPos = position - 1;
				holder.mCheckBox.setVisibility(View.VISIBLE);
				holder.mTextView.setText(all_items.get(listPos).text);

				final T item = all_items.get(listPos).item;
				boolean isSelected = selected_items.contains(item);

				holder.mCheckBox.setOnCheckedChangeListener(null);
				holder.mCheckBox.setChecked(isSelected);

				holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
				{
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
					{
						if(isChecked)
						{
							selected_items.add(item);
						}
						else
						{
							selected_items.remove(item);
						}

						// If the textView is set, set the textView's text to show the selected items, one per line
						if(textView != null)
						{
							String textViewText = "";
							boolean first = true;
							for (T item : selected_items)
							{
								textViewText += (first ? "" : ",\n") + item;
								first = false;
							}

							textView.setText(textViewText);
						}
					}
				});

				// Enabling the textview as clickable
				holder.mTextView.setClickable(false);
				holder.mTextView.setFocusable(false);
				holder.mTextView.setFocusableInTouchMode(false);

				holder.mTextView.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						holder.mCheckBox.toggle();
					}
				});
			}

			return convertView;
		}

		private class ViewHolder
		{
			private TextView mTextView;
			private CheckBox mCheckBox;
		}



	}
}
