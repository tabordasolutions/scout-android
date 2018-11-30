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
package scout.edu.mit.ll.nics.android.api.payload.forms;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.ReportOnConditionData;

public class ReportOnConditionPayload extends ReportPayload
{
	//OES-828 TODO Need to properly implement this.

	private transient ReportOnConditionData messageData;

	public ReportOnConditionData getMessageData()
	{
		return messageData;
	}

	public void setMessageData(ReportOnConditionData messageData)
	{
		this.messageData = messageData;
	}

	public void parse() {
/*    	messageData = new Gson().fromJson(getMessage().replace("NaN", "0.0"), ReportOnConditionData.class);

    	try
    	{
			JSONObject object = new JSONObject(getMessage());
			if(object.has("cat")){
				Object category = object.get("cat");
				String categoryString = category.toString();

				String reverseLanguageResults = DataManager.getInstance().reverseLanguageLookup(categoryString);
				SimpleReportCategoryType categoryType = SimpleReportCategoryType.lookUp(reverseLanguageResults);

				messageData.setCategory(categoryType);
			}

		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/
	}

	public String toJsonString()
	{
		setMessage(new Gson().toJson(getMessageData()));

		return new Gson().toJson(this);
	}
}
