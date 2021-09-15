package com.achtec.tourguide.JetpackNavigation.ui.wallet

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.achtec.tourguide.JetpackNavigation.MyDrawerController
import com.achtec.tourguide.databinding.FragmentWalletBinding

class WalletFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private var _binding: FragmentWalletBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
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
        _binding = null
    }

   }