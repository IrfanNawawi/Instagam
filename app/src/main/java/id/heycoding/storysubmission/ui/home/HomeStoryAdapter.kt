package id.heycoding.storysubmission.ui.home

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.heycoding.storysubmission.data.remote.response.stories.StoryItem
import id.heycoding.storysubmission.databinding.StoryItemBinding

class HomeStoryAdapter : RecyclerView.Adapter<HomeStoryAdapter.ViewHolder>() {
    private val listStoryData = ArrayList<StoryItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listStoryData[position])
    }

    override fun getItemCount(): Int = listStoryData.size

    inner class ViewHolder(private val binding: StoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: StoryItem) {
            binding.apply {
                Glide.with(itemView.context)
                    .load(story.photoUrl)
                    .into(storyImage)

                tvStoryTitle.text = story.name
                tvStoryDesc.text = story.description

                tvStoryTitle.contentDescription = story.name
                tvStoryDesc.contentDescription = story.description
                storyImage.contentDescription = story.photoUrl

                storyLayoutRoot.setOnClickListener {
                    val toDetailStory = HomeFragmentDirections.actionHomeFragmentToDetailFragment()
                    toDetailStory.name = story.name
                    toDetailStory.desc = story.description
                    toDetailStory.image = story.photoUrl

                    val optionsCompat: ActivityOptionsCompat =
                        ActivityOptionsCompat.makeSceneTransitionAnimation(
                            itemView.context as Activity,
                            Pair(storyImage, "image"),
                            Pair(tvStoryTitle, "title"),
                            Pair(tvStoryDesc, "description"),
                        )

                    val extras = ActivityNavigatorExtras(optionsCompat)

                    itemView.findNavController().navigate(
                        toDetailStory, extras
                    )
                }
            }
        }
    }

    fun setStoryData(story: List<StoryItem>) {
        val diffCallback = HomeStoryCallback(listStoryData, story)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        listStoryData.clear()
        listStoryData.addAll(story)
        diffResult.dispatchUpdatesTo(this)
    }
}