package id.heycoding.storysubmission.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import id.heycoding.storysubmission.MainActivity
import id.heycoding.storysubmission.R
import id.heycoding.storysubmission.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var fragmentWelcomeBinding: FragmentWelcomeBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentWelcomeBinding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return fragmentWelcomeBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).supportActionBar?.hide()

        setupView()
        playAnimation()
    }

    private fun setupView() {
        fragmentWelcomeBinding?.apply {
            loginButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_welcomeFragment_to_loginFragment
                )
            }
            signupButton.setOnClickListener {
                findNavController().navigate(
                    R.id.action_welcomeFragment_to_registerFragment
                )
            }
        }
    }

    private fun playAnimation() {
        fragmentWelcomeBinding?.apply {
            ObjectAnimator.ofFloat(imageView, View.TRANSLATION_X, -30f, 30f).apply {
                duration = 6000
                repeatCount = ObjectAnimator.INFINITE
                repeatMode = ObjectAnimator.REVERSE
            }.start()

            val login = ObjectAnimator.ofFloat(loginButton, View.ALPHA, 1f).setDuration(1000)
            val signup = ObjectAnimator.ofFloat(signupButton, View.ALPHA, 1f).setDuration(1000)
            val title = ObjectAnimator.ofFloat(titleTextView, View.ALPHA, 1f).setDuration(1000)
            val desc = ObjectAnimator.ofFloat(descTextView, View.ALPHA, 1f).setDuration(1000)

            val together = AnimatorSet().apply {
                playTogether(login, signup)
            }

            AnimatorSet().apply {
                playSequentially(title, desc, together)
                start()
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentWelcomeBinding = null
    }
}