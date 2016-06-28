package org.bangbang.support.v4.widget;

import org.bangbang.android.support.v4.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.ListAdapter;

/**
 * copied from 
 * https://raw.github.com/android/platform_frameworks_base/donut-release/core/java/android/widget/GridView.java
 * <p>
 * impl note:
 * top ==> left
 * bottom ==> right
 * height ==> width
 * vertical ==> horizontal
 * 
 * bysong@tudou.com
 */
/**
 * A view that shows items in two-dimensional scrolling grid. The items in the
 * grid come from the {@link ListAdapter} associated with this view.
 */
public class HGridView extends HAbsListView {
	private static final String TAG = HGridView.class.getSimpleName();
	private static final boolean DEBUG = true;
	
    public static final int NO_STRETCH = 0;
    public static final int STRETCH_SPACING = 1;
    public static final int STRETCH_COLUMN_WIDTH = 2;
    public static final int STRETCH_SPACING_UNIFORM = 3;
    
    public static final int AUTO_FIT = -1;

//    private int mNumColumns = AUTO_FIT;
    private int mNumRows = AUTO_FIT;
    private int mRequestedNumRows;
    private int mHorizontalSpacing = 0;
    private int mVerticalSpacing = 0;
    private int mRequestedHorizontalSpacing;
    private int mRequestedVerticalSpacing = 0;
    private int mStretchMode = STRETCH_COLUMN_WIDTH;
//    private int mColumnWidth;
    private int mRowHeight;
    private int mRequestedRowHeight;

    private View mReferenceView = null;
    private View mReferenceViewInSelectedRow = null;

    private int mGravity = Gravity.LEFT;

    private final Rect mTempRect = new Rect();

    public HGridView(Context context) {
        super(context);
    }

    public HGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0/*com.android.internal.R.attr.gridViewStyle*/);
    }
    
    public int getNumberRows() {
    	return mNumRows;
    }

    public HGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.HGridView, defStyle, 0);

        int hSpacing = a.getDimensionPixelOffset(
                R.styleable.HGridView_horizontalSpacing, 0);
        setHorizontalSpacing(hSpacing);

        int vSpacing = a.getDimensionPixelOffset(
                R.styleable.HGridView_verticalSpacing, 0);
        setVerticalSpacing(vSpacing);

        int index = a.getInt(R.styleable.HGridView_stretchMode, STRETCH_COLUMN_WIDTH);
        if (index >= 0) {
            setStretchMode(index);
        }

        int rowHeight = a.getDimensionPixelOffset(R.styleable.HGridView_rowHeight, -1);
        if (rowHeight > 0) {
            setRowHeight(rowHeight);
        }

        int numRows = a.getInt(R.styleable.HGridView_numRows, 1);
        setNumRows(numRows);

        index = a.getInt(R.styleable.HGridView_gravity, -1);
        if (index >= 0) {
            setGravity(index);
        }
        
        a.recycle();
        
        setNumRows(2);
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Sets the data behind this GridView.
     *
     * @param adapter the adapter providing the grid's data
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        if (null != mAdapter) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        resetList();
        mRecycler.clear();        
        mAdapter = adapter;

        mOldSelectedPosition = INVALID_POSITION;
        mOldSelectedRowId = INVALID_ROW_ID;
        
        if (mAdapter != null) {
            mOldItemCount = mItemCount;
            mItemCount = mAdapter.getCount();
            mDataChanged = true;
            checkFocus();

            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);

            mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());

            int position;
            if (mStackFromBottom) {
                position = lookForSelectablePosition(mItemCount - 1, false);
            } else {
                position = lookForSelectablePosition(0, true);
            }
            setSelectedPositionInt(position);
            setNextSelectedPositionInt(position);
            checkSelectionChanged();
        } else {
            checkFocus();            
            // Nothing selected
            checkSelectionChanged();
        }

        requestLayout();
    }

    @Override
    int lookForSelectablePosition(int position, boolean lookDown) {
        final ListAdapter adapter = mAdapter;
        if (adapter == null || isInTouchMode()) {
            return INVALID_POSITION;
        }

        if (position < 0 || position >= mItemCount) {
            return INVALID_POSITION;
        }
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void fillGap(boolean down) {
        final int numRows = mNumRows;
        final int horizontalSpacing = mRequestedHorizontalSpacing;

        final int count = getChildCount();

        if (down) {
            final int startOffset = count > 0 ?
                    getChildAt(count - 1).getRight() + horizontalSpacing : getListPaddingLeft();
            int position = mFirstPosition + count;
            if (mStackFromBottom) {
                position += numRows - 1;
            }
            fillRight(position, startOffset);
            correctTooHigh(numRows, horizontalSpacing, getChildCount());
        } else {
            final int startOffset = count > 0 ?
                    getChildAt(0).getLeft() - horizontalSpacing : getWidth() - getListPaddingRight();
            int position = mFirstPosition;
            if (!mStackFromBottom) {
                position -= numRows;
            } else {
                position--;
            }
            fillLeft(position, startOffset);
            correctTooLow(numRows, horizontalSpacing, getChildCount());
        }
    }

    /**
     * Fills the list from pos down to the end of the list view.
     *
     * @param pos The first position to put in the list
     *
     * @param nextLeft The location where the top of the item associated with pos
     *        should be drawn
     *
     * @return The view that is currently selected, if it happens to be in the
     *         range that we draw.
     */
    private View fillRight(int pos, int nextLeft) {
        View selectedView = null;

//        final int end = (mBottom - mTop) - mListPadding.bottom;
        final int end = (getRight() - getLeft()) - mListPadding.right;

        while (nextLeft < end && pos < mItemCount) {
            View temp = makeColumn(pos, nextLeft, true);
            if (temp != null) {
                selectedView = temp;
            }

            nextLeft = mReferenceView.getRight() + mHorizontalSpacing;

            pos += mNumRows;
        }

        return selectedView;
    }

    private View makeColumn(int startPos, int x, boolean flow) {
        final int rowHeight = mRowHeight;//mColumnWidth;
        final int verticalSpacing = mVerticalSpacing;//mHorizontalSpacing;

        int last;
        int nextTop = mListPadding.top + ((mStretchMode == STRETCH_SPACING_UNIFORM) ? verticalSpacing : 0);

        if (!mStackFromBottom) {
            last = Math.min(startPos + mNumRows, mItemCount);
        } else {
            last = startPos + 1;
            startPos = Math.max(0, startPos - mNumRows + 1);

            if (last - startPos < mNumRows) {
                nextTop += (mNumRows - (last - startPos)) * (rowHeight + verticalSpacing);
            }
        }

        View selectedView = null;

        final boolean hasFocus = shouldShowSelector();
        final boolean inClick = touchModeDrawsInPressedState();
        final int selectedPosition = mSelectedPosition;

        mReferenceView = null;

        for (int pos = startPos; pos < last; pos++) {
            // is this the selected item?
            boolean selected = pos == selectedPosition;
            // does the list view have focus or contain focus

            final int where = flow ? -1 : pos - startPos;
            final View child = makeAndAddView(pos, x, flow, nextTop, selected, where);
            mReferenceView = child;

            nextTop += rowHeight;
            if (pos < last - 1) {
                nextTop += verticalSpacing;
            }

            if (selected && (hasFocus || inClick)) {
                selectedView = child;
            }
        }

        if (selectedView != null) {
            mReferenceViewInSelectedRow = mReferenceView;
        }

        return selectedView;
    }

    /**
     * Fills the list from pos up to the top of the list view.
     *
     * @param pos The first position to put in the list
     *
     * @param nextRight The location where the bottom of the item associated
     *        with pos should be drawn
     *
     * @return The view that is currently selected
     */
    private View fillLeft(int pos, int nextRight) {
        View selectedView = null;

        final int end = mListPadding.left;

        while (nextRight > end && pos >= 0) {

            View temp = makeColumn(pos, nextRight, false);
            if (temp != null) {
                selectedView = temp;
            }

            nextRight = mReferenceView.getLeft() - mRequestedVerticalSpacing;

            mFirstPosition = pos;

            pos -= mNumRows;
        }

        if (mStackFromBottom) {
            mFirstPosition = Math.max(0, pos + 1);
        }

        return selectedView;
    }

    /**
     * Fills the list from top to bottom, starting with mFirstPosition
     *
     * @param nextLeft The location where the top of the first item should be
     *        drawn
     *
     * @return The view that is currently selected
     */
    private View fillFromLeft(int nextLeft) {
        mFirstPosition = Math.min(mFirstPosition, mSelectedPosition);
        mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
        if (mFirstPosition < 0) {
            mFirstPosition = 0;
        }
        mFirstPosition -= mFirstPosition % mNumRows;
        return fillRight(mFirstPosition, nextLeft);
    }

    private View fillFromBottom(int lastPosition, int nextBottom) {
        lastPosition = Math.max(lastPosition, mSelectedPosition);
        lastPosition = Math.min(lastPosition, mItemCount - 1);

        final int invertedPosition = mItemCount - 1 - lastPosition;
        lastPosition = mItemCount - 1 - (invertedPosition - (invertedPosition % mNumRows));

        return fillLeft(lastPosition, nextBottom);
    }

    private View fillSelection(int childrenTop, int childrenBottom) {
        final int selectedPosition = reconcileSelectedPosition();
        final int numRows = mNumRows;
        final int verticalSpacing = mRequestedVerticalSpacing;

        int rowStart;
        int rowEnd = -1;

        if (!mStackFromBottom) {
            rowStart = selectedPosition - (selectedPosition % numRows);
        } else {
            final int invertedSelection = mItemCount - 1 - selectedPosition;

            rowEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numRows));
            rowStart = Math.max(0, rowEnd - numRows + 1);
        }

        final int fadingEdgeLength = getVerticalFadingEdgeLength();
        final int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);

        final View sel = makeColumn(mStackFromBottom ? rowEnd : rowStart, topSelectionPixel, true);
        mFirstPosition = rowStart;

        final View referenceView = mReferenceView;

        if (!mStackFromBottom) {
            fillLeft(rowStart + numRows, referenceView.getBottom() + verticalSpacing);
            pinToBottom(childrenBottom);
            fillLeft(rowStart - numRows, referenceView.getTop() - verticalSpacing);
            adjustViewsUpOrDown();
        } else {
            final int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom,
                    fadingEdgeLength, numRows, rowStart);
            final int offset = bottomSelectionPixel - referenceView.getBottom();
