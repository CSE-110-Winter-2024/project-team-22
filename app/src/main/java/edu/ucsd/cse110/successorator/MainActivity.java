package edu.ucsd.cse110.successorator;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.ucsd.cse110.successorator.databinding.ActivityMainBinding;
//import edu.ucsd.cse110.successorator.ui.cardlist.CardListFragment;
import edu.ucsd.cse110.successorator.ui.cardlist.dialog.CreateCardDialogFragment;
//import edu.ucsd.cse110.succesorator.ui.study.StudyFragment;
//
//
//

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding view;
    private boolean isShowingStudy = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.app_name);

        var binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        var itemId = item.getItemId();

        if (itemId == R.id.action_bar_menu_swap_views) {
            // change to add button
            // TODO: make swap button do somnething
            var dialogFragment = CreateCardDialogFragment.newInstance();
            dialogFragment.show(getSupportFragmentManager(), "CreateDialogFragment");

//            swapFragments();
        }

        return super.onOptionsItemSelected(item);
    }

    private void swapFragments() {

    }

}
