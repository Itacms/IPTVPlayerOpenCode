package com.tcl.moviearkclone.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tcl.moviearkclone.Movie
import com.tcl.moviearkclone.MovieAdapter
import com.tcl.moviearkclone.R

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        val recycler = v.findViewById<RecyclerView>(R.id.moviesRecycler)
        recycler.layoutManager = GridLayoutManager(context, 3)
        val sample = listOf(
            Movie("Filme 1"), Movie("Filme 2"), Movie("Filme 3"),
            Movie("Filme 4"), Movie("Filme 5"), Movie("Filme 6")
        )
        recycler.adapter = MovieAdapter(sample) { }
        return v
    }
}
