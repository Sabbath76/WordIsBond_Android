package com.tomapp.wordisbond;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

public class ImageManager 
{ 
//	  private HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>(); 
	  private File cacheDir;
	  private ImageQueue imageQueue = new ImageQueue();
	  private Thread imageLoaderThread = new Thread(new ImageQueueManager());
	  private LruCache<String, Bitmap> mImageCache;
//	  private DiskLruCache mDiskCache;
//	  private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	  
	  private class ImageRef 
	  {
		  public String url;
		  public ImageView imageView;
		  public int index;
		    
		  public ImageRef(String u, ImageView i, int idx) 
		  {
		    url=u;
		    imageView=i;
		    index = idx;
		  }
	  }
	  
	  private class ImageQueue 
	  {
		  private Stack<ImageRef> imageRefs = new Stack<ImageRef>();

		  //removes all instances of this ImageView
		  public void Clean(ImageView view) 
		  {
		    for(int i = 0 ;i < imageRefs.size();) 
		    {
		      if(imageRefs.get(i).imageView == view)
		        imageRefs.remove(i);
		      else 
		    	  ++i;
		    }
		  }
	  }
	  

	  public ImageManager(Context context) 
	  {
	    // Make background thread low priority, to avoid affecting UI performance
	    imageLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

	    // Find the dir to save cached images
	    String sdState = android.os.Environment.getExternalStorageState();
	    if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)) 
	    {
	      File sdDir = android.os.Environment.getExternalStorageDirectory();    
	      cacheDir = new File(sdDir,"data/codehenge");
	    }
	    else
	      cacheDir = context.getCacheDir();

	    if(!cacheDir.exists())
	      cacheDir.mkdirs();
	    
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		int memClass = activityManager.getMemoryClass();
		int cacheSize = 1024 * 1024 * memClass / 4;
		mImageCache = new LruCache<String, Bitmap>( cacheSize )
			{
		        @Override
		        protected int sizeOf(String key, Bitmap bitmap) 
		        {
		            // The cache size will be measured in bytes rather than number of items.
		            return bitmap.getByteCount();
		        }
			};
	  }
	  
/*	  static class DownloadedDrawable extends ColorDrawable 
	  {
	    private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

	    public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) 
	    {
	        super(Color.BLACK);
	        bitmapDownloaderTaskReference =
	            new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
	    }

	    public BitmapDownloaderTask getBitmapDownloaderTask() {
	        return bitmapDownloaderTaskReference.get();
	    }
	  }
*/	  
	  public Bitmap displayImage(String url, ImageView imageView, int defaultDrawableId, int itemIdx) 
	  {
		  if (url == null)
		  {
			  return null;
		  }
		  Bitmap cachedImage = getImage(url);
		  if(cachedImage != null) 
		  {
			  imageView.setImageBitmap(cachedImage);
			  return cachedImage;
		  } 
		  else 
		  {
			  queueImage(url, imageView, itemIdx);
			  imageView.setImageResource(defaultDrawableId);
			  return null;
		  }
	  }

	  public Bitmap getImage(String url)
	  {
		  return mImageCache.get(url);
	  }
	  
	  private void queueImage(String url, ImageView imageView, int index) 
	  {
		  // This ImageView might have been used for other images, so we clear 
		  // the queue of old tasks before starting.
		  imageQueue.Clean(imageView);
		  ImageRef p=new ImageRef(url, imageView, index);

		  synchronized(imageQueue.imageRefs) {
		    imageQueue.imageRefs.push(p);
		    imageQueue.imageRefs.notifyAll();
		  }

		  // Start thread if it's not started yet
		  if(imageLoaderThread.getState() == Thread.State.NEW)
		    imageLoaderThread.start();
		}
	  
	  private class ImageDisplayer implements Runnable 
	  {
		  int index;
		  Bitmap bitmap;
		  public ImageDisplayer(int idx, Bitmap bmap)
		  {
			  index = idx;
			  bitmap = bmap;
		  }
		  
		  public void run()
		  {
//			  ItemListActivity.main.mArrayAdapter.notifyImageLoaded(index, bitmap);
//			  ItemListActivity.main.mFeatureAdapter.notifyImageLoaded(index, bitmap);
		  }
	  }
	  
	  private class ImageQueueManager implements Runnable 
	  {
		  public void run()
		  {
		    try 
		    {
		      while(true) 
		      {
		        // Thread waits until there are images in the 
		        // queue to be retrieved
		        if(imageQueue.imageRefs.size() == 0) 
		        {
		        	synchronized(imageQueue.imageRefs) 
					{
		        		imageQueue.imageRefs.wait();
					}
		        }
		          
		        // When we have images to be loaded
		        int numImages = imageQueue.imageRefs.size();
		        if(numImages != 0) 
		        {
		          ImageRef imageToLoad;

		          synchronized(imageQueue.imageRefs) 
		          {
		            imageToLoad = imageQueue.imageRefs.pop();
		          }
		            
		          Bitmap bmp = getBitmap(imageToLoad.url);
		          
		          mImageCache.put(imageToLoad.url, bmp);
		          
		          if (numImages == 1)
		          {
//		        	  Activity a = (Activity)imageToLoad.imageView.getContext();

		        	  ImageDisplayer imageDisplayer = new ImageDisplayer(imageToLoad.index, bmp);
		        	  ItemListActivity.main.runOnUiThread(imageDisplayer);
		          }
		          
//		          imageMap.put(imageToLoad.url, bmp);
		          
		          // TODO: Display image in ListView on UI thread
		        }

		        if(Thread.interrupted())
			          break;
		      }
		    } 
		    catch (InterruptedException e) 
		    {
		    }
		  }
		  
		  private Bitmap getBitmap(String url) 
		  {
			  String filename = String.valueOf(url.hashCode());
			  File f = new File(cacheDir, filename);

			  // Is the bitmap in our cache?
			  Bitmap bitmap = BitmapFactory.decodeFile(f.getPath());
			  if(bitmap != null) return bitmap;

			  // Nope, have to download it
			  try 
			  {
			    bitmap = BitmapFactory.decodeStream(new URL(url).openConnection().getInputStream());
			    // save bitmap to cache for later
			    writeFile(bitmap, f);
			      
			    return bitmap;
			  } 
			  catch (Exception ex) 
			  {
			    ex.printStackTrace();
			    return null;
			  }
			}

			private void writeFile(Bitmap bmp, File f) 
			{
			  FileOutputStream out = null;

			  try 
			  {
			    out = new FileOutputStream(f);
			    bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
			  } 
			  catch (Exception e) 
			  {
			    e.printStackTrace();
			  }
			  finally 
			  { 
			    try 
			    { 
			    	if (out != null ) out.close(); 
			    }
			    catch(Exception ex) 
			    {
			    } 
			  }
			}
		}

	  
	}