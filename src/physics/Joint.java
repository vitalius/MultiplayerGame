/**
 * 
 * From Erin Catto's 2006 GDC Game Engine "Box2D"
 *
 * Copyright (c) 2006 Erin Catto http://www.gphysics.com
 *
 * Permission to use, copy, modify, distribute and sell this software
 * and its documentation for any purpose is hereby granted without fee,
 * provided that the above copyright notice appear in all copies.
 * Erin Catto makes no representations about the suitability 
 * of this software for any purpose.  
 * It is provided "as is" without express or implied warranty.
 *
 * Modified for jig.engine... 
 */
package physics;

import java.awt.geom.AffineTransform;
import java.util.List;

import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.util.Matrix22;
import jig.engine.util.Vector2D;

/**
 * A Joint that constrains the motion of two <code>Box</code> objects.
 * 
 * @author Erin Catto
 * @author (Ported by) Scott Wallace
 * 
 */
public class Joint {

	public static final int RED = 0;

	public static final int BLACK = 1;

	public static final int WHITE = 2;

	public static final int BOX_IMAGE = 0;

	public static final int TARGET_IMAGE = 1;

	public static final int DOT_IMAGE = 2;

	private static final int NSTYLES = 3;

	static List<ImageResource> jointImages;

	static double halfJointImgWidth;

	static double halfJointImgHeight;

	static int jointFrame = 0;

	Vector2D localAnchor1, localAnchor2;

	Vector2D r1, r2;

	Vector2D bias;

	Vector2D accumulatedImpulse;

	Box body1, body2;

	double relaxation;

	Matrix22 m;

	/**
	 * Creates a new joint between two Boxes.
	 * 
	 * @param b1
	 *            one box being constrained
	 * @param b2
	 *            the other box being constrained
	 * @param anchor
	 *            the initial position of the joint itself
	 * @param relax
	 *            a value in the range [0.0, 1.0] makes sense. 0.0 means that
	 *            the impulse is calculated from 'scratch' each time, while 1.0
	 *            provides a 'warm start' by using the value from the previous
	 *            round as an initial guess
	 * 
	 * 
	 */
	public Joint(final Box b1, final Box b2, final Vector2D anchor,
			final double relax) {

		body1 = b1;
		body2 = b2;

		Matrix22 rot1 = new Matrix22(body1.rotation);
		Matrix22 rot2 = new Matrix22(body2.rotation);

		Matrix22 rot1T = rot1.transpose();
		Matrix22 rot2T = rot2.transpose();

		localAnchor1 = rot1T.multiply(anchor.difference(body1
				.getCenterPosition()));
		localAnchor2 = rot2T.multiply(anchor.difference(body2
				.getCenterPosition()));

		accumulatedImpulse = new Vector2D(0, 0);
		relaxation = relax;

	}

	/**
	 * Performs the once-per-update-loop initialization that will allow the
	 * iterative calculations which determine the final positions of the boxes
	 * and anchor.
	 * 
	 * @param inverseDeltaTime
	 *            the inverse time since the last iteration of the game loop
	 */
	void preStep(final double inverseDeltaTime) {
		Matrix22 rot1 = new Matrix22(body1.rotation);
		Matrix22 rot2 = new Matrix22(body2.rotation);

		r1 = rot1.multiply(localAnchor1);
		r2 = rot2.multiply(localAnchor2);

		Matrix22 k1 = new Matrix22(body1.inverseMass + body2.inverseMass, 0.0,
				0.0, body1.inverseMass + body2.inverseMass);

		Matrix22 k2 = new Matrix22(body1.invMomentOfInertia * r1.getY()
				* r1.getY(), -body1.invMomentOfInertia * r1.getX() * r1.getY(),
				-body1.invMomentOfInertia * r1.getX() * r1.getY(),
				body1.invMomentOfInertia * r1.getX() * r1.getX());

		Matrix22 k3 = new Matrix22(body2.invMomentOfInertia * r2.getY()
				* r2.getY(), -body2.invMomentOfInertia * r2.getX() * r2.getY(),
				-body2.invMomentOfInertia * r2.getX() * r2.getY(),
				body2.invMomentOfInertia * r2.getX() * r2.getX());

		Matrix22 k = k1.add(k2).add(k3);
		m = k.invert();

		Vector2D p1 = body1.getCenterPosition().translate(r1);
		Vector2D p2 = body2.getCenterPosition().translate(r2);
		Vector2D dp = p2.difference(p1);
		bias = dp.scale(-.1 * inverseDeltaTime);

		// when relaxation is non-zero, we're essentially doing
		// a 'warm start' (using the impulse from the last time
		// as an initial guess).
		accumulatedImpulse = accumulatedImpulse.scale(relaxation);

		body1.setVelocity(body1.getVelocity().difference(
				accumulatedImpulse.scale(body1.inverseMass)));
		body1.angularVelocity -= body1.invMomentOfInertia
				* r1.cross(accumulatedImpulse);

		body2.setVelocity(body2.getVelocity().translate(
				accumulatedImpulse.scale(body2.inverseMass)));
		body2.angularVelocity += body2.invMomentOfInertia
				* r2.cross(accumulatedImpulse);

	}

