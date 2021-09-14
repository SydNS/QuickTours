@file:Suppress("DEPRECATION")

package com.achtec.tourguide.Authentication

import android.app.Activity
import android.app.AlertDialog
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
import android.widget.TextView
import android.widget.Toast


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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth;
// ...

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
// Initialize Firebase Auth
        auth = Firebase.auth
    }


    lateinit var uemail: TextView
    lateinit var upasswd: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater)
        binding.logInBtn.setOnClickListener {

            var useremail = uemail.text.toString().trim()
            var userpassword = upasswd.text.toString().trim()

            if (
                useremail.isNotEmpty() &&
                userpassword.isNotEmpty()
            ) {
                auth.signInWithEmailAndPassword(uemail, upasswd)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {

                            Toast.makeText(requireActivity(), "Succes", Toast.LENGTH_SHORT).show()

                        } else {

                            AlertDialog.Builder(requireActivity())
                                .setTitle("Login Not Successful")
                                .setMessage("kindly check your creds")
                                .setCancelable(false)
                                .setPositiveButton(
                                    "ok"
                                ) { dialog, which ->
                                    {
                                        binding.lgEmail.text.clear()
                                        binding.lgPassword.text?.clear()
                                    }
                                    // Whatever...
                                }.show()

                        }
                    }
            }
        }
        return binding.root
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


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFrag.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFrag().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    //    hiding the drawer in the specific fragments
    private var myInterface: MyDrawerController? = null

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        myInterface = try {
            activity as MyDrawerController
        } catch (e: ClassCastException) {
            throw ClassCastException("$activity must implement MyInterface")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        myInterface!!.unlockDrawer()
    }
}