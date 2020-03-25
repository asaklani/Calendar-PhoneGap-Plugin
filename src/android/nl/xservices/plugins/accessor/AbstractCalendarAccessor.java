
package nl.xservices.plugins.accessor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Attendees;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Instances;
import android.text.TextUtils;
import android.util.Log;
 

@SuppressLint("NewApi")
public  class AbstractCalendarAccessor {

  public static final String LOG_TAG = "Calendar";
  public static final String CONTENT_PROVIDER = "content://com.android.calendar";
  public static final String CONTENT_PROVIDER_PRE_FROYO = "content://calendar";

  public static final String CONTENT_PROVIDER_PATH_CALENDARS = "/calendars";
  public static final String CONTENT_PROVIDER_PATH_EVENTS = "/events";
  public static final String CONTENT_PROVIDER_PATH_REMINDERS = "/reminders";
  public static final String CONTENT_PROVIDER_PATH_INSTANCES_WHEN = "/instances/when";
  public static final String CONTENT_PROVIDER_PATH_ATTENDEES = "/attendees";

  /*Class Name: Event
   * Method Name: toJSONObject
   * returun Type: JSONObject
   * Purpose: This method is an DTO method for formating the final result JSONObject(All the Events with Attendees) in the prescribed format.  
   */
  protected static class Event {
    String id;
    String message;
    String location;
    String title;
    String startDate;
    String endDate;
    String ownerAccount;
    String organizer;
    String eventId;
    boolean recurring = false;
    boolean allDay;
    ArrayList<Attendee> attendees;

   /*
    * Method Name: toJSONObject
    * returun Type: JSONObject
    * Purpose: This method is an DTO method for formating the final result JSONObject(All the Events with Attendees) in the prescribed format.  
    */
    
    public JSONObject toJSONObject() {
      JSONObject obj = new JSONObject();
      try {
        obj.put("id", this.id);
        obj.putOpt("message", this.message);
        obj.putOpt("location", this.location);
        obj.putOpt("title", this.title);
        obj.putOpt("ownerAccount", this.ownerAccount);
        obj.putOpt("organizer", this.organizer);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        if (this.startDate != null) {
          obj.put("startDate", sdf.format(new Date(Long.parseLong(this.startDate))));
        }
        if (this.endDate != null) {
          obj.put("endDate", sdf.format(new Date(Long.parseLong(this.endDate))));
        }
        obj.put("allday", this.allDay);
        if (this.attendees != null) {
          JSONArray arr = new JSONArray();
          for (Attendee attendee : this.attendees) {
            arr.put(attendee.toJSONObject());
          }
          obj.put("attendees", arr);
        }
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
      return obj;
    }
  }

  /*Class Nae: Attendee
   * Method Name: toJSONObject
   * returun Type: JSONObject
   * Purpose: This Class and method is an DTO Class/method for formating the Attendees List of the meeting in the prescribed format.  
   */
  protected static class Attendee {
    String id;
    String name;
    String email;
    String status;

    public JSONObject toJSONObject() {
      JSONObject obj = new JSONObject();
      try {
        obj.put("id", this.id);
        obj.putOpt("name", this.name);
        obj.putOpt("email", this.email);
        obj.putOpt("status", this.status);
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
      return obj;
    }
  }

  


  Context context=null;
  public AbstractCalendarAccessor(Context cordova) {
    this.context = cordova;
   
  }

  
  protected enum KeyIndex {
    CALENDARS_ID,
    CALENDARS_VISIBLE,
    EVENTS_ID,
    EVENTS_CALENDAR_ID,
    EVENTS_DESCRIPTION,
    EVENTS_LOCATION,
    EVENTS_SUMMARY,
    EVENTS_START,
    EVENTS_END,
    EVENTS_RRULE,
    EVENTS_ALL_DAY,
    INSTANCES_ID,
    INSTANCES_EVENT_ID,
    INSTANCES_BEGIN,
    INSTANCES_END,
    ATTENDEES_ID,
    ATTENDEES_EVENT_ID,
    ATTENDEES_NAME,
    ATTENDEES_EMAIL,
    ATTENDEES_STATUS
  }

 
  

  
  public String getUsername1() {
	    AccountManager manager = AccountManager.get(context); 
	    Account[] accounts = manager.getAccountsByType("com.android.exchange"); 
	    List<String> possibleEmails = new LinkedList<String>();

	    for (Account account : accounts) {
	      // TODO: Check possibleEmail against an email regex or treat
	      // account.name as an email address only for certain account.type values.
	    	Log.i("Email Account Name",account.name);
	      possibleEmails.add(account.name);
	    }

	    if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
	        String email = possibleEmails.get(0);
	        String[] parts = email.split("@");

	        if (parts.length > 1)
	            return parts[0];
	    }
	    return null;
	}
 
