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
import scout.edu.mit.ll.nics.android.api.messages.DamageReportMessage;
import scout.edu.mit.ll.nics.android.api.payload.forms.DamageReportPayload;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class DamageReportResponseHandler extends AsyncHttpResponseHandler {
	private DataManager mDataManager;
	private Context mContext;
	private long mReportId;
	
	private boolean mFailed;
	
	public DamageReportResponseHandler(Context context, DataManager dataManager, long reportId) {
		mContext = context;
		mReportId = reportId;
		mDataManager = dataManager;
		mFailed = false;
	}
	
	@Override
	public void onProgress(long bytesWritten, long totalSize) {
		double progressPercent = ((double) bytesWritten/ (double)totalSize) * 100;
	    if(progressPercent > 100) {
	    	progressPercent = 100;
	    }
	    
		Log.e("nicsRest", "Progress to post Damage Report information: " + progressPercent);
		
		Intent intent = new Intent();
	    intent.setAction(Intents.nics_DAMAGE_REPORT_PROGRESS);
	    
		intent.putExtra("reportId", mReportId);
		intent.putExtra("progress", progressPercent);
		
        mContext.sendBroadcast (intent);
        
		super.onProgress(bytesWritten, totalSize);
	}

	@Override
	public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
		Log.e("nicsRest", "Success to post Damage Report information");
		
		String content = (responseBody != null) ? new String(responseBody) : "";
		DamageReportMessage message = new Gson().fromJson(content, DamageReportMessage.class);
		for(DamageReportPayload payload : message.getReports()) {
			mDataManager.deleteDamageReportStoreAndForward(mReportId);
			payload.setSendStatus(ReportSendStatus.SENT);
			payload.parse();
			mDataManager.addDamageReportToStoreAndForward(payload);
		}
		
		RestClient.removeDamageReportHandler(mReportId);
		RestClient.setSendingDamageReports(false);
		mDataManager.requestDamageReports();
	}

	@Override
	public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
		Log.e("nicsRest", "Failed to post Damage Report information: " + error.getMessage());
		
		Intent intent = new Intent();
	    intent.setAction(Intents.nics_DAMAGE_REPORT_PROGRESS);
	    
		intent.putExtra("reportId", mReportId);
		intent.putExtra("progress", 0.0d);
		mFailed = true;
		intent.putExtra("failed", mFailed);

        mContext.sendBroadcast (intent);
		RestClient.removeDamageReportHandler(mReportId);
		RestClient.setSendingDamageReports(false);
	}

}
