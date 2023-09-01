package com.example.kotlinapp.Controller

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinapp.Adapters.MessageAdapter
import com.example.kotlinapp.Model.ChannelModel
import com.example.kotlinapp.Model.MessageContent
import com.example.kotlinapp.Model.MessageModel
import com.example.kotlinapp.R
import com.example.kotlinapp.Services.AuthenticateService
import com.example.kotlinapp.Services.MessageService
import com.example.kotlinapp.Services.UserDataService
import com.example.kotlinapp.Utilities.BROADCAST_USERDATA
import com.example.kotlinapp.Utilities.URL_SOCKET
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream


lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
val socket: Socket = IO.socket(URL_SOCKET)

class MainActivity : AppCompatActivity() {

    lateinit var Nametv : TextView
    lateinit var Emailtv : TextView
    lateinit var ChannelName : TextView
    lateinit var ImageIconiv : ImageView
    lateinit var Loginbtn : Button
    lateinit var channelListView: ListView
    lateinit var msgEditText: EditText
    lateinit var selectedImageView: ImageView
    lateinit var sendImageBtn : ImageButton
    lateinit var sendMessageBtn : ImageButton
    lateinit var selectImage : ImageButton
    lateinit var progressBarIV : ProgressBar
    lateinit var channelAdapter : ArrayAdapter<ChannelModel>
    lateinit var messageAdapter : MessageAdapter
    lateinit var msgList : RecyclerView
    var selectedChannel : ChannelModel? = null
    var selectedImageUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val options = IO.Options()
//        options.transports = arrayOf("websocket")  // Replace with the desired transport
//        val socket = IO.socket(URL_SOCKET, options)

        val drawerLayout : DrawerLayout = findViewById(R.id.drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        val toolbar : androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        setSupportActionBar(toolbar)
       // toolbar.setNavigationIcon(R.drawable.baseline_menu_24)
        toolbar.navigationIcon = resources.getDrawable(R.drawable.baseline_menu_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Nametv = findViewById(R.id.usernameNavigation)
        Emailtv = findViewById(R.id.idTextNavigation)
        Loginbtn = findViewById(R.id.loginBtnNavigation)
        ImageIconiv = findViewById(R.id.profileImgNavigation)
        channelListView = findViewById(R.id.channel_list)
        ChannelName = findViewById(R.id.pleaseLogin)
        msgEditText = findViewById(R.id.enterMessage)
        msgList = findViewById(R.id.MessagesListview)
        sendImageBtn = findViewById(R.id.sendImage)
        selectedImageView = findViewById(R.id.selectedImage)
        sendMessageBtn = findViewById(R.id.sendMessage)
        selectImage = findViewById(R.id.selectImage)
        progressBarIV = findViewById(R.id.progressBarImageView)

//        socket.io().on(Manager.EVENT_TRANSPORT, Emitter.Listener { args ->
//            val transport = args[0] as Transport
//            // Adding headers when EVENT_REQUEST_HEADERS is called
//            transport.on(
//                Transport.EVENT_REQUEST_HEADERS
//            ) { args ->
//                Log.v(
//                    "SOCKET TRIAL",
//                    "Caught EVENT_REQUEST_HEADERS after EVENT_TRANSPORT, adding headers"
//                )
//                val mHeaders =
//                    args[0] as MutableMap<String, List<String>>
//                mHeaders["Authorization"] =
//                    Arrays.asList("Basic bXl1c2VyOm15cGFzczEyMw==")
//            }
//        })

        //MessageService.clearMessage()
        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            val exception = args[0] as Exception
            // Handle connection error
            Log.d("SOCKET EXCEPTION", "ERROR: $exception")
           // Log.d("SOCKET EXCEPTION", exception.)
        }
        socket.on(Socket.EVENT_CONNECT) { args ->
            //val exception = args[0] as Exception
            // Handle connection error
            Log.d("SOCKET EXCEPTION", "CONNECTION SUCCESSFUL")
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeBroadcastReceiver,
            IntentFilter(BROADCAST_USERDATA))

        socket.connect()

        setAdapter()

        if (App.sharedPreferences.isLoggedIn){
            AuthenticateService.findUserByEmail(this){
                ChannelName.text = "Welcome!!!"
            }
        }

        //MessageService.clearChannel()
        channelListView.setOnItemClickListener{_, _, i, _ ->

            LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeBroadcastReceiver,
                IntentFilter(BROADCAST_USERDATA))
            selectedChannel = MessageService.channels[i]
            drawerLayout.closeDrawer(GravityCompat.START)
            updateWithChannel()
        }

