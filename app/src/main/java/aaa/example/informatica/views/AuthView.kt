package aaa.example.informatica.views

import android.content.Intent
import androidx.annotation.NonNull
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface  AuthView: MvpView{
    fun toastMe(msg: String)
    fun startChecking(msg: String)
    fun endChecking()
    fun checkErrors(@NonNull task: Task<AuthResult>)
    //fun isNetworkAvailable()
    fun changeActivity(activity: Class<*>)
    fun putPref(str: String)
    fun alreadyChangeActivity()
}