package shadows;
 
import camera.Camera;
import math.Matrix4f;
import math.Transform;
import math.Vector3f;
import math.Vector4f;
import rendering.MasterRenderer;
import window.GLFWWindow;
 
/**
 * Represents the 3D cuboidal area of the world in which objects will cast
 * shadows (basically represents the orthographic projection area for the shadow
 * render pass). It is updated each frame to optimise the area, making it as
 * small as possible (to allow for optimal shadow map resolution) while not
 * being too small to avoid objects not having shadows when they should.
 * Everything inside the cuboidal area represented by this object will be
 * rendered to the shadow map in the shadow render pass. Everything outside the
 * area won't be.
 * 
 * @author Karl
 *
 */
public class ShadowBox {
 
    private static final float OFFSET = 10;
    private static final Vector4f UP = new Vector4f(0, 1, 0, 0);
    private static final Vector4f FORWARD = new Vector4f(0, 0, -1, 0);
    private static final float SHADOW_DISTANCE = 400;
 
    private float minX, maxX;
    private float minY, maxY;
    private float minZ, maxZ;
    private Matrix4f lightViewMatrix;
    private Camera cam;
 
    private float farHeight, farWidth, nearHeight, nearWidth;
 
    /**
     * Creates a new shadow box and calculates some initial values relating to
     * the camera's view frustum, namely the width and height of the near plane
     * and (possibly adjusted) far plane.
     * 
     * @param lightViewMatrix
     *            - basically the "view matrix" of the light. Can be used to
     *            transform a point from world space into "light" space (i.e.
     *            changes a point's coordinates from being in relation to the
     *            world's axis to being in terms of the light's local axis).
     * @param camera
     *            - the in-game camera.
     */
    protected ShadowBox(Matrix4f lightViewMatrix, Camera camera) {
        this.lightViewMatrix = lightViewMatrix;
        this.cam = camera;
        calculateWidthsAndHeights();
    }
 
    /**
     * Updates the bounds of the shadow box based on the light direction and the
     * camera's view frustum, to make sure that the box covers the smallest area
     * possible while still ensuring that everything inside the camera's view
     * (within a certain range) will cast shadows.
     */
    protected void update() {
        Matrix4f rotation = calculateCameraRotationMatrix();
        Vector3f forwardVector = new Vector3f(Matrix4f.transform(rotation, FORWARD));
 
        Vector3f toFar = new Vector3f(forwardVector);
        toFar.multiplyScalar(SHADOW_DISTANCE);
        Vector3f toNear = new Vector3f(forwardVector);
        toNear.multiplyScalar(MasterRenderer.NEAR_PLANE);
        Vector3f centerNear = toNear.copyOf();
        centerNear.add(cam.getPosition());
        Vector3f centerFar = toFar.copyOf();
        centerFar.add(cam.getPosition());
        
        Vector4f[] points = calculateFrustumVertices(rotation, forwardVector, centerNear,
                centerFar);
 
        boolean first = true;
        for (Vector4f point : points) {
            if (first) {
                minX = point.getX();
                maxX = point.getX();
                minY = point.getY();
                maxY = point.getY();
                minZ = point.getZ();
                maxZ = point.getZ();
                first = false;
                continue;
            }
            if (point.getX() > maxX) {
                maxX = point.getX();
            } else if (point.getX() < minX) {
                minX = point.getX();
            }
            if (point.getY() > maxY) {
                maxY = point.getY();
            } else if (point.getY() < minY) {
                minY = point.getY();
            }
            if (point.getZ() > maxZ) {
                maxZ = point.getZ();
            } else if (point.getZ() < minZ) {
                minZ = point.getZ();
            }
        }
        maxZ += OFFSET;
 
    }
 
    /**
     * Calculates the center of the "view cuboid" in light space first, and then
     * converts this to world space using the inverse light's view matrix.
     * 
     * @return The center of the "view cuboid" in world space.
     */
    protected Vector3f getCenter() {
        float x = (minX + maxX) / 2f;
        float y = (minY + maxY) / 2f;
        float z = (minZ + maxZ) / 2f;
        Vector4f cen = new Vector4f(x, y, z, 1);
        Matrix4f invertedLight = lightViewMatrix.getInverse();
        return new Vector3f(Matrix4f.transform(invertedLight, cen));
    }
 
    /**
     * @return The width of the "view cuboid" (orthographic projection area).
     */
    protected float getWidth() {
        return maxX - minX;
    }
 
    /**
     * @return The height of the "view cuboid" (orthographic projection area).
     */
    protected float getHeight() {
        return maxY - minY;
    }
 
    /**
     * @return The length of the "view cuboid" (orthographic projection area).
     */
    protected float getLength() {
        return maxZ - minZ;
    }
 
