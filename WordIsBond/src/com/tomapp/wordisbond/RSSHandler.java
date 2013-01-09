package com.tomapp.wordisbond;

import java.io.StringReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

import com.tomapp.wordisbond.RSSFeed.EFeedType;


public class RSSHandler extends DefaultHandler 
{
	
	RSSFeed _feed;
	RSSItem _item;
	String _lastElementName = "";
	String _encodedBuffer = "";
	boolean bFoundChannel = false;
	final int RSS_TITLE = 1;
	final int RSS_LINK = 2;
	final int RSS_DESCRIPTION = 3;
	final int RSS_CATEGORY = 4;
	final int RSS_PUBDATE = 5;
	final int RSS_IMAGE = 6;
	final int RSS_ENCODED = 7;
	
	int mExpectedFeatures = 0;
	
	int depth = 0;
	int currentstate = 0;
	int numItems = 0;
	int numFeatures = 0;
	Boolean inItem = false;
	
	Boolean mDoneFeatures = false;
	EFeedType mFeedType = EFeedType.FEED_TABLET;
	
	String mInitialPubDate 	= new String();
	String mLatestTitle 		= new String();
	String mLatestFeatureTitle 	= new String();
	
	/*
	 * Constructor 
	 */
	RSSHandler(int featureCount, EFeedType feedType)
	{
		mExpectedFeatures = featureCount;
		mFeedType = feedType;
	}
	
	/*
	 * getFeed - this returns our feed when all of the parsing is complete
	 */
	RSSFeed getFeed()
	{
		return _feed;
	}
	
	
	public void startDocument() throws SAXException
	{
		// initialize our RSSFeed object - this will hold our parsed contents
		if (_feed == null)
		{
			_feed = new RSSFeed();
			_feed.SetFeedType(mFeedType);
		}
		else
		{
			int oldItemCount = _feed.getItemCount();
			if (oldItemCount > 0)
			{
				mInitialPubDate = _feed.getPubDate();
				mLatestTitle 	= _feed.getItem(0).getTitle();
			}

			if (_feed.getFeatureCount() > 0)
			{
				mLatestFeatureTitle = _feed.getFeature(0).getTitle();
			}
		}
		numItems = 0;
		numFeatures = 0;
		mDoneFeatures = false;
		if (_item == null)
		{
			// initialize the RSSItem object - we will use this as a crutch to grab the info from the channel
			// because the channel and items have very similar entries..
			_item = new RSSItem();
		}
		
	}
	private boolean isParsingFeature()
	{
		return numFeatures < mExpectedFeatures;
	}
	public void endDocument() throws SAXException
	{
	}
	public void startElement(String namespaceURI, String localName,String qName, Attributes atts) throws SAXException
	{
		depth++;
		if (localName.equals("channel"))
		{
			currentstate = 0;
			return;
		}
		if (localName.equals("encoded"))
		{
			currentstate = RSS_ENCODED;
			_encodedBuffer = "";
			return;
		}
		if (localName.equals("image"))
		{
			// record our feed data - we temporarily stored it in the item :)
			_feed.setTitle(_item.getTitle());
			_feed.setPubDate(_item.getPubDate());
			currentstate = RSS_IMAGE;
			return;
		}
		if (localName.equals("item"))
		{
			// create a new item
			_item = new RSSItem();
			inItem = true;
			return;
		}
		if (localName.equals("title"))
		{
			currentstate = RSS_TITLE;
			return;
		}
		if (localName.equals("description"))
		{
//			if (inItem)
//			{
//				currentstate = RSS_ENCODED;
//				_encodedBuffer = "";
//				return;
//			}
//			else
			{
				if (inItem)
				{
					_item.setFullDescription("");
				}
				
				currentstate = RSS_DESCRIPTION;
				return;
			}
		}
		if (localName.equals("link"))
		{
			currentstate = RSS_LINK;
			return;
		}
		if (localName.equals("category"))
		{
			currentstate = RSS_CATEGORY;
			return;
		}
		if (localName.equals("pubDate") || localName.equals("lastBuildDate"))
		{
			currentstate = RSS_PUBDATE;
			return;
		}
		// if we don't explicitly handle the element, make sure we don't wind up erroneously 
		// storing a newline or other bogus data into one of our existing elements
		currentstate = 0;
	}
	
