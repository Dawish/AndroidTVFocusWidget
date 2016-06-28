[![Build Status](https://travis-ci.org/luoqii/android_demo_focus_layer.png?branch=master)](https://travis-ci.org/luoqii/android_demo_focus_layer)

android_demo_focus_layer
========================


原理
========================
当焦点发生变化时，从最新得到焦点的View获取一个在新高宽尺寸下的Bitmap，
然后使用该Bitmap实现动画，动画实现分类

1.  复用Android原生View系统动画框架 （帧率没办法保证，需要做全局优化）
     1.1 Animation
     1.2 Animator
2. 使用自定义的SurfaceView，自己绘制动画 （保证帖率，实现复杂度高）

FEATURE
========================
1. 适用性较好，较容易与现有View层次结合，实现一致效果；
2. 如果动画过程中Bitmap需要频烦更新（如TextView的marquee效果），这个方案
可能不适用

how to build
========================
android_common_lib.jar come from [android common lib](https://github.com/luoqii/android_common_lib).

