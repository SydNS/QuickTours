package com.achtec.tourist.JetpackNavigation.ui.profile

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.achtec.tourist.R
import com.achtec.tourist.databinding.FragmentGalleryBinding
import com.achtec.tourist.databinding.TourguideitemviewBinding
import kotlinx.android.synthetic.main.fragment_gallery.*
import android.view.MenuInflater





class ProfileFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        _binding?.orderNow?.setOnClickListener {
        findNavController().navigate(R.id.action_nav_gallery_to_nav_slideshow)
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.profile_menu, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_profile -> {

                true
            }
            R.id.action_logout -> {

                findNavController().navigate(R.id.action_nav_home_to_loginFrag)
                true
            }
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}