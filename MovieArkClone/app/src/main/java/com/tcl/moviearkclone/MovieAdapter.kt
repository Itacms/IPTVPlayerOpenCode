package com.tcl.moviearkclone

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MovieAdapter(
    private val items: List<Movie>,
    private val onClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.VH>() {
    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val poster: ImageView = v.findViewById(R.id.posterImage)
        val title: TextView = v.findViewById(R.id.movieTitle)
    }
    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_movie, p, false))
    override fun onBindViewHolder(h: VH, pos: Int) {
        val m = items[pos]
        h.title.text = m.title
        if (m.posterUrl != null) Glide.with(h.poster).load(m.posterUrl).into(h.poster)
        h.itemView.setOnClickListener { onClick(m) }
    }
    override fun getItemCount(): Int = items.size
}
