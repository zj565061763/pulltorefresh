package com.sd.demo.pulltorefresh.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.sd.demo.pulltorefresh.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_button:
                startActivity(new Intent(MainActivity.this, ButtonActivity.class));
                break;
            case R.id.btn_recyclerview:
                startActivity(new Intent(MainActivity.this, RecyclerViewActivity.class));
                break;
            case R.id.btn_listview:
                startActivity(new Intent(MainActivity.this, ListViewActivity.class));
                break;
            case R.id.btn_scrollview:
                startActivity(new Intent(MainActivity.this, ScrollViewActivity.class));
                break;
            case R.id.btn_nested_recyclerview:
                startActivity(new Intent(MainActivity.this, NestedRecyclerViewActivity.class));
                break;
            default:
                break;
        }
    }
}
