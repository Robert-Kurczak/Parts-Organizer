package pl.edu.uwr.partsorganizer.view_controller

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import kotlinx.coroutines.launch
import pl.edu.uwr.partsorganizer.R
import pl.edu.uwr.partsorganizer.databinding.FragmentAddPartBinding
import pl.edu.uwr.partsorganizer.model.*
class AddPartFragment : Fragment() {
    private lateinit var binding: FragmentAddPartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPartBinding.inflate(inflater, container, false)
        return binding.root
    }

    //------Camera------

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it){ launchCamera() }
    }

    private val resultLauncherCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.partImageAdd.setImageBitmap(imageBitmap)
        }
    }

    private fun openCamera(){
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.CAMERA) -> {
                showMessageOKCancel("Allow access")
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun launchCamera(){
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        resultLauncherCamera.launch(intent)
    }

    private fun showMessageOKCancel(message: String) {
        AlertDialog.Builder(requireContext())
            .setMessage(message)
            .setPositiveButton("OK") { dialogInterface: DialogInterface, _: Int ->
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    //------------

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        //---Setting empty form---
        binding.partImageAdd.setOnClickListener{
            openCamera()
        }

        val database = DataProvider.getDatabase(view.context)

        val types = database.TypesDao().selectAll()
        val drawers = database.DrawerDao().selectAll()

        val typesMap = mutableMapOf<String, Int>()
        val drawersMap = mutableMapOf<String, Int>()

        for(type in types) typesMap[type.Name] = type.typeID
        for(drawer in drawers){
            val key = drawer.Column.toString() + "x" + drawer.Row.toString()
            drawersMap[key] = drawer.drawerID
        }

        val typeSpinner = binding.partTypeAdd
        val drawerSpinner = binding.drawerPositionAdd

        val typeSpinnerAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            typesMap.keys.toList()
        )
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        typeSpinner.adapter = typeSpinnerAdapter

        val drawerSpinnerAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            drawersMap.keys.toList()
        )
        drawerSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        drawerSpinner.adapter = drawerSpinnerAdapter

        //------

        binding.submitPartButton.setOnClickListener{
            val partName = binding.partNameAdd.text.toString()
            val partDescription = binding.partDescriptionAdd.text.toString()
            val partImage = ImageProcessor.getBytes(binding.partImageAdd.drawable.toBitmap())
            val partAmount = Integer.parseInt(binding.partAmountAdd.text.toString())
            val partTypeID = typesMap[binding.partTypeAdd.selectedItem.toString()]!!
            val drawerID = drawersMap[binding.drawerPositionAdd.selectedItem.toString()]!!

            lifecycleScope.launch{
                database.PartsDao().insertPart(
                    PartsItem(
                        0,
                        partName,
                        partDescription,
                        partImage,
                        partAmount,
                        partTypeID,
                        drawerID
                    )
                )
            }

            view.findNavController().navigate(R.id.mainFragment)
        }
    }
}