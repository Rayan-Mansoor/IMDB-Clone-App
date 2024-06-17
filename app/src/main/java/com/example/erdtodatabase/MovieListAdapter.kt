package com.example.erdtodatabase

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MovieListAdapter(private var movie: List<Movie>,private var db : AppDB, private var user: String?) : RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>() {

    class MovieViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.titleview)
        val description = itemView.findViewById<TextView>(R.id.descriptionView)
        val image = itemView.findViewById<ImageView>(R.id.thumbnail)
        val genre = itemView.findViewById<TextView>(R.id.genreView)
        val Tup = itemView.findViewById<ImageView>(R.id.thumbsUp)
        val Tdown = itemView.findViewById<ImageView>(R.id.thumbsDown)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        Log.d("MovieListAdapter","on create called")
        return MovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_item,parent,false))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
            Log.d("MovieListAdapter","on bind called")
            holder.title.text = movie[position].title
            holder.description.text = movie[position].description

        GlobalScope.launch(Dispatchers.Main) {
            holder.genre.text = db.AppDAO().getGenreByMovie(movie[position].title)
        }

            GlobalScope.launch {
                Log.d("MovieListAdapter", user!! )
                if (db.AppDAO().getlike_dislike(movie[position].title,user!!) != null){
                    if (db.AppDAO().getlike_dislike(movie[position].title,user!!) == true){
                        withContext(Dispatchers.Main){
                            holder.Tup.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24)
                        }

                    }
                    else if (db.AppDAO().getlike_dislike(movie[position].title,user!!) == false){
                        withContext(Dispatchers.Main){
                            holder.Tdown.setImageResource(R.drawable.ic_baseline_thumb_down_alt_24)
                        }

                    }
                }
            }


            if (movie[position].image != null){
            Picasso.get().load(movie[position].image).into(holder.image)
            }


            holder.Tup.setOnClickListener {

                GlobalScope.launch(Dispatchers.Main){
                    db.AppDAO().updateOrInsert(user!!,movie[position].title,true)
                    holder.Tup.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24)
                    holder.Tdown.setImageResource(R.drawable.ic_baseline_thumb_down_off_alt_24)
                }
            }

            holder.Tdown.setOnClickListener {

                GlobalScope.launch(Dispatchers.Main){
                    db.AppDAO().updateOrInsert(user!!,movie[position].title,false)
                    holder.Tdown.setImageResource(R.drawable.ic_baseline_thumb_down_alt_24)
                    holder.Tup.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24)
                }
            }

            holder.itemView.setOnClickListener{
                val intent = Intent(it.context,MovieDetailActivity::class.java)
                intent.putExtra("data",movie[position])
                intent.putExtra("genres",holder.genre.text)
                intent.putExtra("user",user!!)
                it.context.startActivity(intent)
            }

        holder.title.setOnClickListener{
            val intent = Intent(it.context,MovieDetailActivity::class.java)
            intent.putExtra("data",movie[position])
            intent.putExtra("genres",holder.genre.text)
            intent.putExtra("user",user!!)
            it.context.startActivity(intent)
        }

        holder.description.setOnClickListener{
            val intent = Intent(it.context,MovieDetailActivity::class.java)
            intent.putExtra("data",movie[position])
            intent.putExtra("genres",holder.genre.text)
            intent.putExtra("user",user!!)
            it.context.startActivity(intent)
        }


        }

    override fun getItemCount(): Int {
        Log.d("MovieListAdapter","on count called")
        return movie.size
    }

    fun setMovies(movies: List<Movie>) {
        this.movie = movies
    }

    fun setUser(user : String) {
        this.user = user
    }
}