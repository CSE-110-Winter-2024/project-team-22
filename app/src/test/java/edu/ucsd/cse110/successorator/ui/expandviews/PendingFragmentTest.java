package edu.ucsd.cse110.successorator.ui.expandviews;

import android.os.Bundle;

import junit.framework.TestCase;

public class PendingFragmentTest extends TestCase {

    public void testNewInstance() {
        PendingFragment actual = PendingFragment.newInstance();
        Bundle actualArgs = actual.getArguments();
        assertNotNull(actualArgs);
    }
}