package id.heycoding.storysubmission.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import id.heycoding.storysubmission.BuildConfig.PREF_NAME
import id.heycoding.storysubmission.MainActivity
import id.heycoding.storysubmission.R
import id.heycoding.storysubmission.data.remote.response.auth.AuthSession
import id.heycoding.storysubmission.databinding.FragmentLoginBinding
import id.heycoding.storysubmission.ui.auth.AuthViewModel
import id.heycoding.storysubmission.utils.Helper
import id.heycoding.storysubmission.utils.Preferences

class LoginFragment : Fragment() {

    private var fragmentLoginBinding: FragmentLoginBinding? = null
    private lateinit var authViewModel: AuthViewModel
    private lateinit var pref: SharedPreferences
    private lateinit var userLoginPref: Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLoginBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return fragmentLoginBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).supportActionBar?.hide()
        initPreferences()
        playAnimation()
        initView()
        setupAccessibility()
    }

    private fun initPreferences() {
        pref = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        userLoginPref = Preferences(requireContext())
    }

    private fun playAnimation() {
        fragmentLoginBinding?.apply {
            ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val title = ObjectAnimator.ofFloat(titleTextView, View.ALPHA, 1f).setDuration(1000)
            val message = ObjectAnimator.ofFloat(messageTextView, View.ALPHA, 1f).setDuration(1000)
            val email = ObjectAnimator.ofFloat(emailTextView, View.ALPHA, 1f).setDuration(1000)
            val emailEditText =
                ObjectAnimator.ofFloat(textInputLayoutEmail, View.ALPHA, 1f).setDuration(1000)
            val password =
                ObjectAnimator.ofFloat(passwordTextView, View.ALPHA, 1f).setDuration(1000)
            val passwordEditText =
                ObjectAnimator.ofFloat(textInputLayoutPassword, View.ALPHA, 1f).setDuration(1000)
            val login = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 1f).setDuration(1000)

            AnimatorSet().apply {
                playSequentially(
                    title,
                    message,
                    email,
                    emailEditText,
                    password,
                    passwordEditText,
                    login
                )
                start()
            }
        }
    }

    private fun initView() {
        fragmentLoginBinding?.apply {
            loginButton.setOnClickListener {
                Helper.hideKeyboard(it)
                val validationResult = validateUserInput()
                if (validationResult.first) {
                    doLogin()
                } else {
                    showValidationErrors(validationResult.second)
                }
            }
        }
        bindObservers()
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val emailAddress = fragmentLoginBinding?.edtLoginEmail?.text.toString()
        val password = fragmentLoginBinding?.edtLoginPassword?.text.toString()
        return authViewModel.validateCredentials(emailAddress, "", password, true)
    }

    private fun doLogin() {
        val userEmail = fragmentLoginBinding?.edtLoginEmail?.text.toString().trim()
        val userPassword = fragmentLoginBinding?.edtLoginPassword?.text.toString().trim()

        authViewModel.apply {
            doLogin(userEmail, userPassword)
            userLogin.observe(viewLifecycleOwner) { userLoginResult ->
                if (userLoginResult != null) {
                    //save the login session

                    val currentUser = AuthSession(
                        userLoginResult.name,
                        userLoginResult.token,
                        userLoginResult.userId,
                        true
                    )

                    userLoginPref.setUserLogin(currentUser)

                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Login Berhasil")
                        setMessage("Logged atas nama ${userLoginResult.name}!")
                        setPositiveButton("Ok") { _, _ ->
                            findNavController().navigate(
                                R.id.action_loginFragment_to_homeFragment
                            )
                        }
                        create()
                        show()
                    }
                }

            }
        }

    }

    private fun showValidationErrors(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun bindObservers() {
        authViewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]

        authViewModel.isLoading.observe(viewLifecycleOwner) { showLoading(it) }
        authViewModel.message.observe(viewLifecycleOwner) { showValidationErrors(it) }
    }

    private fun showLoading(isLoading: Boolean) {
        fragmentLoginBinding?.progressIndicator!!.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupAccessibility() {
        fragmentLoginBinding?.apply {
            imageView.contentDescription = getString(R.string.txt_image_desc)
            titleTextView.contentDescription = getString(R.string.txt_title_login_desc)
            messageTextView.contentDescription = getString(R.string.txt_msg_login_desc)
            emailTextView.contentDescription = getString(R.string.txt_email_desc)
            textInputLayoutEmail.contentDescription = getString(R.string.txt_colomn_email_desc)
            passwordTextView.contentDescription = getString(R.string.txt_password_desc)
            textInputLayoutPassword.contentDescription =
                getString(R.string.txt_colomn_password_desc)
            loginButton.contentDescription = getString(R.string.txt_btn_login_desc)
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentLoginBinding = null
    }
}