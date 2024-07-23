# YJS Android

Yjs Android is a real-time collaboration framework for Android. It is based on [Yjs](https://github.com/yjs/yjs)
and [quickjs-android](https://github.com/seven332/quickjs-android)


## build.gradle Configuration
```groovy
// project build
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation "com.github.netless-io:yjs-android:0.1.0-alpha01"
}
```
