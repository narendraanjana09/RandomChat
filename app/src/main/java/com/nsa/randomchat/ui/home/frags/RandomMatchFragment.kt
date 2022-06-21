package com.nsa.randomchat.ui.home.frags


import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.*
import android.widget.ImageView
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
import com.nsa.randomchat.R
import com.nsa.randomchat.databinding.FragmentRandomMatchBinding
import com.nsa.randomchat.ui.home.adapters.AvtarsAdapter
import com.nsa.randomchat.ui.home.extra.FireBase
import com.nsa.randomchat.ui.home.extra.SavedText
import com.nsa.randomchat.ui.home.models.AvtarModel
import com.nsa.randomchat.ui.home.models.UserModel
import com.nsa.randomchat.ui.home.models.UserRoomModel
import com.nsa.randomchat.ui.home.viewmodel.MainViewModel
import com.nsa.randomchat.ui.home.viewmodel.RoomViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.nsa.randomchat.ui.home.extra.Analyitcs
import java.util.*


class RandomMatchFragment : Fragment() {


    private lateinit var binding: FragmentRandomMatchBinding
    private lateinit var auth: FirebaseAuth
    private val imagesList= arrayListOf<AvtarModel>()

    private val roomUsersList= arrayListOf<String>()

    private val alphas= arrayOf('A','B','C','D','E','F','G','H','I','J','K','L','M','5', '6', '7','8','N','O','P','Q','R','S','T','U','V','9', '0','W','X','Y','Z',
        'a','b','c','d','e','f','g','1', '2', '3','4','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z')


    private lateinit var counterRecycler: CountDownTimer
    private lateinit var counterImage: CountDownTimer
    private val fireBase= FireBase()
    private lateinit var firebaseUser: FirebaseUser
    private var fUserUid=""
    private lateinit var mainViewModel: MainViewModel
    private lateinit var roomViewModel: RoomViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_random_match, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Analyitcs().log("random_frag_open","Random Match Frag Started")
        fUserUid=SavedText(context).getText(SavedText.USER_UID)
        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        roomViewModel = ViewModelProvider(this).get(RoomViewModel::class.java)
        roomViewModel.setFirebaseUID(fUserUid)

        auth = Firebase.auth
        firebaseUser= auth.currentUser!!
        FireBase().userRef.child(fUserUid).child("room").removeValue()
        if(auth.currentUser==null){
            findNavController().navigate(R.id.welcomeFragment)
        }else{
            binding.apply {
                imageView.loadSvg(auth.currentUser!!.photoUrl.toString())
                userNameTv.text=auth.currentUser!!.displayName
                imageView.setOnClickListener {
                    cancelAllTimer()
                }
            }
            setMatchingQueries()
        }

        for(i in 1..5){
            var str = ""
            for (j in 1..5) {
                str += alphas.random()
            }
            imagesList.add(AvtarModel(str, true))
        }

