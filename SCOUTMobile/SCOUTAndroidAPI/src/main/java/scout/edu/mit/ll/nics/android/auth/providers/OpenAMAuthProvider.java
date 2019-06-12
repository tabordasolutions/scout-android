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
package scout.edu.mit.ll.nics.android.auth.providers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.ClientParamsStack;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.conn.ClientConnectionManager;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLContextBuilder;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.impl.client.BasicCookieStore;

import android.content.Intent;
import android.os.Looper;
import android.provider.ContactsContract;
import android.util.Log;
import android.content.Context;

import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import cz.msebera.android.httpclient.entity.mime.Header;
import cz.msebera.android.httpclient.impl.cookie.BasicClientCookie;
import cz.msebera.android.httpclient.params.HttpParams;
import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.R;
import scout.edu.mit.ll.nics.android.api.data.OpenAMAuthenticationData;
import scout.edu.mit.ll.nics.android.api.handlers.OpenAMAuthResponseHandler;
import scout.edu.mit.ll.nics.android.auth.AuthManager;
import scout.edu.mit.ll.nics.android.auth.AuthProvider;
import scout.edu.mit.ll.nics.android.utils.Intents;

public class OpenAMAuthProvider extends AuthProvider {

	private String mToken;
	private boolean mTokenIsValid;
	private boolean mIsAuthenticating;
	private GsonBuilder mBuilder;

	private Context mContext;

	private boolean debugIgnoreOpenAmAuth = false;

	public OpenAMAuthProvider( Context context) {
		super();
		mContext = context;
		mBuilder = new GsonBuilder();
		mClient.setURLEncodingEnabled(false);
		mClient.setMaxRetriesAndTimeout(2, 1000);

		// If we are using the staging or dev server, trust whatever SSL certificate is in use by the server.
		// (otherwise, self-signed certificates won't work)
		/*if(DataManager.ENVIRONMENT == DataManager.STAGING || DataManager.ENVIRONMENT == DataManager.DEV)
		{
			try
			{
				// Creating the certificate and loading it from the strings config file
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

				Certificate certificate;

				// Loading the certificate
				String certificateString = DataManager.getInstance().getContext().getResources().getString(R.string.config_staging_self_signed_cert);
				InputStream is = new ByteArrayInputStream(certificateString.getBytes(Charset.forName("UTF-8")));
				certificate = certificateFactory.generateCertificate(is);

				// Creating a keystore containing our trusted certificate
				String keyStoreType = KeyStore.getDefaultType();
				KeyStore keyStore = KeyStore.getInstance(keyStoreType);
				keyStore.load(null, null);
				keyStore.setCertificateEntry("ca", certificate);

				//-------------------------------------------------
				// Adding the self-signed cert to the async mClient
				//-------------------------------------------------

				// Set the async client's socket factory to one that uses the keystore containing the self-signed certificate
				mClient.setSSLSocketFactory(new cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory(keyStore));


				//-------------------------------------------------
				// Adding the self-signed cert to the mSyncClient
				//-------------------------------------------------

				SchemeRegistry schemeRegistry = new SchemeRegistry();
				schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
				schemeRegistry.register(new Scheme("https", new SSLSocketFactory(keyStore), 443));
				org.apache.http.params.HttpParams params = new BasicHttpParams();
				org.apache.http.conn.ClientConnectionManager connectionManager = new SingleClientConnManager(params, schemeRegistry);

				// Re-allocating the syncClient with a new connection manager that is set up to accept self-signed certificate
				mSyncClient = new DefaultHttpClient(connectionManager, params);

			}
			catch(Exception e)
			{
				Log.e("CERT","There was an error trusting the self-signed certificate for staging or dev environment.");
			}
		}*/




	}

	@Override
	public String getType() {
		return "OpenAM";
	}

	@Override
	public void setupAuth(String username, String password) {
		mDataManager.setUsername(username);
		mPassword = password;

		if(debugIgnoreOpenAmAuth){
			mIsAuthenticating = false;
			AuthManager.setRequestingAuth(false);
		}else{
			mIsAuthenticating = true;
			AuthManager.setRequestingAuth(true);
		}

		mLatch = new CountDownLatch(1);

		if(!debugIgnoreOpenAmAuth){
			String authToken = mDataManager.getAuthToken();
			if(authToken != null) {
				validateAuthToken(authToken, mPassword);
			} else {
				requestAuthToken(username, mPassword);
			}
		}
	}

