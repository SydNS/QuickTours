package com.achtec.tourguide.JetpackNavigation.ui.wallet

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.achtec.tourguide.JetpackNavigation.MyDrawerController
import com.achtec.tourguide.R
import com.achtec.tourguide.databinding.FragmentWalletBinding
import com.achtec.tourguide.databinding.TransferBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_wallet.*
import kotlinx.android.synthetic.main.transfer.*
import java.util.*

class WalletFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private var _binding: FragmentWalletBinding? = null
    private var transferBinding: TransferBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    //    bottomsheet refill

    lateinit var btnClose: ImageView
    lateinit var amount: EditText
    lateinit var phone_number: Button
    lateinit var save_button: ImageView
    lateinit var close_button: ImageView

    lateinit var refill_btn: ImageButton

    //    bottomsheet transfer

    lateinit var transfer_btnClose: ImageView
    lateinit var transfer_amount: EditText
    lateinit var transfer_reciver: EditText
    lateinit var transfer_now_btn: Button


    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var CustomerDatabaseRef: DatabaseReference? = null
    private var databaseReference: DatabaseReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        val root: View = binding.root
        databaseReference =
            FirebaseDatabase.getInstance().reference.child("Users").child("Account_balance")

        val c = Calendar.getInstance()

        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        binding.currentDate.text = Calendar.getInstance().time.toString()
        binding.refillAccountNumber.text= bal.toString()

        binding.refillBtn.setOnClickListener {


            val dialog = BottomSheetDialog(requireActivity())

            val view = layoutInflater.inflate(R.layout.bottomsheet_layout, null)

            btnClose = view.findViewById(R.id.close_button)
            amount = view.findViewById(R.id.amount)
            phone_number = view.findViewById(R.id.phone_number)
            save_button = view.findViewById(R.id.save_button)
            close_button = view.findViewById(R.id.close_button)

            phone_number.setOnClickListener {
                validateAndSaveOnlyInformation()

            }
            userInformation


            btnClose.setOnClickListener {

                dialog.dismiss()
            }

            dialog.setCancelable(true)

            dialog.setContentView(view)

            dialog.show()
        }


        binding.transferBtn.setOnClickListener {

            val dialog = BottomSheetDialog(requireActivity())

            val trfview = layoutInflater.inflate(R.layout.transfer, null)

            transfer_btnClose = trfview.findViewById(R.id.close_button)
            transfer_amount = trfview.findViewById(R.id.amountsend)
            transfer_reciver = trfview.findViewById(R.id.reciver)
            transfer_now_btn = trfview.findViewById(R.id.transfernow)


            transfer_now_btn.setOnClickListener {
                validateAndSaveOnlyInformationTransfer()

            }
            userInformation
            transfer_btnClose.setOnClickListener {

                dialog.dismiss()
            }
            dialog.setCancelable(true)

            dialog.setContentView(trfview)

            dialog.show()
        }

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


    var bal:Int = 0
    private fun validateAndSaveOnlyInformationTransfer() {
        when {
            TextUtils.isEmpty(transfer_amount.text.toString()) -> {
                Toast.makeText(activity, "Please provide an Amount to Send.", Toast.LENGTH_SHORT)
                    .show()
            }

            else -> {
                val userMap = java.util.HashMap<String, Any>()
//                userMap["uid"] = mAuth?.currentUser!!.uid
//                userMap["amount"] = amount.text.toString()
//                databaseReference?.child(mAuth?.currentUser!!.uid)?.updateChildren(userMap)

                transfer_amount.hint = transfer_amount.text.toString()
                " ${bal - transfer_amount.text.toString().toInt()}".also {
                    holdinh_balance.text = it
                }
            }
        }
    }


    private fun validateAndSaveOnlyInformation() {
        when {
            TextUtils.isEmpty(amount.text.toString()) -> {
                Toast.makeText(activity, "Please provide an Amount.", Toast.LENGTH_SHORT).show()
            }

            else -> {
                val userMap = java.util.HashMap<String, Any>()
//                userMap["uid"] = mAuth?.currentUser!!.uid
//                userMap["amount"] = amount.text.toString()
//                databaseReference?.child(mAuth?.currentUser!!.uid)?.updateChildren(userMap)

                amount.hint = amount.text.toString()
                holdinh_balance.text = amount.text.toString()
                bal= amount.text.toString().toInt()
            }
        }
    }

    lateinit var amountshs: String
    private val userInformation: Unit
        get() {
            mAuth?.currentUser?.let {
                databaseReference?.child(it.uid)
                    ?.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                                amountshs =
                                    dataSnapshot.child("amount").value.toString()
                                amount.hint = amountshs
                                holdinh_balance.text = amountshs


                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
            }
        }
    private val userInformationTransfer: Unit
        get() {
            mAuth?.currentUser?.let {
                databaseReference?.child(it.uid)
                    ?.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                                amountshs =
                                    dataSnapshot.child("amount").value.toString()
                                amount.hint = amountshs
                                holdinh_balance.text = amountshs


                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
            }
        }


}