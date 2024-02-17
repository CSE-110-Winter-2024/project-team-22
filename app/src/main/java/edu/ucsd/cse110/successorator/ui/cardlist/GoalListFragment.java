package edu.ucsd.cse110.successorator.ui.cardlist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentGoalListBinding;
import edu.ucsd.cse110.successorator.ui.cardlist.dialog.ConfirmDeleteCardDialogFragment;

public class GoalListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentGoalListBinding view;
    //private GoalCompletedListAdapter completedListAdapter;
    private GoalListAdapter goalListAdapter;

    public GoalListFragment() {
        // Required empty public constructor
    }

    public static GoalListFragment newInstance() {
        GoalListFragment fragment = new GoalListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        this.goalListAdapter = new GoalListAdapter(requireContext(), List.of(), id -> {
            var dialogFragment = ConfirmDeleteCardDialogFragment.newInstance(id);
            dialogFragment.show(getParentFragmentManager(), "ConfirmDeleteCardDialogFragment");
        });

        activityModel.getUncompletedGoals().observe(goals -> {
            if (goals == null) return;
            goalListAdapter.clear();
            goalListAdapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
            goalListAdapter.notifyDataSetChanged();
        });

        activityModel.getCompletedGoals().observe(goals -> {
            if (goals == null) return;
            goalListAdapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
            goalListAdapter.notifyDataSetChanged();
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentGoalListBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        //view.completedGoalsList.setAdapter(completedListAdapter);
        view.goalsList.setAdapter(goalListAdapter);

        // change this
        // setHeight(view.completedGoalsList);
        // setHeight(view.uncompletedGoalsList);

        // Show CreateCardDialogFragment
        // TODO: eventually get rid of this button
//        view.createCardButton.setOnClickListener(v -> {
//            var dialogFragment = CreateCardDialogFragment.newInstance();
//            dialogFragment.show(getParentFragmentManager(), "CreateCardDialogFragment");
//        });

        return view.getRoot();
    }
}




