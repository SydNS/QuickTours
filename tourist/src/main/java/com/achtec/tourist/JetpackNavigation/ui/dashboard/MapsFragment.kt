package com.achtec.tourist.JetpackNavigation.ui.dashboard

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.achtec.tourist.R
import com.achtec.tourist.databinding.FragmentMapsBinding
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQuery
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.*
import com.google.android.gms.location.places.Place
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment

import com.google.android.gms.location.places.ui.PlaceSelectionListener


import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList
import java.util.HashMap

class MapsFragment : Fragment(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    LocationListener {
    lateinit var fragmentMapsBinding: FragmentMapsBinding
    private var mMap: GoogleMap? = null
    var mLastLocation: Location? = null
    var mLocationRequest: LocationRequest? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var mLogout: Button? =
        null
    private var mRequest: android.widget.Button? = null
    private var mSettings: android.widget.Button? = null
    private var mHistory: android.widget.Button? = null
    private var pickupLocation: LatLng? = null
    private var requestBol = false
    private var pickupMarker: Marker? = null

    private var mapFragment: SupportMapFragment? = null

    private var destination: String? = null

    private var destinationLatlng: LatLng? = null

    private var mRatingBar: RatingBar? = null

    private var mDriverInfo: LinearLayout? = null

    private var mDriverName: TextView? = null
    private var mDriverPhone: TextView? = null
    private var mDriverCar: TextView? = null

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        val sydney = LatLng(-34.0, 151.0)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMapsBinding = FragmentMapsBinding.inflate(inflater, container, false)


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)

        destinationLatlng = LatLng(0.0, 0.0)

        mDriverInfo = fragmentMapsBinding.driverInfo as LinearLayout

        mDriverName = fragmentMapsBinding.driverName as TextView
        mDriverPhone = fragmentMapsBinding.driverPhone as TextView
        mDriverCar = fragmentMapsBinding.driverCar as TextView

        mRequest = fragmentMapsBinding.request as Button
        mSettings = fragmentMapsBinding.settings as Button
        mRatingBar = fragmentMapsBinding.ratingBar as RatingBar
        mHistory = fragmentMapsBinding.history as Button
        mLogout = fragmentMapsBinding.logout as Button


        fragmentMapsBinding.orderButton.setOnClickListener {

            findNavController().navigate(R.id.startTour)
        }


        mLogout!!.setOnClickListener(View.OnClickListener {
            FirebaseAuth.getInstance().signOut()

            activity?.finish()
        })


        mRequest!!.setOnClickListener {
            if (requestBol) {
                endRide()
            } else {
            }
            requestBol = true
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val ref = FirebaseDatabase.getInstance().getReference("customerRequest")
            val geoFire = GeoFire(ref)
            geoFire.setLocation(
                userId,
                GeoLocation(mLastLocation!!.latitude, mLastLocation!!.longitude)
            )
            pickupLocation = LatLng(
                mLastLocation!!.latitude,
                mLastLocation!!.longitude
            )
            pickupMarker = mMap!!.addMarker(
                MarkerOptions().position(pickupLocation).title("Pickup Here")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.tour_guide))
            )
            mRequest!!.text = "Getting Your Tour Guide..."
            getClosestDriver()
        }


        mSettings!!.setOnClickListener(View.OnClickListener {

            findNavController().navigate(R.id.action_signupTourguide_to_nav_home)
        })


        mHistory!!.setOnClickListener {
        }

        if (!Places.isInitialized()) {
            Places.initialize(requireActivity(), "AIzaSyBqi1z_qeYRs14m9snAo2124KYY" );
        }

        val autocompleteFragment =
            requireFragmentManager().findFragmentById(R.id.place_autocomplete_fragment) as PlaceAutocompleteFragment?

        autocompleteFragment?.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                destination = place.name.toString()
                destinationLatlng = place.latLng
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
            }
        })

        return fragmentMapsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }


    //    methods


    private var radius = 1
    private var driverFound = false
    private var driverFoundID: String? = null

    var geoQuery: GeoQuery? = null


    private fun getClosestDriver() {
        val driverLocation = FirebaseDatabase.getInstance().reference.child("driversAvailable")
        val geoFire = GeoFire(driverLocation)
        geoQuery = geoFire.queryAtLocation(
            GeoLocation(
                pickupLocation!!.latitude,
                pickupLocation!!.longitude
            ), radius.toDouble()
        )
        geoQuery?.removeAllListeners()
        geoQuery?.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String?, location: GeoLocation?) {
                if (!driverFound && requestBol) {
                    val mCustomerDatabase =
                        FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
                            .child(
                                key!!
                            )
                    mCustomerDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                                // Map<String, Object> driverMap = (Map<String, Object>) dataSnapshot.getValue();
                                if (driverFound) {
                                    return
                                }
                                driverFound = true
                                driverFoundID = dataSnapshot.key
                                val driverRef =
                                    FirebaseDatabase.getInstance().reference.child("Users")
                                        .child("Drivers").child(
                                            driverFoundID!!
                                        ).child("customerRequest")
                                val customerId = FirebaseAuth.getInstance().currentUser!!
                                    .uid
                                val map: HashMap<String?, Any?> = HashMap<String?, Any?>()
                                map["customerRideId"] = customerId
                                map["destination"] = destination
                                map["destinationLat"] = destinationLatlng!!.latitude
                                map["destinationLng"] = destinationLatlng!!.longitude
                                driverRef.updateChildren(map)
                                getDriverLocation()
                                getDriverInfo()
                                getHasRideEnded()
                                mRequest!!.text = "Looking for Driver's Location...."
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })
                }
            }

            override fun onKeyExited(key: String?) {}
            override fun onKeyMoved(key: String?, location: GeoLocation?) {}
            override fun onGeoQueryReady() {
                if (!driverFound) {
                    radius++
                    getClosestDriver()
                }
            }

            override fun onGeoQueryError(error: DatabaseError?) {}
        })
    }

    private var mDriverMarker: Marker? = null
    private var driverLocationRef: DatabaseReference? = null
    private var driverLocationRefListener: ValueEventListener? = null
    private fun getDriverLocation() {
        driverLocationRef =
            FirebaseDatabase.getInstance().reference.child("driversWorking").child(driverFoundID!!)
                .child("l")
        driverLocationRefListener =
            driverLocationRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && requestBol) {
                        val map = dataSnapshot.value as List<Any?>?
                        var LocationLat = 0.0
                        var LocationLng = 0.0
                        // mRequest.setText("Ambulance Found");
                        if (map!![0] != null) {
                            LocationLat = map[0].toString().toDouble()
                        }
                        if (map[1] != null) {
                            LocationLng = map[1].toString().toDouble()
                        }
                        val driverLatLng = LatLng(LocationLat, LocationLng)
                        if (mDriverMarker != null) {
                            mDriverMarker!!.remove()
                        }
                        val loc1 = Location("")
                        loc1.latitude = pickupLocation!!.latitude
                        loc1.longitude = pickupLocation!!.longitude
                        val loc2 = Location("")
                        loc2.latitude = driverLatLng.latitude
                        loc2.longitude = driverLatLng.longitude
                        val distance = loc1.distanceTo(loc2)
                        if (distance < 100) {
                            mRequest!!.text = "Tour Guide Arrived"
                        } else {
                            val dis = distance.toInt() / 1000
                            mRequest!!.text = "Tour Guide Found: $dis Kms away..."
                        }
                        mDriverMarker = mMap!!.addMarker(
                            MarkerOptions().position(driverLatLng).title("Your Guide")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.tour_guide))
                        )
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    private fun getDriverInfo() {
        mDriverInfo!!.visibility = View.VISIBLE
        val mCustomerDatabase =
            FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
                .child(driverFoundID!!)
        mCustomerDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    if (dataSnapshot.child("Name") != null) {
                        mDriverName!!.text = dataSnapshot.child("Name").value.toString()
                    }
                    if (dataSnapshot.child("Phone") != null) {
                        mDriverPhone!!.text = dataSnapshot.child("Phone").value.toString()
                    }
                    if (dataSnapshot.child("Car") != null) {
                        mDriverCar!!.text = dataSnapshot.child("Car").value.toString()
                    }
                    var ratingSum = 0
                    var ratingsTotal = 0f
                    var ratingsAvg = 0f
                    for (child in dataSnapshot.child("rating").children) {
                        ratingSum += Integer.valueOf(child.value.toString())
                        ratingsTotal++
                    }
                    if (ratingsTotal != 0f) {
                        ratingsAvg = ratingSum / ratingsTotal
                        mRatingBar!!.rating = ratingsAvg
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) === PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                checkLocationPermission()
            }
        }
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
        mMap!!.isMyLocationEnabled = true
    }

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLastLocation = location
                val latLng = LatLng(location.latitude, location.longitude)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))
                if (!getDriversAroundStarted) getDriversAround()
            }
        }
    }

    var getDriversAroundStarted = false
    var markers: MutableList<Marker> = ArrayList()
    private fun getDriversAround() {
        getDriversAroundStarted = true
        val driverLocation = FirebaseDatabase.getInstance().reference.child("driversAvailable")
        val geoFire = GeoFire(driverLocation)
        val geoQuery: GeoQuery = geoFire.queryAtLocation(
            GeoLocation(
                mLastLocation!!.longitude,
                mLastLocation!!.latitude
            ), 999999999.0
        )
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            override fun onKeyEntered(key: String, location: GeoLocation) {
                val driverLocation = LatLng(location.latitude, location.longitude)
                val mDriverMarker = mMap!!.addMarker(
                    MarkerOptions().position(driverLocation).title(key)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.tour_guide))
                )
                mDriverMarker.setTag(key)
                markers.add(mDriverMarker)
                for (markerIt in markers) {
                    if (markerIt.tag == key) return
                }
            }

            override fun onKeyExited(key: String) {
                for (markerIt in markers) {
                    if (markerIt.tag == key) {
                        markerIt.remove()
                    }
                }
            }

            override fun onKeyMoved(key: String, location: GeoLocation) {
                for (markerIt in markers) {
                    if (markerIt.tag == key) {
                        markerIt.position =
                            LatLng(location.latitude, location.longitude)
                    }
                }
            }

            override fun onGeoQueryReady() {}
            override fun onGeoQueryError(error: DatabaseError?) {}
        })
    }


    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(requireActivity())
                    .setTitle("Please give permission...")
                    .setMessage("Please give permission...")
                    .setPositiveButton(
                        "OK"
                    ) { dialog, which ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            requireActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) === PackageManager.PERMISSION_GRANTED
                    ) {
                        mFusedLocationClient!!.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                        mMap!!.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(
                        activity,
                        "Please provide the permission...",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private var driveHasEndedRef: DatabaseReference? = null
    private var driveHasEndedRefListener: ValueEventListener? = null
    private fun getHasRideEnded() {
        // String driverId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        driveHasEndedRef = FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
            .child(driverFoundID!!).child("customerRequest").child("customerRideId")
        driveHasEndedRefListener =
            driveHasEndedRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                    } else {
                        endRide()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }


    private fun endRide() {
        requestBol = false
        if (geoQuery != null) {
            geoQuery?.removeAllListeners()
        }
        if (driverLocationRefListener != null && driveHasEndedRefListener != null) {
            driverLocationRef?.removeEventListener(driverLocationRefListener!!)
            driveHasEndedRef?.removeEventListener(driveHasEndedRefListener!!)
        }
        if (driverFoundID != null) {
            val driverRef = FirebaseDatabase.getInstance().reference.child("Users").child("Drivers")
                .child(driverFoundID!!).child("customerRequest")
            driverRef.removeValue()
            driverFoundID = null
        }
        driverFound = false
        radius = 1
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("customerRequest")
        val geoFire = GeoFire(ref)
        geoFire.removeLocation(userId)
        pickupMarker?.remove()
        if (mDriverMarker != null) {
            mDriverMarker?.remove()
        }
        mRequest!!.text = "Request An Ambulance"
        mDriverInfo!!.visibility = View.GONE
        mDriverName!!.text = ""
        mDriverPhone!!.text = ""
    }

    override fun onConnected(p0: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun onConnectionSuspended(p0: Int) {
        TODO("Not yet implemented")
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("Not yet implemented")
    }

    override fun onLocationChanged(p0: Location?) {
        TODO("Not yet implemented")
    }
}