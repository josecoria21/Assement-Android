package dev.propoc.honeywell.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import dev.propoc.honeywell.R
import dev.propoc.honeywell.databinding.FragmentCameraBinding
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFragment : Fragment() {

    private lateinit var binding: FragmentCameraBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraSelector: CameraSelector
    private var imageCapture: ImageCapture? = null
    private lateinit var imgCaptureExecutor: ExecutorService
    private val cameraPermissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { permissionGranted ->
            if (permissionGranted) {
                startCamera()
            } else {
                Snackbar.make(
                    binding.root,
                    "The camera permission is necessary",
                    Snackbar.LENGTH_INDEFINITE
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraProviderFuture = this.context?.let { ProcessCameraProvider.getInstance(it) } as ListenableFuture<ProcessCameraProvider>
        cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        imgCaptureExecutor = Executors.newSingleThreadExecutor()

        cameraPermissionResult.launch(android.Manifest.permission.CAMERA)

        binding.capturePhoto.setOnClickListener {
            takePhoto()
        }
    }

    private fun startCamera() {
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
        }

        this.context?.let { ContextCompat.getMainExecutor(it) }?.let {
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                imageCapture = ImageCapture.Builder().build()

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                } catch (e: Exception) {
                    Log.d("CameraFragment", "Use case binding failed")
                }
            }, it)
        }
    }

    private fun takePhoto() {
        imageCapture?.let {
            context?.let { it1 -> ContextCompat.getMainExecutor(it1) }?.let { it2 ->
                it.takePicture(
                    it2,
                    @ExperimentalGetImage
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {

                            val buffer = image.planes[0].buffer
                            val bytes = ByteArray(buffer.remaining())
                            buffer.get(bytes)

                            val bundle = Bundle().apply {
                                putByteArray("imageByteArray", bytes)
                            }
                            findNavController().navigate(R.id.action_cameraFragment_to_addNewItemFragment, bundle)
                            image.close()
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Toast.makeText(
                                binding.root.context,
                                "Error taking photo",
                                Toast.LENGTH_LONG
                            ).show()
                            Log.d("CameraFragment", "Error taking photo:$exception")
                        }
                    })
            }
        }
    }
}