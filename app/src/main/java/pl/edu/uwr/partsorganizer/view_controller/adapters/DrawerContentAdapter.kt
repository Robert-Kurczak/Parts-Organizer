package pl.edu.uwr.partsorganizer.view_controller.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import pl.edu.uwr.partsorganizer.databinding.FragmentDrawerContentBinding
import pl.edu.uwr.partsorganizer.model.ImageProcessor
import pl.edu.uwr.partsorganizer.model.Parts

class DrawerContentViewHolder(
    private val binding: FragmentDrawerContentBinding
): RecyclerView.ViewHolder(binding.root){
    fun bind(item: Parts){
        binding.partImage.setImageBitmap(ImageProcessor.getImage(item.Part.Image))
        binding.partName.text = item.Part.Name
        binding.partDescription.text = item.Part.Description
        binding.partType.text = item.Type.Name
        binding.partAmount.text = item.Part.amount.toString()
    }
}

class DrawerContentComparator : DiffUtil.ItemCallback<Parts>() {
    override fun areItemsTheSame(oldItem: Parts, newItem: Parts): Boolean {
        return oldItem.Part.partID == newItem.Part.partID
    }

    override fun areContentsTheSame(oldItem: Parts, newItem: Parts): Boolean {
        return oldItem == newItem
    }
}

class DrawerContentAdapter(itemComparator: DrawerContentComparator)
    : ListAdapter<Parts, DrawerContentViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawerContentViewHolder {
        return DrawerContentViewHolder(
            FragmentDrawerContentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: DrawerContentViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}