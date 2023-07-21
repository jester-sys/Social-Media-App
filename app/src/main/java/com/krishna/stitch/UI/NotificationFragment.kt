package com.krishna.stitch.UI

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.krishna.stitch.NotificationUi.AllFragment
import com.krishna.stitch.NotificationUi.MentionFragment
import com.krishna.stitch.NotificationUi.RequestFragment
import com.krishna.stitch.R
import com.krishna.stitch.databinding.FragmentNotificationBinding


class NotificationFragment : Fragment() {
    private lateinit var binding: FragmentNotificationBinding
    private var isAllBtnClicked = false
    private var isRequestBtnClicked = false
    private var isMentionsBtnClicked = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationBinding.inflate(layoutInflater)

        openFragment(AllFragment())
        binding.AllBtn.setBackgroundResource(R.drawable.text_desgin)
        binding.AllBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        binding.AllBtn.setOnClickListener {
            binding.AllBtn.setBackgroundResource(R.drawable.text_desgin)
            binding.AllBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))

            binding.MentionsBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black_shade))
            binding.MentionsBtn.setBackgroundResource(R.drawable.btn_desgin)

            binding.RequestBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black_shade))
            binding.RequestBtn.setBackgroundResource(R.drawable.btn_desgin)
            openFragment(AllFragment())
        }

        binding.RequestBtn.setOnClickListener {
            binding.RequestBtn.setBackgroundResource(R.drawable.text_desgin)
            binding.RequestBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))

            binding.AllBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black_shade))
            binding.AllBtn.setBackgroundResource(R.drawable.btn_desgin)

            binding.MentionsBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black_shade))
            binding.MentionsBtn.setBackgroundResource(R.drawable.btn_desgin)

            openFragment(RequestFragment())
        }

        binding.MentionsBtn.setOnClickListener {
            binding.MentionsBtn.setBackgroundResource(R.drawable.text_desgin)
            binding.MentionsBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
            binding.AllBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black_shade))
            binding.AllBtn.setBackgroundResource(R.drawable.btn_desgin)

            binding.RequestBtn.setTextColor(ContextCompat.getColor(requireActivity(), R.color.black_shade))
            binding.RequestBtn.setBackgroundResource(R.drawable.btn_desgin)
            openFragment(MentionFragment())
        }

        return binding.root
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.notification_container_view, fragment)
        fragmentTransaction.addToBackStack(null) // Optional: Add the transaction to the back stack
        fragmentTransaction.commit()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val fragmentManager = requireActivity().supportFragmentManager
                if (fragmentManager.backStackEntryCount > 0) {
                    fragmentManager.popBackStack() // Pop the fragment from the back stack
                } else {
                    isEnabled = false // Disable the callback if there are no fragments in the back stack
                    requireActivity().onBackPressed() // Default back button behavior
                }
            }
        })
    }
}


