
package com.viratara.android.quickshare

import `is`.arontibo.library.ElasticDownloadView
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.app.PendingIntent.getActivity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.text.Html
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.iid.InstanceIdResult
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.viratara.android.quickshare.R.layout.activity_main
import es.dmoral.toasty.Toasty
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    //Declarations
    private val READ_REQUEST_CODE: Int = 111
    lateinit var fileUri: Uri
    lateinit var storage: FirebaseStorage
    lateinit var database: FirebaseDatabase
    lateinit var progressBar: ProgressBar
   


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storage = FirebaseStorage.getInstance()
        database = FirebaseDatabase.getInstance()

        val sendButton: Button = findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                showChooser()
            }
            else
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 9)
        }

        val receiveButton: Button = findViewById(R.id.downloadButton)
        receiveButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
            {
                downloadFile()
            }
            else
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 6)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        inflater.inflate(R.menu.overflow_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.history -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.about ->
            {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.read_me ->
            {
                val intent = Intent(this, read_me::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun downloadFile() {
        val idText : EditText = findViewById(R.id.downloadId)
        val downloadId = idText.text.toString()
        println("hello")
        val reference: DatabaseReference = database.reference
        reference.addChildEventListener(
            object : ChildEventListener{
                override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                    val urls = dataSnapshot.key.toString()
                    if(urls.equals(downloadId)) {
                        startDownload(dataSnapshot.value.toString())
                        Toasty.info(this@MainActivity, "Download Started!", Toast.LENGTH_SHORT, true).show()
                    }


                }

                override fun onCancelled(p0: DatabaseError) {
                    Toasty.error(this@MainActivity, "Error!", Toast.LENGTH_SHORT, true).show()
                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildRemoved(p0: DataSnapshot) {
                    Toasty.error(this@MainActivity, "File not found!", Toast.LENGTH_SHORT, true).show()
                }
            }
        )

    }

    private fun startDownload(toString: String) {
        val r: DownloadManager.Request = DownloadManager.Request(Uri.parse(toString))
        r.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "fileName")
        r.allowScanningByMediaScanner()
        r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        val dm: DownloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(r)
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            showChooser()
        }
        else if (requestCode==6 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            downloadFile()
        }
        else
            Toasty.error(this@MainActivity, "Please give permission!", Toast.LENGTH_SHORT, true).show()
    }

    private fun showChooser() {
        val  intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, READ_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode==READ_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null)
        {
            fileUri = data.getData()
            uploadFile(fileUri)
        }
        else{
            Toasty.warning(this@MainActivity, "Please select a file!", Toast.LENGTH_SHORT, true).show()
        }
    }

    private fun uploadFile(fileUri: Uri) {
        val mElasticDownloadView : ElasticDownloadView = findViewById((R.id.elastic_download_view))
        mElasticDownloadView.startIntro()
        mElasticDownloadView.setProgress(0.toFloat())

        val STRING_CHARACTERS = ('a'..'z').toList().toTypedArray()
        val fileId = (1..32).map { STRING_CHARACTERS.random() }.joinToString("")
        var storageReference: StorageReference = storage.reference

        val ref = storageReference.child("Uploads").child(fileId)
        val uploadTask = ref.putFile(fileUri).addOnSuccessListener{ }

            .addOnFailureListener { exception ->
                Toasty.error(this@MainActivity, "File Upload Failed!", Toast.LENGTH_SHORT, true).show()
                mElasticDownloadView.fail()
                mElasticDownloadView.visibility = View.GONE
            }
            .addOnProgressListener { taskSnapshot ->
                var currentProgress = (100*taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount).toFloat()
                mElasticDownloadView.setProgress(currentProgress)
            }
            .addOnCompleteListener{
                mElasticDownloadView.success()

            }


        val urlTask = uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result.toString()
                val reference: DatabaseReference = database.reference
                reference.child(fileId).setValue(downloadUri).addOnCompleteListener { task ->
                    if(task.isSuccessful) {
                        mElasticDownloadView.startIntro()

                        Toasty.success(this@MainActivity, "Success!", Toast.LENGTH_SHORT, true).show()


                        uploadDialog(fileId)

                    }
                    else
                        Toast.makeText(this@MainActivity, "File Upload Failed", Toast.LENGTH_SHORT).show()
                        mElasticDownloadView.startIntro()
                }

            } else {
                Toasty.error(this@MainActivity, "File upload failed!", Toast.LENGTH_SHORT, true).show()
            }
        }


    }

    private fun uploadDialog(fileId: String) {

            val builder = AlertDialog.Builder(this@MainActivity)
            //setContentView(R.layout.activity_main)

            // Set the alert dialog title
            builder.setTitle("File Id")

            // Display a message on alert dialog
            builder.setMessage(fileId)

            // Set a positive button and its click listener on alert dialog
            builder.setPositiveButton("Copy"){ dialog, which ->
                // Do something when user press the positive button
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("File Id", fileId)
                clipboard.primaryClip = clip
                Toasty.info(this@MainActivity, "File-Id copied to clipboard!", Toast.LENGTH_SHORT, true).show()

            }


            // Display a negative button on alert dialog
            builder.setNegativeButton("Cancel"){dialog,which ->
                Toasty.info(this@MainActivity, "File-Id saved in history.", Toast.LENGTH_SHORT, true).show()

            }


            // Finally, make the alert dialog using builder
            val dialog: AlertDialog = builder.create()

            // Display the alert dialog on app interface
            dialog.show()

        val mypreference = MyPreferences(this)
        mypreference.setFileId(fileId)


        val vg = findViewById<View>(R.id.elastic_download_view)
        vg.invalidate()
     
        }

}
