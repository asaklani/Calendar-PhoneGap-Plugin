package nl.xservices.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import nl.xservices.plugins.accessor.AbstractCalendarAccessor;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;




public class Calendar extends CordovaPlugin {
	public static final String ACTION_FIND_EVENT = "findEvents";
	
	private CallbackContext callback;
	@Override
	public boolean execute(String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {
		if ("findEvents".equals(action)) {
	        cordova.getActivity().runOnUiThread(new Runnable() {
	            public void run() {
	            	findEvents(args, callbackContext);//call the "findEvents()" method with input JSONArray and callbackcontext objects.
	            }
	        });
	        return true;
	    }
	    return false;
	}
	
	  /*
	   * Method Name: findEvents
	   * returun Type: boolean
	   * Purpose: This method is used for 
	   * 1) Getting the Inputs(Title, location, start date, end date)  from js.
	   * 2) Check whether the device is configured with the Sapient Exhange emai account or not.
	   * 3) pass the inputs to the "Todayevents()" method of "AbstractCalendarAccessor" class and get the events
	   * 4) Check for the length of events JSONArray object and based on length format the JSONObject
	   * 5) return the final JSONObject to the js(JavaScript)
	   */
	 private boolean findEvents(JSONArray args, CallbackContext callbackContext) {
		 AbstractCalendarAccessor calaccs=new   AbstractCalendarAccessor(this.cordova.getActivity());
		 
		 boolean resultFlg = false;
		    if (args.length() == 0) {
		      System.err.println("Exception: No Arguments passed");
		    }
		    JSONObject result = new JSONObject();
		    try {
		    	JSONObject paramArray = args.getJSONObject(0);
		    	JSONArray events = null;
		    	String[] resObj = calaccs.getUsername();
		    	String accountExists = resObj[0];
		    	String exchangeAccEmail = resObj[1];
		    	Log.i("accountExists", accountExists);
		    	Log.i("exchangeAccEmail", exchangeAccEmail);
		    	//accountExists ="true";
		    	//if(calaccs.getUsername()){//checking whether the device is configured with Sapient Exchange Email Account.
		    	if(accountExists.equals("true")){//checking whether the device is configured with Sapient Exchange Email Account.
		    		String deviceExchangeUsrName = exchangeAccEmail.substring(0,exchangeAccEmail.indexOf("@"));
		    		String userEmail =  paramArray.optString("empEmail");
		    		String userNtid =  paramArray.optString("usrNtid");
		    		/*String usrNtidEmail = userNtid+"@publicisgroupe.net";*/
		    		
		    		/*Start - Edited By albin Daniel for debugging*/
		    		System.out.println("exchangeAccEmail" +exchangeAccEmail );
		    		System.out.println("userEmail" +userEmail );
		    		System.out.println("userNtid" +userNtid );
		    		/*System.out.println("usrNtidEmail" +usrNtidEmail );*/
		    		/*End - Edited By albin Daniel for debugging*/
		    		
	//	 if(userEmail.equalsIgnoreCase(exchangeAccEmail) || usrNtidEmail.equalsIgnoreCase(exchangeAccEmail)){
		    			events = calaccs.Todayevents(
					              paramArray.optString("title"),
					              paramArray.optString("location"),
					              paramArray.optString("startTime"),
					              paramArray.optString("endTime"),
					              paramArray.optString("empEmail"),
					              paramArray.optString("usrNtid"),
					              exchangeAccEmail);//Calling the "Todayevents()" method and getting the events JSONArray object
//					      PluginResult res = new PluginResult(PluginResult.Status.OK, events);
					      Log.i("jsonevents", events.toString());
					      
					      if(events==null || events.length()==0){//checking for the length of above returned events JSONArray
					    	  Log.i("jsonevents2", "NO_EVENTS");
					    	  result.put("status", "NO_EVENTS");
					    	  result.put("data", events);
					    	  callbackContext.success(result);
					    	  resultFlg = true;
					      }else{//checking for the length of above returned events JSONArray
					    	  Log.i("jsonevents3", "EVENTS_FOUND");
					    	  result.put("status", "EVENTS_FOUND");
					    	  result.put("data", events);
					    	  callbackContext.success(result);
					    	  resultFlg = true;
					      }
	         
				}else{
						Log.i("jsonevents1", "NO_SAP_ACC_FOUND");
			    	  result.put("status", "NO_SAP_ACC_FOUND");
			    	  result.put("data", events);
			    	  callbackContext.success(result);
			    	 resultFlg = true;
				}

		    } catch (JSONException e) {
		    	
		      System.err.println("Exception: " + e.getMessage());
		      callbackContext.error(result);
		      resultFlg = false;
		    }
		    return resultFlg;
		  }
	
	  private AbstractCalendarAccessor calendarAccessor;
	  
}
