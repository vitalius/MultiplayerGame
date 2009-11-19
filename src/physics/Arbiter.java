package physics;

import java.awt.geom.AffineTransform;
import java.util.List;

import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.util.Matrix22;
import jig.engine.util.Vector2D;
import jig.engine.util.VectorOpsBuffer;

/**
 * An dynamic arbitration object created to respond to collisions between Catto
 * <code>Box</code> objects. For each collision point between two
 * <code>Box</code>es, an <code>Arbiter</code> is created to accumulate and
 * track impulse calculations.
 * 
 * @author Erin Catto
 * @author (Ported by) Scott Wallace
 * 
 */
public class Arbiter {
	static final boolean WATCH_IMPULSE = false;

	private static final int FACE_AX = 1;

	private static final int FACE_AY = 2;

	private static final int FACE_BX = 3;

	private static final int FACE_BY = 4;

	static List<ImageResource> arbiterImages;

	static int halfArbiterImgWidth;

	static int halfArbiterImgHeight;

	protected int numContacts;

	protected Contact[] contacts;

	// DESIGN: this needs to be protected, have a get() method, or go away
	public Box body1, body2;

	protected double friction;

	/**
	 * Creates an Arbiter between two Boxes which share a contact point.
	 * 
	 * @param b1
	 *            a box touching <code>b2</code>
	 * @param b2
	 *            another box touching <code>b1</code>
	 */
	protected Arbiter(final Box b1, final Box b2) {
		if (b1.compareTo(b2) < 0) {
			body1 = b1;
			body2 = b2;
		} else {
			body1 = b2;
			body2 = b1;
		}
		contacts = new Contact[2];
		for (int i = 0; i < 2; i++) {
			contacts[i] = new Contact();
		}
		numContacts = collide(contacts, body1, body2);

		friction = Math.sqrt(body1.friction * body2.friction);

	}

	/**
	 * Loads the arbiter images which will be displayed if the physics engine is
	 * set to render its markup.
	 * 
	 * @return <code>true</code> iff the images were successfully loaded
	 * 
	 * @see CattoPhysicsEngine#renderPhysicsMarkup(RenderingContext)
	 */
	static boolean loadImages() {
		if (arbiterImages != null) {
			return true;
		}

		// suppress the warning message that would be issued if
		// we don't 'preload' the images...which really we aren't doing ;)
		ResourceFactory f = ResourceFactory.getFactory();
		f.loadSheet("jig/resources/small_boxes.png", "jig/resources/small_boxes.xml");
		arbiterImages = f.getFrames("jig/resources/small_boxes.png#small_boxes");

		if (arbiterImages == null) {
			return false;
		}

		halfArbiterImgWidth = arbiterImages.get(0).getWidth() / 2;
		halfArbiterImgHeight = arbiterImages.get(0).getWidth() / 2;
		return true;
	}

	/**
	 * Renders the Arbiter's markup.
	 * 
	 * @param rc
	 *            the game frame's rendering context
	 * @see CattoPhysicsEngine#renderPhysicsMarkup(RenderingContext)
	 */
	void render(final RenderingContext rc) {
		AffineTransform at;
		for (int i = 0; i < numContacts; i++) {
			at = AffineTransform.getTranslateInstance(contacts[i].position
					.getX()
					- halfArbiterImgWidth, contacts[i].position.getY()
					- halfArbiterImgHeight);
			arbiterImages.get(0).render(rc, at);
		}
	}

