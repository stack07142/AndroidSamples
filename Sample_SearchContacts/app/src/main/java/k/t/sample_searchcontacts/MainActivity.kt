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
import androidx.core.content.MimeTypeFilter
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    private val map: HashMap<String, Contact> = hashMapOf()

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
            .flatMapObservable {
                Observable.fromIterable(RxCursorIterable.from(it))
                    .doFinally { it.close() }
            }
            .reduce(hashMapOf<String, Contact>()) { map, cursor ->
                val lookUpKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
                val mimeType = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.MIMETYPE))
                val contact = map.getOrPut(lookUpKey) { Contact() }
                when (mimeType) {
                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE -> {
                        contact.name =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME_PRIMARY))
                        contact.givenName =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                        contact.familyName =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
                    }
                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                        val phoneType =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                        val phoneNumber =
                            cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        when (phoneType) {
                            "1" -> contact.phone1 = phoneNumber
                            "2" -> contact.phone2 = phoneNumber
                            "3" -> contact.phone3 = phoneNumber
                            else -> contact.phone1 = phoneNumber
                        }
                    }
                }
                map
            }
            //.map { digestAllContactInfo(it) }
            //.doOnNext { Log.w("Main", it) }
            //.collect({ StringBuilder() }, { sb, s -> sb.append(s) })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    it.asIterable().forEach {
                        Log.w("result", "${it.value}")
                    }
                    Log.d("Main", "--end--")
                },
                onError = {
                    Log.d("Main", "error= ${it.message}")
                }
            )
    }

    private fun queryContacts(): Cursor? {
        return contentResolver.query(
            ContactsContract.Data.CONTENT_URI,
            arrayOf(
                ContactsContract.Contacts.LOOKUP_KEY,
                ContactsContract.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            ),
            ContactsContract.Data.MIMETYPE + " IN (?, ?)",
            arrayOf(
                ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            ),
            ContactsContract.Contacts.DISPLAY_NAME_PRIMARY + ", " + ContactsContract.CommonDataKinds.Phone.TYPE + " COLLATE LOCALIZED ASC"
        )
    }

/*    private fun digestAllContactInfo(cursor: Cursor): String {
        val count = cursor.columnCount
        val sb = StringBuilder()
        for (i in 0 until count) {
            sb.append(cursor.getString(i))
            sb.append(" | ")
        }
        return sb.toString()
    }*/
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

data class Contact(
    var key: String? = null,
    var name: String? = null,
    var givenName: String? = null,
    var familyName: String? = null,
    var phone1: String? = null,
    var phone2: String? = null,
    var phone3: String? = null
) {
    override fun toString(): String {
        return String.format(
            "%20s | %20s | %20s | %20s | %20s | %20s\n",
            name,
            givenName,
            familyName,
            phone1,
            phone2,
            phone3
        )
    }
}