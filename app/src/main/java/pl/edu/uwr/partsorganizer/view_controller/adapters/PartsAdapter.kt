package pl.edu.uwr.partsorganizer.view_controller.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import pl.edu.uwr.partsorganizer.R
import pl.edu.uwr.partsorganizer.databinding.FragmentPartsBinding
import pl.edu.uwr.partsorganizer.model.ImageProcessor
import pl.edu.uwr.partsorganizer.model.Parts

class PartsViewHolder(
    private val binding: FragmentPartsBinding,
    private val showDrawerInfo: Boolean
): RecyclerView.ViewHolder(binding.root){
    fun bind(item: Parts){
        binding.partImage.setImageBitmap(ImageProcessor.getImage(item.Part.Image))
        binding.partName.text = item.Part.Name
        binding.partDescription.text = item.Part.Description
        binding.partType.text = item.Type.Name
        binding.partAmount.text = item.Part.amount.toString()

        if(showDrawerInfo){
            binding.drawerPositionLabel.visibility = View.VISIBLE
            binding.drawerPosition.visibility = View.VISIBLE

            val drawerPosition: String = item.Drawer.Column.toString() + "x" + item.Drawer.Row.toString()
            binding.drawerPosition.text = drawerPosition
        }
    }
}

class PartsComparator : DiffUtil.ItemCallback<Parts>() {
    override fun areItemsTheSame(oldItem: Parts, newItem: Parts): Boolean {
        return oldItem.Part.partID == newItem.Part.partID
    }

    override fun areContentsTheSame(oldItem: Parts, newItem: Parts): Boolean {
        return oldItem == newItem
    }
}

class PartsAdapter(itemComparator: PartsComparator, private val showDrawerInfo: Boolean = false)
    : ListAdapter<Parts, PartsViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartsViewHolder {
        return PartsViewHolder(
            FragmentPartsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            showDrawerInfo
        )
    }

    override fun onBindViewHolder(holder: PartsViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener{ view ->
            val bundle = bundleOf("partID" to item.Part.partID)

            view.findNavController().navigate(R.id.editPartFragment, bundle)
        }
    }
}