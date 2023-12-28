package id.heycoding.storysubmission.ui.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import id.heycoding.storysubmission.MainActivity
import id.heycoding.storysubmission.R
import id.heycoding.storysubmission.databinding.FragmentMapsBinding
import id.heycoding.storysubmission.utils.Preferences

class MapsFragment : Fragment(), OnMapReadyCallback {

    private var fragmentMapsBinding: FragmentMapsBinding? = null
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var userLoginPref: Preferences
    private lateinit var mMap: GoogleMap
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMapsBinding = FragmentMapsBinding.inflate(layoutInflater)
        return fragmentMapsBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        userLoginPref = Preferences(requireContext())
        (activity as MainActivity).supportActionBar?.hide()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * In this case, we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to
     * install it inside the SupportMapFragment. This method will only be triggered once the
     * user has installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        setupMapStyle()
        bindObservers()
    }

    private fun setupMapStyle() {
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle))
            if (!success) {
                Log.e("Setup Map Style", "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e("Setup Map Style", "Can't find style. Error: $exception")
        }
    }

    private val requestPermissionLaucher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext().applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLaucher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun bindObservers() {
        mapsViewModel = ViewModelProvider(this)[MapsViewModel::class.java]

        mapsViewModel.apply {
            if (userLoginPref.getLoginData().isLogin) {
                getAllMapsStoriesData(userLoginPref.getLoginData().token)
            }

            listMapsStoryData.observe(requireActivity()) {
                if (it != null) {
                    it.forEach { mapsStory ->
                        val latLng = LatLng(mapsStory.lat, mapsStory.lon)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title(mapsStory.name)
                                .snippet(mapsStory.description)
                        )
                        boundsBuilder.include(latLng)
                    }


                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )

                }
            }
            isLoading.observe(requireActivity()) {
                showLoading(it)
            }
        }
    }

    private fun showLoading(loading: Boolean?) {

    }
}