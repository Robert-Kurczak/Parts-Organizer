package pl.edu.uwr.partsorganizer.view_controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import pl.edu.uwr.partsorganizer.databinding.FragmentPartsListBinding
import pl.edu.uwr.partsorganizer.model.*
import pl.edu.uwr.partsorganizer.view_controller.adapters.PartsAdapter
import pl.edu.uwr.partsorganizer.view_controller.adapters.PartsComparator

class DrawerContentFragment : Fragment(){
    private lateinit var binding: FragmentPartsListBinding

    private fun setupRecyclerView(partsAdapter: PartsAdapter){
        binding.drawerContentRecyclerView.apply {
            adapter = partsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPartsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        val adapter = PartsAdapter(PartsComparator())
        val drawerID = arguments?.getInt("drawerID")!!
        val database = DataProvider.getDatabase(view.context)

        val data = database.PartsDao().selectInDrawer(drawerID)

        setupRecyclerView(adapter)
        adapter.submitList(data)
    }
}