package com.jin10.lgd.myocredemo;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.animation.LinearInterpolator;

import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.QuaternionEvaluator;
import com.google.ar.sceneform.math.Vector3;

/**
 * Created by caisongliang on 2019/8/1 10:34
 */
public class RotatingNode extends Node {

    private ObjectAnimator rotationAnimation = null;

    private float degreesPerSecond = 90.0f;

    private float lastSpeedMultiplier = 1.0f;

    private Float speedMultiplier = 1.0f;

    private Long animationDuration = (long) ((1000 * 360) / (degreesPerSecond * speedMultiplier));

    // 重载方法节点激活时调用

    @Override

    public void onActivate() {

        super.onActivate();

        startAnimation();

    }

    // 重载方法，节点取消激活状态时调用

    @Override

    public void onDeactivate() {

        super.onDeactivate();

        stopAnimation();

    }

    // ARCore 每一处理帧都会调用一次

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override

    public void onUpdate(FrameTime frameTime) {

        super.onUpdate(frameTime);

        if (rotationAnimation == null) {

            return;

        }

        Float speedMultiplier = this.speedMultiplier;

        // 如果速度没变就继续以之前速度运行.

        if (lastSpeedMultiplier == speedMultiplier) {

            return;

        }

        if (speedMultiplier == 0.0f) {

            // 转速为0则停止旋转

            rotationAnimation.pause();

        } else {

            // 速度改变重新运行属性动画

            rotationAnimation.resume();

            float animatedFraction = rotationAnimation.getAnimatedFraction();

            rotationAnimation.setDuration(animationDuration);

            rotationAnimation.setCurrentFraction(animatedFraction);

        }

        lastSpeedMultiplier = speedMultiplier;

    }

    // 设置速度

    void setDegreesPerSecond(Float degreesPerSecond) {

        this.degreesPerSecond = degreesPerSecond;

    }

    // 启动动画

    private void startAnimation() {

        if (rotationAnimation != null) {

            return;

        }

        rotationAnimation = createAnimator();

        rotationAnimation.setTarget(this);

        rotationAnimation.setDuration(animationDuration);

        rotationAnimation.start();

    }

    // 停止动画

    private void stopAnimation() {

        if (rotationAnimation == null) {

            return;

        }

        rotationAnimation.cancel();

        rotationAnimation = null;

    }

    // 返回一个 ObjectAnimator 用来使节点旋转起来

    private ObjectAnimator createAnimator() {

        // 节点的位置和角度信息设置通过Quaternion来设置

        // 创建4个Quaternion 来设置四个关键位置

        Quaternion orientation1 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 0f);

        Quaternion orientation2 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 120f);

        Quaternion orientation3 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 240f);

        Quaternion orientation4 = Quaternion.axisAngle(new Vector3(0.0f, 1.0f, 0.0f), 360f);

        ObjectAnimator rotationAnimation = new ObjectAnimator();

        rotationAnimation.setObjectValues(orientation1, orientation2, orientation3, orientation4);

        // 设置属性动画修改的属性为localRotation

        rotationAnimation.setPropertyName("localRotation");

        // 使用Sceneform 框架提供的估值器 QuaternionEvaluator 作为属性动画估值器

        rotationAnimation.setEvaluator(new QuaternionEvaluator());

        //  设置动画重复无限次播放。

        rotationAnimation.setRepeatCount(ObjectAnimator.INFINITE);

        rotationAnimation.setRepeatMode(ObjectAnimator.RESTART);

        rotationAnimation.setInterpolator(new LinearInterpolator());

        rotationAnimation.setAutoCancel(true);

        return rotationAnimation;

    }
}