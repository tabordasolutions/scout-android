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

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.messages.SimpleReportMessage;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class SimpleReportResponseHandler extends AsyncHttpResponseHandler {
	private DataManager mDataManager;
	private Context mContext;
	private long mReportId;
	
	private boolean mFailed;

	private long mUserSessionId;
	
	public SimpleReportResponseHandler(Context context, DataManager dataManager, long reportId, long userSessionId) {
		mContext = context;
		mReportId = reportId;
		mDataManager = dataManager;
		mFailed = false;
		mUserSessionId = userSessionId;
	}

	@Override
	public void onProgress(long bytesWritten, long totalSize) {
		double progressPercent = ((double) bytesWritten/ (double)totalSize) * 100;
	    if(progressPercent > 100) {
	    	progressPercent = 100;
	    }
	    
		Log.e("nicsRest", "Progress to post Simple Report information: " + progressPercent);
		
		Intent intent = new Intent();
	    intent.setAction(Intents.nics_SIMPLE_REPORT_PROGRESS);
	    
		intent.putExtra("reportId", mReportId);
		intent.putExtra("progress", progressPercent);
		
        mContext.sendBroadcast (intent);
        
		super.onProgress(bytesWritten, totalSize);
	}



	@Override
	public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
		Log.e("USIDDEFECT", "Simple Report No Image got response code: " + statusCode);

		if(mDataManager.sessionIsInvalid(statusCode,mUserSessionId))
		{
			RestClient.removeSimpleReportHandler(mReportId);
			return;
		}

		Log.e("nicsRest", "Success to post Simple Report w/ Image information");
		Log.e("nicsRest","USIDDEFECT, onSuccess statusCode: " + statusCode);
		Log.e("nicsRest","USIDDEFECT, onSuccess headers: " + headers);
		Log.e("nicsRest","USIDDEFECT, onSuccess responseBody: " + (responseBody != null ? new String(responseBody) : "null"));

		Log.e("nicsRest", "Success to post Simple Report information");
		Log.e("nicsRest", "Deleting: " + mReportId + " success: " + mDataManager.deleteSimpleReportStoreAndForward(mReportId));
		
		String content = (responseBody != null) ? new String(responseBody) : "";
		SimpleReportMessage message = new Gson().fromJson(content, SimpleReportMessage.class);
		for(SimpleReportPayload payload : message.getReports()) {
			mDataManager.deleteSimpleReportStoreAndForward(mReportId);
			payload.setSendStatus(ReportSendStatus.SENT);
			payload.parse();
			mDataManager.addSimpleReportToStoreAndForward(payload);
		}
		
		RestClient.removeSimpleReportHandler(mReportId);
		RestClient.setSendingSimpleReports(false);
		mDataManager.requestSimpleReports();
	}

	@Override
	public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
		Log.e("nicsRest", "Failed to post Simple Report information: Status Code: " + statusCode + " Message: " + error.getMessage());
		Log.e("nicsRest","USIDDEFECT, onFailure responseBody: " + (responseBody != null ? new String(responseBody) : "null"));

		Intent intent = new Intent();
	    intent.setAction(Intents.nics_SIMPLE_REPORT_PROGRESS);
	    
		intent.putExtra("reportId", mReportId);
		intent.putExtra("progress", 0.0d);
		mFailed = true;
		intent.putExtra("failed", mFailed);
		
        mContext.sendBroadcast (intent);
		RestClient.removeSimpleReportHandler(mReportId);

		if(mDataManager.sessionIsInvalid(statusCode,mUserSessionId))
		{
			return;
		}

		RestClient.setSendingSimpleReports(false);

	}
}
