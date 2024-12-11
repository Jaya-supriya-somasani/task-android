import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.task.databinding.FragmentApplicationsBinding

class ApplicationsFragment : Fragment() {
    private lateinit var binding: FragmentApplicationsBinding

    private val viewModel: ApplicationViewModel by viewModels()
    private lateinit var adapter: ApplicationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentApplicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ApplicationsAdapter(emptyList())
        binding.recyclerView.adapter = adapter

        viewModel.filteredAppList.observe(viewLifecycleOwner) { filteredList ->
            adapter.updateList(filteredList)
            if (filteredList.isEmpty()) {
                binding.noDataText.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.GONE
            } else {
                binding.noDataText.visibility = View.GONE
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterAppList(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }
}