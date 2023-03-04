package com.example.kotlinandfirebase

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kotlinandfirebase.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var adapter: DataRecyclerAdapter
    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>
    private val loadingDialog by lazy { LoadingDialog() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startActivityForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == 100) {
                binding.swipeToRefresh.isRefreshing = true
                getData()
            }
        }

        adapter = DataRecyclerAdapter(emptyList(), null)
        binding.recyclerData.setHasFixedSize(true)
        binding.recyclerData.layoutManager = LinearLayoutManager(this)
        binding.recyclerData.adapter = adapter

        getData()

        binding.swipeToRefresh.setOnRefreshListener {
            getData()
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            startActivityForResult.launch(intent)
        }
    }

    private fun getData() {
        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (binding.cPILoading.visibility == View.VISIBLE) {
                    binding.cPILoading.visibility = View.GONE
                }

                if (binding.swipeToRefresh.isRefreshing) {
                    binding.swipeToRefresh.isRefreshing = false
                }

                if (task.isSuccessful) {
                    val dataArrayList = ArrayList<ModelData>()
                    for (document in task.result) {
                        dataArrayList.add(
                            ModelData(
                                document.id,
                                document.data["name"].toString(),
                                document.data["number"].toString(),
                                document.data["address"].toString()
                            )
                        )
                    }
                    adapter = DataRecyclerAdapter(dataArrayList.reversed(), OnClickListener {
                        loadingDialog.show(supportFragmentManager, "")
                        deleteData(it)
                    })
                    binding.recyclerData.adapter = adapter
                } else {
                    Toast.makeText(
                        this,
                        task.exception?.message
                            ?: ("task.isComplete = ${task.isComplete}\n" +
                                    "task.isCanceled = ${task.isCanceled}"),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                binding.cPILoading.visibility = View.GONE
            }
    }

    private fun deleteData(id: String) {
        db.collection("users").document(id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "تم الحذف بنجاح", Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
                adapter.refreshAdapter(id)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                loadingDialog.dismiss()
            }
    }
}