        socket.on("channelCreated", onNewChannel)
        socket.on("messageCreated", onNewMessage)
        selectImage.setOnClickListener {
            openImagePicker.launch("image/*")
        }


        Log.d("OVERRIDE LEVEL", "ON CREATE")
    }


    override fun onResume() {
        super.onResume()
//        MessageService.clearMessage()
//        MessageService.clearChannel()
//        updateListData()
        Log.d("OVERRIDE LEVEL", "ON RESUME")
    }

    override fun onDestroy() {
        socket.disconnect()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            userDataChangeBroadcastReceiver)
        super.onDestroy()
        Log.d("OVERRIDE LEVEL", "ON DESTROY")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            true
        }
        else
            return super.onOptionsItemSelected(item)
    }

    private fun setAdapter(){
     //   MessageService.clearChannel()
        channelAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,
            MessageService.channels)
        channelListView.adapter = channelAdapter
        messageAdapter = MessageAdapter(this, MessageService.message)
        msgList.adapter = messageAdapter
        val layoutManager = LinearLayoutManager(this)
        msgList.layoutManager = layoutManager
    }

    val userDataChangeBroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if (App.sharedPreferences.isLoggedIn){
                Nametv.text = UserDataService.uname
                Emailtv.text = UserDataService.email
                val resourceId = resources.getIdentifier(UserDataService.userAvatar, "drawable" ,
                packageName)
                ImageIconiv.setImageResource(resourceId)
                Loginbtn.text = "Logout"
                ImageIconiv.setBackgroundColor(UserDataService.returnAvatarColor(UserDataService.userAvatarColor))
                if (context != null) {
                    MessageService.getChannel{ complete ->
                        if(complete){
                            if (MessageService.channels.isNotEmpty()){
                                selectedChannel = MessageService.channels[0]
                                channelAdapter.notifyDataSetChanged()
                                updateWithChannel()
                            }
                        }
                    }
                }
            }
        }

    }
