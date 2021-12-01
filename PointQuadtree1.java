import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * A point quadtree: stores an element at a 2D position, 
 * with children at the subdivided quadrants.
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015.
 * @author CBK, Spring 2016, explicit rectangle.
 * @author CBK, Fall 2016, generic with Point2D interface.
 * 
 */
public class PointQuadtree<E extends Point2D> {
	private E point;							// the point anchoring this node
	private int x1, y1;							// upper-left corner of the region
	private int x2, y2;							// bottom-right corner of the region
	private PointQuadtree<E> c1, c2, c3, c4;	// children

	/**
	 * Initializes a leaf quadtree, holding the point in the rectangle
	 */
	public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
		this.point = point;
		this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
	}

	// Getters
	
	public E getPoint() {
		return point;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	/**
	 * Returns the child (if any) at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public PointQuadtree<E> getChild(int quadrant) {
		if (quadrant==1) return c1;
		if (quadrant==2) return c2;
		if (quadrant==3) return c3;
		if (quadrant==4) return c4;
		return null;
	}

	/**
	 * Returns whether or not there is a child at the given quadrant, 1-4
	 * @param quadrant	1 through 4
	 */
	public boolean hasChild(int quadrant) {
		return (quadrant==1 && c1!=null) || (quadrant==2 && c2!=null) || (quadrant==3 && c3!=null) || (quadrant==4 && c4!=null);
	}

	/**
	 * Inserts the point into the tree
	 */
	public void insert(E p2) {
		//To insert point p at (x,y)
		//If child 1 exists, then insert p in child 1
		if (p2.getX() > point.getX() && p2.getX() < getX2() && p2.getY() < point.getY() && p2.getY() > getY1() ) {
			if (hasChild(1)) {
				c1.insert(p2);
				// else set child 1 to a new tree holding just p
			} else {
				c1 = new PointQuadtree<E>(p2, (int) point.getX(), y1, x2,(int) point.getY());
			}
		}
		// Do the same thing with the other quadrants
		if (p2.getX() < point.getX() && p2.getX() > getX1() && p2.getY() < point.getY() && p2.getY() > getY1()) {
			if (hasChild(2)) {
				c2.insert(p2);
			} else {
				c2 = new PointQuadtree<E>(p2, x1, y1, (int) point.getX(),(int) point.getY());
			}
		}
		// Do the same thing with the other quadrants
		if (p2.getX() < point.getX() && p2.getX() > x1 && p2.getY() > point.getY() && p2.getY() < getY2() ) {
			if (hasChild(3)) {
				c3.insert(p2);
			} else {
				c3 = new PointQuadtree<E>(p2, x1,(int) point.getY(), (int) point.getX(), y2);
			}
		}
		// Do the same thing with the other quadrants
		if (p2.getX() < getX2() && p2.getX() > point.getX() && p2.getY() > point.getY() && p2.getY() < getY2() ) {
			if (hasChild(4)) {
				c4.insert(p2);
			} else {
				c4 = new PointQuadtree<E>(p2, (int) point.getX(), (int) point.getY(), x2, y2);
			}
		}
		// TODO: YOUR CODE HERE
	}
	
	/**
	 * Finds the number of points in the quadtree (including its descendants)
	 */
	public int size() {
		// Uses recursion to add to the total 'num' that indicates the size
		int num = 1;
		if (getChild(1) != null) num += c1.size();
		if (getChild(2) != null) num += c2.size();
		if (getChild(3) != null) num += c3.size();
		if (getChild(4) != null) num += c4.size();
		return num;



		// TODO: YOUR CODE HERE
	}
	
	/**
	 * Builds a list of all the points in the quadtree (including its descendants)
	 */
	public List<E> allPoints() {
		// returns list that is developed through helper method so a new list
		// is not made everytime a point is added.
		ArrayList<E> points = new ArrayList<>();
		build(points);
		return points;
		// TODO: YOUR CODE HERE

	}	

	/**
	 * Uses the quadtree to find all points within the circle
	 * @param cx	circle center x
	 * @param cy  	circle center y
	 * @param cr  	circle radius
	 * @return    	the points in the circle (and the qt's rectangle)
	 */
	public List<E> findInCircle(double cx, double cy, double cr) {
		// To find all points within the circle, stored in a tree covering with x1, y1, x2, y2 boundaries
		// start with new list
		ArrayList<E> found = new ArrayList<>();
		// if the circle intersects the rectangle
		if (Geometry.circleIntersectsRectangle(cx, cy, cr, getX1(), getY1(), getX2(), getY2())) {
			// if the tree's point is in the circle, then the blob is a "hit"
			if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) {
				found.add(getPoint());
				System.out.println(found);
			}
			// for each quadrant with a child, recurse
			for (int i = 1; i <= 4; i ++) {
				if (hasChild(i)) {
					found.addAll(getChild(i).findInCircle(cx,cy,cr));
				}
			}

		}



		return found;

		// TODO: YOUR CODE HERE
	}

	private void build(ArrayList<E> lists) {

		// helper function that builds the array of points
		// adds a point and recurse over its children without starting
		// a new list for each child
		lists.add(point);
		if (hasChild(1)) c1.build(lists);
		if (hasChild(2)) c2.build(lists);
		if (hasChild(3)) c3.build(lists);
		if (hasChild(4)) c4.build(lists);
	}
	// TODO: YOUR CODE HERE for any helper methods.

}
