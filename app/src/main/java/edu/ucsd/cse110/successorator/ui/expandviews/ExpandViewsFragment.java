package edu.ucsd.cse110.successorator.ui.expandviews;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.time.LocalDate;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentExpandMoreViewsBinding;
import edu.ucsd.cse110.successorator.databinding.FragmentGoalListBinding;
import edu.ucsd.cse110.successorator.lib.domain.Goal;
import edu.ucsd.cse110.successorator.lib.domain.SuccessDate;
import edu.ucsd.cse110.successorator.ui.goallist.GoalListAdapter;

public class ExpandViewsFragment extends Fragment {
    private FragmentExpandMoreViewsBinding view;
    private MainViewModel activityModel;


    ExpandViewsFragment(){
        // Required empty public constructor
    }


    public static ExpandViewsFragment newInstance(){
        ExpandViewsFragment fragment = new ExpandViewsFragment();
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

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = FragmentExpandMoreViewsBinding.inflate(inflater, container, false);
        setupClickListeners();
        return view.getRoot();
    }


    private void setupClickListeners() {
        view.todayViewLabel.setOnClickListener(v -> swapFragment(new TodayViewFragment()));
        view.tomorrowViewLabel.setOnClickListener(v -> swapFragment(new TomorrowViewFragment()));
        view.pendingViewLabel.setOnClickListener(v -> swapFragment(new PendingViewFragment()));
        view.recurringViewLabel.setOnClickListener(v -> swapFragment(new RecurringViewFragment()));
    }

    private void swapFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

}