//
//    fun updateListData(){
//        MessageService.getChannel { complete ->
//            if(complete){
//                if (MessageService.channels.isNotEmpty()){
//                    selectedChannel = MessageService.channels[0]
//                    channelAdapter.notifyDataSetChanged()
//                    updateWithChannel()
//                }
//            }
//        }
//    }

    fun updateWithChannel() {
        ChannelName.text = "#${selectedChannel?.name}"
//        MessageService.clearMessage()
//        LocalBroadcastManager.getInstance(this).registerReceiver(userDataChangeBroadcastReceiver,
//            IntentFilter(BROADCAST_USERDATA))
        messageAdapter.notifyDataSetChanged()
        //for message
        if(selectedChannel != null){
            MessageService.getMessage(selectedChannel!!.id){
                complete ->
                if (complete){
                    selectedImageView.visibility = View.GONE
                    messageAdapter.notifyDataSetChanged()
                    if (messageAdapter.itemCount > 0){
                        msgList.smoothScrollToPosition(messageAdapter.itemCount - 1)
                    }
                    else if(messageAdapter.itemCount <= 0){
                        MessageService.clearMessage()
                    }
                }
            }
        }
    }

    fun sendMessage(view: View)
    {
        if (App.sharedPreferences.isLoggedIn && msgEditText.text.isNotEmpty() && selectedChannel != null){
            val userId = UserDataService.id
            val channelId = selectedChannel!!.id
            val messagetxt = msgEditText.text.toString()
            val messageData = JSONObject()
            messageData.put("type", "text")
            messageData.put("content", messagetxt)
            socket.emit("newMessage", messageData.toString(),
            userId, channelId, UserDataService.uname, UserDataService.userAvatar, UserDataService.userAvatarColor)
          //  Log.d("SENT MESG", "SENT $messageData")
            msgEditText.text.clear()
            hidekeyboard()
            messageAdapter.notifyDataSetChanged()
        }
    }

    val openImagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()){
            uri : Uri? ->
        if (uri != null){
            selectedImageUri = uri
            selectedImageView.visibility = View.VISIBLE
            selectedImageView.setImageURI(uri)
            sendImageBtn.visibility = View.VISIBLE
            sendMessageBtn.visibility = View.GONE
            msgList.visibility = View.GONE
            Log.d("Image URI", "DONE")
        }
    }

    fun sendImage(view: View){
        //Log.d("SEND", "IMAGE")
        if (App.sharedPreferences.isLoggedIn && selectedChannel != null) {
           // Log.d("SEND", "IMAGE11")
            selectedImageUri?.let { imageUri ->
                progressBarIV.visibility = View.VISIBLE
                selectImage.visibility = View.GONE
                sendImageBtn.visibility = View.GONE
                GlobalScope.launch(Dispatchers.IO){
                    try {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        val outputStream = ByteArrayOutputStream()
                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                        val imageBytes = outputStream.toByteArray()
                        val imageBase = Base64.encodeToString(imageBytes, Base64.DEFAULT)
                        val messageData = JSONObject()
                        messageData.put("content", imageBase)
                        messageData.put("type", "image")
//                        val mesg = JSONObject().getJSONObject("content")
//                        Log.d("SEND", mesg.toString())
                        val userId = UserDataService.id
                        val channelId = selectedChannel!!.id
                       // Log.d("SEND", "IMAGE15")
                        socket.emit("newMessage", messageData.toString(), userId, channelId, UserDataService.uname,
                            UserDataService.userAvatar, UserDataService.userAvatarColor)
                        withContext(Dispatchers


                            .Main) {
                            delay(3000)
                            selectedImageUri = null
                            selectedImageView.visibility = View.GONE
                            selectedImageView.setImageURI(null)
                            progressBarIV.visibility = View.GONE
                            selectImage.visibility = View.VISIBLE
                            sendMessageBtn.visibility = View.VISIBLE
                            msgList.visibility = View.VISIBLE
                            messageAdapter.notifyDataSetChanged()
                        }
                    }
                    catch (e : Exception){
                        Log.d("SNED", "Error ${e.message}")
                    }
                    }
                }
                    ?: run {
                Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addChannelNav(view: View)
    {
        if (App.sharedPreferences.isLoggedIn){
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.add_channel_dialog, null)
            builder.setView(dialogView)
                .setPositiveButton("ADD"){ dialogInterface, i ->
                    val channelNameEt = dialogView.findViewById<EditText>(R.id.channel_name_et)
                    val channelDescEt = dialogView.findViewById<EditText>(R.id.channel_description_et)
                    val channelName = channelNameEt.text.toString()
                    val channelDesc = channelDescEt.text.toString()

                    socket.emit("newChannel", channelName, channelDesc)
                }
                .setNegativeButton("CANCEL"){dialogInterface, i ->

                }.show()
        }
    }

    val onNewChannel = Emitter.Listener { args ->
        runOnUiThread{
            val channelName = args[0] as String
            val channelDescription = args[1] as String
            val channelId = args[2] as String

            val newChannel = ChannelModel(channelName,
                channelDescription, channelId)
            MessageService.channels.add(newChannel)
            channelAdapter.notifyDataSetChanged()
        }
    }

    val onNewMessage = Emitter.Listener { args ->
        if (App.sharedPreferences.isLoggedIn){
            runOnUiThread {
                Log.d("SOCKET EMITTER", "on New Meesage")
                val chanelId = args[3] as String
                if (chanelId == selectedChannel?.id){
                    val message = args[0]
                    val messagetype = args[1] as? String?: ""
                    val userId = args[2] as String
                    val userName = args[4] as String
                    val userAvatar = args[5] as String
                    val userAvatarColor = args[6] as String
                   // val image = args[5] as String
                    val id = args[7] as String
                    val timestamp = args[8] as String
//                    Log.d("MESSAGE ", "$message")
                    Log.d("MESSAE ", "$messagetype")
                 //   if ()
                    val messageContent = when(message) {
                        is String -> MessageContent.StringData(message)
                        is Bitmap -> MessageContent.BitmapData(message)
                        else -> null
                    }
                    if (messageContent != null){
                       // Log.d("MESSAGE CONTENT", "$messageContent, $MessageType")
                        val newMessage = MessageModel(messageContent, messagetype, userId ,chanelId, userName, userAvatar, userAvatarColor,id, timestamp)
                        MessageService.message.add(newMessage)
                 //       Log.d("NEW MESSAGE SENT","Message : $message")
                        messageAdapter.notifyDataSetChanged()
                        msgList.smoothScrollToPosition(messageAdapter.itemCount - 1)

                    }
                }
            }
        }
    }


    fun loginBtnNav(view: View)
    {
        if (App.sharedPreferences.isLoggedIn){
            UserDataService.logout()
            channelAdapter.notifyDataSetChanged()
            messageAdapter.notifyDataSetChanged()
            Nametv.text = "User"
            Emailtv.text = "user@email.com"
            Loginbtn.text = "Login"
            ChannelName.text = "Please Log In!!"
            ImageIconiv.setImageResource(R.drawable.profiledefault)
            ImageIconiv.setBackgroundColor(Color.TRANSPARENT)
            MessageService.clearMessage()
            MessageService.clearChannel()
        }
        else{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    fun hidekeyboard(){
        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText){
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken , 0)
        }
    }


}