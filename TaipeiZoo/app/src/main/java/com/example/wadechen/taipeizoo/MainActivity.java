package com.example.wadechen.taipeizoo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;

import com.example.wadechen.taipeizoo.about.ContactFragment;
import com.example.wadechen.taipeizoo.category.CategoryFragment;
import com.example.wadechen.taipeizoo.introduction.IntroducationFragment;
import com.example.wadechen.taipeizoo.network.NetWorkHelper;
import com.example.wadechen.taipeizoo.service.Cache;
import com.example.wadechen.taipeizoo.service.NetworkService;

import static com.example.wadechen.taipeizoo.service.NetworkService.MSG_GET_IMAGE_FILE;
import static com.example.wadechen.taipeizoo.service.NetworkService.MSG_GET_JSON_FILE;
import static com.example.wadechen.taipeizoo.service.NetworkService.MSG_REGISTER_CLIENT;
import static com.example.wadechen.taipeizoo.service.NetworkService.MSG_UNREGISTER_CLIENT;

public class MainActivity extends AppCompatActivity {

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger mMessenger = new Messenger(new ActivityTaskHandler());
    /**
     * Messenger for communicating with service.
     */
    Messenger mService = null;
    /**
     * Flag indicating whether we have called bind on the service.
     */
    boolean mIsBound;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private CategoryFragment mCategoryFragment = new CategoryFragment();
    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.
            mService = new Messenger(service);

            // We want to monitor the service for as long as we are
            // connected to it.
            try {
                Message msg = Message.obtain(null, MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
                L.i("onServiceConnected...");

            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
                e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            try {
                Message msg = Message.obtain(null, MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
                L.i("onServiceDisconnected...");

            } catch (RemoteException e) {
                // In this case the service has crashed before we could even
                // do anything with it; we can count on soon being
                // disconnected (and then reconnected if it can be restarted)
                // so there is no need to do anything here.
                e.printStackTrace();
            }
            mService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        mTabLayout.setupWithViewPager(mViewPager);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setupViewPager(mViewPager);
        //start service to downlaod images
        startService(new Intent(this, NetworkService.class));
        //bind service to notify dowload progress to UI
        doBindService();
    }

    private void findViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    private void setupViewPager(ViewPager viewPager) {
        TabPageAdapter adapter = new TabPageAdapter(getSupportFragmentManager());
        adapter.addFragment(mCategoryFragment, getString(R.string.app_category));
        adapter.addFragment(new IntroducationFragment(), getString(R.string.app_intro));
        adapter.addFragment(new ContactFragment(), getString(R.string.app_about));
        viewPager.setAdapter(adapter);
    }

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        bindService(new Intent(this, NetworkService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with
            // it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service
                    // has crashed.
                }

                // Detach our existing connection.
                unbindService(mConnection);
                mIsBound = false;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    /**
     * Handler of messages from service.
     */
    class ActivityTaskHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_JSON_FILE:
                case MSG_GET_IMAGE_FILE:
                    mCategoryFragment.showView();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

}
