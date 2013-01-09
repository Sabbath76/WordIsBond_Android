package com.tomapp.wordisbond;

import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.*;

public class RSSEncodedHandler extends DefaultHandler 
{
	
	RSSFeed _feed;
	RSSItem _item;
	String _lastElementName = "";
	boolean bFoundChannel = false;
	final int RSS_TITLE = 1;
	final int RSS_LINK = 2;
	final int RSS_DESCRIPTION = 3;
	final int RSS_CATEGORY = 4;
	final int RSS_PUBDATE = 5;
	final int RSS_IMAGE = 6;
	final int RSS_ENCODED = 7;
	
	int depth = 0;
	int currentstate = 0;
	int pdepth = 0;
	/*
	 * Constructor 
	 */
	RSSEncodedHandler(RSSFeed feed, RSSItem item)
	{
		_feed = feed;
		_item = item;
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
		// initialize the RSSItem object - we will use this as a crutch to grab the info from the channel
		// because the channel and items have very similar entries..
		//_item = new RSSItem();

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
			return;
		}
		if (localName.equals("img"))
		{
			currentstate = RSS_IMAGE;
			
			String image = atts.getValue("src");
			Integer height = Integer.parseInt(atts.getValue("height"));
			Integer width = Integer.parseInt(atts.getValue("width"));

			if ((height > 2) && (width > 2))
			{
				if (_item.getImageURL() == null)
				{
					_item.setImage(image);
				}
			}
			
			return;
		}
		if (localName.equals("iframe"))
		{
			String webView = atts.getValue("src");
			_item.setWebview(webView);
//			String webView = "<iframe ";
//			for (Integer i=0; i<atts.getLength(); i++)
//			{
//				webView += atts.getLocalName(i) + "=" +atts.getValue(i) + " ";
//			}
//			webView += "> <iframe>";
//			_item.setWebview(webView);
		}
		if (localName.equals("title"))
		{
			currentstate = RSS_TITLE;
			return;
		}
		if (localName.equals("p"))
		{
			pdepth++;
			currentstate = RSS_DESCRIPTION;
			String newString = new String();
			newString = "<p>";
			if (_item.getDescription() == null)
				_item.setDescription(newString);
			else
				_item.setDescription(_item.getDescription()+newString);
			return;
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
		if (localName.equals("pubDate"))
		{
			currentstate = RSS_PUBDATE;
			return;
		}
		if (currentstate == RSS_DESCRIPTION)
		{
			String newString = new String();
			newString = "<"+localName+" ";
			int numAtts = atts.getLength();
			for (int i=0; i<numAtts; i++)
			{
				newString = newString + " "+atts.getQName(i)+"= \""+atts.getValue(i) +"\"";				
			}
			newString = newString+" >";
			_item.setDescription(_item.getDescription()+newString);	
		}
		else
		{
			// if we don't explicitly handle the element, make sure we don't wind up erroneously 
			// storing a newline or other bogus data into one of our existing elements
			currentstate = 0;
		}
	}
	
	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		depth--;
		
		if (currentstate == RSS_DESCRIPTION)
		{
				String newString = new String();
				newString = "</"+localName+">";
				if (_item.getDescription() == null)
					_item.setDescription(newString);
				else
					_item.setDescription(_item.getDescription()+newString);
//					_item.setDescription(_item.getDescription()+"<a href=\"http://www.google.com\">Google</a>"+newString);
	
			if (localName == "p")
			{
				pdepth--;
				if (pdepth <= 0)
				{
					currentstate = 0;
				}				
			}
		}
		else
		{
			currentstate = 0;
			if (localName == "div")
			{
				currentstate = RSS_DESCRIPTION;
			}
		}
	}
	 
	public void characters(char ch[], int start, int length)
	{
		String theString = new String(ch,start,length);
//		Log.i("RSSReader","characters[" + theString + "]");
		
		switch (currentstate)
		{
			case RSS_TITLE:
				if (theString != "\n")
				{
					_item.setTitle(theString);
					currentstate = 0;
				}
				break;
			case RSS_LINK:
				_item.setLink(theString);
				currentstate = 0;
				break;
			case RSS_IMAGE:
//				_item.setImage(theString);
				currentstate = 0;
				break;
			case RSS_DESCRIPTION:
				if (_item.getDescription() == null)
					_item.setDescription(theString);
				else
 					_item.setDescription(_item.getDescription()+"\n"+theString);
//				currentstate = 0;
				break;
			case RSS_CATEGORY:
				_item.setCategory(theString);
				currentstate = 0;
				break;
			case RSS_PUBDATE:
				_item.setPubDate(theString);
				currentstate = 0;
				break;
			default:
				return;
		}
		
	}
}
