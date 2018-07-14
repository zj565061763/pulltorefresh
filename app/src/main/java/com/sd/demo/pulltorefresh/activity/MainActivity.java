package com.sd.demo.pulltorefresh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fanwe.lib.utils.context.FContext;
import com.sd.demo.pulltorefresh.R;

public class MainActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FContext.set(this);
        findViewById(R.id.btn_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, ButtonActivity.class));
            }
        });

        findViewById(R.id.btn_recyclerview).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));
            }
        });

        findViewById(R.id.btn_listview).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, ListViewActivity.class));
            }
        });

        findViewById(R.id.btn_scrollview).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(MainActivity.this, ScrollViewActivity.class));
            }
        });
    }
}
