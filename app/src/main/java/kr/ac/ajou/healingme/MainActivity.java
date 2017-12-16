package kr.ac.ajou.healingme;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    private static String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private int status;
    public static ImageView imageView;
    private LetterCountModel letterCountModel;
    private List<LetterCountData> letterCountDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);


        // display the first navigation drawer view on app launch
        displayView(0);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (Objects.equals(status, 1)) {
            getMenuInflater().inflate(R.menu.menu_letter, menu);
        } else if (Objects.equals(status, 2)) {
        } else if (Objects.equals(status, 3)) {
            getMenuInflater().inflate(R.menu.menu_posting, menu);
        } else if (Objects.equals(status, 4)) {
            getMenuInflater().inflate(R.menu.menu_draw, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String title = getString(R.string.app_name);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_customservice) {
            Fragment fragment = new CustomServiceActivity();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            title = getString(R.string.title_customservice);
            getSupportActionBar().setTitle(title);
            return true;
        }

        if (id == R.id.action_mailbox) {
            Intent intent = new Intent(getApplicationContext(), GetLetterActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_album) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivity(intent);
        }

        if (id == R.id.action_posting) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new RecommendActivity();
                title = getString(R.string.title_recommend);
                status = 0;
                break;
            case 1:
                fragment = new LetterFragment();
                title = getString(R.string.title_letters);
                status = 1;
                break;
            case 2:
                fragment = new ChatListFragment();
                title = getString(R.string.title_counsel);
                status = 2;
                break;
            case 3:
                fragment = new CategoryFragment();
                title = getString(R.string.title_hobby);
                status = 3;
                break;
            case 4:
                fragment = new DrawActivity();
                title = getString(R.string.title_draw);
                status = 4;
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(title);
        }
    }


}
