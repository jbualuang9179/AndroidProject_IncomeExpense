package jbualuang.com.incomeexpense;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class SectionHolder extends RecyclerView.ViewHolder {

      public TextView textView;

      public SectionHolder(View v) {
            super(v);
            textView = (TextView)v.findViewById(R.id.textView_section);
      }
}

