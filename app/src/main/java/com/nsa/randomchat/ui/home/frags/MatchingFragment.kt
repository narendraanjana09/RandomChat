package com.nsa.randomchat.ui.home.frags

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nsa.randomchat.BuildConfig
import com.nsa.randomchat.R
import com.nsa.randomchat.databinding.FragmentMatchingBinding
import com.nsa.randomchat.ui.home.adapters.MessageProgressAdapter
import com.nsa.randomchat.ui.home.extra.Analyitcs
import com.nsa.randomchat.ui.home.extra.FireBase
import com.nsa.randomchat.ui.home.extra.SavedText
import com.nsa.randomchat.ui.home.models.ChatsModel
import com.nsa.randomchat.ui.home.viewmodel.MainViewModel
import com.nsa.randomchat.ui.splash.SplashScreensActivity

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

        Analyitcs().log("matching_frag_open","Matching Fragment Opended")

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
        binding.infoCard.setOnClickListener {
            Analyitcs().log("Splash_From_Matching","Opening Splash From Matching Frag")
            startActivity(Intent(requireActivity(),SplashScreensActivity::class.java))
        }
        binding.shareCard.setOnClickListener {
         shareAPP()
        }
    }
    private fun shareAPP() {
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_SUBJECT, "Random Chat")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Start Chat With Random People." +
                    "\nhttps://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"+"\n.\n.\nRandom Chat allows you to connect with random people from around the world."

        )
        intent.type = "text/plain"
        startActivity(intent)
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
        mainViewModel.startChatProgress()
        binding.progressRecycler.layoutManager = linearLayoutManager
        binding.progressRecycler.adapter=MessageProgressAdapter(requireContext(),chatsList)
        mainViewModel.newChat.observe(viewLifecycleOwner){
            chatsList.add(it)
            (binding.progressRecycler.adapter as MessageProgressAdapter).notifyItemInserted(chatsList.size-1)
            binding.progressRecycler.scrollToPosition(chatsList.size-1)
        }

    }

    override fun onPause() {
        super.onPause()
        mainViewModel.pauseCountDown()
    }


}