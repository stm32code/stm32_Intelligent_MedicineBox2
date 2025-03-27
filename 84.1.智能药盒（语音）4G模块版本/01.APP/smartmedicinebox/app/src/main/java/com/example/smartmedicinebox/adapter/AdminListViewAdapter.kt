package com.example.smartmedicinebox.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.smartmedicinebox.R
import com.example.smartmedicinebox.databinding.ListAdminItemBinding
import com.example.smartmedicinebox.db.UserDao
import com.example.smartmedicinebox.entity.User
import com.example.smartmedicinebox.utils.MToast


class AdminListViewAdapter(
    private val context: Context,
    private var listData: MutableList<Any>
) :
    BaseAdapter() {
    private val dao = UserDao(context)
    override fun getCount(): Int {
        return listData.size
    }

    override fun getItem(i: Int): Any {
        return listData[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(i: Int, p0: View?, viewGroup: ViewGroup): View {
        var view = p0
        val holder: ViewHolder
        if (view == null) {
            val binding = ListAdminItemBinding.inflate(
                LayoutInflater.from(
                    context
                ), viewGroup, false
            )
            view = binding.root
            holder = ViewHolder(binding)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }


        initView(holder.binding, i)


        return view
    }

    private fun initView(binding: ListAdminItemBinding, index: Int) {
        val user: User = listData[index] as User
        binding.userID.text = java.lang.String.valueOf(user.uid)
        binding.name.text = user.uname
        binding.password.text = user.upassword
        binding.phone.text = user.phone

        binding.deleteBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("请确认").setNegativeButton("确认") { p0, p1 ->
                dao.delete(user.uid.toString())
                listData.removeAt(index)
                notifyDataSetChanged()
                builder.create().dismiss()
            }.setPositiveButton("取消") { p0, p1 ->
                builder.create().dismiss()
            }
            builder.create().show()
        }
        binding.updateBtn.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val view = View.inflate(context, R.layout.dialog_update_user, null)
            builder.setView(view)
            val nameText = view.findViewById<TextView>(R.id.updateName)
            val passwordText = view.findViewById<EditText>(R.id.updatePassword)
            val phoneText = view.findViewById<EditText>(R.id.updatePhone)
            val submitBtn = view.findViewById<Button>(R.id.submitBtn)
            val cancelBtn = view.findViewById<Button>(R.id.cancelBtn)
            nameText.text = user.uname
            passwordText.setText(user.upassword)
            phoneText.setText(user.phone)
            val dialog = builder.create()
            submitBtn.setOnClickListener {
                val name = nameText.text.toString()
                val password = passwordText.text.toString()
                val phone = phoneText.text.toString()
                if (name.isEmpty() || password.isEmpty() || phone.isEmpty()) {
                    MToast.mToast(context, "信息不能为空")
                } else {
                    user.upassword = password
                    user.phone = phone
                    dao.update(user, user.uid.toString())
                    dialog.dismiss()
                }
            }

            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()

        }
    }


    class ViewHolder(var binding: ListAdminItemBinding)
}
