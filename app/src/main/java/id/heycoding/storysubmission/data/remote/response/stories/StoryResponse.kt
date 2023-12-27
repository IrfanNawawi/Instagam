package id.heycoding.storysubmission.data.remote.response.stories

import com.google.gson.annotations.SerializedName

data class StoryListResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("listStory")
    val listStory: List<StoryItem>
)

data class AddStoryResponse(
    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)