	/**
	 * Updates the Arbiter with new contact points stored in the 
	 * temporary/potential Arbiter.
	 * 
	 * @param newArbiter
	 *            a (temporary) Arbiter containing updated contact points
	 *            which will replace the contact points in this Arbiter
	 */
	protected void update(final Arbiter newArbiter) {
		Contact[] mergedContacts = { new Contact(), new Contact() };
		Contact cNew, cOld, c;
		int k;
		int j;

		for (int i = 0; i < newArbiter.numContacts; i++) {
			cNew = newArbiter.contacts[i];
			k = -1;
			for (j = 0; j < numContacts; j++) {
				cOld = contacts[j];
				if (cNew.feature.equals(cOld.feature)) {
					k = j;
					break;
				}
			}
			// NOTE (performance) it unclear if this is faster than replacement
			if (k > -1) {
				cOld = contacts[k];
				mergedContacts[i] = new Contact(cNew);
				c = mergedContacts[i];
				c.accNormalImpulse = cOld.accNormalImpulse;
				c.accTangetImpulse = cOld.accTangetImpulse;
			} else {
				mergedContacts[i] = newArbiter.contacts[i];
			}
		}
		for (int i = 0; i < newArbiter.numContacts; i++) {
			contacts[i] = mergedContacts[i];
		}
		numContacts = newArbiter.numContacts;
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
		final double kAllowedPenetration = .01;
		Contact c;
		double rn1, rn2, kNormal, rt1, rt2, kTangent;
		Vector2D tangent;
		Vector2D impulse;

		for (int i = 0; i < numContacts; i++) {
			c = contacts[i];
			Vector2D r1 = c.position.difference(body1.getCenterPosition());
			Vector2D r2 = c.position.difference(body2.getCenterPosition());

			rn1 = r1.dot(c.normal);
			rn2 = r2.dot(c.normal);
			kNormal = body1.inverseMass + body2.inverseMass;
			kNormal += body1.invMomentOfInertia * (r1.dot(r1) - rn1 * rn1)
					+ body2.invMomentOfInertia * (r2.dot(r2) - rn2 * rn2);
			c.massNormal = 1.0 / kNormal;

			tangent = c.normal.dCrossV(-1.0);
			rt1 = r1.dot(tangent);
			rt2 = r2.dot(tangent);
			kTangent = body1.inverseMass + body2.inverseMass;
			kTangent += body1.invMomentOfInertia * (r1.dot(r1) - rt1 * rt1)
					+ body2.invMomentOfInertia * (r2.dot(r2) - rt2 * rt2);
			c.massTangent = 1.0 / kTangent;

			c.bias = -.1f * inverseDeltaTime
					* Math.min(0.0, c.separation + kAllowedPenetration);

			// apply normal + friction impulse
			impulse = c.normal.scale(c.accNormalImpulse).translate(
					tangent.scale(c.accTangetImpulse));

			body1.setVelocity(body1.getVelocity().translate(
					impulse.scale(-1.0 * body1.inverseMass)));
			double applied = body1.invMomentOfInertia * r1.cross(impulse);
			body1.angularVelocity -= applied;

			body2.setVelocity(body2.getVelocity().translate(
					impulse.scale(body2.inverseMass)));
			applied = body2.invMomentOfInertia * r2.cross(impulse);
			body2.angularVelocity += applied;
		}
	}

