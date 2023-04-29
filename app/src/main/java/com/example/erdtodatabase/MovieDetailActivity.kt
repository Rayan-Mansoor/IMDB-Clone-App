package com.example.erdtodatabase

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.erdtodatabase.databinding.ActivityMovieDetailBinding
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*

class MovieDetailActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMovieDetailBinding
    private lateinit var db: AppDB
    private lateinit var reviewList : ArrayList<MovieUser>
    private lateinit var reviewAdapeter : ReviewerListAdapter
    private lateinit var movieData : Movie
    private lateinit var user: String
    private lateinit var directors : String
    private lateinit var writers : String

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        binding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext,AppDB::class.java,"movie-database").build()
        db.openHelper.writableDatabase

        movieData = intent.getSerializableExtra("data") as Movie
       // val genres = intent.getStringExtra("genres")


        directors = ""
        writers = ""

        GlobalScope.launch {
            directors = db.AppDAO().getDirectorByMovie(movieData.title)
            withContext(Dispatchers.Main){
                if(directors.isNotEmpty())
                binding.directorDetail.text = "Directors: "+ directors
            }
        }

        GlobalScope.launch {
            val genres = db.AppDAO().getGenreByMovie(movieData.title)
            withContext(Dispatchers.Main){
                if(genres.isNotEmpty())
                    binding.genreDetail.text = "Genres: "+ genres
            }
        }

        GlobalScope.launch {
            writers = db.AppDAO().getWriterByMovie(movieData.title)
            withContext(Dispatchers.Main){
                if(writers.isNotEmpty())
                binding.writerDetail.text ="Writers: "+ writers
            }
        }


        user = intent.getStringExtra("user")!!



        reviewList = arrayListOf()

        reviewAdapeter = ReviewerListAdapter(reviewList)
        binding.commentrcv.adapter = reviewAdapeter
        binding.commentrcv.layoutManager = LinearLayoutManager(this)


        binding.titleDetail.text = movieData.title

        Picasso.get().load(movieData.image).into(binding.imageDetail)

       // binding.genreDetail.text = genres





        binding.discriptionDetail.text = movieData.description

        binding.yearDetail.text = "Year: "+ movieData.year.toString()


        binding.reviewBtn.setOnClickListener {
            popup(it)
        }

//        binding.newComment.inputType = InputType.TYPE_CLASS_TEXT
//        binding.newComment.setOnEditorActionListener { _, actionId, _ ->
//            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                UpdateMovieUser(FMID = movieData.title, FUID = user, binding.newComment.text.toString())
//                true
//            } else {
//                false
//            }
//        }



        GlobalScope.launch {
            reviewList = db.AppDAO().getReviews(movieData.title) as ArrayList<MovieUser>
            reviewAdapeter.setMovie(reviewList)

            withContext(Dispatchers.Main){
                reviewAdapeter.notifyDataSetChanged()
            }

        }

    }

    private fun UpdateMovieUser(FMID: String, FUID: String, comment: String? = null, selfRating: Double? = null, likes: Boolean? = null){
        Log.d("MovieDetailActivity","UpdateMovieUser called")

        val updatedReview = MovieUser(FMID,FUID,comment,selfRating,likes)
        GlobalScope.launch {
            db.AppDAO().updateOrInsertMU(updatedReview)
            withContext(Dispatchers.Main){
                Log.d("MovieDetailActivity","notify dataset changed called inside Dispatchers.Main")
                val all_reviews = db.AppDAO().getReviews(movieData.title) as ArrayList<MovieUser>
                reviewAdapeter.movieReviews.clear()
                reviewAdapeter.setMovie(all_reviews)
                reviewAdapeter.notifyDataSetChanged()
            }
        }

    }

    private fun popup(view: View){
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.custom_dialog, null)
        dialogBuilder.setView(dialogView)

        val newComment = dialogView.findViewById<EditText>(R.id.addMovDirec)
        val newrating = dialogView.findViewById<RatingBar>(R.id.ratingrv)
        val likeButton = dialogView.findViewById<ImageView>(R.id.tuprv)
        val dislikeButton = dialogView.findViewById<ImageView>(R.id.Tdownrv)


        var review : List<MovieUser>
        var like : Boolean? = null

//        GlobalScope.launch {
//            review = db.AppDAO().getReviews(movieData.title)
//            like = review[0].likes
//        }


        likeButton.setOnClickListener {
            like = true
            likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_alt_24)
            dislikeButton.setImageResource(R.drawable.ic_baseline_thumb_down_off_alt_24)
        }

        dislikeButton.setOnClickListener {
            like = false
            dislikeButton.setImageResource(R.drawable.ic_baseline_thumb_down_alt_24)
            likeButton.setImageResource(R.drawable.ic_baseline_thumb_up_off_alt_24)
        }

        dialogBuilder.setPositiveButton("ok"){ _,_ ->
            UpdateMovieUser(FMID = movieData.title, FUID = user, likes = like, comment = newComment.text.toString(), selfRating =  newrating.rating.toDouble())
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

    }

    override fun onResume() {
        super.onResume()
        reviewAdapeter.notifyDataSetChanged()
    }
}