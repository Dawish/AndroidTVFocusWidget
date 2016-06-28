
package org.bangbang.song.focuslayer.animator;

import android.animation.Animator;

/**
 * 
 * <p>
 * to incorporate with {@link Animator}, add {@link #setWidth(float)} and
 * {@link #setHeight(float)} method.
 * <p>
 * @author bysong
 *
 */
public interface IAnimatableView {

    public abstract void setWidth(float w);

    public abstract void setHeight(float h);

}
