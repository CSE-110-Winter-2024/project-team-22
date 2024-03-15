
package edu.ucsd.cse110.successorator.ui.goallist;


import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.ListItemGoalBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class GoalListAdapter extends ArrayAdapter<Goal> {
    private final Consumer<Goal> onGoalClicked;
    private final Consumer<Goal> onDeleteClicked;
    public GoalListAdapter(
            Context context,
            List<Goal> goals,
            Consumer<Goal> onGoalClicked,
            Consumer<Goal> onDeleteClicked
    ) {
        // This sets a bunch of stuff internally, which we can access
        // with getContext() and getItem() for example.
        //
        // Also note that ArrayAdapter NEEDS a mutable List (ArrayList),
        // or it will crash!
        super(context, 0, new ArrayList<>(goals));
        this.onGoalClicked = onGoalClicked;
        this.onDeleteClicked = onDeleteClicked;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the goal for this position.
        var goal = getItem(position);

        assert goal != null;

        // Check if a view is being reused...
        ListItemGoalBinding binding;
        if (convertView != null) {
            // if so, bind to it
            binding = ListItemGoalBinding.bind(convertView);
        } else {
            // otherwise inflate a new view from our layout XML.
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemGoalBinding.inflate(layoutInflater, parent, false);
        }

        // Populate the view with the goal's data.
        // M -> V
        binding.goalText.setText(goal.text());
        String context = goal.getContext(); // Get the context

        // Set visibility of the context icon
        binding.ContextLabelView.setVisibility(context != null && !context.isEmpty() ? View.VISIBLE : View.INVISIBLE);

        // Set visibility and text of the context label
        TextView contextLabelTextView = binding.contextLabelText;

        if (context != null && !context.isEmpty()) {
            contextLabelTextView.setVisibility(View.VISIBLE);
            switch (context) {
                case "Home":
                    binding.ContextLabelView.setImageResource(R.drawable.home_button);
                    contextLabelTextView.setText("H");
                    break;
                case "School":
                    binding.ContextLabelView.setImageResource(R.drawable.school_button);
                    contextLabelTextView.setText("S");
                    break;
                case "Work":
                    binding.ContextLabelView.setImageResource(R.drawable.work_button);
                    contextLabelTextView.setText("W");
                    break;
                case "Errands":
                    binding.ContextLabelView.setImageResource(R.drawable.errands_button);
                    contextLabelTextView.setText("E");
                    break;
                default:
                    contextLabelTextView.setVisibility(View.GONE); // Hide if context does not match known types
                    break;
            }
        } else {
            // Hide if no context is set
            // This will should/never occur
            contextLabelTextView.setVisibility(View.GONE);
        }

        if (goal.isCompleted()) {
            // TODO: replace with strikethrough
            binding.goalText.setPaintFlags(binding.goalText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.ContextLabelView.setImageResource(R.drawable.done_goal);
            contextLabelTextView.setText("");
        } else {
            binding.goalText.setPaintFlags(binding.goalText.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
//
        // V -> M
        binding.getRoot().setOnClickListener(v->{
            onGoalClicked.accept(goal);
        });

        //Bind the delete button to the callback.
        // binding.cardDeleteButton.setOnClickListener(v -> {
        // onDeleteClicked.accept(goal);
        //  });


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
        var flashcard = getItem(position);
        assert flashcard != null;

        var id = flashcard.id();
        assert id != null;

        return id;
    }
}
