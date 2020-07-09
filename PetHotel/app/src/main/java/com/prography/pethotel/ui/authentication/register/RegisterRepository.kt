package com.prography.pethotel.ui.authentication.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prography.pethotel.api.auth.PetmilyAuthApi
import com.prography.pethotel.api.auth.request.*
import com.prography.pethotel.api.auth.response.UserToken
import com.prography.pethotel.api.main.response.PetNumberResponse
import com.prography.pethotel.api.main.response.UserInfoResponse
import com.prography.pethotel.models.GeneralUserInfo
import com.prography.pethotel.models.PetInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.security.spec.ECField

private const val TAG = "RegisterRepository"

object RegisterRepository{

    private val coroutineScope  = CoroutineScope(Dispatchers.Main)

    /*동물등록번호 API 호출 시 오는 응답에 대한 라이브 데이터*/
    private val _petInfo : MutableLiveData<PetNumberResponse> = MutableLiveData()
    val petInfo : LiveData<PetNumberResponse>
        get() = _petInfo

    /* 회원가입시 발급되는 유저 토큰 */
    private val _userToken : MutableLiveData<UserToken> = MutableLiveData()
    val userToken : LiveData<UserToken>
        get() = _userToken

    /*회원가입 성공 혹은 실패에 대한 정보 */
    private val _registerStatus : MutableLiveData<Boolean> = MutableLiveData()
    val registerStatus : LiveData<Boolean>
        get() = _registerStatus

    /*유저 토큰으로 요청해서 받아오는 유저 정보*/
    private val _userInfoRespone : MutableLiveData<UserInfoResponse> = MutableLiveData()
    val userInfoResponse : LiveData<UserInfoResponse>
        get() = _userInfoRespone


    /*공공 API 에서 유효한 동물등록 번호인지를 확인하는 메서드 */
    fun checkPublicApiForPetInfo(dogRegNo : String, serviceKey : String){
        try {
            coroutineScope.launch {
                val response
                        = PetmilyAuthApi.publicApiRetrofitService.getAnimalCheckResponse(dog_reg_no = dogRegNo)
                //응답을 확인한 후 응답 전체를 petInfo 에 저장한다.
                _petInfo.value = response
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun registerPetInfo(userToken: UserToken, userId : Int, petInfoList: ArrayList<PetInfo>){
        val petDataList = ArrayList<PetData>()
        petInfoList.forEach {
            petDataList.add(PetData(it.birthYear?.toInt()!!, it.name!!, it.registrationNum!!))
        }

        val registerPetBody = RegisterPetBody(
            data = petDataList,
            userId = userId
        )
        try{
            coroutineScope.launch {
                val response = PetmilyAuthApi.authApiRetrofitService.registerPet(
                    userToken.token, registerPetBody
                )
            }
        }catch (e : Exception){
            Log.d(TAG, "registerPetInfo: ${e.printStackTrace()}")
        }
    }


    /*일반 회원가입. 유저의 이름, 이메일, 비밀번호, 전화번호를 서버로 전송한다. 응답은 토큰이고,
    * 이 토큰을 저장해 두었다가 다른 요청때 헤더로 넣어서 전송한다. */
    fun generalRegister(generalUserInfo: GeneralUserInfo){
        val registerInfo =
            RegisterUserInfo(
                UserData(
                    generalUserInfo.nickName!!,
                    generalUserInfo.phoneNumber,
                    generalUserInfo.email!!,
                    generalUserInfo.password!!
                )
            )
        Log.d(TAG, "generalRegister: ${generalUserInfo}")
        try {
            coroutineScope.launch {
                val response
                        = PetmilyAuthApi.authApiRetrofitService.generalRegister(registerInfo)
                Log.d(TAG, "generalLogin: $response")
                //결과에 따라서 응답 상태를 다르게 설정한다.
                _registerStatus.value = response.status == "success"
                _userToken.value = response.userToken
            }
        }catch (e : Exception){
            e.printStackTrace()
        }
    }

    /*카카오 회원가입. 유저 아이디와 유저 이메일, 전화번호, 프로필 사진을 서버로 전송한다. */
    fun kakaoRegister(kakaoRegisterBody: KakaoRegisterBody){
        try {
            coroutineScope.launch {
                val response =
                    PetmilyAuthApi.authApiRetrofitService.kakaoRegister(
                        kakaoRegisterBody
                    )
                Log.d(TAG, "kakaoRegister: $response")
                /*카카오 회원가입 또한 동일한 register status, user token 객체에
                * 데이터를 setting 한다 */
                _registerStatus.value = response.status == "success"
                _userToken.value = response.userToken
            }
        }catch (e : Exception){
            Log.d(TAG, "kakaoRegister: ${e.printStackTrace()}")
        }
    }


    fun getUser(userToken: UserToken){
        try {
            coroutineScope.launch {
                val response = PetmilyAuthApi.authApiRetrofitService.getUser(
                    userToken.token
                )
                _userInfoRespone.value = response
            }
        }catch (e : Exception){
            Log.d(TAG, "getUser: ${e.printStackTrace()}")
        }
    }
}