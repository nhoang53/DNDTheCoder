package edu.orangecoastcollege.cs273.dndthecoder;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DragAndDropActivity extends Activity {

    private Context context;
    private GridLayout dragLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_and_drop);
        context = this;

        dragLayout = (GridLayout) findViewById(R.id.activity_drag_and_drop);
        //TODO:  Add the CoderDragListener to every LinearLayout in activity_drag_and_drop
        // Loop through all 25 LinearLayouts which are children of the GridLayout
        int llChildCount = dragLayout.getChildCount();
        for(int i = 0; i < llChildCount; i++) // in this case we have only 1 child
        {
            View childView = dragLayout.getChildAt(i);
            if(childView instanceof LinearLayout) // check if that cell is a LinearLayout
            {
                LinearLayout childLinearLayout = (LinearLayout) childView;
                // Apply Drag listener
                childLinearLayout.setOnDragListener(new CoderDragListener());

                // imageView
                int ivChildCount = childLinearLayout.getChildCount();
                for(int j = 0; j < ivChildCount; j++) // in this case we have only 1 child
                {
                    View grandchildView = childLinearLayout.getChildAt(j);
                    if(grandchildView instanceof ImageView) // check if that cell is a ImageView
                    {
                        ImageView childImageView = (ImageView) grandchildView;
                        //TODO:  Add the CoderTouchListener to every ImageView within each LinearLayout
                        childImageView.setOnTouchListener(new CoderTouchListener());
                    }
                }
            }
        }

    }

    /**
     * CoderTouchListener implements an OnTouchListener, specifically for ImageViews.
     * It employs a DragShadowBuilder, such that whenever an ImageView is touched,
     * it will be elevated, shadowed and have a visual effect that
     * indicates it's ready to be dragged (and dropped).
     */
    class CoderTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

                // As of Android Nougat (7), startDrag is deprecated, use startDragAndDrop
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, view, 0);
                    view.setTag(null);
                }
                else
                    view.startDrag(data, shadowBuilder, view, 0);

                view.setVisibility(View.INVISIBLE);

                /*if (view.getTag() == null)
                {
                    // Make invisible
                    view.setTag("One click.");
                    view.setVisibility(View.INVISIBLE);
                }
                else
                {
                    // Make visible
                    view.setTag(null);
                    view.setVisibility(View.VISIBLE);
                }*/

                return true;
            }
            return false;
        }
    }

    /**
     * CoderDragListener implements an OnDragListener, specifically for LinearLayouts.
     * It alternates layouts between a normal shape (gradient square with white background)
     * to a target shape (gradient square with red background).
     *
     * The GridLayout must remove the view before it can be added to another view,
     * therefore removeView *must* be called before addView.
     */
    class CoderDragListener implements View.OnDragListener {
        Drawable targetShape = ContextCompat.getDrawable(context, R.drawable.target_shape);
        Drawable normalShape = ContextCompat.getDrawable(context, R.drawable.normal_shape);


        @Override
        public boolean onDrag(View v, DragEvent event)  // v is LinearLayout
        {
            View targetView = (View) event.getLocalState(); // targetView is ImageView
            //ViewGroup targetLinearLayout = (ViewGroup) targetView.getParent();
            ViewGroup targetLinearLayout = (ViewGroup) targetView.getParent();

            LinearLayout destinationLinearLayout = (LinearLayout) v;

            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing, handled by OnTouchListener (ShadowBuilder)
                    break;
                case DragEvent.ACTION_DRAG_ENTERED: // drop position
                    v.setBackground(targetShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackground(normalShape);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to somewhere else in the ViewGroup

                    /*View targetView = (View) event.getLocalState(); // targetView is ImageView
                    //ViewGroup targetLinearLayout = (ViewGroup) targetView.getParent();
                    ViewGroup targetLinearLayout = (ViewGroup) targetView.getParent();

                    LinearLayout destinationLinearLayout = (LinearLayout) v;*/

                    if (destinationLinearLayout.getChildCount() == 0) {

                        targetLinearLayout.removeView(targetView);
                        destinationLinearLayout.addView(targetView);
                        targetView.setVisibility(View.VISIBLE);
                    } else if (destinationLinearLayout.getChildCount() > 0) {
                        View temp = destinationLinearLayout.getChildAt(0);
                        //swap the image
                        targetLinearLayout.removeView(targetView);
                        destinationLinearLayout.addView(targetView);

                        destinationLinearLayout.removeView(temp);
                        targetLinearLayout.addView(temp);

                        targetView.setVisibility(View.VISIBLE);
                        temp.setVisibility(View.VISIBLE);
/*
                        // put the image back
                        targetLinearLayout.removeView(targetView);
                        targetLinearLayout.addView(targetView);
                        targetView.setVisibility(View.VISIBLE);*/
                    }

                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Put image back if the destination is not a LinearLayout
                    targetLinearLayout.removeView(targetView);
                    targetLinearLayout.addView(targetView);
                    targetView.setVisibility(View.VISIBLE);

                    v.setBackground(normalShape);
                default:
                    break;
            }
            return true;
        }
    }


}

