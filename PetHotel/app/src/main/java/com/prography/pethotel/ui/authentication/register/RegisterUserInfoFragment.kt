package com.prography.pethotel.ui.authentication.register

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.prography.pethotel.R
import com.prography.pethotel.models.GeneralUserInfo
import com.prography.pethotel.ui.MainActivity
import com.prography.pethotel.ui.authentication.afterTextChanged
import com.prography.pethotel.ui.authentication.utils.BaseFragment
import com.prography.pethotel.ui.authentication.kakao.KakaoRegisterViewModel
import com.prography.pethotel.utils.ENTER_PET
import com.prography.pethotel.utils.NO_PET
import com.prography.pethotel.utils.USER_TOKEN
import kotlinx.android.synthetic.main.fragment_kakao_register.*
import kotlinx.android.synthetic.main.register_user_info_fragment.*


private const val TAG = "RegisterUserInfoFragment"


@Suppress("DEPRECATION")
class RegisterUserInfoFragment : BaseFragment() {

    companion object {
        fun newInstance() =
            RegisterUserInfoFragment()

    }
    private lateinit var registerViewModel: RegisterViewModel
//    private var currentPhotoPath = ""
    private var generalUserInfo : GeneralUserInfo = GeneralUserInfo()
    private lateinit var userImage : ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_user_info_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        registerViewModel = ViewModelProvider(requireActivity())[RegisterViewModel::class.java]

        userImage = img_register_user_image

        /*다시 입력 화면으로 돌아왔을 때 정보가 남아있도록 한다.*/
        observeUserInfo()

//        btn_upload_user_image.setOnClickListener {
//            getAlbum()
//        }
//
//        btn_take_user_photo.setOnClickListener {
//            takePhoto()
//        }

        /* 정확한 정보 입력을 감지한다. */
        setListenersToFields()

        /*펫 정보 입력하는 버튼 작동할때까지 press 안되게 하기 */
        btn_register_pet_info.isEnabled = false
//        btn_register_pet_info.setOnClickListener {
//            registerUser(ENTER_PET)
//        }


        registerViewModel.registerFormState.observe(viewLifecycleOwner, Observer {
            btn_register_complete.isEnabled = it.isDataValid

            if(it.emailError != null){
                email_edit_text_field.error = it.emailError
            }
            if(it.nicknameError != null){
                nickname_edit_text_field.error = it.nicknameError
            }
            if(it.phoneNumberError != null){
                phone_edit_text_field.error = it.phoneNumberError
            }
            if(it.passwordError != null){
                password_check_edit_text_field.error = it.passwordError
            }
        })

        btn_register_complete.setOnClickListener {
           registerUser(NO_PET)
        }

