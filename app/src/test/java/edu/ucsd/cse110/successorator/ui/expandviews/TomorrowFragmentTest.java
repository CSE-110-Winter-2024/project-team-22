package edu.ucsd.cse110.successorator.ui.expandviews;

import android.os.Bundle;

import junit.framework.TestCase;

public class TomorrowFragmentTest extends TestCase {
    
    
    public void testNewInstance() {

        TomorrowFragment actual = TomorrowFragment.newInstance();
        Bundle actualArgs = actual.getArguments();
        assertNotNull(actualArgs);

    }



}