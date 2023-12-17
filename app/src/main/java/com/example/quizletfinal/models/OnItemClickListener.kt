package com.example.quizletfinal.models

interface OnItemClickListener {
    fun onItemClickListener(topic: Topic)
    fun onItemClickListener(folder: Folder)
    fun onItemClickListener(card: Card)
    fun onItemLongClickListener(card: Card)
    fun onItemDeleteListener(topic: Topic)
    fun onItemMoveListener(topic: Topic)
}