package com.mkalachova.trelloclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mkalachova.trelloclone.R
import kotlinx.android.synthetic.main.item_member.view.*
import com.mkalachova.trelloclone.models.User
import com.mkalachova.trelloclone.utils.Constants

class MembersListAdapter (private val context: Context, private var list: ArrayList<User>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MembersViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_member, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MembersViewHolder) {
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(holder.itemView.iv_member_image)

            holder.itemView.tv_member_name.text = model.name
            holder.itemView.tv_member_email.text = model.email

            if(model.selected) {
                holder.itemView.iv_selected_member.visibility = View.VISIBLE
            } else {
                holder.itemView.iv_selected_member.visibility = View.GONE
            }
            holder.itemView.setOnClickListener {
                if(onClickListener != null) {
                    if(model.selected) {
                        onClickListener!!.onClick(position, model, Constants.UNSELECT)
                    } else {
                        onClickListener!!.onClick(position, model, Constants.SELECT)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MembersViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int, user: User, action: String)
    }
}