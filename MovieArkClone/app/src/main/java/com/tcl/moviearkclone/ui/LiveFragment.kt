package com.tcl.moviearkclone.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tcl.moviearkclone.ChannelAdapter
import com.tcl.moviearkclone.M3uParser
import com.tcl.moviearkclone.PlayerActivity
import com.tcl.moviearkclone.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class LiveFragment : Fragment() {
    private val entries = mutableListOf<com.tcl.moviearkclone.M3uEntry>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val v = inflater.inflate(R.layout.fragment_live, container, false)
        val urlInput = v.findViewById<EditText>(R.id.urlInput)
        val loadButton = v.findViewById<Button>(R.id.loadButton)
        val statusText = v.findViewById<TextView>(R.id.statusText)
        val recyclerView = v.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        loadButton.setOnClickListener {
            val input = urlInput.text.toString().trim()
            if (input.isEmpty()) { statusText.text = "Informe uma URL"; return@setOnClickListener }
            statusText.text = "Carregando..."
            lifecycleScope.launch {
                try {
                    val text = withContext(Dispatchers.IO) {
                        if (input.startsWith("http://") || input.startsWith("https://")) URL(input).readText()
                        else java.io.File(input).readText()
                    }
                    val parsed = M3uParser.parse(text)
                    entries.clear(); entries.addAll(parsed)
                    recyclerView.adapter = ChannelAdapter(entries) { entry ->
                        val intent = Intent(requireContext(), PlayerActivity::class.java)
                        intent.putExtra("url", entry.url)
                        intent.putExtra("title", entry.title)
                        startActivity(intent)
                    }
                    statusText.text = "Carregados ${entries.size} canais"
                } catch (e: Exception) {
                    statusText.text = "Erro: ${e.message}"
                }
            }
        }
        return v
    }
}
