package edu.ucsd.cse110.successorator.ui.expandviews;

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
import edu.ucsd.cse110.successorator.databinding.FragmentPendingBinding;
import edu.ucsd.cse110.successorator.ui.goallist.GoalListAdapter;
import edu.ucsd.cse110.successorator.ui.goallist.dialog.MovePendingDialogFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingFragment extends Fragment {

    private MainViewModel activityModel;
    private FragmentPendingBinding view;
    private GoalListAdapter adapter;

    public PendingFragment() {
        // Required empty public constructor
    }

    public static PendingFragment newInstance() {
        PendingFragment fragment = new PendingFragment();
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
                goal -> {
                    if (goal.getDate().equals("Pending")) {
                        var movePendingDialog = MovePendingDialogFragment.newInstance(goal.id(), goal.text(), goal.isCompleted(), goal.sortOrder(), goal.getFrequency(), goal.getContext());
                        movePendingDialog.show(getParentFragmentManager(), "MovePendingDialogFragment");
                    }
                },
                goal -> {}
        );

        // when goal list changes in ModelView, we update it
        activityModel.getGoalsForPending().observe(goals -> {
            if (goals == null) return;
//            activityModel.updateDisplayedGoals();
            adapter.clear();
            adapter.addAll(new ArrayList<>(goals)); // remember the mutable copy here!
            adapter.notifyDataSetChanged();
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.view = FragmentPendingBinding.inflate(inflater, container, false);

        // Set the adapter on the ListView
        view.cardList.setAdapter(adapter);


        return view.getRoot();
    }
}