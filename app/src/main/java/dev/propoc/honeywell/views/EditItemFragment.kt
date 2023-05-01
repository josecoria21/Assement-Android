package dev.propoc.honeywell.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.skydoves.colorpickerview.listeners.ColorListener
import dev.propoc.honeywell.R
import dev.propoc.honeywell.databinding.FragmentEditBinding
import dev.propoc.honeywell.model.Items
import dev.propoc.honeywell.viewmodel.ListViewModel

class EditItemFragment : Fragment() {

    lateinit var binding: FragmentEditBinding
    private val viewModel: ListViewModel by activityViewModels()

    var colorSelected: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false)

        val position = EditItemFragmentArgs.fromBundle(requireArguments()).position

        val item = viewModel.itemsList.value?.get(position.toInt())

        binding.apply {
            selectImage.setOnClickListener {
                openGalleryForImage()
            }

            colorPickerView.setColorListener(object : ColorListener {
                override fun onColorSelected(color: Int, fromUser: Boolean) {
                    binding.colorPicked.text = String.format("#%06X", (color))
                    colorSelected = color
                }
            })

            editCta.setOnClickListener {
                val name = if (itemName.text.toString() != "") {
                    itemName.text.toString()
                } else {
                    ""
                }
                val imageAdded = selectedImage.drawable.toBitmapOrNull()

                imageAdded?.let { Items(name, imageAdded, colorSelected) }
                    ?.let { it2 -> viewModel.updateItem(it2, position) }

                Toast.makeText(context, "Item Modified", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editItemFragment_to_listFragment)
            }
        }

        if (item != null) {
            setValues(item)
        }

        return binding.root
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, AddNewItemFragment.REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == AddNewItemFragment.REQUEST_CODE) {
            binding.selectedImage.setImageURI(data?.data)
        }
    }

    private fun setValues(item: Items) {
        binding.itemName.setText(item.name)
        binding.selectedImage.setImageBitmap(item.image)
        binding.colorPicked.text = String.format("#%06X", (item.colorCoding))
    }
}