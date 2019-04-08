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
package scout.edu.mit.ll.nics.android.api.payload;

import java.util.ArrayList;

public class IncidentTypePayload
{
	private long incidentIncidenttypeid;
	private String incident;
	private IncidentTypeIncidentTypePayload incidentType;
	private long incidenttypeid;
	private long incidentId;

	public static class IncidentTypeIncidentTypePayload
	{
		private long incidentTypeId;
		private String incidentTypeName;

		public void setIncidentTypeId (long incidentTypeId)
		{
			this.incidentTypeId = incidentTypeId;
		}

		public String getIncidentTypeName ()
		{
			return incidentTypeName;
		}

		public void setIncidentTypeName (String incidentTypeName)
		{
			this.incidentTypeName = incidentTypeName;
		}

		public long getIncidentTypeId()
		{
			return incidentTypeId;
		}

	}

	public long getIncidentIncidenttypeid ()
	{
		return incidentIncidenttypeid;
	}

	public void setIncidentIncidenttypeid (long incidentIncidenttypeid)
	{
		this.incidentIncidenttypeid = incidentIncidenttypeid;
	}

	public String getIncident ()
	{
		return incident;
	}

	public void setIncident (String incident)
	{
		this.incident = incident;
	}

	public IncidentTypeIncidentTypePayload getIncidentType ()
	{
		return incidentType;
	}

	public void setIncidentType (IncidentTypeIncidentTypePayload incidentType)
	{
		this.incidentType = incidentType;
	}

	public long getIncidenttypeid ()
	{
		return incidenttypeid;
	}

	public void setIncidenttypeid (long incidenttypeid)
	{
		this.incidenttypeid = incidenttypeid;
	}

	public long getIncidentId ()
	{
		return incidentId;
	}

	public void setIncidentId (long incidentId)
	{
		this.incidentId = incidentId;
	}
}
