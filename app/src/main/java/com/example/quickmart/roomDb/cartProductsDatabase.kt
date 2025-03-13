package com.example.quickmart.roomDb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase



// if we true the exportSchema it create the json file , but right now we dont use here so we did it false
@Database(entities = [cartProducts::class] , version = 1, exportSchema = false)


abstract class cartProductsDatabase : RoomDatabase(){

    abstract fun cartProductDao() : cartProductsDao

    companion object{
        @Volatile
        var INSTANCE : cartProductsDatabase ?= null

        fun getDatabaseInstance(context: Context) : cartProductsDatabase{
            var instance = INSTANCE
            if(instance != null) return instance

            synchronized(this){
                val roomDb = Room.databaseBuilder(context , cartProductsDatabase::class.java ,"CartProducts").allowMainThreadQueries().build()
                INSTANCE = roomDb
                return roomDb
            }
        }
    }
}


