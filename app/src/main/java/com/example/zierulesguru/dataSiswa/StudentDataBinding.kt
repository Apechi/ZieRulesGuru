package com.example.zierulesguru.dataSiswa

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.zierulesguru.R
import com.example.zierulesguru.databinding.StudentlistLayoutItemBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem

class StudentDataBinding : AbstractBindingItem<StudentlistLayoutItemBinding>() {
    var nis: String? = null
    var name: String? = null
    var password: String? = null
    var image: String? = null

    override val type: Int
        get() = R.id.studentLayoutItem

    override fun bindView(binding: StudentlistLayoutItemBinding, payloads: List<Any>) {
        binding.name.text = name
        binding.nis.text = nis
        binding.password.text = password
        Glide.with(binding.photoProfile.context)
            .load(image).into(binding.photoProfile)
    }

    override fun createBinding(
        inflater: LayoutInflater,
        parent: ViewGroup?
    ): StudentlistLayoutItemBinding {
        return StudentlistLayoutItemBinding.inflate(inflater, parent, false)
    }
}