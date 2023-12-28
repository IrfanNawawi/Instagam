package id.heycoding.storysubmission.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.heycoding.storysubmission.data.remote.WebServices
import id.heycoding.storysubmission.data.remote.response.stories.StoryItem
import id.heycoding.storysubmission.data.remote.response.stories.StoryListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by Irfan Nawawi on 28/12/23.
 * heycoding.tech
 * heycoding@gmail.com
 */
class MapsViewModel: ViewModel() {
    private val _listMapsStoryData = MutableLiveData<List<StoryItem>>()
    val listMapsStoryData: LiveData<List<StoryItem>> = _listMapsStoryData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()

    private val services = WebServices.create()

    fun getAllMapsStoriesData(auth: String) {
        _isLoading.value = true
        services.getAllStoriesWithLocation("Bearer $auth", 1).enqueue(object :
            Callback<StoryListResponse> {
            override fun onResponse(
                call: Call<StoryListResponse>,
                response: Response<StoryListResponse>
            ) {
               _isLoading.value = false
               if (response.isSuccessful) {
                   _listMapsStoryData.postValue(response.body()?.listStory)
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