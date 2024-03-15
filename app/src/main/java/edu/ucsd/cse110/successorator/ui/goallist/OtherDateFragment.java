package edu.ucsd.cse110.successorator.ui.goallist;

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
import edu.ucsd.cse110.successorator.databinding.FragmentTomorrowBinding;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentGoalListBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentTomorrowBinding;
import edu.ucsd.cse110.successorator.ui.goallist.GoalListAdapter;
import edu.ucsd.cse110.successorator.ui.goallist.GoalListFragment;

public class OtherDateFragment extends Fragment {

    private MainViewModel activityModel;
    private FragmentTomorrowBinding view;
    private GoalListAdapter adapter;

    public OtherDateFragment() {
        // Required empty public constructor
    }

    public static OtherDateFragment newInstance() {
        OtherDateFragment fragment = new OtherDateFragment();
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

        // Initialize the Adapter (with an empty list for now)
        this.adapter = new GoalListAdapter(
                requireContext(),
                List.of(),
                goal -> { // onGoalClicked
                    // When goal is tapped, this is lambda function is called.
                    // NOTE: ConfirmDeleteCardDialogFragment is NOT called.
                },
                goal -> { // something else?
                    // var dialogFragment = ConfirmDeleteCardDialogFragment.newInstance(goal.id());
                    // dialogFragment.show(getParentFragmentManager(), "ConfirmDeleteCardDialogFragment");
                }
        );

        // when goal list changes in ModelView, we update it
        activityModel.getGoalsForDate().observe(goals -> {
            if (goals == null) return;
            adapter.clear();
            adapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentTomorrowBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.cardList.setAdapter(adapter);


        return view.getRoot();
    }
}
