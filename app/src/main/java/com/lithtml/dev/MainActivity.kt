package com.lithtml.dev

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import com.lithtml.dev.network.MyRepository
import com.lithtml.dev.ui.theme.ComposeDevTheme
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter
import com.lithtml.dev.data.Photo
import com.lithtml.dev.data.photos

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeDevTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

//                    Greeting("not baddd")
//                    Gesture()
//                    GestureOutSide()


//                    var activeId by rememberSaveable {
//                        mutableStateOf<Int?>(null)
//                    }
//                    val gridState = rememberLazyGridState()
//                    PhotoGrid(photos = photos,
//                        state=gridState,
//                        navigateToPhoto = {activeId =it }
//                        )
//                    if(activeId !=null){
//                        FullScreenPhoto(photo = photos.first{it.id==activeId}, onDismiss = { activeId=null })
//                    }
                    val text = "你好我是随机的字符串是的嗯呢真的是很不错呢饿呢啊供了关于其父容器约束的呵有"
                    var selectedTextItems = remember {
                        mutableStateOf(emptySet<Int>())
                    }



                    Box( contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxWidth().clickable {
                            selectedTextItems.value = emptySet()
                        },

                    ){
                        MyTextSelector(text = text,

                            selectedTextItems = selectedTextItems,

                        )
                    }


                }
            }
        }
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoGrid(
    photos:List<Photo>,
    modifier: Modifier=Modifier,
    state: LazyGridState,
    navigateToPhoto:(Int)->Unit = {}
) {
    var selectedIds by rememberSaveable {
        mutableStateOf(emptySet<Int>())
    }
    val inSelectionMode by remember {
        derivedStateOf { selectedIds.isNotEmpty() }
    }


    LazyVerticalGrid(GridCells.Adaptive(128.dp),
        state=state,

        modifier = modifier.photoGridDragHandler(
            lazyGridState = state,
            selectedIds = { selectedIds },
            setSelectedIds = { selectedIds = it },

            autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() }
        )
        ) {
        items(photos,key={it.id}) { photo ->
            val selected by remember {
                derivedStateOf {
                    photo.id in selectedIds
                }
            }
            PhotoItem(photo = photo,
                inSelectionMode = inSelectionMode,
                selected = selected,

                modifier = if(inSelectionMode){
                    Modifier.clickable {
                            if(selected){
                                selectedIds -= photo.id
                            }else{
                                selectedIds += photo.id
                            }
                        }
                    }else{
                    Modifier.combinedClickable (
                        onClick= {
                            navigateToPhoto(photo.id)
                        },
                        onLongClick = {
                            selectedIds += photo.id
                        }
                    )
                }
            )
        }
    }
}
fun Modifier.photoGridDragHandler(
    lazyGridState: LazyGridState,
    selectedIds: () -> Set<Int>,
    autoScrollThreshold: Float,
    setSelectedIds: (Set<Int>) -> Unit = { },
    setAutoScrollSpeed: (Float) -> Unit = { },
) = pointerInput(autoScrollThreshold, setSelectedIds, setAutoScrollSpeed) {
    fun photoIdAtOffset(hitPoint: Offset): Int? =
        lazyGridState.layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.key as? Int

    var initialPhotoId: Int? = null
    var currentPhotoId: Int? = null
    detectDragGesturesAfterLongPress(
        onDragStart = { offset ->
            photoIdAtOffset(offset)?.let { key ->
                if (!selectedIds().contains(key)) {
                    initialPhotoId = key
                    currentPhotoId = key
                    setSelectedIds(selectedIds() + key)
                }
            }
        },
        onDragCancel = { setAutoScrollSpeed(0f); initialPhotoId = null },
        onDragEnd = { setAutoScrollSpeed(0f); initialPhotoId = null },
        onDrag = { change, _ ->
            if (initialPhotoId != null) {
                val distFromBottom =
                    lazyGridState.layoutInfo.viewportSize.height - change.position.y
                val distFromTop = change.position.y
                setAutoScrollSpeed(
                    when {
                        distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                        distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop)
                        else -> 0f
                    }
                )

                photoIdAtOffset(change.position)?.let { pointerPhotoId ->
                    if (currentPhotoId != pointerPhotoId) {
                        setSelectedIds(
                            selectedIds().addOrRemoveUpTo(pointerPhotoId, currentPhotoId, initialPhotoId)
                        )
                        currentPhotoId = pointerPhotoId
                    }
                }
            }
        }
    )
}
private fun Set<Int>.addOrRemoveUpTo(
    pointerKey: Int?,
    previousPointerKey: Int?,
    initialKey: Int?
): Set<Int> {
    return if (pointerKey == null || previousPointerKey == null || initialKey == null) {
        this
    } else {
        this
            .minus(initialKey..previousPointerKey)
            .minus(previousPointerKey..initialKey)
            .plus(initialKey..pointerKey)
            .plus(pointerKey..initialKey)
    }
}

