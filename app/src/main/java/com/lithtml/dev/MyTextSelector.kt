package com.lithtml.dev

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect

@Composable
fun MyTextSelector(
    text:String,
    modifier:Modifier=Modifier,

    selectedTextItems: MutableState<Set<Int>>,
) {


    val state  = rememberLazyGridState()
    val letters by remember {
        derivedStateOf {
            text.mapIndexed { index, char ->
                TextItem(content=char.toString(),id=index)
            }
        }
    }
    LazyVerticalGrid(
        state=state,
        columns = GridCells.Adaptive(40.dp), // 适应性列数，根据大小决定一行多少元素
        modifier = modifier
            .myTextDrag(
                lazyGridState = state,
                selectedTextItems=selectedTextItems.value,
                setSelectTextItems = { selectedTextItems.value = it }
//                setSelectTextItems = setSelectTextItems
            )
        ,

        horizontalArrangement = Arrangement.spacedBy(10.dp), // 列之间的水平间距
        verticalArrangement = Arrangement.spacedBy(10.dp), // 行之间的垂直间距
    ) {
        items(letters,key={it.id}) { item ->
            val isSelected by remember {
                derivedStateOf {
                    item.id in selectedTextItems.value
                }
            }
            Box{
                TextItem(
                    item = item,
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                        .background(
                            if (isSelected) Color.Blue else Color.Green
                        )
                        .clickable {
                            if (isSelected) {
                                selectedTextItems.value -= item.id
                            } else {
                                selectedTextItems.value += item.id

                            }
                        }
                )
            }

        }
    }
}

fun Modifier.myTextDrag(
    lazyGridState:LazyGridState,
    selectedTextItems:Set<Int>,
    setSelectTextItems : (Set<Int>) -> Unit = {}
) = pointerInput(lazyGridState){
    var initialTextItemId: Int? = null
    var currentTextItemId: Int? = null
    val TAG = "MyTextSelector"
    fun textItemIdAtOffset(hitPoint: Offset): Int? =
        lazyGridState.layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.key as? Int



detectDragGesturesAfterLongPress(
    onDragStart = {
        textItemIdAtOffset(it)?.let {id->
            if(!selectedTextItems.contains(id)){
                initialTextItemId = id
                currentTextItemId = id
                setSelectTextItems(selectedTextItems + id)
            }
        }
    },
    onDragCancel = {
            initialTextItemId=null
    },
    onDragEnd = {
        initialTextItemId=null
    },
    onDrag = {change,_->
    if(initialTextItemId == null ) return@detectDragGesturesAfterLongPress
    textItemIdAtOffset(change.position)?.let{ pointerTextItemId->
        if(pointerTextItemId!= currentTextItemId){
            setSelectTextItems(
                selectedTextItems.addOrRemoveUpTo(
                    pointerKey = pointerTextItemId,
                    previousPointerKey = currentTextItemId,
                    initialKey = initialTextItemId
                )
            )
            currentTextItemId = pointerTextItemId
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
    //把之前的都去掉，然后把现在的加上
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
data class TextItem (
    val content:String,
    val id:Int
)
@Composable
fun TextItem(item:TextItem,
             modifier: Modifier= Modifier,
             ) {
    Box(modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Text(text = item.content)
    }
}