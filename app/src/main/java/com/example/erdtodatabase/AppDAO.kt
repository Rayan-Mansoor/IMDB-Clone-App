package com.example.erdtodatabase

import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import kotlinx.coroutines.selects.select

@Dao
interface AppDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenre(genre: Genre)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWriter(writer: Writer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMG(mg : MovieGenre)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMU(movieUser: MovieUser)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMD(movieUser: MovieDirector)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMW(movieWriter: MovieWriter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirector(director: Director)

    @Query("select * from movie")
    suspend fun getAllMovies() : List<Movie>

    @Query("SELECT * FROM movie WHERE title LIKE '%' || :title || '%'")
    suspend fun getAllMoviesWithName(title : String) : List<Movie>

    @Query("select * from movie where year = :year")
    suspend fun getMoviesByYear(year : Int) : List<Movie>

    @Query("select * from movie where title IN (select FMID from MovieGenre where Rtrim(ltrim(lower(FGID))) = Rtrim(ltrim(lower(:genre))))")
    suspend fun getMoviesByGenre(genre : String) : List<Movie>

    @Query("select * from movie where title in (select FMID from MovieDirector where Rtrim(ltrim(lower(FDID))) = Rtrim(ltrim(lower(:director))))")
    suspend fun getMoviesByDirector(director: String) : List<Movie>

    @Query("select * from movie where title in (select FMID from MovieWriter where Rtrim(ltrim(lower(FWID))) = Rtrim(ltrim(lower(:writer))))")
    suspend fun getMoviesByWriter(writer: String) : List<Movie>

    @Query("select group_concat(FGID) from moviegenre where FMID = :movie")
    suspend fun getGenreByMovie(movie: String) : String

    @Query("select group_concat(FDID) from moviedirector where FMID = :movie")
    suspend fun getDirectorByMovie(movie: String) : String

    @Query("select group_concat(FWID) from moviewriter where FMID = :movie")
    suspend fun getWriterByMovie(movie: String) : String

    @Query("UPDATE MovieUser SET likes = :thumbs WHERE FMID = :movie AND FUID = :user")
    suspend fun updateLikes(user: String, movie: String, thumbs: Boolean): Int

    @Query("INSERT INTO MovieUser (FUID,FMID,likes) VALUES (:user, :movie, :thumbs)")
    suspend fun insertLikes(user: String, movie: String, thumbs: Boolean)

    suspend fun updateOrInsert(user: String, movie: String, thumbs: Boolean) {
        try {
            val rowsAffected = updateLikes(user, movie, thumbs)
            if (rowsAffected == 0) {
                insertLikes(user, movie, thumbs)
            }
        } catch (e: SQLiteConstraintException) {
            insertLikes(user, movie, thumbs)
        }
    }

    @Query("select likes from MovieUser where FMID = :movieTitle and FUID = :user")
    suspend fun getlike_dislike(movieTitle : String, user: String) : Boolean?

    @Query("select * from user where UID = :id and password = :pass")
    suspend fun getUser(id: String, pass: String) : List<User>

    @Query("Select * from movieuser where FMID = :movieID")
    suspend fun getReviews(movieID : String) : List<MovieUser>

    @Query("Select * from movieuser")
    suspend fun getAllReviews() : List<MovieUser>

    @Update
    suspend fun updateMU(MU : MovieUser) : Int

    @Query("select * from MOVIEUSER where FMID = :mid and FUID = :uid ")
    suspend fun getMU(mid : String, uid: String) : MovieUser

    suspend fun updateOrInsertMU(review : MovieUser) {
        val oldMU = getMU(review.FMID,review.FUID)

        if (review.comment == null || review.comment == ""){
            if (oldMU != null){
                review.comment = oldMU.comment
            }

        }

        if (review.selfRating == null || review.selfRating == 0.0){
            if(oldMU != null){
                review.selfRating = oldMU.selfRating
            }

        }

        if (review.likes == null){
            if(oldMU != null) {
                review.likes = oldMU.likes
            }
        }


        try {
            val rowsAffected = updateMU(review)
            if (rowsAffected == 0) {
                insertMU(review)
            }
        } catch (e: SQLiteConstraintException) {
            insertMU(review)
        }
    }

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("delete from MovieDirector where Rtrim(ltrim(lower(FMID))) = Rtrim(ltrim(lower(:md)))")
    suspend fun deleteMD(md : String)

    @Query("delete from MovieWriter where Rtrim(ltrim(lower(FMID))) = Rtrim(ltrim(lower(:mw)))")
    suspend fun deleteMW(mw : String)

    @Query("delete from MovieGenre where Rtrim(ltrim(lower(FMID))) = Rtrim(ltrim(lower(:mg)))")
    suspend fun deleteMG(mg : String)

    @Query("delete from MovieUser where Rtrim(ltrim(lower(FMID))) = Rtrim(ltrim(lower(:mu)))")
    suspend fun deleteMU(mu : String)

    @Query("select count(*) from Movie")
    suspend fun MovieRowCount() : Int

    @Query("select count(*) from MovieGenre")
    suspend fun MovieGenreRowCount() : Int

    @Query("select count(*) from MovieDirector")
    suspend fun MovieDirectorRowCount() : Int

    @Query("select count(*) from MovieWriter")
    suspend fun MovieWriterRowCount() : Int

    @Query("select count(*) from MovieUser")
    suspend fun MovieUserRowCount() : Int

    suspend fun EmptyMovieData() : Boolean{
        if(MovieRowCount() != 0 && MovieDirectorRowCount() != 0 && MovieGenreRowCount() != 0 && MovieWriterRowCount() != 0 && MovieUserRowCount() != 0 ){
            return true
        }
        return false
    }
}