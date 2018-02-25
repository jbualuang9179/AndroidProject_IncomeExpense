package jbualuang.com.incomeexpense;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;

public class InputDataActivity extends AppCompatActivity {
      private SQLiteDatabase mDb;
      private SQLiteHelper mSqlite;

      private DatePickerEditText mDatePicker;
      private RadioButton mRadioIncome;
      private RadioButton mRadioExpense;
      private EditText mEditTitle;
      private EditText mEditAmount;
      private String[] mItems;

      private String mType;
      private int mDate;
      private int mMonth;
      private int mYear;
      private String mTitle;
      private int mAmount;
      private String m_id = "";

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_input_data);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("บันทึกรายรับ - รายจ่าย");
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                  }
            });
            fab.hide();

            mSqlite = SQLiteHelper.getInstance(this);
            mDb = mSqlite.getWritableDatabase();

            mRadioIncome = (RadioButton) findViewById(R.id.radio_income);
            mRadioExpense = (RadioButton) findViewById(R.id.radio_expense);

            mDatePicker = (DatePickerEditText) findViewById(R.id.datePickerEditText);
            mDatePicker.setManager(getSupportFragmentManager());

            mEditTitle = (EditText)findViewById(R.id.edit_title);
            mEditAmount = (EditText)findViewById(R.id.edit_amount);


            Intent intent = getIntent();
            if(intent.getStringExtra("_id") != null) {
                  m_id = intent.getStringExtra("_id");
                  readDataFromDb();
            }


            findViewById(R.id.button_date).setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        mDatePicker.onClick(mDatePicker);
                  }
            });


            findViewById(R.id.button_title).setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        showItemDialog();
                  }
            });


            findViewById(R.id.button_save).setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                        readDataFromView();
                  }
            });
      }


      private void readDataFromDb() {

            String sql = "SELECT * FROM table_income_expense WHERE _id = " + m_id;
            Cursor cursor = mDb.rawQuery(sql, null);


            if(cursor.moveToNext()) {
                  if(cursor.getString(4).equals("-")) {
                        mRadioExpense.setChecked(true);
                  } else {
                        mRadioIncome.setChecked(true);
                  }


                  String d = ((cursor.getInt(1) < 10) ? "0" : "") + cursor.getInt(1);
                  String m = ((cursor.getInt(2) < 10) ? "0" : "") + cursor.getInt(2);
                  int y = cursor.getInt(3);
                  mDatePicker.setText(d + "-" + m + "-" + y);

                  mEditTitle.setText(cursor.getString(5));
                  mEditAmount.setText(cursor.getString(6));
            }
      }

      private void readDataFromView() {

            String errMsg = "";
            if(!mRadioIncome.isChecked() && !mRadioExpense.isChecked()) {
                  errMsg = "กรุณาเลือกชนิดรายการ";
            } else if(mDatePicker.getText().toString().isEmpty()) {
                  errMsg = "กรุณากำหนดวันเดือนปี";
            } else if(mEditTitle.getText().toString().trim().isEmpty()) {
                  errMsg = "กรุณาใส่ชื่อรายการ";
            } else if(mEditAmount.getText().toString().trim().isEmpty()) {
                  errMsg = "กรุณาใส่จำนวนเงิน";
            }

            if(!errMsg.isEmpty()) {
                  showToast(errMsg);
                  return;
            }

            if(mRadioExpense.isChecked()) {
                  mType = "-";
            } else if(mRadioIncome.isChecked()) {
                  mType = "+";
            }


            String[] dmy = mDatePicker.getText().toString().split("-");
            mDate = Integer.valueOf(dmy[0]);
            mMonth =  Integer.valueOf(dmy[1]);
            mYear  = Integer.valueOf(dmy[2]);

            mTitle = mEditTitle.getText().toString();


            double a = Double.valueOf(mEditAmount.getText().toString());
            mAmount = (int)a;

            saveData();
      }

      private void saveData() {

            ContentValues cv = new ContentValues();
            cv.put("date", mDate);
            cv.put("month", mMonth);
            cv.put("year", mYear);
            cv.put("type", mType);
            cv.put("title", mTitle);
            cv.put("amount", mAmount);


            if(m_id.equals("")) {
                  mDb.insert("table_income_expense", null, cv);
            } else {
                  mDb.update("table_income_expense", cv, "_id = ?", new String[] {m_id});
            }

            showToast("บันทึกข้อมูลแล้ว");


            mRadioExpense.setChecked(true);
            mDatePicker.setText("");
            mEditTitle.setText("");
            mEditAmount.setText("");
      }


      private void showItemDialog() {
            FragmentManager fm = getSupportFragmentManager();

           String[] items_income = {
                    "เงินเดือน", "ถูกหวย", "โบนัส", "รายได้พิเศษ", "ลูกหนี้ใช้คืน"
            };

            String[] items_expense = {
                    "ค่าเช่าห้อง","จ่ายตลาด", "ช้อปปิ้ง", "ซื้อหวย", "อาหารการกิน",
                    "งวดรถ", "ใช้หนี้", "ค่าน้ำมัน", "ให้ยืม", "ค่าประกัน"
            };


            if(mRadioIncome.isChecked()) {
                  mItems = items_income;
            } else if(mRadioExpense.isChecked()) {
                  mItems = items_expense;
            }

            //แสดง Item Dialog
            ItemDialog dialog = ItemDialog.newInstance("กรุณาเลือกรายการ", mItems);
            dialog.show(fm, null);
            dialog.setOnFinishDialogListener(new ItemDialog.OnFinishDialogListener() {
                  @Override
                  public void onFinishDialog(int selectedIndex) {
                        if(selectedIndex == -1) {
                              return;
                        }

                        mEditTitle.setText(mItems[selectedIndex]);
                  }
            });
      }

      private void showToast(String msg) {
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
      }

      @Override
      public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if(id == android.R.id.home) {
                  startActivity(new Intent(InputDataActivity.this, MainActivity.class));
                  return true;
            }
            return super.onOptionsItemSelected(item);
      }
      /*
      @Override
      public void onBackPressed() {
            startActivity(new Intent(InputDataActivity.this, IncomeExpenseActivity.class));
      }
      */
}
