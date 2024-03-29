package com.vechkanov.drive64.data.repositories

import com.google.gson.Gson
import com.vechkanov.drive64.data.dtoModels.UserVK
import com.vk.sdk.api.VKApi
import com.vk.sdk.api.VKError
import com.vk.sdk.api.VKRequest
import com.vk.sdk.api.VKResponse
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class VKNetworkRepository
@Inject constructor(
        private val gson: Gson
) {
    fun retrieveCurrentUser(): Maybe<UserVK> {
        val request = VKApi.users().get()
        return Maybe.create<VKResponse> { maybe ->
            request.executeSyncWithListener(object : VKRequest.VKRequestListener() {
                override fun onComplete(response: VKResponse?) {

                    if (response != null) {
                        maybe.onSuccess(response)
                    } else {
                        maybe.onError(NullPointerException("VK returns null response."))
                    }
                }

                override fun onError(error: VKError?) {
                    maybe.onComplete()
                }
            })
        }
                .observeOn(Schedulers.io())
                .map { response ->
                    gson.fromJson(response.json.getJSONArray("response")[0].toString(), UserVK::class.java)
                }
    }
}