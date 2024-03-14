package edu.ucsd.cse110.successorator.ui.expandviews;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentViewMenuBinding;
import edu.ucsd.cse110.successorator.ui.goallist.GoalListFragment;

public class ViewMenuFragment extends Fragment {
    private FragmentViewMenuBinding view;
    private MainViewModel activityModel;
    //private Date DisplayDate;

    ViewMenuFragment(){
        // Required empty public constructor
    }

    public static ViewMenuFragment newInstance() {
        ViewMenuFragment fragment = new ViewMenuFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the Model
        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialize the View
        view = FragmentViewMenuBinding.inflate(inflater, container, false);

        setupMvp();

        return view.getRoot();
    }

    private void setupMvp() {
        // Observe View -> call Model
        view.todayViewLabel.setOnClickListener(v -> toToday());
        view.tomorrowViewLabel.setOnClickListener(v -> toTomorrow());
        view.pendingViewLabel.setOnClickListener(v -> toPending());
        view.recurringViewLabel.setOnClickListener(v -> toRecurring());
    }

    private void toToday() {
            activityModel.toToday();
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, GoalListFragment.newInstance())
                    .commit();
    }

    private void toTomorrow() {
        activityModel.toTomorrow();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, TomorrowFragment.newInstance())
                .commit();
    }

    private void toPending() {
        activityModel.toPending();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, PendingFragment.newInstance())
                .commit();
    }

    private void toRecurring() {
        activityModel.toRecurring();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, RecurringFragment.newInstance())
                .commit();
    }

}



