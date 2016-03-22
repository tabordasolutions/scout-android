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
package edu.mit.ll.nics.android.formgen;

import org.json.JSONException;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import edu.mit.ll.nics.android.MainActivity;
import edu.mit.ll.nics.android.R;
import edu.mit.ll.nics.android.api.DataManager;
import edu.mit.ll.nics.android.dialogs.ColorPickerDialog;
import edu.mit.ll.nics.android.fragments.FormFragment;

public class FormColorPicker extends FormWidget {
	protected TextView mLabel;
	protected Button mColorPickerButton;
	int mCurrentColor;
	
	public FormColorPicker(FragmentActivity context, String name, String displayText, boolean enabled, OnFocusChangeListener listener,Fragment fragment) {
		super(context, name, displayText,fragment);

		mEnabled = enabled;
		
		if(mDisplayTextKey != null && !mDisplayTextKey.isEmpty())  {
			mLabel = new TextView(context);
			mLabel.setText(getDisplayText());
			mLabel.setLayoutParams(FormFragment.defaultLayoutParams);
			mLayout.addView(mLabel);
		}
		
		mColorPickerButton = new Button(context);
		mColorPickerButton.setLayoutParams(FormFragment.defaultLayoutParams);
		mColorPickerButton.setImeOptions(EditorInfo.IME_ACTION_NEXT);
		mColorPickerButton.setEnabled(enabled);
		mColorPickerButton.setOnFocusChangeListener(listener);
		mColorPickerButton.setText(mContext.getString(R.string.select_color));
//		mColorPickerButton.setTag(property);
		
        mColorPickerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ColorPickerDialog dialog = new ColorPickerDialog();
				dialog.setTargetFragment(mFragment, 200);
				MainActivity activity = (MainActivity)DataManager.getInstance().getActiveActivity();
				dialog.show(activity.getSupportFragmentManager(), "markup_color_dialog");
			}
		});
//        setColor(Color.parseColor("#000000"));
		mLayout.addView(mColorPickerButton);
	}
	
	public void setColor(int temp) {
		mColorPickerButton.getBackground().setColorFilter(temp, PorterDuff.Mode.SRC);
		mCurrentColor = temp;
	}
	
	@Override
	public String getValue() {
		String strColor = String.format("#%06X", 0xFFFFFF & mCurrentColor);
		return strColor;
	}

	@Override
	public void setValue(String value) {
		if(value == null || value.equals("")){
			setColor(Color.parseColor("#000000"));
		}else{
			if(value.charAt(0) != '#'){
				value = "#" + value;
			}
			setColor(Color.parseColor(value));
		}
	}

	@Override
	public void setEditable(boolean isEditable) {
		if(isEditable){
			mColorPickerButton.setText("Select Color");
			mColorPickerButton.setClickable(true);
		}else{
			mColorPickerButton.setText("");
			mColorPickerButton.setClickable(false);
		}
	}
}