	/**
	 * Iteratively applies impulses to calculate the final positions of the
	 * boxes and anchor on each iteration of the game loop.
	 * 
	 */
	public void applyImpulse() {
		Vector2D dv = body2.getVelocity().translate(
				r2.dCrossV(body2.angularVelocity));
		Vector2D q = body1.getVelocity().translate(
				r1.dCrossV(body1.angularVelocity));

		dv = dv.difference(q);

		Vector2D impulse = m.multiply(bias.difference(dv));

		body1.setVelocity(body1.getVelocity().difference(
				impulse.scale(body1.inverseMass)));
		body1.angularVelocity -= body1.invMomentOfInertia * r1.cross(impulse);

		body2.setVelocity(body2.getVelocity().translate(
				impulse.scale(body2.inverseMass)));
		body2.angularVelocity += body2.invMomentOfInertia * r2.cross(impulse);

		accumulatedImpulse = accumulatedImpulse.translate(impulse);
	}

	/**
	 * Selects the image to use when displaying the joint's markup.
	 * 
	 * @param color
	 *            one of <code>RED</code>, <code>BLACK</code> or
	 *            <code>WHITE</code>
	 * @param style
	 *            one of <code>BOX_IMAGE</code>, <code>TARGET_IMAGE</code>
	 *            or <code>DOT_IMAGE</code>
	 */
	public static void setJointMarkup(final int color, final int style) {
		int img = color * NSTYLES + style;
		if (img > 0 && img < jointImages.size()) {
			jointFrame = img;
		} else {
			throw new IllegalArgumentException("Joint Markup Image " + img
					+ " is undefined");
		}

	}

	/**
	 * Renders the Joint's markup.
	 * 
	 * @param rc
	 *            the game frame's rendering context
	 * @see CattoPhysicsEngine#renderPhysicsMarkup(RenderingContext)
	 */
	void render(final RenderingContext rc) {
		Vector2D c = new Matrix22(body1.rotation).multiply(localAnchor1);
		Vector2D v = body1.getCenterPosition().translate(c);

		Vector2D d = new Matrix22(body2.rotation).multiply(localAnchor2);
		Vector2D w = body2.getCenterPosition().translate(d);

		AffineTransform at = AffineTransform.getTranslateInstance(v.getX()
				- halfJointImgWidth, v.getY() - halfJointImgHeight);
		Joint.jointImages.get(jointFrame).render(rc, at);
		at = AffineTransform.getTranslateInstance(w.getX() - halfJointImgWidth,
				w.getY() - halfJointImgHeight);
		Joint.jointImages.get(jointFrame).render(rc, at);
	}

	/**
	 * Loads the joint images which will be displayed if the physics engine is
	 * set to render its markup.
	 * 
	 * @return <code>true</code> iff the images were successfully loaded
	 * 
	 * @see CattoPhysicsEngine#renderPhysicsMarkup(RenderingContext)
	 */
	static boolean loadImages() {
		if (jointImages != null) {
			return true;
		}
		// suppress the warning message that would be issued if
		// we don't 'preload' the images...which really we aren't doing ;)
		ResourceFactory f = ResourceFactory.getFactory();
		f.loadSheet("jig/resources/small_boxes.png", "jig/resources/small_boxes.xml");
		jointImages = f.getFrames("jig/resources/small_boxes.png#small_boxes");

		if (jointImages == null) {
			return false;
		}
		halfJointImgWidth = jointImages.get(0).getWidth() / 2.0;
		halfJointImgHeight = jointImages.get(0).getWidth() / 2.0;

		return true;
	}

}
