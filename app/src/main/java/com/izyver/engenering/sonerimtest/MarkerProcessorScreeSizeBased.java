package com.izyver.engenering.sonerimtest;

import android.graphics.Point;

import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MarkerProcessorScreeSizeBased extends MarkerProcessor {
    private float lastZoom = -1;
    @Nullable
    private List<MarkerPoint> lastPoints;

    public MarkerProcessorScreeSizeBased(@NotNull Point screenSize, int markerDiameter) {
        super(screenSize, markerDiameter);
    }

    @Override
    public void generate(@NotNull LatLng upLeft, @NotNull LatLng downRight) {
        super.generate(upLeft, downRight);
        lastPoints = null;
    }

    @NotNull
    @Override
    public List<MarkerPoint> getPointsForProjection(@NotNull Projection projection, float zoom) {
        List<MarkerPoint> points;

        if (lastZoom > zoom && lastPoints != null) {
            points = lastPoints;
        } else if (lastZoom == zoom && lastPoints != null) {
            return lastPoints;
        }else {
            points = getPoints();
        }
        lastZoom = zoom;

        lastPoints = calculateMarkerPoints(projection, points);
        return lastPoints;
    }

    @NotNull
    protected List<MarkerPoint> calculateMarkerPoints(@NotNull Projection projection, List<MarkerPoint> points) {
        Point[] screePoints = convertToPoints(projection, points);

        List<MarkerPoint> resultPoints = new ArrayList<>();
        for (int i = 0; i < screePoints.length; i++) {
            List<MarkerPoint> intersects = new ArrayList<>();
            for (int j = i; j < screePoints.length; j++) {
                if (j != i && isIntersect(screePoints[i], screePoints[j])) {
                    intersects.add(points.get(j));
                    screePoints[j] = null;//remove item to avoid duplicates points
                }
            }
            if (intersects.isEmpty()) {
                if (screePoints[i] == null) {
                    //don't add duplicated item to result
                    continue;
                }
                resultPoints.add(points.get(i));
            } else {
                intersects.add(points.get(i));
                MarkerPoint markerPoint = makeAveragePoint(intersects);
                resultPoints.add(markerPoint);
            }
        }
        return resultPoints;
    }

    @NotNull
    protected Point[] convertToPoints(@NotNull Projection projection, List<MarkerPoint> points) {
        Point[] screePoints = new Point[points.size()];
        for (int i = 0; i < points.size(); i++) {
            screePoints[i] = projection.toScreenLocation(points.get(i).latLng);
            //by default the point points to bottom part of marker by y coordinate
            screePoints[i].y = screePoints[i].y - markerDiameter / 2; //set point to point to center of marker
        }
        return screePoints;
    }

    @NotNull
    protected MarkerPoint makeAveragePoint(List<MarkerPoint> intersects) {
        float value = 0;
        double latitude = 0;
        double longitude = 0;
        for (MarkerPoint intersect : intersects) {
            value += intersect.value;
            latitude += intersect.latLng.latitude;
            longitude += intersect.latLng.longitude;
        }
        LatLng latLng = new LatLng(latitude / intersects.size(), longitude / intersects.size());
        return new MarkerPoint(Math.round(value / intersects.size()), latLng);
    }

    protected boolean isIntersect(Point point1, Point point2) {
        if (point1 == null || point2 == null) return false;
        int length = length(point1, point2);
        return length < markerDiameter;
    }

    protected int length(Point point1, Point point2) {
        int first = point2.x - point1.x;
        int second = point2.y - point1.y;
        return (int) sqrt(pow(first, 2) + pow(second, 2));
    }
}
