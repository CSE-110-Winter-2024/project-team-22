package edu.ucsd.cse110.successorator.ui.expandviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainActivity;
import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.databinding.FragmentExpandMoreViewsBinding;

public class ExpandViewsFragment extends Fragment {
    private FragmentExpandMoreViewsBinding view;
    private MainViewModel activityModel;
    //private Date DisplayDate;

    ExpandViewsFragment(){
        // Required empty public constructor
    }

    public static ExpandViewsFragment newInstance() {
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

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = FragmentExpandMoreViewsBinding.inflate(inflater, container, false);
        setupMvp();
        return view.getRoot();
    }

    private void setupMvp() {
        // Observe View -> call Model
        view.todayViewLabel.setOnClickListener(v -> {
            switchToLabelFragment("Today");
            activityModel.toToday();
        });
        view.tomorrowViewLabel.setOnClickListener(v -> {
            switchToLabelFragment("Tomorrow");
            activityModel.toTomorrow();
        });
        view.pendingViewLabel.setOnClickListener(v -> {
            switchToLabelFragment("Pending");
            activityModel.toPending();
        });
        view.recurringViewLabel.setOnClickListener(v -> {
            switchToLabelFragment("Recurring");
            activityModel.toRecurring();
        });
    }

    private void switchToLabelFragment(String label) {
        MainActivity activity = (MainActivity) getActivity();
        activity.swapExpandFragment(label);
    }


}