        //이미회원이신가요? 로그인하기 클릭하면 넘어가는 로직
        tv_login_from_register_screen.setOnClickListener {
            findNavController().navigate(R.id.action_registerUserInfoFragment_to_loginRegisterFragment)
        }

//        setUpUserInfoInputListeners()
    }

    private fun observeUserInfo(){

        registerViewModel.userInfo.observe(viewLifecycleOwner, Observer {
            Log.d("REGISTER", "register viewmodel called $it")
            email_edit_text_field.setText(it.email)
            nickname_edit_text_field.setText(it.nickName)
            phone_edit_text_field.setText(it.phoneNumber)
            password_edit_text_field.setText(it.password)
            password_check_edit_text_field.setText(it.password)
        })

    }

    private fun setListenersToFields(){
        email_edit_text_field.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    nickname = nickname_edit_text_field.text.toString(),
                    email = email_edit_text_field.text.toString(),
                    phone = phone_edit_text_field.text.toString(),
                    password = password_edit_text_field.text.toString(),
                    passwordCheck = password_check_edit_text_field.text.toString()
                )
            }
        }
        nickname_edit_text_field.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    nickname = nickname_edit_text_field.text.toString(),
                    email = email_edit_text_field.text.toString(),
                    phone = phone_edit_text_field.text.toString(),
                    password = password_edit_text_field.text.toString(),
                    passwordCheck = password_check_edit_text_field.text.toString()
                )
            }
        }
        phone_edit_text_field.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    nickname = nickname_edit_text_field.text.toString(),
                    email = email_edit_text_field.text.toString(),
                    phone = phone_edit_text_field.text.toString(),
                    password = password_edit_text_field.text.toString(),
                    passwordCheck = password_check_edit_text_field.text.toString()
                )
            }
        }
        password_edit_text_field.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    nickname = nickname_edit_text_field.text.toString(),
                    email = email_edit_text_field.text.toString(),
                    phone = phone_edit_text_field.text.toString(),
                    password = password_edit_text_field.text.toString(),
                    passwordCheck = password_check_edit_text_field.text.toString()
                )
            }
        }
        password_check_edit_text_field.apply {
            afterTextChanged {
                registerViewModel.registerDataChanged(
                    nickname = nickname_edit_text_field.text.toString(),
                    email = email_edit_text_field.text.toString(),
                    phone = phone_edit_text_field.text.toString(),
                    password = password_edit_text_field.text.toString(),
                    passwordCheck = password_check_edit_text_field.text.toString()
                )
            }
        }
    }

    /*returns true if the registration was successful*/
    private fun registerUser(registerType : Int){

            generalUserInfo = GeneralUserInfo(
                nickName = nickname_edit_text_field.text.toString(),
                email = email_edit_text_field.text.toString(),
                phoneNumber = phone_edit_text_field.text.toString(),
                password = password_edit_text_field.text.toString()
            )

            registerViewModel.setUserInfo(generalUserInfo)

            registerViewModel.registerUserGeneral(generalUserInfo)

            registerViewModel.getRegisterStatus().observe(viewLifecycleOwner, Observer {
                Log.d(TAG, "onActivityCreated: register status = $it")

                if(it == true){
                    registerViewModel.getUserToken().observe(viewLifecycleOwner, Observer {
                            userToken ->
                        Log.d(TAG, "onActivityCreated: $userToken")

                        val pref = requireActivity().getSharedPreferences(USER_TOKEN, MODE_PRIVATE)
                        val editor = pref.edit()
                        editor.putString(USER_TOKEN, userToken.token)
                        val e = editor.commit()
                        Log.d(TAG, "onActivityCreated: $e")
                    })
                    //different redirection based on register type
                    when(registerType){
                        NO_PET -> {
                            redirectToMainActivity()
                        }
                        ENTER_PET -> {
                            findNavController().navigate(R.id.action_registerUserInfoFragment_to_registerPetInfoFragment)
                        }
                    }
                }else{
                    //FAIL REGISTRATION
                    Toast.makeText(requireContext(),
                        getString(R.string.registration_fail_msg),
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun redirectToMainActivity(){
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

//    private fun setUpUserInfoInputListeners(){
//        email_edit_text_field.addTextChangedListener(
//            afterTextChanged = {
//                generalUserInfo.email = it.toString()
//            }
//        )
//
//        nickname_edit_text_field.addTextChangedListener(
//            afterTextChanged = {
//                generalUserInfo.nickName = it.toString()
//            }
//        )
//
//        phone_edit_text_field.addTextChangedListener(
//            afterTextChanged = {
//                generalUserInfo.phoneNumber = it.toString()
//            }
//        )
//        setUpPasswordInputListener()
//    }

//    private fun setUpPasswordInputListener(){
//        password_edit_text_field.setOnFocusChangeListener { v, hasFocus ->
//            val password = password_edit_text_field.text.toString()
//            if(!hasFocus && password.isNotEmpty()){
//                setUpPasswordCheckInputLister(password)
//                generalUserInfo.password = password
//                Log.d("REGISTER", registerViewModel.userInfo.toString())
//            }
//        }
//    }

//    private fun setUpPasswordCheckInputLister(password: String) {
//        password_check_edit_text_field.setOnFocusChangeListener { v, hasFocus ->
//            if(hasFocus &&
//                password.isEmpty()
//            ){
//                Toast.makeText(context, "사용하실 비밀번호를 먼저 입력해 주세요", Toast.LENGTH_LONG).show()
//            }else if(hasFocus &&
//                password.isNotEmpty()
//            ){
//                password_check_edit_text_field.addTextChangedListener(
//                    onTextChanged = {
//                            pw, _, _, _ -> checkPasswordMatch(password, pw.toString())
//                        Log.d("REGISTER", pw.toString() + ", " + " Checking against " + password)
//                    }
//                )
//            }
//        }
//    }

//    private fun checkPasswordMatch(password : String, checkPassword : String){
//        if(password != checkPassword){
//            tv_register_password_not_match.visibility = View.VISIBLE
//        }else{
//            tv_register_password_not_match.text = "비밀번호가 일치합니다."
//            tv_register_password_not_match.setTextColor(resources.getColor(R.color.petHotelSecondary))
//        }
//    }


//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
//
//            Log.d(TAG_PHOTO, "${currentPhotoPath}")
////            Glide.with(requireContext())
////                .load(Uri.parse(currentPhotoPath))
////                .into(userImage)
//            userImage.setImageURI(Uri.parse(currentPhotoPath))
//
//        } else if (requestCode == REQUEST_TAKE_ALBUM && resultCode == Activity.RESULT_OK) {
//            if (data != null) {
//                currentPhotoPath = data.data.toString()
//
//                Glide.with(requireContext())
//                    .load(Uri.parse(currentPhotoPath))
//                    .into(userImage)
//            }
//
//        }
//    }

}
