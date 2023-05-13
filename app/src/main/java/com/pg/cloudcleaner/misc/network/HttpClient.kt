package com.pg.cloudcleaner.misc.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

var API_BASE_URL = "https://www.googleapis.com"

private var httpClient = OkHttpClient.Builder().addInterceptor(
    HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
)

private var builder = Retrofit.Builder()
    .baseUrl(API_BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())

var retrofit: Retrofit = builder
    .client(
        httpClient.build()
    )
    .build()
