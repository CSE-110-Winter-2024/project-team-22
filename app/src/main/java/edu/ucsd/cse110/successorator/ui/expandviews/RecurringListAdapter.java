package edu.ucsd.cse110.successorator.ui.expandviews;

import android.content.Context;
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
import edu.ucsd.cse110.successorator.databinding.ListItemRecurringBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;

public class RecurringListAdapter extends ArrayAdapter<Goal> {
    private final Consumer<Goal> onGoalClicked;
    private final Consumer<Goal> onDeleteClicked;
    public RecurringListAdapter(
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
        ListItemRecurringBinding binding;
        if (convertView != null) {
            binding = ListItemRecurringBinding.bind(convertView);
        } else {
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemRecurringBinding.inflate(layoutInflater, parent, false);
        }

        // Populate the view with the goal's data.
        // M -> V
        binding.goalTextRecurring.setText(goal.text());
        binding.recurringDate.setText(goal.getDate());
        String context = goal.getContext(); // Get the context

        // Set visibility of the context icon
        binding.ContextLabelViewRecurring.setVisibility(context != null && !context.isEmpty() ? View.VISIBLE : View.INVISIBLE);

        // Set visibility and text of the context label
        TextView contextLabelTextView = binding.contextLabelTextRecurring;

        if (context != null && !context.isEmpty()) {
            contextLabelTextView.setVisibility(View.VISIBLE);
            switch (context) {
                case "Home":
                    binding.ContextLabelViewRecurring.setImageResource(R.drawable.home_button);
                    contextLabelTextView.setText("H");
                    break;
                case "School":
                    binding.ContextLabelViewRecurring.setImageResource(R.drawable.school_button);
                    contextLabelTextView.setText("S");
                    break;
                case "Work":
                    binding.ContextLabelViewRecurring.setImageResource(R.drawable.work_button);
                    contextLabelTextView.setText("W");
                    break;
                case "Errands":
                    binding.ContextLabelViewRecurring.setImageResource(R.drawable.errands_button);
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

        binding.deleteRecurring.setOnClickListener(v->{
            onDeleteClicked.accept(goal);
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
        var flashcard = getItem(position);
        assert flashcard != null;

        var id = flashcard.id();
        assert id != null;

        return id;
    }
}

