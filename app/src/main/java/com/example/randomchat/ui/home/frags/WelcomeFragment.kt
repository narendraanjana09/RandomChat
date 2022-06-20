package com.example.randomchat.ui.home.frags

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.randomchat.R
import com.example.randomchat.databinding.FragmentWelcomeBinding
import com.example.randomchat.ui.ProgressDialog
import com.example.randomchat.ui.home.adapters.AvtarsAdapter
import com.example.randomchat.ui.home.extra.FireBase
import com.example.randomchat.ui.home.extra.SavedText
import com.example.randomchat.ui.home.models.AvtarModel
import com.example.randomchat.ui.home.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase


class WelcomeFragment : Fragment() {

    private lateinit var binding: FragmentWelcomeBinding
    private val imagesList= arrayListOf<AvtarModel>()
    private val alphas= arrayOf('A','B','C','D','E','F','G','H','I','J','K','L','M','5', '6', '7','8','N','O','P','Q','R','S','T','U','V','9', '0','W','X','Y','Z',
        'a','b','c','d','e','f','g','1', '2', '3','4','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z')
    private var previousIndex=-1
    private var loading = true
    private var pastVisiblesItems: Int=0
    private var visibleItemCount: Int=0
    private var totalItemCount: Int=0
    private var selectedAvtar:String=""

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       binding=DataBindingUtil.inflate(inflater,R.layout.fragment_welcome,container,false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        if(auth.currentUser!=null){
            findNavController().navigate(R.id.action_welcomeFragment_to_matchingFragment)
        }

        getList(false)

        binding.avtarRv.layoutManager=GridLayoutManager(requireContext(), 3)
        binding.avtarRv.adapter=AvtarsAdapter(requireContext(),imagesList,object :AvtarsAdapter.OnImageClickListener{
            override fun onClick(index: Int) {
                if(previousIndex!=-1){
                    imagesList[previousIndex].selected=false
                }
                selectedAvtar=imagesList[index].link
                (binding.avtarRv.adapter as AvtarsAdapter).notifyItemChanged(previousIndex)
                previousIndex=index
                imagesList[index].selected=true
                (binding.avtarRv.adapter as AvtarsAdapter).notifyItemChanged(previousIndex)

            }
        })
        binding.nextBtn.setOnClickListener {
           val userName=binding.userNameEd.text.toString().trim()
            if(checkUserName(userName)){
                val progress=ProgressDialog()
                progress.show(childFragmentManager,"progress")
                auth.signInAnonymously()
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInAnonymously:success")
                            val user = auth.currentUser
                            SavedText(context).setText(SavedText.USER_UID,user?.uid)
                            val profileUpdates = userProfileChangeRequest {
                                displayName = userName
                                photoUri = Uri.parse("https://avatars.dicebear.com/api/avataaars/$selectedAvtar.svg")
                            }
                            FireBase().apply {
                                if (user != null) {
                                    userRef.child(user.uid).setValue(
                                        UserModel(userName,
                                            "https://avatars.dicebear.com/api/avataaars/$selectedAvtar.svg"
                                            ,user.uid)
                                    )
                                }
                            }
                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    progress.dismiss()
                                    if (task.isSuccessful) {
                                        findNavController().navigate(R.id.action_welcomeFragment_to_matchingFragment)
                                    }
                                }
                        } else {
                            progress.dismiss()
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInAnonymously:failure", task.exception)
                            showToast("Authentication failed.")
                        }
                    }


            }
        }

        binding.avtarRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if(imagesList.size>=200){
                    return
                }
                if (dy > 0) { //
                    val mLayoutManager=((binding.avtarRv.layoutManager) as GridLayoutManager)
                    visibleItemCount = mLayoutManager.childCount
                    totalItemCount = mLayoutManager.itemCount
                    pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            loading = false
                            Log.v("...", "Last Item Wow !")
                            getList(true)

                            loading = true
                        }
                    }
                }
            }
        })
    }

    private fun getList(b: Boolean) {
        val positionStart=imagesList.size
        for (i in 1..20) {
            var str = ""
            for (j in 1..5) {
                str += alphas.random()
            }
            imagesList.add(AvtarModel(str, false))
        }
        if(b){
            (binding.avtarRv.adapter as AvtarsAdapter).notifyItemRangeInserted(positionStart,20)
        }
    }
    private fun checkUserName(userName: String): Boolean {
        if (userName.isEmpty()) {
            showToast("please enter UserName!")
            return false
        }
        if (userName.length < 2) {
            showToast("UserName must have 5 characters!")
            return false
        }
        if(selectedAvtar.isEmpty()){
            showToast("please select a Avtar!")
            return false
        }
        return true
    }
    fun showToast(message: String) {
        Toast.makeText(context, "" + message, Toast.LENGTH_SHORT).show()
    }

}