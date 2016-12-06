package myapplication.sairamkrishna.example.com.githubprofilesgrabber;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String ASYNKTASK_IN_PROGRESS = "ASYNKTASK_IN_PROGRESS";
    public static final String PROFILES_LIST = "PROFILES_LIST";
    public static final String HTTPS_API_GITHUB_COM_USERS = "https://api.github.com/users";
    public static final String BUTTON_VISIBILITY = "BUTTON_VISIBILITY";
    List<ProfileData> profiles;
    ProfileAdapter adapter;
    private ProgressBar progressBar;
    Button but;
    ListView profile_list;
    String loginName;
    String avatarSource;
    String htmlUrl;
    boolean isVisible;
    boolean AsynkTaskStarted;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean AsynkTaskStarted = false;

        profiles = new ArrayList<ProfileData>();
        profile_list = (ListView)findViewById(R.id.profile_list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        but = (Button) findViewById(R.id.but1);

        //checks whether activity is recreated, whether screen has been rotated
        if(savedInstanceState != null)
        {

            AsynkTaskStarted = savedInstanceState.getBoolean(ASYNKTASK_IN_PROGRESS);

            //checks whether JSON loading has been started
            if (AsynkTaskStarted)
            {
                //Continues to load data
                loadData();

            }else
            {
                //if JSON loading hasn't been started, activity is restored and ListView is repopulated
                profiles = savedInstanceState.getParcelableArrayList(PROFILES_LIST);
                adapter = new ProfileAdapter(this, profiles);
                profile_list.setAdapter(adapter);
            }
        }else {
            //program is opened for the first time, screen hasn't been rotated
            loadData();
        }

        /*
        hides button and assigns value false to a variable to track visibility of the button:
        if  button is visible variable is true, otherwise it's false
        */
        but.setVisibility(View.GONE);
        isVisible = false;

        /*
        * When button is pressed, data on the screen is erased and new data is fetched from
        * url
        * */
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                but.setVisibility(View.GONE);
                isVisible = false;
                profile_list.setAdapter(null);
                profiles.clear();
                loadData();
            }
        });
    }


    /*
    * saves data to use when recreating screen after rotation*/
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ASYNKTASK_IN_PROGRESS, AsynkTaskStarted);
        outState.putBoolean(BUTTON_VISIBILITY, isVisible);

        outState.putParcelableArrayList(PROFILES_LIST, (ArrayList<? extends Parcelable>) profiles);

    }


    /*
    * Gets data to use when recreating screen after rotation
    * */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        isVisible = savedInstanceState.getBoolean(BUTTON_VISIBILITY);
        AsynkTaskStarted = savedInstanceState.getBoolean(ASYNKTASK_IN_PROGRESS);

        /*
        * if button was visible before rotation we make it visible afterwards,
         * otherwise keep it hidden
        * */
        if(isVisible == true)
        {
            but.setVisibility(View.VISIBLE);
        }else
        {
            but.setVisibility(View.GONE);
        }

        /*
        * deals with ListView populating and displaying
        * */
        ProfileAdapter adapter = new ProfileAdapter(this, profiles);

        profile_list = (ListView)findViewById(R.id.profile_list);
        profile_list.setAdapter(adapter);

    }

    /*
    * hides refresh button, shows progressbar and calls AsynkTask
    * which fetches, parses github json data and populates ListView
    * */
    private void loadData() {
        but.setVisibility(View.GONE);
        isVisible = false;
        progressBar.setVisibility(View.VISIBLE);


        new JSONTask().execute(HTTPS_API_GITHUB_COM_USERS);
    }


    public class JSONTask extends AsyncTask<String, String, String> {

        /*
        fetches github JSON data
        */
        @Override
        protected String doInBackground(String... params) {
            AsynkTaskStarted = true;
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }


                return buffer.toString();

            } catch (MalformedURLException e) {
                return getString(R.string.msgMalformedURLException);
            } catch (IOException e) {
                return getString(R.string.msgIOException);
            } finally {
                if (connection != null) {

                    connection.disconnect();

                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    return getString(R.string.msgDataIOException);
                }

            }


        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            /*
            * Checks whether the result is needed github data
            * or a custom message of an error occured.
            * If it's an error message it calls a method to
            * display a message to a user about an error that occured,
            * otherwise parses JSON data
            * */
            if (result == getString(R.string.msgMalformedURLException))
            {
                showErrorAndFinishActivity(result);
            }
            else if(result == getString(R.string.msgIOException))
            {
                showErrorAndFinishActivity(result);
            }
            else if(result == getString(R.string.msgDataIOException))
            {
                showErrorAndFinishActivity(result);
            }
            else
            {
                try
                {
                    //gets required data and populates ArrayList of Profiles
                    JSONArray items = (JSONArray) (new JSONTokener(result).nextValue());
                    for (int i = 0; i < items.length(); i++)
                    {
                        JSONObject item = (JSONObject) items.get(i);

                        loginName = item.getString("login");
                        avatarSource = item.getString("avatar_url");
                        htmlUrl = item.getString("html_url");


                        populateProfileArrayList(loginName, avatarSource, htmlUrl);

                    }
                    adapter = new ProfileAdapter(MainActivity.this, profiles);
                    profile_list.setAdapter(adapter);

                    AsynkTaskStarted = false;
                    progressBar.setVisibility(View.GONE);
                    but.setVisibility(View.VISIBLE);
                    isVisible = true;
                } catch (JSONException e)
                {
                    showErrorAndFinishActivity(getString(R.string.msgJSONException));
                }

            }

        }
    }

    private void populateProfileArrayList(String loginName, String avatarSource, String htmlUrl) {

        profiles.add(new ProfileData(loginName, avatarSource, htmlUrl));
    }

    //shows custom error message to a user an AlertDialogue and finishes activity
    private void showErrorAndFinishActivity(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setPositiveButton(R.string.Alert_Dialogue_Quit_Label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setTitle(R.string.AlertDialogTitle);
        builder.setMessage(message);
        AlertDialog dlg = builder.create();
        dlg.show();
    }


}
