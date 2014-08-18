package com.android.internal.policy.impl;

public interface KeyguardScreen {

    /**
     * Return true if your view needs input, so should allow the soft
     * keyboard to be displayed.
     */
    boolean needsInput();

    /**
     * This screen is no longer in front of the user.
     */
    void onPause();

    /**
     * This screen is going to be in front of the user.
     */
    void onResume();

    /**
     * This view is going away; a hook to do cleanup.
     */
    void cleanUp();
}