package com.memes.app

import android.content.Intent
import android.graphics.drawable.Drawable
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.memes.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentMemeLink: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadMemes()
        binding.apply {
            btnNext.setOnClickListener { showNextMeme() }
            btnShare.setOnClickListener { shareMeme() }
        }
    }

    private fun loadMemes() {
        binding.apply {
            pbLoading.visibility = View.VISIBLE
            btnNext.isEnabled = false
            btnShare.isEnabled = false
        }
        // val url =  "https://meme-api.com/gimme"
        val url = "https://meme-api.com/gimme/wholesomememes"

        // Request a string response from the provided url
        val jsonRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                val memeLink = response.getString("url")
                currentMemeLink = memeLink
                Glide.with(this).load(memeLink)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean,
                        ): Boolean {
                            binding.apply {
                                pbLoading.visibility = View.GONE
                                btnNext.isEnabled = true
                                btnShare.isEnabled = false
                            }
                            Toast.makeText(this@MainActivity,
                                "Something went wrong!",
                                Toast.LENGTH_SHORT)
                                .show()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean,
                        ): Boolean {
                            binding.apply {
                                pbLoading.visibility = View.GONE
                                btnNext.isEnabled = true
                                btnShare.isEnabled = true
                            }
                            return false
                        }
                    }).into(binding.ivMeme)
            },
            { error ->
                Toast.makeText(this, "Something went wrong! ${error.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        )

        // Add the request to request queue
        MySingleton.getInstance(this).addToRequestQueue(jsonRequest)
    }

    private fun shareMeme() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT,
            "Hey, checkout this meme from reddit!\n $currentMemeLink")
        intent.type = "text/plain"
        val chooser = Intent.createChooser(intent, "Share this meme using...")
        startActivity(chooser)
    }

    private fun showNextMeme() {
        loadMemes()
    }
}