	/**
	 * Applies the calculated impulses to the bodies managed by this arbiter.
	 * 
	 */
	void applyImpulse() {

		Box b1 = body1;
		Box b2 = body2;
		Contact c;
		Vector2D r1, r2, impulse, tangent;
		VectorOpsBuffer dv;
		double vn, normalImpulse, oldNormalImpulse;
		double maxTangentImpulse, vt, tangentImpulse, oldTangentImpulse;

		for (int i = 0; i < numContacts; i++) {
			c = contacts[i];
			r1 = c.position.difference(b1.getCenterPosition());
			r2 = c.position.difference(b2.getCenterPosition());

			dv = new VectorOpsBuffer(b2.getVelocity());
			dv.translateMe(r2.dCrossV(b2.angularVelocity));
			dv.differenceMe(b1.getVelocity());
			dv.translateMe(r1.dCrossV(-1.0 * b1.angularVelocity));

			// compute normal impulse
			vn = dv.getDot(c.normal);
			normalImpulse = c.massNormal * (-vn + c.bias);

			// Clamp
			oldNormalImpulse = c.accNormalImpulse;
			c.accNormalImpulse = Math
					.max(oldNormalImpulse + normalImpulse, 0.0);
			normalImpulse = c.accNormalImpulse - oldNormalImpulse;

			// Apply contact impulse
			impulse = c.normal.scale(normalImpulse);

			// b1.velocity.translateMe( impulse.scale( -1.0 * b1.inverseMass )
			// );
			b1.setVelocity(b1.getVelocity().translate(
					impulse.scale(-1.0 * b1.inverseMass)));

			double applied = b1.invMomentOfInertia * r1.cross(impulse);
			b1.angularVelocity -= applied;

			b2.setVelocity(b2.getVelocity().translate(
					impulse.scale(b2.inverseMass)));

			applied = b2.invMomentOfInertia * r2.cross(impulse);
			b2.angularVelocity += applied;

			// relative velocity at contact
			// dv = b2.velocity.translate( r2.vCrossD( -1.0 * b2.angularVelocity
			// ) );
			// dv.differenceMe( b1.velocity ).translateMe( r1.vCrossD(
			// b1.angularVelocity ));
			dv = new VectorOpsBuffer(b2.getVelocity());
			dv.translateMe(r2.dCrossV(b2.angularVelocity));
			dv.differenceMe(b1.getVelocity());
			dv.translateMe(r1.dCrossV(-1.0 * b1.angularVelocity));

			maxTangentImpulse = friction * c.accNormalImpulse;
			tangent = c.normal.dCrossV(-1.0);

			vt = dv.getDot(tangent);
			tangentImpulse = c.massTangent * -vt;

			// clamp
			oldTangentImpulse = c.accTangetImpulse;
			c.accTangetImpulse = clamp(oldTangentImpulse + tangentImpulse,
					-maxTangentImpulse, maxTangentImpulse);
			tangentImpulse = c.accTangetImpulse - oldTangentImpulse;

			impulse = tangent.scale(tangentImpulse);
			b1.setVelocity(b1.getVelocity().difference(
					impulse.scale(b1.inverseMass)));
			applied = b1.invMomentOfInertia * r1.cross(impulse);
			b1.angularVelocity -= applied;

			applied = b2.invMomentOfInertia * r2.cross(impulse);
			b2.setVelocity(b2.getVelocity().translate(
					impulse.scale(b2.inverseMass)));
			b2.angularVelocity += applied;
		}

	}

	/**
	 * Clamps a value between low and high bounds.
	 * 
	 * @param a
	 *            the value to be clamped
	 * @param low
	 *            the lower bound on a
	 * @param high
	 *            the upper bound on a
	 * @return the clamped value
	 */
	static double clamp(final double a, final double low, final double high) {
		return Math.max(low, Math.min(a, high));

	}

	/**
	 * Clips a line.
	 * 
	 * TODO: what does this actually do? Improve parameter documentation
	 * 
	 * @param vOut
	 *            the vertex output array. These ClipVerticies are initialized
	 *            by this method. There should be 2 ClipVerticies in this array,
	 *            created with the default constructor.
	 * 
	 * @param vIn
	 *            the vertex input array. These verticies have not been clipped,
	 *            but are used to initialize the clipped verticies.
	 * @param normal
	 *            the normal to the reference edge
	 * @param offset
	 *            the relative position
	 * @param clipEdge
	 *            an edge identifier
	 * @return the number of valid verticies in the vOut array
	 */
	static int clipSegmentToLine(final ClipVertex[] vOut,
			final ClipVertex[] vIn, final Vector2D normal, final double offset,
			final int clipEdge) {

		int numOut = 0;

		double d0 = normal.dot(vIn[0].getV()) - offset;
		double d1 = normal.dot(vIn[1].getV()) - offset;

		if (d0 <= 0.0) {
			vOut[numOut++] = vIn[0];
		}
		if (d1 <= 0.0) {
			vOut[numOut++] = vIn[1];
		}

		if (d0 * d1 < 0.0) {

			// Find intersection point of edge and plane
			double interp = d0 / (d0 - d1);
			// vOut[numOut].v = vIn[0].v.translate( vIn[1].v.difference(
			// vIn[0].v ).scale( interp ));
			vOut[numOut].vx = vIn[0].vx + (vIn[1].vx - vIn[0].vx) * interp;
			vOut[numOut].vy = vIn[0].vy + (vIn[1].vy - vIn[0].vy) * interp;

			if (d0 > 0.0) {
				vOut[numOut].fp = vIn[0].fp;
				vOut[numOut].fp.inEdge1 = clipEdge;
				vOut[numOut].fp.inEdge2 = FeaturePair.NO_EDGE;
			} else {
				vOut[numOut].fp = vIn[1].fp;
				vOut[numOut].fp.outEdge1 = clipEdge;
				vOut[numOut].fp.outEdge2 = FeaturePair.NO_EDGE;
			}
			numOut++;
		}
		return numOut;

	}

