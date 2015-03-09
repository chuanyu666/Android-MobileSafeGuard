package com.yc.mobilesafeguard.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FocusTextView extends TextView {

	public FocusTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public FocusTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean isFocused() {
		// TODO Auto-generated method stub
		return true;
	} 
}
