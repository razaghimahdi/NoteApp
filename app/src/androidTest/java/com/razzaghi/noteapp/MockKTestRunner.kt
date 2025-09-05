package com.razzaghi.noteapp

import android.os.Bundle
import androidx.test.runner.AndroidJUnitRunner

/**
 * A custom AndroidJUnitRunner that sets up the DexMaker cache directory for MockK.
 * This is required for MockK to work in instrumented tests on modern Android versions.
 */
class MockKTestRunner : AndroidJUnitRunner() {
    override fun onCreate(arguments: Bundle?) {
        // This line tells DexMaker to use the app's code cache directory for its files
        System.setProperty("dexmaker.dexcache", super.getTargetContext().codeCacheDir.path)
        super.onCreate(arguments)
    }
}