	/**
	 * @return a hash code for the Arbiter.
	 */
	@Override
	public int hashCode() {
		return body1.hashCode() * 7 + body2.hashCode() * 3;

	}

	/**
	 * Determines if two Arbiters are equal.
	 * 
	 * @param o
	 *            the object to compare against
	 * @return <code>true</code> iff the arbiters both mange the same box
	 *         instances.
	 */
	@Override
	public boolean equals(final Object o) {
		if (o instanceof Arbiter) {
			return ((body1.id == ((Arbiter) o).body1.id) 
					&& (body2.id == ((Arbiter) o).body2.id));
		}
		return false;
	}

	/**
	 * Computes the incident edge.
	 * 
	 * TODO improve parameter documentation
	 * 
	 * @param c
	 *            a 2 element vector of ClipVerticies which are initialized
	 *            during this method.
	 * 
	 * @param h
	 *            a vector
	 * @param pos
	 *            another vector
	 * @param rot
	 *            the rotation matrix
	 * @param normal
	 *            the normal
	 */
	static void computeIncidentEdge(final ClipVertex[] c, final Vector2D h,
			final Vector2D pos, final Matrix22 rot, final Vector2D normal) {

		Matrix22 rotT = rot.transpose();
		Vector2D n = (rotT.multiply(normal).scale(-1.0));
		Vector2D nAbs = n.abs();

		if (nAbs.getX() > nAbs.getY()) {
			if (n.getX() > 0.0) {
				c[0].vx = h.getX();
				c[0].vy = -h.getY();
				c[0].fp.inEdge2 = FeaturePair.EDGE3;
				c[0].fp.outEdge2 = FeaturePair.EDGE4;

				c[1].vx = h.getX();
				c[1].vy = h.getY();
				c[1].fp.inEdge2 = FeaturePair.EDGE4;
				c[1].fp.outEdge2 = FeaturePair.EDGE1;

			} else {
				c[0].vx = -h.getX();
				c[0].vy = h.getY();
				c[0].fp.inEdge2 = FeaturePair.EDGE1;
				c[0].fp.outEdge2 = FeaturePair.EDGE2;

				c[1].vx = -h.getX();
				c[1].vy = -h.getY();
				c[1].fp.inEdge2 = FeaturePair.EDGE2;
				c[1].fp.outEdge2 = FeaturePair.EDGE3;

			}
		} else {

			if (n.getY() > 0.0) {
				c[0].vx = h.getX();
				c[0].vy = h.getY();
				c[0].fp.inEdge2 = FeaturePair.EDGE4;
				c[0].fp.outEdge2 = FeaturePair.EDGE1;

				c[1].vx = -h.getX();
				c[1].vy = h.getY();
				c[1].fp.inEdge2 = FeaturePair.EDGE1;
				c[1].fp.outEdge2 = FeaturePair.EDGE2;

			} else {
				c[0].vx = -h.getX();
				c[0].vy = -h.getY();
				c[0].fp.inEdge2 = FeaturePair.EDGE2;
				c[0].fp.outEdge2 = FeaturePair.EDGE3;

				c[1].vx = h.getX();
				c[1].vy = -h.getY();
				c[1].fp.inEdge2 = FeaturePair.EDGE3;
				c[1].fp.outEdge2 = FeaturePair.EDGE4;

			}
		}

		// c[0].v = rot.multiply( c[0].v ).translateMe( pos );
		// c[1].v = rot.multiply( c[1].v ).translateMe( pos );
		Vector2D rc0 = rot.multiply(c[0].getV());
		Vector2D rc1 = rot.multiply(c[1].getV());
		c[0].vx = rc0.getX() + pos.getX();
		c[0].vy = rc0.getY() + pos.getY();
		c[1].vx = rc1.getX() + pos.getX();
		c[1].vy = rc1.getY() + pos.getY();

	}

