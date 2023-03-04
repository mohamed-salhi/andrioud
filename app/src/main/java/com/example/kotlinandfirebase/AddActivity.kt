package com.example.kotlinandfirebase

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinandfirebase.databinding.ActivityAddBinding
import com.google.firebase.firestore.FirebaseFirestore

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buSave.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val number = binding.etNumber.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()

            if (name.isNotEmpty() && number.isNotEmpty() && address.isNotEmpty()) {
                addData(name, number, address)
            } else {
                Toast.makeText(this, "الرجاء ادخال البيانات", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addData(name: String, number: String, address: String) {
        val loadingDialog = LoadingDialog()
        loadingDialog.show(supportFragmentManager, "")

        val userInfo = mutableMapOf<String, String>()
        userInfo["name"] = name
        userInfo["number"] = number
        userInfo["address"] = address

        db.collection("users")
            .add(userInfo)
            .addOnSuccessListener {
//                        documentReference.id
                loadingDialog.dismiss()
                setResult(100)
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
    }
}