package com.vitin.mylaptop.tipcalculator;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.text.NumberFormat;






public class MainActivity extends AppCompatActivity implements OnEditorActionListener, OnClickListener {


    //define data into widget
    private EditText billamountEditText;
    private TextView percenttextView;
    private Button percentupbutton;
    private Button percentdownButton;
    private TextView tiptextView;
    private TextView totaltextView;
    private RadioButton roundNoneRadioButton;
    private RadioButton roundTipRadioButton;
    private RadioButton roundTotalRadioButton;
    private Spinner splitSpinner;
    private TextView perPersonLabel;
    private TextView perPersonTextView;
    private Button   applyButton;

    // define the SharedPreferences object
    private SharedPreferences savedValues;

    //define instance variables  that needs to be save
    private float tipPercent = .15f;
    private String billAmountString = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // get reference into wigdget
        billamountEditText = (EditText) findViewById(R.id.billamountEditText);
        percenttextView = (TextView) findViewById(R.id.percenttextView);
        percentupbutton = (Button) findViewById(R.id.percentupbutton);
        percentdownButton = (Button) findViewById(R.id.percentdownButton);
        tiptextView = (TextView) findViewById(R.id.tiptextView);
        totaltextView = (TextView) findViewById(R.id.totaltextView);
        roundNoneRadioButton = (RadioButton) findViewById(R.id.roundNoneRadioButton);
        roundTipRadioButton = (RadioButton) findViewById(R.id.roundTipRadioButton);
        roundTotalRadioButton = (RadioButton) findViewById(R.id.roundTotalRadioButton);
        splitSpinner = (Spinner) findViewById(R.id.splitSpinner);
        perPersonLabel = (TextView) findViewById(R.id.perPersonLabel);
        perPersonTextView = (TextView) findViewById(R.id.perPersonTextView);
        applyButton = (Button) findViewById(R.id.applyButton);


        // set array adapter for spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.split_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        splitSpinner.setAdapter(adapter);

        // set the listeners
        billamountEditText.setOnEditorActionListener(this);
        percentupbutton.setOnClickListener(this);
        percentdownButton.setOnClickListener(this);
        applyButton.setOnClickListener(this);

        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);
    }

    @Override
    public void onPause() {
        // save the instance variables
        Editor editor = savedValues.edit();
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.commit();

        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        // get the instance variables

        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billamountEditText.setText(billAmountString);

        // calculate and display
        calculateAndDisplay();
    }

    public void calculateAndDisplay() {

        // get the bill amount
        String billAmountString = billamountEditText.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        }
        else {
            billAmount = Float.parseFloat(billAmountString);
        }

        // calculate tip and total
        float tipAmount = 0;
        float totalAmount = 0;
        if (roundNoneRadioButton.isChecked()) {
            tipAmount = billAmount * tipPercent;
            totalAmount = billAmount + tipAmount;
        }
        else if (roundTipRadioButton.isChecked()) {
            tipAmount = StrictMath.round(billAmount * tipPercent);
            totalAmount = billAmount + tipAmount;
        }
        else if (roundTotalRadioButton.isChecked()) {
            float tipNotRounded = billAmount * tipPercent;
            totalAmount = StrictMath.round(billAmount + tipNotRounded);
            tipAmount = totalAmount - billAmount;
        }

        // split amount and show/hide split amount widgets
        int splitPosition = splitSpinner.getSelectedItemPosition();
        int split = splitPosition + 1;
        float perPersonAmount = 0;
        if (split == 1) {  // no split - hide widgets
            perPersonLabel.setVisibility(View.GONE);
            perPersonTextView.setVisibility(View.GONE);
        }
        else { // split - show widgets
            perPersonAmount = totalAmount / split;
            perPersonLabel.setVisibility(View.VISIBLE);
            perPersonTextView.setVisibility(View.VISIBLE);
        }

        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tiptextView.setText(currency.format(tipAmount));
        totaltextView.setText(currency.format(totalAmount));
        perPersonTextView.setText(currency.format(perPersonAmount));

        NumberFormat percent = NumberFormat.getPercentInstance();
        percenttextView.setText(percent.format(tipPercent));
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.percentdownButton:
                tipPercent = tipPercent - .01f;
                roundNoneRadioButton.setChecked(true);
                calculateAndDisplay();
                break;
            case R.id.percentupbutton:
                tipPercent = tipPercent + .01f;
                roundNoneRadioButton.setChecked(true);
                calculateAndDisplay();
                break;
            case R.id.applyButton:
                calculateAndDisplay();
                break;
        }
    }
}