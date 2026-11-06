package com.tcl.moviearkclone

data class Movie(
    val title: String,
    val posterUrl: String? = null,
    val streamUrl: String? = null,
    val category: String = "Filmes"
)
