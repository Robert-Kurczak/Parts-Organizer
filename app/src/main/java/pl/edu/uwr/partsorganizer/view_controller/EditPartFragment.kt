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
import pl.edu.uwr.partsorganizer.databinding.FragmentEditPartBinding
import pl.edu.uwr.partsorganizer.model.*
class EditPartFragment : Fragment() {
    private lateinit var binding: FragmentEditPartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditPartBinding.inflate(inflater, container, false)
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
            binding.partImageEdit.setImageBitmap(imageBitmap)
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

        //---Setting form---
        binding.partImageEdit.setOnClickListener{
            openCamera()
        }

        val database = DataProvider.getDatabase(view.context)

        val partID = arguments?.getInt("partID")!!
        val part = database.PartsDao().selectByID(partID)

        val types = database.TypesDao().selectAll()
        val drawers = database.DrawerDao().selectAll()

        val typesMap = mutableMapOf<String, Int>()
        val drawersMap = mutableMapOf<String, Int>()

        var typesSelectionID = 0
        for((index, type) in types.withIndex()) {
            typesMap[type.Name] = type.typeID

            if(type.typeID == part.Part.PartTypeID){
                typesSelectionID = index
            }
        }

        var drawerSelectionID = 0
        for((index, drawer) in drawers.withIndex()){
            val key = drawer.Column.toString() + "x" + drawer.Row.toString()
            drawersMap[key] = drawer.drawerID

            if(drawer.drawerID == part.Part.PartDrawerID){
                drawerSelectionID = index
            }
        }

        val typeSpinner = binding.partTypeEdit
        val drawerSpinner = binding.drawerPositionEdit

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

        binding.partNameEdit.setText(part.Part.Name)
        binding.partDescriptionEdit.setText(part.Part.Description)
        binding.partImageEdit.setImageBitmap(ImageProcessor.getImage(part.Part.Image))
        binding.partAmountEdit.setText(part.Part.amount.toString())
        typeSpinner.setSelection(typesSelectionID)
        drawerSpinner.setSelection(drawerSelectionID)

        binding.submitPartButton.setOnClickListener{
            part.Part.Name = binding.partNameEdit.text.toString()
            part.Part.Description = binding.partDescriptionEdit.text.toString()
            part.Part.Image = ImageProcessor.getBytes(binding.partImageEdit.drawable.toBitmap())
            part.Part.amount = Integer.parseInt(binding.partAmountEdit.text.toString())
            part.Type.typeID = typesMap[binding.partTypeEdit.selectedItem.toString()]!!
            part.Part.PartDrawerID = drawersMap[binding.drawerPositionEdit.selectedItem.toString()]!!

            lifecycleScope.launch{
                database.PartsDao().updatePart(part.Part)
            }

            view.findNavController().navigate(R.id.mainFragment)
        }

        binding.deletePartButton.setOnClickListener{
            lifecycleScope.launch{
                database.PartsDao().deletePart(part.Part)

                view.findNavController().navigate(R.id.mainFragment)
            }
        }
    }
}