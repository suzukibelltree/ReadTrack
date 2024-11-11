package com.example.readtrack.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.readtrack.network.BookViewModel
import kotlinx.coroutines.launch

@Composable
fun BookDetail(
    navController: NavController,
    bookViewModel: BookViewModel,
) {
    val bookItem by bookViewModel.book
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            AsyncImage(
                model = bookItem.volumeInfo.imageLinks.thumbnail,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(0.4f).padding(16.dp)
            )
            Column {
                Text(
                    text = bookItem.volumeInfo.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = bookItem.volumeInfo.authors.toString(),
                )
                Text(
                    text = bookItem.volumeInfo.publishedDate.toString(),
                )
            }
        }
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            //あらすじについてはボタンを押すとすべてを表示するように後で修正する？
            if(bookItem.volumeInfo.description.toString().length>50){
                Text(text = bookItem.volumeInfo.description.toString().substring(0,50)+"...")
            }else{
                Text(text = bookItem.volumeInfo.description.toString())
            }

        }
        //このRowでは本に関する追加情報を表示する
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ジャンル")
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ページ数")
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "出版社")
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = null,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
        Button(
            onClick = {/*TODO*/ },
        ) {
            Text("登録")
        }
    }
}