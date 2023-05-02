package dev.propoc.honeywell.views

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.hardware.camera2.CameraManager
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
import dev.propoc.honeywell.databinding.FragmentAddNewItemBinding
import dev.propoc.honeywell.model.Items
import dev.propoc.honeywell.viewmodel.ListViewModel

class AddNewItemFragment : Fragment() {

    lateinit var binding: FragmentAddNewItemBinding
    private val viewModel: ListViewModel by activityViewModels()

    var colorSelected: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddNewItemBinding.inflate(inflater, container, false)
        val byteArray = arguments?.getByteArray("imageByteArray")

        if (byteArray != null) {
            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            binding.selectedImage.setImageBitmap(bitmap)
        }

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

            takePhoto.setOnClickListener {
                findNavController().navigate(R.id.action_addNewItemFragment_to_permissionsFragment)
            }

            actionButton.setOnClickListener {
                val name = if (itemName.text.toString() != "") {
                    itemName.text.toString()
                } else {
                    ""
                }
                val imageAdded = selectedImage.drawable.toBitmapOrNull()

                imageAdded?.let { Items(name, imageAdded, colorSelected) }
                    ?.let { it2 -> viewModel.addNewItem(it2) }
                Toast.makeText(context, "Item Added", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addNewItemFragment_to_listFragment)
            }
        }
        return binding.root
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            binding.selectedImage.setImageURI(data?.data)
        }
    }

    companion object {
        const val REQUEST_CODE = 100
    }
}