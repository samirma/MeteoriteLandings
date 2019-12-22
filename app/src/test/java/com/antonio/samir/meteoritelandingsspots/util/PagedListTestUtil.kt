package com.antonio.samir.meteoritelandingsspots.util

import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList


fun <T> List<T>.asPagedList() = LivePagedListBuilder<Int, T>(createMockDataSourceFactory(this),
        PagedList.Config(enablePlaceholders = false,
                prefetchDistance = 24,
                pageSize = if (size == 0) 1 else size))
        .build().getOrAwaitValue()

private fun <T> createMockDataSourceFactory(itemList: List<T>): DataSource.Factory<Int, T> =
        object : DataSource.Factory<Int, T>() {
            override fun create(): DataSource<Int, T> = MockLimitDataSource(itemList)
        }

