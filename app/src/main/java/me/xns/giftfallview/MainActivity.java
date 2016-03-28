package me.xns.giftfallview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.gitfallview)
    GiftFallView mGitfallview;
    @Bind(R.id.simple_giftfall)
    SimpleGiftFallView mSimpleGiftFallView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Bitmap[] bitmaps = {
                BitmapFactory.decodeResource(getResources(), R.mipmap.gold_piece1),
                BitmapFactory.decodeResource(getResources(), R.mipmap.gold_piece2),
                BitmapFactory.decodeResource(getResources(), R.mipmap.gold_piece3)
        };
        mGitfallview.init(bitmaps);
        mSimpleGiftFallView.init(bitmaps);
    }

    @OnClick(R.id.btn_switch)
    public void onClick(final View v) {
        mGitfallview.startFall();
        v.setEnabled(false);
        mGitfallview.postDelayed(new Runnable() {
            @Override
            public void run() {
                mGitfallview.stopFall();
                v.setEnabled(true);
            }
        },2000);
    }

    @OnClick(R.id.btn_switch2)
    public void onClick2(final View v){
        mSimpleGiftFallView.startFall();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
