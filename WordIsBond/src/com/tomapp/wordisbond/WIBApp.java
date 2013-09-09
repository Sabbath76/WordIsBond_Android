package com.tomapp.wordisbond;

import android.app.Application;

/**
 * Created by Tom on 31/08/13.
 */
import org.acra.*;
import org.acra.annotation.*;

@ReportsCrashes(
        formKey = "",
        formUri = "",
        mailTo = "tomberry76@hotmail.com")
public class WIBApp extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        ACRA.init(this);
    }
}
