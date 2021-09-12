package com.achtec.tourguide.JetpackNavigation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.achtec.tourguide.JetpackNavigation.TourguideModel
import com.achtec.tourguide.R

class TourguidesAdapter(
    var tourguideList: ArrayList<TourguideModel>, var mlistener: OnItemClickListener
) : RecyclerView.Adapter<TourguidesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.tourguideitemview,
            parent, false
        )

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = tourguideList[position]


        holder.order_tourguide_name.text = currentItem.name
        holder.order_tourguide_location_now.text = currentItem.location
        holder.order_tourguide_profilepic.setImageResource(currentItem.image)
        holder.order_tourguide_phonenumber_topofJrid.text = currentItem.phone_number
        holder.id_number.text = currentItem.id_number
    }

    override fun getItemCount(): Int {
        return tourguideList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) ,
        View.OnClickListener{

        val order_tourguide_name: TextView = itemView.findViewById(R.id.tourguide_name)
        val order_tourguide_profilepic: ImageView =
            itemView.findViewById(R.id.order_tourguide_profilepic)
        val order_tourguide_location_now: TextView =
            itemView.findViewById(R.id.order_tourguide_location_now)
        val order_tourguide_phonenumber_topofJrid: TextView =
            itemView.findViewById(R.id.order_tourguide_phonenumber_topofJrid)
        val id_number: TextView = itemView.findViewById(R.id.order_tourguide_JRID)
        val order_tourguide_choose: ImageView = itemView.findViewById(R.id.order_tourguide_choose)


        init {
            itemView.setOnClickListener(this)
            order_tourguide_choose.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    mlistener.onCallClick(position)

                }
            }
            var x = 1
            order_tourguide_choose.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    mlistener.onSelectingRider(position)

                }
                x += 2
                if (x % 2 == 0) {
                    order_tourguide_choose.setImageResource(R.drawable.choose)

                } else {
                    order_tourguide_choose.setImageResource(R.drawable.choose)

                }

            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                mlistener.onItemClick(position)
            }

        }


    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onCallClick(position: Int)
        fun onSelectingRider(position: Int)

    }


    fun setOnItemClickListener(listener: OnItemClickListener?) {
        if (listener != null) mlistener = listener
    }
}