  /*
   * Method Name: getUsername
   * returun Type: boolean
   * Purpose: This method is used to check whether the Android device is confiruged with the sapient Exchange Email Account.  
   */
  
  public String[] getUsername() {

	//For all registered accounts;
		/*try {
			Account[] accounts = AccountManager.get(this).getAccounts();
			for (Account account : accounts) {
				
				Item item = new Item( account.type, account.name);
				accountsList.add(item);
			}
		} catch (Exception e) {
			Log.i("Exception", "Exception:" + e);
		}*/
		String[] resultObj = new String[2];
		String accountExists = "false";
	  boolean accExists= false;
      /*AccountManager manager = AccountManager.get(context);
      Account[] accounts = manager.getAccountsByType("com.android.exchange");*/
	  Account[] accounts = AccountManager.get(context).getAccounts();
       
      for (Account account : accounts) {
    	  Log.d("Account Name",account.name+"");
    	  Log.d("Account Type",account.type+"");
          String accountName = account.name;
    	  String accountType =account.type;
        // Log.d("TestAccount account.name",account.name.contains("@sapient.")+"");
         //Log.d("TestAccount accountName",accountName.contains("@sapient.")+"");
//         if(account.name.contains("@sapient.")){
         if(accountType.contains("com.google.android.gm.exchange")){
        	 accExists =  true;
        	 accountExists = "true";
        	 String exchangeAccName = account.name;
        	 resultObj[0] = accountExists;
        	 resultObj[1] = exchangeAccName;
        	 
        	 break;
         }else{
        	 accExists= false;
        	 accountExists = "false";
        	 String exchangeAccName = "";
        	 resultObj[0] = accountExists;
        	 resultObj[1] = exchangeAccName;
        	 
         }
      }
      return resultObj;
  }
  
