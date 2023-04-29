package com.example.erdtodatabase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ReviewerListAdapter(var movieReviews: ArrayList<MovieUser>) : RecyclerView.Adapter<ReviewerListAdapter.ReviewerViewHolder>() {

    class ReviewerViewHolder(itemview : View) : RecyclerView.ViewHolder(itemview){
        val name = itemview.findViewById<TextView>(R.id.commentor)
        val comment = itemview.findViewById<TextView>(R.id.Comment_full)
        val rating = itemview.findViewById<RatingBar>(R.id.rating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewerViewHolder {
        return ReviewerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.review_item,parent,false))
    }

    override fun onBindViewHolder(holder: ReviewerViewHolder, position: Int) {
        holder.name.text = movieReviews[position].FUID
        if (movieReviews[position].comment != null){
            holder.comment.text = movieReviews[position].comment
        }
        else{
            holder.comment.visibility = View.INVISIBLE
        }
        if (movieReviews[position].selfRating != null){
            holder.rating.numStars = movieReviews[position].selfRating?.toInt()!!
        }
        else{
            holder.rating.visibility = View.INVISIBLE
        }

    }

    override fun getItemCount(): Int {
        return movieReviews.size
    }

    fun setMovie(reviews: ArrayList<MovieUser>){
        this.movieReviews = reviews
    }
}