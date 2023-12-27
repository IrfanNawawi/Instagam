package id.heycoding.storysubmission.data.remote.response.stories

import com.google.gson.annotations.SerializedName

data class StoryItem(
    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String
)