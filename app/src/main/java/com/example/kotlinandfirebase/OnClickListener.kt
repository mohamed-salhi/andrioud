package com.example.kotlinandfirebase

class OnClickListener(val clickListener: (id: String) -> Unit) {
    fun onClick(id: String) = clickListener(id)
}