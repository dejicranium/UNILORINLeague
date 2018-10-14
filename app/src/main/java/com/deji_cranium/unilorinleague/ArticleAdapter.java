package com.deji_cranium.unilorinleague;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.deji_cranium.unilorinleague.Utils.ArticleUtils;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.util.List;

/**
 * Created by cranium on 10/20/17.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private List<Article> mArticlesList;
    private LayoutInflater inflater;
    private ViewGroup parent;
    private CLickListener listener;
    Typeface mNeuton;

    public ArticleAdapter(Context context, List<Article>articlesList, CLickListener listener){
        this.mArticlesList = articlesList;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }
    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.articles_row, parent, false);
        this.parent = parent;
        mNeuton = Typeface.createFromAsset(parent.getContext().getAssets(), "Neuton-Bold.ttf");

        return new ArticleViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position) {
        String lArticleDate = mArticlesList.get(position).getPubDate();
        holder.articleTitle.setTypeface(mNeuton);

        if(mArticlesList.get(position).getType() == "bookmark"){
            holder.articleTitle.setTextColor(Color.GRAY);

        }

        if (mArticlesList.get(position).getRead() != null) {
            if (mArticlesList.get(position).getRead().equalsIgnoreCase("true")) {
                holder.articleTitle.setTextColor(Color.GRAY);
            }
        }
        try {
            holder.articlePubDate.setText(ArticleUtils.getDate(lArticleDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.articleTitle.setText(mArticlesList.get(position).getTitle());
        holder.articleDescription.setText(Html.fromHtml(mArticlesList.get(position).getDescription()));
    }

    @Override
    public int getItemCount() {
        return mArticlesList.size();
    }







    public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView articleTitle, articleDescription, articleAuthor, articleRead, articleType, articlePubDate;
        ImageButton deleteBookmark;
        private WeakReference<CLickListener> listenerRef;

        public ArticleViewHolder(View itemView, CLickListener listener) {
            super(itemView);

            listenerRef = new WeakReference<CLickListener>(listener);

            articleTitle = (TextView)itemView.findViewById(R.id.article_title);
            articleDescription = (TextView)itemView.findViewById(R.id.article_description);
            articlePubDate = (TextView)itemView.findViewById(R.id.pubDate);
            articleTitle.setOnClickListener(this);
            articleDescription.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            if(view.getId() == articleTitle.getId()){
                putExtrasAndStartIntent();
            }
            if(view.getId() == articleDescription.getId()){
                putExtrasAndStartIntent();
            }
            listenerRef.get().onPositionClicked(getAdapterPosition());
        }

        private void putExtrasAndStartIntent(){
            Intent in  = new Intent(parent.getContext(), DetailsActivity.class);
            in.putExtra("article", mArticlesList.get(getAdapterPosition()));
            articleTitle.setTextColor(Color.GRAY);
            parent.getContext().startActivity(in);

        }
    }
}


