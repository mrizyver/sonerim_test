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

        Point[] screePoints = new Point[points.size()];
        for (int i = 0; i < points.size(); i++) {
            screePoints[i] = projection.toScreenLocation(points.get(i).latLng);
            screePoints[i].y = screePoints[i].y - markerDiameter / 2;
        }

        List<MarkerPoint> resultPoints = new ArrayList<>();
        for (int i = 0; i < screePoints.length; i++) {
            List<MarkerPoint> intersects = new ArrayList<>();
            for (int j = i; j < screePoints.length; j++) {
                if (j != i && isIntersect(screePoints[i], screePoints[j])) {
                    intersects.add(points.get(j));
                    screePoints[j] = null;
                }
            }
            if (intersects.isEmpty()) {
                if (screePoints[i] == null) continue;
                resultPoints.add(points.get(i));
            } else {
                intersects.add(points.get(i));
                float value = 0;
                double latitude = 0;
                double longitude = 0;
                for (MarkerPoint intersect : intersects) {
                    value += intersect.value;
                    latitude += intersect.latLng.latitude;
                    longitude += intersect.latLng.longitude;
                }
                LatLng latLng = new LatLng(latitude / intersects.size(), longitude / intersects.size());
                resultPoints.add(new MarkerPoint(Math.round(value / intersects.size()), latLng));
            }
        }

        lastPoints = resultPoints;
        return resultPoints;
    }

    private boolean isIntersect(Point point1, Point point2) {
        if (point1 == null || point2 == null) return false;
        int length = length(point1, point2);
        return length < markerDiameter;
    }

    private int length(Point point1, Point point2) {
        int first = point2.x - point1.x;
        int second = point2.y - point1.y;
        return (int) sqrt(pow(first, 2) + pow(second, 2));
    }
}