  /*
   * Method Name: fetchEventInstances
   * returun Type: ArrayList<Event>
   * Purpose: This method is used 
   * 1)To fetch all the events from the device calendar for the given date range.
   * 
   */
  @SuppressLint("NewApi")
private ArrayList<Event> fetchEventInstances(String title, String location, long startFrom, long startTo) {
    //-----------------------------------------------------------------------------------
    Uri.Builder builder=null;
    Uri attendeesUri = null;
    if (Build.VERSION.SDK_INT >= 8) {
    	builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
    	 attendeesUri = Uri.parse("content://com.android.calendar/attendees");
    } else {
    	builder = Uri.parse("content://calendar/instances/when").buildUpon();
    	  attendeesUri = Uri.parse("content://calendar/attendees");
    }
    
    long now = new Date().getTime();
    Log.d("contentUri", "ContentUris Start"+startFrom);
    ContentUris.appendId(builder, startFrom);
    ContentUris.appendId(builder,startTo);
    Log.d("contentUri", "ContentUris End"+startTo);
    Log.i("kar","event Count====="+now);
    Cursor eventCursor =  context.getContentResolver().query(builder.build(),
            new String[]  { Events.TITLE, "begin", "end", "allDay", "event_id","description","ownerAccount", Events.CALENDAR_ID}, null,
            null, "event_id ASC");//Creating the Cursor Object and preparing/Creating the Query for getting the Calendar Events.
 
    Log.i("event Count","=====>"+eventCursor.getCount());
    Log.e("123","eventCursor count=====>"+eventCursor.getCount());
    Event[] instances = null;
    ArrayList<Event> eventsList = new ArrayList<AbstractCalendarAccessor.Event>();
    Event event;
    if(eventCursor.getCount()>0)//Checking whether the Cursor consists fo the Events from the device calendar for the given date range.
    {

        if(eventCursor.moveToFirst())
        {
             int i = 0;
//        looping the cursor to get the Events one by one.
            do
            {
                 String titleo = eventCursor.getString(0);
                 Date begin = new Date(eventCursor.getLong(1));
                 Date end = new Date(eventCursor.getLong(2));
                 Boolean allDay = !eventCursor.getString(3).equals("0");
                 int eventId = eventCursor.getInt(4);
                 String calendarID = eventCursor.getString(7);
                 
                 String msgeo = eventCursor.getString(5);
                 String ownerAcc = eventCursor.getString(6);
                 
                 if(ownerAcc.contains("@")){//Condition to check whether the events is of Sapient events.
                	 
                	 
                	 Log.e("123", "Event Id----->>>>>"+eventId+"---------calendarId----->>>"+calendarID);

                             Log.e("123","Title:"+titleo);
                             Log.e("123","Begin:"+begin);
                             Log.e("123","End:"+end);
                             Log.e("123","All Day:"+allDay);
                             
                             event = new Event();
                             event.id = calendarID;
                             event.eventId = eventId+"";
                             event.startDate = eventCursor.getString(1);
                             event.endDate = eventCursor.getString(2);
                             event.message = msgeo;
                             event.ownerAccount =ownerAcc;
                             eventsList.add(event);
                             i += 1;
                             
                          // Attendees Code
                             Cursor eventAttendeesCoursor = context.getContentResolver().query(attendeesUri, new String []{ "attendeeName", "event_id"}, "event_id" +" = " + (eventId), null, null);
                             Log.e("123", "Count of no of attendees-----"+eventAttendeesCoursor.getCount());
                             if(eventAttendeesCoursor.getCount()>0)
                             {

                                 if(eventAttendeesCoursor.moveToFirst())
                                 {
                                     do {
                                         Log.e("123", "Attendees Name---->>>"+ eventAttendeesCoursor.getString(0));
                                         Log.e("123", "Attendees Event ID---->>>"+ eventAttendeesCoursor.getString(1));
                                     } while(eventAttendeesCoursor.moveToNext());
                                 }
                             }
                             eventAttendeesCoursor.close();//Cursor should be closed after use.
                 }

            }
            while(eventCursor.moveToNext());
            
        }
        eventCursor.close();//Event curosr is closed finnaly after looping is done.
    }else{
    	Log.e("Count 0 Else","eventCursor count=====0 Else");
    }
            //--------------------------------------------------------
    return eventsList;//Returns the all Sapient events for the given date range.
  }

  
  /*
   * Method Name: getActiveCalendarIds
   * returun Type: ArrayList<String>
   * Purpose: This method is used 
   * 1)To get the list of all the Active calendar IDs prsent/configured on the device..
   * 
   */
  private ArrayList<String> getActiveCalendarIds() {
    // Get only active calendars.
    Cursor cursor = queryCalendars(new String[]{Calendars._ID},Calendars.VISIBLE + "=1", null, null);
    ArrayList<String> calIds = new ArrayList<String>();
    if (cursor.moveToFirst()) {
      do {
        int col = cursor.getColumnIndex(Calendars._ID);
        calIds.add(cursor.getString(col));
      } while (cursor.moveToNext());
    }
    return calIds;
  }
   
  
  
