package com.example.kimhun.thewalker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*



/**
 * Created by Hp on 2017-09-13.
 */
class ItemsListViewAdapter(val context : Context?, val itemsList : ArrayList<ItemsListItem>) : BaseAdapter() {
    private lateinit var mAuth: FirebaseAuth
    private var selectedPosition : Int = 0

    override fun getCount() = itemsList.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        //val context = parent?.context
        var view = convertView

        if(convertView == null) {
            var inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.itemslist_item, parent, false);
        }

        // 화면에 표시될 view로부터 위젯에 대한 참조 획득
        val shoesImageView = view?.findViewById(R.id.shoes_img) as ImageView
        val nameTextView = view.findViewById(R.id.shoes_name) as TextView
        val costTextView = view.findViewById(R.id.shoes_cost) as TextView
        val contextTextView = view.findViewById(R.id.shoes_context) as TextView
        val buyButton = view.findViewById(R.id.buyBtn) as ImageButton

        val item = itemsList[position]

        // 위젯에 데이터 반영
        shoesImageView.setImageDrawable(item.itemImage) //item.getItemImage()
        nameTextView.text = item.itemName //item.getItemName()
        costTextView.text = item.itemCost.toString() //item.getItemCost().toString()
        contextTextView.text = item.itemContext //item.getItemContext()
        buyButton.setOnClickListener{
            mAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth.currentUser
            val userId = currentUser!!.email
            val index = userId!!.indexOf("@")
            val path = userId.substring(0,index)
            val DBinfoRef = FirebaseDatabase.getInstance().getReference("/info/" + path)

            DBinfoRef.child("shoes").setValue(position)
            val intent = Intent(context,ShopActivity::class.java)
            intent.putExtra("shoes",position)
            (context as Activity).setResult(1,intent)
            context.finish()
            Log.d("shoes","shoesPosition is " + position)

            /*
            var alertDialogBuilder : AlertDialog.Builder = AlertDialog.Builder(view!!.context)
            alertDialogBuilder.setMessage(itemsList[position].getFriendName() + "님을 삭제 하시겠습니까?").setCancelable(false).setPositiveButton("확인",
                    DialogInterface.OnClickListener { dialog, which ->
                        FirebaseDatabase.getInstance().getReference("/friends/" + path + "/" + itemsList[position].getFriendName()).removeValue()
                        var intent = Intent(view!!.context, FriendsActivity::class.java)
                        view!!.context.startActivity(intent)
                        (context as Activity).finish()

                    }).setNegativeButton("취소",
                    DialogInterface.OnClickListener { dialog, which ->

                    })
            val alert = alertDialogBuilder.create()
            alert.show()
            */

        }

        return view
    }

    override fun getItem(position: Int): Any {
        return itemsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position as Long
    }

    fun addItem(image : Drawable, name : String, cost : Int, context : String, ability : Int) {
        val item = ItemsListItem(image,name,cost,context,ability)

        itemsList.add(item)
    }

}