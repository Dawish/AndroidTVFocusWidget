
package custom.droid.layout;

import java.util.ArrayList;

import android.graphics.Matrix;
import android.view.View;

public final class LayoutUtils {

    /**
     * Given a coordinate relative to the descendant, find the coordinate in a
     * parent view's coordinates.
     * 
     * @param descendant The descendant to which the passed coordinate is
     *            relative.
     * @param root The root view to make the coordinates relative to.
     * @param coord The coordinate that we want mapped.
     * @param includeRootScroll Whether or not to account for the scroll of the
     *            descendant: sometimes this is relevant as in a child's
     *            coordinates within the descendant.
     * @return The factor by which this descendant is scaled relative to this
     *         DragLayer. Caution this scale factor is assumed to be equal in X
     *         and Y, and so if at any point this assumption fails, we will need
     *         to return a pair of scale factors.
     */
    public static float getDescendantCoordRelativeToParent(View descendant, View root, int[] coord,
            boolean includeRootScroll) {
        ArrayList<View> ancestorChain = new ArrayList<View>();

        float[] pt = {
                coord[0], coord[1]
        };

        View v = descendant;
        while (v != root && v != null) {
            ancestorChain.add(v);
            v = (View) v.getParent();
        }
        ancestorChain.add(root);

        float scale = 1.0f;
        int count = ancestorChain.size();
        for (int i = 0; i < count; i++) {
            View v0 = ancestorChain.get(i);
            // For TextViews, scroll has a meaning which relates to the text
            // position
            // which is very strange... ignore the scroll.
            if (v0 != descendant || includeRootScroll) {
                pt[0] -= v0.getScrollX();
                pt[1] -= v0.getScrollY();
            }

            v0.getMatrix().mapPoints(pt);
            pt[0] += v0.getLeft();
            pt[1] += v0.getTop();
            scale *= v0.getScaleX();
        }

        coord[0] = (int) Math.round(pt[0]);
        coord[1] = (int) Math.round(pt[1]);
        return scale;
    }

    /**
     * Inverse of {@link #getDescendantCoordRelativeToSelf(View, int[])}.
     */
    public static float mapCoordInSelfToDescendent(View descendant, View root, int[] coord) {
        ArrayList<View> ancestorChain = new ArrayList<View>();

        float[] pt = {
                coord[0], coord[1]
        };

        View v = descendant;
        while (v != root) {
            ancestorChain.add(v);
            v = (View) v.getParent();
        }
        ancestorChain.add(root);

        float scale = 1.0f;
        Matrix inverse = new Matrix();
        int count = ancestorChain.size();
        for (int i = count - 1; i >= 0; i--) {
            View ancestor = ancestorChain.get(i);
            View next = i > 0 ? ancestorChain.get(i - 1) : null;

            pt[0] += ancestor.getScrollX();
            pt[1] += ancestor.getScrollY();

            if (next != null) {
                pt[0] -= next.getLeft();
                pt[1] -= next.getTop();
                next.getMatrix().invert(inverse);
                inverse.mapPoints(pt);
                scale *= next.getScaleX();
            }
        }

        coord[0] = (int) Math.round(pt[0]);
        coord[1] = (int) Math.round(pt[1]);
        return scale;
    }
}
