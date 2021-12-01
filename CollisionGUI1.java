import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * Using a quadtree for collision detection
 * 
 * @author Chris Bailey-Kellogg, Dartmouth CS 10, Spring 2015
 * @author CBK, Spring 2016, updated for blobs
 * @author CBK, Fall 2016, using generic PointQuadtree
 */
public class CollisionGUI extends DrawingGUI {
	private static final int width = 800, height = 600;        // size of the universe

	private List<Blob> blobs;                        // all the blobs
	private List<Blob> colliders;                    // the blobs who collided at this step
	private char blobType = 'b';                        // what type of blob to create
	private char collisionHandler = 'c';                // when there's a collision, 'c'olor them, or 'd'estroy them
	private int delay = 100;                            // timer control

	public CollisionGUI() {
		super("super-collider", width, height);

		blobs = new ArrayList<Blob>();

		// Timer drives the animation.
		startTimer();
	}

	/**
	 * Adds an blob of the current blobType at the location
	 */
	private void add(int x, int y) {
		if (blobType == 'b') {
			blobs.add(new Bouncer(x, y, width, height));
		} else if (blobType == 'w') {
			blobs.add(new Wanderer(x, y));
		}
		else {
			System.err.println("Unknown blob type " + blobType);
		}
	}

	/**
	 * DrawingGUI method, here creating a new blob
	 */
	public void handleMousePress(int x, int y) {
		add(x, y);
		repaint();
	}

	/**
	 * DrawingGUI method
	 */
	public void handleKeyPress(char k) {
		if (k == 'f') { // faster
			if (delay > 1) delay /= 2;
			setTimerDelay(delay);
			System.out.println("delay:" + delay);
		} else if (k == 's') { // slower
			delay *= 2;
			setTimerDelay(delay);
			System.out.println("delay:" + delay);
		} else if (k == 'r') { // add some new blobs at random positions
			for (int i = 0; i < 10; i++) {
				add((int) (width * Math.random()), (int) (height * Math.random()));
				repaint();
			}
		} else if (k == 'c' || k == 'd') { // control how collisions are handled
			collisionHandler = k;
			System.out.println("collision:" + k);
		}else if (k == '1') {
			test1();

		}else if (k == '2') {
			test2();

		}else { // set the type for new blobs
			blobType = k;
		}
	}

	/**
	 * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
	 */
	public void draw(Graphics g) {

		// Draw method that draws each blob in blobs, while constantly
		// checking for colliders and painting them accordingly.
		for (Blob blob : blobs) {
			blob.draw(g);
		}
		if (colliders != null) {
			for (Blob collider : colliders) {
				g.setColor(Color.red);
				collider.draw(g);
			}

		}
		// TODO: YOUR CODE HERE
		// Ask all the blobs to draw themselves.
		// Ask the colliders to draw themselves in red.
	}

	/**
	 * Sets colliders to include all blobs in contact with another blob
	 */
	private void findColliders() {
		// find colliders method that goes through list of blobs and sees if they are close enough
		// to be considered colliders
		PointQuadtree<Blob> allBlobs = null;
		// create new list of blobs
		colliders = new ArrayList<Blob>();
		// instantiate list of colliders
		for (Blob blob : blobs) {
			// loop through blobs
			if (allBlobs == null) allBlobs = new PointQuadtree<>(blob,0,0,800,600);
			//as long as allBlobs is null, start a new tree and start to add blobs
			allBlobs.insert(blob);
			for (Blob collide: allBlobs.allPoints()) {
				// for every blob in the tree check if they are close enough to being considered blobs
				// then add them to list of colliders
				if (Geometry.pointInCircle(blob.x,blob.y,collide.x,collide.y,2* collide.r) && blob != collide) {
					colliders.add(collide);
					colliders.add(blob);
					System.out.println(colliders.size());
				}
			}

		}

		// TODO: YOUR CODE HERE
		// Create the tree
		// For each blob, see if anybody else collided with it
	}

	/**
	 * DrawingGUI method, here moving all the blobs and checking for collisions
	 */
	public void handleTimer() {
		// Ask all the blobs to move themselves.
		for (Blob blob : blobs) {
			blob.step();
		}
		// Check for collisions
		if (blobs.size() > 0) {
			findColliders();
			if (collisionHandler=='d') {
				blobs.removeAll(colliders);
				colliders = null;
			}
		}
		// Now update the drawing
		repaint();
	}

	private void test1() {
		// test one, tests what happens when the blob will touch each other
		blobs.add(new Bouncer(200,300, width, height));
		blobs.add(new Bouncer(200,300, width, height));

	}
	private void test2() {
		// test two, tests what happens when the blobs will never touch each other
		blobs.add(new Wanderer(200, 300));
		blobs.add(new Wanderer(300, 300));
	}



	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CollisionGUI();
			}
		});
	}
}

