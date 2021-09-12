package com.achtec.tourguide.JetpackNavigation.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.achtec.tourguide.JetpackNavigation.TourguideModel
import com.achtec.tourguide.JetpackNavigation.adapters.TourguidesAdapter
import com.achtec.tourguide.R
import com.achtec.tourguide.databinding.FragmentSearchForTourGuideBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Search_for_tour_guide.newInstance] factory method to
 * create an instance of this fragment.
 */
class Search_for_tour_guide : Fragment() ,TourguidesAdapter.OnItemClickListener{
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var ordersList: ArrayList<TourguideModel>? = null



    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private var _binding: FragmentSearchForTourGuideBinding? = null
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

        _binding = FragmentSearchForTourGuideBinding.inflate(inflater, container, false)
        val root: View = _binding!!.root
        // Inflate the layout for this fragment

        ordersList = ArrayList()
        ordersList!!.add(TourguideModel(R.drawable.tg2, "Roy", "UBK 167A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg1, "Prince", "UBK 183A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg2, "Dean", "UBK 113A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg1, "Roy", "UBK 167A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg2, "Prince", "UBK 183A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg1, "Dean", "UBK 113A", "Ntinda", "0780134747","NO. 1233"))
ordersList!!.add(TourguideModel(R.drawable.tg2, "Roy", "UBK 167A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg1, "Prince", "UBK 183A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg2, "Dean", "UBK 113A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg1, "Roy", "UBK 167A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg2, "Prince", "UBK 183A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(TourguideModel(R.drawable.tg1, "Dean", "UBK 113A", "Ntinda", "0780134747","NO. 1233"))
        ordersList!!.add(
            TourguideModel(
                R.drawable.tg2,
                "Roy",
                "UBK 167A",
                "Ntinda",
                "0780134747",
                "NO. 1233"
            )
        )
        ordersList!!.add(
            TourguideModel(
                R.drawable.tg1,
                "Prince",
                "UBK 183A",
                "Ntinda",
                "0780134747",
                "NO. 1233"
            )
        )
        ordersList!!.add(
            TourguideModel(
                R.drawable.tg2,
                "Dean",
                "UBK 113A",
                "Ntinda",
                "0780134747",
                "NO. 1233"
            )
        )
        ordersList!!.add(
            TourguideModel(
                R.drawable.tg1,
                "Roy",
                "UBK 167A",
                "Ntinda",
                "0780134747",
                "NO. 1233"
            )
        )
        ordersList!!.add(
            TourguideModel(
                R.drawable.tg2,
                "Prince",
                "UBK 183A",
                "Ntinda",
                "0780134747",
                "NO. 1233"
            )
        )
        ordersList!!.add(
            TourguideModel(
                R.drawable.tg1,
                "Dean",
                "UBK 113A",
                "Ntinda",
                "0780134747",
                "NO. 1233"
            )
        )

        _binding!!.rcvTourguides.adapter=TourguidesAdapter(ordersList!!, this)
        _binding!!.rcvTourguides.layoutManager=LinearLayoutManager(requireActivity())
        _binding!!.tgSearchBtn.setOnClickListener {
            Toast.makeText(requireActivity(),"searc guides",Toast.LENGTH_SHORT).show()
        }
        return root
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Search_for_tour_guide.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Search_for_tour_guide().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(position: Int) {
        Toast.makeText(requireActivity(), "Item $position clicked", Toast.LENGTH_SHORT).show()

//        when (position) {
//            0 -> startActivity(Intent(activity, WalletActivity::class.java))
//            8 -> println("no")
//            else -> println("maybe")
//        }
    }

    override fun onCallClick(position: Int) {
        TODO("Not yet implemented")
    }

    override fun onSelectingRider(position: Int) {
        Toast.makeText(requireActivity(), "Item $position clicked", Toast.LENGTH_SHORT).show()

    }
}