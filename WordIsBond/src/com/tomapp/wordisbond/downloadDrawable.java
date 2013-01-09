package com.tomapp.wordisbond;

import java.lang.ref.WeakReference;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

class downloadedDrawable extends ColorDrawable 
{
    private final WeakReference<bitmapDownloader> bitmapDownloaderTaskReference;

    public downloadedDrawable(bitmapDownloader bitmapDownloaderTask) 
    {
        super(ItemListActivity.main.mFeed.getColour());
        bitmapDownloaderTaskReference =
            new WeakReference<bitmapDownloader>(bitmapDownloaderTask);
    }

    public bitmapDownloader getBitmapDownloaderTask() {
        return bitmapDownloaderTaskReference.get();
    }
}