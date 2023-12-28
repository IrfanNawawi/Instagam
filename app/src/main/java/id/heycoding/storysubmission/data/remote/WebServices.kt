package id.heycoding.storysubmission.data.remote

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import id.heycoding.storysubmission.BuildConfig
import id.heycoding.storysubmission.data.remote.response.auth.UserLoginResponse
import id.heycoding.storysubmission.data.remote.response.auth.UserRegisterResponse
import id.heycoding.storysubmission.data.remote.response.stories.AddStoryResponse
import id.heycoding.storysubmission.data.remote.response.stories.StoryListResponse
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface WebServices {

    @FormUrlEncoded
    @POST(EndPoint.User.LOGIN)
    fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserLoginResponse>

    @FormUrlEncoded
    @POST(EndPoint.User.REGISTER)
    fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<UserRegisterResponse>

    @GET(EndPoint.Stories.GET_ALL_STORIES)
    fun getAllStories(
        @Header("Authorization") auth: String
    ): Call<StoryListResponse>

    @Multipart
    @POST(EndPoint.Stories.GET_ALL_STORIES)
    fun uploadStory(
        @Header("Authorization") auth: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddStoryResponse>

    @GET(EndPoint.Stories.GET_ALL_STORIES)
    fun getAllStoriesWithLocation(
        @Header("Authorization") auth: String,
        @Query("location") location: Int
    ): Call<StoryListResponse>

    companion object {

        private val gson = GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        fun create(): WebServices {
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .build()
                .create(WebServices::class.java)
        }
    }

    object EndPoint {

        object User {
            const val LOGIN = "login"
            const val REGISTER = "register"
        }

        object Stories {
            const val GET_ALL_STORIES = "stories"
        }
    }
}