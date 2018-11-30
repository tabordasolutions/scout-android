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
package scout.edu.mit.ll.nics.android.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.client.HttpResponseException;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.data.OperationalUnit;
import scout.edu.mit.ll.nics.android.api.data.OrgCapabilities;
import scout.edu.mit.ll.nics.android.api.data.SimpleReportCategoryType;
import scout.edu.mit.ll.nics.android.api.data.SimpleReportData;
import scout.edu.mit.ll.nics.android.api.handlers.ChatResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.SimpleReportNoImageResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.MDTResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.MarkupResponseHandler;
import scout.edu.mit.ll.nics.android.api.handlers.SimpleReportResponseHandler;
import scout.edu.mit.ll.nics.android.api.messages.AssignmentMessage;
import scout.edu.mit.ll.nics.android.api.messages.ChatMessage;
import scout.edu.mit.ll.nics.android.api.messages.CollaborationRoomMessage;
import scout.edu.mit.ll.nics.android.api.messages.TrackingLayerMessage;
import scout.edu.mit.ll.nics.android.api.messages.IncidentMessage;
import scout.edu.mit.ll.nics.android.api.messages.LoginMessage;
import scout.edu.mit.ll.nics.android.api.messages.MarkupMessage;
import scout.edu.mit.ll.nics.android.api.messages.OrganizationMessage;
import scout.edu.mit.ll.nics.android.api.messages.SimpleReportMessage;
import scout.edu.mit.ll.nics.android.api.messages.ReportOnConditionMessage;
import scout.edu.mit.ll.nics.android.api.messages.UserMessage;
import scout.edu.mit.ll.nics.android.api.payload.AssignmentPayload;
import scout.edu.mit.ll.nics.android.api.payload.ChatPayload;
import scout.edu.mit.ll.nics.android.api.payload.CollabroomPayload;
import scout.edu.mit.ll.nics.android.api.payload.IncidentPayload;
import scout.edu.mit.ll.nics.android.api.payload.LoginPayload;
import scout.edu.mit.ll.nics.android.api.payload.MarkupPayload;
import scout.edu.mit.ll.nics.android.api.payload.MobileDeviceTrackingPayload;
import scout.edu.mit.ll.nics.android.api.payload.TrackingLayerPayload;
import scout.edu.mit.ll.nics.android.api.payload.TrackingTokenPayload;
import scout.edu.mit.ll.nics.android.api.payload.WeatherPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.ReportOnConditionPayload;
import scout.edu.mit.ll.nics.android.api.tasks.ParseChatMessagesTask;
import scout.edu.mit.ll.nics.android.api.tasks.ParseMarkupFeaturesTask;
import scout.edu.mit.ll.nics.android.api.tasks.ParseSimpleReportsTask;
import scout.edu.mit.ll.nics.android.api.tasks.ParseReportOnConditionsTask;
import scout.edu.mit.ll.nics.android.auth.AuthManager;
import scout.edu.mit.ll.nics.android.auth.providers.OpenAMAuthProvider;
import scout.edu.mit.ll.nics.android.utils.Constants;
import scout.edu.mit.ll.nics.android.utils.EncryptedPreferences;
import scout.edu.mit.ll.nics.android.utils.Intents;
import scout.edu.mit.ll.nics.android.utils.NotificationsHandler;

@SuppressWarnings("unchecked")
public class RestClient
{
	private static Context mContext = null;
	private static DataManager mDataManager = null;
	private static GsonBuilder mBuilder = new GsonBuilder();
	private static Header[] mAuthHeader = null;

	private static AsyncTask<ArrayList<ChatPayload>, Object, Integer> mParseChatMessagesTask;
	private static AsyncTask<ArrayList<SimpleReportPayload>, Object, Integer> mParseSimpleReportsTask;
	private static AsyncTask<MarkupPayload, Object, Integer> mParseMarkupFeaturesTask;
	private static AsyncTask<ArrayList<ReportOnConditionPayload>, Object, Integer> mParseReportOnConditionsTask;

	private static SparseArray<SimpleReportResponseHandler> mSimpleReportResponseHandlers;

	private static boolean firstRun = true;

	private static String mDeviceId;

	private static boolean mSendingSimpleReports;
	private static boolean mSendingFieldReports;
	private static boolean mSendingDamageReports;
	private static boolean mSendingResourceRequests;
	private static boolean mSendingWeatherReports;
	private static boolean mSendingChatMessages;
	private static boolean mSendingMarkupFeatures;

	private static boolean mFetchingSimpleReports;
	private static boolean mFetchingFieldReports;
	private static boolean mFetchingReportOnConditions;
	private static boolean mFetchingDamageReports;
	private static boolean mFetchingResourceRequests;
	private static boolean mFetchingWeatherReports;
	private static boolean mFetchingChatMessages;
	private static boolean mFetchingMarkupFeatures;

	private static AuthManager mAuthManager;

	// Returns whether or not the private AuthManager mAuthManager is null
	public static boolean isAuthManagerNull ()
	{
		return mAuthManager == null;
	}

	public static void switchOrgs (int orgId)
	{
/*
		try {
	    	LoginPayload p = new LoginPayload(mDataManager.getUsername());
	    	p.setWorkspaceId(mDataManager.getWorkspaceId());
	    	p.setOrgId(orgId);
	    	
			StringEntity entity = new StringEntity(p.toJsonString());
			
	    	mAuthManager.getClient().post("login/switchOrg", entity, new AsyncHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
	    			Log.e(Constants.nics_DEBUG_ANDROID_TAG, "Success to switch orgs." + content);
	    			LoginMessage message = mBuilder.create().fromJson(content, LoginMessage.class);
					LoginPayload payload = message.getLoginPayload().get(0);
					mDataManager.setLoginData(payload);
					mDataManager.requestOrgCapabilitiesUpdate();
				}

				@Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
	    			Log.e(Constants.nics_DEBUG_ANDROID_TAG, "Fail to switch orgs." + content);
				}
	    	});
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		*/
	}

	//Test function to test JSON Parsing
	public static void getHardCodedOrgCapabilities ()
	{// {'name':'FR-Form','capId':10}, {'name':'RES-Form','capId':14},  , {'name':'SVR-Form','capId':14}
		String content = "{'message':'ok','count':5,'capabilities':[{'name':'Chat','capId':14}, {'name':'MapMarkup','capId':14}, {'name':'WR-Form','capId':10}, {'name':'DR-Form','capId':10}, {'name':'ROC-Form','capId':10}],'orgCapability':null,'capabilitiesName':null}";
		OrgCapabilities OrgCap = new OrgCapabilities();
		OrgCap.setCapabilitiesFromJSON(content);
		mDataManager.setOrgCapabilites(OrgCap);
	}

