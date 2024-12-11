package com.example.task.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.task.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayoutMediator

class ProfileFragment : Fragment() {
    private val viewModel: ProfileViewModel by viewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ProfilePagerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initLayout()
    }

    private fun initLayout() {

        print("init-layout")
        // Set up the adapter
        adapter = ProfilePagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            println("tab-pos $position")
            tab.text = when (position) {
                0 -> "Applications"
                1 -> "Settings"
                else -> throw IllegalStateException("Invalid tab position")
            }
        }.attach()


        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                println("Page selected: $position")
            }
        })


        // Ensure ViewPager allows user swiping
        binding.viewPager.isUserInputEnabled = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
