package id.heycoding.storysubmission.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.heycoding.storysubmission.data.remote.WebServices
import id.heycoding.storysubmission.data.remote.response.stories.AddStoryResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Created by Irfan Nawawi on 26/12/23.
 * heycoding.tech
 * heycoding@gmail.com
 */
class AddStoryViewModel : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    private val services = WebServices.create()

    fun uploadStoriesData(
        auth: String,
        imageMultipart: MultipartBody.Part,
        description: RequestBody
    ) {
        services.uploadStory("Bearer $auth", imageMultipart, description)
            .enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            _message.postValue(responseBody.message)
                        } else {
                            _message.postValue(response.message())
                        }
                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _message.postValue(t.message.toString())
                }

            })
    }
}