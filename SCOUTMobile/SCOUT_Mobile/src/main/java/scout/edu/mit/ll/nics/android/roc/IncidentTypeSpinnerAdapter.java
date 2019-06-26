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

package scout.edu.mit.ll.nics.android.roc;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import scout.edu.mit.ll.nics.android.R;

/**
 * Created by luisgutierrez on 6/25/19.
 * This class implements an Android spinner adapter that shows a checkbox and a TextView
 * It shows list of incident types, and allows the user to select / deselect incident types
 * The selected incident types are stored in an the array "selected_items"
 */


public class IncidentTypeSpinnerAdapter<T> extends BaseAdapter
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
	// The ReportOnConditionFragment to notify of changes
	private ReportOnConditionFragment fragment;

	IncidentTypeSpinnerAdapter(Context c, String h, List<SpinnerItem<T>> l, Set<T> s, TextView t, ReportOnConditionFragment f)
	{
		context = c;
		headerText = h;
		all_items = l;
		selected_items = s;
		textView = t;
		fragment = f;
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

					// Calling notifyDataSetAdapter on the IncidentTypeSpinnerAdapter instance
					notifyDataSetChanged();
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

	@Override
	public void notifyDataSetChanged ()
	{
		super.notifyDataSetChanged();
		Log.e("ROC","ROCIncidentType - IncidentTypeSpinnerAdapter - notifyDataSetChanged");

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

		// If the fragment is not null, notify that the incident type has changed
		if(fragment != null)
		{
			Log.e("ROC","ROCIncidentType - IncidentTypeSpinnerAdapter - fragment is not null, calling incidentTypeChanged");
			fragment.incidentTypeChanged();
		}
		Log.e("ROC","ROCIncidentType - IncidentTypeSpinnerAdapter - notifyDataSetChanged finished");
	}

	private class ViewHolder
	{
		private TextView mTextView;
		private CheckBox mCheckBox;
	}



}