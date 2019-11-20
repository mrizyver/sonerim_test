package com.izyver.engenering.sonerimtest;

import android.graphics.Point;

import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class ImprovedProcessorScreenSizeBased extends MarkerProcessorScreeSizeBased {
    public ImprovedProcessorScreenSizeBased(@NotNull Point screenSize, int markerDiameter) {
        super(screenSize, markerDiameter);
    }

    @NotNull
    @Override
    protected List<MarkerPoint> calculateMarkerPoints(@NotNull Projection projection, List<MarkerPoint> markerPoints) {
        VPoint[] points = new VPoint[markerPoints.size()];
        for (int i = 0; i < markerPoints.size(); i++) {
            Point point = projection.toScreenLocation(markerPoints.get(i).latLng);
            points[i] = new VPoint(markerPoints.get(i).value, point.x, point.y - markerDiameter / 2);
        }
        points = removeIncreasing(points);
        points = removeIncreasing(points);

        List<MarkerPoint> result = new LinkedList<>();
        for (VPoint dirtyPoint : points) {
            dirtyPoint.y += markerDiameter / 2;
            LatLng latLng = projection.fromScreenLocation(dirtyPoint);
            result.add(new MarkerPoint(dirtyPoint.value, latLng));
        }

        return result;
    }

    private VPoint[] removeIncreasing(VPoint[] points) {
        ScreenPointCollection collection = new ScreenPointCollection(points);
        while (collection.hasNext()) {
            VPoint next = collection.next();
            VPoint[] around = collection.getAround(next, markerDiameter, true);
            if (around.length > 0) {
                VPoint splitPoint = splitPoints(next, around);
                collection.setInstead(splitPoint);
                collection.moveBack();
            }
        }
        return collection.toArray();
    }


    private VPoint splitPoints(VPoint next, VPoint[] points) {
        int x = next.x, y = next.y, value = next.value;
        for (VPoint point : points) {
            value += point.value;
            x += point.x;
            y += point.y;
        }
        int pointCount = points.length + 1;
        return new VPoint(value / pointCount, x / pointCount, y / pointCount);
    }
}

