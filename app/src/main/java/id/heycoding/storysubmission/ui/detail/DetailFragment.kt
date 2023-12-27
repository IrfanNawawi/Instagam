package id.heycoding.storysubmission.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import id.heycoding.storysubmission.MainActivity
import id.heycoding.storysubmission.databinding.FragmentDetailBinding

class DetailFragment : Fragment() {

    private var fragmentDetailBinding: FragmentDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentDetailBinding = FragmentDetailBinding.inflate(layoutInflater)
        return fragmentDetailBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).supportActionBar?.hide()

        showDetail()
    }

    private fun showDetail() {
        val nameDetail = DetailFragmentArgs.fromBundle(arguments as Bundle).name
        val descDetail = DetailFragmentArgs.fromBundle(arguments as Bundle).desc
        val imgDetail = DetailFragmentArgs.fromBundle(arguments as Bundle).image

        fragmentDetailBinding?.apply {
            tvStoryDetailDesc.text = descDetail
            tvStoryTitle.text = nameDetail
            Glide.with(requireContext())
                .load(imgDetail)
                .into(storyImage)
        }

        setupAccessibility(nameDetail, descDetail, imgDetail)
    }

    private fun setupAccessibility(nameDetail: String, descDetail: String, imgDetail: String) {
        fragmentDetailBinding?.apply {
            tvStoryTitle.contentDescription = nameDetail
            tvStoryDetailDesc.contentDescription = descDetail
            storyImage.contentDescription = imgDetail
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentDetailBinding = null
    }
}