    /*public static void getOrgCapabilities(long orgId){
		//FIXME: This endpoint doesn't seem to be implemented on the backend server
		Log.e("tag","hullo sent org capabilities request for workSpaceId: ");
		Log.e("tag","hullo orgCapabilities URL: " + "\"orgs/" + mDataManager.getWorkspaceId() +"/capabilities?orgId=" + orgId + "\"");


		mAuthManager.getClient().get("orgs/" + mDataManager.getWorkspaceId() +"/capabilities?orgId=" + orgId , new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				String content = (responseBody != null) ? new String(responseBody) : "error";

				Log.e("tag","hullo got OrgCapabilities : " + content);
				
				OrgCapabilities OrgCap = new OrgCapabilities();
				OrgCap.setCapabilitiesFromJSON(content);
				mDataManager.setOrgCapabilites(OrgCap);
			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				Log.e("tag","hullo get orgCapabilities failed." + statusCode);

				String content = (responseBody != null) ? new String(responseBody) : "error";
    			Log.e(Constants.nics_DEBUG_ANDROID_TAG, "Fail to get Org Capabilities." + content);
			}
    	});
    }*/

	private static void attemptLogin (final DataManager.CustomCommand command)
	{
		try
		{
			//This method used to have the parameter getActiveAssignment
			// but it was never used.
			mDataManager = DataManager.getInstance(mContext);
			final String username = mDataManager.getUsername();

			mDeviceId = Build.SERIAL;
			if (mDeviceId == null)
			{
				mDeviceId = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
			}

			mSimpleReportResponseHandlers = new SparseArray<SimpleReportResponseHandler>();

			JSONObject obj = new JSONObject();
			obj.put("username", username);
			obj.put("userId", 0);
			obj.put("userSessionId", 0);
			obj.put("workspaceId", 0);


			//LoginPayload p = new LoginPayload(username);
			//p.setWorkspaceId(mDataManager.getWorkspaceId());
//	    	p.setWorkspaceId(0);
//	    	p.setUserId(null);
//	    	p.setUserSessionId(null);
			Log.e("test", "JSON Data sent in login request: " + obj);
			cz.msebera.android.httpclient.entity.StringEntity entity = new cz.msebera.android.httpclient.entity.StringEntity(obj.toString());


			mAuthManager.getClient().post("login", entity, new AsyncHttpResponseHandler()
			{

				@Override
				public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
				{
					String content = (responseBody != null) ? new String(responseBody) : "error";

					//if you don't properly logout then the api doesn't handle the usersession properly and sends back "error" next time you try to log in.
					//Until the APi sends back the response that the user is already logged in. I am currently catching the error and logging out, then logging back in.
					if (content.equals("error"))
					{
						logout(username, true, command);
						return;
					}


					mDataManager.setLoggedIn(true);
					Log.i("nicsRest", "Successfully logged in as: " + username + " status code: " + statusCode);

					Log.i("nicsRest", "Received response: " + content);

					mAuthHeader = headers;

					LoginMessage message = mBuilder.create().fromJson(content, LoginMessage.class);
					LoginPayload payload = message.getLoginPayload().get(0);

					mDataManager.setLoginData(payload);

					Log.e("LOGINTEST", "Just logged in: received USID: " + mDataManager.getUserSessionId());

					Intent intent = new Intent();
					intent.setAction(Intents.nics_SUCCESSFUL_LOGIN);
					intent.putExtra("payload", message.toJsonString());
					mContext.sendBroadcast(intent);

					getUserOrgs(payload.getUserId());
					getAllIncidents(payload.getUserId());
					getUserData(payload.getUserId());
					mDataManager.requestOrgCapabilitiesUpdate();

					if (command != null)
					{
						command.performAction();
					}

//					if(getActiveAssignment) {
//						getActiveAssignment(username, payload.getUserId());
//					}
					//getWeatherUpdate(mDataManager.getMDTLatitude(), mDataManager.getMDTLongitude());

					mDataManager.addPersonalHistory("User " + username + " logged in successfully. ");
				}

				@Override
				public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
				{
					String content = (responseBody != null) ? new String(responseBody) : "error";

					mDataManager.setLoggedIn(false);
					mDataManager.stopPollingAlarms();

					boolean broadcast = true;

					Intent intent = new Intent();
					intent.setAction(Intents.nics_FAILED_LOGIN);

					if (error.getClass() == HttpResponseException.class)
					{
						HttpResponseException exception = (HttpResponseException) error;

						if (exception.getStatusCode() == 412)
						{
							//USIDDEFECT: this code was already in place before the fix, it may no longer be needed
							broadcast = false;
							LoginMessage message = mBuilder.create().fromJson(content, LoginMessage.class);
							Log.w("nicsRest", message.getMessage());
							logout(username, true, command);
						}
						else if (exception.getStatusCode() == 401)
						{
							intent.putExtra("message", "Invalid username or password");
						}
						else
						{
							intent.putExtra("message", exception.getMessage());
						}
					}
					else
					{

						if (error.getMessage() != null)
						{
							Log.e("nicsRest", error.getMessage());
						}
						else
						{
							Log.e("nicsRest", "null error on failed login attempt");
						}
						intent.putExtra("offlineMode", true);

						if (error.getClass() == UnknownHostException.class)
						{
							intent.putExtra("message", "Failed to connect to server. Please check your network connection.");
						}
						else
						{
							if (error.getMessage() != null)
							{
								intent.putExtra("message", error.getMessage());
							}
							else
							{
								Log.e("nicsRest", "null error on failed login attempt");
							}
						}
						error.printStackTrace();
					}

					if (broadcast)
					{
						mContext.sendBroadcast(intent);

						if (intent.getExtras() != null)
						{
							mDataManager.addPersonalHistory("User " + username + " login failed: " + intent.getExtras().get("message"));
						}
						else
						{
							mDataManager.addPersonalHistory("User " + username + " login failed.");
						}
					}
				}
			});
		}
		catch (UnsupportedEncodingException e)
		{
			Log.e("nicsRest", e.getLocalizedMessage());

			Intent intent = new Intent();
			intent.setAction(Intents.nics_FAILED_LOGIN);

			intent.putExtra("offlineMode", true);
			intent.putExtra("message", "Failed to connect to server: " + e.getLocalizedMessage() + " - Please check your network connection.");
			mContext.sendBroadcast(intent);
		}
		catch (JSONException e)
		{
			Log.e("nicsRest", e.getLocalizedMessage());

			Intent intent = new Intent();
			intent.setAction(Intents.nics_FAILED_LOGIN);

			intent.putExtra("offlineMode", true);
			intent.putExtra("message", "Failed to connect to server: " + e.getLocalizedMessage() + " - Something went wrong.");
			mContext.sendBroadcast(intent);
		}
	}

