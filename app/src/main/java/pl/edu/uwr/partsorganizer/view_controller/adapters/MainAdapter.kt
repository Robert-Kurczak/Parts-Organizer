package pl.edu.uwr.partsorganizer.view_controller.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import pl.edu.uwr.partsorganizer.R
import pl.edu.uwr.partsorganizer.databinding.FragmentMainBinding
import pl.edu.uwr.partsorganizer.model.Drawer

class MainViewHolder(
    private val binding: FragmentMainBinding
): RecyclerView.ViewHolder(binding.root){

    fun bind(item: Drawer){
        val label = 4 * (item.Row - 1) + (item.Column - 1) + 1
        binding.drawerLabel.text = label.toString()
    }
}

class MainAdapterComparator : DiffUtil.ItemCallback<Drawer>() {
    override fun areItemsTheSame(oldItem: Drawer, newItem: Drawer): Boolean {
        return oldItem.drawerID == newItem.drawerID
    }

    override fun areContentsTheSame(oldItem: Drawer, newItem: Drawer): Boolean {
        return oldItem == newItem
    }
}

class MainAdapter(itemComparator: MainAdapterComparator)
    : ListAdapter<Drawer, MainViewHolder>(itemComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        return MainViewHolder(
            FragmentMainBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)

        holder.itemView.setOnClickListener{ view ->
            val bundle = bundleOf("drawerID" to item.drawerID)

            view.findNavController().navigate(R.id.drawerContentFragment, bundle)
        }
    }
}