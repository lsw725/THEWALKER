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
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*



/**
 * Created by Hp on 2017-09-13.
 */
class ItemsListViewAdapter(val context : Context?, val itemsList : ArrayList<ItemsListItem>, val nowShoes : Int, val money : Int) : BaseAdapter() {
    private lateinit var mAuth: FirebaseAuth
    private var selectedPosition : Int = 0

    override fun getCount() = itemsList.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
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
            val alertDialogBuilder : AlertDialog.Builder = AlertDialog.Builder(context)
            if (nowShoes >= position) {
                alertDialogBuilder.setTitle("아이템 구매")
                alertDialogBuilder.setMessage("이미 구매하셨습니다.").setCancelable(true)
                alertDialogBuilder.show()
            } else {
                if (money >= itemsList[position].itemCost!!) {
                    mAuth = FirebaseAuth.getInstance()
                    val currentUser = mAuth.currentUser
                    val userId = currentUser!!.email
                    val index = userId!!.indexOf("@")
                    val path = userId.substring(0, index)
                    val DBinfoRef = FirebaseDatabase.getInstance().getReference("/info/" + path)

                    DBinfoRef.child("shoes").setValue(position)
                    Toast.makeText(context,"${itemsList[position].itemName}을(를) 구매하셨습니다.",Toast.LENGTH_SHORT)
                    val intent = Intent(context, ShopActivity::class.java)
                    intent.putExtra("money", money - itemsList[position].itemCost!!)
                    intent.putExtra("shoes", position)
                    (context as Activity).setResult(1, intent)
                    context.finish()
                    Log.d("shoes", "shoesPosition is " + position)
                } else {
                    alertDialogBuilder.setTitle("아이템 구매")
                    alertDialogBuilder.setMessage("돈이 부족합니다.").setCancelable(true)
                    alertDialogBuilder.show()
                }
            }
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return itemsList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun addItem(image : Drawable, name : String, cost : Int, context : String, ability : Int) {
        val item = ItemsListItem(image,name,cost,context,ability)

        itemsList.add(item)
    }

}