	public static void login (final Context context, final String username, final String password, DataManager.CustomCommand command)
	{
		mContext = context;

		mAuthManager = AuthManager.getInstance(username, password);
		mAuthManager.registerAuthType(new OpenAMAuthProvider(mContext));        //enable for openAM

//		mAuthManager.registerAuthType(new BasicAuthProvider());			//enable for basic
//		also check AuthManager getClient() for the correct auth type

		attemptLogin(command);
	}

	public static void logout (final String username, final boolean retryLogin, final DataManager.CustomCommand command)
	{
		if (mAuthManager != null)
		{

			//	mAuthManager.getClient().post("login?username=" + username, new RequestParams(), new AsyncHttpResponseHandler() {
			mAuthManager.getClient().delete("login/" + username, new AsyncHttpResponseHandler()
			{

				@Override
				public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
				{

					Log.i("nicsRest", "Successfully logged out: " + username);
					mFetchingChatMessages = false;
					mFetchingFieldReports = false;
					mFetchingDamageReports = false;
					mFetchingMarkupFeatures = false;
					mFetchingResourceRequests = false;
					mFetchingSimpleReports = false;

					mSendingChatMessages = false;
					mSendingFieldReports = false;
					mSendingDamageReports = false;
					mSendingMarkupFeatures = false;
					mSendingResourceRequests = false;
					mSendingSimpleReports = false;

					clearParseChatMessagesTask();
					clearParseMarkupFeaturesTask();
					clearParseSimpleReportTask();
					clearParseReportOnConditionTask();

					mDataManager.stopPollingAlarms();

					if (retryLogin)
					{
						attemptLogin(command);
					}
					else
					{
						mDataManager.addPersonalHistory("User " + username + " logged out successfully. ");
						mDataManager.setLoggedIn(false);
					}
				}

				@Override
				public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
				{
					Log.e("nicsRest", "Failed to log out: " + username);
				}
			});
		}
	}

	// Checks whether our current usersessionID is still valid on the server
	// Invokes DataManager's invalidSessionIDCommand if a session was not found
	// Returns true if it is valid
	// Returns false if it is invalid
	public static void validateSessionID ()
	{
		String url = "users/1/verifyActiveSession/" + mDataManager.getUserSessionId();

		AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler()
		{

			long userSessionID = mDataManager.getUserSessionId();

			@Override
			public void onSuccess (int statusCode, Header[] headers, byte[] responseBody)
			{

				String content = (responseBody != null) ? new String(responseBody) : "error";
				Log.e("nicsRest", "Validate Session ID Status: " + statusCode + ", Response: " + content);

				JSONObject response;
				try
				{
					response = new JSONObject(content);


					boolean sessionActive = response.getBoolean("activeSession");

					// Inform Main Activity that the session is no longer active
					if (!sessionActive)
					{
						Log.e("USIDDEFECT", "Informing MainActivity");
						mDataManager.performInvalidSessionIDCommand(userSessionID);
					}

				}
				catch (JSONException e)
				{
					Log.e("nicsRest", "Error: unable to read server response. Unable to validate the user session.");
					e.printStackTrace();
					mDataManager.performInvalidSessionIDCommand(userSessionID);
				}
			}

			@Override
			public void onFailure (int statusCode, Header[] headers, byte[] responseBody, Throwable error)
			{
				Log.e("nicsRest", "Failed to validate user session ID");

				String content = (responseBody != null) ? new String(responseBody) : "error";

				Log.e("nicsRest", "Validate Session ID Status: " + statusCode + ", Response: " + content);
				mDataManager.performInvalidSessionIDCommand(userSessionID);
			}
		};

		Log.e("nicsRest", "About to check USID: " + mDataManager.getUserSessionId());


		mAuthManager.getClient().get(url, responseHandler);
	}

	public static void deleteMarkup (final String featureId)
	{
		mAuthManager.getClient().delete("mapmarkups/" + mDataManager.getWorkspaceId() + "/" + featureId, new AsyncHttpResponseHandler()
		{
			@Override
			public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
			{

				Log.i("nicsRest", "Successfully deleted feature: " + featureId);
				mDataManager.addPersonalHistory("Successfully deleted feature: " + featureId);
				mDataManager.deleteMarkupHistoryForCollabroomByFeatureId(mDataManager.getSelectedCollabRoom().getCollabRoomId(), featureId);
			}

			@Override
			public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
			{

				Log.e("nicsRest", "Failed to delete out: " + featureId);
				mDataManager.addPersonalHistory("Failed to delete out: " + featureId);
			}
		});
	}

