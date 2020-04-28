package thegroceryshop.com.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import thegroceryshop.com.R;
import thegroceryshop.com.custom.ValidationUtil;

/**
 * Created by JUNED on 6/10/2016.
 */
public class SearchSuggestionListAdapter extends RecyclerView.Adapter<SearchSuggestionListAdapter.SuggestionHolder> {

    Context mContext;
    private ArrayList<String> list_suggestion;
    private OnSuggesionClickListener onSuggesionClickListener;

    public SearchSuggestionListAdapter(Context mContext, ArrayList<String> list_suggestion) {
        this.list_suggestion = list_suggestion;
        this.mContext = mContext;
    }

    public static class SuggestionHolder extends RecyclerView.ViewHolder  {

        public TextView txt_suggestion;

        public SuggestionHolder(View view) {
            super(view);
            txt_suggestion    = view.findViewById(R.id.search_suggestion_txt_suggestion);
        }
    }

    @Override
    public SuggestionHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_suggestion_item, parent, false);
        return new SuggestionHolder(view);
    }

    @Override
    public void onBindViewHolder(SuggestionHolder holder, final int position) {

        if(!ValidationUtil.isNullOrBlank(list_suggestion.get(position))){
            holder.txt_suggestion.setText(list_suggestion.get(position));
        }
        holder.txt_suggestion.setTag(position);
        holder.txt_suggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSuggesionClickListener != null){
                    onSuggesionClickListener.onSuggestionClick((int)v.getTag());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list_suggestion.size();
    }

    public void setOnSuggestionClickListener(OnSuggesionClickListener onSuggesionClickListener){
        this.onSuggesionClickListener = onSuggesionClickListener;
    }

    public interface OnSuggesionClickListener{
        void onSuggestionClick(int position);
    }
}