  @SuppressLint("NewApi")
private String[] getCalendarAccType(){
	   String[] EVENT_PROJECTION = new String[] {
		    "_id",                           // 0
		    "accound_name",                  // 1
		    "calendar_displayname",         // 2
		    "ownerAccount"                  // 3
		};
	 
		  
	  Cursor cursor = null;
	  ContentResolver cr = context.getContentResolver();
	  Uri uri =null;   
	  
	   if (Build.VERSION.SDK_INT >= 8) {
		  
		   uri = Uri.parse("content://com.android.calendars");
	    } else {
	     
	    	uri = Uri.parse("content://calendars");
	    }
		  String selection = "((" + "account_name" + " = ?) AND (" 
		                          + "account_type" + " = ?))";
	  String[] selectionArgs = new String[] {"nobody@localhost.com", "com.sapient"}; 
	  // Submit the query and get a Cursor object back. 
	  cursor = cr.query(uri, selectionArgs, selection, null, null);
	  
	  String[] calendartpe = null;
	    if (cursor.moveToFirst()) {
	    	calendartpe = new String[cursor.getCount()];
	      int i = 0;
	      do {
	    
	        calendartpe[i] = cursor.getString(1);
	        
	        Log.i("cal id",cursor.getString(0));
	        Log.i("accound_name",cursor.getString(1));
	        Log.i("calendar_displayname",cursor.getString(2));
	        Log.i("ownerAccount",cursor.getString(3));
	        
	        i += 1;
	      } while (cursor.moveToNext());
	    }
	    return calendartpe;
	  
  }
  
  
  /*
   * Method Name: fetchEventsAsMap
   * returun Type: ArrayList<String>
   * Input param:Array List of Events  which is we got from "fetchEventInstances()" method. 
   * Purpose: This method is used 
   * 1)To get the Outer Event details like Event Id, Event Title, start date, end date, event Recurring status, AllDay event status, Owner Account(Sapient Account configured on device) and Organizer of the event.
   * 
   */
  private Map<String, Event> fetchEventsAsMap(ArrayList<Event> eventsList) {
	  Log.i("instances Obj",""+eventsList);
	  
    // Only selecting from active calendars, no active calendars = no events.
    ArrayList<String> activeCalendarIds = getActiveCalendarIds();//get the List of Active Calendar Ids.
    Log.i("Before Calling activeCalendarIds.length","");
    if (activeCalendarIds.size() == 0) {//checking whether CalendarId list id empty or not
      return null;
    }
    Log.i("After Calling activeCalendarIds.length", "");
    String[] projection = new String[]{//Creating a String Array with the Event details, that is used for Querying.
    		 Events._ID,
        
    		 Events.EVENT_LOCATION,
    		 Events.TITLE,
    		 Events.DTSTART,
    		 Events.DTEND,
    		 Events.RRULE,
    		 Events.ALL_DAY,
    		 "description",
    		 "ownerAccount",
    		 Events.ORGANIZER
    };
    // Get all the ids at once from active calendars.
//    Creating a SQL Query for searching in the eventList --Starts--
    StringBuffer select = new StringBuffer();
    select.append( Events._ID + " IN (");
//    Looping the EventList object for getting the EventIds with comma seperated.Example(106,112,158)
    for(Event event:eventsList){
    	Log.i("instances EventId ",""+event.eventId);
        select.append(event.eventId);
        if(eventsList.lastIndexOf(event)!=eventsList.size()-1)//checking the length of EventList and if it is last index then not to append comma.
        select.append(",");
    }
 
    select.append(") AND " + Events.CALENDAR_ID +
        " IN (");
    
//  Looping the CalendarIds List object for getting the CalendarIds with comma seperated.Example(11888787,5445112,1444458)   
    for (String activeCalendarId:activeCalendarIds){
    	Log.i("instances EventId ",""+activeCalendarId);
         select.append(activeCalendarId);
         if(activeCalendarIds.lastIndexOf(activeCalendarId)!=activeCalendarIds.size()-1)//checking the length of EventList and if it is last index then not to append comma.
         select.append(",");
    }
 
    select.append(")");
//  Creating a SQL Query for searching in the eventList --Ends--  
    Log.i("select =", select.toString());
    Cursor cursor = queryEvents(projection, select.toString(), null, null);//Queryng the Cursor object with the SQL Query prepared
    Map<String, Event> eventsMap = new HashMap<String, Event>();
    if (cursor.moveToFirst()) {
      int[] cols = new int[projection.length];
      for (int i = 0; i < cols.length; i++) {
        cols[i] = cursor.getColumnIndex(projection[i]);
      }
      do {//Looping to get the Events(By Querying with EventIds and CalendarIds) which is returned by SLQ Querying, 
        Event event = new Event();
        event.id = cursor.getString(cols[0]);
        event.location = cursor.getString(cols[1]);
        event.title = cursor.getString(cols[2]);
        event.startDate = cursor.getString(cols[3]);
        event.endDate = cursor.getString(cols[4]);
        event.recurring = !TextUtils.isEmpty(cursor.getString(cols[5]));
        event.allDay = cursor.getInt(cols[6]) != 0;
        event.message = cursor.getString(cols[7]);
        event.ownerAccount = cursor.getString(cols[8]);
        event.organizer = cursor.getString(cols[9]);
        
        Log.e("EventId ", "Id "+cursor.getString(cols[0]));
        Log.e("EventLocation ", "location "+cursor.getString(cols[1]));
        Log.e("EventTitle ", "Title "+cursor.getString(cols[2]));
        Log.e("EventStartDate ", "StartDate "+cursor.getString(cols[3]));
        Log.e("EventEndDate ", "EndDate "+cursor.getString(cols[4]));
        Log.e("EventRecurringn ", "Recurring "+cursor.getString(cols[5]));
        Log.e("EventAllDay ", "AllDay "+cursor.getString(cols[6]));
        Log.e("EventMessage ", "Message "+cursor.getString(cols[7]));
        Log.e("EventOwnerAccount ", "OwnerAccount "+cursor.getString(cols[8]));
        Log.e("EventOrganiser ", "Organiser "+cursor.getString(cols[9]));
        
        eventsMap.put(event.id, event);
      } while (cursor.moveToNext());
    }
    cursor.close();
    return eventsMap;//Returns EventsList (Events with outer event details(without attendees))which map the Active CalendarIds condition. 
  }

