package in.cinderella.testapp.Utils;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import in.cinderella.testapp.R;


public class HashtagView extends LinearLayout {

    @IntDef({GRAVITY_LEFT, GRAVITY_CENTER, GRAVITY_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GravityMode {
    }

    @SuppressLint("RtlHardcoded")
    public static final int GRAVITY_LEFT = Gravity.LEFT;
    @SuppressLint("RtlHardcoded")
    public static final int GRAVITY_RIGHT = Gravity.RIGHT;
    public static final int GRAVITY_CENTER = Gravity.CENTER;

    @IntDef({MODE_WRAP, MODE_STRETCH, MODE_EQUAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StretchMode {
    }

    public static final int MODE_WRAP = 0;
    public static final int MODE_STRETCH = 1;
    public static final int MODE_EQUAL = 2;

    @IntDef({DISTRIBUTION_LEFT, DISTRIBUTION_MIDDLE, DISTRIBUTION_RIGHT, DISTRIBUTION_RANDOM, DISTRIBUTION_NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RowDistribution {
    }

    public static final int DISTRIBUTION_LEFT = 0;
    public static final int DISTRIBUTION_MIDDLE = 1;
    public static final int DISTRIBUTION_RIGHT = 2;
    public static final int DISTRIBUTION_RANDOM = 3;
    public static final int DISTRIBUTION_NONE = 4;

    @IntDef({ELLIPSIZE_START, ELLIPSIZE_MIDDLE, ELLIPSIZE_END, ELLIPSIZE_MARQUEE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Ellipsize {
    }

    public static final int ELLIPSIZE_START = 0;
    public static final int ELLIPSIZE_MIDDLE = 1;
    public static final int ELLIPSIZE_END = 2;
    public static final int ELLIPSIZE_MARQUEE = 3;

    @IntDef({COMPOSE_ORIGIN, COMPOSE_LINEAR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Compose {
    }

    public static final int COMPOSE_ORIGIN = 0;
    public static final int COMPOSE_LINEAR = 1;

    private static final SparseArray<TextUtils.TruncateAt> ellipsizeList = new SparseArray<>(4);

    static {
        ellipsizeList.put(ELLIPSIZE_START, TextUtils.TruncateAt.START);
        ellipsizeList.put(ELLIPSIZE_MIDDLE, TextUtils.TruncateAt.MIDDLE);
        ellipsizeList.put(ELLIPSIZE_END, TextUtils.TruncateAt.END);
        ellipsizeList.put(ELLIPSIZE_MARQUEE, TextUtils.TruncateAt.MARQUEE);
    }

    private final LayoutParams rowLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private final LayoutParams itemLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private final FrameLayout.LayoutParams itemFrameParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private LayoutTransition layoutTransition;

    private List<TagsClickListener> clickListeners;
    private List<TagsSelectListener> selectListeners;

    private List<Float> widthList;
    private List<ItemData> data;
    private Multimap<Integer, ItemData> viewMap;
    private SortState sortState = SortState.initState();

    private ColorStateList itemTextColorStateList;
    private int itemMargin;
    private int itemPaddingLeft;
    private int itemPaddingRight;
    private int itemPaddingTop;
    private int itemPaddingBottom;
    private int itemDrawablePadding;
    private int minItemWidth;
    private int maxItemWidth;
    private int itemTextGravity;
    private int itemTextEllipsize;
    private float itemTextSize;

    private int rowMargin;
    private int rowGravity;
    private int rowDistribution;
    private int rowMode;
    private int rowCount;
    private int composeMode;
    private int backgroundDrawable;
    private int foregroundDrawable;
    private int leftDrawable;
    private int leftSelectedDrawable;
    private int rightDrawable;
    private int rightSelectedDrawable;

    private Typeface typeface;

    private float totalItemsWidth;
    private int selectionLimit = -1;
    private int selectedItemsCount = 0;

    private boolean isInSelectMode;
    private boolean isDynamic;

    private DataTransform transformer = DefaultTransform.newInstance();
    private DataSelector selector = DefaultSelector.newInstance();

    private ComposeHelper composeHelper;

    private final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            if (isPrepared()) {
                wrap();
                sort();
                draw();
                getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
            }
            return true;
        }
    };

    public HashtagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);

        extractAttributes(attrs);
        prepareLayoutParams();
        prepareLayoutTransition();
        prepareComposeHelper();

        widthList = new ArrayList<>();
        data = new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getData() {
        List<T> list = new ArrayList<>();

        if (viewMap != null && !viewMap.isEmpty()) {
            for (ItemData itemData : viewMap.values()) {
                list.add(((T) itemData.data));
            }
        }

        return list;
    }

    public <T> void setData(@NonNull List<T> list) {
        widthList.clear();
        data.clear();

        if (list.isEmpty()) {
            removeAllViews();
            return;
        }

        for (T item : list) {
            data.add(new ItemData<>(item));
        }
        getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }


    public <T> void setData(@NonNull List<T> list, @NonNull DataTransform<T> transformer) {
        this.transformer = transformer;
        setData(list);
    }

    public <T> boolean addItem(@NonNull T item) {
        if (!isDynamic) return false;

        ItemData itemData = new ItemData<>(item);
        if (viewMap != null && viewMap.values().contains(itemData)) return false;

        if (viewMap != null) {
            data.addAll(viewMap.values());
            viewMap.clear();
        }
        data.add(itemData);

        getViewTreeObserver().addOnPreDrawListener(preDrawListener);
        return true;
    }


    public boolean removeAll() {
        if (!isDynamic || viewMap == null || viewMap.isEmpty()) return false;

        data.clear();
        viewMap.clear();
        removeAllViews();

        return true;
    }
    public void addOnTagSelectListener(TagsSelectListener listener) {
        if (selectListeners == null) {
            selectListeners = new ArrayList<>();
        }
        selectListeners.add(listener);
    }

    public void setBackgroundColor(@ColorRes int backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
    }

    private void extractAttributes(AttributeSet attrs) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.HashtagView, 0, 0);
        try {
            itemMargin = a.getDimensionPixelOffset(R.styleable.HashtagView_tagMargin, getResources().getDimensionPixelOffset(R.dimen.default_item_margin));
            itemPaddingLeft = a.getDimensionPixelOffset(R.styleable.HashtagView_tagPaddingLeft, getResources().getDimensionPixelOffset(R.dimen.default_item_padding));
            itemPaddingRight = a.getDimensionPixelOffset(R.styleable.HashtagView_tagPaddingRight, getResources().getDimensionPixelOffset(R.dimen.default_item_padding));
            itemPaddingTop = a.getDimensionPixelOffset(R.styleable.HashtagView_tagPaddingTop, getResources().getDimensionPixelOffset(R.dimen.default_item_padding));
            itemPaddingBottom = a.getDimensionPixelOffset(R.styleable.HashtagView_tagPaddingBottom, getResources().getDimensionPixelOffset(R.dimen.default_item_padding));
            itemDrawablePadding = a.getDimensionPixelOffset(R.styleable.HashtagView_tagDrawablePadding, 0);
            minItemWidth = a.getDimensionPixelOffset(R.styleable.HashtagView_tagMinWidth, getResources().getDimensionPixelOffset(R.dimen.min_item_width));
            maxItemWidth = a.getDimensionPixelOffset(R.styleable.HashtagView_tagMaxWidth, getResources().getDimensionPixelOffset(R.dimen.min_item_width));
            rowMargin = a.getDimensionPixelOffset(R.styleable.HashtagView_rowMargin, getResources().getDimensionPixelOffset(R.dimen.default_row_margin));
            itemTextSize = a.getDimension(R.styleable.HashtagView_tagTextSize, getResources().getDimension(R.dimen.default_text_size));

            itemTextGravity = a.getInt(R.styleable.HashtagView_tagTextGravity, Gravity.CENTER);
            itemTextEllipsize = a.getInt(R.styleable.HashtagView_tagEllipsize, ELLIPSIZE_END);
            rowGravity = a.getInt(R.styleable.HashtagView_rowGravity, Gravity.CENTER);
            rowDistribution = a.getInt(R.styleable.HashtagView_rowDistribution, DISTRIBUTION_NONE);
            rowMode = a.getInt(R.styleable.HashtagView_rowMode, MODE_WRAP);
            rowCount = a.getInt(R.styleable.HashtagView_rowsQuantity, 0);
            composeMode = a.getInt(R.styleable.HashtagView_composeMode, COMPOSE_ORIGIN);

            backgroundDrawable = a.getResourceId(R.styleable.HashtagView_tagBackground, 0);
            foregroundDrawable = a.getResourceId(R.styleable.HashtagView_tagForeground, 0);
            leftDrawable = a.getResourceId(R.styleable.HashtagView_tagDrawableLeft, 0);
            leftSelectedDrawable = a.getResourceId(R.styleable.HashtagView_tagSelectedDrawableLeft, 0);
            rightDrawable = a.getResourceId(R.styleable.HashtagView_tagDrawableRight, 0);
            rightSelectedDrawable = a.getResourceId(R.styleable.HashtagView_tagSelectedDrawableRight, 0);

            itemTextColorStateList = a.getColorStateList(R.styleable.HashtagView_tagTextColor);
            if (itemTextColorStateList == null) {
                itemTextColorStateList = ColorStateList.valueOf(Color.BLACK);
            }

            isInSelectMode = a.getBoolean(R.styleable.HashtagView_selectionMode, false);
            isDynamic = a.getBoolean(R.styleable.HashtagView_dynamicMode, false);
        } finally {
            a.recycle();
        }
    }

    private void prepareLayoutParams() {
        itemFrameParams.gravity = itemTextGravity;

        itemLayoutParams.leftMargin = itemMargin;
        itemLayoutParams.rightMargin = itemMargin;
        itemLayoutParams.weight = rowMode > 0 ? 1 : 0;
        if (MODE_EQUAL == rowMode) {
            itemLayoutParams.width = 0;
        }

        rowLayoutParams.topMargin = rowMargin;
        rowLayoutParams.bottomMargin = rowMargin;
    }

    private void prepareLayoutTransition() {
        if (isDynamic) {
            layoutTransition = new LayoutTransition();
            layoutTransition.setStagger(LayoutTransition.APPEARING, 250);
            layoutTransition.setAnimateParentHierarchy(false);
        }
    }

    private void prepareComposeHelper() {
        switch (composeMode) {
            case COMPOSE_ORIGIN:
                composeHelper = new OriginalComposer();
                break;
            case COMPOSE_LINEAR:
                composeHelper = new LinearComposer();
                break;
        }
    }

    private boolean isPrepared() {
        return getViewWidth() > 0 || rowCount > 0;
    }

    private void wrap() {
        if (data == null || data.isEmpty()) return;
        widthList.clear();
        totalItemsWidth = 0;

        for (ItemData item : data) {
            wrapItem(item);
            widthList.add(item.width);
            totalItemsWidth += item.width;
        }

        composeHelper.prepareData();
    }

    private void wrapItem(ItemData item) {
        View view = inflateItemView(item);

        TextView itemView = (TextView) view.findViewById(R.id.text);
        itemView.setText(transformer.prepare(item.data));
        decorateItemTextView(itemView);

        float width = itemView.getMeasuredWidth() + drawableMetrics(itemView) + totalOffset();
        width = Math.max(width, minItemWidth);
        width = Math.min(width, getViewWidth() - 2 * totalOffset());
        item.view = view;
        item.width = width;
        setItemPreselected(item);
    }

    private void setItemPreselected(ItemData item) {
        if (isInSelectMode) {
            boolean selection = selector.preselect(item.data);
            if (selection) {
                if (selectionLimit == -1 || selectedItemsCount < selectionLimit) {
                    selectedItemsCount += 1;
                } else {
                    return;
                }
            }
            item.isSelected = selection;

            item.decorateText(transformer);
            item.displaySelection(leftDrawable, leftSelectedDrawable, rightDrawable, rightSelectedDrawable);
        }
    }

    private void sort() {
        if (data == null || data.isEmpty()) return;

        evaluateRowsQuantity();

        final int[] rowWidths = new int[sortState.totalRows()];
        viewMap = ArrayListMultimap.create(sortState.totalRows(), data.size());

        composeHelper.sortingLoop(0, sortState.rowCount, rowWidths, true);

        if (sortState.hasExtraRows) {
            composeHelper.sortingLoop(sortState.rowCount, sortState.totalRows(), rowWidths, false);
            sortState.release();
        }
    }

    private boolean extraCondition() {
        return !(sortState.hasExtraRows && data.size() == sortState.extraCount);
    }

    private void draw() {
        if (viewMap == null || viewMap.isEmpty()) return;
        removeAllViews();

        List<Integer> keys = new ArrayList<>(viewMap.keySet());
        Collections.sort(keys);

        for (Integer key : keys) {
            ViewGroup rowLayout = getRowLayout(viewMap.get(key).size());
            addView(rowLayout);
            applyDistribution(viewMap.get(key));
            rowLayout.setLayoutTransition(layoutTransition);

            for (ItemData item : viewMap.get(key)) {
                releaseParent(item.view);
                rowLayout.addView(item.view, itemLayoutParams);
            }
        }
        keys.clear();
    }

    private void releaseParent(View child) {
        ViewGroup parent = (ViewGroup) child.getParent();
        if (parent != null) {
            parent.removeView(child);
        }
    }

    private void applyDistribution(Collection<ItemData> list) {
        switch (rowDistribution) {
            case DISTRIBUTION_LEFT:
                Collections.sort((List) list);
                break;
            case DISTRIBUTION_MIDDLE:
                SortUtil.symmetricSort((List) list);
                break;
            case DISTRIBUTION_RIGHT:
                Collections.sort((List) list, Collections.reverseOrder());
                break;
            case DISTRIBUTION_RANDOM:
                Collections.shuffle((List) list);
                break;
            case DISTRIBUTION_NONE:
                break;
        }
    }

    private ViewGroup getRowLayout(int weightSum) {
        LinearLayout rowLayout = new LinearLayout(getContext());
        rowLayout.setLayoutParams(rowLayoutParams);
        rowLayout.setOrientation(HORIZONTAL);
        rowLayout.setGravity(rowGravity);
        rowLayout.setWeightSum(weightSum);
        return rowLayout;
    }

    private int getViewWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    private int totalOffset() {
        return itemPaddingLeft + itemPaddingRight + 2 * itemMargin;
    }

    private int drawableMetrics(TextView textView) {
        int drawablesWidth = 0;
        Drawable[] drawables = textView.getCompoundDrawables();
        drawablesWidth += drawables[0] != null ? drawables[0].getIntrinsicWidth() + itemDrawablePadding : 0;
        drawablesWidth += drawables[2] != null ? drawables[2].getIntrinsicWidth() + itemDrawablePadding : 0;
        return drawablesWidth;
    }

    private void evaluateRowsQuantity() {
        if (widthList == null || widthList.isEmpty()) return;

        if (rowCount > 0) {
            sortState.preserveState(rowCount);
            return;
        }

        composeHelper.evaluateRowsQuantity();
    }

    private View inflateItemView(final ItemData item) {
        ViewGroup itemLayout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.layout_item, this, false);
        itemLayout.setBackgroundResource(backgroundDrawable);
        itemLayout.setPadding(itemPaddingLeft, itemPaddingTop, itemPaddingRight, itemPaddingBottom);
        itemLayout.setMinimumWidth(minItemWidth);
        try {
            if (foregroundDrawable != 0)
                ((FrameLayout) itemLayout).setForeground(ContextCompat.getDrawable(getContext(), foregroundDrawable));
        } catch (Exception e) {
            e.printStackTrace();
        }

        itemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInSelectMode) {
                    handleSelection(item);
                } else {
                    handleClick(item);
                }
            }
        });
        return itemLayout;
    }

