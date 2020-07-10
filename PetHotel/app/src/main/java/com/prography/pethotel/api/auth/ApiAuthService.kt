package com.prography.pethotel.api.auth

import com.prography.pethotel.api.auth.request.KakaoRegisterBody
import com.prography.pethotel.api.auth.request.LoginInfoBody
import com.prography.pethotel.api.auth.request.RegisterPetBody
import com.prography.pethotel.api.auth.request.RegisterUserInfo
import com.prography.pethotel.api.auth.response.*
import com.prography.pethotel.api.main.response.UserInfoResponse
import com.prography.pethotel.utils.ANIMAL_NUM_BASE_URL
import com.prography.pethotel.utils.BASE_URL
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import retrofit2.http.Body
import retrofit2.http.Header


//GET https://api.mypetmily.net/hotels
private val authRetrofit
        = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

private val publicApiRetrofit
        = Retrofit.Builder()
    .addConverterFactory(TikXmlConverterFactory.create())
    .baseUrl(ANIMAL_NUM_BASE_URL)
    .build()


private val kakaoRetrofit
        =Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

interface AnimalNumberApiService{

    @GET("service/rest/animalInfoSrvc/animalInfo")
    fun getAnimalCheckResponse(
        @Query("ServiceKey") serviceKey : String,
        @Query("dogregno") dog_reg_no : String,
        @Query("rfid_cd") rfid_cd : String
    ) : Call<PetNumResponse>

}

interface  AuthApiService{

    @POST("auth/login")
    suspend fun generalLogin(@Body loginInfo : LoginInfoBody) : GeneralLoginResponse

    @POST("user")
    suspend fun generalRegister(@Body registerUserInfo : RegisterUserInfo) : RegistrationResponse

    @GET("user")
    suspend fun getUser(@Header("Authorization") token : String) : UserInfoResponse //GET 마이페이지

    @POST("pet")
    suspend fun registerPet(@Header("Authorization") token : String,
                            @Body registerPetBody: RegisterPetBody) : PostPetResponse

}


interface KakaoApiService{

//    @GET("auth/kakao/login")
//    suspend fun kakaoLogin() : String

    @POST("user")
    suspend fun kakaoRegister(@Body kakaoRegisterBody: KakaoRegisterBody) : RegistrationResponse

}


object PetmilyAuthApi{
    val publicApiRetrofitService : AnimalNumberApiService by lazy {
        publicApiRetrofit.create(AnimalNumberApiService::class.java)
    }

    val authApiRetrofitService : AuthApiService by lazy {
        authRetrofit.create(AuthApiService::class.java)
    }

    val kakaoApiRetrofitService : KakaoApiService by lazy{
        kakaoRetrofit.create(KakaoApiService::class.java)
    }
}

