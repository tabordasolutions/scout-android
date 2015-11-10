/*|~^~|Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.phinics.android.adapters;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.mit.ll.phinics.android.R;
import edu.mit.ll.phinics.android.api.DataManager;
import edu.mit.ll.phinics.android.api.data.DamageReportData;
import edu.mit.ll.phinics.android.api.data.ReportStatus;
import edu.mit.ll.phinics.android.api.payload.forms.DamageReportPayload;

public class DamageReportListAdapter extends ArrayAdapter<DamageReportPayload> {
	private List<DamageReportPayload> mItems;
	private DataManager mDataManager;
	private Context mContext;
	
	public DamageReportListAdapter(Context context, int resource,
			int textViewResourceId, List<DamageReportPayload> list) {
		
		super(context, resource, textViewResourceId, list);
		
		mContext = context;
		mDataManager = DataManager.getInstance(context);
		mItems = list;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		DamageReportPayload payload = mItems.get(position);
		DamageReportData data = payload.getMessageData();
		
		View row = super.getView(position, convertView, parent);

		TextView name = (TextView)row.findViewById(R.id.damageReportTitle);

		TextView size = (TextView)row.findViewById(R.id.damageReportTime);
		size.setText(new Date(payload.getSeqTime()).toString());
		
		if(payload.isDraft()) {
			name.setText(mContext.getString(R.string.draft) + String.valueOf(data.getUser()));
		} else if(payload.getStatus() == ReportStatus.WAITING_TO_SEND && payload.getProgress() != 100.0) {
			if(!payload.isFailedToSend()) {
				name.setText(mContext.getString(R.string.sending_progress, payload.getProgress()) + String.valueOf(data.getUser()));
			} else {
				name.setText(R.string.sending_failed + String.valueOf(data.getUser()));
			}
			
			if(!mDataManager.isOnline()) {
				size.setText(R.string.device_not_connected_to_network);
			}
		} else {
			name.setText(String.valueOf(data.getUser()));
		}
		
		return(row);
	}
	
	public List<DamageReportPayload> getItems() {
		return mItems;
	}
	
	@Override
	public void addAll(Collection<? extends DamageReportPayload> collection) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			super.addAll(collection);
		} else {
			for(DamageReportPayload payload : collection) {
				add(payload);
			}
		}
	}
}
