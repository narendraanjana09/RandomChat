package com.example.randomchat.ui.home.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.randomchat.ui.home.extra.FireBase
import com.example.randomchat.ui.home.models.UserModel
import com.example.randomchat.ui.home.models.UserRoomModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomViewModel(application: Application): AndroidViewModel(application) {

    val toast: MutableLiveData<String> = MutableLiveData()

    val roomDeleted: MutableLiveData<Boolean> = MutableLiveData()

    private val _userModel: MutableLiveData<UserModel> = MutableLiveData()
    private val _fuserUid: MutableLiveData<String> = MutableLiveData()
    val userModel: LiveData<UserModel>
        get() = _userModel

    private val _roomModel: MutableLiveData<UserRoomModel> = MutableLiveData()
    val roomModel: LiveData<UserRoomModel>
        get() = _roomModel

    private val _roomList: MutableLiveData<List<String>> = MutableLiveData()
    val roomList: LiveData<List<String>>
        get() = _roomList

    private val _emptyRoom: MutableLiveData<Boolean> = MutableLiveData()
    val emptyRoom: LiveData<Boolean>
        get() = _emptyRoom



    fun getRoomUsers() {
        CoroutineScope(Dispatchers.IO).launch {
           FireBase().apply {
               roomRef.addValueEventListener(object :ValueEventListener{
                   override fun onDataChange(snapshot: DataSnapshot) {
                       _emptyRoom.value = !snapshot.exists()
                       if(snapshot.exists()){
                               val roomUserList= arrayListOf<String>()
                               snapshot.children.forEach {
                                   val userId=it.key
                                   if(userId!=_fuserUid.value){
                                       roomUserList.add(userId!!)
                                   }
                               }
                           CoroutineScope(Dispatchers.Main).launch {
                           if(roomUserList.isNullOrEmpty()){
                               _emptyRoom.value=true
                           }else{
                               _emptyRoom.value=false
                              _roomList.value=roomUserList
                           }
                           }
                       }
                   }

                   override fun onCancelled(error: DatabaseError) {
                       Log.e("TAG", "onCancelled: 1 $error ", )
                   }
               })
           }
        }
    }

    fun setFirebaseUID(fUserUid: String?) {
        _fuserUid.value=fUserUid
    }

    fun connectRandomUsers(random: String) {
        if(roomCreated){
            return
        }
        toast.value="creating room"
        FireBase().userRef.child(random).child("room").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Log.e("TAG", "room $random user got engaged: " )
                }else{
                    if(!roomCreated) {
                        val roomId="${_fuserUid.value}$random".toCharArray().sorted().joinToString("")
                        FireBase().userRef.child(_fuserUid.value.toString()).child("room")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (!snapshot.exists()) {
                                        toast.value="creating random model"
                                        Log.e("TAG", "creating random model: ", )
                                        FireBase().userRef.child(_fuserUid.value.toString()).child("room")
                                            .setValue(UserRoomModel(roomId,random))
                                        FireBase().userRef.child(random).child("room")
                                            .setValue(UserRoomModel(roomId,_fuserUid.value))
                                    }else{
                                        Log.e("TAG", "room $random user got engaged: " )
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Log.e("TAG", "onCancelled: 4 $error ", )
                                }
                            })
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TAG", "onCancelled: 3 $error ", )
            }
        })


    }
    private var roomCreated=false

    fun checkRoomCreted() {
        CoroutineScope(Dispatchers.IO).launch {
            FireBase().apply {
                userRef.child(_fuserUid.value.toString()).child("room").addValueEventListener(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){
                            roomDeleted.value=false
                            roomCreated=true
                            val roomModel=snapshot.getValue<UserRoomModel>()
                            CoroutineScope(Dispatchers.Main).launch{
                                _roomModel.value=roomModel
                            }
                        }else{
                            roomDeleted.value=true
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TAG", "onCancelled: 2 $error ", )
                    }
                })
            }
        }
        }


}