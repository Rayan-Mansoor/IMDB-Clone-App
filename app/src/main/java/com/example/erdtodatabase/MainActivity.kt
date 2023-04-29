package com.example.erdtodatabase

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.erdtodatabase.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Response

const val API_Key = "1f54c331demsh94dc2871fb83ec0p1175c0jsn5b6264316489" //Replace your API key here

class MainActivity : AppCompatActivity() {
    private lateinit var allDistinctGenres : List<String>
    private lateinit var allDistinctDirectors : List<String>
    private lateinit var allDistinctWriters : List<String>
    private lateinit var response : Response<ApiResponse>
    private lateinit var allMovies : ArrayList<Movie>
    private lateinit var movieGenreRel : ArrayList<MovieGenre>
    private lateinit var movieDirectorRel : ArrayList<MovieDirector>
    private lateinit var movieWriterRel : ArrayList<MovieWriter>
    private lateinit var db : AppDB
    private lateinit var movieAdapter : MovieListAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var allMoviesDB : List<Movie>
    private lateinit var currentUser : String

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.lgout){
            val sharedPref = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("logged_in", false)
            editor.apply()

            startActivity(Intent(this,LoginAccount::class.java))
            finish()
            return true
        }

        if(item.itemId == R.id.addmov){
            startActivity(Intent(this,AddMovie::class.java))
            finish()
            return true
        }

        if(item.itemId == R.id.delmov){
            popupDelete()
            return true
        }

        if(item.itemId == R.id.showAll){
            GlobalScope.launch {
                allMoviesDB = db.AppDAO().getAllMovies()
                movieAdapter.setMovies(allMoviesDB)

                withContext(Dispatchers.Main){
                    Log.d("MainActivity","notifyDataSetChanged called")
                    movieAdapter.notifyDataSetChanged()
                }
            }
            return true


        }

        return false
    }

    private fun popupDelete() {
        var ToDelete = ""
        val ET = EditText(this)
        ET.inputType = InputType.TYPE_CLASS_TEXT
        ET.hint = "Enter the movie title to delete"
        val builder = AlertDialog.Builder(this).setView(ET)
        builder.setPositiveButton("OK") { _, _ ->
            ToDelete = ET.text.toString()
            Log.d("Main",ToDelete)
            GlobalScope.launch {
                val getMovie = db.AppDAO().getAllMoviesWithName(ToDelete)
                db.AppDAO().deleteMD(getMovie[0].title)

                db.AppDAO().deleteMG(getMovie[0].title)

                db.AppDAO().deleteMW(getMovie[0].title)

                db.AppDAO().deleteMU(getMovie[0].title)

                db.AppDAO().deleteMovie(getMovie[0])

                allMoviesDB = db.AppDAO().getAllMovies()
                movieAdapter.setMovies(allMoviesDB)

                withContext(Dispatchers.Main){
                    Log.d("MainActivity","notifyDataSetChanged called")
                    movieAdapter.notifyDataSetChanged()
                }
            }

        }.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity","MainActivity's OnCreate Called")

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        //currentUser = "Rayan"




            val sharedPref = getSharedPreferences("shared_prefs", Context.MODE_PRIVATE)
            val loggedIn = sharedPref.getBoolean("logged_in", false)
            currentUser = sharedPref.getString("User_name","@RAMA")!!
            if (!loggedIn) {
                Log.d("MainActivity","Not Logged in")
                // Redirect to login activity
                val intent = Intent(this, LoginAccount::class.java)
                startActivity(intent)
                finish()
            }


//        applicationContext.deleteDatabase("movie-database")
//        finish()

        db = Room.databaseBuilder(applicationContext,AppDB::class.java,"movie-database").build()
        db.openHelper.writableDatabase







        allMoviesDB = listOf(
           // Movie("Maula Jatt",2021,"Highest grossing pakistani movie",150)
        )
//        Log.d("MainActivity",allMoviesDB[0].title)
//        Log.d("MainActivity", allMoviesDB.size.toString())
        movieAdapter = MovieListAdapter(allMoviesDB,db,currentUser)
        binding.rcvview.adapter = movieAdapter
        binding.rcvview.layoutManager = GridLayoutManager(this,1)
 //       binding.rcvview.setHasFixedSize(true)

        allDistinctGenres = listOf()
        allDistinctWriters = listOf()
        allDistinctDirectors = listOf()
        allMovies = arrayListOf()
        movieGenreRel = arrayListOf()
        movieDirectorRel = arrayListOf()
        movieWriterRel = arrayListOf()

        GlobalScope.launch {
            allMoviesDB = db.AppDAO().getAllMovies()
            movieAdapter.setMovies(allMoviesDB)

            withContext(Dispatchers.Main){
                Log.d("MainActivity","notifyDataSetChanged called")
                movieAdapter.notifyDataSetChanged()
            }
        }

        binding.searchQuery.setOnQueryTextListener(object :
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(myquery: String?): Boolean {
                GlobalScope.launch {
                    val allMoviesDBLike = db.AppDAO().getAllMoviesWithName(myquery!!)
                    movieAdapter.setMovies(allMoviesDBLike)

                    withContext(Dispatchers.Main){
                        Log.d("MainActivity","notifyDataSetChanged called")
                        movieAdapter.notifyDataSetChanged()
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == ""){
                    GlobalScope.launch {
                        allMoviesDB = db.AppDAO().getAllMovies()
                        movieAdapter.setMovies(allMoviesDB)

                        withContext(Dispatchers.Main){
                            Log.d("MainActivity","notifyDataSetChanged called")
                            movieAdapter.notifyDataSetChanged()
                        }
                    }
                    return true
                }
                return false
            }
        })

        binding.yearFilter.setOnClickListener {
            showPopup(it)
        }

        binding.genreFilter.setOnClickListener {
            popupSearch(it)
        }

        binding.directorFilter.setOnClickListener {
            popupSearch(it)
        }

        binding.writerFilter.setOnClickListener {
            popupSearch(it)
        }

        GlobalScope.launch {
            if(!db.AppDAO().EmptyMovieData()){
                CallAPI()
            }
        }


    }

    private fun isConnected() : Boolean{
        var connected : Boolean = false
        try {
            val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            connected = networkInfo?.isConnectedOrConnecting == true
            Log.d("MainActivity",connected.toString())
            return connected
        }catch (e : Exception){
            Toast.makeText(this,"Your not connected to the Internet", Toast.LENGTH_SHORT).show()
        }

        return connected
    }

    private fun showPopup(view: View) {
        Log.d("MainActivity",currentUser)
        Log.d("MainActivity",isConnected().toString())
        CallAPI()
        val popup = PopupMenu(this, view)
        for (i in 2023 downTo 1900) {
            popup.menu.add(i.toString())
        }
        popup.setOnMenuItemClickListener { item ->
            GlobalScope.launch {
                Log.d("MainActivity", item.itemId.toString())
                allMoviesDB = db.AppDAO().getMoviesByYear(item.title.toString().toInt())
                movieAdapter.setMovies(allMoviesDB)

                withContext(Dispatchers.Main){
                    Log.d("MainActivity","notifyDataSetChanged called")
                    movieAdapter.notifyDataSetChanged()
                }
            }
            true
        }
        popup.show()
    }

    private fun popupSearch(view: View){
        var ToSearch = ""
        val ET = EditText(this)
        ET.inputType = InputType.TYPE_CLASS_TEXT

        if (view.id == R.id.genreFilter){
            ET.hint = "Enter the genre to search"
            val builder = AlertDialog.Builder(this).setView(ET)
            builder.setPositiveButton("OK") { _, _ ->
                ToSearch = ET.text.toString()
                Log.d("Main",ToSearch)
                GlobalScope.launch {
                    allMoviesDB = db.AppDAO().getMoviesByGenre(ToSearch)
                    movieAdapter.setMovies(allMoviesDB)

                    withContext(Dispatchers.Main){
                        Log.d("MainActivity","notifyDataSetChanged called")
                        movieAdapter.notifyDataSetChanged()
                    }
                }

            }.show()
        }

        else if (view.id == R.id.directorFilter){
            ET.hint = "Enter the director to search"
            val builder = AlertDialog.Builder(this).setView(ET)
            builder.setPositiveButton("OK") { _, _ ->
                ToSearch = ET.text.toString()
                Log.d("Main",ToSearch)
                GlobalScope.launch {
                    allMoviesDB = db.AppDAO().getMoviesByDirector(ToSearch)
                    movieAdapter.setMovies(allMoviesDB)

                    withContext(Dispatchers.Main){
                        Log.d("MainActivity","notifyDataSetChanged called")
                        movieAdapter.notifyDataSetChanged()
                    }
                }

            }.show()
        }

        else if (view.id == R.id.writerFilter){
            ET.hint = "Enter the writer to search"
            val builder = AlertDialog.Builder(this).setView(ET)
            builder.setPositiveButton("OK") { _, _ ->
                ToSearch = ET.text.toString()
                Log.d("Main",ToSearch)
                GlobalScope.launch {
                    allMoviesDB = db.AppDAO().getMoviesByWriter(ToSearch)
                    movieAdapter.setMovies(allMoviesDB)

                    withContext(Dispatchers.Main){
                        Log.d("MainActivity","notifyDataSetChanged called")
                        movieAdapter.notifyDataSetChanged()
                    }
                }

            }.show()
        }




    }

    private fun CallAPI(){
        if(isConnected()){
            val MovieApi = APIclient.getAPIinstance().create(APIcall::class.java)
            GlobalScope.launch {

                Log.d("MainActivity","API called")
                response = MovieApi.getTopMovies(API_Key)
                if (response.isSuccessful){
                    for(i in 0 until response.body()!!.size){
                        val movie = Movie(response.body()!![i].title,response.body()!![i].year,response.body()!![i].description,response.body()!![i].rank,response.body()!![i].image)
                        allMovies.add(movie)
                    }

                    for (i in 0 until response.body()!!.size){
                        for (j in 0 until response.body()!![i].genre.size){
                            val movGen = MovieGenre(response.body()!![i].title,response.body()!![i].genre[j])
                            movieGenreRel.add(movGen)
                        }
                    }

                    allDistinctGenres = response.body()!!.flatMap { it.genre }.toList().distinct()
                    val allDirectors = response.body()!!.flatMap { it.director }.toList()
                    val allWriters = response.body()!!.flatMap { it.writers }.toList()

                    val cleanDirectors = allDirectors.map { it.replace("\\([^)]*\\)".toRegex(), "") }
                    val cleanWriters = allWriters.map { it.replace("\\([^)]*\\)".toRegex(), "") }

                    allDistinctDirectors = cleanDirectors.distinct()
                    allDistinctWriters = cleanWriters.distinct()

                    for (i in 0 until response.body()!!.size){
                        for (j in 0 until response.body()!![i].director.size){
                            val director = response.body()!![i].director[j]
                            val cleanDirector = director.replace("\\([^)]*\\)".toRegex(), "")
                            val movDirec = MovieDirector(response.body()!![i].title,cleanDirector)
                            movieDirectorRel.add(movDirec)
                        }
                    }

                    for (i in 0 until response.body()!!.size){
                        for (j in 0 until response.body()!![i].writers.size){
                            val writer = response.body()!![i].writers[j]
                            val cleanWriter = writer.replace("\\([^)]*\\)".toRegex(), "")
                            val movWrite = MovieWriter(response.body()!![i].title,cleanWriter)
                            movieWriterRel.add(movWrite)
                        }
                    }

                }
                else{
                    Log.d("MainActivity","response not successful")
                }



                //  val user = User("@RXM","Muhammad Rayan Mansoor","12345")

                //  db.AppDAO().insertUser(user)


                for (element in allDistinctGenres){
                    db.AppDAO().insertGenre(Genre(element))
                }

                for (element in allDistinctWriters){
                    db.AppDAO().insertWriter(Writer(element))
                }

                for (element in allDistinctDirectors){
                    db.AppDAO().insertDirector(Director(element))
                }

                for (i in 0 until allMovies.size){
                    db.AppDAO().insertMovie(allMovies[i])
                }

                for (i in 0 until movieGenreRel.size){
                    db.AppDAO().insertMG(movieGenreRel[i])
                }

                for (i in 0 until movieDirectorRel.size){
                    db.AppDAO().insertMD(movieDirectorRel[i])
                }

                for (i in 0 until movieWriterRel.size){
                    db.AppDAO().insertMW(movieWriterRel[i])
                }


                //    db.AppDAO().insertMU(MovieUser(allMovies[0].title,user.UID,"Good Movie", 8.5))

                allMoviesDB = db.AppDAO().getAllMovies()
                movieAdapter.setMovies(allMoviesDB)

//                Log.d("MainActivity",allMovies[4].title)

                withContext(Dispatchers.Main){
                    Log.d("MainActivity","notifyDataSetChanged called")
                    movieAdapter.notifyDataSetChanged()
                }
            }


        }
    }

    override fun onResume() {
        super.onResume()
        movieAdapter.notifyDataSetChanged()
    }
}