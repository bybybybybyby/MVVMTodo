package com.codinginflow.mvvmtodo.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase: RoomDatabase() {

    abstract fun taskDao(): TaskDao

    // Create the nested Callback class here instead of directly in the AppModule to keep Separation of Concerns
    // @Inject annotation tells dagger how it can create an instance of the class, and also tells
    // Dagger to pass the necessary dependencies if we define something in the constructor
    // @Provides (in AppModule) and @Inject constructor() basically have the same purpose (how create instance and dependencies to pass),
    // but use @Provides on classes we don't own (like Room; code of the library)
    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,   // Using `Provider` to be able to use TaskDatabase lazily (otherwise there would be a circular dependency of Callback needing TaskDatabase and viceversa)
        @ApplicationScope private val applicationScope: CoroutineScope   // From AppModule defined in provideApplicationScope(); Explicitly defining our own created Annotation @ApplicationScope
    ) : RoomDatabase.Callback() {
        // onCreate function will run only the first time database is created
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()  // Here is where actual database is instantiated (using get()) from the constructor inject because using `Provider`

            // GlobalScope could work,but is considered bad practice. Created our own Scope using DI in dagger
            // Whole reason to create our CoroutineScope; needed to execute any suspend functions (dao.insert())
            applicationScope.launch {
                dao.insert(Task("Wash the dishes", completed = true))
                dao.insert(Task("Do the laundry"))
                dao.insert(Task("Cloud up", important = true))
                dao.insert(Task("Work on app", important = true))
                dao.insert(Task("Prepare food"))
                dao.insert(Task("Zither practice"))
                dao.insert(Task("Flouting about"))
                dao.insert(Task("Clean room and vacuum"))
                dao.insert(Task("Play music"))
                dao.insert(Task("Wake up", completed = true))
            }

        }
    }
}