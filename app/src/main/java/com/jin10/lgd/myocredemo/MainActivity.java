package com.jin10.lgd.myocredemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.model.GradientColor;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.Trackable;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.jin10.lgd.chart.listener.BarChartGestureListener;
import com.jin10.lgd.chart.view.BarChart;
import com.jin10.lgd.chart.view.DoughnutChart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity implements BarChartGestureListener.OnEdgeListener{

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final double MIN_OPENGL_VERSION = 3.0;

    private ArFragment fragment;
    private ModelRenderable andyRenderable;
    private ModelRenderable andyRenderable2;
    private ModelRenderable andyRenderable3;
    private ViewRenderable mViewRenderable;
    private BarChart chart;
    private DoughnutChart doughnutChart;
    private BarChartGestureListener bcGesture;
    final ArrayList<BarEntry> values = new ArrayList<>();
    private int type = 1;


    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkIsSupportedDeviceOrFinish(this)) {
            return;
        }
        //初始化柱状图的数据
        for (int i = 0; i < 80; i++) {
            float multi = (100 + 1);
            float val = (float) (Math.random() * multi) + multi / 3;
            values.add(new BarEntry(i, val,""+i+" "));
        }
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(v -> {
           type = 1;
        });
        findViewById(R.id.btn2).setOnClickListener(v -> {
            type = 2;
        });
        findViewById(R.id.btn3).setOnClickListener(v -> {
            type = 3;
        });
        findViewById(R.id.btn4).setOnClickListener(v -> {
            type = 4;
        });
        fragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.ux_fragment);


        //findViewById(R.id.tv).setOnClickListener(v -> addObject(Uri.parse("mosaic_ball.sfb")));
        //arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ux_fragment);
        // When you build a Renderable, Sceneform loads its resources in the background while returning
        // a CompletableFuture. Call thenAccept(), handle(), or check isDone() before calling get().
        ModelRenderable.builder()
                .setSource(this, R.raw.mosaic_ball)
                .build()
                .thenAccept(renderable -> andyRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, R.raw.model)
                .build()
                .thenAccept(renderable -> andyRenderable2 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        ModelRenderable.builder()
                .setSource(this, R.raw.pink_dread)
                .build()
                .thenAccept(renderable -> andyRenderable3 = renderable)
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_chart,null);
        Log.e("View",        view.getMeasuredWidth()+"");
        ViewRenderable.builder()
                .setView(MainActivity.this,view)
                .build()
                .thenAccept(new Consumer<ViewRenderable>() {
                    @Override
                    public void accept(ViewRenderable viewRenderable) {
                        mViewRenderable = viewRenderable;
                        chart = mViewRenderable.getView().findViewById(R.id.bar_chart);
                        //doughnutChart = mViewRenderable.getView().findViewById(R.id.chart_dou);
                    }
                })
                .exceptionally(
                        throwable -> {
                            Toast toast =
                                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return null;
                        });

        fragment.setOnTapArPlaneListener(
                (HitResult hitResult, Plane plane, MotionEvent motionEvent) -> {

                    if (mViewRenderable == null) {
                        return;
                    }
                     new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(3000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        initChart();
                                        /*Glide.with(MainActivity.this)
                                                .load(R.drawable.test)
                                                .into((ImageView) mViewRenderable.getView().findViewById(R.id.UI_image));*/
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                    // Create the Anchor.
                    /*Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    RotatingNode rotatingNode = new RotatingNode();
                    anchorNode.setParent(fragment.getArSceneView().getScene());
                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(fragment.getTransformationSystem());

                    rotatingNode.setRenderable(mViewRenderable);
                    rotatingNode.addChild(andy);
                    rotatingNode.setParent(anchorNode);
                    rotatingNode.setEnabled(true);

                    fragment.getArSceneView().getScene().addChild(anchorNode);
                    andy.select();*/
                    Anchor anchor = hitResult.createAnchor();
                    AnchorNode anchorNode = new AnchorNode(anchor);
                    anchorNode.setParent(fragment.getArSceneView().getScene());

                    // Create the transformable andy and add it to the anchor.
                    TransformableNode andy = new TransformableNode(fragment.getTransformationSystem());
                    andy.setParent(anchorNode);
                    if (type == 1){
                        andy.setRenderable(mViewRenderable);
                    }else if (type == 2){
                        andy.setRenderable(andyRenderable);
                    }else if (type == 3){
                        andy.setRenderable(andyRenderable2);
                    }else if (type == 4){
                        andy.setRenderable(andyRenderable3);
                    }
                    andy.select();
                });
    }

    public void initChart(){
        chart.showDrawValues(true);
        chart.setDrawMarkers(false);
        /**
         * 设置一页显示多少条数据
         */
        chart.setRangeNumber(20);

        chart.setRightData(values);
        bcGesture = new BarChartGestureListener(MainActivity.this,this, chart);
        chart.setOnChartGestureListener(bcGesture);

        //初始化圆饼图的颜色，根据数据的条数来定
        /*List<GradientColor> gradientColors = new ArrayList<>();
        int startColor1 = ContextCompat.getColor(this, com.jin10.lgd.chart.R.color.chart_text_red_start);
        int endColor1 = ContextCompat.getColor(this, com.jin10.lgd.chart.R.color.chart_text_red_end);
        int startColor2 = ContextCompat.getColor(this, com.jin10.lgd.chart.R.color.chart_text_green_start);
        int endColor2 = ContextCompat.getColor(this, com.jin10.lgd.chart.R.color.chart_text_green_end);
        gradientColors.add(new GradientColor(startColor2,endColor2));
        gradientColors.add(new GradientColor(startColor1,endColor1));
        doughnutChart.setCenterTextColor(ContextCompat.getColor(this, com.jin10.lgd.chart.R.color.chart_white));
        doughnutChart.setDouhnutGradientColor(gradientColors);
        //初始化圆饼图数据
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) 40,
                "",
                getResources().getDrawable(com.jin10.lgd.chart.R.color.chart_text_green)));
        entries.add(new PieEntry((float) 60,
                "",
                getResources().getDrawable(com.jin10.lgd.chart.R.color.chart_text_green)));
        doughnutChart.setCenterText(generateCenterSpannableText());
        doughnutChart.setDoughnutData(entries);*/
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("60%\n做多");
        s.setSpan(new RelativeSizeSpan(2.0f), 0, 3, 0);
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0,3, 0);
        /*s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
        s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);*/
        return s;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void placeObject(ArFragment fragment, Anchor createAnchor, Uri model) {

        ModelRenderable.builder()

                .setSource(fragment.getContext(), model)

                .build()

                .thenAccept(modelRenderable -> addNodeToScene(fragment,createAnchor, modelRenderable))

                .exceptionally(throwable -> {

                    AlertDialog.Builder builder =new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage(throwable.getMessage())

                            .setTitle("error!");

                    AlertDialog dialog = builder.create();

                    dialog.show();

                    return null;

                }) ;

    }


    private void addNodeToScene( ArFragment fragment, Anchor createAnchor, ModelRenderable renderable) {

        AnchorNode anchorNode =new AnchorNode(createAnchor);
        RotatingNode rotatingNode = new RotatingNode();

        TransformableNode transformableNode =new TransformableNode(fragment.getTransformationSystem());

        /*transformableNode.setRenderable(renderable);

        transformableNode.setParent(anchorNode);*/

        rotatingNode.setRenderable(renderable);

        rotatingNode.addChild(transformableNode);

        rotatingNode.setParent(anchorNode);

        fragment.getArSceneView().getScene().addChild(anchorNode);

        transformableNode.select();


    }

    private android.graphics.Point getScreenCenter() {

        View vw = findViewById(R.id.ux_fragment);

        return new Point(vw.getWidth() /2, vw.getHeight() /2);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void addObject(Uri parse) {

        Frame frame =fragment.getArSceneView().getArFrame();

        android.graphics.Point point = getScreenCenter();

        if (frame !=null) {

            List<HitResult> hits = frame.hitTest(point.x, point.y);

            for (HitResult hit : hits) {

                Trackable trackable = hit.getTrackable();

                if (trackable instanceof Plane && ((Plane)trackable).isPoseInPolygon(hit.getHitPose())) {

                    placeObject(fragment, hit.createAnchor(), parse);

                    break;

                }

            }

        }

    }

    /**
     * Returns false and displays an error message if Sceneform can not run, true if Sceneform can run
     * on this device.
     *
     * <p>Sceneform requires Android N on the device as well as OpenGL 3.0 capabilities.
     *
     * <p>Finishes the activity if Sceneform can not run
     */
    public static boolean checkIsSupportedDeviceOrFinish(final Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later");
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show();
            activity.finish();
            return false;
        }
        String openGlVersionString =
                ((ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE))
                        .getDeviceConfigurationInfo()
                        .getGlEsVersion();
        if (Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later");
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show();
            activity.finish();
            return false;
        }
        return true;
    }

    @Override
    public void edgeLoadLeft(float x) {

    }

    @Override
    public void edgeLoadRight(float x) {

    }
}
