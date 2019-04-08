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

public class ReportOnConditionData
{
	private long incidentid;
	private String incidentname;
	private ReportOnConditionMessageData message;

	public long getIncidentid ()
	{
		return incidentid;
	}

	public void setIncidentid (long incidentid)
	{
		this.incidentid = incidentid;
	}

	public String getIncidentname ()
	{
		return incidentname;
	}

	public void setIncidentname (String incidentname)
	{
		this.incidentname = incidentname;
	}

	public ReportOnConditionMessageData getMessage ()
	{
		return message;
	}

	public void setMessage (ReportOnConditionMessageData message)
	{
		this.message = message;
	}

	static class ReportOnConditionMessageData
	{
		private String datecreated;
		private ReportOnConditionMessageReportData report;

		public String getDatecreated ()
		{
			return datecreated;
		}

		public void setDatecreated (String datecreated)
		{
			this.datecreated = datecreated;
		}

		public ReportOnConditionMessageReportData getReport ()
		{
			return report;
		}

		public void setReport (ReportOnConditionMessageReportData report)
		{
			this.report = report;
		}

		static class ReportOnConditionMessageReportData
		{
			private String reportType;
			private String date;
			private String starttime;
			private long formTypeId;
			private String reportBy;
			private String email;
			private String rocDisplayName;
			private String location;
			private String jurisdiction;
			private String incidentType;
			private long scope;
			private String 	spreadRate;
			private double percentContained;
			private double temperature;
			private double relHumidity;
			private double windSpeed;
			//FIXME - this should be changed to a double (degrees from north)
			private String windDirection;
			private String predictedWeather;
			private String evacuations;
			private String structuresThreat;
			private String infrastructuresThreat;
			private String county;
			private Boolean simplifiedEmail;
			private String comments;

			public String getReportType ()
			{
				return reportType;
			}

			public void setReportType (String reportType)
			{
				this.reportType = reportType;
			}

			public String getDate ()
			{
				return date;
			}

			public void setDate (String date)
			{
				this.date = date;
			}

			public String getStarttime ()
			{
				return starttime;
			}

			public void setStarttime (String starttime)
			{
				this.starttime = starttime;
			}

			public long getFormTypeId ()
			{
				return formTypeId;
			}

			public void setFormTypeId (long formTypeId)
			{
				this.formTypeId = formTypeId;
			}

			public String getReportBy ()
			{
				return reportBy;
			}

			public void setReportBy (String reportBy)
			{
				this.reportBy = reportBy;
			}

			public String getEmail ()
			{
				return email;
			}

			public void setEmail (String email)
			{
				this.email = email;
			}

			public String getRocDisplayName ()
			{
				return rocDisplayName;
			}

			public void setRocDisplayName (String rocDisplayName)
			{
				this.rocDisplayName = rocDisplayName;
			}

			public String getLocation ()
			{
				return location;
			}

			public void setLocation (String location)
			{
				this.location = location;
			}

			public String getJurisdiction ()
			{
				return jurisdiction;
			}

			public void setJurisdiction (String jurisdiction)
			{
				this.jurisdiction = jurisdiction;
			}

			public String getIncidentType ()
			{
				return incidentType;
			}

			public void setIncidentType (String incidentType)
			{
				this.incidentType = incidentType;
			}

			public long getScope ()
			{
				return scope;
			}

			public void setScope (long scope)
			{
				this.scope = scope;
			}

			public String getSpreadRate ()
			{
				return spreadRate;
			}

			public void setSpreadRate (String spreadRate)
			{
				this.spreadRate = spreadRate;
			}

			public double getPercentContained ()
			{
				return percentContained;
			}

			public void setPercentContained (double percentContained)
			{
				this.percentContained = percentContained;
			}

			public double getTemperature ()
			{
				return temperature;
			}

			public void setTemperature (double temperature)
			{
				this.temperature = temperature;
			}

			public double getRelHumidity ()
			{
				return relHumidity;
			}

			public void setRelHumidity (double relHumidity)
			{
				this.relHumidity = relHumidity;
			}

			public double getWindSpeed ()
			{
				return windSpeed;
			}

			public void setWindSpeed (double windSpeed)
			{
				this.windSpeed = windSpeed;
			}

			public String getWindDirection ()
			{
				return windDirection;
			}

			public void setWindDirection (String windDirection)
			{
				this.windDirection = windDirection;
			}

			public String getPredictedWeather ()
			{
				return predictedWeather;
			}

			public void setPredictedWeather (String predictedWeather)
			{
				this.predictedWeather = predictedWeather;
			}

			public String getEvacuations ()
			{
				return evacuations;
			}

			public void setEvacuations (String evacuations)
			{
				this.evacuations = evacuations;
			}

			public String getStructuresThreat ()
			{
				return structuresThreat;
			}

			public void setStructuresThreat (String structuresThreat)
			{
				this.structuresThreat = structuresThreat;
			}

			public String getInfrastructuresThreat ()
			{
				return infrastructuresThreat;
			}

			public void setInfrastructuresThreat (String infrastructuresThreat)
			{
				this.infrastructuresThreat = infrastructuresThreat;
			}

			public String getCounty ()
			{
				return county;
			}

			public void setCounty (String county)
			{
				this.county = county;
			}

			public Boolean getSimplifiedEmail ()
			{
				return simplifiedEmail;
			}

			public void setSimplifiedEmail (Boolean simplifiedEmail)
			{
				this.simplifiedEmail = simplifiedEmail;
			}

			public String getComments ()
			{
				return comments;
			}

			public void setComments (String comments)
			{
				this.comments = comments;
			}
		}
	}


}
