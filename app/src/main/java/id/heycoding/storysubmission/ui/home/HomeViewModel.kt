package id.heycoding.storysubmission.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.heycoding.storysubmission.data.remote.WebServices
import id.heycoding.storysubmission.data.remote.response.stories.StoryItem
import id.heycoding.storysubmission.data.remote.response.stories.StoryListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {
    private val _listStoryData = MutableLiveData<List<StoryItem>>()
    val listStoryData: LiveData<List<StoryItem>> = _listStoryData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()

    private val services = WebServices.create()

    fun getAllStoriesData(auth: String) {
        _isLoading.value = true
        services.getAllStories("Bearer $auth")
            .enqueue(object : Callback<StoryListResponse> {
                override fun onResponse(
                    call: Call<StoryListResponse>,
                    response: Response<StoryListResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _listStoryData.postValue(response.body()?.listStory)
                        _message.postValue(response.body()?.message)
                    } else {
                        _message.postValue(response.message())
                    }


                }

                override fun onFailure(call: Call<StoryListResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = t.message
                }

            })
    }
}