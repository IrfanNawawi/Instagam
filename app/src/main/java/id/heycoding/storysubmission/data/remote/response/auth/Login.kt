package id.heycoding.storysubmission.data.remote.response.auth

import com.google.gson.annotations.SerializedName

data class UserLoginResult(
    @field:SerializedName("name")
    var name: String,

    @field:SerializedName("token")
    var token: String,

    @field:SerializedName("userId")
    var userId: String
)

data class UserLoginResponse(
    @field:SerializedName("error")
    var error: Boolean,

    @field:SerializedName("loginResult")
    var loginResult: UserLoginResult,

    @field:SerializedName("message")
    var message: String
)