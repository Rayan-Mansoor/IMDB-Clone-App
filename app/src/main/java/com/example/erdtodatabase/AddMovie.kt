package com.example.erdtodatabase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.room.Room
import com.example.erdtodatabase.databinding.ActivityLoginAccountBinding
import com.example.erdtodatabase.databinding.AddMovDialogBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AddMovie : AppCompatActivity() {
    private lateinit var binding : AddMovDialogBinding
    private lateinit var db: AppDB

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        binding = AddMovDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Room.databaseBuilder(applicationContext,AppDB::class.java,"movie-database").build()
        db.openHelper.writableDatabase

        binding.addBtn.setOnClickListener {
            if(binding.addMovName.text != null
                && binding.addMovDesc.text != null
                && binding.addMovYear.text != null
                && binding.addMovGenre.text != null
                && binding.addMovDirec.text != null
                && binding.addMovWrite.text != null
            ){
                val title = binding.addMovName.text.toString()
                val description = binding.addMovDesc.text.toString()
                val year = binding.addMovYear.text.toString().toInt()
                val genres = binding.addMovGenre.text.toString()
                val directors = binding.addMovDirec.text.toString()
                val writers = binding.addMovWrite.text.toString()

                val genreList : ArrayList<String> = ArrayList(genres.split(","))
                val directorList : ArrayList<String> = ArrayList(directors.split(","))
                val writerList : ArrayList<String> = ArrayList(writers.split(","))






                val newMovie = Movie(title,year,description,null,null)



                GlobalScope.launch {
                    db.AppDAO().insertMovie(newMovie)

                    for (element in directorList){
                        db.AppDAO().insertDirector(Director(element))

                        val dirRel = MovieDirector(title,element)
                        db.AppDAO().insertMD(dirRel)
                    }



                    for (element in genreList){
                        db.AppDAO().insertGenre(Genre(element))

                        val genRel = MovieGenre(title,element)
                        db.AppDAO().insertMG(genRel)
                    }

                    for (element in writerList){
                        db.AppDAO().insertWriter(Writer(element))

                        val wriRel = MovieWriter(title,element)
                        db.AppDAO().insertMW(wriRel)
                    }
                    val intent = Intent(this@AddMovie, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.cancelBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}