package com.example.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ArticleFragment extends Fragment {

    public ArticleFragment() {
        // Required empty public constructor
    }

    static ArticleFragment newInstance(Article article, int index, int max)
    {
        ArticleFragment a = new ArticleFragment();
        Bundle bdl = new Bundle(1);
        bdl.putSerializable("COUNTRY_DATA", article);
        bdl.putSerializable("INDEX", index);
        bdl.putSerializable("TOTAL_COUNT", max);
        a.setArguments(bdl);
        return a;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment_layout = inflater.inflate(R.layout.fragment_article, container, false);

        Bundle args = getArguments();
        if (args != null) {
            final Article currentArticle = (Article) args.getSerializable("COUNTRY_DATA");
            if (currentArticle == null) {
                return null;
            }
            int index = args.getInt("INDEX");
            int total = args.getInt("TOTAL_COUNT");

            TextView headline = fragment_layout.findViewById(R.id.headline);
            headline.setText(currentArticle.getTitle());

            TextView date = fragment_layout.findViewById(R.id.date);
            date.setText(currentArticle.getPublishedAt());

            TextView author = fragment_layout.findViewById(R.id.author);
            author.setText(currentArticle.getAuthor());

            TextView description = fragment_layout.findViewById(R.id.description);
            description.setText(currentArticle.getDescription());

            TextView count = fragment_layout.findViewById(R.id.count);
            count.setText(String.format(Locale.US, "%d of %d", index, total));

            ImageView imageView = fragment_layout.findViewById(R.id.image);
            Picasso.get().load(currentArticle.getUrlToImage())
                    //.error(R.drawable.brokenimage)
                    //.placeholder(R.drawable.placeholder)
                    .into(imageView);

            return fragment_layout;
        } else {
            return null;
        }
    }

    private void clickFlag(String name) {

        Uri mapUri = Uri.parse("geo:0,0?q=" + Uri.encode(name));

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);

    }

}
