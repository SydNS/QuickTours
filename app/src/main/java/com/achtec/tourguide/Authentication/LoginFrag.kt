@file:Suppress("DEPRECATION")

package com.achtec.tourguide.Authentication

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.achtec.tourguide.JetpackNavigation.MyDrawerController
import com.achtec.tourguide.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import android.content.DialogInterface
import android.graphics.Color
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.achtec.tourguide.R
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFrag.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFrag : Fragment() {

    lateinit var loginbtn: Button
    lateinit var loginbanner: TextView
    lateinit var login_emailaddress: TextInputLayout
    lateinit var login_password: TextInputLayout

    lateinit var uemail: String
    lateinit var upassd: String


    //firebase variables
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseAuthListner: FirebaseAuth.AuthStateListener
    private lateinit var firebasedatabase: FirebaseDatabase
    private lateinit var customersDatabaseRef: DatabaseReference
    private lateinit var loadingBar: ProgressDialog
    private lateinit var currentUser: FirebaseUser
    private lateinit var currentUserId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

// Initialize Firebase Auth

        firebaseAuthListner = FirebaseAuth.AuthStateListener {
            currentUser = FirebaseAuth.getInstance().currentUser!!

            NavHostFragment.findNavController(this)
                .navigate(R.id.action_loginFrag_to_tourGuidehome)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        auth = FirebaseAuth.getInstance()
        firebasedatabase = FirebaseDatabase.getInstance()

        loadingBar = ProgressDialog(activity)

        val v: View = inflater.inflate(R.layout.fragment_login, container, false)
        loginbtn = v.findViewById(R.id.loginbtn)
        loginbanner = v.findViewById(R.id.loginbanner)
        login_emailaddress = v.findViewById(R.id.login_emailaddress)
        login_password = v.findViewById(R.id.login_password)

        login(loginbtn, login_emailaddress, login_password, loginbanner)


        return v
    }

    private fun login(
        v: Button,
        em: TextInputLayout,
        pswd: TextInputLayout,
        loginbanner: TextView
    ) {
        v.setOnClickListener {

//            setting the errors flags to non
            em.isErrorEnabled = false
            em.error = ""
            pswd.isErrorEnabled = false
            pswd.error = ""

            uemail = login_emailaddress.editText?.text.toString().trim()
            upassd = login_password.editText?.text.toString().trim()

            //checking if the value from the email field is empty or  not
            when {
                uemail.isEmpty() -> {
                    em.isErrorEnabled = true
                    em.error = getString(R.string.enteremailaddress)
                }
                upassd.isEmpty() -> {
                    pswd.isErrorEnabled = true
                    pswd.error = getString(R.string.kindlyenterpassword)
                }
                upassd.isNotEmpty() && upassd.length < 8 -> {
                    pswd.isErrorEnabled = true
                    pswd.error = getString(R.string.eightcharacterlong)
                }

                else -> {
//            method logging in the user with the email & password provided
                    loginusers(uemail, upassd, loginbanner)


                }
            }


        }
    }

    private fun loginusers(
        uemail: String,
        upassd: String,
        loginbanner: TextView
    ) {
        auth.signInWithEmailAndPassword(uemail, upassd).addOnCompleteListener {
            if (it.isSuccessful) {

                if (WhatTypeOfUser() == "Tourist") {
                    NavHostFragment
                        .findNavController(this)
                        .navigate(R.id.action_loginFrag_to_tourGuidehome)

                } else {
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_loginFrag_to_nav_home)
                }
                Toast.makeText(requireActivity(), "${auth.currentUser?.email}", Toast.LENGTH_LONG)
                    .show()
            } else {
                loginbanner.text = it.exception?.message.toString()
                Toast.makeText(
                    requireActivity(),
                    it.exception?.message.toString(),
                    Toast.LENGTH_LONG
                ).show()

                Color.parseColor("#FF001E")


            }
        }
    }


    private fun WhatTypeOfUser(): String? {
        val sharedPreferences =
            requireActivity().getSharedPreferences("OldUserType", Context.MODE_PRIVATE)
        val isOld = sharedPreferences.getString("Type", null)
        return isOld
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
