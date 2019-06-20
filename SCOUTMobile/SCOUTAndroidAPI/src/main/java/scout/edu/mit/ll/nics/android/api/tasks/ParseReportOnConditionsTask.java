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
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.ReportOnConditionData;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.utils.NotificationsHandler;


public class ParseReportOnConditionsTask extends AsyncTask<ArrayList<ReportOnConditionData>, Object, Integer>
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
	protected Integer doInBackground(@SuppressWarnings("unchecked") ArrayList<ReportOnConditionData>... rocData)
	{
		Integer numParsed = 0;

		// Iterate through each ROC data
		for(ReportOnConditionData data : rocData[0])
		{
			if(data == null)
				continue;
			data.sendStatus = ReportSendStatus.SENT;
			data.isForNewIncident = true;

			Log.e("ROC","Adding ROC data to table: " + data.toJSON());
			mDataManager.addReportOnConditionToHistory(data);

			// Broadcast that we've received a new report
			// These broadcasts are used for UI changes based on having received a new report
			// currently, nothing uses these broadcasts.
			//Intent intent = new Intent();
			//intent.setAction(Intents.nics_NEW_REPORT_ON_CONDITION_RECEIVED);
			// NOTE - must somehow insert the object data
			//intent.putExtra("payload", data);
			//intent.putExtra("sendStatus", ReportSendStatus.SENT.getId());
			//mContext.sendBroadcast(intent);
			numParsed++;
		}

		// Check if we have any ROCs that have successfully been sent in our store and forward table
		// if so, remove them
		if(numParsed > 0)
		{
			ArrayList<ReportOnConditionData> reports = mDataManager.getAllReportOnConditionStoreAndForwardHasSent();

			for(int i = 0; i < reports.size(); i++)
			{
				mDataManager.deleteReportOnConditionStoreAndForward(reports.get(i).incidentname, reports.get(i).datecreated.getTime());

				// Broadcast that we've removed a report from the send table
				// Currently there is nothing to be done for this broadcast, but I'll leave it here in case future functionality could use this broadcast
				//Intent intent = new Intent();
				//intent.setAction(Intents.nics_SENT_REPORT_ON_CONDITION_CLEARED);
				//intent.putExtra("reportId", reports.get(i).id);
				//mContext.sendBroadcast (intent);

			}
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
