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
package scout.edu.mit.ll.nics.android.maps.markup.tileprovider;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PathDashPathEffect;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import scout.edu.mit.ll.nics.android.maps.markup.MarkupFireLine;
import scout.edu.mit.ll.nics.android.maps.markup.tileprovider.MarkupTileProjection.DoublePoint;

public class MarkupFeatureTileProvider extends MarkupCanvasTileProvider {

	private CopyOnWriteArrayList<MarkupFireLine> mFirelineFeatures;

	public MarkupFeatureTileProvider() {
		mFirelineFeatures = new CopyOnWriteArrayList<MarkupFireLine>();
	}

	public void setFirelineFeatures(ArrayList<MarkupFireLine> features) {
		mFirelineFeatures.clear();
		mFirelineFeatures.addAll(features);
	}

	@Override
	void onDraw(Canvas canvas, MarkupTileProjection projection)
	{
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.RED);


		//This draws a black transparent grid
		/*paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.BLACK);
		paint.setAlpha(50);
		float gridSpacing = 10;
		for(int i = 0; i <= canvas.getWidth(); i+= gridSpacing)
		{
			canvas.drawLine(0,i,canvas.getWidth(),i,paint);
			canvas.drawLine(i,0,i,canvas.getHeight(),paint);
		}*/

		//This draws a blue border at the edge of each tile
		/*paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(5);
		paint.setColor(Color.BLUE);
		canvas.drawLine(0,0,canvas.getWidth(),0,paint);
		canvas.drawLine(0,canvas.getHeight(),canvas.getWidth(),canvas.getHeight(),paint);
		canvas.drawLine(canvas.getWidth(),0,canvas.getWidth(),canvas.getHeight(),paint);
		canvas.drawLine(0,0,0,canvas.getHeight(),paint);*/

