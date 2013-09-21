package com.mhacks.dealradar;

import android.app.ActionBar;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class DealRadar extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.setCustomView(R.layout.action_bar);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);

    }

}
