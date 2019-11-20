package com.izyver.engenering.sonerimtest;

import android.graphics.Point;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

class ScreenPointCollection {
    private static final int NOT_MUST_TO_JUMP = -1;

    private final VPoint[] points;
    private Node[] indexOfX;
    private Node[] indexOfY;

    private int size;
    private int position;
    private int mustToJump = NOT_MUST_TO_JUMP;

    public ScreenPointCollection(VPoint[] points) {
        this.points = points;
        this.position = -1;
        this.indexOfX = new Node[points.length];
        this.indexOfY = new Node[points.length];
        this.size = points.length;
        for (int i = 0; i < points.length; i++) {
            indexOfX[i] = new Node(i, points[i].x);
            indexOfY[i] = new Node(i, points[i].y);
        }
        Arrays.sort(indexOfX);
        Arrays.sort(indexOfY);
    }

    public VPoint[] getAround(VPoint point, int distance, boolean remove) {
        Set<Integer> indexes = new HashSet<>();
        indexes.addAll(indexSampling(indexOfX, point.x - distance, point.x + distance));
        indexes.addAll(indexSampling(indexOfY, point.y - distance, point.y + distance));

        List<Integer> toRemove = new LinkedList<>();
        for (Integer index : indexes) {
            if (points[index] == null
                    || (point.y == points[index].y && point.x == points[index].x)
                    || length(point, points[index]) > distance) {
                toRemove.add(index);
            }
        }
        indexes.removeAll(toRemove);

        VPoint[] result = new VPoint[indexes.size()];
        int i = 0;
        for (Integer index : indexes) {
            result[i++] = points[index];
            if (remove) {
                points[index] = null;
                size--;
            }
        }
        return result;
    }

    public boolean hasNext() {
        int index = position + 1;
        boolean result = false;
        while (index < points.length) {
            Point point = points[index];
            if (point == null) {
                index++;
                continue;
            }
            result = true;
            break;
        }
        mustToJump = index - position;
        return result;
    }

    public VPoint next() {
        if (mustToJump != NOT_MUST_TO_JUMP) {
            position += mustToJump;
            mustToJump = NOT_MUST_TO_JUMP;
        } else {
            position++;
        }
        return points[position];
    }

    public boolean moveBack() {
        if (position < 0) {
            return false;
        } else {
            position++;
            return true;
        }
    }

//    public void remove(VPoint... points) {
//        for (Point point : points) {
//            int indexY = UtilKt.binaryPlaceSearch(indexOfY, new Node(0, point.y), Node::compareTo);
//            indexY = indexOfY[indexY].index;
//
//            int indexX = UtilKt.binaryPlaceSearch(indexOfX, new Node(0, point.x), Node::compareTo);
//            indexX = indexOfX[indexX].index;
//
//            if (indexX != indexY) {
//                throw new RuntimeException("index x not equal to index y");
//            }
//            this.points[indexX] = null;
//            size--;
//        }
//    }

    public void setInstead(VPoint point) {
        points[position] = point;
    }

    /**
     * @return collection with null values
     */
    public VPoint[] getDirtyCollection() {
        return points;
    }

    public VPoint[] toArray() {
        VPoint[] result = new VPoint[size];
        int i = 0;
        for (VPoint point : points) {
            if (point != null) {
                result[i++] = point;
            }
        }
        return result;
    }

    private List<Integer> indexSampling(Node[] nodes, int startValue, int endValue) {
        if (startValue >= endValue) {
            throw new RuntimeException("start value must be less than end value");
        }
        int index = UtilKt.binaryPlaceSearch(nodes, new Node(0, startValue), Node::compareTo);
        List<Integer> result = new LinkedList<>();
        if (index < 0) return result;
        while (index < nodes.length && nodes[index].value <= endValue) {
            result.add(nodes[index].index);
            index++;
        }
        return result;
    }

    protected int length(VPoint point1, VPoint point2) {
        int first = point2.x - point1.x;
        int second = point2.y - point1.y;
        return (int) sqrt(pow(first, 2) + pow(second, 2));
    }

    /**
     * @param srcPoint - point according that will calculate diapason
     * @param distance - diapason above and below src
     * @param dstPoint - point that is checked for being in the diapason
     * @return true if the dst is in diapason, else - false
     * <p>
     * also see {@link ScreenPointCollection#isInHorizontalDiapason(VPoint, int, VPoint)}
     * it has the same meaning
     */
    private boolean isInVerticalDiapason(VPoint srcPoint, int distance, VPoint dstPoint) {
        return dstPoint.y >= srcPoint.y - distance
                && dstPoint.y <= srcPoint.y + distance;
    }

    /**
     * @see ScreenPointCollection#isInVerticalDiapason(VPoint, int, VPoint)
     */
    private boolean isInHorizontalDiapason(VPoint srcPoint, int distance, VPoint dstPoint) {
        return dstPoint.x >= srcPoint.x - distance
                && dstPoint.x <= srcPoint.x + distance;
    }

    class Node implements Comparable<Node> {
        final int index;
        final int value;

        Node(int index, int value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public int hashCode() {
            return value * index;
        }

        @Override
        public int compareTo(Node o) {
            return value - o.value;
        }

        @NonNull
        @Override
        public String toString() {
            return "index - " + index + ", value - " + value;
        }
    }
}
