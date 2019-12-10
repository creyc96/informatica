package aaa.example.informatica.activities

import aaa.example.informatica.R
import aaa.example.informatica.presenters.AuthPresenter
import aaa.example.informatica.views.AuthView
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.NonNull
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.auth_progress_dialog.*

class AuthActivity : MvpAppCompatActivity(), AuthView {


    override fun alreadyChangeActivity() {
        val tmp = prefs.getString("type", "")
        var i: Intent
        if (tmp == "Teachers") {
            i = Intent(this, TeacherMainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
        if (tmp == "Students") {
            i = Intent(this, StudentMainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
        }
    }


    @InjectPresenter
    lateinit var authPresenter: AuthPresenter

    private var index: Int = 0
    private lateinit var prefs: SharedPreferences

    override fun checkErrors(@NonNull task: Task<AuthResult>) {
        when ((task.exception as FirebaseAuthException).errorCode) {

            "ERROR_INVALID_CUSTOM_TOKEN" -> toastMe ("The custom token format is incorrect. Please check the documentation.")
            "ERROR_CUSTOM_TOKEN_MISMATCH" -> toastMe ("The custom token corresponds to a different audience.")
            "ERROR_INVALID_CREDENTIAL" -> toastMe ("The supplied auth credential is malformed or has expired.")
            "ERROR_USER_MISMATCH" -> toastMe ("The supplied credentials do not correspond to the previously signed in user.")
            "ERROR_REQUIRES_RECENT_LOGIN" -> toastMe ("This operation is sensitive and requires recent authentication. Log in again before retrying this request.")
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> toastMe ("An account already exists with the same email address but different sign-in credentials. Sign in using a provider associated with this email address.")
            "ERROR_CREDENTIAL_ALREADY_IN_USE" -> toastMe ("This credential is already associated with a different user account.")
            "ERROR_USER_DISABLED" -> toastMe ("The user account has been disabled by an administrator.")
            "ERROR_USER_TOKEN_EXPIRED" -> toastMe ("The user\\'s credential is no longer valid. The user must sign in again.")
            "ERROR_USER_NOT_FOUND" -> toastMe ("Пользователь с таким адресом не зарегистрирован")
            "ERROR_INVALID_USER_TOKEN" -> toastMe ("The user\\'s credential is no longer valid. The user must sign in again.")
            "ERROR_OPERATION_NOT_ALLOWED" -> toastMe ("This operation is not allowed. You must enable this service in the console.")
            "ERROR_WEAK_PASSWORD" -> {
                ET_password.error = "Пароль должен состоять минимум из 6 символов"
                ET_password.requestFocus()
            }
            "ERROR_INVALID_EMAIL" -> {
                ET_email.error = "Неверный формат почты"
                ET_email.requestFocus()
            }
            "ERROR_WRONG_PASSWORD" -> {
                ET_password.error = "Неверный пароль"
                ET_password.requestFocus()
                ET_password.setText("")
            }
            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                ET_email.error = "Пользователь с такой почтой уже существует"
                ET_email.requestFocus()
            }
        }
    }

    /*fun isNetworkAvailable() {
        val mConnectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val mNetworkInfo = mConnectivityManager.allNetworkInfo
        if (mNetworkInfo != null) {
            for (i in mNetworkInfo.indices) {
                if (mNetworkInfo[i].state == NetworkInfo.State.CONNECTED){}
                    //return true
            }
        }
        //return false
    }*/


    override fun toastMe(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun startChecking(msg: String) {
        authProgressBar.visibility = View.VISIBLE
        pbText.text = msg
    }

    override fun endChecking() {
        authProgressBar.visibility = View.GONE
    }

    override fun changeActivity(activity: Class<*>) {
        startActivity(Intent(this, activity))
    }

    override fun putPref(str: String) {
        prefs.edit().putString("type", str).apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = getSharedPreferences(packageName, MODE_PRIVATE)
        super.onCreate(savedInstanceState)
        authPresenter.checkAlreadySigned()
        setContentView(R.layout.activity_auth)
        RB_student.setOnClickListener { index = 2 }
        RB_teacher.setOnClickListener { index = 1 }
        BTN_registration.setOnClickListener {authPresenter.registration(ET_email.text.toString(), ET_password.text.toString(), index)}
        BTN_sign_in.setOnClickListener {authPresenter.signing(ET_email.text.toString(), ET_password.text.toString(), index)}
    }

    override fun onBackPressed() {}
}