	/**
	 * Computes the contact points between two boxes.
	 * 
	 * @param contacts
	 *            an array of 2 Contact objects created with the default
	 *            construct and uninitialized.
	 * 
	 * @param bodyA
	 *            one box involved in the collision
	 * @param bodyB
	 *            the other box involved in the collision
	 * @return the number of valid contact points in the array
	 */
	private static int collide(final Contact[] contacts, final Box bodyA,
			final Box bodyB) {

		// Setup
		Vector2D hA = new Vector2D(bodyA.halfWidth, bodyA.halfHeight);
		Vector2D hB = new Vector2D(bodyB.halfWidth, bodyB.halfHeight);

		Vector2D posA = bodyA.getCenterPosition();
		Vector2D posB = bodyB.getCenterPosition();

		Matrix22 rotA = new Matrix22(bodyA.rotation);
		Matrix22 rotB = new Matrix22(bodyB.rotation);

		Matrix22 rotAT = rotA.transpose();
		Matrix22 rotBT = rotB.transpose();

		Vector2D dp = posB.difference(posA);
		Vector2D dA = rotAT.multiply(dp);
		Vector2D dB = rotBT.multiply(dp);

		Matrix22 c = rotAT.multiply(rotB);
		Matrix22 absC = c.abs();
		Matrix22 absCT = absC.transpose();

		VectorOpsBuffer faceA = new VectorOpsBuffer(dA);
		faceA.absMe();
		faceA.differenceMe(hA);
		faceA.differenceMe(absC.multiply(hB));

		if (faceA.x > 0.0 || faceA.y > 0.0) {
			return 0;
		}

		VectorOpsBuffer faceB = new VectorOpsBuffer(dB);
		faceB.absMe();
		faceB.differenceMe(absCT.multiply(hA));
		faceB.differenceMe(hB);

		if (faceB.x > 0.0 || faceB.y > 0.0) {
			return 0;
		}

		// Find best axis
		int axis;
		double seperation;
		Vector2D normal;

		axis = FACE_AX;
		seperation = faceA.x;
		if (dA.getX() > 0.0) {
			normal = new Vector2D(rotA.a, rotA.c);
		} else {
			normal = new Vector2D(-rotA.a, -rotA.c);
		}

		if (faceA.y > 1.05 * seperation + .01 * hA.getY()) {
			axis = FACE_AY;
			seperation = faceA.y;
			if (dA.getY() > 0) {
				normal = new Vector2D(rotA.b, rotA.d);
			} else {
				normal = new Vector2D(-rotA.b, -rotA.d);
			}
		}

		if (faceB.x > 1.05f * seperation + .01f * hB.getX()) {
			axis = FACE_BX;
			seperation = faceB.x;
			if (dB.getX() > 0.0) {
				normal = new Vector2D(rotB.a, rotB.c);
			} else {
				normal = new Vector2D(-rotB.a, -rotB.c);
			}
		}

		if (faceB.y > 1.05f * seperation + .01f * hB.getY()) {
			axis = FACE_BY;
			seperation = faceB.y;
			if (dB.getY() > 0.0) {
				normal = new Vector2D(rotB.b, rotB.d);
			} else {
				normal = new Vector2D(-rotB.b, -rotB.d);
			}
		}

		// Setup clipping plane
		Vector2D frontNormal = new Vector2D(0, 0), sideNormal = new Vector2D(0,
				0);
		ClipVertex[] incidentEdge = { new ClipVertex(), new ClipVertex() };
		double front = 0, negSide = 0, posSide = 0;
		int negEdge = 0, posEdge = 0;
		double side = 0;

		switch (axis) {
		case FACE_AX:
			frontNormal = normal;
			front = posA.dot(frontNormal) + hA.getX();
			sideNormal = new Vector2D(rotA.b, rotA.d);
			side = posA.dot(sideNormal);
			negSide = -side + hA.getY();
			posSide = side + hA.getY();
			negEdge = FeaturePair.EDGE3;
			posEdge = FeaturePair.EDGE1;
			computeIncidentEdge(incidentEdge, hB, posB, rotB, frontNormal);

			break;

		case FACE_AY:
			frontNormal = normal;
			front = posA.dot(frontNormal) + hA.getY();
			sideNormal = new Vector2D(rotA.a, rotA.c);
			side = posA.dot(sideNormal);
			negSide = -side + hA.getX();
			posSide = side + hA.getX();
			negEdge = FeaturePair.EDGE2;
			posEdge = FeaturePair.EDGE4;
			computeIncidentEdge(incidentEdge, hB, posB, rotB, frontNormal);

			break;

		case FACE_BX:

			frontNormal = normal.scale(-1.0);
			front = posB.dot(frontNormal) + hB.getX();
			sideNormal = new Vector2D(rotB.b, rotB.d);
			side = posB.dot(sideNormal);
			negSide = -side + hB.getY();
			posSide = side + hB.getY();
			negEdge = FeaturePair.EDGE3;
			posEdge = FeaturePair.EDGE1;
			computeIncidentEdge(incidentEdge, hA, posA, rotA, frontNormal);

			break;

		case FACE_BY:
			frontNormal = normal.scale(-1.0);
			front = posB.dot(frontNormal) + hB.getY();
			sideNormal = new Vector2D(rotB.a, rotB.c);
			side = posB.dot(sideNormal);
			negSide = -side + hB.getX();
			posSide = side + hB.getX();
			negEdge = FeaturePair.EDGE2;
			posEdge = FeaturePair.EDGE4;
			computeIncidentEdge(incidentEdge, hA, posA, rotA, frontNormal);

			break;

		default:
			ResourceFactory.getJIGLogger().warning(
					"Unexpected condition in jig.engine.physics.ecpe.Arbiter");
			break;
		}
		ClipVertex[] clipPoints1 = { new ClipVertex(), new ClipVertex() };
		ClipVertex[] clipPoints2 = { new ClipVertex(), new ClipVertex() };

		int np;

		// Clip to box side 1
		np = clipSegmentToLine(clipPoints1, incidentEdge, sideNormal
				.scale(-1.0), negSide, negEdge);
		if (np < 2) {
			return 0;
		}

		// Clip to - box side 1
		np = clipSegmentToLine(clipPoints2, clipPoints1, sideNormal, posSide,
				posEdge);
		if (np < 2) {
			return 0;
		}

		// Due to roundoff, clipping may remove all points
		int numContacts = 0;
		for (int i = 0; i < 2; ++i) {
			double separation = frontNormal.dot(clipPoints2[i].getV()) - front;

			if (separation <= 0) {
				contacts[numContacts].separation = separation;
				contacts[numContacts].normal = normal;
				// slide contact point onto reference face (easy to cull)
				contacts[numContacts].position = clipPoints2[i].getV()
						.difference(frontNormal.scale(separation));
				contacts[numContacts].feature = clipPoints2[i].fp;
				if (axis == FACE_BX || axis == FACE_BY) {
					contacts[numContacts].feature.flip();
				}
				++numContacts;
			}
		}

		return numContacts;

	}
}

