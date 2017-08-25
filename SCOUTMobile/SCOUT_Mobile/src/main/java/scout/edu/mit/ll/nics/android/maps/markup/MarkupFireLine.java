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
package scout.edu.mit.ll.nics.android.maps.markup;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.util.Log;

import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import scout.edu.mit.ll.nics.android.api.DataManager;
import scout.edu.mit.ll.nics.android.api.data.MarkupFeature;
import scout.edu.mit.ll.nics.android.api.data.Vector2;


public class MarkupFireLine extends MarkupBaseShape {

	public LatLng boundsSW;
	public LatLng boundsNE;

	public MarkupFireLine(DataManager manager, String title, LatLng coordinate, int[] strokeColor) {
		super(manager);

		setTitle(title);
		setPoint(coordinate);
		setType(MarkupType.sketch);

		
		setStrokeColor(strokeColor);
	}

	public MarkupFireLine(DataManager manager, MarkupFeature item, int[] strokeColor, float zoom) {
		super(manager);
	
		mFeature = item;

		setTitle(item.getLabelText());
		setType(MarkupType.sketch);
		setTime(item.getSeqTime());

		setStrokeColor(strokeColor);
		
		setCreator(item.getUsername());
		setDraft(false);
		setFeatureId(item.getFeatureId());
		setFeature(item);
		
		ArrayList<Vector2> points = item.getGeometryVector2();

		Vector2 point;
		LatLng coordinate;

		final ArrayList<LatLng> coordinates = new ArrayList<LatLng>();

		//Initializing lastCoord to point[0]
		point = points.get(0);
		LatLng lastCoord = new LatLng(point.x,point.y );


		//Creating a bounding box
		//Google's LatLngBounds class won't work for us because consecutive points MUST contain the area between them,
		// and LatLngBounds handles only a discrete set of points

		//We will iteratively increase the size of the bounding box
		coordinate = new LatLng(point.x, point.y);

		mutableLatLng bboxMins = new mutableLatLng(coordinate.latitude, coordinate.longitude);
		mutableLatLng bboxMaxs = new mutableLatLng(bboxMins.latitude,bboxMins.longitude);

		//Keeps track of how many antimeridians (180 degrees) we have crossed
		//negative means we crossed to the left
		//positive means we crossed to the right
		int antimeridiansCrossed = 0;

		//The longitude of the previous point
		double lastLong = coordinate.longitude;
		double curLong = coordinate.longitude;


		for (int i = 0; i < points.size(); i++) {
			point = points.get(i);

			coordinate = new LatLng(point.x, point.y);

			coordinates.add(coordinate);


			//Updating the bounding box

			//============ Latitude =============

			if(coordinate.latitude < bboxMins.latitude)
				bboxMins.latitude = coordinate.latitude;
			if(coordinate.latitude > bboxMaxs.latitude)
				bboxMaxs.latitude = coordinate.latitude;


			//============ Longitude ============

			//Check if we have crossed an antimeridian:
			antimeridiansCrossed += deltaAntimeridians(lastLong, coordinate.longitude);

			//Offset the longitudes by the amount of antimeridians we have crossed
			//This will be the absolute longitude of the point, relative to the first point in the line
			curLong = coordinate.longitude + (antimeridiansCrossed * 360.0f);

			if(curLong < bboxMins.longitude)
				bboxMins.longitude = curLong;
			if(curLong > bboxMaxs.longitude)
				bboxMaxs.longitude = curLong;

			lastLong = curLong;

			//======================================
		}

		//Setting the calculated bbox values
		boundsSW = new LatLng(bboxMins.latitude,bboxMins.longitude);
		boundsNE = new LatLng(bboxMaxs.latitude,bboxMaxs.longitude);
		
		setPoints(coordinates);
	}

	//Have to create a class to be able to modify a LatLng instance (LatLng's member variables are final)
	//The alternative to this is to instantiate a new LatLng every time we wish to modify
	class mutableLatLng {
		public double latitude;
		public double longitude;

		mutableLatLng(double latitude, double longitude) {
			this.latitude = latitude;
			this.longitude = longitude;
		}

	}

	//Returns how many antimeridians we crossed from longitude a to longitude b along the shortest path (<= 180 degrees)
	//Along the shortest path implies this can only return -1, 0, or 1
	private int deltaAntimeridians(double lng1, double lng2) {
		//If the distance between a and b < 180, the shortest path from a to b does not cross the antimeridian
		if(Math.abs(lng1 - lng2) <= 180) {
			return 0;
		}
		//Otherwise, the shortest path from a to b does cross the antimeridian

		//If lng1 > 0, and we crossed the antimeridian in the positive direction ( to the right from + to - )
		if(lng1 >= 0)
			return 1;

		//else, we crossed the antimeridian in the negative direction ( to the left from - to + )
		return -1;
	}

	public void setPoint(LatLng coordinate) {
		
		if(mPoints == null || mPoints.size() == 0) {
			addPoint(coordinate);
		} else {
			mPoints.set(0, coordinate);
		}
	}

	public GroundOverlayOptions getOptions() {
		return null;
	}

	@Override
	public void removeFromMap() {
	}

	@Override
	public boolean removeLastPoint() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getNicsType() {
		return "sketch";
	}

	public Path makeCircle(float radius) {
		Path p = new Path();
		p.addCircle(0, 0, radius, Path.Direction.CCW);
		return p;
	}

	public Path makeRectangle(float width, float length) {
		Path p = new Path();
		p.addRect(0 - width / 2, length / 2, width / 2, -length / 2, Direction.CCW);
		p.close();
		return p;
	}

	public Path makeDash(float width) {
		Path p = new Path();
		p.addRect(5, -5, 1, -width + 5, Direction.CCW);
		p.close();
		return p;
	}

	public Path makeCross(float size) {
		Path p = new Path();
		int i = -5;
		p.moveTo(5, 5);
		while (i < 5) {
			p.addCircle(i, -i, size, Direction.CCW);
			p.addCircle(-i, -i, size, Direction.CCW);
			i++;
		}

		return p;
	}

	public Path makeCrossWithCircle(float size) {
		Path p = new Path();
		int i = -5;
		p.moveTo(5, 5);
		while (i < 5) {
			p.addCircle(i, -i, size, Direction.CCW);
			p.addCircle(-i, -i, size, Direction.CCW);
			i++;
		}

		p.addCircle(15, -2, size * 2, Direction.CCW);
		return p;
	}

	@Override
	public void setIcon(Bitmap bitmap, int[] color) {
	}

	@Override
	public void showInfoWindow() {
	}

}
