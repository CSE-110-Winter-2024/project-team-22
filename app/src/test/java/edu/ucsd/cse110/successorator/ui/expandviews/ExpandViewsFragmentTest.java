package edu.ucsd.cse110.successorator.ui.expandviews;

import static org.junit.Assert.assertEquals;

import android.os.Bundle;

import junit.framework.TestCase;

public class ExpandViewsFragmentTest extends TestCase {

    public void testNewInstance() {

        ExpandViewsFragment actual = ExpandViewsFragment.newInstance();
        Bundle actualArgs = actual.getArguments();
        assertNotNull(actualArgs);

    }
}