/**
 * A contact point between two bodies.
 * 
 * @author Scott Wallace
 */
class Contact {
	Vector2D position;

	Vector2D normal;

	double separation;

	double accNormalImpulse = 0.0;

	double accTangetImpulse = 0.0;

	double massNormal, massTangent;

	double bias;

	FeaturePair feature;

	/**
	 * Creates a new, essentially uninitialized, contact point between two
	 * bodies.
	 */
	public Contact() {
		position = new Vector2D(0.0, 0.0);
		normal = new Vector2D(0.0, 0.0);
		feature = new FeaturePair();
	}

	/**
	 * Copies a contact point.
	 * 
	 * @param c
	 *            the contact point to copy
	 */
	public Contact(final Contact c) {
		position = c.position; // new Vector2D(c.position);
		normal = c.normal; // new Vector2D(c.normal);
		separation = c.separation;
		accNormalImpulse = c.accNormalImpulse;
		accTangetImpulse = c.accTangetImpulse;
		massNormal = c.massNormal;
		massTangent = c.massTangent;
		bias = c.bias;
		feature = new FeaturePair(c.feature);
	}
}

/**
 * An indicator of where contact points lie with respect to the edges of each
 * box involved in the collision.
 * 
 * <pre>
 *              EDGE1 
 *            +-------+ 
 *     EDGE2  |       |  EDGE4
 *            |       | 
 *            +-------+ 
 *              EDGE3
 * </pre>
 * 
 * @author Erin Catto
 * @author (Ported by) Scott Wallace
 * 
 */
