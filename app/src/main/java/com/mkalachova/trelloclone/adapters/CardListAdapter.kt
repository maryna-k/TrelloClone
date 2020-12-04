package com.mkalachova.trelloclone.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mkalachova.trelloclone.R
import com.mkalachova.trelloclone.activities.TaskListActivity
import kotlinx.android.synthetic.main.item_card.view.*
import com.mkalachova.trelloclone.models.Card
import com.mkalachova.trelloclone.models.SelectedMembers

class CardListAdapter(private val context: Context, private var list: ArrayList<Card>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CardViewHolder(LayoutInflater.from(context).inflate(
            R.layout.item_card, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is CardViewHolder) {
            if (model.labelColor.isNotEmpty()) {
                holder.itemView.view_label_color.visibility = View.VISIBLE
                holder.itemView.view_label_color
                    .setBackgroundColor(Color.parseColor(model.labelColor))
            } else {
                holder.itemView.view_label_color.visibility = View.GONE
            }
            holder.itemView.tv_card_name.text = model.name

            if((context as TaskListActivity).assignedMemberDetailList.size > 0) {
                val selectedMembersList: ArrayList<SelectedMembers> = ArrayList()

                for(i in context.assignedMemberDetailList.indices) {
                    for(j in model.assignedTo) {
                        if(context.assignedMemberDetailList[i].id == j) {
                            val selectedMember = SelectedMembers(
                                context.assignedMemberDetailList[i].id,
                                context.assignedMemberDetailList[i].image,
                            )
                            selectedMembersList.add(selectedMember)
                        }
                    }

                    if(selectedMembersList.size > 0) {
                        if(selectedMembersList.size == 1 && selectedMembersList[0].id == model.createdBy) {
                            holder.itemView.rv_card_selected_members_list.visibility = View.GONE
                        } else {
                            holder.itemView.rv_card_selected_members_list.visibility = View.VISIBLE
                            holder.itemView.rv_card_selected_members_list.layoutManager = GridLayoutManager(context, 4)
                            val adapter = CardMemberAdapter(context, selectedMembersList, false)
                            holder.itemView.rv_card_selected_members_list.adapter = adapter

                            adapter.setOnClickListener(object: CardMemberAdapter.OnClickListener {
                                override fun onClick() {
                                    if(onClickListener != null) {
                                        onClickListener!!.onClick(position)
                                    }
                                }
                            })
                        }
                    } else {
                        holder.itemView.rv_card_selected_members_list.visibility = View.VISIBLE
                    }
                }
            }

            holder.itemView.setOnClickListener {
                if(onClickListener != null) {
                    onClickListener!!.onClick(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    interface OnClickListener {
        fun onClick(position: Int)
    }
}