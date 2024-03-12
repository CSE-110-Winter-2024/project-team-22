package edu.ucsd.cse110.successorator.ui.focusview;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainViewModel;
import edu.ucsd.cse110.successorator.R;
import edu.ucsd.cse110.successorator.databinding.FragmentDialogChooseFocusBinding;
import edu.ucsd.cse110.successorator.ui.goallist.GoalListFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.*;

public class FocusModeFragment extends Fragment {
    private FragmentDialogChooseFocusBinding view;
    private MainViewModel activityModel;

    FocusModeFragment(){
        // Required empty public constructor
    }

    public static FocusModeFragment newInstance() {
        FocusModeFragment fragment = new FocusModeFragment();
        Bundle args = new Bundle();
        // args.putSerializable();
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
        this.view = FragmentDialogChooseFocusBinding.inflate(inflater, container, false);
        setupListeners();
        return view.getRoot();
    }

    private void setupListeners() {
        // Observe View -> call Model
        view.homeButton.setOnClickListener(v -> {
            activityModel.focusHome();
            switchToMainView();
        });
        view.workButton.setOnClickListener(v -> {
            activityModel.focusWork();
            switchToMainView();
        });
        view.schoolButton.setOnClickListener(v -> {
            activityModel.focusSchool();
            switchToMainView();
        });
        view.errandsButton.setOnClickListener(v -> {
            activityModel.focusErrands();
            switchToMainView();
        });
    }

    private void switchToMainView() {
        FragmentActivity activity = getActivity();
        if (activity != null) {
            FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, GoalListFragment.newInstance());
            transaction.addToBackStack(null);  // Optional
            transaction.commit();
        }
    }
}






