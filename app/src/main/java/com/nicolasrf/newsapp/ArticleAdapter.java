package com.nicolasrf.newsapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Nicolas on 5/02/2018.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder>{
    private static final String TAG = "ArticleAdapter";

    private ArrayList<Article> mArticles = new ArrayList<>();
    private OnArticleClickListener OnArticleClickListener;
    private Context mContext;

    /**METODOS para pasar el article correspondiente.**/

    //interface para pasar el metodo onArticleList mediante el cual pasaremos el article correspondiente al MainActivity
    // y poder hacer click en el elemento article de la lista.
    public interface OnArticleClickListener {
        // Se utiliza en el onClickListener del onBindViewHolder.
        void onArticleList(Article article);
    }
    //Metodo donde pasamos el OnArticleClickListener.
    public void setOnArticleClickListener(OnArticleClickListener OnArticleClickListener){
        this.OnArticleClickListener = OnArticleClickListener;
    }
    /*****/

    public ArticleAdapter(Context mContext, ArrayList<Article> articles) {
        this.mContext = mContext;
        this.mArticles = articles;
    }

    /**
     * Metodo resposnsable de inflar la vista
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_article, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        final Article article = mArticles.get(position);

        holder.titleTv.setText(article.getArticleTitle());
        holder.sectionTv.setText(article.getArticleSection());
        holder.dateTv.setText(Utils.formatDate(article.getArticlePublishTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Al hacer click llamamos al método onArticleList y pasamos como argumento el article correspondiente.
                OnArticleClickListener.onArticleList(article);
            }
            //en vez de hacerlo para el itemView se podria hacer para click en otra parte (petNameTextView, u otro)
        });
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTv, sectionTv, dateTv ;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTv = itemView.findViewById(R.id.title_text_view);
            sectionTv = itemView.findViewById(R.id.section_text_view);
            dateTv = itemView.findViewById(R.id.date_text_view);
        }
    }

    public void clearAdapter() {
        int size = this.mArticles.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                mArticles.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void addArticles(ArrayList<Article> articles) {
        this.mArticles.addAll(articles);
        this.notifyItemRangeInserted(0, articles.size() - 1);
    }
}