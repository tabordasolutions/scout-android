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
package scout.edu.mit.ll.nics.android.api.database.tables;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import scout.edu.mit.ll.nics.android.api.data.ReportSendStatus;
import scout.edu.mit.ll.nics.android.api.data.ReportOnConditionData;
import scout.edu.mit.ll.nics.android.utils.Constants;

/**
 * @author Luis Gutierrez
 *         <p>
 *         This class contains all the items necessary for creating and accessing a report on condition table in the
 *         nics database.
 */
public class ReportOnConditionTable extends DatabaseTable<ReportOnConditionData>
{
	/**
	 * Defines the columns and its SQLite data type.
	 */
	private static final Map<String, String> TABLE_COLUMNS_MAP = new HashMap<String, String>()
	{
		private static final long serialVersionUID = 4292279082677888054L;

		{
			//------------------------------------------
			// Metadata
			//------------------------------------------
			// Which ones should be marked as new?
			// FIXME - If the primary key works, remove this:
			//put("id",				"integer primary key autoincrement");
			put("sendStatus",		"integer");


			// Placing this to sort by creation date
			put("datecreated",		"integer");

			// Adding this as its own row to query against it (as UTC)
			put("incidentid",		"integer");
			put("incidentname",		"text");

			// The serialized object
			put("json",				"text");

			// Specify the compound primary key
			//put("(incidentid, incidentname)", "primary key");
			// This doesn't work, because TABLE_COLUMNS_MAP is used for retrieving the data, not just the create table
		}
	};

	/**
	 * Constructor.
	 *
	 * @param tableName Name of the table.
	 * @param context   Android context reference.
	 */
	public ReportOnConditionTable (final String tableName, final Context context)
	{
		super(tableName, context);
	}

	@Override
	public void createTable (SQLiteDatabase database)
	{
		createTable(TABLE_COLUMNS_MAP, database);
	}

