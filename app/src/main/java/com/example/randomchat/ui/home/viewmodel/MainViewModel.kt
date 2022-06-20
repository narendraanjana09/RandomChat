package com.example.randomchat.ui.home.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.randomchat.ui.home.extra.Util
import com.example.randomchat.ui.home.models.ChatsModel
import com.example.randomchat.ui.home.models.UserModel
import com.example.randomchat.ui.home.models.UserRoomModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application): AndroidViewModel(application) {

    private val _newChat: MutableLiveData<ChatsModel> = MutableLiveData()
    val newChat: LiveData<ChatsModel>
        get() = _newChat

    private val _userModel: MutableLiveData<UserModel> = MutableLiveData()
    val userModel: LiveData<UserModel>
        get() = _userModel

    private val _roomModel: MutableLiveData<UserRoomModel> = MutableLiveData()
    val roomModel: LiveData<UserRoomModel>
        get() = _roomModel

    fun setUserModel(userModel: UserModel) {
        _userModel.value=userModel
    }
    fun setRoomModel(roomModel: UserRoomModel) {
        _roomModel.value=roomModel
    }

    fun startChatProgress() {

           object :CountDownTimer(200000,1000){
               override fun onTick(p0: Long) {
                   var message=""
                   for(j in 1..Util.numbers.random()){
                       message+=" "
                   }

                   val chatsModel=ChatsModel(message,Util.senderReciver.random().toString())
                       _newChat.value=chatsModel
               }

               override fun onFinish() {

               }
           }.start()

    }

}