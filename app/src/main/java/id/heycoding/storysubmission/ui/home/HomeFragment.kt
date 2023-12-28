package id.heycoding.storysubmission.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import id.heycoding.storysubmission.MainActivity
import id.heycoding.storysubmission.R
import id.heycoding.storysubmission.databinding.FragmentHomeBinding
import id.heycoding.storysubmission.utils.Preferences

class HomeFragment : Fragment() {
    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeStoryAdapter: HomeStoryAdapter
    private lateinit var userLoginPref: Preferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentHomeBinding = FragmentHomeBinding.inflate(layoutInflater)
        return fragmentHomeBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userLoginPref = Preferences(requireContext())
        (activity as MainActivity).supportActionBar?.apply {
            title = "INSTAGAM"
            show()
        }
        homeStoryAdapter = HomeStoryAdapter()
        setupView()
    }

    private fun setupView() {
        fragmentHomeBinding?.apply {
            rvStory.apply {
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = homeStoryAdapter
            }

            fabAddStory.setOnClickListener {
                findNavController().navigate(
                    R.id.action_homeFragment_to_addStoryFragment
                )
            }
        }
        bindObservers()
    }

    private fun bindObservers() {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        homeViewModel.apply {
            if (userLoginPref.getLoginData().isLogin) {
                getAllStoriesData(userLoginPref.getLoginData().token)
            } else {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }

            listStoryData.observe(requireActivity()) { listStory ->
                if (listStory != null) {
                    homeStoryAdapter.setStoryData(listStory)
                }
            }
            isLoading.observe(requireActivity()) {
                showLoading(it)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        fragmentHomeBinding?.apply {
            if (isLoading) {
                pgShimmerHome.startShimmer()
                pgShimmerHome.visibility = View.VISIBLE
            } else {
                pgShimmerHome.stopShimmer()
                pgShimmerHome.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bindObservers()
    }

    override fun onDetach() {
        super.onDetach()
        fragmentHomeBinding = null
    }
}