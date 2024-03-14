package edu.ucsd.cse110.successorator.ui.expandviews;

import android.os.Bundle;

import junit.framework.TestCase;

public class RecurringFragmentTest extends TestCase {

    public void testNewInstance() {
        RecurringFragment actual = RecurringFragment.newInstance();
        Bundle actualArgs = actual.getArguments();
        assertNotNull(actualArgs);
    }
}