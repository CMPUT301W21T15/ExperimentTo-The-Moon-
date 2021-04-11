package com.example.Experiment_To_The_Moon;

import android.app.Activity;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.zxing.integration.android.IntentIntegrator;
import com.robotium.solo.Solo;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
/**
 * Test class for MainActivity. All the UI tests are written here. Robotium test framework is used
 */
@RunWith(AndroidJUnit4.class)

public class IntegrationTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);
    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{

        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkActivityNavigation() {
        solo.clickOnButton("Go");
        solo.assertCurrentActivity("Wrong Activity", SearchActivity.class);
        solo.goBack();
        solo.clickOnButton("Add Experiment");
        assertTrue(solo.waitForFragmentByTag("ADD_EXPERIMENT", 1000));
        solo.goBack();
        solo.clickOnButton("Profile");
        assertTrue(solo.waitForFragmentByTag("PROFILE_SEARCH", 1000));
        solo.goBack();
        // cannot test QR scanner, uses a custom library that isn't a fragment
    }

    @Test
    public void checkUserContactInfo() {
        solo.clickOnButton("Profile");
        solo.clickOnButton("My Profile");
        solo.clearEditText((EditText) solo.getView(R.id.contact_info));
        solo.enterText((EditText) solo.getView(R.id.contact_info), "Test Contact Information");
        solo.clickOnView(solo.getView(R.id.user_profile_update));
        solo.clickOnView(solo.getView(R.id.user_profile_back));
        solo.clickOnButton("Profile");
        solo.clickOnButton("My Profile");
        assertTrue(solo.searchEditText("Test Contact Information"));
    }

    @Test
    public void checkExperimentCreation() {
        solo.clickOnButton("Add Experiment");
        solo.enterText((EditText) solo.getView(R.id.exp_name_editText), "Test Experiment Name");
        solo.enterText((EditText) solo.getView(R.id.exp_description_editText), "Test Experiment Description");
        solo.enterText((EditText) solo.getView(R.id.exp_region_editText), "Test Experiment Region");
        solo.enterText((EditText) solo.getView(R.id.exp_min_trials_editText), "10");
        solo.clickOnView(solo.getView(R.id.exp_trial_type_spinner));
        solo.scrollToTop();
        solo.clickOnView(solo.getView(TextView.class, 0)); // get binomial
        solo.clickOnButton("OK");
        assertTrue(solo.waitForText("Test Experiment Name", 1, 5000));

        solo.clickOnText("Test Experiment Name");
        assertTrue(solo.waitForText("Test Experiment Description"));
        assertTrue(solo.waitForText("Test Experiment Region"));
        assertTrue(solo.waitForText("Min trials: 10"));
        assertTrue(solo.waitForText("Binomial"));
        assertTrue(solo.waitForText("Total trials: 0"));

        solo.clickOnView(solo.getView(R.id.unpublish_button));
        assertEquals(solo.getView(R.id.participate_button).getVisibility(), View.INVISIBLE);
        solo.clickOnView(solo.getView(R.id.unpublish_button));
        assertEquals(solo.getView(R.id.participate_button).getVisibility(), View.VISIBLE);

        solo.clickOnView(solo.getView(R.id.blacklist_button));
        solo.clickOnButton("Enter User ID");
        solo.enterText((EditText) solo.getView(R.id.inputUserId), "Test UID Ban");
        solo.clickOnButton("Ban");
        solo.clickOnView(solo.getView(R.id.blacklist_button));
        assertTrue(solo.waitForText("Test UID Ban"));
        solo.goBack();

        solo.clickOnButton("Q&A");
        solo.assertCurrentActivity("Wrong Activity", Question.class);
        solo.clickOnView(solo.getView(R.id.add_post));
        solo.enterText((EditText) solo.getView(R.id.body), "Test Question");
        solo.clickOnButton("Ok");
        assertTrue(solo.waitForText("Test Question"));
        solo.clickOnText("Test Question");
        solo.enterText((EditText) solo.getView(R.id.body), "Test Answer");
        solo.clickOnButton("Ok");
        assertTrue(solo.waitForText("Test Answer"));
        solo.goBack();
        solo.goBack();

        // adding trials doesn't work atm

        // deleting the experiment TEST LAST
        solo.clickOnButton("Delete");
        assertFalse(solo.waitForText("Test Experiment Name", 1, 5000));
    }

}
