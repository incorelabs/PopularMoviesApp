package com.example.android.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReviewListAdapter extends ArrayAdapter<ReviewCard> {
    private static final String LOG_TAG = ReviewListAdapter.class.getSimpleName();

    // View lookup cache
    private static class ViewHolder {
        TextView reviewAuthor;
        TextView reviewContent;
    }

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context     The current context. Used to inflate the layout file.
     * @param reviewCards A List of ReviewCard objects to display in a list
     */
    public ReviewListAdapter(Context context, ArrayList<ReviewCard> reviewCards) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, R.layout.list_review_item, reviewCards);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the ReviewCard object from the ArrayAdapter at the appropriate position
        ReviewCard reviewCard = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_review_item, parent, false);
            viewHolder.reviewAuthor = (TextView) convertView.findViewById(R.id.review_author_list_item_textview);
            viewHolder.reviewContent = (TextView) convertView.findViewById(R.id.review_content_list_item_textview);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.reviewAuthor.setText(reviewCard.reviewAuthor);
        viewHolder.reviewContent.setText(reviewCard.reviewContent);

        // Return the completed view to render on screen
        return convertView;
    }
}
