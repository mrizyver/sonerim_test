package com.izyver.engenering.sonerimtest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ScreenPointCollectionTest {
    private FakeVPoint[] points;

    @Before
    public void before() {
        points = new FakeVPoint[]{
                new FakeVPoint(0, 10, 10),
                new FakeVPoint(0, 15, 10),
                new FakeVPoint(0, 35, 10),
                new FakeVPoint(0, 45, 10),
                new FakeVPoint(0, 20, 25),
                new FakeVPoint(0, 25, 30),
                new FakeVPoint(0, 45, 35),
                new FakeVPoint(0, 35, 45),
                new FakeVPoint(0, 15, 45),
                new FakeVPoint(0, 10, 45),
                new FakeVPoint(0, 10, 50),
                new FakeVPoint(0, 5, 50)
        };

    }

    @Test
    public void hasNextNextTest() {
        VPoint[] points = new VPoint[10];
        for (int i = 0; i < points.length; i++) {
            points[i] = new VPoint(0, i, i);
        }
        ScreenPointCollection collection = new ScreenPointCollection(points);

        int i = 0;
        while (collection.hasNext()) {
            VPoint next = collection.next();
            assertEquals(points[i].x, next.x);
            assertEquals(points[i].y, next.y);
            i++;
        }
        assertEquals(points.length, i);
    }

    @Test
    public void getAroundTest() {
        int distance = 10;

        ScreenPointCollection collection = new ScreenPointCollection(points);
        VPoint[] around = collection.getAround(points[0], distance, false);
        assertEquals(1, around.length);
        assertEquals(points[1], around[0]);

        around = collection.getAround(points[2], distance, false);
        assertEquals(1, around.length);
        assertEquals(points[3], around[0]);

        around = collection.getAround(points[3], distance, false);
        assertEquals(1, around.length);
        assertEquals(points[2], around[0]);

        around = collection.getAround(points[7], distance, false);
        assertEquals(0, around.length);

        around = collection.getAround(points[9], distance, false);
        assertEquals(3, around.length);
        assertTrue(contains(around, points[11]));
        assertTrue(contains(around, points[10]));
        assertTrue(contains(around, points[8]));
    }

    @Test
    public void fullTest() {
        ScreenPointCollection collection = new ScreenPointCollection(points);
        while (collection.hasNext()) {
            VPoint next = collection.next();
            VPoint[] around = collection.getAround(next, 10, true);
            if (around.length > 0) {
                FakeVPoint splitPoint = splitPoints(next, around);
                collection.setInstead(splitPoint);
                collection.moveBack();
            }
        }
        VPoint[] vPoints = collection.toArray();
        assertEquals(vPoints.length, 6);
        assertTrue(contains(vPoints, points[6]));
        assertTrue(contains(vPoints, points[7]));
        assertTrue(contains(vPoints, new FakeVPoint(0, 12, 10)));
        assertTrue(contains(vPoints, new FakeVPoint(0, 22, 27)));
        assertTrue(contains(vPoints, new FakeVPoint(0, 35, 45)));
        assertTrue(contains(vPoints, new FakeVPoint(0, 45, 35)));
    }

    private FakeVPoint splitPoints(VPoint _point, VPoint[] points) {
        int x = _point.x, y = _point.y, value = _point.value;
        for (VPoint point : points) {
            value += point.value;
            x += point.x;
            y += point.y;
        }
        int itemCount = points.length + 1;
        return new FakeVPoint(value / itemCount, x / itemCount, y / itemCount);
    }

    private boolean contains(VPoint[] arr, VPoint point) {
        for (VPoint vPoint : arr) {
            if (vPoint.x == point.x && vPoint.y == point.y) return true;
        }
        return false;
    }

    class FakeVPoint extends VPoint {
        FakeVPoint(int value, int x, int y) {
            super(value, x, y);
            this.value = value;
            this.x = x;
            this.y = y;
        }
    }
}