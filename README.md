# NoiseOut

## Overview
NoiseOut is an optimized version of Facebook AI's denoiser [here](https://github.com/facebookresearch/denoiser), which is deployed on labtop CPU for real-time speech enhancement and noise filtering. We focused on improving storage size of the model to enable mobile speech enhancement.

## Build the demo with Android Studio

### Prerequisites

*   The **[Android Studio](https://developer.android.com/studio/index.html)**
    IDE. This sample has been tested on Android Studio Bumblebee.

*   A physical Android device with a minimum OS version of SDK 23 (Android 6.0)
    with developer mode enabled. The process of enabling developer mode may vary
    by device.

### Building

*   Open Android Studio. From the Welcome screen, select Open an existing
    Android Studio project.

*   From the Open File or Project window that appears, navigate to and select
    the tensorflow-lite/examples/audio_classification/android
    directory. Click OK.

*   If it asks you to do a Gradle Sync, click OK.

*   With your Android device connected to your computer and developer mode
    enabled, click on the green Run arrow in Android Studio.

### Models used

Downloading, extraction, and placing the models into the assets folder is
managed automatically by the download_model.gradle file.
