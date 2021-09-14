@file:Suppress("DEPRECATION")
package com.achtec.tourguide

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.navigation.fragment.findNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.achtec.tourguide.databinding.FragmentSplashBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SplashFragment.newInstance] factory method to
 * create an instance of this fragment.
 * create an instance of requireActivity() fragment.
 */
class SplashFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentSplashBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for requireActivity() fragment
        binding = FragmentSplashBinding.inflate(layoutInflater)

//        (activity as Activity?).hideActionbar()
//        (activity as Activity?).setDrawerEnabled(False)

        binding.appTitle.animation =
            AnimationUtils.loadAnimation(requireActivity(), R.anim.top_anim)
        binding.enterButton.animation =
            AnimationUtils.loadAnimation(requireActivity(), R.anim.left_anim)
        binding.findBestGuide.animation =
            AnimationUtils.loadAnimation(requireActivity(), R.anim.right_anim)
        binding.developer.animation =
            AnimationUtils.loadAnimation(requireActivity(), R.anim.bottom_anim)

        binding.enterButton.setOnClickListener {

            Toast.makeText(activity,"rehtrhtr",Toast.LENGTH_SHORT).show()
            NavHostFragment.findNavController(this).navigate(R.id.action_splashFragment_to_loginFrag)
        }

        Handler().postDelayed({
            checkUserSignupStatus()
        }, 4000)



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
         * Use requireActivity() factory method to create a new instance of
         * requireActivity() fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SplashFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SplashFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    private fun checkUserSignupStatus() {
        when (getSignupStatus()) {
            true -> NavHostFragment.findNavController(this).navigate(R.id.action_splashFragment_to_loginFrag)
            false -> NavHostFragment.findNavController(this).navigate(R.id.action_splashFragment_to_signupFragment)
        }
    }

    private fun getSignupStatus(): Boolean {
        val sharedPreferences =
            activity?.getSharedPreferences("Signed_Up", Context.MODE_PRIVATE)

        return sharedPreferences!!.getBoolean("Signed_Up", false)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED) {
                    if ((PackageManager.PERMISSION_GRANTED === ContextCompat.checkSelfPermission(requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION))) {
                        Toast.makeText(activity, "Permission Granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
//                    onStop()
                }
                return
            }
        }


    }
}