  /*
   * Method Name: fetchAttendeesForEventsAsMap
   * returun Type: Map<String, ArrayList<Attendee>>
   * Input param:String Array of EventIds  which is we got from "fetchEventsAsMap()" method. 
   * Purpose: This method is used 
   * 1)To get the Attendees(Attendees List) of the events.
   * 
   */
  private Map<String, ArrayList<Attendee>> fetchAttendeesForEventsAsMap(String[] eventIds) {
    // At least one id.
    if (eventIds.length == 0) {
      return null;
    }
    String[] projection = new String[]{//Creating String Array which hold EventId and Attendees details like Attendee Id, Attendee Name,Attendee Email, Attendee Status
    		 Attendees.EVENT_ID,
    		 Attendees._ID,
    		 Attendees.ATTENDEE_NAME,
    		 Attendees.ATTENDEE_EMAIL,
    		 Attendees.ATTENDEE_STATUS
    };
    
//    creating a string buffer object for creating a SQL query 
    StringBuffer select = new StringBuffer();
    select.append(Attendees.EVENT_ID + " IN (");
    select.append(eventIds[0]);
//    Looping the eventIds List for getting the EventIds seperated with comma
    for (int i = 0; i < eventIds.length; i++) {
    	select.append(",");
   	  	select.append(eventIds[i]); 
   	  
      /*if(i == 0){
    	  
      }else{
    	  select.append(",");
    	  select.append(eventIds[i]); 
      }*/
      
    }
    select.append(")");
    Log.e("select ", "select Query "+select);
    // Group the events together for easy iteration.
    Cursor cursor = queryAttendees(projection, select.toString(), null,
    		 Attendees.EVENT_ID + " ASC");//Querying and getting the Attendees List based on EventId
    Map<String, ArrayList<Attendee>> attendeeMap =
        new HashMap<String, ArrayList<Attendee>>();
    if (cursor.moveToNext()) {
      int[] cols = new int[projection.length];
      for (int i = 0; i < cols.length; i++) {
    	  cols[i] = cursor.getColumnIndex(projection[i]);
    	  /*if(i.equals(0)){
    		  
    	  }else{
    		  cols[i] = cursor.getColumnIndex(projection[i]);
    	  }*/
    	 /* if(i!=0){
    		  cols[i] = cursor.getColumnIndex(projection[i]);
    	  }else{
    		  
    	  }*/
      }
      ArrayList<Attendee> array = null;
      String currentEventId = null;
      int doItr = 0;
      do {//Looping the Cursor for getting the Attendees and putting in an Map(Map<String, ArrayList<Attendee>>) set object. 
        String eventId = cursor.getString(cols[0]);
        Log.e("eventId ", "eventId "+cursor.getString(cols[0]));
        Log.e("doItr ", "doItr "+doItr);
        if (currentEventId == null || !currentEventId.equals(eventId)) {
          currentEventId = eventId;
          Log.e("currentEventId ", "currentEventId "+currentEventId);
          array = new ArrayList<Attendee>();
          Log.e("Inside ", "currentEventId Null");
          attendeeMap.put(currentEventId, array);
          doItr = 0;
        }
        //Log.e("doItr1 ", "doItr1 "+doItr);
        /*if(i==0)
        	continue;*/
        
              
        
        /*if(eventAttendeesCoursor.getCount()>0)
        {

            if(eventAttendeesCoursor.moveToFirst())
            {
                do {
                    Log.e("123", "Attendees Name---->>>"+ eventAttendeesCoursor.getString(0));
                    Log.e("123", "Attendees Event ID---->>>"+ eventAttendeesCoursor.getString(1));
                } while(eventAttendeesCoursor.moveToNext());
            }
        }
        eventAttendeesCoursor.close();//Cursor should be closed after use.
*/        
        
        
        Attendee attendee = new Attendee();
        attendee.id = cursor.getString(cols[1]);
        attendee.name = cursor.getString(cols[2]);
        attendee.email = cursor.getString(cols[3]);
        attendee.status = cursor.getString(cols[4]);
        
        /*if(attendee.status == null){
        	String stat = "0";
        	attendee.status = stat;
        }
        
        if(attendee.name == null || attendee.name.equals("")){
        	String atndName = "";
        	attendee.name = atndName;
        }
        
        if(attendee.id == null || attendee.id.equals("")){
        	String atndId = "";
        	attendee.id = atndId;
        }
        
        if(attendee.email == null || attendee.email.equals("")){
        	String atndEmail = "";
        	attendee.email = atndEmail;
        }*/
        Log.i("attendeeId ", "Id "+cursor.getString(cols[1]));
        Log.i("attendeeName ", "name "+cursor.getString(cols[2]));
        Log.i("attendeeEmail ", "email "+cursor.getString(cols[3]));
        Log.i("attendeeStatus ", "Status "+cursor.getString(cols[4]));
        
        if(doItr!=0){
        	Log.e("doItr2 ", "doItr2 "+doItr);
        	array.add(attendee);
        }else{
        	Log.e("doItr3 ", "doItr3 "+doItr);
        }
        doItr++;
      } while (cursor.moveToNext());
    }
    cursor.close();
    return attendeeMap;//Returns the Attendees List map object.
  }
  
