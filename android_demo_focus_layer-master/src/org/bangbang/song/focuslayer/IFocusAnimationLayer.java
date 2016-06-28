package org.bangbang.song.focuslayer;

import android.view.View;
/**
 * 
 * <pre>
    +--------------focus layer (top layer)--------------------+
    |                                                         |
  +-+-----------subject layer (bottom layer)----------------+ |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | |                                                       | |
  | +-------------------------------------------------------+-+
  |                                                         |
  +---------------------------------------------------------+
  </pre>
*/
public interface IFocusAnimationLayer extends View.OnFocusChangeListener{
    // yes, we do not need this, this is implicitly in first {@link onFocusChange} callback.
    // we know the beginning, but can not guess the ending.
//    public void onFocusSessionBegin(View lastFocus);
    public void onFocusSessionEnd(View lastFocus);
}
