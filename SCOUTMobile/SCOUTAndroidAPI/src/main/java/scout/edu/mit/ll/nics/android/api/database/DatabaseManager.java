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
package scout.edu.mit.ll.nics.android.api.database;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sqlcipher.database.SQLiteDatabase;
//import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;
//import net.sqlcipher.database.SQLiteContentHelper;

import net.sqlcipher.DatabaseUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
//import android.database.DatabaseUtils;
import android.os.Build;
import android.util.Log;

import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.database.tables.ChatTable;
import scout.edu.mit.ll.nics.android.api.database.tables.MarkupTable;
import scout.edu.mit.ll.nics.android.api.database.tables.MobileDeviceTrackingTable;
import scout.edu.mit.ll.nics.android.api.database.tables.SimpleReportTable;
import scout.edu.mit.ll.nics.android.api.payload.ChatPayload;
import scout.edu.mit.ll.nics.android.api.payload.MobileDeviceTrackingPayload;
import scout.edu.mit.ll.nics.android.api.payload.forms.SimpleReportPayload;
import scout.edu.mit.ll.nics.android.utils.AesCbcWithIntegrity;
import scout.edu.mit.ll.nics.android.utils.Constants;

/**
 * @author Glenn L. Primmer
 *         <p>
 *         The database manager is responsible for handling all transactions to the SQLite database that is used for either
 *         persistent data, or store and forward data.
 */
public class DatabaseManager extends SQLiteOpenHelper
{
	/**
	 * Table responsible for storing the mobile device tracking data that is required to be sent to the nics server.
	 */
	MobileDeviceTrackingTable mdtSendTable;

	/**
	 * Table responsible for storing all incoming chat data from the nics server.
	 * <p>
	 * Note: This table will include the messages that the user has sent out (IE was in the chat send table).
	 */
	ChatTable chatReceiveTable;
	ChatTable chatSendTable;

	ChatTable personalLogTable;    // Logs are stored as chat messages

	SimpleReportTable simpleReportSendTable;
	SimpleReportTable simpleReportReceiveTable;

	MarkupTable markupReceiveTable;
	MarkupTable markupSendTable;

	private Context mContext;

	String SB_PASSPHRASE = "";

	protected static SQLiteDatabase database;

	/**
	 * Constructor.
	 *
	 * @param context Android application context.
	 */
	public DatabaseManager(final Context context)
	{
		super(context, Constants.nics_DATABASE_NAME, null,
				Constants.nics_DATABASE_VERSION);

		mContext = context;

		InitEncryptKey();
		SQLiteDatabase.loadLibs(mContext); //first init the db libraries with the context


		chatReceiveTable = new ChatTable("chatReceiveTable", context);
		chatSendTable = new ChatTable("chatSendTable", context);

		simpleReportSendTable = new SimpleReportTable("simpleReportSendTable", context);
		simpleReportReceiveTable = new SimpleReportTable("simpleReportReceiveTable", context);

		markupReceiveTable = new MarkupTable("markupReceiveTable", context);
		markupSendTable = new MarkupTable("markupSendTable", context);

		mdtSendTable = new MobileDeviceTrackingTable("mdtSendTable", context);
		personalLogTable = new ChatTable("personalLogTable", context);

		database = this.getDatabase();

	}

	/**
	 * Called when the database is created for the first time.  This is where the creation of tables and the initial
	 * population of the tables should happen.
	 *
	 * @param database The database.
	 */
	public void onCreate(final SQLiteDatabase database)
	{
		createDatabaseTables(database);
	}

	public void InitEncryptKey()
	{

		SharedPreferences mPreferences = mContext.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);
		SB_PASSPHRASE = mPreferences.getString(Constants.nics_USER_KEY, "");