	public void requestAuthToken(final String username, final String password) {
		//mClient.removeAllHeaders();
		mClient.addHeader("X-OpenAM-Username", username);
		mClient.addHeader("X-OpenAM-Password", password);
		mClient.addHeader("Content-Type", "application/json");

		Log.e("USIDDEFECTrq","Requesting Auth Token from OpenAM:");
		Log.e("USIDDEFECTrq","requestAuthToken URL: " + mDataManager.getAuthServerURL() + "json/authenticate");
		Log.e("USIDDEFECTrq","requestAuthToken Header: " + " X-OpenAM-Username: " + username);
		Log.e("USIDDEFECTrq","requestAuthToken Header: " + " Content-Type: " + "application/json");


		mClient.post(mDataManager.getAuthServerURL() + "json/authenticate", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
				// Removing the username / password headers.
				mClient.removeHeader("X-OpenAM-Username");
				mClient.removeHeader("X-OpenAM-Password");

				String content = (responseBody != null) ? new String(responseBody) : "error";
				Log.e("USIDDEFECTrq","requestAuthToken Response: " + content);


				OpenAMAuthenticationData authData = mBuilder.create().fromJson(content, OpenAMAuthenticationData.class);

				if(authData.getErrorMessage() == null) {

					Log.d("nicsRest","" + authData.getTokenId());

					mToken = authData.getTokenId();
					mTokenIsValid = true;
					mDataManager.setAuthToken(mToken);
					setAuthCookies();

					mIsAuthenticating = false;
					AuthManager.setRequestingAuth(false);
					mLatch.countDown();
					Log.e("nics_AUTH", content);
				} else {
					onFailure(statusCode, headers, responseBody, null);
				}
			}

			@Override
			public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
				// Removing the username / password headers.
				mClient.removeHeader("X-OpenAM-Username");
				mClient.removeHeader("X-OpenAM-Password");

				String content = (responseBody != null) ? new String(responseBody) : "error";
				clearAuthCookies();

				mIsAuthenticating = false;
				AuthManager.setRequestingAuth(false);
				Log.e("nics_AUTH", content);

				Intent intent = new Intent();
				intent.setAction(Intents.nics_FAILED_LOGIN);

				if(error.getClass() == HttpResponseException.class) {
					HttpResponseException exception = (HttpResponseException)error;

					if(exception.getStatusCode() == 401) {
						intent.putExtra("message", "Invalid username or password");
					} else {
						intent.putExtra("message", exception.getMessage());
					}
				} else {
					Log.e("nicsRest", error.getMessage());
					intent.putExtra("offlineMode", true);

					if(error.getClass() == UnknownHostException.class) {
						intent.putExtra("message", "Failed to connect to server. Please check your network connection.");
					} else {
						intent.putExtra("message", error.getMessage());
					}
					error.printStackTrace();
				}
				mContext.sendBroadcast(intent);

				if(intent.getExtras() != null) {
					mDataManager.addPersonalHistory("User " + username + " login failed: " + intent.getExtras().get("message"));
				} else {
					mDataManager.addPersonalHistory("User " + username + " login failed.");
				}


				mLatch.countDown();
			}
		});
	}

	public void validateAuthToken(final String tokenId, final String password) {
		try {
			mClient.get(mDataManager.getAuthServerURL() + "identity/isTokenValid?tokenid=" + URLEncoder.encode(tokenId, "UTF-8"), new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					Log.e("nics_AUTH", content);

					String temp = content.split("=")[1];
					temp = temp.substring(0, temp.length() - 1);
					mTokenIsValid = Boolean.parseBoolean(temp);

					if(!mTokenIsValid) {
						clearAuthCookies();
						requestAuthToken(mDataManager.getUsername(), password);
					} else {
						Log.d("nicsRest","" + tokenId);

						mToken = tokenId;
						setAuthCookies();
						mIsAuthenticating = false;
						AuthManager.setRequestingAuth(false);
						mLatch.countDown();
					}
				}

				@Override
				public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
					String content = (responseBody != null) ? new String(responseBody) : "error";
					Log.e("nics_AUTH", content);

					clearAuthCookies();
					mIsAuthenticating = false;
					AuthManager.setRequestingAuth(false);
					mLatch.countDown();
				}
			});
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean setAuthCookies() {

		if(mTokenIsValid && mToken != null) {
			BasicCookieStore cookieStore = new BasicCookieStore();

			BasicClientCookie iPlanetDirectoryProCookie = new BasicClientCookie("iPlanetDirectoryPro", mToken);
			iPlanetDirectoryProCookie.setPath("/");
			iPlanetDirectoryProCookie.setDomain(mDataManager.getIplanetCookieDomain());

			BasicClientCookie AMAuthCookie = new BasicClientCookie("AMAuthCookie", mToken);
			AMAuthCookie.setPath("/");
			AMAuthCookie.setDomain(mDataManager.getAmAuthCookieDomain());

			cookieStore.addCookie(iPlanetDirectoryProCookie);
			cookieStore.addCookie(AMAuthCookie);

			mClient.setCookieStore(cookieStore);

			return true;
		}

		mClient.setCookieStore(null);

		return false;
	}

	public void clearAuthCookies() {
		mToken = null;
		mTokenIsValid = false;
		mClient.setCookieStore(null);
		mDataManager.setAuthToken(null);
	}

	public void get(final String url, final AsyncHttpResponseHandler responseHandler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					if(mIsAuthenticating) {
						if(mLatch == null || mLatch.getCount() == 0) {
							mLatch = new CountDownLatch(1);
						}
						mLatch.await();
					}


					mClient.addHeader("AMAuthCookie", mToken);
					mClient.addHeader("iPlanetDirectoryPro", mToken);
					mClient.addHeader("CUSTOM-uid",mDataManager.getUsername());
					mClient.addHeader("Content-Type", "application/json");
					mClient.get(null, getAbsoluteUrl(url), new OpenAMAuthResponseHandler(responseHandler, Looper.myLooper()));
				} catch(InterruptedException e) {

				}
				Looper.loop();
			}
		}).start();
	}

	public void getWithoutCredentials(Context context,final String url, final AsyncHttpResponseHandler responseHandler) {

	}

	public void post(final String url, final cz.msebera.android.httpclient.entity.StringEntity entity, final AsyncHttpResponseHandler responseHandler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {




					if(mIsAuthenticating) {
						if(mLatch == null || mLatch.getCount() == 0) {
							mLatch = new CountDownLatch(1);

						}
						mLatch.await();
					}

					if(debugIgnoreOpenAmAuth){
						mTokenIsValid = true;
					}


					if(mTokenIsValid) {
						//mClient.removeAllHeaders();
						mClient.addHeader("AMAuthCookie", mToken);
						String cookie = "iPlanetDirectoryPro=" + mToken + ";AMAuthCookie=" + mToken;
						mClient.addHeader("Cookie",cookie);
						//mClient.addHeader("iPlanetDirectoryPro", mToken);
						mClient.addHeader("CUSTOM-uid",mDataManager.getUsername());
						mClient.addHeader("Content-Type", "application/json");

						mClient.setEnableRedirects(true, true, true);

						Log.e("test","Authentication token: " + mToken);


						mClient.post(null, getAbsoluteUrl(url) , entity, "application/json", new OpenAMAuthResponseHandler(responseHandler, Looper.myLooper()));

					}

				} catch(InterruptedException e) {

				}

				Looper.loop();
			}
		}).start();
	}

	public void post(final String url, final RequestParams params, final AsyncHttpResponseHandler responseHandler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					if(mIsAuthenticating) {
						if(mLatch == null || mLatch.getCount() == 0) {
							mLatch = new CountDownLatch(1);
						}
						mLatch.await();
					}

					if(debugIgnoreOpenAmAuth){mTokenIsValid = true;};

					if(mTokenIsValid) {
						mClient.addHeader("AMAuthCookie", mToken);
						mClient.addHeader("iPlanetDirectoryPro", mToken);
						mClient.addHeader("CUSTOM-uid",mDataManager.getUsername());
						mClient.addHeader("Content-Type", "multipart/form-data");

						mClient.setEnableRedirects(true, true, true);
						mClient.post(null, getAbsoluteUrl(url), null, params, "multipart/form-data", new OpenAMAuthResponseHandler(responseHandler, Looper.myLooper()));
					}
				} catch(InterruptedException e) {

				}

				Looper.loop();
			}
		}).start();
	}

	public void delete(final String url, final AsyncHttpResponseHandler responseHandler) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				try {
					if(mIsAuthenticating) {
						if(mLatch == null || mLatch.getCount() == 0) {
							mLatch = new CountDownLatch(1);
						}
						mLatch.await();
					}
					if(debugIgnoreOpenAmAuth){mTokenIsValid = true;};

					if(mTokenIsValid)
					{
						mClient.addHeader("AMAuthCookie", mToken);
						mClient.addHeader("iPlanetDirectoryPro", mToken);
						mClient.addHeader("CUSTOM-uid",mDataManager.getUsername());
						mClient.addHeader("Content-Type", "application/json");
						mClient.delete(null, getAbsoluteUrl(url), null, new OpenAMAuthResponseHandler(responseHandler, Looper.myLooper()));
					}
				} catch(InterruptedException e) {

				}

				Looper.loop();
			}
		}).start();
	}

	// Performs a synchronous HTTP get request and returns the result
	public HttpResponse syncGet(String url)
	{
		Log.e("ROC", "OpenAMAuthProvider - About to make a get request to: \"" + getAbsoluteUrl(url) + "\"");
		HttpGet request = new HttpGet(getAbsoluteUrl(url));

		//request.removeAllHeaders();

		String cookie = "iPlanetDirectoryPro=" + mToken + ";AMAuthCookie=" + mToken;
		request.addHeader("Cookie", cookie);
		request.addHeader("Content-Type", "application/json");

		//request.addHeader("AMAuthCookie", mToken);
		//request.addHeader("iPlanetDirectoryPro", mToken);
		//request.addHeader("CUSTOM-uid",mDataManager.getUsername());
		//request.addHeader("Content-Type", "application/json");

		HttpResponse response = null;
		try
		{
			response = mSyncClient.execute(request);
		}
		catch (Exception e)
		{
			Log.e("ROC", "OpenAMAuthProvider - Exception occured: " + e);
		}
		Log.e("ROC", "OpenAMAuthProvider - Response object: \"" + response + "\"");


		return response;
	}



}