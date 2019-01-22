package k.t.sample_searchcontacts

import android.Manifest
import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toMap
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when (intent.action) {
            Intent.ACTION_SEARCH -> {
                // doMyQuery
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS),
                0
            )
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        // configuring the search widget

        // get the SearchView and set the searchable configuration
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        // enable assisted search for each SearchView
        (menu?.findItem(R.id.search)?.actionView as SearchView).apply {
            // assume current activity is the searchable activity
            setSearchableInfo(searchManager.getSearchableInfo((componentName)))
            setIconifiedByDefault(false)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.findAll -> {
                getContacts()
            }
        }
        return true
    }

    @SuppressLint("CheckResult")
    private fun getContacts() {
        Single.fromCallable { queryContacts() }
            .flatMapObservable { cursor ->
                Observable.fromIterable(RxCursorIterable.from(cursor))
                    .doFinally {
                        cursor.close()
                    }
            }
            .map { it.getString(1) to it.getString(0) }
            .toMap()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    Log.d("Main", "result= $it")
                },
                onError = {
                    Log.d("Main", "error= ${it.message}")
                }
            )
    }

    private fun queryContacts(): Cursor? {
        return contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            null, null,
            ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC"
        )
    }
}

class RxCursorIterable(private val cursor: Cursor) : Iterable<Cursor> {
    override fun iterator(): Iterator<Cursor> = RxCursorIterator.from(cursor)

    companion object {
        fun from(cursor: Cursor): Iterable<Cursor> = RxCursorIterable(cursor)
    }

    class RxCursorIterator(private val cursor: Cursor) : Iterator<Cursor> {
        override fun hasNext(): Boolean = !cursor.isClosed && cursor.moveToNext()
        override fun next(): Cursor = cursor

        companion object {
            fun from(cursor: Cursor): Iterator<Cursor> = RxCursorIterator(cursor)
        }
    }
}