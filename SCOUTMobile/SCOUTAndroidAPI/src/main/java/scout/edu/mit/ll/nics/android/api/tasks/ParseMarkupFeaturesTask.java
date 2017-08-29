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

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.RestClient;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.payload.MarkupPayload;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class ParseMarkupFeaturesTask extends AsyncTask<MarkupPayload, Object, Integer> {
	private Context mContext;
	private DataManager mDataManager;
	private ArrayList<String> mFeaturesToAdd;
	private ArrayList<String> mFeaturesToRemove;
	private long collabroomId;
	
	public ParseMarkupFeaturesTask(Context context) {
		mContext = context;
		mDataManager = DataManager.getInstance(mContext);

		mFeaturesToAdd = new ArrayList<String>();
		mFeaturesToRemove = new ArrayList<String>();
	}

	@Override
	protected Integer doInBackground(MarkupPayload... markupPayload) {
		Integer numParsed = 0;
		
		mFeaturesToAdd.clear();
		mFeaturesToRemove.clear();
		
		if(markupPayload != null) {
			MarkupPayload payload = markupPayload[0];
			collabroomId = payload.getCollabRoomId();
	
			if(payload.getDeletedFeatures() != null) {
				mFeaturesToRemove.addAll(payload.getDeletedFeatures());
			}
			
			for(String featureId : mFeaturesToRemove) {
				mDataManager.deleteMarkupHistoryForCollabroomByFeatureId(collabroomId, featureId);
			}
			Log.i("nicsParseMarkup", "Successfully removed " + mFeaturesToRemove.size() + " markup features.");
			
			ArrayList<MarkupFeature> features = payload.getFeatures();
			
			if(features != null && features.size() > 0) {
				
				for(MarkupFeature feature : features) {
					feature.setCollabRoomId(collabroomId);
					if(mDataManager.addMarkupFeatureToHistory(feature)) {
						mFeaturesToAdd.add(feature.toFullJsonString());
					}
				}
				numParsed = features.size();
			}
			
			if(numParsed > 0) {
				//mNotificationHandler.createMarkupNotification(chatPayloads[0], mDataManager.getActiveIncidentId());
		        mDataManager.addPersonalHistory("Successfully received " + numParsed + " markup features from " + mDataManager.getSelectedCollabRoom().getName());
			}
		}
		return numParsed;
	}
	
	@Override
	protected void onPostExecute(Integer numParsed) {

		super.onPostExecute(numParsed);


		int ftrsToAdd = mFeaturesToAdd.size();
		int ftrsToRemove = mFeaturesToRemove.size();

		Log.i("nicsParseMarkup", "about to parse markup features ( +" + ftrsToAdd + ", -" + ftrsToRemove + ")");

		//The intents expect a string array, but we are not passing in the markup data to the intent
		//(too much data crashes the intent)
		//So we need an empty string array to pass in
		String[] emptyStringArr = new String[1];
		emptyStringArr[0] = "nothing to see here";

		ReceivedMarkupFeaturesData.clearFeatureData();

		if(ftrsToAdd > 0 || ftrsToRemove > 0){

			Intent intent = new Intent();
			intent.putExtra("collabroomId", collabroomId);
			intent.setAction(Intents.nics_NEW_MARKUP_RECEIVED);
			
			if(ftrsToAdd > 0){
		        String[] addStrings = mFeaturesToAdd.toArray(new String[0]);

				//We are no longer sending the features to add through the intent
				//intent.putExtra("featuresToAdd", addStrings);
				intent.putExtra("featuresToAdd",emptyStringArr);
				//We are instead sending them through this data class (that was created for this purpose)
				ReceivedMarkupFeaturesData.setFeaturesToAdd(addStrings);

				mDataManager.setNewMapAvailable(true);
			}
			if(ftrsToRemove > 0){
				String[] removeStrings = mFeaturesToRemove.toArray(new String[0]);

				//We are no longer sending the features to remove through the intent
				//intent.putExtra("featuresToRemove", removeStrings);
				intent.putExtra("featuresToRemove",emptyStringArr);
				//We are instead sending them through this data class (that was created for this purpose)
				ReceivedMarkupFeaturesData.setFeaturesToRemove(removeStrings);
			}
			mContext.sendBroadcast (intent);
			Log.i("nicsParseMarkup", "Successfully parsed features: ( +" + ftrsToAdd + ", -" + ftrsToRemove + ")");
		}
		else
		{
			Log.i("nicsParseMarkup", "no features to parse");
		}
		mFeaturesToAdd.clear();
		mFeaturesToRemove.clear();
		
		RestClient.clearParseMarkupFeaturesTask();
	


	}

}
