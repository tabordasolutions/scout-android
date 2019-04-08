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
package scout.edu.mit.ll.nics.android.api.tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.payload.forms.ReportOnConditionPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.NotificationsHandler;


public class ParseReportOnConditionsTask extends AsyncTask<ArrayList<ReportOnConditionPayload>, Object, Integer>
{
	//OES-828
	//TODO - Update this
	private Context mContext;
	private DataManager mDataManager;
	private NotificationsHandler mNotificationHandler;

	public ParseReportOnConditionsTask (Context context)
	{
		mContext = context;
		mDataManager = DataManager.getInstance(mContext);
		mNotificationHandler = NotificationsHandler.getInstance(mContext);
	}

	@Override
	protected Integer doInBackground(@SuppressWarnings("unchecked") ArrayList<ReportOnConditionPayload>... rocPayloads)
	{
		Integer numParsed = 0;
		for(ReportOnConditionPayload payload : rocPayloads[0])
		{
			if(payload.getIncidentId() == mDataManager.getActiveIncidentId())
			{
				payload.parse();
				payload.setSendStatus(ReportSendStatus.SENT);
				payload.setNew(true);
				mDataManager.addReportOnConditionToHistory(payload);

		        Intent intent = new Intent();
		        intent.setAction(Intents.nics_NEW_SIMPLE_REPORT_RECEIVED);
		        intent.putExtra("payload", payload.toJsonString());
		        intent.putExtra("sendStatus", ReportSendStatus.SENT.getId());
		        mContext.sendBroadcast (intent);
		        numParsed++;
			}
		}

		if(numParsed > 0)
		{
			
			ArrayList<ReportOnConditionPayload> reports = mDataManager.getAllReportOnConditionStoreAndForwardHasSent();
			for(int i = 0; i < reports.size(); i++)
			{
				mDataManager.deleteReportOnConditionStoreAndForward(reports.get(i).getId());
				Log.d("ParseReportOnCondition","deleted sent report on condition: " + reports.get(i).getId());
				Intent intent = new Intent();
			    intent.setAction(Intents.nics_SENT_REPORT_ON_CONDITION_CLEARED);
				intent.putExtra("reportId", reports.get(i).getFormId());
		        mContext.sendBroadcast (intent);
			}
			
	        mDataManager.addPersonalHistory("Successfully received " + numParsed + " report on conditions from " + mDataManager.getActiveIncidentName());
	        //mDataManager.setNewGeneralMessageAvailable(true);
		}

		return numParsed;
	}
	
	@Override
	protected void onPostExecute(Integer numParsed)
	{
		super.onPostExecute(numParsed);

		RestClient.clearParseReportOnConditionTask();
		
		Log.i("nicsROCTask", "Successfully parsed " + numParsed + " Report on Conditions.");
	}

}