@Composable
fun PhotoItem(
    inSelectionMode:Boolean,
    selected:Boolean,
    photo:Photo,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier.aspectRatio(1f),
        tonalElevation = 3.dp
        ) {
        Box{
            Image(painter = rememberAsyncImagePainter(photo.url),  contentDescription = null,)
            if (inSelectionMode) {
                if (selected) {
                    val bgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                    Icon(
                        Icons.Filled.CheckCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .border(2.dp, bgColor, CircleShape)
                            .clip(CircleShape)
                            .background(bgColor)
                    )
                } else {
                    Icon(
                        Icons.Filled.AddCircle,
                        tint = Color.White.copy(alpha = 0.7f),
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }

    }
}
@Composable
private fun FullScreenPhoto(
    photo: Photo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scrim(onDismiss, Modifier.fillMaxSize())
        PhotoImage(photo)
    }
}

@Composable
private fun Scrim(onClose: () -> Unit, modifier: Modifier = Modifier) {
    val strClose = "关闭"
    Box(
        modifier
            .fillMaxSize()
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            .semantics {
                onClick(strClose) { onClose(); true }
            }
            .focusable()
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onClose(); true
                } else {
                    false
                }
            }
            .background(Color.DarkGray.copy(alpha = 0.75f))
    )
}
@Composable
fun PhotoImage(photo: Photo, modifier: Modifier = Modifier) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var zoom by remember { mutableFloatStateOf(1f) }


    Image(
        painter = rememberAsyncImagePainter(model = photo.highResUrl),
        contentDescription = photo.contentDescription,
        modifier
            .clipToBounds()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        zoom = if (zoom > 1f) 1f else 2f
                        offset = calculateDoubleTapOffset(zoom, size, tapOffset)
                    }
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures(
                    onGesture = { centroid, pan, gestureZoom, _ ->
                        offset = offset.calculateNewOffset(
                            centroid, pan, zoom, gestureZoom, size
                        )
                        zoom = maxOf(1f, zoom * gestureZoom)
                    }
                )
            }
            .graphicsLayer {
                translationX = -offset.x * zoom
                translationY = -offset.y * zoom
                scaleX = zoom; scaleY = zoom
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .aspectRatio(1f)
    )
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val TAG = "test"
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
        Button(onClick = {
            scope.launch {
                val data = MyRepository().fetchSomeData()
                Log.d(TAG, "Greeting: $data")
                Log.d(TAG, "Greeting: ${data.history[0]}")
            }


        }) {
            Text(text = "点我就完事了")
        }
    }

}

@Composable
fun Gesture() {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val TAG = "Guest"
    Box(
        Modifier
            .graphicsLayer {
                scaleX = scale;
                scaleY = scale;
                translationX = offset.x;
                translationY = offset.y;
                rotationZ = rotation
            }
            .transformable(state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->

                scale *= zoomChange
                rotation += rotationChange
                offset += offsetChange

            })
            .background(Color.Blue)
            .fillMaxSize()
    )

}

@Composable
fun GestureOutSide() {
    var scale by remember { mutableStateOf(1f) }
    var rotation by remember { mutableStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val TAG = "Guest"
    Box(
        Modifier

            .transformable(state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->

                scale *= zoomChange
                rotation += rotationChange
                offset += offsetChange

            })
            .background(Color.Blue)
            .fillMaxSize()
    ){
        Box(modifier = Modifier
            .graphicsLayer {
                scaleX = scale;
                scaleY = scale;
                translationX = offset.x;
                translationY = offset.y;
                rotationZ = rotation
            }
            .width(200.dp)
            .height(200.dp)
            .background(Color.Green))
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ComposeDevTheme {
        Greeting("Android")
    }
}

fun calculateDoubleTapOffset(
    zoom: Float,
    size: IntSize,
    tapOffset: Offset
): Offset {
    val newOffset = Offset(tapOffset.x, tapOffset.y)
    return Offset(
        newOffset.x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
        newOffset.y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
    )
}

fun Offset.calculateNewOffset(
    centroid: Offset,
    pan: Offset,
    zoom: Float,
    gestureZoom: Float,
    size: IntSize
): Offset {
    val newScale = maxOf(1f, zoom * gestureZoom)
    val newOffset = (this + centroid / zoom) -
            (centroid / newScale + pan / zoom)
    return Offset(
        newOffset.x.coerceIn(0f, (size.width / zoom) * (zoom - 1f)),
        newOffset.y.coerceIn(0f, (size.height / zoom) * (zoom - 1f))
    )
}