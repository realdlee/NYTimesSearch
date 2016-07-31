package com.lee.nytimessearch.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.lee.nytimessearch.Filter;
import com.lee.nytimessearch.R;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {
    Filter filter;
    Spinner sortSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        filter = (Filter) Parcels.unwrap(getIntent().getParcelableExtra("filter"));
        setupToolbar();
        setupSpinner();
        setupCheckBoxes();
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Filters");
    }

    public void setupSpinner() {
        sortSpinner = (Spinner) findViewById(R.id.spinnerDateSort);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);
        int position = adapter.getPosition(filter.sort);
        sortSpinner.setSelection(position);
        sortSpinner.setOnItemSelectedListener(this);

        setBeginDateTextView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // This is the up button
            case android.R.id.home:
                saveAndReturnToParent();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setBeginDateTextView() {
        TextView tvBeginDate = (TextView) findViewById(R.id.tvBeginDate);
        if(filter.getBeginDate() != null) {
            SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy");
            tvBeginDate.setText(format.format(filter.beginDate.getTime()));
        } else {
            tvBeginDate.setText("Click to select");
        }
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        filter.sort = sortSpinner.getSelectedItem().toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // required
    }

    public void setupCheckBoxes() {
        CheckBox cbHealth = (CheckBox) findViewById(R.id.cbHealth);
        CheckBox cbSports = (CheckBox) findViewById(R.id.cbSports);
        CheckBox cbBusiness = (CheckBox) findViewById(R.id.cbBusiness);
        cbHealth.setChecked(filter.newsDesk.contains("health"));
        cbSports.setChecked(filter.newsDesk.contains("sports"));
        cbBusiness.setChecked(filter.newsDesk.contains("business"));
    }

    public void checkBoxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();

        switch(view.getId()) {
            case R.id.cbHealth:
                if (checked) {
                    filter.newsDesk.add("health");
                } else {
                    filter.newsDesk.remove("health");
                }
                break;
            case R.id.cbSports:
                if (checked) {
                    filter.newsDesk.add("sports");
                } else {
                    filter.newsDesk.remove("sports");
                }
                break;
            case R.id.cbBusiness:
                if (checked) {
                    filter.newsDesk.add("business");
                } else {
                    filter.newsDesk.remove("business");
                }
                break;
        }
    }

    public void onSave(View view) {
        saveAndReturnToParent();
    }

    public void saveAndReturnToParent() {
        Intent i = new Intent();
        i.putExtra("code", 200);
        i.putExtra("filter", Parcels.wrap(filter));
        setResult(RESULT_OK, i);
        finish();
    }

    public void showDatePickerDialog(View v) {
        int year = 0;
        int month = 0;
        int day = 0;
        if(filter.getBeginDate() != null) {
            year = filter.getBeginDate().get(Calendar.YEAR);
            month = filter.getBeginDate().get(Calendar.MONTH);
            day = filter.getBeginDate().get(Calendar.DAY_OF_MONTH);
        }
        DatePickerFragment newFragment = DatePickerFragment.newInstance(year, month, day);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        // store the values selected into a Calendar instance
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, monthOfYear);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        filter.beginDate = c;
        setBeginDateTextView();
    }
}