	@Override
	public long addData (final ReportOnConditionData data, SQLiteDatabase database)
	{
		Log.e("ROC","ReportOnConditionTable.addData executed.");
		long row = 0L;

		if (database != null)
		{
			try
			{
				ContentValues contentValues = new ContentValues();

				// If we add draft functionality, add this:
				//contentValues.put("isDraft", data.isDraft());
				// Storing metadata:
				contentValues.put("sendStatus", data.sendStatus != null ? data.sendStatus.getId() : 0);

				// Store incidentid as its own column for faster retrieval
				contentValues.put("incidentid", data.incidentid);
				contentValues.put("incidentname", data.incidentname);

				// Inserting the time at which it was created (storing as UTC)
				contentValues.put("datecreated",		data.datecreated.getTime());

				// Store the actual object info
				contentValues.put("json",		data.toJSON().toString());

				// Insert the db row
				row = database.insert(tableName, null, contentValues);
			}
			catch (Exception ex)
			{
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to add data to table: \"" + tableName + "\"", ex);
			}
		}
		else
		{
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to add data to table: \"" + tableName + "\"");
		}

		return row;
	}


	public ArrayList<ReportOnConditionData> getAllDataReadyToSend (long collaborationRoomId, SQLiteDatabase database)
	{
		String orderBy = "datecreated DESC";
		String sqlSelection = "sendStatus==? AND incidentid==?";
		String[] sqlSelectionArguments = {String.valueOf(ReportSendStatus.WAITING_TO_SEND.getId()), String.valueOf(collaborationRoomId)};

		return getData(sqlSelection, sqlSelectionArguments, orderBy, NO_LIMIT, database);
	}

	public ArrayList<ReportOnConditionData> getAllDataReadyToSend (String orderBy, SQLiteDatabase database)
	{
		String sqlSelection = "sendStatus==?";
		String[] sqlSelectionArguments = {String.valueOf(ReportSendStatus.WAITING_TO_SEND.getId())};

		return getData(sqlSelection, sqlSelectionArguments, orderBy, NO_LIMIT, database);
	}

	public ArrayList<ReportOnConditionData> getAllDataHasSent (long collaborationRoomId, SQLiteDatabase database)
	{
		String orderBy = "datecreated DESC";
		String sqlSelection = "sendStatus==? AND incidentid==?";
		String[] sqlSelectionArguments = {String.valueOf(ReportSendStatus.SENT.getId()), String.valueOf(collaborationRoomId)};

		return getData(sqlSelection, sqlSelectionArguments, orderBy, NO_LIMIT, database);
	}

	public ArrayList<ReportOnConditionData> getAllDataHasSent (String orderBy, SQLiteDatabase database)
	{
		String sqlSelection = "sendStatus==?";
		String[] sqlSelectionArguments = {String.valueOf(ReportSendStatus.SENT.getId())};

		return getData(sqlSelection, sqlSelectionArguments, orderBy, NO_LIMIT, database);
	}


	/**
	 * Deletes a row in the database where the incidentname column is equal to the provided incidentname,
	 * and datecreated is equal to the provided creation date
	 *
	 * @param incidentName value of the row to delete.
	 * @param creationDate value of the row to delte.
	 * @param database The database.
	 * @return The number of rows affected by this call. -1 indicates that there was an error in performing this operation.
	 */
	public long deleteData(final String incidentName, final long creationDate, SQLiteDatabase database) {
		long rows = 0L;

		if (database != null) {
			String whereClause = "incidentname==? AND datecreated==?";
			String[] whereArguments = {incidentName, String.valueOf(creationDate)};

			try
			{
				rows = database.delete(tableName, whereClause, whereArguments); // SQL where clause arguments.
			}
			catch (Exception ex)
			{
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to delete data from table: \"" + tableName + "\"", ex);
			}
		}
		else
		{
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to delete data from table: \"" + tableName + "\"");
		}
		return rows;
	}



	public ReportOnConditionData getDataByIncidentAndTime(String incidentName, long creationDate, String orderBy, SQLiteDatabase database)
	{
		// These make up a primary key
		// NOTE: We can't just use incidentid, as ROCs that created an incident have not been assigned an incidentid, but they have a unique incident name!
		String sqlSelection = "incidentname==? AND datecreated==?";
		String[] sqlSelectionArguments = {incidentName, String.valueOf(creationDate)};

		ArrayList<ReportOnConditionData> data = getData(sqlSelection, sqlSelectionArguments, orderBy, NO_LIMIT, database);

		// Return the first element:
		if(data.size() > 0)
		{
			return data.get(0);
		}

  		return null;
	}





	@Override
	protected ArrayList<ReportOnConditionData> getData (String sqlSelection, String[] sqlSelectionArguments, String orderBy, String limit, SQLiteDatabase database)
	{
		ArrayList<ReportOnConditionData> dataList = new ArrayList<ReportOnConditionData>();

		if (database != null)
		{
			try
			{
				Cursor cursor;
				if (sqlSelection == null)
				{
					cursor = database.query(tableName,                                                                   // Table
							TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), // Columns
							null,                                                                        // Selection
							null,                                                                        // Selection arguments
							null,                                                                        // Group by
							null,                                                                        // Having
							orderBy);                                                                       // Order by
				}
				else
				{
					cursor = database.query(tableName,                                                                   // Table
							TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), // Columns
							sqlSelection,                                                                // Selection
							sqlSelectionArguments,                                                       // Selection arguments
							null,                                                                        // Group by
							null,                                                                        // Having
							orderBy);                                                                    // Order by
				}

				if (cursor != null)
				{
					cursor.moveToFirst();

					while (!cursor.isAfterLast())
					{
						// Unfortunately, the use of having things simplified in the table constructor leaves us having
						// to make 2 calls for every data element retrieved.  However, the code is easier to follow.


						ReportOnConditionData dataItem = ReportOnConditionData.fromJSON(cursor.getString(cursor.getColumnIndex("json")));

						// If the dataItem was successfully converted from JSON
						if(dataItem != null)
						{
							// Update the dataItem fields that may have been modified while it is in the db:
							dataItem.sendStatus = ReportSendStatus.lookUp(cursor.getInt(cursor.getColumnIndex("sendStatus")));
							dataList.add(dataItem);
						}

						cursor.moveToNext();
					}

					cursor.close();
				}
			}
			catch (Exception ex)
			{
				Log.w( Constants.nics_DEBUG_ANDROID_TAG,	"Exception occurred while trying to get data from table: \"" + tableName + "\"", ex);
			}
		}
		else
		{
			Log.w(Constants.nics_DEBUG_ANDROID_TAG,"Could not get database to get all data from table: \"" + tableName + "\"");
		}

		return dataList;
	}

	/**
	 * Gets the last data that was received and stored into the database.
	 *
	 * @param database The database.
	 * @return The timestamp of the last message received or (-1L) if no messages were received for that chat room.
	 */

	// TODO ================================================================================================
	// TODO - Make these sections compatible
	// TODO ================================================================================================

	public long getLastDataTimestamp (SQLiteDatabase database)
	{
		long lastMessageTimestamp = -1L;

		if (database != null)
		{
			try
			{
				// Descending by timestamp so that the newest item is the first item returned.
				String orderBy = "datecreated DESC";

				Cursor cursor = database.query(tableName,                                                                   // Table
						TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), // Columns
						null,                                                                        // Selection
						null,                                                                        // Selection arguments
						null,                                                                        // Group by
						null,                                                                        // Having
						orderBy);                                                                    // Order by

				if (cursor != null)
				{
					cursor.moveToFirst();

					// First record is our newest item (largest timestamp).
					if (!cursor.isAfterLast())
					{
						int colIdx = cursor.getColumnIndex("datecreated");
						if (colIdx > -1)
						{
							lastMessageTimestamp = cursor.getLong(colIdx);
						}
					}

					cursor.close();
				}
			}
			catch (Exception ex)
			{
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to get data from table: \"" + tableName + "\"", ex);
			}
		}
		else
		{
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to get all data from table: \"" + tableName + "\"");
		}

		return lastMessageTimestamp;
	}

	// TODO ================================================================================================
	// TODO - Make these sections compatible
	// TODO ================================================================================================

	public long getLastDataForIncidentTimestamp (long incidentid, SQLiteDatabase database)
	{
		long lastMessageTimestamp = -1L;

		if (database != null)
		{
			try
			{
				// Descending by time-stamp so that the newest item is the first item returned.
				String orderBy = "datecreated DESC";
				String sqlSelection = "incidentid==?";
				String[] sqlSelectionArguments = {String.valueOf(incidentid)};

				Cursor cursor = database.query(tableName, TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), sqlSelection, sqlSelectionArguments, null, null, orderBy);

				if (cursor != null)
				{
					cursor.moveToFirst();

					// First record is our newest item (largest time-stamp).
					if (!cursor.isAfterLast())
					{
						lastMessageTimestamp = cursor.getLong(cursor.getColumnIndex("datecreated"));
					}

					cursor.close();
				}
			}
			catch (Exception ex)
			{
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to get data from table: \"" + tableName + "\"", ex);
			}
		}
		else
		{
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to get all data from table: \"" + tableName + "\"");
		}

		return lastMessageTimestamp;
	}

	public ReportOnConditionData getLastDataForIncident (long incidentid, SQLiteDatabase database)
	{
		ReportOnConditionData lastPayload = null;

		if (database != null)
		{
			try
			{
				// Descending by time-stamp so that the newest item is the first item returned.
				String orderBy = "datecreated DESC";
				String sqlSelection = "incidentid==?";
				String[] sqlSelectionArguments = {String.valueOf(incidentid)};

				Cursor cursor = database.query(tableName, TABLE_COLUMNS_MAP.keySet().toArray(new String[TABLE_COLUMNS_MAP.size()]), sqlSelection, sqlSelectionArguments, null, null, orderBy);

				if (cursor != null)
				{
					cursor.moveToFirst();

					// First record is our newest item (largest time-stamp).
					if (!cursor.isAfterLast())
					{
						lastPayload = ReportOnConditionData.fromJSON(cursor.getString(cursor.getColumnIndex("json")));

						// If the dataItem was successfully converted from JSON
						if(lastPayload != null)
						{
							// Update the dataItem fields that may have been modified while it is in the db:
							lastPayload.sendStatus = ReportSendStatus.lookUp(cursor.getInt(cursor.getColumnIndex("sendStatus")));
						}
					}

					cursor.close();
				}
			}
			catch (Exception ex)
			{
				Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Exception occurred while trying to get data from table: \"" + tableName + "\"", ex);
			}
		}
		else
		{
			Log.w(Constants.nics_DEBUG_ANDROID_TAG, "Could not get database to get all data from table: \"" + tableName + "\"");
		}
		return lastPayload;
	}

	public ArrayList<ReportOnConditionData> getDataForIncident (long collaborationRoomId, SQLiteDatabase database)
	{
		String orderBy = "datecreated DESC";
		String sqlSelection = "incidentid==?";
		String[] sqlSelectionArguments = {String.valueOf(collaborationRoomId)};

		return getData(sqlSelection, sqlSelectionArguments, orderBy, NO_LIMIT, database);
	}


	@Override
	public long addData (ArrayList<ReportOnConditionData> data, SQLiteDatabase database)
	{
		return 0;
	}

}
