package pl.edu.uwr.partsorganizer.view_controller

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.launch
import pl.edu.uwr.partsorganizer.R
import pl.edu.uwr.partsorganizer.databinding.FragmentMainListBinding
import pl.edu.uwr.partsorganizer.model.DataProvider
import pl.edu.uwr.partsorganizer.model.Drawer
import pl.edu.uwr.partsorganizer.model.ImageProcessor
import pl.edu.uwr.partsorganizer.model.PartsItem
import pl.edu.uwr.partsorganizer.view_controller.adapters.MainAdapter
import pl.edu.uwr.partsorganizer.view_controller.adapters.MainAdapterComparator

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainListBinding

    private fun setupRecyclerView(mainAdapter: MainAdapter){
        binding.mainRecyclerView.apply {
            adapter = mainAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val adapter = MainAdapter(MainAdapterComparator())
        val database = DataProvider.getDatabase(view.context)

        val data = database.DrawerDao().selectAll()

        setupRecyclerView(adapter)
        adapter.submitList(data)

        binding.searchButton.setOnClickListener{
            val bundle = bundleOf("searchName" to binding.searchText.text.toString())

            view.findNavController().navigate(R.id.searchedPartsFragment, bundle)
        }

        binding.addPartButton.setOnClickListener{
            view.findNavController().navigate(R.id.addPartFragment)
        }
    }
}