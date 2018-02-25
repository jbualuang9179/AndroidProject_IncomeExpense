package jbualuang.com.incomeexpense;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class IncomeExpenseActivity extends AppCompatActivity {

      private SQLiteHelper mSqlite;
      private SQLiteDatabase mDb;
      private Spinner mSpinnerMonth;
      private Spinner mSpinnerYear;

      private String[] mThaiMonths = {
              "มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม", "มิถุนายน",
              "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"
      };

      @Override
      protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_income_expense);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle("");
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                        startActivity(new Intent(IncomeExpenseActivity.this, InputDataActivity.class));
                  }
            });


            Calendar cal = Calendar.getInstance();
            int currentMonth = cal.get(Calendar.MONTH);
            int currentYear = cal.get(Calendar.YEAR) + 543;   //แปลงเป็น พ.ศ.


            mSpinnerMonth = (Spinner) findViewById(R.id.spinner_month);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, mThaiMonths);
            mSpinnerMonth.setAdapter(adapter);
            mSpinnerMonth.setSelection(currentMonth);


             ArrayList years = new ArrayList();
            for(int i = 0; i < 3; i++) {
                  years.add(String.valueOf(currentYear - i));
            }
            mSpinnerYear = (Spinner) findViewById(R.id.spinner_year);
            adapter = new ArrayAdapter<>(this, R.layout.spinner_item, years);
            mSpinnerYear.setAdapter(adapter);
            mSpinnerYear.setSelection(0);


            mSpinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        createItem();
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) { }
            });

            mSpinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                  @Override
                  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        createItem();
                  }

                  @Override
                  public void onNothingSelected(AdapterView<?> parent) { }
            });

            createItem();
            setSpinnerDropdownHeight();   //ปรับแต่ง dropdown ของ Spinner
      }

      private void createItem() {
            mSqlite = SQLiteHelper.getInstance(this);
            mDb = mSqlite.getWritableDatabase();

            String sql =
                    "SELECT * FROM table_income_expense " +
                    "WHERE month = ? AND year = ?  " +
                    "ORDER BY date";


            int m = mSpinnerMonth.getSelectedItemPosition() + 1;
            int y = Integer.valueOf(mSpinnerYear.getSelectedItem().toString()) - 543;

            String[] args = {m + "", y + ""};
            Cursor cursor = mDb.rawQuery(sql, args);

            //_id(0), date(1), month(2), year(3), type(4), title(5), amount(6)
            int date = 0;
            String dateMonth = "";
            String type = "";
            int drawable = 0;
            String title = "";
            int amount = 0;
            int lastDate = 0;
            int total_income = 0;
            int total_expense = 0;
            int _id = 0;
            ArrayList items = new ArrayList();

            while(cursor.moveToNext()) {
                  _id = cursor.getInt(0);
                  date = cursor.getInt(1);
                  if(date != lastDate) {
                        if(cursor.getInt(1) < 10) {
                              dateMonth = "0";
                        } else {
                              dateMonth = "";
                        }
                        dateMonth += cursor.getInt(1) + "  " + mThaiMonths[cursor.getInt(2) - 1];
                        items.add(new SectionItem(dateMonth));
                        lastDate = date;
                  }

                  type = cursor.getString(4);
                  title = cursor.getString(5);
                  amount = cursor.getInt(6);
                  if(type.equals("+")) {
                        drawable = R.drawable.ic_plus_circle;
                        total_income += amount;
                  } else if(type.equals("-")) {
                        drawable = R.drawable.ic_minus_circle;
                        total_expense += amount;
                  }

                  items.add(new ChildItem(_id, drawable, title, amount));
            }

            CustomAdapter adapter = new CustomAdapter(this, items);
            RecyclerView rcv = (RecyclerView) findViewById(R.id.recyclerView);
            rcv.setAdapter(adapter);
            rcv.setLayoutManager(new LinearLayoutManager(this));


            String ic = NumberFormat.getIntegerInstance().format(total_income);
            TextView textIncome = (TextView) findViewById(R.id.text_income);
            textIncome.setText(ic);

            String ex = NumberFormat.getIntegerInstance().format(total_expense);
            TextView textExpense = (TextView) findViewById(R.id.text_expense);
            textExpense.setText(ex);

            String ba = NumberFormat.getIntegerInstance().format(total_income - total_expense);
            TextView textBalance = (TextView) findViewById(R.id.text_balance);
            textBalance.setText(ba);

            cursor.close();


            adapter.setOnItemClickListener(new CustomAdapter.OnItemClickListener() {
                  @Override
                  public void onItemClick(int _id) {
                        showItemDialog(_id);
                  }
            });
      }

      private void showItemDialog(final int _id) {
            FragmentManager fm = getSupportFragmentManager();
            String[] items = {"แก้ไข", "ลบ"};

            ItemDialog dialog = ItemDialog.newInstance("เลือกสิ่งที่จะทำ", items);
            dialog.show(fm, null);
            dialog.setOnFinishDialogListener(new ItemDialog.OnFinishDialogListener() {
                  @Override
                  public void onFinishDialog(int selectedIndex) {
                        if(selectedIndex == -1) {
                              return;
                        }

                        if(selectedIndex == 0) {
                              Intent intent = new Intent(IncomeExpenseActivity.this, InputDataActivity.class);
                              intent.putExtra("_id", _id + "");
                              startActivity(intent);
                        } else if(selectedIndex == 1) {   //ถ้าเลือก "ลบ"
                              String sql = "DELETE FROM table_income_expense WHERE _id = " + _id;
                              mDb.execSQL(sql);
                              createItem();
                        }
                  }
            });
      }

      private void setSpinnerDropdownHeight() {
            mSpinnerMonth.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                        mSpinnerMonth.setDropDownVerticalOffset(mSpinnerMonth.getHeight() + 5);
                  }
            }, 500);

            mSpinnerYear.postDelayed(new Runnable() {
                  @Override
                  public void run() {
                        mSpinnerYear.setDropDownVerticalOffset(mSpinnerYear.getHeight() + 5);
                  }
            }, 500);
      }

      private void showToast(String msg) {
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
      }

      @Override
      public void onBackPressed() {
            startActivity(new Intent(IncomeExpenseActivity.this, MainActivity.class));
      }

}
