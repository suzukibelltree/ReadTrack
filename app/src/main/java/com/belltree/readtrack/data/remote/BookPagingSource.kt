package com.belltree.readtrack.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.belltree.readtrack.domain.model.BookItem

/**
 * Google Books APIを利用して書籍情報を取得するためのPagingSource
 * @param bookApiService Google Books APIを利用するためのインターフェース
 * @param query 検索クエリ
 * @param apiKey APIキー
 */
class BookPagingSource(
    private val bookApiService: GoogleBooksApiService,
    private val query: String,
    private val apiKey: String,
) : PagingSource<Int, BookItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BookItem> {
        return try {
            val page = params.key ?: 0
            val limit = params.loadSize
            val startIndex = page * limit
            val response = bookApiService.searchBooks(
                query = query,
                apiKey = apiKey,
                startIndex = startIndex,
                maxResults = limit
            )
            val items = response.items ?: emptyList()

            LoadResult.Page(
                data = items,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (items.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, BookItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}