    /**
     * Calculates the position of the vertex at each corner of the view frustum
     * in light space (8 vertices in total, so this returns 8 positions).
     * 
     * @param rotation
     *            - camera's rotation.
     * @param forwardVector
     *            - the direction that the camera is aiming, and thus the
     *            direction of the frustum.
     * @param centerNear
     *            - the center point of the frustum's near plane.
     * @param centerFar
     *            - the center point of the frustum's (possibly adjusted) far
     *            plane.
     * @return The positions of the vertices of the frustum in light space.
     */
    private Vector4f[] calculateFrustumVertices(Matrix4f rotation, Vector3f forwardVector,
            Vector3f centerNear, Vector3f centerFar) {
        Vector3f upVector = new Vector3f(Matrix4f.transform(rotation, UP));
        Vector3f rightVector = forwardVector.copyOf();//Vector3f.cross(forwardVector, upVector, null);
        rightVector.multiply(upVector);
        Vector3f downVector = new Vector3f(-upVector.getX(), -upVector.getY(), -upVector.getZ());
        Vector3f leftVector = new Vector3f(-rightVector.getX(), -rightVector.getY(), -rightVector.getZ());
        Vector3f farTop = centerFar.copyOf();
        farTop.add(new Vector3f(upVector.getX() * farHeight,
                upVector.getY() * farHeight, upVector.getZ() * farHeight));
        Vector3f farBottom = centerFar.copyOf();
        farBottom.add(new Vector3f(downVector.getX() * farHeight,
                downVector.getY() * farHeight, downVector.getZ() * farHeight));
        Vector3f nearTop = centerNear.copyOf();
        nearTop.add(new Vector3f(upVector.getX() * nearHeight,
                upVector.getY() * nearHeight, upVector.getZ() * nearHeight));
        Vector3f nearBottom = centerNear.copyOf();
        nearBottom.add(new Vector3f(downVector.getX() * nearHeight,
                downVector.getY() * nearHeight, downVector.getZ() * nearHeight));
        Vector4f[] points = new Vector4f[8];
        points[0] = calculateLightSpaceFrustumCorner(farTop, rightVector, farWidth);
        points[1] = calculateLightSpaceFrustumCorner(farTop, leftVector, farWidth);
        points[2] = calculateLightSpaceFrustumCorner(farBottom, rightVector, farWidth);
        points[3] = calculateLightSpaceFrustumCorner(farBottom, leftVector, farWidth);
        points[4] = calculateLightSpaceFrustumCorner(nearTop, rightVector, nearWidth);
        points[5] = calculateLightSpaceFrustumCorner(nearTop, leftVector, nearWidth);
        points[6] = calculateLightSpaceFrustumCorner(nearBottom, rightVector, nearWidth);
        points[7] = calculateLightSpaceFrustumCorner(nearBottom, leftVector, nearWidth);
        return points;
    }
 
    /**
     * Calculates one of the corner vertices of the view frustum in world space
     * and converts it to light space.
     * 
     * @param startPoint
     *            - the starting center point on the view frustum.
     * @param direction
     *            - the direction of the corner from the start point.
     * @param width
     *            - the distance of the corner from the start point.
     * @return - The relevant corner vertex of the view frustum in light space.
     */
    private Vector4f calculateLightSpaceFrustumCorner(Vector3f startPoint, Vector3f direction,
            float width) {
    	startPoint.add(new Vector3f(direction.getX() * width, direction.getY() * width, direction.getZ() * width));
        Vector4f point4f = new Vector4f(startPoint.getX(), startPoint.getY(), startPoint.getZ(), 1f);
        return Matrix4f.transform(lightViewMatrix, point4f);
    }
 
    /**
     * @return The rotation of the camera represented as a matrix.
     */
    private Matrix4f calculateCameraRotationMatrix() {
        Matrix4f rotation = Matrix4f.getIdentity();
        rotation.multiply(Transform.yRotation(-cam.getTransform().getRotation().getY()));
        rotation.multiply(Transform.xRotation(-cam.getTransform().getRotation().getX()));
        return rotation;
    }
 
    /**
     * Calculates the width and height of the near and far planes of the
     * camera's view frustum. However, this doesn't have to use the "actual" far
     * plane of the view frustum. It can use a shortened view frustum if desired
     * by bringing the far-plane closer, which would increase shadow resolution
     * but means that distant objects wouldn't cast shadows.
     */
    private void calculateWidthsAndHeights() {
        farWidth = (float) (SHADOW_DISTANCE * Math.tan(Math.toRadians(MasterRenderer.FOV)));
        nearWidth = (float) (MasterRenderer.NEAR_PLANE
                * Math.tan(Math.toRadians(MasterRenderer.FOV)));
        farHeight = farWidth / getAspectRatio();
        nearHeight = nearWidth / getAspectRatio();
    }
 
    /**
     * @return The aspect ratio of the display (width:height ratio).
     */
    private float getAspectRatio() {
        return (float) GLFWWindow.getWidth() / (float) GLFWWindow.getHeight();
    }
 
}