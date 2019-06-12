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
package scout.edu.mit.ll.nics.android.api.handlers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.ReportOnConditionData;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.messages.SimpleReportMessage;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class ReportOnConditionResponseHandler extends AsyncHttpResponseHandler {

	private DataManager mDataManager;
	private Context mContext;
	private String mIncidentName;
	private long mCreationDate;
	private long mUserSessionId;

	public ReportOnConditionResponseHandler (Context context, DataManager dataManager, String incidentName, long creationDate, long userSessionId) {
		mContext = context;
		mIncidentName = incidentName;
		mCreationDate = creationDate;
		mDataManager = dataManager;
		mUserSessionId = userSessionId;
	}
	
	@Override
	public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {

		Log.e("ROC", "ROCResponseHandler - onSuccess - Post Report on Condition got response code: " + statusCode);

		if(mDataManager.sessionIsInvalid(statusCode,mUserSessionId))
		{
			Log.e("ROC", "ROCResponseHandler - onSuccess - user session is invalid. Stopping");
			return;
		}

		Log.e("ROC", "ROCResponseHandler - onSuccess - responseBody: \"" + (responseBody != null ? new String(responseBody) : "null") + "\"");


		// TODO - We have a responseBody, does this responseBody contain the info of the incident created?

		// Getting the report from the db
		ReportOnConditionData rocData = mDataManager.getReportOnConditionStoreAndForward(mIncidentName, mCreationDate);

		if(rocData != null)
		{

			// Removing it from the db
			mDataManager.deleteReportOnConditionStoreAndForward(mIncidentName, mCreationDate);

			// Updating the send status:
			rocData.sendStatus = ReportSendStatus.SENT;

			// Add the updated rocData to the history db
			mDataManager.addReportOnConditionToHistory(rocData);
		}

		RestClient.setSendingReportOnConditions(false);

		// Tell datamanager to refresh incidents list, in case the new ROC created a new Incident.
		RestClient.getAllIncidents(mDataManager.getUserId());
	}
	
	@Override
	public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {

		Toast.makeText(mContext, "Failed to post ROC - " + error.getMessage(), Toast.LENGTH_SHORT).show();

		// FIXME - if we leave the ROC in the send queue, it will rapidly try resending it over and over again, leading to MANY toasts...

		// Remove the ROC from the send queue, we cannot reqover from an internal server error, so repeatedly sending it over and over solves nothing
		ReportOnConditionData rocData = mDataManager.getReportOnConditionStoreAndForward(mIncidentName, mCreationDate);

		if(rocData != null)
		{
			// Removing it from the db
			mDataManager.deleteReportOnConditionStoreAndForward(mIncidentName, mCreationDate);

			rocData.sendStatus = ReportSendStatus.SENT;

			// Don't add it to history table because we had a server error...
			// Updating the send status:
			//rocData.sendStatus = ReportSendStatus.SENT;

			// Add the updated rocData to the history db
			//mDataManager.addReportOnConditionToHistory(rocData);
		}


		Log.e("ROC", "ROCResponseHandler - onFailure - Information: " + error.getMessage());
		Log.e("ROC","ROC - Error Information:" + error.toString());
		Log.e("ROC","ROC - onFailure status code: " + statusCode);
		Log.e("ROC","ROC - onFailure headers: " + headers);
		Log.e("ROC","ROC - onFailure responseBody: " + (responseBody != null ? new String(responseBody) : "null"));

		if(mDataManager.sessionIsInvalid(statusCode,mUserSessionId))
		{
			return;
		}

		RestClient.setSendingReportOnConditions(false);
	}
}
