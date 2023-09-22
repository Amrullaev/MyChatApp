package com.example.mychatapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mychatapp.databinding.ItemProfileBinding
import com.example.mychatapp.domain.UserData
import com.squareup.picasso.Picasso

class ProfileAdapter(private val list: List<UserData>, private val listener: OnClickListener) :
    RecyclerView.Adapter<ProfileAdapter.VH>() {

    inner class VH(private val itemProfileBinding: ItemProfileBinding) :
        RecyclerView.ViewHolder(itemProfileBinding.root) {
        fun onBind(user: UserData) {
            Picasso.get().load(user.photoUrl).into(itemProfileBinding.profileImage)
            itemProfileBinding.profileName.text = user.displayName
            itemProfileBinding.root.setOnClickListener {
                listener.onClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list[position])
    }

    interface OnClickListener {
        fun onClick(user: UserData)
    }
}