	public void parseEncodedData(String encodedData)
	{
		try
		{
           // create the factory
           SAXParserFactory factory = SAXParserFactory.newInstance();
           // create a parser
           SAXParser parser = factory.newSAXParser();

           // create the reader (scanner)
           XMLReader xmlreader = parser.getXMLReader();
           // instantiate our handler
           RSSEncodedHandler theRssHandler = new RSSEncodedHandler(_feed, _item);
           // assign our handler
           xmlreader.setContentHandler(theRssHandler);
           
           /* Parse the xml-data from our URL. */
           InputSource inputSource = new InputSource();
           inputSource.setEncoding("UTF-8");
           inputSource.setCharacterStream(new StringReader("<root>"+encodedData+"</root>"));

           String encodedDataItem = new String(encodedData);
           _item.setEncodedData(encodedDataItem);
           
           // perform the synchronous parse           
           xmlreader.parse(inputSource);
		}
		catch (Exception ee)
		{
		}		
	}
	
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		depth--;
		if (localName.equals("item"))
		{
			if (_item.getEncodedData() == null)
			{
				parseEncodedData(_item.getFullDescription());
			}
			if (isParsingFeature())
			{
				if (!mDoneFeatures)
				{
					_feed.addFeature(_item, numFeatures);
				}
				numFeatures++;
			}
			else
			{
				// add our item to the list!
				_feed.addItem(_item, numItems);
				numItems++;
			}
			inItem = false;
			return;
		}
//		if (((currentstate == RSS_DESCRIPTION)) && (inItem))
//		{
//			_encodedBuffer = _item.getFullDescription();
		if (currentstate == RSS_ENCODED)
		{
			parseEncodedData(_encodedBuffer);
			
			currentstate = 0;
		}
		else if (currentstate == RSS_DESCRIPTION)
		{
			currentstate = 0;
		}
	}
	 
	public void characters(char ch[], int start, int length) throws SAXException
	{
		String theString = new String(ch,start,length);
//		Log.i("RSSReader","characters[" + theString + "]");
		
		switch (currentstate)
		{
			case RSS_TITLE:
				if (theString != "\n")
				{
					if (isParsingFeature())
					{
						if (mLatestFeatureTitle.compareTo(theString) == 0)
						{
							mDoneFeatures = true;
						}
					}
					else
					{
						if (mLatestTitle.compareTo(theString) == 0)
						{
							//--- Exit parser
							throw new BreakParsingException();
						}
					}

					_item.setTitle(theString);
					currentstate = 0;
				}
				break;
			case RSS_LINK:
				_item.setLink(theString);
				currentstate = 0;
				break;
			case RSS_IMAGE:
				_item.setImage(theString);
				currentstate = 0;
				break;
			case RSS_DESCRIPTION:
				if (inItem)
				{
					_item.setFullDescription(_item.getFullDescription()+theString);					
				}
				else
				{
					_item.setDescription(theString);
					currentstate = 0;
				}
/*				else if (theString.charAt(0) == '<')
				{
					try
					{
			           // create the factory
			           SAXParserFactory factory = SAXParserFactory.newInstance();
			           // create a parser
			           SAXParser parser = factory.newSAXParser();

			           // create the reader (scanner)
			           XMLReader xmlreader = parser.getXMLReader();
			           // instantiate our handler
			           RSSEncodedHandler theRssHandler = new RSSEncodedHandler(_feed, _item);
			           // assign our handler
			           xmlreader.setContentHandler(theRssHandler);
			           
			           // Parse the xml-data from our URL. 
			           InputSource inputSource = new InputSource();
			           inputSource.setEncoding("UTF-8");
			           inputSource.setCharacterStream(new StringReader("<root>"+theString+"</root>"));
			           
			           // perform the synchronous parse           
			           xmlreader.parse(inputSource);

					}
					catch (Exception ee)
					{
					}
				}*/
				break;
			case RSS_CATEGORY:
				int catagoryID = _feed.GetCatagoryId(theString);
				
				_item.SetCatagory(catagoryID);
//				if (theString.contains("WIB") || theString.contains("hip hop"))
//				{
//					_item.setIsFeature(true);
//				}
//				_item.setCategory(theString);
				currentstate = 0;
				break;
			case RSS_PUBDATE:
				if (!inItem && (theString.compareTo(mInitialPubDate) == 0))
				{
					//--- Exit parser
					throw new BreakParsingException();
				}
				_item.setPubDate(theString);
				currentstate = 0;
				break;
			case RSS_ENCODED:
				_encodedBuffer += theString;
				break;
			default:
				return;
		}
		
	}
}
