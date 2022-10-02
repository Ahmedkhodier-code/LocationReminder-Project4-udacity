package com.udacity.project4.locationreminders.reminderslist

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.R
import com.udacity.project4.authentication.AuthenticationActivity
import com.udacity.project4.authentication.LoginViewModel
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentRemindersBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.setTitle
import com.udacity.project4.utils.setup
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReminderListFragment : BaseFragment() {
    //use Koin to retrieve the ViewModel instance
    companion object {
        const val TAG = "ReminderListFragment"
        const val SIGN_IN_RESULT_CODE = 1001
    }

    private lateinit var menu: Menu
    override val _viewModel: RemindersListViewModel by viewModel()
    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: FragmentRemindersBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminders, container, false)
        binding.viewModel = _viewModel

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(false)
        setTitle(getString(R.string.app_name))

        binding.refreshLayout.setOnRefreshListener { _viewModel.loadReminders() }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        setupRecyclerView()

        binding.addReminderFAB.setOnClickListener {
            viewModel.authenticationState.observe(
                viewLifecycleOwner,
                Observer { authenticationState ->
                    when (authenticationState) {
                        LoginViewModel.AuthenticationState.AUTHENTICATED -> navigateToAddReminder()
                        LoginViewModel.AuthenticationState.UNAUTHENTICATED -> launchSignInFlow()
                        else -> Log.e(
                            TAG,
                            "Authentication state that doesn't require any UI change $authenticationState"
                        )
                    }
                })
        }
    }

    override fun onResume() {
        super.onResume()
        //load the reminders list on the ui
        _viewModel.loadReminders()
    }

    private fun navigateToAddReminder() {
        //use the navigationCommand live data to navigate between the fragments

        _viewModel.navigationCommand.postValue(
            NavigationCommand.To(
                ReminderListFragmentDirections.toSaveReminder()
            )
        )
    }

    private fun setupRecyclerView() {
        val adapter = RemindersListAdapter {
        }

//        setup the recycler view using the extension function
        binding.reminderssRecyclerView.setup(adapter)
    }


    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers)
                .build(), SIGN_IN_RESULT_CODE
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Log.i(
                    TAG,
                    "Successfully signed in user " + "${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
                menu.findItem(R.id.login).isVisible = false
                menu.findItem(R.id.logout).isVisible = true
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
                menu.findItem(R.id.logout).isVisible = false
                menu.findItem(R.id.login).isVisible = true

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.logout -> {
//                TODO: add the logout implementation
                Toast.makeText(context, " u r logout", Toast.LENGTH_LONG).show()
                AuthUI.getInstance().signOut(requireContext())
                findNavController().navigate(R.id.action_reminderListFragment_to_authenticationActivity)
            }
            R.id.login -> {
//                TODO: add the login implementation
                Toast.makeText(context, " u r login", Toast.LENGTH_LONG).show()
                launchSignInFlow()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.also { this.menu = it }
        inflater.inflate(R.menu.main_menu, menu)
        viewModel.authenticationState.observe(
            viewLifecycleOwner, Observer { authenticationState ->
                Log.e(TAG, "authenticationState changed to  $authenticationState")
                when (authenticationState) {
                    LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                        menu.findItem(R.id.login).isVisible = false
                    }
                    LoginViewModel.AuthenticationState.UNAUTHENTICATED -> {
                        menu.findItem(R.id.logout).isVisible = false
                    }
                    else -> Log.e(
                        TAG,
                        "Authentication state that doesn't require any UI change $authenticationState"
                    )
                }
            })
//        display logout as menu item

    }

}
