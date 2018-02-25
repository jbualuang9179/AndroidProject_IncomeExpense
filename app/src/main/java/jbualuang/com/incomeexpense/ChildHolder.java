package jbualuang.com.incomeexpense;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ChildHolder extends RecyclerView.ViewHolder {
      public ImageView icon;
      public TextView textView;
      public TextView textAmount;

      public ChildHolder( View v) {
            super(v);
            icon = (ImageView)v.findViewById(R.id.icon);
            textView = (TextView)v.findViewById(R.id.textView_child);
            textAmount = (TextView)v.findViewById(R.id.textView_amount);
      }
}