		if (SB_PASSPHRASE.equals(""))
		{
			try
			{
				AesCbcWithIntegrity.SecretKeys key;
				key = AesCbcWithIntegrity.generateKey();
				SB_PASSPHRASE = AesCbcWithIntegrity.keyString(key);

				mPreferences.edit().putString(Constants.nics_USER_KEY, SB_PASSPHRASE).commit();

			} catch (GeneralSecurityException e)
			{
				Log.e("Security exception", "GeneralSecurityException", e);
			}
		}
	}

	/**
	 * Called when the database needs to be upgraded. The implementation should use this method to drop tables, add
	 * tables, or do anything else it needs to upgrade to the new schema version. The SQLite ALTER TABLE documentation
	 * can be found here. If you add new columns you can use ALTER TABLE to insert them into a live table. If you rename
	 * or remove columns you can use ALTER TABLE to rename the old table, then create the new table and then populate
	 * the new table with the contents of the old table. This method executes within a transaction. If an exception is
	 * thrown, all changes will automatically be rolled back.
	 *
	 * @param database   The database.
	 * @param oldVersion The old database version.
	 * @param newVersion The new database version.
	 */
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion)
	{
		Editor e = mContext.getSharedPreferences(Constants.nics_USER_PREFERENCES, Context.MODE_PRIVATE).edit();
		e.clear();
		e.commit();

		dropDatabaseTables(database);
		createDatabaseTables(database);
		Log.i("nics", "Cleared db and reset preferences.");
	}

	//    @Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		Editor e = mContext.getSharedPreferences(Constants.nics_USER_PREFERENCES, Context.MODE_PRIVATE).edit();
		e.clear();
		e.commit();

		dropDatabaseTables(database);
		createDatabaseTables(database);

		Log.i("nics", "Cleared db and reset preferences.");
	}

	/**
	 * Creates the database tables that are used in the nics mobile application for user settings, persistent data,
	 * as well as store and forward data.
	 */
	protected void createDatabaseTables(SQLiteDatabase database)
	{
		if (database != null)
		{
			chatReceiveTable.createTable(database);
			chatSendTable.createTable(database);
			personalLogTable.createTable(database);

			simpleReportSendTable.createTable(database);
			simpleReportReceiveTable.createTable(database);

			markupReceiveTable.createTable(database);
			markupSendTable.createTable(database);

			mdtSendTable.createTable(database);
		}
	}

	protected void dropDatabaseTables(SQLiteDatabase database)
	{
		if (database != null)
		{
			chatReceiveTable.dropTable(database);
			chatSendTable.dropTable(database);
			personalLogTable.dropTable(database);

			simpleReportSendTable.dropTable(database);
			simpleReportReceiveTable.dropTable(database);

			markupReceiveTable.dropTable(database);
			markupSendTable.dropTable(database);

			mdtSendTable.dropTable(database);
		}
	}

	/**
	 * Adds a chat that was received on the nics server to the internal database table.
	 *
	 * @param chatData The chat data item to add.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean addChatHistory(ChatPayload chatData)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (chatReceiveTable.addData(chatData, database) > 0L)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	/**
	 * Adds a chat message to personal log
	 *
	 * @param chatData The chat data item to add.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean addPersonalHistory(ChatPayload chatData)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (personalLogTable.addData(chatData, database) > 0L)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public ArrayList<ChatPayload> getRecentPersonalHistory()
	{
		ArrayList<ChatPayload> retValue = null;
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = personalLogTable.getDataForCollaborationRoom(-1, database);
		}

		return retValue;
	}

	/**
	 * Returns the last chat message timestamp received for a given collaboration room ID.
	 * <p>
	 * Note: If there are no messages for a provided collaboration room ID, then -1L is returned.
	 *
	 * @param collaborationRoomId The collaboration room ID.
	 * @return The last timestamp of chat history data that was received from the nics server.
	 */
	public long getLastChatHistoryTimestamp(long collaborationRoomId)
	{

		long retValue = -1L;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = chatReceiveTable.getLastDataForCollaborationRoomTimestamp(collaborationRoomId,
					database);
		}

		return retValue;
	}

	public ArrayList<ChatPayload> getRecentChatHistory(long collabRoomId)
	{
		ArrayList<ChatPayload> retValue = null;
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = chatReceiveTable.getDataForCollaborationRoom(collabRoomId, database);
		}

		return retValue;
	}

	public ArrayList<ChatPayload> getNewChatMessagesFromDate(long collabRoomId, long timestamp)
	{
		ArrayList<ChatPayload> retValue = null;
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = chatReceiveTable.getNewChatMessagesFromDate(collabRoomId, timestamp, database);
		}

		return retValue;
	}

	public ArrayList<ChatPayload> getRecentChatHistoryStartingFromAndGoingBack(long collabRoomId, long timestamp, String limit)
	{
		ArrayList<ChatPayload> retValue = null;
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = chatReceiveTable.getDataForCollaborationRoomStartingFromAndGoingBack(collabRoomId, timestamp, limit, database);
		}

		return retValue;
	}

	public ArrayList<ChatPayload> getChatStoreAndForwardReadyToSend(long collabRoomId)
	{
		ArrayList<ChatPayload> retValue = null;
		SQLiteDatabase database = getDatabase();
		if (database != null)
		{
			retValue = chatSendTable.getDataForCollaborationRoom(collabRoomId, database);
		}

		return retValue;
	}

	public ChatPayload getLastChatHistory(long collabRoomId)
	{
		ChatPayload retValue = null;
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = chatReceiveTable.getLastDataForCollaborationRoom(collabRoomId, database);
		}

		return retValue;
	}


	public long getLastSimpleReportTimestamp(long incidentId)
	{
		long retValue = -1L;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = simpleReportReceiveTable.getLastDataForIncidentTimestamp(incidentId, database);

		}

		return retValue;
	}

	public SimpleReportPayload getLastSimpleReportPayload(long incidentId)
	{
		SimpleReportPayload retValue = null;
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = simpleReportReceiveTable.getLastDataForIncidentId(incidentId, database);
		}

		return retValue;
	}

	/**
	 * Gets all the chat data that is in the store and forward database table entries.
	 *
	 * @return All the chat data that is in the store and forward database table entries.
	 */
	public ArrayList<ChatPayload> getAllChatStoreAndForward()
	{
		ArrayList<ChatPayload> retValue = null;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = chatSendTable.getAllData("lastupdated ASC",
					database);
		}

		return retValue;
	}

	public Map<String, ArrayList<ChatPayload>> getAllChatStoreAndForwardMappedByTopic()
	{
		Map<String, ArrayList<ChatPayload>> chatsMappedByTopic = new HashMap<String, ArrayList<ChatPayload>>();

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			ArrayList<ChatPayload> chats = chatSendTable.getAllData("lastupdated ASC",
					database);

			for (ChatPayload chat : chats)
			{
				String key = chat.getTopic();

				if (!chatsMappedByTopic.containsKey(key))
				{
					// No key, so setup the item.
					chatsMappedByTopic.put(key, new ArrayList<ChatPayload>());
				}

				chatsMappedByTopic.get(key).add(chat);
			}
		}

		return chatsMappedByTopic;
	}

	/**
	 * Deletes an entry from the chat store and forward database table.
	 *
	 * @param id The index into the table to delete.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean deleteChatStoreAndForward(long id)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (chatSendTable.deleteData(id, database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public boolean deleteChatStoreAndForwardSince(long timestamp)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (chatSendTable.deleteItemsSince(database, timestamp) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public boolean deleteAllChatStoreAndForward()
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (chatSendTable.deleteAllData(database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}


	public boolean deleteMobileDeviceTrackingStoreAndForwardSince(long timestamp)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (mdtSendTable.deleteItemsSince(database, timestamp) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	/**
	 * Gets all the mobile device tracking store and forward database table entries.
	 *
	 * @return All the mobile device tracking store and forward database table entries.
	 */
	public ArrayList<MobileDeviceTrackingPayload> getAllMobileDeviceTrackingStoreAndForward()
	{
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			return mdtSendTable.getAllData("lastUpdatedUTC ASC", database);
		}

		return null;
	}

	/**
	 * Deletes an entry from the mobile device tracking store and forward table.
	 *
	 * @param id The index into the table to delete.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean deleteMobileDeviceTrackingStoreAndForward(long id)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (mdtSendTable.deleteData(id, database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public ArrayList<SimpleReportPayload> getSimpleReportHistoryForIncident(long incidentId)
	{
		ArrayList<SimpleReportPayload> retValue = null;
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = simpleReportReceiveTable.getDataForIncident(incidentId, database);
		}

		return retValue;
	}

	/**
	 * Gets all the simple reports that are ready to send in the store and forward table.
	 *
	 * @return All the simple reports that are ready to send in the store and forward table.
	 */
	public ArrayList<SimpleReportPayload> getAllSimpleReportStoreAndForwardReadyToSend()
	{
		ArrayList<SimpleReportPayload> retValue = null;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = simpleReportSendTable.getAllDataReadyToSend(null, database);
		}

		return retValue;
	}

	/**
	 * Gets all the simple reports that are ready to send in the store and forward table.
	 *
	 * @return All the simple reports that are ready to send in the store and forward table.
	 */
	public ArrayList<SimpleReportPayload> getAllSimpleReportStoreAndForwardReadyToSend(long incidentId)
	{
		ArrayList<SimpleReportPayload> retValue = null;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = simpleReportSendTable.getAllDataReadyToSend(incidentId, database);
		}

		return retValue;
	}

	/**
	 * Gets all the simple reports that are ready to send in the store and forward table.
	 *
	 * @return All the simple reports that are ready to send in the store and forward table.
	 */
	public ArrayList<SimpleReportPayload> getAllSimpleReportStoreAndForwardHasSent()
	{
		ArrayList<SimpleReportPayload> retValue = null;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = simpleReportSendTable.getAllDataHasSent(null, database);
		}

		return retValue;
	}

	/**
	 * Gets all the simple reports that are ready to send in the store and forward table.
	 *
	 * @return All the simple reports that are ready to send in the store and forward table.
	 */
	public ArrayList<SimpleReportPayload> getAllSimpleReportStoreAndForwardHasSent(long incidentId)
	{
		ArrayList<SimpleReportPayload> retValue = null;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = simpleReportSendTable.getAllDataHasSent(incidentId, database);
		}

		return retValue;
	}

	public ArrayList<SimpleReportPayload> getSimpleReportFromStoreAndForward(int reportId)
	{
		ArrayList<SimpleReportPayload> retValue = null;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = simpleReportSendTable.getDataByReportId(reportId, database);
		}

		return retValue;
	}

	/**
	 * Deletes an entry from the simple report receive table.
	 *
	 * @param id The index into the table to delete.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean deleteSimpleReportHistory(long id)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (simpleReportReceiveTable.deleteData(id, database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	/**
	 * Deletes all entries from the simple report receive table that match a specified incident id.
	 *
	 * @param incidentId The incident id to delete by
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean deleteSimpleReportHistoryByIncident(long incidentId)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (simpleReportReceiveTable.deleteDataByIncident(incidentId, database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	/**
	 * Deletes an entry from the simple report store and forward table.
	 *
	 * @param id The index into the table to delete.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean deleteSimpleReportStoreAndForward(long id)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (simpleReportSendTable.deleteData(id, database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	/**
	 * Gets all the simple reports received from the server.
	 *
	 * @return All the simple reports received from the server.
	 */
	public ArrayList<SimpleReportPayload> getAllSimpleReportHistory()
	{
		ArrayList<SimpleReportPayload> retValue = null;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = simpleReportReceiveTable.getAllData("lastUpdatedUTC DESC", database);
		}

		return retValue;
	}


	//-----------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------------


	/**
	 * Adds a simple report to the simple report history table.
	 *
	 * @param data Simple report to add.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean addSimpleReportHistory(SimpleReportPayload data)
	{
		return (simpleReportReceiveTable.addData(data, getDatabase()) > 0);
	}

	/**
	 * Adds mobile data tracking information to the store and forward database table.
	 *
	 * @param data The MDT information.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean addMobileDeviceTrackingStoreAndForward(MobileDeviceTrackingPayload data)
	{
		return (mdtSendTable.addData(data, getDatabase()) > 0);
	}


	/**
	 * Adds a simple report to the store and forward database table.
	 *
	 * @param data The simple report to add to the store and forward database table.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean addSimpleReportToStoreAndForward(SimpleReportPayload data)
	{
		return (simpleReportSendTable.addData(data, getDatabase()) > 0);
	}

	/**
	 * Adds a chat message to the store and forward database table.
	 *
	 * @param data The chat message to add to the store and forward database table.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean addChatToStoreAndForward(ChatPayload data)
	{
		return (chatSendTable.addData(data, getDatabase()) > 0);
	}

	/**
	 * Adds a markup message to the store and forward database table.
	 *
	 * @param data The chat message to add to the store and forward database table.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean addMarkupToStoreAndForward(MarkupFeature data)
	{
		return (markupSendTable.addData(data, getDatabase()) > 0);
	}

	/**
	 * Gets all the markup data that is in the store and forward database table entries.
	 *
	 * @return All the chat data that is in the store and forward database table entries.
	 */
	public ArrayList<MarkupFeature> getAllMarkupStoreAndForward()
	{
		ArrayList<MarkupFeature> retValue = null;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = markupSendTable.getAllData("seqtime ASC", database);
		}

		return retValue;
	}

	/**
	 * Deletes an entry from the markup store and forward database table.
	 *
	 * @param id The index into the table to delete.
	 * @return True if the operation was successful, otherwise false.
	 */
	public boolean deleteMarkupStoreAndForward(long id)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (markupSendTable.deleteData(id, database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public long getMarkupOutputRowCount()
	{
		if (SB_PASSPHRASE.equals(""))
		{
			InitEncryptKey();
		}
		return DatabaseUtils.queryNumEntries(this.getReadableDatabase(SB_PASSPHRASE), "markupSendTable");
	}

	/**
	 * Returns the writable database.  This only gets a writable database if one has not already been looked up.
	 *
	 * @return The database.
	 */
	protected SQLiteDatabase getDatabase()
	{
		if (database == null)
		{
			if (SB_PASSPHRASE.equals(""))
			{
				InitEncryptKey();
			}
			database = this.getWritableDatabase(SB_PASSPHRASE);
		}

		return database;
	}

	public boolean deleteAllChatHistory()
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (chatReceiveTable.deleteAllData(database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public boolean deleteAllSimpleReportsHistory()
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (simpleReportReceiveTable.deleteAllData(database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public boolean deleteAllSimpleReportsStoreAndForward()
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (simpleReportSendTable.deleteAllData(database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public boolean deleteAllMarkupFeatureHistory()
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (markupReceiveTable.deleteAllData(database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public boolean deleteAllMarkupFeatureStoreAndForward()
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (markupSendTable.deleteAllData(database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public long getLastMarkupTimestamp(long collaborationRoomId)
	{

		long retValue = -1L;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = markupReceiveTable.getLastDataForCollaborationRoomTimestamp(collaborationRoomId, database);
		}

		return retValue;
	}

	public boolean addMarkupFeatureToHistory(MarkupFeature feature)
	{
		return (markupReceiveTable.addData(feature, getDatabase()) > 0);
	}

	public boolean addAllMarkupFeatureToHistory(ArrayList<MarkupFeature> featureSet)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			return (markupReceiveTable.addData(featureSet, getDatabase()) > 0);
		} else
		{
			SQLiteDatabase database = getDatabase();
			for (MarkupFeature feature : featureSet)
			{
				markupReceiveTable.addData(feature, database);
			}
			return true;
		}
	}

	public ArrayList<MarkupFeature> getAllMarkupFeatureHistory()
	{
		ArrayList<MarkupFeature> retValue = null;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = markupReceiveTable.getAllData("seqtime DESC", database);
		}

		return retValue;
	}

	public ArrayList<MarkupFeature> getMarkupHistoryForCollabroom(long collabroomId)
	{
		ArrayList<MarkupFeature> retValue = null;
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = markupReceiveTable.getDataForCollaborationRoom(collabroomId, database);
		}

		return retValue;
	}

	public ArrayList<MarkupFeature> getMarkupHistoryForCollabroomWithFeatureIds(long collabroomId, ArrayList<String> featureIds)
	{
		ArrayList<MarkupFeature> retValue = null;
		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			retValue = markupReceiveTable.getDataForCollaborationRoomByFeatureIds(collabroomId, featureIds, database);
		}

		return retValue;
	}

	public boolean deleteMarkupHistoryForCollabroom(long collabroomId)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (markupReceiveTable.deleteDataByCollabroom(collabroomId, database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public boolean deleteMarkupHistoryForCollabroomByFeatureIds(long collabroomId, ArrayList<String> featuresToRemove)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (markupReceiveTable.deleteDataByCollabroomByFeatureIds(collabroomId, featuresToRemove, database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public boolean deleteMarkupHistoryForCollabroomByFeatureId(long collabroomId, String featureToRemove)
	{
		boolean retValue = false;

		SQLiteDatabase database = getDatabase();

		if (database != null)
		{
			if (markupReceiveTable.deleteDataByCollabroomByFeatureId(collabroomId, featureToRemove, database) > 0)
			{
				retValue = true;
			}
		}

		return retValue;
	}

	public boolean addAllChatHistory(ArrayList<ChatPayload> payloads)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		{
			return (chatReceiveTable.addData(payloads, getDatabase()) > 0);
		} else
		{
			SQLiteDatabase database = getDatabase();
			for (ChatPayload payload : payloads)
			{
				chatReceiveTable.addData(payload, database);
			}
			return true;
		}
	}
}
