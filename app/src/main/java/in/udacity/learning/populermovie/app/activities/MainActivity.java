package in.udacity.learning.populermovie.app.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import in.udacity.learning.populermovie.app.fragment.FragmentMain;
import in.udacity.learning.populermovie.app.R;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize(savedInstanceState);
    }

    public void initialize(Bundle savedInstanceState) {
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frag_container, new FragmentMain()).commit();
        } else {
            Log.e(TAG, "Test");
        }

    }

}
