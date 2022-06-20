package com.example.randomchat.ui.home.frags

import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.randomchat.R
import com.example.randomchat.databinding.FragmentMatchingBinding
import com.example.randomchat.ui.home.adapters.MessageAdapter
import com.example.randomchat.ui.home.adapters.MessageProgressAdapter
import com.example.randomchat.ui.home.extra.FireBase
import com.example.randomchat.ui.home.extra.SavedText
import com.example.randomchat.ui.home.models.ChatsModel
import com.example.randomchat.ui.home.viewmodel.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MatchingFragment : Fragment() {


    private lateinit var binding: FragmentMatchingBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_matching, container, false)
        return binding.root
    }

    private val chatsList= arrayListOf<ChatsModel>()
    private var fUserUid=""
    private lateinit var mainViewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        fUserUid= SavedText(context).getText(SavedText.USER_UID)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        clearAll()
        if(auth.currentUser==null){
            findNavController().navigate(R.id.welcomeFragment)
        }else{
            binding.apply {
                imageView.loadSvg(auth.currentUser!!.photoUrl.toString())
                userNameTv.text=auth.currentUser!!.displayName
            }
        }
        binding.startTv.setOnClickListener {
            findNavController().navigate(R.id.action_matchingFragment_to_randomMatchFragment)
        }
        FireBase().apply {
            roomRef.child(fUserUid).removeValue()
            userRef.child(fUserUid).child("room").removeValue()
        }

    }

    private fun clearAll() {
        val userUid=SavedText(requireContext()).getText(SavedText.USER_UID)
        val randomUserUid=SavedText(requireContext()).getText(SavedText.ROOM_USER_ID)
        val roomId=SavedText(requireContext()).getText(SavedText.ROOM_ID)

        FireBase().apply {
            userRef.child(userUid).child("room").removeValue()
            roomRef.child(userUid).removeValue()
            if(randomUserUid.isNotEmpty()){
                roomRef.child(randomUserUid).removeValue()
                userRef.child(randomUserUid).child("room").removeValue()
            }
            if(roomId.isNotEmpty()){
                chatRoomRef.child(roomId).removeValue()
            }
        }
        SavedText(requireContext()).setText(SavedText.ROOM_ID,"")
        SavedText(requireContext()).setText(SavedText.ROOM_USER_ID,"")
    }

    override fun onResume() {
        super.onResume()
        clearAll()
        setProgress()
    }

    fun showToast(message: String) {
        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show()
    }
    fun ImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }
    var count1=0
    private fun setProgress() {
        setBackgrundChatProgress()
        object :CountDownTimer(1700,30){
            override fun onTick(p0: Long) {

               count1++

                binding.apply {
                    progress1.progress=count1
                    progress2.progress=count1
                }
                }

            override fun onFinish() {
                count1=0
                binding.apply {
                    progress1.progress=50
                    progress2.progress=50
                }
            }
        }.start()
    }

    private fun setBackgrundChatProgress() {
        binding.progressRecycler.setHasFixedSize(true)

        val linearLayoutManager = object : LinearLayoutManager(requireContext(), VERTICAL,false) {
            override fun canScrollVertically(): Boolean {
                return false
            }
        }
        linearLayoutManager.stackFromEnd = true
        binding.progressRecycler.layoutManager = linearLayoutManager
        binding.progressRecycler.adapter=MessageProgressAdapter(requireContext(),chatsList)
        mainViewModel.startChatProgress()
        mainViewModel.newChat.observe(viewLifecycleOwner){
            chatsList.add(it)
            (binding.progressRecycler.adapter as MessageProgressAdapter).notifyItemInserted(chatsList.size-1)
            binding.progressRecycler.scrollToPosition(chatsList.size-1)
        }

    }


}