class FeaturePair {
	static final int NO_EDGE = 0;

	static final int EDGE1 = 1;

	static final int EDGE2 = 2;

	static final int EDGE3 = 3;

	static final int EDGE4 = 4;

	int inEdge1;

	int outEdge1;

	int inEdge2;

	int outEdge2;

	/**
	 * Creates a new, essentially uninitialized, FeaturePair.
	 */
	FeaturePair() {
		inEdge1 = 0;
		outEdge1 = 0;
		inEdge2 = 0;
		outEdge2 = 0;
	}

	/**
	 * Copies an existing FeaturePair.
	 * 
	 * @param f
	 *            the FeaturePair to be copied.
	 */
	FeaturePair(final FeaturePair f) {
		inEdge1 = f.inEdge1;
		inEdge2 = f.inEdge2;
		outEdge1 = f.outEdge1;
		outEdge2 = f.outEdge2;

	}

	/**
	 * Determines if two FeaturePairs are equal.
	 * 
	 * @param fp
	 *            the object to compare against.
	 * @return <code>true</code> iff the FeaturePairs are equal
	 */
	@Override
	public boolean equals(final Object fp) {
		if (!(fp instanceof FeaturePair)) {
			return false;
		}
		FeaturePair f = (FeaturePair) fp;

		if (inEdge1 != f.inEdge1) {
			return false;
		}
		if (inEdge2 != f.inEdge2) {
			return false;
		}
		if (outEdge2 != f.outEdge2) {
			return false;
		}
		if (outEdge1 != f.outEdge1) {
			return false;
		}
		return true;
	}

	/**
	 * @return a (perfect/collision free) hash code for the FeaturePair.
	 */
	@Override
	public int hashCode() {
		// maximum = 4 + 20 + 100 + 500 = 624 //
		return outEdge1 + outEdge2 * 5 + inEdge1 * 25 + inEdge2 * 125;
	}

	/**
	 * Flip the feature pair by swapping Edge1 and Edge2.
	 */
	void flip() {
		int tmp;
		tmp = inEdge1;
		inEdge1 = inEdge2;
		inEdge2 = tmp;

		tmp = outEdge1;
		outEdge1 = outEdge2;
		outEdge2 = tmp;
	}
}

/**
 * A point of contact between two boxes managed by the Arbiter.
 * 
 * @author Erin Catto
 * @author (Ported by) Scott Wallace
 * 
 */
class ClipVertex {

	FeaturePair fp;

	double vx, vy;

	/**
	 * Creates a new uninitialized ClipVertex.
	 */
	ClipVertex() {

		fp = new FeaturePair();
		vx = 0.0;
		vy = 0.0;
	}

	/**
	 * @return a vector representation of the ClipVertex position
	 */
	Vector2D getV() {
		return new Vector2D(vx, vy);
	}

}
