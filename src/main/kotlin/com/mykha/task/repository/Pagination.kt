package com.mykha.task.repository

data class Paged<T>(
    val values: List<T>,
    val maxResults: Long,
    val startAt: Long,
    val total: Long
)

data class PagedRequest(
    val limit: Long,
    val offset: Long
)
