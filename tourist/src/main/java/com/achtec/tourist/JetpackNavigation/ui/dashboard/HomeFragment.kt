package com.achtec.tourist.JetpackNavigation.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.achtec.tourist.R
import com.achtec.tourist.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding!!.findTourGuides.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_search_for_tour_guide)

        }
        _binding !!.eWallet. setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_slideshow)
        }
        _binding !!.circleImageView. setOnClickListener {

            findNavController().navigate(R.id.action_nav_home_to_nav_gallery)
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}