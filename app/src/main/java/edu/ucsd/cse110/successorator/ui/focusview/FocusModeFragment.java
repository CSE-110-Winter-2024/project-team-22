package edu.ucsd.cse110.successorator.ui.focusview;

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
import edu.ucsd.cse110.successorator.databinding.FragmentDialogChooseFocusBinding;

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
        view.focusCancel.setOnClickListener(v -> {
            activityModel.focusAll();
            switchToMainView();
        });
    }

    private void switchToMainView() {
        MainActivity activity = (MainActivity) getActivity();
        activity.swapFocusFragment();
    }
}