    private void decorateItemTextView(TextView textView) {
        textView.setTextColor(itemTextColorStateList);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemTextSize);
        textView.setCompoundDrawablePadding(itemDrawablePadding);
        textView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, 0, rightDrawable, 0);
        textView.setEllipsize(ellipsizeList.get(itemTextEllipsize));
        if (maxItemWidth > 0) textView.setMaxWidth(maxItemWidth);
        if (typeface != null) textView.setTypeface(typeface);

        textView.setLayoutParams(itemFrameParams);
        textView.measure(0, 0);
    }

    private void handleSelection(ItemData item) {
        if (item.isSelected) {
            selectedItemsCount -= 1;
        } else {
            if (selectionLimit == -1 || selectedItemsCount < selectionLimit) {
                selectedItemsCount += 1;
            } else {
                return;
            }
        }
        if (viewMap != null && !viewMap.isEmpty()) {
            for (ItemData it : viewMap.values()) {
                if (it.isSelected) {
                    it.select(leftDrawable, leftSelectedDrawable, rightDrawable, rightSelectedDrawable);
                    it.decorateText(transformer);
                }
            }
        }
        item.select(leftDrawable, leftSelectedDrawable, rightDrawable, rightSelectedDrawable);
        item.decorateText(transformer);

        if (selectListeners != null) {
            for (TagsSelectListener listener : selectListeners) {
                listener.onItemSelected(item.data, item.isSelected);
            }
        }
    }

    private void handleClick(ItemData item) {
        if (clickListeners != null) {
            for (TagsClickListener listener : clickListeners) {
                listener.onItemClicked(item.data);
            }
        }
    }

    private class OriginalComposer implements ComposeHelper {

        @Override
        public void prepareData() {
            Collections.sort(data);
            Collections.sort(widthList, Collections.reverseOrder());
        }

        @Override
        public void evaluateRowsQuantity() {
            int rows = (int) Math.ceil(totalItemsWidth / getViewWidth());
            int[] rowsWidth = new int[rows];
            int iterationLimit = rows + widthList.size();
            int counter = 0;

            sortState.preserveState(rows);
            while (!widthList.isEmpty()) {
                rowIteration:
                for (int i = 0; i < rows; i++) {
                    if (counter > iterationLimit) {
                        sortState.preserveState(rows, true, widthList.size());
                        widthList.clear();
                        return;
                    }

                    counter++;
                    for (Float item : widthList) {
                        if (rowsWidth[i] + item <= getViewWidth()) {
                            rowsWidth[i] += item;
                            widthList.remove(item);
                            continue rowIteration;
                        }
                    }
                }
            }
        }

        @Override
        public void sortingLoop(int start, int end, int[] widths, boolean hasExtra) {
            while (!data.isEmpty() && (!hasExtra || extraCondition())) {
                iteration:

                for (int i = start; i < end; i++) {
                    Iterator<ItemData> iterator = data.iterator();

                    while (iterator.hasNext()) {
                        ItemData item = iterator.next();
                        if (rowCount > 0 || widths[i] + item.width <= getViewWidth()) {
                            widths[i] += item.width;
                            viewMap.put(i, item);
                            iterator.remove();

                            if (hasExtra) continue iteration;
                        }
                    }
                }
            }
        }
    }

    private class LinearComposer implements ComposeHelper {

        @Override
        public void prepareData() {
        }

        @Override
        public void evaluateRowsQuantity() {
        }

        @Override
        public void sortingLoop(int start, int end, int[] widths, boolean hasExtra) {
            if (data.isEmpty()) return;

            int rowWidth = 0;
            int index = start;
            Iterator<ItemData> it = data.iterator();

            while (it.hasNext()) {
                ItemData item = it.next();
                if (rowWidth + item.width > getViewWidth()) {
                    index++;
                    rowWidth = 0;
                }

                rowWidth += item.width;
                viewMap.put(index, item);
                it.remove();
            }
        }
    }

    private interface ComposeHelper {
        void prepareData();

        void evaluateRowsQuantity();

        void sortingLoop(int start, int end, int[] widths, boolean hasExtra);
    }

    public interface TagsClickListener {
        void onItemClicked(Object item);
    }

    public interface TagsSelectListener {
        void onItemSelected(Object item, boolean selected);
    }

    public interface DataTransform<T> {
        CharSequence prepare(T item);
    }
    public interface DataStateTransform<T> extends DataTransform<T> {
        CharSequence prepareSelected(T item);
    }

    public interface DataSelector<T> {
        boolean preselect(T item);
    }

    private static class SortState {
        boolean hasExtraRows = false;
        int extraCount = 0;
        int rowCount = 0;

        static SortState initState() {
            return new SortState();
        }

        int totalRows() {
            return (hasExtraRows ? extraCount : 0) + rowCount;
        }

        void preserveState(int rowCount, boolean needsExtraRow, int extraCount) {
            this.rowCount = rowCount;
            this.hasExtraRows = needsExtraRow;
            this.extraCount = extraCount;
        }

        void preserveState(int rowCount) {
            preserveState(rowCount, false, 0);
        }

        void release() {
            preserveState(0);
        }
    }
}
