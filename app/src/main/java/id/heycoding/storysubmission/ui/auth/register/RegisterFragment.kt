package id.heycoding.storysubmission.ui.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import id.heycoding.storysubmission.MainActivity
import id.heycoding.storysubmission.R
import id.heycoding.storysubmission.databinding.FragmentRegisterBinding
import id.heycoding.storysubmission.ui.auth.AuthViewModel
import id.heycoding.storysubmission.utils.Helper
import id.heycoding.storysubmission.utils.Preferences

class RegisterFragment : Fragment() {

    private var fragmentRegisterBinding: FragmentRegisterBinding? = null
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userLoginPref: Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentRegisterBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        return fragmentRegisterBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userLoginPref = Preferences(requireContext())
        (activity as MainActivity).supportActionBar?.hide()

        playAnimation()
        setupView()
        setupAccessibility()
    }

    private fun playAnimation() {
        fragmentRegisterBinding?.apply {
            ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val title = ObjectAnimator.ofFloat(titleTextView, View.ALPHA, 1f).setDuration(1000)
            val name = ObjectAnimator.ofFloat(nameTextView, View.ALPHA, 1f).setDuration(1000)
            val nameEditText =
                ObjectAnimator.ofFloat(textInputLayoutUsername, View.ALPHA, 1f).setDuration(1000)
            val email = ObjectAnimator.ofFloat(emailTextView, View.ALPHA, 1f).setDuration(1000)
            val emailEditText =
                ObjectAnimator.ofFloat(textInputLayoutEmail, View.ALPHA, 1f).setDuration(1000)
            val password =
                ObjectAnimator.ofFloat(passwordTextView, View.ALPHA, 1f).setDuration(1000)
            val passwordEditText =
                ObjectAnimator.ofFloat(textInputLayoutPassword, View.ALPHA, 1f).setDuration(1000)
            val signup = ObjectAnimator.ofFloat(signupButton, View.ALPHA, 1f).setDuration(1000)

            AnimatorSet().apply {
                playSequentially(
                    title,
                    name,
                    nameEditText,
                    email,
                    emailEditText,
                    password,
                    passwordEditText,
                    signup
                )
                start()
            }
        }
    }

    private fun setupView() {
        fragmentRegisterBinding?.apply {
            signupButton.setOnClickListener {
                Helper.hideKeyboard(it)
                val validationResult = validateUserInput()
                if (validationResult.first) {
                    doRegister()
                } else {
                    showValidationErrors(validationResult.second)
                }
            }
        }
        bindObservers()
    }

    private fun validateUserInput(): Pair<Boolean, String> {
        val emailAddress = fragmentRegisterBinding?.edtRegisterEmail?.text.toString()
        val userName = fragmentRegisterBinding?.edtRegisterUsername?.text.toString()
        val password = fragmentRegisterBinding?.edtRegisterPassword?.text.toString()
        return authViewModel.validateCredentials(emailAddress, userName, password, false)
    }

    private fun doRegister() {
        val username = fragmentRegisterBinding?.edtRegisterUsername?.text.toString().trim()
        val userEmail = fragmentRegisterBinding?.edtRegisterEmail?.text.toString().trim()
        val userPassword = fragmentRegisterBinding?.edtRegisterPassword?.text.toString().trim()

        authViewModel.apply {
            doRegister(username, userEmail, userPassword)
            isError.observe(viewLifecycleOwner) { error ->
                if (error != true) {
                    findNavController().navigate(
                        R.id.action_registerFragment_to_loginFragment
                    )
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
        fragmentRegisterBinding?.progressIndicator!!.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun setupAccessibility() {
        fragmentRegisterBinding?.apply {
            imageView.contentDescription = getString(R.string.txt_image_desc)
            titleTextView.contentDescription = getString(R.string.txt_title_register_desc)
            nameTextView.contentDescription = getString(R.string.txt_name_desc)
            textInputLayoutUsername.contentDescription = getString(R.string.txt_colomn_name_desc)
            emailTextView.contentDescription = getString(R.string.txt_email_desc)
            textInputLayoutEmail.contentDescription = getString(R.string.txt_colomn_email_desc)
            passwordTextView.contentDescription = getString(R.string.txt_password_desc)
            textInputLayoutPassword.contentDescription =
                getString(R.string.txt_colomn_password_desc)
            signupButton.contentDescription = getString(R.string.txt_btn_register_desc)
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentRegisterBinding = null
    }
}