        val myLinearLayoutManager = object : LinearLayoutManager(requireContext(), HORIZONTAL,true) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        binding.randomRv.layoutManager=myLinearLayoutManager
        binding.randomRv.adapter = AvtarsAdapter(requireContext(),imagesList,null)
        getRandomImageList(binding.randomRv.adapter as AvtarsAdapter)
        setRandomIconRotation(binding.randomeIcon)
    }


    private fun setMatchingQueries() {
        fireBase.apply {
            roomRef.child(fUserUid).setValue(true)
            roomViewModel.toast.observe(viewLifecycleOwner){
               // showToast(it)
            }

            roomViewModel.getRoomUsers()
            roomViewModel.roomList.observe(viewLifecycleOwner){
                Log.e("TAG", "setMatchingQueries:$it ")
                Analyitcs().log("Room_list","Got Room List ${it.size}")
                roomViewModel.connectRandomUsers(it.random())
            }

            roomViewModel.checkRoomCreted()
            roomViewModel.roomModel.observe(viewLifecycleOwner){
                Log.e("TAG", "room created $it")
                Analyitcs().log("Room_Created","room created")
                showToast("room created")
                showUserData(it)
            }

        }
    }

    private fun showUserData(roomModel: UserRoomModel?) {
        binding.apply {
            randomRv.visibility=View.INVISIBLE
            randomProfileCard.visibility=View.VISIBLE
            randomeUserNameTv.apply {
                visibility=View.VISIBLE
                text=""
            }
        }
        FireBase().userRef.child(roomModel?.userId.toString()).addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val userModel=snapshot.getValue<UserModel>()

                    if(userModel!=null){
                        mainViewModel.setUserModel(userModel)
                        binding.randomeUserNameTv.text=userModel.userName
                        binding.randomImageView.loadSvg(userModel.photoUrl.toString())
                        gotToChatsFrag(roomModel)
                    }else{

                    }
                }else{

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled: ${error.message}")
            }
        })

    }

    private fun gotToChatsFrag(roomModel: UserRoomModel?) {
        if(findNavController().currentDestination?.id!=R.id.randomMatchFragment){
            return
        }
        SavedText(requireContext()).setText(SavedText.ROOM_ID,roomModel?.roomId)
        SavedText(requireContext()).setText(SavedText.ROOM_USER_ID,roomModel?.userId)
        mainViewModel.setRoomModel(roomModel!!)
        if(roomModel!=null) {
            Handler().postDelayed({
                findNavController().navigate(
                    RandomMatchFragmentDirections.actionRandomMatchFragmentToChatsFragment())
            },2000)
        }
    }


    private fun setRandomIconRotation(imageView: ImageView) {
        var rotate=0f
       counterImage= object :CountDownTimer(Long.MAX_VALUE,40){
            override fun onTick(p0: Long) {
                rotate+=10
                imageView.rotation=rotate
            }

            override fun onFinish() {

            }
        }
        counterImage.start()
    }
    var count=0

    private fun getRandomImageList(avtarsAdapter: AvtarsAdapter) {
        count=0
       counterRecycler= object :CountDownTimer(Long.MAX_VALUE,600){
            override fun onTick(p0: Long) {
                var str = ""
                for (j in 1..5) {
                    str += alphas.random()
                }
                imagesList.add(0,AvtarModel(str, true))
                avtarsAdapter.notifyItemInserted(0)
                binding.randomRv.scrollToPosition(0)
                if(imagesList.size>100){
                    imagesList.clear()
                    for(i in 1..5){
                        var str = ""
                        for (j in 1..5) {
                            str += alphas.random()
                        }
                        imagesList.add(AvtarModel(str, true))
                    }
                    avtarsAdapter.notifyDataSetChanged()
                }
                count++
                if(count>=200){
                    this.cancel()
                    this.onFinish()
                    count=0
                }

            }

            override fun onFinish() {

                val snack = Snackbar.make(
                    binding.card,
                    "Sorry! looks like room is empty!",
                    4000
                )
                snack.setTextColor(ContextCompat.getColor(requireContext(),R.color.text_color1))
                snack.config(requireContext())
                snack.show()
                findNavController().popBackStack()
            }
        }
        counterRecycler.start()

    }
    fun Snackbar.config(context: Context){
        val params = this.view.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(12, 12, 12, 12)
        this.view.layoutParams = params

        this.view.background = context.getDrawable(R.drawable.round)

        ViewCompat.setElevation(this.view, 6f)
    }

    fun showToast(message: String) {
        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show()
    }
    fun ImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .componentRegistry { add(SvgDecoder(this@loadSvg.context)) }
            .build()

        val request = ImageRequest.Builder(this.context)
            .data(url)
            .target(this)
            .build()

        imageLoader.enqueue(request)
    }

    override fun onPause() {
        super.onPause()
        cancelAllTimer()
    }

    override fun onDestroy() {
        super.onDestroy()


    }

    private fun cancelAllTimer() {
        if (counterRecycler != null) {
            counterRecycler.cancel()
        }
        if (counterImage != null) {
            counterImage.cancel()
        }
        binding.randomeIcon.rotation=0f
    }

    override fun onResume() {
        super.onResume()
        if(counterRecycler!=null){
            counterRecycler.start()
        }
        if(counterImage!=null){
            counterImage.start()
        }
    }



}