	public static void getUserData (long userId)
	{
		mAuthManager.getClient().get("users/" + mDataManager.getWorkspaceId() + "/" + userId, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";

				UserMessage message = mBuilder.create().fromJson(content, UserMessage.class);
				if (message.getCount() > 0)
				{
					mDataManager.setUserData(message.getUsers().get(0));
					Log.i("nicsRest", "Successfully received user information.");
					mDataManager.addPersonalHistory("Successfully received user information.");
				}
			}

			@Override
			public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";

				Log.e("nicsRest", content);

				Intent intent = new Intent();
				intent.setAction(Intents.nics_FAILED_LOGIN);

				intent.putExtra("offlineMode", true);
				intent.putExtra("message", "Failed to connect to server: " + content + " - Please check your network connection.");
				mDataManager.addPersonalHistory("Failed to receive user information.");
				mContext.sendBroadcast(intent);
			}
		});
	}

	public static void getUserOrgs (long userId)
	{
		mAuthManager.getClient().get("orgs/" + mDataManager.getWorkspaceId() + "?userId=" + userId, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";

				OrganizationMessage message = mBuilder.create().fromJson(content, OrganizationMessage.class);
				if (message.getCount() > 0)
				{
					mDataManager.setOrganizations(message.getOrgs());

					EncryptedPreferences preferences;
					preferences = new EncryptedPreferences(mContext.getSharedPreferences(Constants.nics_USER_PREFERENCES, 0));
					preferences.savePreferenceString("savedOrgs", message.toJsonString());

					Intent intent = new Intent();
					intent.setAction(Intents.nics_SUCCESSFUL_GET_USER_ORGANIZATION_INFO);
					intent.putExtra("payload", message.toJsonString());
					mContext.sendBroadcast(intent);

					Log.i("nicsRest", "Successfully received user organization information.");
					mDataManager.addPersonalHistory("Successfully received user organization information.");
				}
			}

			@Override
			public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
			{

				if (error.getClass().equals(HttpResponseException.class))
				{
					HttpResponseException exception = (HttpResponseException) error;
					if (exception.getStatusCode() == 404)
					{
						Intent intent = new Intent();
						intent.setAction(Intents.nics_SUCCESSFUL_GET_USER_ORGANIZATION_INFO);
						mContext.sendBroadcast(intent);
					}
				}
				Log.i("nicsRest", "Failed to receive user organization information.");
			}
		});
	}

	public static void getActiveAssignment (final String username, final long userId)
	{
		mAuthManager.getClient().get("assignments?username=" + username + "&activeOnly=true", new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";

				AssignmentMessage message = mBuilder.create().fromJson(content, AssignmentMessage.class);
				message.parse();

				AssignmentPayload currentAssignment = null;

				if (message.getCount() == 1)
				{
					currentAssignment = message.getTaskingAssignmentsList().get(0);
					boolean isCurrent = mDataManager.getCurrentAssignment().equals(currentAssignment);

					if (!isCurrent)
					{
						getIncident(currentAssignment.getPhiUnit().getIncidentId(), false, userId, true);
					}
					else
					{
						getIncident(currentAssignment.getPhiUnit().getIncidentId(), false, userId, false);

						Intent intent = new Intent();
						intent.setAction(Intents.nics_UPDATE_ASSIGNMENT_RECEIVED);
						intent.putExtra("payload", message.toJsonString());
						mContext.sendBroadcast(intent);

					}

//					mDataManager.setCurrentAssignmentData(currentAssignment, isCurrent);

				}
				else
				{
					currentAssignment = new AssignmentPayload();

					boolean isCurrent = mDataManager.getCurrentAssignment().equals(currentAssignment);
					if (!isCurrent)
					{
//						mDataManager.setCurrentAssignmentData(null, false);
					}

					if (firstRun || !mDataManager.getCurrentAssignment().getPhiOperationalPeriod().equals(currentAssignment.getPhiOperationalPeriod()))
					{
						getIncident(-1, true, userId, true);
						firstRun = false;
					}
					else
					{
						if (!isCurrent)
						{
							getIncident(-1, false, userId, false);
						}
						else
						{
							Intent intent = new Intent();
							intent.setAction(Intents.nics_SUCCESSFUL_GET_INCIDENT_INFO);
							intent.putExtra("payload", message.toJsonString());
							mContext.sendBroadcast(intent);
						}
					}
				}

				mDataManager.setCurrentAssignment(currentAssignment);

				Log.i("nicsRest", "Successfully received active assignment information.");
			}

			@Override
			public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";

				Log.e("nicsRest", content);

				Intent intent = new Intent();
				intent.setAction(Intents.nics_FAILED_LOGIN);

				intent.putExtra("offlineMode", true);
				intent.putExtra("message", "Failed to connect to server: " + content + " - Please check your network connection.");
				mContext.sendBroadcast(intent);
			}
		});
	}

	public static void getAllIncidents (long userId)
	{
		mAuthManager.getClient().get("incidents/" + mDataManager.getWorkspaceId() + "?accessibleByUserId=" + userId, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";

				IncidentMessage message = mBuilder.create().fromJson(content, IncidentMessage.class);
				//FIXME: insert incidents in whatever order they were given to us by server
				HashMap<String, IncidentPayload> incidents = new HashMap<String, IncidentPayload>();
				for (IncidentPayload incident : message.getIncidents())
				{
					incidents.put(incident.getIncidentName(), incident);
				}
				mDataManager.setIncidents(incidents);

				EncryptedPreferences preferences;
				preferences = new EncryptedPreferences(mContext.getSharedPreferences(Constants.nics_USER_PREFERENCES, 0));
				preferences.savePreferenceString("savedIncidents", message.toJsonString());

				Intent intent = new Intent();
				intent.setAction(Intents.nics_SUCCESSFUL_GET_ALL_INCIDENT_INFO);
				intent.putExtra("payload", message.toJsonString());
				mContext.sendBroadcast(intent);

				Log.e("nicsRest", "Successfully received incident information.");
				mDataManager.addPersonalHistory("Successfully received incident information.");
			}

			@Override
			public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
			{

				String savedIncidents = mContext.getSharedPreferences(Constants.nics_USER_PREFERENCES, 0).getString(Constants.SAVED_INCIDENTS, null);

				if (savedIncidents != null)
				{

					IncidentMessage message = mBuilder.create().fromJson(savedIncidents, IncidentMessage.class);

					HashMap<String, IncidentPayload> incidents = new HashMap<String, IncidentPayload>();
					for (IncidentPayload incident : message.getIncidents())
					{
						if (incident.getCollabrooms().size() > 0)
						{
							incidents.put(incident.getIncidentName(), incident);
						}
					}
					mDataManager.setIncidents(incidents);

					Intent intent = new Intent();
					intent.setAction(Intents.nics_SUCCESSFUL_GET_ALL_INCIDENT_INFO);
					intent.putExtra("offlineMode", true);
					intent.putExtra("payload", message.toJsonString());
					mContext.sendBroadcast(intent);

					mDataManager.addPersonalHistory("Failed to receive incident information.");
				}

//		        intent.putExtra("message", "Failed to connect to server: " + content + " - Please check your network connection.");
			}
		});
	}

	public static void getIncident (final long incidentId, final boolean isWorkingMap, final long userId, final boolean sendAssignmentIntent)
	{
		mAuthManager.getClient().get("incidents/" + mDataManager.getWorkspaceId() + "/" + incidentId, new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";

				IncidentMessage message = mBuilder.create().fromJson(content, IncidentMessage.class);
				IncidentPayload incident = message.getIncidents().get(0);

				long collabRoomId = -1;
				String collabRoomName = "";
				if (isWorkingMap)
				{
					for (CollabroomPayload payload : incident.getCollabrooms())
					{
						if (payload.getName().contains("WorkingMap"))
						{
							Log.i("nicsRest", "Assigned to: " + payload.getName());
							collabRoomId = payload.getCollabRoomId();
							collabRoomName = payload.getName();
							payload.setIncidentId(incidentId);
							mDataManager.addCollabroom(payload);
							if (sendAssignmentIntent)
							{
								mDataManager.setCurrentIncidentData(null, collabRoomId, collabRoomName);
								//mDataManager.setSelectedCollabRoom(collabRoomName, collabRoomId);
							}
							break;
						}
					}
				}
				else
				{
					collabRoomName = mDataManager.getSelectedCollabRoom().getName();

					if (collabRoomName == null || collabRoomName.isEmpty())
					{
						collabRoomName = mDataManager.getActiveCollabroomName();
					}
				}
				mDataManager.setCurrentIncidentData(null, collabRoomId, collabRoomName);

				if (userId != -1 && sendAssignmentIntent && !isWorkingMap)
				{
					mDataManager.clearCollabRoomList();
				}
//				getUserCollabrooms(incident.getIncidentId(), userId, false);

				Intent intent = new Intent();
				intent.setAction(Intents.nics_SUCCESSFUL_GET_INCIDENT_INFO);
				intent.putExtra("payload", message.toJsonString());
				mContext.sendBroadcast(intent);

				if (sendAssignmentIntent)
				{
					Intent assignmentIntent = new Intent();
					assignmentIntent.setAction(Intents.nics_NEW_ASSIGNMENT_RECEIVED);
					if (incidentId == -1)
					{
						assignmentIntent.putExtra("clear-task", true);

						OperationalUnit unit = new OperationalUnit();
						unit.setCollabroomId(mDataManager.getActiveCollabroomId());
						unit.setCollabroomName(mDataManager.getActiveCollabroomName());
						unit.setIncidentId(mDataManager.getActiveIncidentId());
						unit.setIncidentName(mDataManager.getActiveIncidentName());
						AssignmentPayload payload = mDataManager.getCurrentAssignment();
						payload.setPhiUnit(unit);
						mDataManager.setCurrentAssignment(payload);
					}

					AssignmentPayload payload = mDataManager.getCurrentAssignment();

					if (mDataManager.getPreviousIncidentId() != payload.getPhiUnit().getIncidentId() && mDataManager.getPreviousCollabroom().getCollabRoomId() != payload.getPhiUnit().getCollabroomId())
					{
						NotificationsHandler.getInstance(mContext).createAssignmentChangeNotification(payload);
					}

					getSimpleReports(-1, -1, incidentId);

//					if(collabRoomName.contains("-")) {
//						getChatHistory(incident.getIncidentName(), collabRoomName.split("-")[1]);
//						getMarkupHistory(collabRoomId);
//					}

					mContext.sendBroadcast(assignmentIntent);
				}

				Log.i("nicsRest", "Successfully received incident information.");
			}

			@Override
			public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";

				Log.e("nicsRest", content);

				Intent intent = new Intent();
				intent.setAction(Intents.nics_FAILED_LOGIN);

				intent.putExtra("offlineMode", true);
				intent.putExtra("message", "Failed to connect to server: " + content + " - Please check your network connection.");
				mContext.sendBroadcast(intent);
			}
		});
	}

	public static void getCollabRooms (final long incidentId, final String incidentName)
	{
		Log.e("nicsRest", "requesting collabrooms for " + incidentName);
		Intent intent = new Intent();
		intent.setAction(Intents.nics_POLLING_COLLABROOMS);
		mContext.sendBroadcast(intent);
		mAuthManager.getClient().get("collabroom/" + incidentId + "?userId=" + mDataManager.getUserId(), new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";

				Log.e("nicsRest", "successfully pulled rooms for " + incidentName + " with code " + statusCode);

				CollaborationRoomMessage message = mBuilder.create().fromJson(content, CollaborationRoomMessage.class);

				HashMap<String, CollabroomPayload> rooms = new HashMap<String, CollabroomPayload>();
				for (CollabroomPayload room : message.getresults())
				{
					rooms.put(room.getName(), room);

					room.setName(room.getName().replace(mDataManager.getActiveIncidentName() + "-", ""));
					mDataManager.addCollabroom(room);
				}
				//		ArrayList<CollabroomPayload> roomList =  new ArrayList<CollabroomPayload>(rooms.values());

				mDataManager.setCollabRoomsForIncident(incidentName, message.getresults());

				Intent intent = new Intent();
				intent.setAction(Intents.nics_SUCCESSFULLY_GET_COLLABROOMS);
				mContext.sendBroadcast(intent);

			}

			@Override
			public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
			{
				Log.e("nicsRest", "failed to pull collabrooms.");
				Intent intent = new Intent();
				intent.setAction(Intents.nics_FAILED_GET_COLLABROOMS);
				mContext.sendBroadcast(intent);

			}
		});
	}

	public static void getSimpleReports (int offset, int limit, final long incidentId)
	{
		if (!mFetchingSimpleReports && mParseSimpleReportsTask == null && incidentId != -1)
		{
			String url = "reports/" + mDataManager.getActiveIncidentId() + "/SR?sortOrder=desc&fromDate=" + (mDataManager.getLastSimpleReportTimestamp() + 1);

//			if(incidentId != -1) {
//				url += "&incidentId=" + incidentId;
//			}

			mFetchingSimpleReports = true;

			mAuthManager.getClient().get(url, new AsyncHttpResponseHandler()
			{
				@Override
				public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
				{
					String content = (responseBody != null) ? new String(responseBody) : "error";
					SimpleReportMessage message = mBuilder.create().fromJson(content, SimpleReportMessage.class);

					if (message != null)
					{
						ArrayList<SimpleReportPayload> srPayloads = message.getReports();

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						{
							mParseSimpleReportsTask = new ParseSimpleReportsTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, srPayloads);
						}
						else
						{
							mParseSimpleReportsTask = new ParseSimpleReportsTask(mContext).execute(srPayloads);
						}
						Log.i("nicsRest", "Successfully received simple report information.");
					}
					mFetchingSimpleReports = false;
				}

				@Override
				public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
				{
					mFetchingSimpleReports = false;
				}
			});
		}
	}

	public static void getReportOnConditions (final long incidentId)
	{
		if (!mFetchingReportOnConditions && mParseReportOnConditionsTask == null && incidentId != -1)
		{
			String url = "reports/" + mDataManager.getActiveIncidentId() + "/SR?sortOrder=desc&fromDate=" + (mDataManager.getLastSimpleReportTimestamp() + 1);

			mFetchingReportOnConditions = true;

			mAuthManager.getClient().get(url, new AsyncHttpResponseHandler()
			{
				@Override
				public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
				{
					String content = (responseBody != null) ? new String(responseBody) : "error";
					ReportOnConditionMessage message = mBuilder.create().fromJson(content, ReportOnConditionMessage.class);

					if (message != null)
					{
						ArrayList<ReportOnConditionPayload> rocPayloads = message.getReports();

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						{
							mParseReportOnConditionsTask = new ParseReportOnConditionsTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, rocPayloads);
						}
						else
						{
							mParseReportOnConditionsTask = new ParseReportOnConditionsTask(mContext).execute(rocPayloads);
						}
						Log.i("nicsRest", "Successfully received simple report information.");
					}

					mFetchingReportOnConditions = false;
				}

				@Override
				public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
				{
					mFetchingReportOnConditions = false;
				}
			});
		}
	}

	public static void getChatHistory (final long incidentId, final long collabRoomId)
	{
		if (!mFetchingChatMessages && mParseChatMessagesTask == null && incidentId != -1 && collabRoomId != -1)
		{

			mFetchingChatMessages = true;
			mAuthManager.getClient().get("chatmsgs/" + collabRoomId + "?sortOrder=desc&fromDate=" + (mDataManager.getLastChatTimestamp(incidentId, collabRoomId) + 1) + "&dateColumn=created", new AsyncHttpResponseHandler()
			{

				@Override
				public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
				{
					String content = (responseBody != null) ? new String(responseBody) : "error";
					ChatMessage message = mBuilder.create().fromJson(content, ChatMessage.class);

					try
					{
						JSONObject jObject = new JSONObject(content);
						ArrayList<ChatPayload> chatMessages = message.getChatMsgs();

						for (int i = 0; i < message.getCount(); i++)
						{
							chatMessages.get(i).setIncidentId(incidentId);
							chatMessages.get(i).setuserId(jObject.getJSONArray("chats").getJSONObject(i).getJSONObject("userorg").getJSONObject("user").getLong("userId"));
							chatMessages.get(i).setUserOrgName(jObject.getJSONArray("chats").getJSONObject(i).getJSONObject("userorg").getJSONObject("org").getString("name"));
							chatMessages.get(i).setNickname(jObject.getJSONArray("chats").getJSONObject(i).getJSONObject("userorg").getJSONObject("user").getString("username"));
						}
					}
					catch (JSONException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (message != null)
					{
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						{
							mParseChatMessagesTask = new ParseChatMessagesTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, message.getChatMsgs());
						}
						else
						{
							mParseChatMessagesTask = new ParseChatMessagesTask(mContext).execute(message.getChatMsgs());
						}
						Log.i("nicsRest", "Successfully received chat information for: " + incidentId + "-" + collabRoomId);
					}
					mFetchingChatMessages = false;
				}

				@Override
				public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
				{
					String content = (responseBody != null) ? new String(responseBody) : "error";

					Log.e("nicsRest", "Failed to receive chat information for: " + incidentId + "-" + collabRoomId);
					Log.e("nicsRest", content + " " + error.getLocalizedMessage());
					mFetchingChatMessages = false;
				}
			});
		}
	}

	public static void getMarkupHistory (final long collabRoomId)
	{
		if (collabRoomId != -1)
		{
			if (!mFetchingMarkupFeatures && mParseMarkupFeaturesTask == null && collabRoomId != -1)
			{
				mFetchingMarkupFeatures = true;
				mAuthManager.getClient().get("features/collabroom/" + collabRoomId + "?geoType=4326&userId=" + mDataManager.getUserId() + "&fromDate=" + (mDataManager.getLastMarkupTimestamp(collabRoomId) + 1) + "&dateColumn=seqtime", new AsyncHttpResponseHandler()
				{

					@Override
					public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
					{
						String content = (responseBody != null) ? new String(responseBody) : "error";
						MarkupMessage message = mBuilder.create().fromJson(content, MarkupMessage.class);

						for (int i = 0; i < message.getFeatures().size(); i++)
						{
							message.getFeatures().get(i).buildVector2Point(true);
						}

						MarkupPayload payload = new MarkupPayload();
						payload.setFeatures(message.getFeatures());
						payload.setDeletedFeatures(message.getDeletedFeature());
						payload.setCollabRoomId(collabRoomId);

						if (message != null)
						{
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
							{
								mParseMarkupFeaturesTask = new ParseMarkupFeaturesTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, payload);
							}
							else
							{
								mParseMarkupFeaturesTask = new ParseMarkupFeaturesTask(mContext).execute(payload);
							}
							Log.i("nicsRest", "Successfully received markup information.");
						}
						mFetchingMarkupFeatures = false;
					}

					@Override
					public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
					{
						String content = (responseBody != null) ? new String(responseBody) : "error";

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						{
							mParseMarkupFeaturesTask = new ParseMarkupFeaturesTask(mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (MarkupPayload[]) null);
						}
						else
						{
							mParseMarkupFeaturesTask = new ParseMarkupFeaturesTask(mContext).execute((MarkupPayload[]) null);
						}
						Log.i("nicsRest", "Failed to receive markup history: " + content);
						mFetchingMarkupFeatures = false;
					}
				});

			}
		}
	}

	public static void getWeatherUpdate (final double latitude, final double longitude)
	{
		if (!Double.isNaN(latitude) && !Double.isNaN(longitude))
		{
			mAuthManager.getClient().get("http://forecast.weather.gov/MapClick.php" + "?lat=" + latitude + "&lon=" + longitude + "&FcstType=json&lg=english", new AsyncHttpResponseHandler()
			{

				@Override
				public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
				{
					String content = (responseBody != null) ? new String(responseBody) : "error";

					try
					{
						WeatherPayload payload = mBuilder.create().fromJson(content, WeatherPayload.class);

						if (payload != null)
						{
							mDataManager.setWeather(payload);

							Log.i("nicsRest", "Successfully received weather information for: " + latitude + "," + longitude);
							Intent intent = new Intent();
							intent.setAction(Intents.nics_NEW_WEATHER_REPORT_RECEIVED);
							intent.putExtra("payload", payload.toJsonString());
							mContext.sendBroadcast(intent);
						}
					}
					catch (Exception e)
					{
						Log.e("nicsRest", "Failed to load weather information for: " + latitude + "/" + longitude);
					}
				}

				@Override
				public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
				{
					Log.e("nicsRest", "Failed to load weather information for: " + latitude + "/" + longitude);

				}
			});
		}
	}

	public static void getWFSLayers ()
	{

		mAuthManager.getClient().get("datalayer/" + mDataManager.getWorkspaceId() + "/tracking", new AsyncHttpResponseHandler()
		{

			@Override
			public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";
				TrackingLayerMessage message = mBuilder.create().fromJson(content, TrackingLayerMessage.class);
				Log.i("nicsRest", "Succesfully received Tracking Layers: " + message.getCount());

				mDataManager.setTrackingLayers(message.getLayers());
			}

			@Override
			public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";
				Log.i("nicsRest", "Failed to receive Tracking Layers: " + content);

			}
		});
	}

	public static void getWFSData (final TrackingLayerPayload layer, int numResults, String mLastFeatureTimestamp, AsyncHttpResponseHandler responseHandler)
	{

//		AsyncHttpClient mClient = new AsyncHttpClient();
//		mClient.setTimeout(60 * 1000);
//		mClient.setURLEncodingEnabled(false);
//		mClient.setMaxRetriesAndTimeout(2, 1000);

		if (layer.getDatasourceid() == null)
		{    // no token needed, pull layer

			if (layer.shouldExpectJson())
			{
				Log.d("NICS REST", layer.getInternalurl() + "?service=WFS&outputFormat=json&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults);
				mAuthManager.getClient().get(layer.getInternalurl() + "?service=WFS&outputFormat=json&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults, responseHandler);
				return;
			}
			else
			{
				Log.d("NICS REST", layer.getInternalurl() + "?service=WFS&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults);
				mAuthManager.getClient().get(layer.getInternalurl() + "?service=WFS&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults, responseHandler);
				return;
			}
		}
		else
		{
			if (layer.getAuthtoken() == null)
			{    //get token for layer
				RestClient.getWFSDataToken(layer, numResults, mLastFeatureTimestamp, responseHandler);
			}
			else
			{    //already have token so pull layer
				if (layer.getAuthtoken().getExpires() <= System.currentTimeMillis())
				{    //token is expired so pull a new one
					RestClient.getWFSDataToken(layer, numResults, mLastFeatureTimestamp, responseHandler);
				}
				else
				{
					if (layer.getAuthtoken().getToken() != null)
					{

						if (layer.shouldExpectJson())
						{
							Log.d("NICS REST", layer.getInternalurl() + "?service=WFS&outputFormat=json&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults + "&token=" + layer.getAuthtoken().getToken());
							mAuthManager.getClient().get(layer.getInternalurl() + "?service=WFS&outputFormat=json&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults + "&token=" + layer.getAuthtoken().getToken(), responseHandler);
						}
						else
						{
							Log.d("NICS REST", layer.getInternalurl() + "?service=WFS&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults + "&token=" + layer.getAuthtoken().getToken());
							mAuthManager.getClient().get(layer.getInternalurl() + "?service=WFS&version=1.1.0&request=GetFeature&srsName=EPSG:4326&typeName=" + layer.getLayername() + "&maxFeatures=" + numResults + "&token=" + layer.getAuthtoken().getToken(), responseHandler);
						}
					}
				}
			}
		}
	}

	public static void getWFSDataToken (final TrackingLayerPayload layer, final int numResults, final String mLastFeatureTimestamp, final AsyncHttpResponseHandler responseHandler)
	{

		Log.d("NICS REST", "WFS Data Token: " + mDataManager.getServer() + "datalayer/1/token/" + layer.getDatasourceid());
		mAuthManager.getClient().get(mDataManager.getServer() + "datalayer/" + mDataManager.getWorkspaceId() + "/token/" + layer.getDatasourceid(), new AsyncHttpResponseHandler()
		{
			@SuppressWarnings("unchecked")

			@Override
			public void onSuccess (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";
				try
				{
					Log.d("NICS REST", "Successfully received WFS Data Token: " + content);
					TrackingTokenPayload token = mBuilder.create().fromJson(content, TrackingTokenPayload.class);

					if (token.getToken() == null)
					{
						token.setExpires(System.currentTimeMillis() + 120000);    //set not authorized token to expire in 2 minutes so rest client tries to pull it again later
					}
					else
					{
						RestClient.getWFSData(layer, numResults, mLastFeatureTimestamp, responseHandler);
					}

					layer.setAuthtoken(token);
					mDataManager.UpdateTrackingLayerData(layer);

				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure (int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error)
			{
				String content = (responseBody != null) ? new String(responseBody) : "error";
				Log.e("nicsRest", "Failed to authenticate WFS Layer: " + content);
			}
		});
	}

	public static cz.msebera.android.httpclient.Header[] getAuthData ()
	{
		return mAuthHeader;
	}

	public static void clearParseSimpleReportTask ()
	{
		if (mParseSimpleReportsTask != null)
		{
			mParseSimpleReportsTask.cancel(true);
			mParseSimpleReportsTask = null;
		}
	}

	public static void clearParseMarkupFeaturesTask ()
	{
		if (mParseMarkupFeaturesTask != null)
		{
			mParseMarkupFeaturesTask.cancel(true);
			mParseMarkupFeaturesTask = null;
		}
	}

	public static void clearParseReportOnConditionTask ()
	{
		if (mParseReportOnConditionsTask != null)
		{
			mParseReportOnConditionsTask.cancel(true);
			mParseReportOnConditionsTask = null;
		}
	}

	public static boolean isParsingMarkup ()
	{
		if (mParseMarkupFeaturesTask != null)
		{
			return true;
		}
		return false;
	}

	public static boolean isFetchingMarkup ()
	{
		return mFetchingMarkupFeatures;
	}

	public static void clearParseChatMessagesTask ()
	{
		if (mParseChatMessagesTask != null)
		{
			mParseChatMessagesTask.cancel(true);
			mParseChatMessagesTask = null;
		}
	}


	public static void postSimpleReports ()
	{
		ArrayList<SimpleReportPayload> simpleReports = mDataManager.getAllSimpleReportStoreAndForwardReadyToSend();

		if (mSimpleReportResponseHandlers == null)
		{
			mSimpleReportResponseHandlers = new SparseArray<SimpleReportResponseHandler>();
		}


		// Don't attempt to send simpleReports if AuthManager is null
		if (mAuthManager == null || mAuthManager.getClient() == null)
		{
			return;
		}

		for (SimpleReportPayload report : simpleReports)
		{
			if (!report.isDraft() && mSimpleReportResponseHandlers != null && mSimpleReportResponseHandlers.indexOfKey((int) report.getId()) < 0 && !mSendingSimpleReports)
			{
				Log.w("nics_POST", "Adding simple report " + report.getId() + " to send queue.");
				// Updating User Session ID
				report.setUserSessionId(mDataManager.getUserSessionId());
				SimpleReportData data = report.getMessageData();

				try
				{
					if (data.getFullpath() != null && data.getFullpath() != "")
					{

						SimpleReportResponseHandler handler = new SimpleReportResponseHandler(mContext, mDataManager, report.getId(), report.getUserSessionId());
						mSimpleReportResponseHandlers.put((int) report.getId(), handler);

						RequestParams params = new RequestParams();
						params.put("deviceId", mDeviceId);
						params.put("incidentId", String.valueOf(report.getIncidentId()));
						params.put("userId", String.valueOf(mDataManager.getUserId()));
						params.put("usersessionid", String.valueOf(report.getUserSessionId()));
						params.put("latitude", String.valueOf(data.getLatitude()));
						params.put("longitude", String.valueOf(data.getLongitude()));
						params.put("altitude", "0.0");
						params.put("track", "0.0");
						params.put("speed", "0.0");
						params.put("accuracy", "0.0");
						params.put("description", data.getDescription());
						params.put("category", data.getCategory() != null ? data.getCategory().getText() : SimpleReportCategoryType.BLANK.getText());
						params.put("seqtime", String.valueOf(report.getSeqTime()));

						params.put("image", new File(data.getFullpath()));
						mAuthManager.getClient().post("reports/" + mDataManager.getActiveIncidentId() + "/SR", params, handler);
						mSendingSimpleReports = true;

					}
					else
					{    //no image
						cz.msebera.android.httpclient.entity.StringEntity entity = new cz.msebera.android.httpclient.entity.StringEntity(report.toJsonString());
						SimpleReportNoImageResponseHandler responseHandler;
						responseHandler = new SimpleReportNoImageResponseHandler(mContext, mDataManager, report.getId(), report.getUserSessionId());
						mAuthManager.getClient().post("reports/" + mDataManager.getActiveIncidentId() + "/SR", entity, responseHandler);
						mSendingSimpleReports = true;
					}
				}
				catch (FileNotFoundException e)
				{
					Log.e("nicsRest", "Deleting: " + report.getId() + " success: " + mDataManager.deleteSimpleReportStoreAndForward(report.getId()) + " due to invalid file.");
					mDataManager.addPersonalHistory("Deleting simple report: " + report.getId() + " success: " + mDataManager.deleteSimpleReportStoreAndForward(report.getId()) + " due to invalid/missing image file.");
					mSendingSimpleReports = false;
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}


	public static void removeSimpleReportHandler (long reportId)
	{
		Log.w("nics_POST", "Removing simple report " + reportId + " from send queue.");
		mSimpleReportResponseHandlers.remove((int) reportId);
	}

	public static void postChatMessages ()
	{
		if (!mSendingChatMessages)
		{
			ArrayList<ChatPayload> chatMessages = mDataManager.getAllChatStoreAndForward();

			for (ChatPayload payload : chatMessages)
			{

//				payload.setUserorgid(mDataManager.getUserOrgId());
//				payload.setNickname(mDataManager.getUsername());
//				payload.setUserOrgName(null);

				try
				{
					cz.msebera.android.httpclient.entity.StringEntity entity = new cz.msebera.android.httpclient.entity.StringEntity(payload.toJsonString());
					String test = payload.toJsonString();
//	    			mAuthManager.getClient().post("chatmsgs/" + mDataManager.getWorkspaceId() + "/" + message.getIncidentId() + "/" + message.getcollabroomid(), entity, new ChatResponseHandler(mDataManager, chatMessages));
					mAuthManager.getClient().post("chatmsgs/" + payload.getcollabroomid(), entity, new ChatResponseHandler(mDataManager, chatMessages));

					mSendingChatMessages = true;
				}
				catch (UnsupportedEncodingException e)
				{

				}
			}
		}
	}

	public static void postMDTs ()
	{

		ArrayList<MobileDeviceTrackingPayload> mdtMessages = mDataManager.getAllMDTStoreAndForward();
		MobileDeviceTrackingPayload message = mdtMessages.get(mdtMessages.size() - 1);

//		for (MobileDeviceTrackingPayload message : mdtMessages) {
		try
		{
			if (message.getDeviceId() != null && !message.getDeviceId().isEmpty())
			{
				cz.msebera.android.httpclient.entity.StringEntity entity = new cz.msebera.android.httpclient.entity.StringEntity(message.toJsonString());

				mAuthManager.getClient().post("mdtracks", entity, new MDTResponseHandler(mDataManager, mdtMessages));
			}
			else
			{
				// invalid mdt due to lack of device id, so delete it
				mDataManager.deleteMDTStoreAndForward(message.getId());
			}
		}
		catch (UnsupportedEncodingException e)
		{

		}
//		}
	}

	public static void postMarkupFeatures ()
	{
		if (mDataManager.isOnline() && mDataManager.isLoggedIn())
		{
			if (!mSendingMarkupFeatures)
			{
				ArrayList<MarkupFeature> features = mDataManager.getAllMarkupFeaturesStoreAndForwardReadyToSend();

				if (features.size() > 0)
				{
					try
					{
						cz.msebera.android.httpclient.entity.StringEntity entity = new cz.msebera.android.httpclient.entity.StringEntity(features.get(0).toJsonStringWithWebLonLat());
						String testDebug = features.get(0).toJsonStringWithWebLonLat();

						mAuthManager.getClient().post("features/collabroom/" + features.get(0).getCollabRoomId() + "?geoType=4326", entity, new MarkupResponseHandler(mContext, mDataManager, features));
						mSendingMarkupFeatures = true;
					}
					catch (UnsupportedEncodingException e)
					{

					}
				}
			}
		}
	}

	public static void getGoogleMapsLegalInfo (AsyncHttpResponseHandler responseHandler)
	{
		AsyncHttpClient mClient = new AsyncHttpClient();
		mClient.setTimeout(60 * 1000);
		mClient.setURLEncodingEnabled(false);
		mClient.setMaxRetriesAndTimeout(2, 1000);
		mClient.get("http://www.google.com/mobile/legalnotices/", responseHandler);
	}

	public static String getDeviceId ()
	{
		return mDeviceId;
	}

	public static void setSendingSimpleReports (boolean isSending)
	{
		mSendingSimpleReports = isSending;
		if (mDataManager.isOnline())
		{
			postSimpleReports();
		}
	}

	public static void setSendingChatMessages (boolean isSending)
	{
		mSendingChatMessages = isSending;
	}

	public static void setSendingMarkupFeatures (boolean isSending)
	{
		mSendingMarkupFeatures = isSending;
	}

	public static void setDataManager (DataManager mInstance)
	{
		mDataManager = mInstance;
	}
}
