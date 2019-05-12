[![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14)
# Simple-SpeedView-Android
Simple speedView widget for android
<img src="https://github.com/r3za13/Simple-SpeedView-Android/blob/master/sample.gif" width="400">
### Adding widget:
```
<com.r3za.simplespeedview.SimpleSpeedView
        android:id="@+id/simpleSpeedView"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:has_start_animation="true"
        app:indicator_circle_radius="18dp"
        app:part_margin="0.5dp"
        app:parts_count="5"
        app:parts_width="46dp"
        app:progress="0" />
        ```
### Attributes:
```
parts_count
part_margin
parts_width
progress
total_progress
indicator_circle_radius
has_start_animation
indicator_color
```

### Setting progress:
```
simpleSpeedView.setViewProgress(myProgress)
```