//            offsetChildrenTopAndBottom(offset);
            ViewCompat.offsetChildrenTopAndBottom(this, offset);
            fillLeft(rowStart - 1, referenceView.getTop() - verticalSpacing);
            pinToTop(childrenTop);
            fillLeft(rowEnd + numRows, referenceView.getBottom() + verticalSpacing);
            adjustViewsUpOrDown();
        }

        return sel;
    }

    private void pinToTop(int childrenTop) {
        if (mFirstPosition == 0) {
            final int top = getChildAt(0).getTop();
            final int offset = childrenTop - top;
            if (offset < 0) {
//                offsetChildrenTopAndBottom(offset);
                ViewCompat.offsetChildrenTopAndBottom(this, offset);
            }
        }
    }

    private void pinToBottom(int childrenBottom) {
        final int count = getChildCount();
        if (mFirstPosition + count == mItemCount) {
            final int bottom = getChildAt(count - 1).getBottom();
            final int offset = childrenBottom - bottom;
            if (offset > 0) {
                //offsetChildrenTopAndBottom(offset);
                ViewCompat.offsetChildrenTopAndBottom(this, offset);
            }
        }
    }    

    @Override
    int findMotionRow(int x) {
    	if (true) {
    		int row = findMotionRowImpl(x);
    		if (DEBUG) {
    			Log.d(TAG, "findMotionRow. x: " + x + " row: " + row);
    		}
    		return row;
    	}
    	
        final int childCount = getChildCount();
        if (childCount > 0) {

            final int numRows = mNumRows;
            if (!mStackFromBottom) {
                for (int i = 0; i < childCount; i += numRows) {
                    if (x <= getChildAt(i).getRight()) {
                        return mFirstPosition + i;
                    }
                }
            } else {
                for (int i = childCount - 1; i >= 0; i -= numRows) {
                    if (x >= getChildAt(i).getLeft()) {
                        return mFirstPosition + i;
                    }
                }
            }

            return mFirstPosition + childCount - 1;
        }
        return INVALID_POSITION;
    }
    
    int findMotionRowImpl(int x) {
        final int childCount = getChildCount();
        if (childCount > 0) {

            final int numRows = mNumRows;
            if (!mStackFromBottom) {
                for (int i = 0; i < childCount; i += numRows) {
                    if (x <= getChildAt(i).getRight()) {
                        return mFirstPosition + i;
                    }
                }
            } else {
                for (int i = childCount - 1; i >= 0; i -= numRows) {
                    if (x >= getChildAt(i).getLeft()) {
                        return mFirstPosition + i;
                    }
                }
            }

            return mFirstPosition + childCount - 1;
        }
        return INVALID_POSITION;
    }

    /**
     * Layout during a scroll that results from tracking motion events. Places
     * the mMotionPosition view at the offset specified by mMotionViewTop, and
     * then build surrounding views from there.
     *
     * @param position the position at which to start filling
     * @param left the top of the view at that position
     * @return The selected view, or null if the selected view is outside the
     *         visible area.
     */
    private View fillSpecific(int position, int left) {
        final int numRows = mNumRows;

        int motionColumnStart;
        int motionColumnEnd = -1;

        if (!mStackFromBottom) {
            motionColumnStart = position - (position % numRows);
        } else {
            final int invertedSelection = mItemCount - 1 - position;

            motionColumnEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numRows));
            motionColumnStart = Math.max(0, motionColumnEnd - numRows + 1);
        }

        final View temp = makeColumn(mStackFromBottom ? motionColumnEnd : motionColumnStart, left, true);

        // Possibly changed again in fillUp if we add rows above this one.
        mFirstPosition = motionColumnStart;

        final View referenceView = mReferenceView;
        final int verticalSpacing = mRequestedVerticalSpacing;
        final int horizonalSpacing = mHorizontalSpacing;

        View above;
        View below;

        if (!mStackFromBottom) {
            above = fillLeft(motionColumnStart - numRows, referenceView.getLeft() - horizonalSpacing);
            adjustViewsUpOrDown();
            below = fillRight(motionColumnStart + numRows, referenceView.getRight() + horizonalSpacing);
            // Check if we have dragged the bottom of the grid too high
            final int childCount = getChildCount();
            if (childCount > 0) {
                correctTooHigh(numRows, verticalSpacing, childCount);
            }
        } else {
            below = fillLeft(motionColumnEnd + numRows, referenceView.getBottom() + verticalSpacing);
            adjustViewsUpOrDown();
            above = fillLeft(motionColumnStart - 1, referenceView.getTop() - verticalSpacing);
            // Check if we have dragged the bottom of the grid too high
            final int childCount = getChildCount();
            if (childCount > 0) {
                correctTooLow(numRows, verticalSpacing, childCount);
            }
        }

        if (temp != null) {
            return temp;
        } else if (above != null) {
            return above;
        } else {
            return below;
        }
    }

    private void correctTooHigh(int numRows, int horizontalSpacing, int childCount) {
        // First see if the last item is visible
        final int lastPosition = mFirstPosition + childCount - 1;
        if (lastPosition == mItemCount - 1 && childCount > 0) {
            // Get the last child ...
            final View lastChild = getChildAt(childCount - 1);

            // ... and its bottom edge
            final int lastRight = lastChild.getRight();
            // This is bottom of our drawable area
//            final int end = (mBottom - mTop) - mListPadding.bottom;
            final int end = (getRight() - getLeft()) - mListPadding.right;

            // This is how far the bottom edge of the last view is from the bottom of the
            // drawable area
            int rightOffset = end - lastRight;        

            final View firstChild = getChildAt(0);
            final int firstLeft = firstChild.getLeft();

            // Make sure we are 1) Too high, and 2) Either there are more rows above the
            // first row or the first row is scrolled off the top of the drawable area
            if (rightOffset > 0 && (mFirstPosition > 0 || firstLeft < mListPadding.top))  {
                if (mFirstPosition == 0) {
                    // Don't pull the top too far down
                    rightOffset = Math.min(rightOffset, mListPadding.left - firstLeft);
                }
                
                // Move everything down
//                offsetChildrenTopAndBottom(bottomOffset);
//                ViewCompat.offsetChildrenTopAndBottom(this, rightOffset);
                ViewCompat.offsetChildrenLeftAndRight(this, rightOffset);
                if (mFirstPosition > 0) {
                    // Fill the gap that was opened above mFirstPosition with more rows, if
                    // possible
                    fillLeft(mFirstPosition - (mStackFromBottom ? 1 : numRows),
                            firstChild.getLeft() - horizontalSpacing);            // Close up the remaining gap
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    private void correctTooLow(int numRows, int horizontalSpacing, int childCount) {
        if (mFirstPosition == 0 && childCount > 0) {
            // Get the first child ...
            final View firstChild = getChildAt(0);

            // ... and its top edge
            final int firstLeft = firstChild.getLeft();

            // This is top of our drawable area
            final int start = mListPadding.left;

            // This is bottom of our drawable area
//            final int end = (mBottom - mTop) - mListPadding.bottom;
            final int end = (getRight() - getLeft()) - mListPadding.right;

            // This is how far the top edge of the first view is from the top of the
            // drawable area
            int topOffset = firstLeft - start;
            final View lastChild = getChildAt(childCount - 1);
            final int lastRight = lastChild.getRight();
            final int lastPosition = mFirstPosition + childCount - 1;

            // Make sure we are 1) Too low, and 2) Either there are more rows below the
            // last row or the last row is scrolled off the bottom of the drawable area
            if (topOffset > 0 && (lastPosition < mItemCount - 1 || lastRight > end))  {
                if (lastPosition == mItemCount - 1 ) {
                    // Don't pull the bottom too far up
                    topOffset = Math.min(topOffset, lastRight - end);
                }
                
                // Move everything up
//                offsetChildrenTopAndBottom(-topOffset);
//                ViewCompat.offsetChildrenTopAndBottom(this, -topOffset);
                ViewCompat.offsetChildrenLeftAndRight(this, -topOffset);
                if (lastPosition < mItemCount - 1) {
                    // Fill the gap that was opened below the last position with more rows, if
                    // possible
                    fillRight(lastPosition + (!mStackFromBottom ? 1 : numRows),
                            lastChild.getRight() + horizontalSpacing);
                    // Close up the remaining gap
                    adjustViewsUpOrDown();
                }
            }
        }
    }

    /**
     * Fills the grid based on positioning the new selection at a specific
     * location. The selection may be moved so that it does not intersect the
     * faded edges. The grid is then filled upwards and downwards from there.
     *
     * @param selectedTop Where the selected item should be
     * @param childrenTop Where to start drawing children
     * @param childrenBottom Last pixel where children can be drawn
     * @return The view that currently has selection
     */
    private View fillFromSelection(int selectedTop, int childrenTop, int childrenBottom) {
        final int fadingEdgeLength = getVerticalFadingEdgeLength();
        final int selectedPosition = mSelectedPosition;
        final int numRows = mNumRows;
        final int verticalSpacing = mRequestedVerticalSpacing;

        int rowStart;
        int rowEnd = -1;

        if (!mStackFromBottom) {
            rowStart = selectedPosition - (selectedPosition % numRows);
        } else {
            int invertedSelection = mItemCount - 1 - selectedPosition;

            rowEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numRows));
            rowStart = Math.max(0, rowEnd - numRows + 1);
        }

        View sel;
        View referenceView;

        int topSelectionPixel = getTopSelectionPixel(childrenTop, fadingEdgeLength, rowStart);
        int bottomSelectionPixel = getBottomSelectionPixel(childrenBottom, fadingEdgeLength,
                numRows, rowStart);

        sel = makeColumn(mStackFromBottom ? rowEnd : rowStart, selectedTop, true);
        // Possibly changed again in fillUp if we add rows above this one.
        mFirstPosition = rowStart;

        referenceView = mReferenceView;
        adjustForLeftFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        adjustForRightFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);

        if (!mStackFromBottom) {
            fillLeft(rowStart - numRows, referenceView.getTop() - verticalSpacing);
            adjustViewsUpOrDown();
            fillLeft(rowStart + numRows, referenceView.getBottom() + verticalSpacing);
        } else {
            fillLeft(rowEnd + numRows, referenceView.getBottom() + verticalSpacing);
            adjustViewsUpOrDown();
            fillLeft(rowStart - 1, referenceView.getTop() - verticalSpacing);
        }


        return sel;
    }

    /**
     * Calculate the bottom-most pixel we can draw the selection into
     *
     * @param childrenBottom Bottom pixel were children can be drawn
     * @param fadingEdgeLength Length of the fading edge in pixels, if present
     * @param numColumns Number of columns in the grid
     * @param rowStart The start of the row that will contain the selection
     * @return The bottom-most pixel we can draw the selection into
     */
    private int getBottomSelectionPixel(int childrenBottom, int fadingEdgeLength,
            int numColumns, int rowStart) {
        // Last pixel we can draw the selection into
        int bottomSelectionPixel = childrenBottom;
        if (rowStart + numColumns - 1 < mItemCount - 1) {
            bottomSelectionPixel -= fadingEdgeLength;
        }
        return bottomSelectionPixel;
    }

    /**
     * Calculate the top-most pixel we can draw the selection into
     *
     * @param childrenTop Top pixel were children can be drawn
     * @param fadingEdgeLength Length of the fading edge in pixels, if present
     * @param rowStart The start of the row that will contain the selection
     * @return The top-most pixel we can draw the selection into
     */
    private int getTopSelectionPixel(int childrenTop, int fadingEdgeLength, int rowStart) {
        // first pixel we can draw the selection into
        int topSelectionPixel = childrenTop;
        if (rowStart > 0) {
            topSelectionPixel += fadingEdgeLength;
        }
        return topSelectionPixel;
    }

    /**
     * Move all views upwards so the selected row does not interesect the bottom
     * fading edge (if necessary).
     *
     * @param childInSelectedRow A child in the row that contains the selection
     * @param leftSelectionPixel The topmost pixel we can draw the selection into
     * @param rightSelectionPixel The bottommost pixel we can draw the
     *        selection into
     */
    private void adjustForRightFadingEdge(View childInSelectedRow,
            int leftSelectionPixel, int rightSelectionPixel) {
        // Some of the newly selected item extends below the bottom of the
        // list
        if (childInSelectedRow.getRight() > rightSelectionPixel) {

            // Find space available above the selection into which we can
            // scroll upwards
            int spaceLeft = childInSelectedRow.getLeft() - leftSelectionPixel;

            // Find space required to bring the bottom of the selected item
            // fully into view
            int spaceRight = childInSelectedRow.getRight() - rightSelectionPixel;
            int offset = Math.min(spaceLeft, spaceRight);

            // Now offset the selected item to get it into view
//            offsetChildrenTopAndBottom(-offset);
//            ViewCompat.offsetChildrenTopAndBottom(this, -offset);
            ViewCompat.offsetChildrenLeftAndRight(this, -offset);
        }
    }

    /**
     * Move all views upwards so the selected row does not interesect the top
     * fading edge (if necessary).
     *
     * @param childInSelectedRow A child in the row that contains the selection
     * @param leftSelectionPixel The topmost pixel we can draw the selection into
     * @param rightSelectionPixel The bottommost pixel we can draw the
     *        selection into
     */
    private void adjustForLeftFadingEdge(View childInSelectedRow,
            int leftSelectionPixel, int rightSelectionPixel) {
        // Some of the newly selected item extends above the top of the list
        if (childInSelectedRow.getLeft() < leftSelectionPixel) {
            // Find space required to bring the top of the selected item
            // fully into view
            int spaceLeft = leftSelectionPixel - childInSelectedRow.getLeft();

            // Find space available below the selection into which we can
            // scroll downwards
            int spaceRight = rightSelectionPixel - childInSelectedRow.getRight();
            int offset = Math.min(spaceLeft, spaceRight);

            // Now offset the selected item to get it into view
//            offsetChildrenTopAndBottom(offset);
//            ViewCompat.offsetChildrenTopAndBottom(this, offset);
            ViewCompat.offsetChildrenLeftAndRight(this, offset);
        }
    }

    /**
     * Fills the grid based on positioning the new selection relative to the old
     * selection. The new selection will be placed at, above, or below the
     * location of the new selection depending on how the selection is moving.
     * The selection will then be pinned to the visible part of the screen,
     * excluding the edges that are faded. The grid is then filled upwards and
     * downwards from there.
     *
     * @param delta Which way we are moving
     * @param childrenLeft Where to start drawing children
     * @param childrenRight Last pixel where children can be drawn
     * @return The view that currently has selection
     */
    private View moveSelection(int delta, int childrenLeft, int childrenRight) {
        final int fadingEdgeLength = getVerticalFadingEdgeLength();
        final int selectedPosition = mSelectedPosition;
        final int numRows = mNumRows;
        final int verticalSpacing = mRequestedVerticalSpacing;
        final int horizontalSpacing = mHorizontalSpacing;

        int oldColumnStart;
        int columnStart;
        int columnEnd = -1;

        if (!mStackFromBottom) {
            oldColumnStart = (selectedPosition - delta) - ((selectedPosition - delta) % numRows);

            columnStart = selectedPosition - (selectedPosition % numRows);
        } else {
            int invertedSelection = mItemCount - 1 - selectedPosition;

            columnEnd = mItemCount - 1 - (invertedSelection - (invertedSelection % numRows));
            columnStart = Math.max(0, columnEnd - numRows + 1);

            invertedSelection = mItemCount - 1 - (selectedPosition - delta);
            oldColumnStart = mItemCount - 1 - (invertedSelection - (invertedSelection % numRows));
            oldColumnStart = Math.max(0, oldColumnStart - numRows + 1);
        }

        final int rowDelta = columnStart - oldColumnStart;

        final int topSelectionPixel = getTopSelectionPixel(childrenLeft, fadingEdgeLength, columnStart);
        final int bottomSelectionPixel = getBottomSelectionPixel(childrenRight, fadingEdgeLength,
                numRows, columnStart);

        // Possibly changed again in fillUp if we add rows above this one.
        mFirstPosition = columnStart;

        View sel;
        View referenceView;

        if (rowDelta > 0) {
            /*
             * Case 1: Scrolling to right.
             */

            final int oldRight = mReferenceViewInSelectedRow == null ? 0 :
                    mReferenceViewInSelectedRow.getRight();

            sel = makeColumn(mStackFromBottom ? columnEnd : columnStart, oldRight + horizontalSpacing, true);
            referenceView = mReferenceView;

            adjustForRightFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        } else if (rowDelta < 0) {
            /*
             * Case 2: Scrolling to left.
             */
            final int oldLeft = mReferenceViewInSelectedRow == null ?
                    0 : mReferenceViewInSelectedRow .getLeft();

            sel = makeColumn(mStackFromBottom ? columnEnd : columnStart, oldLeft - horizontalSpacing, false);
            referenceView = mReferenceView;

            adjustForLeftFadingEdge(referenceView, topSelectionPixel, bottomSelectionPixel);
        } else {
            /*
             * Keep selection where it was
             */
            final int oldLeft = mReferenceViewInSelectedRow == null ?
                    0 : mReferenceViewInSelectedRow .getLeft();

            sel = makeColumn(mStackFromBottom ? columnEnd : columnStart, oldLeft, true);
            referenceView = mReferenceView;
        }

        if (!mStackFromBottom) {
            fillLeft(columnStart - numRows, referenceView.getLeft() - horizontalSpacing);
            adjustViewsUpOrDown();
            fillRight(columnStart + numRows, referenceView.getRight() + horizontalSpacing);
        } else {
            fillLeft(columnEnd + numRows, referenceView.getRight() + horizontalSpacing);
            adjustViewsUpOrDown();
            fillRight(columnStart - 1, referenceView.getLeft() - horizontalSpacing);
        }

        return sel;
    }

    private void determineRows(int availableSpace) {
        final int requestedHorizontalSpacing = mRequestedHorizontalSpacing;
        final int requestedVerticalSpacing = mRequestedVerticalSpacing;
        final int stretchMode = mStretchMode;
        final int requestedRowHeight = mRequestedRowHeight;
        
        if (mRequestedNumRows == AUTO_FIT) {
            if (requestedRowHeight > 0) {
                // Client told us to pick the number of columns
                mNumRows = (availableSpace + requestedRowHeight) / 
                		(requestedRowHeight + requestedVerticalSpacing);
            } else {
                // Just make up a number if we don't have enough info
                mNumRows = 2;
            }
        } else {
            // We picked the columns
            mNumRows = mRequestedNumRows;
        }
        
        if (mNumRows <= 0) {
        	mNumRows = 1;
        }

        switch (stretchMode) {
        case NO_STRETCH:
            // Nobody stretches
        	mRowHeight = requestedRowHeight;
            mHorizontalSpacing = requestedHorizontalSpacing;
            mRowHeight = requestedRowHeight;
            break;

        default:
            int spaceLeftOver = availableSpace - (mNumRows * requestedRowHeight) -
                    ((mNumRows - 1) * requestedVerticalSpacing);
            switch (stretchMode) {
            case STRETCH_COLUMN_WIDTH:
                // Stretch the columns
                mRowHeight = requestedRowHeight + spaceLeftOver / mNumRows;
                mHorizontalSpacing = requestedHorizontalSpacing;
                
                break;

            case STRETCH_SPACING:
                // Stretch the spacing between columns
            	mRowHeight = requestedRowHeight;
//                if (mNumColumns > 1) {
//                    mHorizontalSpacing = requestedHorizontalSpacing + 
//                        spaceLeftOver / (mNumColumns - 1);
//                } else {
//                    mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver;
//                }
                break;

            case STRETCH_SPACING_UNIFORM:
                // Stretch the spacing between columns
                mRowHeight = requestedRowHeight;
//                if (mNumColumns > 1) {
//                    mHorizontalSpacing = requestedHorizontalSpacing + 
//                        spaceLeftOver / (mNumColumns + 1);
//                } else {
//                    mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver;
//                }
                break;
            }

            break;
        }
        
        Log.d(TAG, "mNumRows: " + mNumRows + " mRowHeight:" + mRowHeight + 
        		" mHorizontalSpacing:" + mHorizontalSpacing + " mVerticalSpacing:" + mVerticalSpacing);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	if (DEBUG) {
    		Log.d(TAG, "onMeasure(). w:" + MeasureSpec.toString(widthMeasureSpec) + 
    				" h:" + MeasureSpec.toString(widthMeasureSpec));
    	}
    	
        // Sets up mListPadding
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            if (mRowHeight > 0) {
            	heightSize = mRowHeight + mListPadding.top + mListPadding.bottom;
            } else {
            	heightSize = mListPadding.top + mListPadding.bottom;
            }
            heightSize += getHorizontalScrollbarHeight();
        }
        
        int childHeight = heightSize - mListPadding.top - mListPadding.bottom;
        determineRows(heightSize);

        int childWidth = 0;

        mItemCount = mAdapter == null ? 0 : mAdapter.getCount();
        final int count = mItemCount;
        if (count > 0) {
            final View child = obtainView(0);

            HAbsListView.LayoutParams p = (HAbsListView.LayoutParams)child.getLayoutParams();
            if (p == null) {
                p = new HAbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                		ViewGroup.LayoutParams.FILL_PARENT, 0);
                child.setLayoutParams(p);
            }
            p.viewType = mAdapter.getItemViewType(0);

            int childWidthSpec = getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.width);
            int childHeightSpec = getChildMeasureSpec(
                    MeasureSpec.makeMeasureSpec(mRowHeight, MeasureSpec.EXACTLY), 0, p.height);
            child.measure(childWidthSpec, childHeightSpec);

            childWidth = child.getMeasuredWidth();

            if (mRecycler.shouldRecycleViewType(p.viewType)) {
                mRecycler.addScrapView(child);
            }
        }
        
        if (widthMode == MeasureSpec.UNSPECIFIED) {
        	widthSize = mListPadding.left + mListPadding.right + childWidth +
                    getHorizontalFadingEdgeLength() * 2;
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            int ourSize =  mListPadding.left + mListPadding.right;
           
            final int numRows = mNumRows;
            for (int i = 0; i < count; i += numRows) {
                ourSize += childWidth;
                if (i + numRows < count) {
                    ourSize += mRequestedHorizontalSpacing;
                }
                if (ourSize >= widthSize) {
                    ourSize = widthSize;
                    break;
                }
            }
            widthSize = ourSize;
        }

        setMeasuredDimension(widthSize, heightSize);
        mWidthMeasureSpec = widthMeasureSpec;
    }

    @Override
    protected void attachLayoutAnimationParameters(View child,
            ViewGroup.LayoutParams params, int index, int count) {

        GridLayoutAnimationController.AnimationParameters animationParams =
                (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

        if (animationParams == null) {
            animationParams = new GridLayoutAnimationController.AnimationParameters();
            params.layoutAnimationParameters = animationParams;
        }

        animationParams.count = count;
        animationParams.index = index;
        animationParams.columnsCount = mNumRows;
        animationParams.rowsCount = count / mNumRows;

        if (!mStackFromBottom) {
            animationParams.column = index % mNumRows;
            animationParams.row = index / mNumRows;
        } else {
            final int invertedIndex = count - 1 - index;

            animationParams.column = mNumRows - 1 - (invertedIndex % mNumRows);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / mNumRows;
        }
    }

    @Override
    protected void layoutChildren() {
        final boolean blockLayoutRequests = mBlockLayoutRequests;
        if (!blockLayoutRequests) {
            mBlockLayoutRequests = true;
        }

        try {
            super.layoutChildren();

            invalidate();

            if (mAdapter == null) {
                resetList();
                invokeOnItemScrollListener();
                return;
            }

            final int childrenTop = mListPadding.top;
            final int childrenLeft = mListPadding.left;
//            final int childrenBottom = mBottom - mTop - mListPadding.bottom;
            final int childrenBottom = getBottom() - getTop() - mListPadding.bottom;
            final int childrenRight = getRight() - getLeft() - mListPadding.right;

            int childCount = getChildCount();
            int index;
            int delta = 0;

            View sel;
            View oldSel = null;
            View oldFirst = null;
            View newSel = null;

            // Remember stuff we will need down below
            switch (mLayoutMode) {
            case LAYOUT_SET_SELECTION:
                index = mNextSelectedPosition - mFirstPosition;
                if (index >= 0 && index < childCount) {
                    newSel = getChildAt(index);
                }
                break;
            case LAYOUT_FORCE_TOP:
            case LAYOUT_FORCE_BOTTOM:
            case LAYOUT_SPECIFIC:
            case LAYOUT_SYNC:
                break;
            case LAYOUT_MOVE_SELECTION:
                if (mNextSelectedPosition >= 0) {
                    delta = mNextSelectedPosition - mSelectedPosition;
                }
                break;
            default:
                // Remember the previously selected view
                index = mSelectedPosition - mFirstPosition;
                if (index >= 0 && index < childCount) {
                    oldSel = getChildAt(index);
                }

                // Remember the previous first child
                oldFirst = getChildAt(0);
            }

            boolean dataChanged = mDataChanged;
            if (dataChanged) {
                handleDataChanged();
            }

            // Handle the empty set by removing all views that are visible
            // and calling it a day
            if (mItemCount == 0) {
                resetList();
                invokeOnItemScrollListener();
                return;
            }

            setSelectedPositionInt(mNextSelectedPosition);

            // Pull all children into the RecycleBin.
            // These views will be reused if possible
            final int firstPosition = mFirstPosition;
            final RecycleBin recycleBin = mRecycler;

            if (dataChanged) {
                for (int i = 0; i < childCount; i++) {
                    recycleBin.addScrapView(getChildAt(i));
                }
            } else {
                recycleBin.fillActiveViews(childCount, firstPosition);
            }

            // Clear out old views
            //removeAllViewsInLayout();
            detachAllViewsFromParent();

            switch (mLayoutMode) {
            case LAYOUT_SET_SELECTION:
                if (newSel != null) {
                    sel = fillFromSelection(newSel.getTop(), childrenTop, childrenBottom);
                } else {
                    sel = fillSelection(childrenTop, childrenBottom);
                }
                break;
            case LAYOUT_FORCE_TOP:
                mFirstPosition = 0;
                sel = fillFromLeft(childrenTop);
                adjustViewsUpOrDown();
                break;
            case LAYOUT_FORCE_BOTTOM:
                sel = fillLeft(mItemCount - 1, childrenBottom);
                adjustViewsUpOrDown();
                break;
            case LAYOUT_SPECIFIC:
                sel = fillSpecific(mSelectedPosition, mSpecificLeft);
                break;
            case LAYOUT_SYNC:
                sel = fillSpecific(mSyncPosition, mSpecificLeft);
                break;
            case LAYOUT_MOVE_SELECTION:
                // Move the selection relative to its old position
                sel = moveSelection(delta, childrenLeft, childrenRight);
                break;
            default:
                if (childCount == 0) {
                    if (!mStackFromBottom) {
                        setSelectedPositionInt(0);
                        sel = fillFromLeft(childrenLeft);
                    } else {
                        final int last = mItemCount - 1;
                        setSelectedPositionInt(last);
                        sel = fillFromBottom(last, childrenBottom);
                    }
                } else {
                    if (mSelectedPosition >= 0 && mSelectedPosition < mItemCount) {
                        sel = fillSpecific(mSelectedPosition, oldSel == null ?
                                childrenLeft : oldSel.getLeft());
                    } else if (mFirstPosition < mItemCount)  {
                        sel = fillSpecific(mFirstPosition, oldFirst == null ?
                                childrenLeft : oldFirst.getLeft());
                    } else {
                        sel = fillSpecific(0, childrenLeft);
                    }
                }
                break;
            }

            // Flush any cached views that did not get reused above
            recycleBin.scrapActiveViews();

            if (sel != null) {
               positionSelector(sel);
               mSelectedTop = sel.getTop();
            } else {
               mSelectedTop = 0;
               mSelectorRect.setEmpty();
            }

            mLayoutMode = LAYOUT_NORMAL;
            mDataChanged = false;
            mNeedSync = false;
            setNextSelectedPositionInt(mSelectedPosition);

            updateScrollIndicators();

            if (mItemCount > 0) {
                checkSelectionChanged();
            }

            invokeOnItemScrollListener();
        } finally {
            if (!blockLayoutRequests) {
                mBlockLayoutRequests = false;
            }
        }
    }


    /**
     * Obtain the view and add it to our list of children. The view can be made
     * fresh, converted from an unused view, or used as is if it was in the
     * recycle bin.
     *
     * @param position Logical position in the list
     * @param x left or right edge of the view to add
     * @param flow if true, align left edge to y. If false, align right edge to
     *        y.
     * @param childrenTop Left edge where children should be positioned
     * @param selected Is this position selected?
     * @param where to add new item in the list
     * @return View that was added
     */
    private View makeAndAddView(int position, int x, boolean flow, int childrenTop,
            boolean selected, int where) {
        View child;

        if (!mDataChanged) {
            // Try to use an exsiting view for this position
            child = mRecycler.getActiveView(position);
            if (child != null) {
                // Found it -- we're using an existing child
                // This just needs to be positioned
                setupChild(child, position, x, flow, childrenTop, selected, true, where);
                return child;
            }
        }

        // Make a new view for this position, or convert an unused view if
        // possible
        child = obtainView(position);

        // This needs to be positioned and measured
        setupChild(child, position, x, flow, childrenTop, selected, false, where);

        return child;
    }

    /**
     * Add a view as a child and make sure it is measured (if necessary) and
     * positioned properly.
     *
     * @param child The view to add
     * @param position The position of the view
     * @param x The y position relative to which this view will be positioned
     * @param flow if true, align top edge to y. If false, align bottom edge
     *        to y.
     * @param childrenTop Left edge where children should be positioned
     * @param selected Is this position selected?
     * @param recycled Has this view been pulled from the recycle bin? If so it
     *        does not need to be remeasured.
     * @param where Where to add the item in the list
     *
     */
    private void setupChild(View child, int position, int x, boolean flow, int childrenTop,
            boolean selected, boolean recycled, int where) {
        boolean isSelected = selected && shouldShowSelector();

        final boolean updateChildSelected = isSelected != child.isSelected();
        boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

        // Respect layout params that are already in the view. Otherwise make
        // some up...
        HAbsListView.LayoutParams p = (HAbsListView.LayoutParams)child.getLayoutParams();
        if (p == null) {
            p = new HAbsListView.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        }
        p.viewType = mAdapter.getItemViewType(position);

        if (recycled) {
            attachViewToParent(child, where, p);
        } else {
            addViewInLayout(child, where, p, true);
        }

        if (updateChildSelected) {
            child.setSelected(isSelected);
            if (isSelected) {
                requestFocus();
            }
        }

        if (needToMeasure) {
            int childHeightSpec = ViewGroup.getChildMeasureSpec(
            		MeasureSpec.makeMeasureSpec(mRowHeight, MeasureSpec.EXACTLY), 0, p.height);

            int childWidthSpec = ViewGroup.getChildMeasureSpec(
            		MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 0, p.width);
            child.measure(childWidthSpec, childHeightSpec);
        } else {
            cleanupLayoutState(child);
        }

        final int w = child.getMeasuredWidth();
        final int h = child.getMeasuredHeight();

        int childLeft = flow ? x : x - w;
        int childTop = 0;

        switch (mGravity & Gravity.VERTICAL_GRAVITY_MASK) {
        case Gravity.TOP:
        	childTop = childrenTop;
            break;
        case Gravity.CENTER_VERTICAL:
        	childTop = childrenTop + ((mRowHeight - h) / 2);
            break;
        case Gravity.BOTTOM:
        	childTop = childrenTop + mRowHeight - w;
            break;
        default:
        	childTop = childrenTop;
            break;
        }

        if (needToMeasure) {
            final int childRight = childLeft + w;
            final int childBottom = childTop + h;
            child.layout(childLeft, childTop, childRight, childBottom);
        } else {
            child.offsetLeftAndRight(childLeft - child.getLeft());
            child.offsetTopAndBottom(childTop - child.getTop());
        }

        if (mCachingStarted) {
            child.setDrawingCacheEnabled(true);
        }
    }

    /**
     * Sets the currently selected item
     * 
     * @param position Index (starting at 0) of the data item to be selected.
     * 
     * If in touch mode, the item will not be selected but it will still be positioned
     * appropriately.
     */
    @Override
    public void setSelection(int position) {
        if (!isInTouchMode()) {
            setNextSelectedPositionInt(position);
        } else {
            mResurrectToPosition = position;
        }
        mLayoutMode = LAYOUT_SET_SELECTION;
        requestLayout();
    }

    /**
     * Makes the item at the supplied position selected.
     *
     * @param position the position of the new selection
     */
    @Override
    void setSelectionInt(int position) {
        setNextSelectedPositionInt(position);
        layoutChildren();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return commonKey(keyCode, 1, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return commonKey(keyCode, repeatCount, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return commonKey(keyCode, 1, event);
    }

    private boolean commonKey(int keyCode, int count, KeyEvent event) {
        if (mAdapter == null) {
            return false;
        }

        if (mDataChanged) {
            layoutChildren();
        }

        boolean handled = false;
        int action = event.getAction();

        if (action != KeyEvent.ACTION_UP) {
            if (mSelectedPosition < 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_SPACE:
                    case KeyEvent.KEYCODE_ENTER:
                        resurrectSelection();
                        return true;
                }
            }

            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    handled = arrowScroll(FOCUS_LEFT);
                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    handled = arrowScroll(FOCUS_RIGHT);
                    break;

                case KeyEvent.KEYCODE_DPAD_UP:
                    if (!event.isAltPressed()) {
                        handled = arrowScroll(FOCUS_UP);

                    } else {
                        handled = fullScroll(FOCUS_UP);
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (!event.isAltPressed()) {
                        handled = arrowScroll(FOCUS_DOWN);
                    } else {
                        handled = fullScroll(FOCUS_DOWN);
                    }
                    break;

                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER: {
                    if (getChildCount() > 0 && event.getRepeatCount() == 0) {
                        keyPressed();
                    }

                    return true;
                }

                case KeyEvent.KEYCODE_SPACE:
                    if (mPopup == null || !mPopup.isShowing()) {
                        if (!event.isShiftPressed()) {
                            handled = pageScroll(FOCUS_DOWN);
                        } else {
                            handled = pageScroll(FOCUS_UP);
                        }
                    }
                    break;
            }
        }

        if (!handled) {
            handled = sendToTextFilter(keyCode, count, event);
        }

        if (handled) {
            return true;
        } else {
            switch (action) {
                case KeyEvent.ACTION_DOWN:
                    return super.onKeyDown(keyCode, event);
                case KeyEvent.ACTION_UP:
                    return super.onKeyUp(keyCode, event);
                case KeyEvent.ACTION_MULTIPLE:
                    return super.onKeyMultiple(keyCode, count, event);
                default:
                    return false;
            }
        }
    }

    /**
     * Scrolls up or down by the number of items currently present on screen.
     *
     * @param direction either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     * @return whether selection was moved
     */
    boolean pageScroll(int direction) {
        int nextPage = -1;

        if (direction == FOCUS_UP) {
            nextPage = Math.max(0, mSelectedPosition - getChildCount() - 1);
        } else if (direction == FOCUS_DOWN) {
            nextPage = Math.min(mItemCount - 1, mSelectedPosition + getChildCount() - 1);
        }

        if (nextPage >= 0) {
            setSelectionInt(nextPage);
            invokeOnItemScrollListener();
            return true;
        }

        return false;
    }

    /**
     * Go to the last or first item if possible.
     *
     * @param direction either {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}.
     *
     * @return Whether selection was moved.
     */
    boolean fullScroll(int direction) {
        boolean moved = false;
        if (direction == FOCUS_UP) {
            mLayoutMode = LAYOUT_SET_SELECTION;
            setSelectionInt(0);
            invokeOnItemScrollListener();
            moved = true;
        } else if (direction == FOCUS_DOWN) {
            mLayoutMode = LAYOUT_SET_SELECTION;
            setSelectionInt(mItemCount - 1);
            invokeOnItemScrollListener();
            moved = true;
        }

        return moved;
    }

    /**
     * Scrolls to the next or previous item, horizontally or vertically.
     *
     * @param direction either {@link View#FOCUS_LEFT}, {@link View#FOCUS_RIGHT},
     *        {@link View#FOCUS_UP} or {@link View#FOCUS_DOWN}
     *
     * @return whether selection was moved
     */
    boolean arrowScroll(int direction) {
        final int selectedPosition = mSelectedPosition;
        final int numRows = mNumRows;

        int startOfColumnPos;
        int endOfColumnPos;

        boolean moved = false;

        if (!mStackFromBottom) {
            startOfColumnPos = (selectedPosition / numRows) * numRows;
            endOfColumnPos = Math.min(startOfColumnPos + numRows - 1, mItemCount - 1);
        } else {
            final int invertedSelection = mItemCount - 1 - selectedPosition;
            endOfColumnPos = mItemCount - 1 - (invertedSelection / numRows) * numRows;
            startOfColumnPos = Math.max(0, endOfColumnPos - numRows + 1);
        }

        switch (direction) {
            case FOCUS_UP:
//                if (startOfColumnPos > 0) {
//                    mLayoutMode = LAYOUT_MOVE_SELECTION;
//                    setSelectionInt(Math.max(0, selectedPosition - numRows));
//                    moved = true;
//                }            
                if (selectedPosition > startOfColumnPos) {
                    mLayoutMode = LAYOUT_MOVE_SELECTION;
                    setSelectionInt(Math.max(0, selectedPosition - 1));
                    moved = true;
                }
                break;
            case FOCUS_DOWN:
//                if (endOfColumnPos < mItemCount - 1) {
//                    mLayoutMode = LAYOUT_MOVE_SELECTION;
//                    setSelectionInt(Math.min(selectedPosition + numRows, mItemCount - 1));
//                    moved = true;
//                }
                if (selectedPosition < endOfColumnPos) {
                    mLayoutMode = LAYOUT_MOVE_SELECTION;
                    setSelectionInt(Math.min(selectedPosition + 1, mItemCount - 1));
                    moved = true;
                }
                break;
            case FOCUS_LEFT:
//                if (selectedPosition > startOfColumnPos) {
//                    mLayoutMode = LAYOUT_MOVE_SELECTION;
//                    setSelectionInt(Math.max(0, selectedPosition - 1));
//                    moved = true;
//                }
            	if (startOfColumnPos > 0) {
            		mLayoutMode = LAYOUT_MOVE_SELECTION;
            		setSelectionInt(Math.max(0, selectedPosition - numRows));
            		moved = true;
            	}     
                break;
            case FOCUS_RIGHT:
//                if (selectedPosition < endOfColumnPos) {
//                    mLayoutMode = LAYOUT_MOVE_SELECTION;
//                    setSelectionInt(Math.min(selectedPosition + 1, mItemCount - 1));
//                    moved = true;
//                }
                if (endOfColumnPos < mItemCount - 1) {
                	mLayoutMode = LAYOUT_MOVE_SELECTION;
                	setSelectionInt(Math.min(selectedPosition + numRows, mItemCount - 1));
                	moved = true;
                }
                break;
        }

        if (moved) {
            playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
            invokeOnItemScrollListener();
        }

        return moved;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);

        int closestChildIndex = -1;
        if (gainFocus && previouslyFocusedRect != null) {
//            previouslyFocusedRect.offset(mScrollX, mScrollY);
            previouslyFocusedRect.offset(getScrollX(), getScrollY());

            // figure out which item should be selected based on previously
            // focused rect
            Rect otherRect = mTempRect;
            int minDistance = Integer.MAX_VALUE;
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                // only consider view's on appropriate edge of grid
                if (!isCandidateSelection(i, direction)) {
                    continue;
                }

                final View other = getChildAt(i);
                other.getDrawingRect(otherRect);
                offsetDescendantRectToMyCoords(other, otherRect);
                int distance = getDistance(previouslyFocusedRect, otherRect, direction);

                if (distance < minDistance) {
                    minDistance = distance;
                    closestChildIndex = i;
                }
            }
        }

        if (closestChildIndex >= 0) {
            setSelection(closestChildIndex + mFirstPosition);
        } else {
            requestLayout();
        }
    }

    /**
     * Is childIndex a candidate for next focus given the direction the focus
     * change is coming from?
     * @param childIndex The index to check.
     * @param direction The direction, one of
     *        {FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}
     * @return Whether childIndex is a candidate.
     */
    private boolean isCandidateSelection(int childIndex, int direction) {
        final int count = getChildCount();
        final int invertedIndex = count - 1 - childIndex;

        int rowStart;
        int rowEnd;

        if (!mStackFromBottom) {
            rowStart = childIndex - (childIndex % mNumRows);
            rowEnd = Math.max(rowStart + mNumRows - 1, count);
        } else {
            rowEnd = count - 1 - (invertedIndex - (invertedIndex % mNumRows));
            rowStart = Math.max(0, rowEnd - mNumRows + 1);
        }

        switch (direction) {
            case View.FOCUS_RIGHT:
                // coming from left, selection is only valid if it is on left
                // edge
                return childIndex == rowStart;
            case View.FOCUS_DOWN:
                // coming from top; only valid if in top row
                return rowStart == 0;
            case View.FOCUS_LEFT:
                // coming from right, must be on right edge
                return childIndex == rowEnd;
            case View.FOCUS_UP:
                // coming from bottom, need to be in last row
                return rowEnd == count - 1;
            default:
                throw new IllegalArgumentException("direction must be one of "
                        + "{FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, FOCUS_RIGHT}.");
        }
    }

    /**
     * Describes how the child views are horizontally aligned. Defaults to Gravity.LEFT
     *
     * @param gravity the gravity to apply to this grid's children
     *
     * @attr ref android.R.styleable#GridView_gravity
     */
    public void setGravity(int gravity) {
        if (mGravity != gravity) {
            mGravity = gravity;
            requestLayoutIfNecessary();
        }
    }

    /**
     * Set the amount of horizontal (x) spacing to place between each item
     * in the grid.
     *
     * @param horizontalSpacing The amount of horizontal space between items,
     * in pixels.
     *
     * @attr ref android.R.styleable#GridView_horizontalSpacing
     */
    public void setHorizontalSpacing(int horizontalSpacing) {
        if (horizontalSpacing != mRequestedHorizontalSpacing) {
            mRequestedHorizontalSpacing = horizontalSpacing;
            requestLayoutIfNecessary();
        }
    }


    /**
     * Set the amount of vertical (y) spacing to place between each item
     * in the grid.
     *
     * @param verticalSpacing The amount of vertical space between items,
     * in pixels.
     *
     * @attr ref android.R.styleable#GridView_verticalSpacing
     */
    public void setVerticalSpacing(int verticalSpacing) {
        if (verticalSpacing != mRequestedVerticalSpacing) {
            mRequestedVerticalSpacing = verticalSpacing;
            requestLayoutIfNecessary();
        }
    }

    /**
     * Control how items are stretched to fill their space.
     *
     * @param stretchMode Either {@link #NO_STRETCH},
     * {@link #STRETCH_SPACING}, {@link #STRETCH_SPACING_UNIFORM}, or {@link #STRETCH_COLUMN_WIDTH}.
     *
     * @attr ref android.R.styleable#GridView_stretchMode
     */
    public void setStretchMode(int stretchMode) {
        if (stretchMode != mStretchMode) {
            mStretchMode = stretchMode;
            requestLayoutIfNecessary();
        }
    }

    public int getStretchMode() {
        return mStretchMode;
    }

    /**
     * Set the width of columns in the grid.
     *
     * @param rowHeight The column width, in pixels.
     *
     * @attr ref android.R.styleable#GridView_columnWidth
     */
    public void setRowHeight(int rowHeight) {
        if (rowHeight != mRequestedRowHeight) {
            mRequestedRowHeight = rowHeight;
            requestLayoutIfNecessary();
        }
    }

    /**
     * Set the number of columns in the grid
     *
     * @param numRows The desired number of columns.
     *
     * @attr ref android.R.styleable#GridView_numColumns
     */
    public void setNumRows(int numRows) {
        if (numRows != mRequestedNumRows) {
            mRequestedNumRows = numRows;
            requestLayoutIfNecessary();
        }
    }

    /**
     * Make sure views are touching the top or bottom edge, as appropriate for
     * our gravity
     */
    private void adjustViewsUpOrDown() {
        final int childCount = getChildCount();

        if (childCount > 0) {
            int delta;
            View child;

            if (!mStackFromBottom) {
                // Uh-oh -- we came up short. Slide all views up to make them
                // align with the top
                child = getChildAt(0);
                delta = child.getLeft() - mListPadding.left;
                if (mFirstPosition != 0) {
                    // It's OK to have some space above the first item if it is
                    // part of the vertical spacing
                    delta -= mRequestedHorizontalSpacing;
                }
                if (delta < 0) {
                    // We only are looking to see if we are too low, not too high
                    delta = 0;
                }
            } else {
                // we are too high, slide all views down to align with bottom
                child = getChildAt(childCount - 1);
                delta = child.getRight() - (getWidth() - mListPadding.right);
                
                if (mFirstPosition + childCount < mItemCount) {
                    // It's OK to have some space below the last item if it is
                    // part of the vertical spacing
                    delta += mRequestedHorizontalSpacing;
                }
                
                if (delta > 0) {
                    // We only are looking to see if we are too high, not too low
                    delta = 0;
                }
            }

            if (delta != 0) {
//                offsetChildrenTopAndBottom(-delta);
//                ViewCompat.offsetChildrenTopAndBottom(this, -delta);
            	ViewCompat.offsetChildrenLeftAndRight(this, -delta);
            }
        }
    }
    
    @Override
    protected int computeVerticalScrollExtent() {
        final int count = getChildCount();
        if (count > 0) {
            final int numRows = mNumRows;
            final int rowCount = (count + numRows - 1) / numRows;
            
            int extent = rowCount * 100;

            View view = getChildAt(0);
            final int top = view.getTop();
            int height = view.getHeight();
            if (height > 0) {
                extent += (top * 100) / height;
            }

            view = getChildAt(count - 1);
            final int bottom = view.getBottom();
            height = view.getHeight();
            if (height > 0) {
                extent -= ((bottom - getHeight()) * 100) / height;
            }

            return extent;
        }
        return 0;
    }

    @Override
    protected int computeVerticalScrollOffset() {
        if (mFirstPosition >= 0 && getChildCount() > 0) {
            final View view = getChildAt(0);
            final int top = view.getTop();
            int height = view.getHeight();
            if (height > 0) {
                final int whichRow = mFirstPosition / mNumRows;
                return Math.max(whichRow * 100 - (top * 100) / height, 0);
            }
        }
        return 0;
    }

    @Override
    protected int computeVerticalScrollRange() {
        // TODO: Account for vertical spacing too
        final int numColumns = mNumRows;
        final int rowCount = (mItemCount + numColumns - 1) / numColumns;
        return Math.max(rowCount * 100, 0);
    }
}


