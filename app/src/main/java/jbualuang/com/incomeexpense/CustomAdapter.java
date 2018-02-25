package jbualuang.com.incomeexpense;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.NumberFormat;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter {
      private Context mContext;
      private ArrayList mItems;
      private final int SECTION_ITEM = 0;
      private final int CHILD_ITEM = 1;
      private boolean mIsFirstChild =  true;

      public CustomAdapter(Context context, ArrayList items) {
            mContext = context;
            mItems = items;
      }

      @Override
      public int getItemCount() {
            return mItems.size();
      }

      @Override
      public int getItemViewType(int position) {
            if(mItems.get(position) instanceof SectionItem) {
                  return SECTION_ITEM;
            } else if(mItems.get(position) instanceof ChildItem) {
                  return CHILD_ITEM;
            }
            return -1;
      }

      @Override
      public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            final RecyclerView.ViewHolder vHolder;
            if(viewType == SECTION_ITEM) {
                  View v = inflater.inflate(R.layout.section_layout, parent, false);
                  vHolder = new SectionHolder(v);
                  return vHolder;
            } else if(viewType == CHILD_ITEM) {
                  View v = inflater.inflate(R.layout.child_layout, parent, false);
                  vHolder = new ChildHolder(v);
                  v.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                              int pos = vHolder.getAdapterPosition();
                              if(pos != RecyclerView.NO_POSITION) {
                                    ChildItem item = (ChildItem)mItems.get(pos);
                                    mListener.onItemClick(item._id);
                              }
                        }
                  });
                  return vHolder;
            }
            return null;
      }

      @Override
      public void onBindViewHolder(RecyclerView.ViewHolder vHolder, int position) {
            int type = getItemViewType(position);
            if(type == SECTION_ITEM) {
                  SectionItem item = (SectionItem) mItems.get(position);
                  SectionHolder secHolder = (SectionHolder) vHolder;
                  secHolder.textView.setText(item.sectionText);
                  mIsFirstChild = true;
            } else if(type == CHILD_ITEM) {
                  ChildItem item = (ChildItem) mItems.get(position);
                  ChildHolder childHolder = (ChildHolder) vHolder;
                  childHolder.icon.setImageResource(item.iconId);
                  childHolder.textView.setText(item.itemText);

                  childHolder.textAmount.setText(NumberFormat.getIntegerInstance().format(item.amount));
                  if(item.iconId == R.drawable.ic_minus_circle) {
                        childHolder.textAmount.setTextColor(Color.parseColor("#990000"));
                  }


                  boolean isLastOfSection = position < mItems.size() - 1 && getItemViewType(position + 1) == SECTION_ITEM;
                  boolean isLastOfAll = position == mItems.size() - 1;
                  boolean isLastChild = isLastOfSection || isLastOfAll;

                  if(mIsFirstChild && isLastChild) {
                        childHolder.textView.getRootView().setBackgroundResource(R.drawable.one_item_state);
                        mIsFirstChild = false;
                  } else if(mIsFirstChild || position == 0) {
                        childHolder.textView.getRootView().setBackgroundResource(R.drawable.top_item_state);
                        mIsFirstChild = false;
                  } else if(isLastChild) {
                        childHolder.textView.getRootView().setBackgroundResource(R.drawable.bottom_item_state);
                  }
            }
      }

      interface OnItemClickListener {
            void onItemClick(int _id);
      }

      private OnItemClickListener mListener;

      public void setOnItemClickListener(OnItemClickListener listener) {
            mListener = listener;
      }
}

