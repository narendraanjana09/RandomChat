package com.example.randomchat.ui.home.frags

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.randomchat.R
import com.example.randomchat.databinding.FragmentChatsBinding
import com.example.randomchat.ui.home.adapters.MessageAdapter
import com.example.randomchat.ui.home.extra.FireBase
import com.example.randomchat.ui.home.extra.SavedText
import com.example.randomchat.ui.home.models.ChatsModel
import com.example.randomchat.ui.home.models.UserRoomModel
import com.example.randomchat.ui.home.viewmodel.MainViewModel
import com.example.randomchat.ui.home.viewmodel.RoomViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class ChatsFragment : Fragment() {


    private lateinit var binding: FragmentChatsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var roomModel:UserRoomModel
    private lateinit var mainViewModel: MainViewModel
    private lateinit var roomViewModel: RoomViewModel
    private val fireBase= FireBase()
    private lateinit var fUserUid:String
    private lateinit var chatsRef: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_chats, container, false)
        return binding.root
    }
   private val chatsList= arrayListOf<ChatsModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        firebaseUser= auth.currentUser!!

        roomViewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

         fUserUid=SavedText(context).getText(SavedText.USER_UID)
        FireBase().roomRef.child(fUserUid).removeValue()
        roomViewModel.setFirebaseUID(fUserUid)
        roomViewModel.checkRoomCreted()
        roomViewModel.roomDeleted.observe(viewLifecycleOwner){
            if(it){
                cancelChat()
            }
        }

        mainViewModel.roomModel.observe(viewLifecycleOwner){
            if(it==null){

            }else{
            roomModel=it
            Log.e("TAG", "roomModel: $roomModel" )
            fireBase.chatRoomRef.child(roomModel.roomId.toString()).addChildEventListener(object :ChildEventListener{
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    binding.sendMessageTv.visibility=View.GONE
                    val chat = snapshot.getValue<ChatsModel>()
                    if (chat != null) {
                        chatsList.add(chat)
                        (binding.chatsRecyclerView.adapter as MessageAdapter).notifyItemInserted(chatsList.size-1)
                    }
                }

                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
        }
        mainViewModel.userModel.observe(viewLifecycleOwner){
            binding.apply {
                userImageView.loadSvg(it.photoUrl.toString())
                userNameTv.text=it.userName
            }
        }

        if(auth.currentUser==null){
            findNavController().navigate(R.id.welcomeFragment)
        }
        binding.chatsRecyclerView.setHasFixedSize(true)
        val linearLayoutManager =
            LinearLayoutManager(requireContext())
        linearLayoutManager.stackFromEnd = true
        binding.chatsRecyclerView.layoutManager = linearLayoutManager
        binding.chatsRecyclerView.adapter = MessageAdapter(requireContext(), chatsList)



        binding.sendBtn.setOnClickListener {
            val message=binding.messageEd.text.toString().trim()
            if(message.isNullOrEmpty()){
                return@setOnClickListener
            }
            fireBase.chatRoomRef.child(roomModel.roomId.toString())
                .push()
                .setValue(ChatsModel(messageText = message, messageUser = firebaseUser.uid))
            binding.messageEd.setText("")
        }
        binding.randomIcon.setOnClickListener {
            val dialog = Dialog(requireActivity())
            dialog.setContentView(R.layout.cancel_chat_layout)
            dialog.setCancelable(false)
            val displayMetrics = DisplayMetrics()
            (context as Activity?)!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
            val height = displayMetrics.heightPixels
            val width = displayMetrics.widthPixels
            dialog.window!!.setLayout(width, (height * 0.6).toInt())
            dialog.window!!.attributes.windowAnimations = R.style.DialogAnimation
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            var yes_btn: MaterialCardView=dialog.findViewById(R.id.yes_btn);
            var no_btn: MaterialCardView=dialog.findViewById(R.id.no_btn);

            yes_btn.setOnClickListener {
                dialog.dismiss()
                findNavController().popBackStack()

            }
            no_btn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }
    }

    private fun cancelChat() {

        val snack = Snackbar.make(
            binding.chatsRecyclerView,
            "User Has left the room!",3000
        )
        snack.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_color1))
        snack.config(requireContext())
        snack.show()
        findNavController().popBackStack()
    }
    fun Snackbar.config(context: Context){
        val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(12, 12, 12, 12)
        this.view.layoutParams = params
        this.view.background = context.getDrawable(R.drawable.round)
        ViewCompat.setElevation(this.view, 6f)
    }
    private fun showToast(message: String) {
        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show()
    }
    private fun ImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }


}