package aaa.example.informatica.presenters

import aaa.example.informatica.activities.StudentMainActivity
import aaa.example.informatica.activities.TeacherMainActivity
import aaa.example.informatica.views.AuthView
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

@InjectViewState
class AuthPresenter: MvpPresenter<AuthView>() {

    fun checkAlreadySigned(){
        if (FirebaseAuth.getInstance().currentUser != null){
            viewState.alreadyChangeActivity()
        }

    }


    fun registration(email: String, password:String, index: Int){
        if (email == "" || password == "")
            viewState.toastMe ( "Для регистрации ведите E-mail и пароль")
        else if (index == 0)
            viewState.toastMe("Отметьте тип аккаунта")
        /*else if (!isNetworkAvailable(this))
            toastMe ( "Нет соединения с интернетом") !!!!!!!!!!!!!!!!!!!!!!*/
        else {
            viewState.startChecking("Регистрация")
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val db = FirebaseFirestore.getInstance()
                    val user = java.util.HashMap<String, Any>()
                    user["need_to_update"] = true
                    if (index == 1) {
                        val dbTeacher: CollectionReference = db.collection("Teachers")
                        dbTeacher.document(email).set(user)
                        dbTeacher.document(email).collection("my_students")
                    } else if (index == 2) {
                        val dbStudent = db.collection("Students")
                        dbStudent.document(email).set(user)
                    }
                    viewState.toastMe("Регистрация прошла успешна")
                    viewState.endChecking()
                } else {
                    viewState.checkErrors(it)
                    viewState.endChecking()
                }
            }
        }
    }

    fun signing(email: String, password:String, index: Int){
        if (email == "" || password == "")
            viewState.toastMe("Для регистрации ведите E-mail и пароль")
        else if (index == 0)
            viewState.toastMe("Отметьте тип аккаунта")
        /*else if (false!viewState.isNetworkAvailable())
            viewState.toastMe ( "Нет соединения с интернетом") !!!!!!!!!!!!!!!!!!!!!!!*/
        else{
            viewState.startChecking("Авторизация")
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener{
                if (it.isSuccessful){
                    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
                    if (index==1) auth(db, email, "Teachers", "преподавателем", TeacherMainActivity::class.java)
                    if (index==2) auth(db, email, "Students", "студентом", StudentMainActivity::class.java)
                } else {
                    viewState.checkErrors(it)
                    viewState.endChecking()
                }
            }
        }
    }

    private fun auth(db: FirebaseFirestore, email:String, type: String, rus_type: String, activity: Class<*>){
        val docRef: DocumentReference = db.collection(type).document(email)
        docRef.get().addOnCompleteListener{
            if (it.isSuccessful){
                val document: DocumentSnapshot? = it.result
                if (document != null) {
                    if (document.exists()) {
                        viewState.toastMe ( "Aвторизация прошла успешна")
                        viewState.putPref(type)
                        viewState.changeActivity(activity)
                        viewState.endChecking()
                    } else {
                        viewState.toastMe("Вы не являетесь $rus_type")
                        viewState.endChecking()
                    }
                }
            }
        }
    }

}
