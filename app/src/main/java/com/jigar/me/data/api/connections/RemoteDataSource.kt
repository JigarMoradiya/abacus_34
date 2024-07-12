package com.jigar.me.data.api.connections

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.jigar.me.BuildConfig
import com.jigar.me.data.model.ErrorData
import com.jigar.me.data.model.MainAPIResponse
import com.jigar.me.data.model.MainAPIResponseArray
import com.jigar.me.data.pref.AppPreferencesHelper
import com.jigar.me.ui.view.login.LoginDashboardActivity
import com.jigar.me.utils.AppConstants
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.SSLException

class RemoteDataSource @Inject constructor() {
    lateinit var prefManager : AppPreferencesHelper
    private var client: OkHttpClient? = null
    private var retrofit: Retrofit? = null

    fun <Api> buildApi(api: Class<Api>,context: Context,base_url: String?): Api {
        prefManager = AppPreferencesHelper(context, AppConstants.PREF_NAME)
        return getClient(context, base_url)?.create(api)!!
    }

    private fun getClient(context: Context,base_url: String?,): Retrofit? {
        client = OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                forwardNext(context, chain)!!
            })
            .addInterceptor(AuthHeaderInterceptor(prefManager))
            .readTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(base_url)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

        return retrofit
    }


    @Throws(IOException::class)
    fun forwardNext(context: Context, chain: Interceptor.Chain): Response? {
        val request: Request = chain.request()
        Log.e("post_request", request.url.toString() + "")
//        get Response Body sent by Repositary APT calls
        val oldBody = request.body
        val buffer = Buffer()
        oldBody?.writeTo(buffer)

        val strNewBody = buffer.readUtf8()
        Log.e("post_request_new_body", "Without encryption enabled body $strNewBody")
        return try {
            val response: Response = chain.proceed(request) // get the API response

            Log.e("post_response_main", "Response from direct API $response")
            val stringJson = response.body?.string()
            val jsonObject = JSONObject(stringJson)
            val decrypted_string = jsonObject.toString()

            //remove null keys from the json object
            val gson = GsonBuilder().setPrettyPrinting().create()
            val je = JsonParser.parseString(decrypted_string)
            val prettyJsonString = gson.toJson(je)
            Log.e(
                "post_response",
                "Remove all NULL keys from JSON Object$prettyJsonString"
            )
            Log.e(
                "post_response",
                "creating the main response to send it to APIs for" + request.url
            )
            if (request.url.toString().contains("getImages")){
                val mainAPIResponse = gson.fromJson(prettyJsonString, MainAPIResponseArray::class.java)
                response.newBuilder().body(
                    Gson().toJson(mainAPIResponse).toResponseBody(response.body?.contentType())
                ).build()
            }else{
                val mainAPIResponse = gson.fromJson(prettyJsonString, MainAPIResponse::class.java)
                if (mainAPIResponse.statusCode == 401){
                    handleForbiddenResponse(context)
                    createEmptyResponse(chain,null)
                }else{
                    response.newBuilder().body(
                        Gson().toJson(mainAPIResponse).toResponseBody(response.body?.contentType())
                    ).build()
                }
            }

                //create response body with decrypted value and pass it to the respositories

        } catch (e: Exception) {
            Log.e("post_response_Issue with response of API ", request.url.toString())
            return handleException(chain, e)
        }
    }

    private fun handleForbiddenResponse(context: Context) {
        prefManager.setAccessToken("")
        prefManager.setLoginData("")
        prefManager.setUserLoggedIn(false)
        LoginDashboardActivity.getInstance(context)
    }


    private fun handleException(
        chain: Interceptor.Chain,
        e: Exception
    ): Response? {
        Log.e("post_response_message", e.message + " " + e.javaClass.canonicalName)
        //logout user while facing error related to JWT
        return when (e) {
            is SocketTimeoutException, is UnknownHostException -> {
                Log.e("response_create_post", e.message + "")
                createEmptyResponse(chain,"Uh-Oh! Slow or no internet connection. Please check your internet settings and try again")
            }

            is SSLException -> {
                createEmptyResponse(chain,"Something went wrong, Please try after sometime")
            }

            else -> {
                //in case content/error dose not fount in json
                createEmptyResponse(chain,"Something went wrong, Please try after sometime")
            }
        }
    }

    private fun createEmptyResponse(chain: Interceptor.Chain, errorMessage: String?): Response? {
        val mediaType = "application/json".toMediaType()

        val mainAPIResponse = MainAPIResponse(errorMessage,AppConstants.APIStatus.ERROR, error = ErrorData(errorMessage))
        val responseBody = Gson().toJson(mainAPIResponse).toResponseBody(mediaType)

        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(500)
            .message("Something went wrong, Please try after sometime")
            .body(responseBody)
            .build()

    }


}