package edu.ucsd.cse110.successorator.ui.cardlist;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.databinding.ListItemBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class GoalListAdapter extends ArrayAdapter<Goal> {
    Consumer<Integer> onDeleteClick;
    public GoalListAdapter(Context context, List<Goal> goals, Consumer<Integer> onDeleteClick) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(goals));
        this.onDeleteClick = onDeleteClick;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the goal for this position.
        var goal = getItem(position);
        // assert goal != null;

        // Check if a view is being reused...
        ListItemBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListItemBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemBinding.inflate(layoutInflater, parent, false);
        }

        // Populate the view with the goal's data.
        // binding.cardFrontText.setText(goal.text());

        binding.taskCompleted.setText(goal.text());
        if (goal.goalStatus()) {
            binding.taskCompleted.setPaintFlags(binding.taskCompleted.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            binding.taskCompleted.setPaintFlags(binding.taskCompleted.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        /*
        String text = "Text with strikethrough";
        SpannableString spannableString = new SpannableString(text);
        spannableString.setSpan(new StrikethroughSpan(), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableString);
         */


        // Bind the delete button to the callback.
        // callback is performed when button is clicked
        binding.cardDeleteButton.setOnClickListener(v -> {
            var id = goal.id();
            // assert id != null;
            onDeleteClick.accept(id);

        });

        return binding.getRoot();
    }

    // The below methods aren't strictly necessary, usually.
    // But get in the habit of defining them because they never hurt
    // (as long as you have IDs for each item) and sometimes you need them.

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getItemId(int position) {
        var goal = getItem(position);
        // assert goal != null;

        var id = goal.id();
        // assert id != null;

        return id;
    }
}
