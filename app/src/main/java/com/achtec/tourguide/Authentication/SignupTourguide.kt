package com.achtec.tourguide.Authentication

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.achtec.tourguide.R
import com.achtec.tourguide.databinding.FragmentLoginBinding
import com.achtec.tourguide.databinding.FragmentSignupTourguideBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignupTourguide.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignupTourguide : Fragment() {

    lateinit var layoutwithtabs: RelativeLayout
    lateinit var tabs: TabLayout
    lateinit var signupbtn: Button
    lateinit var signupbanner: TextView


    //declaring the views for grabbing the text from usrs
    lateinit var username: TextInputLayout
    lateinit var useremailaddress: TextInputLayout
    lateinit var userpassword: TextInputLayout
    private lateinit var userpassword2: TextInputLayout
    lateinit var userphone: TextInputLayout

    //declaring the radio views for grabbing the decision from usrs
    lateinit var userselection: RadioGroup
    lateinit var userradiobtn: RadioButton
    lateinit var delivererradiobtn: RadioButton
    lateinit var radioUserButtonselected: RadioButton


    //firebase variables
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAuthListner: FirebaseAuth.AuthStateListener
    private lateinit var firebasedatabase: FirebaseDatabase
    private lateinit var customersDatabaseRef: DatabaseReference
    private lateinit var deliverersDatabaseRef: DatabaseReference
    private lateinit var loadingBar: ProgressDialog
    private lateinit var currentUser: FirebaseUser
    private lateinit var currentUserId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

// Initialize Firebase Auth

        firebaseAuthListner = FirebaseAuth.AuthStateListener {
            currentUser = FirebaseAuth.getInstance().currentUser!!
//
//            NavHostFragment.findNavController(this)
//                .navigate(R.id.signup)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_signup_tourguide, container, false)

        auth = FirebaseAuth.getInstance()
        firebasedatabase = FirebaseDatabase.getInstance()

        loadingBar = ProgressDialog(activity)

//        hooking views to e used in the class
        username = v.findViewById(R.id.username)
        userphone = v.findViewById(R.id.phone)
        useremailaddress = v.findViewById(R.id.emailaddress)
        userpassword = v.findViewById(R.id.password)
        userpassword2 = v.findViewById(R.id.password2)
        signupbtn = v.findViewById(R.id.signupbtn)
        signupbanner = v.findViewById(R.id.signupbanner)
        userradiobtn = v.findViewById(R.id.radioButtonUser)
        delivererradiobtn = v.findViewById(R.id.radioButtonDeliverer)
        userselection = v.findViewById(R.id.userselection)


//        setting the action onclicking the button
        signupbtn.setOnClickListener {

//          setting the views to showing no errors when the button is first clicked
            useremailaddress.isErrorEnabled = false
            useremailaddress.error = ""
            username.isErrorEnabled = false
            username.error = ""
            userpassword.isErrorEnabled = false
            userpassword.error = ""
            userpassword2.isErrorEnabled = false

            userpassword2.error = ""
            userphone.isErrorEnabled = false
            userphone.error = ""

//       gettting the user input on the click of the button
            val uname: String = username.editText?.text.toString().trim()
            val phone: String = userphone.editText?.text.toString().trim()
            val uemail: String = useremailaddress.editText?.text.toString().trim()
            val password: String = userpassword.editText?.text.toString().trim()
            val password2: String = userpassword2.editText?.text.toString().trim()

//           getting radiobutton values
            val selectedId: Int = userselection.checkedRadioButtonId
            radioUserButtonselected = v.findViewById(selectedId)

            val userdecision: String = radioUserButtonselected.text.toString().trim()


//         flow structure controlled here

            //checking if the value from the email field is empty or  not
//            when {
//                uname.isEmpty() -> {
//                    username.isErrorEnabled = true
//                    username.error = getString(R.string.enteruserameneeded)
//                }
//
//                uemail.isEmpty() -> {
//                    useremailaddress.isErrorEnabled = true
//                    useremailaddress.error = getString(R.string.enteremailaddress)
//                }
//                phone.isEmpty() -> {
//                    userphone.isErrorEnabled = true
//                    userphone.error = getString(R.string.enterphonenumber)
//                }
//                phone.isNotEmpty() && phone.length != 10 -> {
//
//                    userphone.isErrorEnabled = true
//                    userphone.error = getString(R.string.enter10digitnumbers)
//                }
//
//                password.isEmpty() -> {
//                    signupbanner.text =
//                        getString(R.string.confirmpassword)
//                    userpassword.isErrorEnabled = true
//                    userpassword.error = getString(R.string.kindlyenterpassword)
//                }
//                password.isNotEmpty() && password.length < 8 -> {
//                    signupbanner.text = getString(R.string.eightcharacterlong)
//                    userpassword.isErrorEnabled = true
//                    userpassword.error = getString(R.string.eightcharacterlong)
//                }
//                password2.isEmpty() -> {
//                    signupbanner.text = getString(R.string.confirmpassword)
//                    userpassword2.isErrorEnabled = true
//                    userpassword2.error = getString(R.string.kindlyconfirm)
//                }
////
//                password != password2 -> {
//                    signupbanner.text = getString(R.string.matchingpasswordsneeded)
//                    userpassword2.isErrorEnabled = true
//                    userpassword2.error = getString(R.string.matchingpasswordsneeded)
//                }
//                userdecision.isEmpty() -> {
//                    userradiobtn.error = getString(R.string.radiobtnusertype)
//                    delivererradiobtn.error = getString(R.string.radiobtnusertype)
//                }
//
//                else -> {
//                    signupbanner.text = getString(R.string.thanks)
////            method creating the user with the email & password provided
//                    createAccount(uemail, password, v, userdecision)
//
//                }
//            }

            findNavController().navigate(R.id.action_signupTourguide_to_nav_home)
        }

        return v
    }

    private fun createAccount(
        email: String,
        password: String,
        view: View,
        appuser: String
    ) {

        storeDecidedUser(appuser)

        // loading bar that show the user some thing is happening
        loadingBar.setTitle("Please wait :")
        loadingBar.setMessage("While system is performing processing on your data...")
        loadingBar.show()

//        firebase code for signing up a user
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {

                // Sign in success, update UI with the signed-in user's information
                Log.d(ContentValues.TAG, "createUserWithEmail:success")
                val user = auth.currentUser

                currentUserId = user?.uid.toString()
                if (appuser=="Client"){
                    customersDatabaseRef = firebasedatabase.reference
                        .child(getString(R.string.users)).child(getString(R.string.clients))
                        .child(currentUserId)
                    customersDatabaseRef.setValue(true)

                    loadingBar.dismiss()
                    Navigation.findNavController(view)
                        .navigate(R.id.action_signupTourguide_to_nav_home)
                }else{

                    deliverersDatabaseRef =
                        FirebaseDatabase.getInstance().reference.child(getString(R.string.users))
                            .child(getString(R.string.tourguide)).child(
                                currentUserId
                            )
                    deliverersDatabaseRef.setValue(true)

                    loadingBar.dismiss()
                    Navigation.findNavController(view)
                        .navigate(R.id.action_signupTourguide_to_tourGuidehome2)

                }


                Toast.makeText(
                    activity,
                    "Welcome $currentUserId to the $appuser-side",
                    Toast.LENGTH_SHORT
                ).show()



            } else {
                // If sign in fails, display a message to the user.
                //                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                signupbanner.text = it.exception?.message.toString()
                Toast.makeText(
                    activity,
                    "Error Occured ${it.exception?.message.toString()}",
                    Toast.LENGTH_SHORT
                ).show()
                loadingBar.dismiss()

            }

        }
    }

    private fun storeDecidedUser(usertype: String) {

        val sharedPreferences =
            requireActivity().getSharedPreferences("OldUserType", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("Type", usertype)
        editor.apply()

    }

    override fun onResume() {
        super.onResume()
        val supportActionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.hide()

    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
    }


}