		for (MarkupFireLine feature : mFirelineFeatures)
		{
			paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setStyle(Style.STROKE);
			int red = feature.getStrokeColor()[1];
			int green = feature.getStrokeColor()[2];
			int blue = feature.getStrokeColor()[3];

			paint.setColor(Color.rgb(red, green, blue));
			LatLngBounds bounds = projection.getTileBounds();

			DoublePoint sw = new DoublePoint(0, 0);
			DoublePoint ne = new DoublePoint(0, 0);

			projection.latLngToPoint(bounds.southwest, sw);
			projection.latLngToPoint(bounds.northeast, ne);

			ArrayList<LatLng> coordinates = new ArrayList<LatLng>(feature.getPoints());

			Path path = new Path();
			float[] floatPoints = new float[feature.getPoints().size() * 2];
			DoublePoint pt = new DoublePoint(0, 0);

			boolean outOfBounds = true;

			Log.w("W", "Bounds calculated: NE: " + ne.x + "," + ne.y + ")  SW: (" + sw.x + "," + sw.y + ")");


			for (int i = 0; i < coordinates.size(); i++)
			{
				LatLng coord = coordinates.get(i);
				projection.latLngToPoint(coord, pt);
				floatPoints[(i * 2)] = (float) pt.x;
				floatPoints[(i * 2) + 1] = (float) pt.y;

				float x = floatPoints[(i*2)];
				float y = floatPoints[(i*2)+1];


				//Making sure the fireline is in the canvas bounds

				//Add a 32px buffer around it, because we don't want line styling to be truncated on edges
				//if a line ends up right against a border, it's style (width, dashes, etc) may be truncated
				float buffer = 32;
				if(outOfBounds)
				{
					if (x >= sw.x - buffer && x <= ne.x + buffer && y <= sw.y + buffer && y >= ne.y - buffer)
					{
						outOfBounds = false;
					}
				}
			}

			//If the fire-line is out of this canvas' bounds, do not render
			if(outOfBounds)
			{
				continue;
			}
			//=====================================================

			for (int i = 0; i < coordinates.size(); i++)
			{
				if (i == 0)
				{
					path.moveTo(floatPoints[0], floatPoints[1]);
				}
				else
				{
					path.lineTo(floatPoints[i * 2], floatPoints[(i * 2) + 1]);
				}
			}

			String dashStyle = feature.getFeature().getDashStyle();

			if (dashStyle.equals("primary-fire-line"))
			{
				paint.setPathEffect(new PathDashPathEffect(makeRectangle(10, 10), 25, 0, PathDashPathEffect.Style.ROTATE));
			}
			else if (dashStyle.equals("action-point"))
			{
				//Black border
				paint.setColor(Color.rgb(0,0,0));
				paint.setStrokeWidth(8);
				canvas.drawPath(path, paint);

				//Orange thinner line
				paint.setColor(Color.rgb(255,163,0));
				paint.setStrokeWidth(6);
				canvas.drawPath(path, paint);

				//Orange dots on the ends
				//Backing up the current paint style
				Paint.Style backupStyle = paint.getStyle();

				paint.setStyle(Paint.Style.FILL);
				canvas.drawCircle(floatPoints[0],floatPoints[1],10,paint);
				canvas.drawCircle(floatPoints[floatPoints.length - 2],floatPoints[floatPoints.length - 1],10,paint);

				//Restoring the previous style
				paint.setStyle(backupStyle);
			}
			else if (dashStyle.equals("secondary-fire-line"))
			{
				PathDashPathEffect effect = new PathDashPathEffect(makeCircle(4), 30, 0, PathDashPathEffect.Style.ROTATE);
				//PathDashPathEffect effect = new PathDashPathEffect(makeDash(3, 15,1), 30, 0, PathDashPathEffect.Style.ROTATE);
				paint.setPathEffect(effect);
			}
			else if (dashStyle.equals("fire-spread-prediction"))
			{
				paint.setColor(Color.rgb(247, 148, 30));
				paint.setStrokeWidth(5);
			}
			else if (dashStyle.equals("completed-dozer-line"))
			{
				paint.setPathEffect(new PathDashPathEffect(makeCross(2), 25, 0, PathDashPathEffect.Style.ROTATE));
			}
			else if (dashStyle.equals("proposed-dozer-line"))
			{
				paint.setPathEffect(new PathDashPathEffect(makeCross(2), 35, 0, PathDashPathEffect.Style.ROTATE));
				canvas.drawPath(path, paint);
				paint.setPathEffect(new PathDashPathEffect(makeCircle(2), 35, 17.5f, PathDashPathEffect.Style.ROTATE));
			}
			else if (dashStyle.equals("fire-edge-line"))
			{
				paint.setColor(Color.RED);
				paint.setStrokeWidth(4);
				canvas.drawPath(path, paint);
				//PathDashPathEffect effect = new PathDashPathEffect(makeDash(gap, length, width), dash spacing, offset, PathDashPathEffect.Style.ROTATE);
				PathDashPathEffect effect = new PathDashPathEffect(makeDash(3, 15,1), 10, 0, PathDashPathEffect.Style.ROTATE);
				paint.setPathEffect(effect);
			}
			else if (dashStyle.equals("map"))
			{
				path.addCircle(floatPoints[0], floatPoints[1] + 2, 4, Direction.CW);
				path.addCircle(floatPoints[floatPoints.length - 2], floatPoints[floatPoints.length - 1] - 2, 4, Direction.CCW);
				paint.setStrokeWidth(8);
				canvas.drawPath(path, paint);
				paint.setStrokeWidth(5);
				paint.setColor(Color.rgb(247, 148, 30));
			}
			else if(dashStyle.equals("dash"))
			{
				int[] strokeColor = feature.getStrokeColor();
				paint.setColor(Color.rgb(strokeColor[1], strokeColor[2], strokeColor[3]));
				paint.setStrokeWidth(feature.getFeature().getStrokeWidth().floatValue());
				paint.setPathEffect(new DashPathEffect(new float[]{20,10}, 0));
			}

			canvas.drawPath(path, paint);
		}
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

	public Path makeDash(float gap, float length, float width) {
		Path p = new Path();
		p.addRect(-width * 0.5f,-gap - length, width * 0.5f, -gap, Direction.CCW);
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

}
