/*|~^~|Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
 |~^~|All rights reserved.
 |~^~|
 |~^~|Redistribution and use in source and binary forms, with or without
 |~^~|modification, are permitted provided that the following conditions are met:
 |~^~|
 |~^~|-1. Redistributions of source code must retain the above copyright notice, this
 |~^~|ist of conditions and the following disclaimer.
 |~^~|
 |~^~|-2. Redistributions in binary form must reproduce the above copyright notice,
 |~^~|this list of conditions and the following disclaimer in the documentation
 |~^~|and/or other materials provided with the distribution.
 |~^~|
 |~^~|-3. Neither the name of the copyright holder nor the names of its contributors
 |~^~|may be used to endorse or promote products derived from this software without
 |~^~|specific prior written permission.
 |~^~|
 |~^~|THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 |~^~|AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 |~^~|IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 |~^~|DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 |~^~|FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 |~^~|DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 |~^~|SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 |~^~|CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 |~^~|OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 |~^~|OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\*/
/**
 *
 */
package scout.edu.mit.ll.nics.android.api.data;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ReportOnConditionData
{
	// Report metadata
	// This is data not sent to the server, but used locally to manage db
	public transient ReportSendStatus sendStatus = ReportSendStatus.SENT;
	public transient boolean isForNewIncident = false;
	public transient int id;


	public long incidentid;
	public String incidentname;
	public Date datecreated;
	public String incidentnumber;

	public String reportType;
	public String county;
	public String additionalAffectedCounties;
	public String street;
	public String crossStreet;
	public String nearestCommunity;
	public String milesFromNearestCommunity;
	public String directionFromNearestCommunity;
	public Date startDate;
	public Date startTime;
	public String location;
	public String dpa;
	public String ownership;
	public String jurisdiction;
	public ArrayList<String> incidentTypes;
	public String incidentState;
	public String acreage;
	public String spreadRate;
	public ArrayList<String> fuelTypes;
	public String otherFuelTypes;
	public String percentContained;
	public String temperature;
	public String relHumidity;
	public String windSpeed;
	public String windDirection;
	public String windGusts;
	public String evacuations;
	public ArrayList<String> evacuationsInProgress;
	public String structureThreats;
	public ArrayList<String> structureThreatsInProgress;
	public String infrastructureThreats;
	public ArrayList<String> infrastructureThreatsInProgress;
	public ArrayList<String> otherSignificantInfo;
	public String calfireIncident;
	public ArrayList<String> resourcesAssigned;
	public String email;
	public double latitude;
	public double longitude;
	public boolean weatherDataAvailable;



	// Takes an incidentType ID and returns the string representation from it
	// TODO - in the future, we should query this from the incident types table instead of hardcoding it in the app
	// NOTE - If you change this list, make sure you update "incidentTypeStringToId()" so that it iterates over the new range
	public static String incidentTypeIdToString(int id)
	{
		switch(id)
		{
			case 1:
				return "Fire (Wildland)";
			case 2:
				return "Mass Casualty";
			case 3:
				return "Search and Rescue";
			case 4:
				return "Terrorist Threat / Attack";
			case 5:
				return "Fire (Structure)";
			case 6:
				return "Hazardous Materials";
			case 7:
				return "Blizzard";
			case 8:
				return "Hurricane";
			case 9:
				return "Earthquake";
			case 10:
				return "Nuclear Accident";
			case 11:
				return "Oil Spill";
			case 12:
				return "Planned Event";
			case 13:
				return "Public Health / Medical Emergency";
			case 14:
				return "Tornado";
			case 15:
				return "Tropical Storm";
			case 16:
				return "Tsunami";
			case 17:
				return "Aircraft Accident";
			case 18:
				return "Civil Unrest";
			case 19:
				return "Flood";
// TODO - Add once we add Vegetation Fire incident type.
			/*			case 20:
				return "Vegetation Fire";*/
			default:
				return null;
		}
	}

	public static int incidentTypeStringToId(String str)
	{
		// Iterating through 22 ids to reduce redundant code
		// NOTE: if we add more incident types, we need to increase this cound
		for(int i = 0; i < 22; i++)
		{
			// Get the string for the current id
			String val = incidentTypeIdToString(i);

			// Skip null values
			if(val == null)
			{
				continue;
			}

			// If we found the value, return the id
			if(val.equals(str))
			{
				return i;
			}
		}

		// If we didn't find the value, return -1
		return -1;
	}




	// Tries to read a double from a json string, returns -1 if the string could not be parsed
	private static double getDoubleFromJsonString(JSONObject obj, String str) throws JSONException, NumberFormatException
	{
		String valueStr = obj.getString(str);

		if(valueStr != null)
		{
			return Double.parseDouble(valueStr);
		}

		return -1;
	}


	// Tries to read a double from a json double, returns -1 if the value is null
	private static double getDoubleFromJsonDouble(JSONObject obj, String str) throws JSONException, NumberFormatException
	{
		if(!obj.isNull(str))
		{
			return obj.getDouble(str);
		}
		return -1;
	}


	// Parses the list of incident Types from the array
	// IDs are returned from the server, we convert IDs to their string representation here
	static private ArrayList<String> getIncidentTypeNamesFromJson(JSONObject obj, String str) throws JSONException
	{
		ArrayList<String> list = new ArrayList<String>();

		JSONArray jsonArray = obj.getJSONArray(str);

		if(jsonArray == null)
		{
			return null;
		}

		int itemCount = jsonArray.length();

		for(int i = 0; i < itemCount; i++)
		{
			String incidentTypeStr = incidentTypeIdToString(jsonArray.getInt(i));

			if(incidentTypeStr != null)
			{
				list.add(incidentTypeStr);
			}
		}

		return list;
	}

	// Parses array located at obj.str and returns the ArrayList<String> populated with the values
	// Returns null if the array is not found in the object, otherwise, always returns an array
	static private ArrayList<String> getStringArrayFromJson(JSONObject obj, String str) throws JSONException
	{
		ArrayList<String> list = new ArrayList<String>();

		JSONArray jsonArray = obj.getJSONArray(str);

		if(jsonArray == null)
		{
			return null;
		}

		int itemCount = jsonArray.length();

		for(int i = 0; i < itemCount; i++)
		{
			list.add(jsonArray.getString(i));
		}

		return list;
	}

	// Parses array located at obj.str and returns the ArrayList<String> populated with the values
	// Returns null if the array is not found in the object, otherwise, always returns an array
	// NOTE - Due to peculiarities with the server:
	// If the array is empty, the array value will be null
	// If the array has one value, the array value will be the single string
	// If the array has more than one value, the array value will be a JSON array containing strings
	static private ArrayList<String> parseStringArrayFromJson(JSONObject obj, String str) throws JSONException
	{
		if(obj == null)
		{
			return null;
		}

		ArrayList<String> list = new ArrayList<String>();

		// Try reading an array first
		if(obj.optJSONArray(str) != null)
		{
			JSONArray jsonArray = obj.getJSONArray(str);

			int itemCount = jsonArray.length();

			for(int i = 0; i < itemCount; i++)
			{
				list.add(jsonArray.getString(i));
			}
		}

		// obj might contain a single string, so try reading that
		else if(obj.optString(str,null) != null)
		{
			list.add(obj.optString(str));
		}
		// The object was not found
		else
		{
			// return null
			list = null;
		}

		return list;
	}

	// Parses the incidentTypes field from a ROC server payload, converts the ids to strings and returns an array of human-readable strings
	// This method handles the following logic (the format the data is sent by the server depends on the number of incident types)
	// If there are no incident types, returns an empty ArrayList<String>
	//
	// empty:
	//"incidentTypes": []
	//
	// one:
	// "incidentTypes": { "incidenttype": 12 }
	//
	// multiple:
	// "incidentTypes": { "incidenttype": [17,7,18,9,5,1] }
	//
	// Not sure why the server payload varies so much, but we have to deal with it
	static private ArrayList<String> getIncidentTypesArrayFromJson(JSONObject obj)
	{
		ArrayList<String> array = new ArrayList<String>();

		if(obj == null)
			return array;

		try
		{
			// If the object contains a single value (not an array)
			if(obj.optInt("incidenttype",-1) != -1)
			{
				array.add(incidentTypeIdToString(obj.optInt("incidenttype",-1)));
			}
			// Else, the object should contain an array
			else
			{
				JSONArray incidentTypeArray = obj.getJSONArray("incidenttype");

				// Convert each incident type id to human-readable string
				for(int i = 0; i < incidentTypeArray.length(); i++)
				{
					String str = incidentTypeIdToString(incidentTypeArray.getInt(i));

					// if the string is null, it's likely an invalid id value
					// Only add valid values
					if(str != null)
					{
						array.add(str);
					}
				}
			}
		}
		catch(JSONException e)
		{
			Log.w("ROC","Unable to parse incident Types array from server payload. Exception: " + e);
		}

		return array;
	}

	// Creates the incident types payload as required by the server
	//
	// empty:
	//"incidentTypes": []
	//
	// one:
	// "incidentTypes": { "incidenttype": 12 }
	//
	// multiple:
	// "incidentTypes": { "incidenttype": [17,7,18,9,5,1] }
	//
	static private Object createIncidentTypePayload(ReportOnConditionData data) throws JSONException
	{
		// If empty, return
		if(data.incidentTypes == null || data.incidentTypes.size() == 0)
		{
			return new JSONArray();
		}

		// If one element, return object with value
		if(data.incidentTypes.size() == 1)
		{
			JSONObject obj = new JSONObject();
			obj.put("incidenttype",incidentTypeStringToId(data.incidentTypes.get(0)));
			return obj;
		}

		// Otherwise, add them as an array inside of an object

		JSONObject obj = new JSONObject();

		JSONArray array = new JSONArray();


		for(String str : data.incidentTypes)
		{
			int id = incidentTypeStringToId(str);
			if(id == -1)
				continue;

			array.put(id);
		}

		// Add the array to the object
		obj.put("incidenttype", array);

		return obj;
	}



	public static ReportOnConditionData fromServerPayload(JSONObject payload)
	{
		try
		{
			ReportOnConditionData report = new ReportOnConditionData();
			// Parsing out the ROC message data:
			// JSONObject doesn't handle the value "null" very well, it turns it into a String
			// So we are going to remove any instance of null
			String messageStr = payload.getString("message");
			messageStr = messageStr.replaceAll("null","\"\"");
			JSONObject messagePayload = new JSONObject(messageStr);

			// Parsing the ROC embedded in messagePayload.report
			JSONObject rocPayload = messagePayload.getJSONObject("report");


			//================================================
			// Form metadata fields:
			//================================================
			report.incidentid = payload.optInt("incidentid",-1);
			report.datecreated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(messagePayload.optString("datecreated",""));


			//================================================
			// ROC Form Info Fields
			//================================================
			report.incidentname = payload.getString("incidentname");
			report.reportType = rocPayload.getString("reportType");


			//================================================
			// Incident Info Fields
			//================================================

			report.incidentnumber = rocPayload.optString("incidentnumber","");
			report.incidentTypes = getIncidentTypesArrayFromJson(rocPayload.optJSONObject("incidentTypes"));
			if(report.incidentTypes == null)
			{
				report.incidentTypes = new ArrayList<String>();
			}

			report.latitude = rocPayload.optDouble("latitudeAtROCSubmission",-1);
			report.longitude = rocPayload.optDouble("longitudeAtROCSubmission",-1);
			// Not all server ROCs have an incidentState yet, we have to allow for that
			// use optString so that reading the ROC doesn't fail
			report.incidentState = rocPayload.optString("incidentState","");

			//================================================
			// ROC Incident Info Fields
			//================================================

			report.county = rocPayload.optString("county","");
			report.additionalAffectedCounties = rocPayload.optString("additionalAffectedCounties","");
			report.location = rocPayload.optString("location","");
			report.street = rocPayload.optString("street","");
			report.crossStreet = rocPayload.optString("crossStreet","");
			report.nearestCommunity = rocPayload.optString("nearestCommunity","");
			report.milesFromNearestCommunity = rocPayload.optString("milesFromNearestCommunity","");
			report.directionFromNearestCommunity = rocPayload.optString("directionFromNearestCommunity","");

			report.dpa = rocPayload.optString("dpa","");
			// not a bug, the json schema reuses "sra" as ownership field
			report.ownership = rocPayload.optString("sra","");
			report.jurisdiction = rocPayload.optString("jurisdiction","");

			// Parse the string to a Date object

			String startDate = rocPayload.optString("date","");
			if(!startDate.equals(""))
			{
				report.startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(startDate);
			}
			else
			{
				report.startDate = new Date(70,1,1);
			}

			String startTime = rocPayload.optString("starttime","");
			if(!startTime.equals(""))
			{
				report.startTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()).parse(startTime);
			}
			else
			{
				report.startTime = new Date(70,1,1);
			}

			//================================================
			// Vegetation Fire Incident Scope Fields
			//================================================

			report.acreage = rocPayload.optString("scope","");//getDoubleFromJsonString(rocPayload, "scope");
			report.spreadRate = rocPayload.optString("spreadRate","");
			report.fuelTypes = parseStringArrayFromJson(rocPayload, "fuelTypes");
			if(report.fuelTypes == null)
			{
				report.fuelTypes = new ArrayList<String>();
			}
			report.otherFuelTypes = rocPayload.optString("otherFuelTypes","");
			report.percentContained = rocPayload.optString("percentContained","");//getDoubleFromJsonString(rocPayload, "percentContained");

			//================================================
			// Weather Information Fields
			//================================================

			report.temperature = rocPayload.optString("temperature","");
			report.relHumidity = rocPayload.optString("relHumidity","");
			report.windSpeed = rocPayload.optString("windSpeed","");
			report.windDirection = rocPayload.optString("windDirection","");
			report.windGusts = rocPayload.optString("windGust", "");

			//================================================
			// Threats & Evacuations Fields
			//================================================

			report.evacuations = rocPayload.optString("evacuations","");
			report.evacuationsInProgress = parseStringArrayFromJson(rocPayload.optJSONObject("evacuationsInProgress"),"evacuations");
			if(report.evacuationsInProgress == null)
			{
				report.evacuationsInProgress = new ArrayList<String>();
			}

			report.structureThreats = rocPayload.optString("structuresThreat","");
			report.structureThreatsInProgress = parseStringArrayFromJson(rocPayload.optJSONObject("structuresThreatInProgress"),"structuresThreat");
			if(report.structureThreatsInProgress == null)
			{
				report.structureThreatsInProgress = new ArrayList<String>();
			}

			report.infrastructureThreats = rocPayload.optString("infrastructuresThreat","");
			report.infrastructureThreatsInProgress = parseStringArrayFromJson(rocPayload.optJSONObject("infrastructuresThreatInProgress"),"infrastructuresThreat");
			if(report.infrastructureThreatsInProgress == null)
			{
				report.infrastructureThreatsInProgress = new ArrayList<String>();
			}

			//================================================
			// Resource Commitment Fields
			//================================================

			report.calfireIncident = rocPayload.optString("calfireIncident","");
			report.resourcesAssigned = parseStringArrayFromJson(rocPayload.optJSONObject("resourcesAssigned"), "resourcesAssigned");
			if(report.resourcesAssigned == null)
			{
				report.resourcesAssigned = new ArrayList<String>();
			}

			//================================================
			// Other Significant Info Fields
			//================================================

			report.otherSignificantInfo = parseStringArrayFromJson(rocPayload, "otherSignificantInfo");
			if(report.otherSignificantInfo == null) {
				report.otherSignificantInfo = new ArrayList<String>();
			}

			//================================================
			// Email Fields
			//================================================

			report.email = rocPayload.optString("email","");

			//================================================
			// Other Fields
			//================================================
			report.weatherDataAvailable = rocPayload.optBoolean("weatherDataAvailable");

			Log.i("ROC","ReportOnConditionData - fromServerPayload - Parsed report JSON: " + report.toJSON().toString());
			return report;
		}
		catch(Exception e)
		{
			Log.e("ROC","Unable to parse individual Report on Condition from server. Exception: " + e);
			return null;
		}
	}






	// Reads a message payload from the server containing multiple ROC payloads, then generates and returns an ArrayList of ReportOnConditionData objects
	public static ArrayList<ReportOnConditionData> multipleFromServerPayload(JSONObject payload)
	{
		try
		{
			ArrayList<ReportOnConditionData> rocData = new ArrayList<ReportOnConditionData>();

			int count = payload.getInt("count");

			// If no payloads, return the empty list
			if(count == 0)
				return rocData;


			JSONArray payloadArray = payload.getJSONArray("reports");

			for(int i = 0; i < count; i++)
			{
				// If we fail to parse one roc, catch inside the loop so we can still try and parse the rest
				try
				{
					JSONObject reportPayload = payloadArray.getJSONObject(i);


					ReportOnConditionData report = ReportOnConditionData.fromServerPayload(reportPayload);

					rocData.add(report);
				}
				catch(Exception e)
				{
					Log.e("ROC","Unable to parse Report on Condition in array from server. Exception: " + e);
				}
			}

			return rocData;
		}
		catch(Exception e)
		{
			Log.e("ROC","Unable to parse Report on Condition message from server. Exception: " + e);
		}

		return null;
	}

	// Converts an ArrayList of strings to a JSONArray with strings
	private JSONArray arrayListToJsonArray(ArrayList<String> list)
	{
		JSONArray array = new JSONArray();

		for(String str : list)
		{
			array.put(str);
		}

		return array;
	}

	// Creates a JSONObject to use as a payload for a ReportOnConditionData
	private JSONObject toServerReportPayload(long usersessionid)
	{
		try
		{
			JSONObject payload = new JSONObject();
			JSONObject messagePayload = new JSONObject();
			JSONObject rocPayload = new JSONObject();

			//================================================
			// Form metadata fields:
			//================================================
			payload.put("incidentid", incidentid);

			//================================================
			// ROC Form Info Fields
			//================================================
			payload.put("incidentname", incidentname);
			rocPayload.put("reportType", reportType);


			//================================================
			// Incident Info Fields
			//================================================

			rocPayload.put("incidentTypes",createIncidentTypePayload(this));
			rocPayload.put("latitudeAtROCSubmission", latitude);
			rocPayload.put("longitudeAtROCSubmission", longitude);
			rocPayload.put("incidentState", incidentState);

			//================================================
			// ROC Incident Info Fields
			//================================================

			rocPayload.put("county", county);
			rocPayload.put("additionalAffectedCounties", additionalAffectedCounties);
			rocPayload.put("location", location);
			rocPayload.put("street", street);
			rocPayload.put("crossStreet", crossStreet);
			rocPayload.put("nearestCommunity", nearestCommunity);
			rocPayload.put("milesFromNearestCommunity", milesFromNearestCommunity);
			rocPayload.put("directionFromNearestCommunity", directionFromNearestCommunity);


			rocPayload.put("dpa", dpa);
			// not a bug, the json schema reuses "sra" as ownership field
			rocPayload.put("sra", ownership);
			rocPayload.put("jurisdiction", jurisdiction);

			// Parse the string to a Date object
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
			rocPayload.put("date", dateFormatter.format(startDate));
			rocPayload.put("starttime", dateFormatter.format(startTime));

			//================================================
			// Vegetation Fire Incident Scope Fields
			//================================================

			rocPayload.put("scope", acreage);
			rocPayload.put("spreadRate", spreadRate);
			rocPayload.put("fuelTypes", arrayListToJsonArray(fuelTypes));
			rocPayload.put("otherFuelTypes", otherFuelTypes);
			rocPayload.put("percentContained", percentContained);

			//================================================
			// Weather Information Fields
			//================================================

			rocPayload.put("temperature", temperature);
			rocPayload.put("relHumidity", relHumidity);
			rocPayload.put("windSpeed", windSpeed);
			rocPayload.put("windDirection", windDirection);
			rocPayload.put("windGust", windGusts);

			//================================================
			// Threats & Evacuations Fields
			//================================================

			rocPayload.put("evacuations", evacuations);
			// TODO - Need to verify that this properly handles all combinations sent from server (0, 1, and 2+ strings)
			rocPayload.put("evacuationsInProgress", new JSONObject().put("evacuations",arrayListToJsonArray(evacuationsInProgress)));

			rocPayload.put("structuresThreat", structureThreats);
			rocPayload.put("structuresThreatInProgress", new JSONObject().put("structuresThreat",arrayListToJsonArray(structureThreatsInProgress)));

			rocPayload.put("infrastructuresThreat", infrastructureThreats);
			rocPayload.put("infrastructuresThreatInProgress", new JSONObject().put("infrastructuresThreat",arrayListToJsonArray(infrastructureThreatsInProgress)));

			//================================================
			// Resource Commitment Fields
			//================================================

			rocPayload.put("calfireIncident", calfireIncident);
			// TODO - webapp supports "other resources assigned" checkbox with field.
			// TODO - but it wasn't a part of the requirements (it was not on the wireframes)
			// TODO -
			// TODO - to add it to the server payload, we need to add an "public String otherResourcesAssigned" member variable
			// TODO - and add it to the payload as follows:
			// TODO - rocPayload.put("resourcesAssigned", new JSONObject().put("resourcesAssigned",arrayListToJsonArray(resourcesAssigned)).put("other", otherResourcesAssigned));
			rocPayload.put("resourcesAssigned", new JSONObject().put("resourcesAssigned",arrayListToJsonArray(resourcesAssigned)));

			//================================================
			// Other Significant Info Fields
			//================================================

			rocPayload.put("otherSignificantInfo", arrayListToJsonArray(otherSignificantInfo));

			//================================================
			// Email Fields
			//================================================

			rocPayload.put("email", email);

			//================================================
			// Other Fields
			//================================================

			rocPayload.put("weatherDataAvailable", weatherDataAvailable);
			rocPayload.put("simplifiedEmail",true);
			rocPayload.put("reportBy",email);
			rocPayload.put("comments","");
			rocPayload.put("rocDisplayName","");


			//--------------------------------------------
			// Building the final payload:
			//--------------------------------------------
			messagePayload.put("report",rocPayload);
			dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
			messagePayload.put("datecreated",dateFormatter.format(datecreated));



			//TODO - What should this be? How do we get the workspace id?
			//TODO- is hardcoding the value of 1 okay?
			//payload.put("workspaceid",1);

			//payload.put("formTypeId",10);
			// FIXME - formtypeid must be 1 for the ROC to show up in the webapp
			// Documentation says ROC formtypeid should be 7, but I think the webapp / emapi are programmed to use 1
			payload.put("formtypeid",1);
			payload.put("usersessionid",usersessionid);
			payload.put("distributed", false);
			payload.put("message",messagePayload.toString());
			return payload;
		}
		catch(JSONException e)
		{
			Log.e("ROC","Unable to create server payload from ROC data object. Exception: " + e);
		}

		return null;
	}


	// Creates a JSONObject payload to be sent to the server
	// This method should be used when an ROC will create a new incident
	// as this method adds the data required to create the new incident
	private JSONObject toServerIncidentPayload(long usersessionid)
	{
		try
		{
			JSONObject payload = toServerReportPayload(usersessionid);

			JSONObject incidentPayload = new JSONObject();

			incidentPayload.put("usersessionid",usersessionid);

			//TODO - What should this be? How do we get the workspace id?
			//TODO- is hardcoding the value of 1 okay?
			incidentPayload.put("workspaceid",1);
			incidentPayload.put("usersession",null);

			incidentPayload.put("incidentname",incidentname);
			incidentPayload.put("incidentnumber",incidentnumber);
			incidentPayload.put("lat", latitude);
			incidentPayload.put("lon", longitude);
			incidentPayload.put("active", true);
			incidentPayload.put("folder","");
			incidentPayload.put("description","");
			incidentPayload.put("bounds",null);

			// Building the incident Types Array
			JSONArray incidentTypesArray = new JSONArray();
			for(int i = 0; i < incidentTypes.size(); i++)
			{
				JSONObject incidentTypeObj = new JSONObject();
				incidentTypeObj.put("incidenttypeid",incidentTypeStringToId(incidentTypes.get(i)));

				incidentTypesArray.put(incidentTypeObj);
			}
			incidentPayload.put("incidentIncidenttypes",incidentTypesArray);

			// Adding the incident payload to the entire payload
			payload.put("incident",incidentPayload);
			return payload;
		}
		catch(Exception e)
		{
			Log.e("ROC","Unable to create server incident payload from ROC data object. Exception: " + e);
		}
		return null;
	}

	// Returns a JSONArray of strings from an ArrayList of strings
	private static JSONArray stringArrayListToJson(ArrayList<String> stringArray)
	{
		JSONArray array = new JSONArray();

		if(stringArray == null)
			return array;

		for(String str : stringArray)
		{
			array.put(str);
		}

		return array;
	}

	// Returns an ArrayList of strings from a JSONArray of strings
	private static ArrayList<String> stringArrayFromJson(JSONArray array) throws JSONException
	{
		ArrayList<String> strArray = new ArrayList<String>();

		if(array == null)
			return strArray;


		for(int i = 0; i < array.length(); i++)
		{
			strArray.add(array.getString(i));
		}

		return strArray;
	}

	// This serializes the object to store it in the ReportOnConditionTable database
	public JSONObject toJSON()
	{
		try
		{
			JSONObject obj = new JSONObject();

			// Building the SimpleDateFormat object to convert a string to a Date object
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

			// Metadata
			obj.put("isForNewIncident", isForNewIncident);

			// long cannot be null, use regular obj.put()
			obj.put("incidentid", incidentid);
			obj.put("incidentname", incidentname);
			obj.put("incidentnumber", incidentnumber);

			obj.put("datecreated", dateFormatter.format(datecreated));

			obj.put("reportType", reportType);
			obj.put("county", county);
			obj.put("additionalAffectedCounties", additionalAffectedCounties);
			obj.put("location", location);
			obj.put("street", street);
			obj.put("crossStreet", crossStreet);
			obj.put("nearestCommunity", nearestCommunity);
			obj.put("milesFromNearestCommunity", milesFromNearestCommunity);
			obj.put("directionFromNearestCommunity", directionFromNearestCommunity);

			obj.put("startDate", dateFormatter.format(startDate));
			obj.put("startTime", dateFormatter.format(startTime));
			obj.put("dpa", dpa);
			obj.put("ownership", ownership);
			obj.put("jurisdiction", jurisdiction);
			obj.put("incidentTypes", stringArrayListToJson(incidentTypes));
			obj.put("incidentState", incidentState);
			obj.put("acreage", acreage);
			obj.put("spreadRate", spreadRate);
			obj.put("fuelTypes", stringArrayListToJson(fuelTypes));
			obj.put("otherFuelTypes", otherFuelTypes);
			obj.put("percentContained", percentContained);
			obj.put("temperature", temperature);
			obj.put("relHumidity", relHumidity);
			obj.put("windSpeed", windSpeed);
			obj.put("windDirection", windDirection);
			obj.put("windGusts", windGusts);
			obj.put("evacuations", evacuations);
			obj.put("evacuationsInProgress", stringArrayListToJson(evacuationsInProgress));
			obj.put("structureThreats", structureThreats);
			obj.put("structureThreatsInProgress", stringArrayListToJson(structureThreatsInProgress));
			obj.put("infrastructureThreats", infrastructureThreats);
			obj.put("infrastructureThreatsInProgress", stringArrayListToJson(infrastructureThreatsInProgress));
			obj.put("otherSignificantInfo", stringArrayListToJson(otherSignificantInfo));
			obj.put("calfireIncident", calfireIncident);
			obj.put("resourcesAssigned", stringArrayListToJson(resourcesAssigned));
			obj.put("email", email);
			// doubles can't be null, use regular obj.put()
			obj.put("latitude", latitude);
			obj.put("longitude", longitude);
			obj.put("weatherDataAvailable", weatherDataAvailable);

			return obj;
		}
		catch(Exception e)
		{
			Log.e("ROC","Failed to serialize ROC Data object for DB storage. Exception: " + e);
		}

		return null;

	}


	// Computes the server payload,
	// if the ROC must create an incident, adds the incident portion to the payload
	public JSONObject toServerPayload(long usersessionid)
	{
		if(isForNewIncident)
		{
			return toServerIncidentPayload(usersessionid);
		}
		else
		{
			return toServerReportPayload(usersessionid);
		}
	}

	// This deserializes and rebuilds the object to retrieve it from the ReportOnConditionTable database
	public static ReportOnConditionData fromJSON(String json)
	{
		try
		{
			// Try building the JSONObject from the string
			JSONObject obj = new JSONObject(json);

			ReportOnConditionData data = new ReportOnConditionData();

			// Building the SimpleDateFormat object to convert a string to a Date object
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());


			// Metadata
			data.isForNewIncident = obj.optBoolean("isForNewIncident");

			// Actual ROC data
			data.incidentid = obj.getLong("incidentid");
			data.incidentname = obj.getString("incidentname");
			data.datecreated = dateFormatter.parse(obj.getString("datecreated"));
			data.incidentnumber = obj.getString("incidentnumber");

			data.reportType = obj.getString("reportType");
			data.county = obj.getString("county");
			data.additionalAffectedCounties = obj.getString("additionalAffectedCounties");
			data.location = obj.getString("location");
			data.street = obj.getString("street");
			data.crossStreet = obj.getString("crossStreet");
			data.nearestCommunity = obj.getString("nearestCommunity");
			data.milesFromNearestCommunity = obj.getString("milesFromNearestCommunity");
			data.directionFromNearestCommunity = obj.getString("directionFromNearestCommunity");
			data.startDate = dateFormatter.parse(obj.getString("startDate"));
			data.startTime = dateFormatter.parse(obj.getString("startTime"));
			data.dpa = obj.getString("dpa");
			data.ownership = obj.getString("ownership");
			data.jurisdiction = obj.getString("jurisdiction");
			data.incidentTypes = stringArrayFromJson(obj.getJSONArray("incidentTypes"));
			data.incidentState = obj.getString("incidentState");
			data.acreage = obj.getString("acreage");// FIXME - should be getDouble
			data.spreadRate = obj.getString("spreadRate");
			data.fuelTypes = stringArrayFromJson(obj.getJSONArray("fuelTypes"));
			data.otherFuelTypes = obj.getString("otherFuelTypes");
			data.percentContained = obj.getString("percentContained");// FIXME - should be getDouble
			data.temperature = obj.getString("temperature");// FIXME - should be getDouble
			data.relHumidity = obj.getString("relHumidity");// FIXME - should be getDouble
			data.windSpeed = obj.getString("windSpeed");// FIXME - should be getDouble
			data.windDirection = obj.getString("windDirection");
			data.windGusts = obj.getString("windGusts");// FIXME - should be getDouble
			data.evacuations = obj.getString("evacuations");
			data.evacuationsInProgress = stringArrayFromJson(obj.getJSONArray("evacuationsInProgress"));
			data.structureThreats = obj.getString("structureThreats");
			data.structureThreatsInProgress = stringArrayFromJson(obj.getJSONArray("structureThreatsInProgress"));
			data.infrastructureThreats = obj.getString("infrastructureThreats");
			data.infrastructureThreatsInProgress = stringArrayFromJson(obj.getJSONArray("infrastructureThreatsInProgress"));
			data.otherSignificantInfo = stringArrayFromJson(obj.getJSONArray("otherSignificantInfo"));
			data.calfireIncident = obj.getString("calfireIncident");
			data.resourcesAssigned = stringArrayFromJson(obj.getJSONArray("resourcesAssigned"));
			data.email = obj.getString("email");
			data.latitude = obj.getDouble("latitude");
			data.longitude = obj.getDouble("longitude");
			data.weatherDataAvailable = obj.optBoolean("weatherDataAvailable");
			return data;
		}
		catch(Exception e)
		{
			Log.e("ROC","Failed to deserialize ROC Data object for DB retrieval. Exception: " + e);
		}

		return null;
	}
}