  /*
   * Method Name: Todayevents
   * returun Type: JSONArray
   * Input param:Start Data and EndDate Strings based on which we have to retrieve the events. 
   * Purpose: This is the first/main method which
   * 1) Gets the inputs(startdate and endadate) from Calendar.java.
   * 2) Get all the Events in the device calendar based on the date range and which is of Sapient Events.
   * 3) Executing  the "fetchEventInstances()" method which fetches all the events based on input/given date range and Sapient Events.
   * 4) Executing the "fetchEventsAsMap()" method which will get and format (Events object )the outer details of the events like EventId, title, start date end date, owner account, orgnizer etc,.
   * 5) Executing the "fetchAttendeesForEventsAsMap()" method which will get and format the Attendees List object.
   * 6) Looping the Events and Attendees object and creating the final result JSONArray Object(based on Organizer of the Event/Meeting)
   */
	public JSONArray Todayevents(String eventTitle, String loce,String sartdate, String enddate, String empEmail, String userNtid, String exchangeAccEmail) {
		
		 JSONArray result = new JSONArray();
		Calendar calendar = Calendar.getInstance();//creating the Android Calendar instance
        Uri l_eventUri;
		if (Build.VERSION.SDK_INT >= 8) {//checking for the version of Android
          l_eventUri = Uri.parse("content://com.android.calendar/events");
      } else {
          l_eventUri = Uri.parse("content://calendar/events");
      }
      String dtstart = "dtstart";
      String dtend = "dtend";
    
// formatiing/Converting the start Date and end Date to required format.
      SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
         
      Date dateCC = null;
      
      Log.i("stdate", sartdate);
      Log.i("enddate", enddate);

	try {
		dateCC = formatter.parse(sartdate);
	} catch (ParseException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
      calendar.setTime(dateCC);

      long after = calendar.getTimeInMillis();

      SimpleDateFormat formatterr = new SimpleDateFormat("MM/dd/yy hh:mm:ss");

      Calendar endOfDay = Calendar.getInstance();
      Date dateCCC = null;
	try {
		dateCCC = formatterr.parse(enddate+" 23:59:59");
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
      endOfDay.setTime(dateCCC);
      
     Log.i("stdate", dateCC.toString());
     Log.i("enddate", dateCCC.toString());
		   
     
     
     		//getCalendarAccType();
     
		    ArrayList<Event> instances = fetchEventInstances(eventTitle, loce, after, endOfDay.getTimeInMillis());//method for getting all the events based on given condition
		    
		    if(!instances.isEmpty()){//Checking whether the above "fetchEventInstances()" is returning the empty List ot not.
		    	
			    Map<String, Event> eventMap = fetchEventsAsMap(instances);//Method for getting the Event details(Event List onject)
			    
			    Map<String, ArrayList<Attendee>> attendeeMap = fetchAttendeesForEventsAsMap(eventMap.keySet().toArray(new String[0]));//Method for getting/formatting the Attendees details (Attendees List).
	//		    Looping the Events List object and preparing the final result JSONArray object.
			    for (Event instance : instances) {
			        Event event = eventMap.get(instance.eventId);
			        if (event != null) {
			        	
			        	Log.e("instanceMessage ", "InstMsg "+instance.message);
			        	Log.e("instancelocation ", "InstLoc "+instance.location);
			        	Log.e("instancetitle ", "InstTitle "+instance.title);
			        	Log.e("instanceownerAccount ", "InstOwnAcc "+instance.ownerAccount);
			        	Log.e("instanceorganizer ", "InstOrgnr "+instance.organizer);
			        	Log.e("instancestartDate ", "InstSrtDt "+instance.startDate);
			        	Log.e("instanceendDate ", "InstEndDt "+instance.endDate);
			        	Log.e("instanceAllDay ", "InstAllDay "+instance.allDay);
			        	Log.e("instanceeventId ", "InstEventId "+instance.eventId);
			        	Log.e("empEmail ", "empEmail "+empEmail);
			        	String orgnaizerChk  = event.organizer;
			        	String ownerAccChk = instance.ownerAccount;
			        	//String ntid = "ksrini";
			        	String orgnaizerOfMeeting = orgnaizerChk.substring(0,orgnaizerChk.indexOf("@"));
			        	String ownerOfDevice = ownerAccChk.substring(0,ownerAccChk.indexOf("@"));
			        	
			        	
			        	
//			        	if(event.ownerAccount.equalsIgnoreCase(event.organizer) || empEmail.equalsIgnoreCase(event.organizer)){//Checking whether the meeting/Event Orgnaizer with the Owner Account and getting only the matching events(Meetings organised by Account Owner). 
			        	if(empEmail.equalsIgnoreCase(event.organizer)){//Checking whether the meeting/Event Orgnaizer with the Owner Account and getting only the matching events(Meetings organised by Account Owner).
					          instance.message = event.message;
					          instance.location = event.location;
					          instance.title = event.title;
					          instance.ownerAccount = event.ownerAccount;
					          instance.organizer = event.organizer;
					          
					          Log.e("eventMessage ", "evntMsg "+event.message);
					          Log.e("eventlocation ", "evntLoc "+event.location);
					          Log.e("eventtitle ", "evntTitle "+event.title);
					          Log.e("eventownerAccount ", "evntOwnAcc "+event.ownerAccount);
					          Log.e("eventorganizer ", "evntOrgnr "+event.organizer);
					          Log.e("eventrecurring ", "evntReccur "+event.recurring);
					          Log.e("eventstartDate ", "evntSrtDt "+event.startDate);
					          Log.e("eventendDate ", "evntEndDt "+event.endDate);
					          
					          if (!event.recurring) {
					            instance.startDate = event.startDate;
					            instance.endDate = event.endDate;
					          }
					          Log.e("eventallDay ", "evntAllDay "+event.allDay);
					          instance.allDay = event.allDay;
					          instance.attendees = attendeeMap.get(instance.eventId);
					          result.put(instance.toJSONObject());
				       }
			        }
			      }
			  //  cursor.close();
		    }
		    return result;//returns final result JSONArray object
		
		
	}
  
	  /*
	   * Method Name: getDate
	   * returun Type: String
	   * Input param:Date in milli seconds 
	   * Purpose: This is used
	   * 1) for converting the date calendar instance
	   */
	public static String getDate(long milliSeconds) {
	    SimpleDateFormat formatter = new SimpleDateFormat(
	            "dd/MM/yyyy hh:mm:ss a");
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(milliSeconds);
	    return formatter.format(calendar.getTime());
	}
   
	  /*
	   * Method Name: isAllDayEvent
	   * returun Type: boolean
	   * Input param:Date objects
	   * Purpose: This is used
	   * 1) checking for isAlldayEvent
	   */
  public static boolean isAllDayEvent(final Date startDate, final Date endDate) {
    return
        endDate.getTime() - startDate.getTime() == (24*60*60*1000) &&
            startDate.getHours() == 0 &&
            startDate.getMinutes() == 0 &&
            startDate.getSeconds() == 0 &&
            endDate.getHours() == 0 &&
            endDate.getMinutes() == 0 &&
            endDate.getSeconds() == 0;
  }
  
  /*
   * Method Name: queryAttendees
   * returun Type: Cursor
   * Input param:Object, SQL Qurery for searhing and retreving
   * Purpose: This is used
   * 1) Querying the Attendees list
   */
  protected Cursor queryAttendees(String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
    return context.getContentResolver().query(
        Attendees.CONTENT_URI, projection, selection, selectionArgs,
        sortOrder);
  }

  /*
   * Method Name: queryCalendars
   * returun Type: Cursor
   * Input param:Object, SQL Qurery for searhing and retreving
   * Purpose: This is used
   * 1) Querying the Calendar object list
   */
  protected Cursor queryCalendars(String[] projection, String selection,
                                  String[] selectionArgs, String sortOrder) {
    return context.getContentResolver().query(
        Calendars.CONTENT_URI, projection, selection, selectionArgs,
        sortOrder);
  }

  /*
   * Method Name: queryEvents
   * returun Type: Cursor
   * Input param:Object, SQL Qurery for searhing and retreving
   * Purpose: This is used
   * 1) Querying the Events object list
   */
  protected Cursor queryEvents(String[] projection, String selection,
                               String[] selectionArgs, String sortOrder) {
    return context.getContentResolver().query(
        Events.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
  }

  /*
   * Method Name: queryEventInstances
   * returun Type: Cursor
   * Input param:Object, SQL Qurery for searhing and retreving
   * Purpose: This is used
   * 1) Querying the Events object
   */
  protected Cursor queryEventInstances(long startFrom, long startTo,
                                       String[] projection, String selection, String[] selectionArgs,
                                       String sortOrder) {
    Uri.Builder builder = Instances.CONTENT_URI.buildUpon();
    ContentUris.appendId(builder, startFrom);
    ContentUris.appendId(builder, startTo);
    return context.getContentResolver().query(
        builder.build(), projection, selection, selectionArgs, sortOrder);
  }
  
  
  /*  public JSONArray findEvents(String title, String location, long startFrom, long startTo) {
  JSONArray result = new JSONArray();
  // Fetch events from the instance table.
  Event[] instances = fetchEventInstances(title, location, startFrom, startTo);
  if (instances == null) {
    return result;
  }
  // Fetch events from the events table for more event info.
  Map<String, Event> eventMap = fetchEventsAsMap(instances);
  // Fetch event attendees
  Map<String, ArrayList<Attendee>> attendeeMap =
      fetchAttendeesForEventsAsMap(eventMap.keySet().toArray(new String[0]));
  // Merge the event info with the instances and turn it into a JSONArray.
  for (Event instance : instances) {
    Event event = eventMap.get(instance.eventId);
    if (event != null) {
      instance.message = event.message;
      instance.location = event.location;
      instance.title = event.title;
      
      if (!event.recurring) {
        instance.startDate = event.startDate;
        instance.endDate = event.endDate;
      }
      
      instance.allDay = event.allDay;
      instance.attendees = attendeeMap.get(instance.eventId);
      result.put(instance.toJSONObject());
    }
  }
  return result;
}*/
}
