package com.mkalachova.trelloclone.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mkalachova.trelloclone.R
import kotlinx.android.synthetic.main.item_card_selected_member.view.*
import com.mkalachova.trelloclone.models.SelectedMembers

open class CardMemberAdapter(
    private val context: Context,
    private var list: ArrayList<SelectedMembers>,
    private var assignMembers: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardMemberViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_card_selected_member,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is CardMemberViewHolder) {
            if (position == list.size - 1 && assignMembers) {
                holder.itemView.iv_add_member.visibility = View.VISIBLE
                holder.itemView.iv_selected_member_image.visibility = View.GONE
                holder.itemView.iv_selected_member_image.contentDescription = ""
            } else {
                holder.itemView.iv_add_member.visibility = View.GONE
                holder.itemView.iv_selected_member_image.visibility = View.VISIBLE
                holder.itemView.iv_selected_member_image.contentDescription = model.email

                Glide
                    .with(context)
                    .load(model.image)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.itemView.iv_selected_member_image)
            }
            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CardMemberViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick()
    }
}