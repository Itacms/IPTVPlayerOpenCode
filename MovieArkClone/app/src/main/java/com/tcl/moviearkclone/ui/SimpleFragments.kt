package com.tcl.moviearkclone.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tcl.moviearkclone.R

class SearchFragment : Fragment() {
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, b: Bundle?): View =
        i.inflate(R.layout.fragment_simple, c, false).apply {
            findViewById<android.widget.TextView>(R.id.simpleText).text = "Buscar"
        }
}
class FavoritesFragment : Fragment() {
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, b: Bundle?): View =
        i.inflate(R.layout.fragment_simple, c, false).apply {
            findViewById<android.widget.TextView>(R.id.simpleText).text = "Favoritos"
        }
}
class SettingsFragment : Fragment() {
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, b: Bundle?): View =
        i.inflate(R.layout.fragment_simple, c, false).apply {
            findViewById<android.widget.TextView>(R.id.simpleText).text = "Configurações"
        }
}
