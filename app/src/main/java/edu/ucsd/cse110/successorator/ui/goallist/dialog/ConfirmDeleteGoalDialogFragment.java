/*
package edu.ucsd.cse110.successorator.ui.cardlist.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.successorator.MainViewModel;

public class ConfirmDeleteGoalDialogFragment extends DialogFragment {
    private static final String ARG_FLASHCARD_ID = "flashcard_id";
    private int goalID;

    private MainViewModel activityModel;

    ConfirmDeleteGoalDialogFragment(){
        // Required empty public constructor
    }

    // make sure you return the right type;
    public static ConfirmDeleteGoalDialogFragment newInstance(int flashcardID){
        var fragment = new ConfirmDeleteGoalDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_FLASHCARD_ID, flashcardID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Retrieve the ID from the arguments (crash if no args)
        this.goalID = requireArguments().getInt(ARG_FLASHCARD_ID);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner,modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog (@Nullable Bundle savedInstanceState){
        return new AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Delete",this::onPositiveButtonClick)
                .setNegativeButton("Cancel",this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick (DialogInterface dialog, int which){
        activityModel.remove(goalID);
        dialog.dismiss();
    }

    private void onNegativeButtonClick (DialogInterface dialog, int which){
        dialog.cancel();
    }
}
*/
