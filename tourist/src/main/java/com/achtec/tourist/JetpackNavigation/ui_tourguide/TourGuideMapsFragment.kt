package com.achtec.tourist.JetpackNavigation.ui_tourguide

import android.Manifest
import android.app.AlertDialog
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
import androidx.annotation.NonNull
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.achtec.tourist.R
import com.directions.route.AbstractRouting
import com.directions.route.Routing
import com.directions.route.RoutingListener
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList
import java.util.HashMap

class TourGuideMapsFragment : Fragment(), OnMapReadyCallback, RoutingListener {

    private var mMap: GoogleMap? = null
    var mLastLocation: Location? = null
    var mLocationRequest: LocationRequest? = null
    var mapFragment: SupportMapFragment? = null
    private lateinit var myLocation: LatLng
    private var myMarker: Marker? = null

    private var mCustomerInfo: LinearLayout? = null
    private val mAuth: FirebaseAuth? = null
    private val mCustomerDatabase: DatabaseReference? = null

    private var mFusedLoactionClient: FusedLocationProviderClient? = null

    private lateinit var mLogout: Button
    private lateinit var mSetting: Button
    private lateinit var mrideStatus: Button
    private lateinit var mHistory: Button

    private lateinit var mworkingSwitch: Switch

    private var status = 0

    private var rideDistance = 0f

    private var isLoggingOut = false

    private lateinit var mCustomerName: TextView
    private lateinit var mCustomerPhone: TextView
    private lateinit var mCustomerDestination: TextView
    private var customerId = ""
    private lateinit var destination: kotlin.String

    private var destinationLatLng: LatLng? = null




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_tour_guide_maps, container, false)

        mFusedLoactionClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        polylines = ArrayList()
        mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mCustomerInfo = v.findViewById(R.id.customerInfo) as LinearLayout
        mCustomerName = v.findViewById(R.id.customerName) as TextView
        mCustomerPhone = v.findViewById(R.id.customerPhone) as TextView
        mCustomerDestination = v.findViewById(R.id.customerDestination) as TextView
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            mapFragment!!.getMapAsync(this)
        }
        mSetting = v.findViewById<Button>(R.id.settings)
        mLogout = v.findViewById(R.id.logout)
        mrideStatus = v.findViewById(R.id.rideStatus)
        mworkingSwitch = v.findViewById(R.id.workingSwitch)
        mHistory = v.findViewById(R.id.history)
        mworkingSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                connectDriver()
            } else {
                disconnectDriver()
            }
        }
        mrideStatus.setOnClickListener(View.OnClickListener {
            when (status) {
                1 -> {
                    status = 2
                    erasePolylines()
                    if (destinationLatLng!!.latitude != 0.0 && destinationLatLng!!.longitude != 0.0) {
                        getRouteToMarker(destinationLatLng)
                    }
                    mrideStatus.setText("Tourist Assigned ! Please pickup the patient.")
                }
                2 -> {
                    recordRide()
                    endRide()
                }
            }
        })
        mLogout.setOnClickListener(View.OnClickListener {
            isLoggingOut = true
            disconnectDriver()
            FirebaseAuth.getInstance().signOut()
            requireActivity().finish()
            return@OnClickListener
        })
        mSetting.setOnClickListener(View.OnClickListener {


        })
        mHistory.setOnClickListener(View.OnClickListener {

        })
        getAssignedCustomer()
        return v
    }



    private fun endRide() {
        mrideStatus.setText("Pick Tourist")
        erasePolylines()
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val driverRef =
            FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(userId)
                .child("customerRequest")
        driverRef.removeValue()
        val ref = FirebaseDatabase.getInstance().getReference("customerRequest")
        val geoFire = GeoFire(ref)
        geoFire.removeLocation(customerId)
        customerId = ""
        rideDistance = 0f
        if (pickupMarker != null) {
            pickupMarker!!.remove()
        }
    }

    private fun recordRide() {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val driverRef =
            FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(userId)
                .child("history")
        val customerRef = FirebaseDatabase.getInstance().reference.child("Users").child("Customers")
            .child(customerId).child("history")
        val historyRef = FirebaseDatabase.getInstance().reference.child("History")
        val requestId = historyRef.push().key
        driverRef.child(requestId!!).setValue(true)
        customerRef.child(requestId).setValue(true)
        val map: HashMap<String?, Any?> = HashMap<String?, Any?>()
        map["driver"] = userId
        map["customer"] = customerId
        map["rating"] = 0
        map["timestamp"] = getCurrentTimestamp()
        map["destination"] = destination
        map["location/from/lat"] = myLocation!!.latitude
        map["location/from/lng"] = myLocation!!.longitude
        map["location/to/lat"] = destinationLatLng!!.latitude
        map["location/to/lng"] = destinationLatLng!!.longitude
        map["distance"] = rideDistance
        historyRef.child(requestId).updateChildren(map)
    }

    private fun getCurrentTimestamp(): Long? {
        return System.currentTimeMillis() / 1000
    }


    private fun getAssignedCustomer() {
        val driverId = FirebaseAuth.getInstance().currentUser!!.uid
        val assignedCustomerRef =
            FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(driverId)
                .child("customerRequest").child("customerRideId")
        assignedCustomerRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    status = 1
                    customerId = dataSnapshot.value.toString()
                    getAssignedCustomerPickupLocation()
                    getAssignedCustomerDestination()
                    getAssignedCustomerInfo()
                } else {
                    endRide()
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    private fun getAssignedCustomerInfo() {
        mCustomerInfo!!.visibility = View.VISIBLE
        val mCustomerDatabase =
            FirebaseDatabase.getInstance().reference.child("Users").child("Customers")
                .child(customerId)
        mCustomerDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.childrenCount > 0) {
                    val map = dataSnapshot.value as Map<*, *>?
                    if (map!!["Name"] != null) {
                        mCustomerName.text = map["Name"].toString()
                    }
                    if (map["Phone"] != null) {
                        mCustomerPhone.text = map["Phone"].toString()
                    }
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    private fun getAssignedCustomerDestination() {
        val driverId = FirebaseAuth.getInstance().currentUser!!.uid
        val assignedCustomerRef =
            FirebaseDatabase.getInstance().reference.child("Users").child("Drivers").child(driverId)
                .child("customerRequest")
        assignedCustomerRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val map = dataSnapshot.value as Map<String, Any?>?
                    if (map!!["destination"] != null) {
                        destination = map["destination"].toString()
                        mCustomerDestination.setText("destination: $destination")
                    } else {
                        mCustomerDestination.setText("destination: ---")
                    }
                    var destinationLat = 0.0
                    var destinationLng = 0.0
                    if (map["destinationLat"] != null) {
                        destinationLat = java.lang.Double.valueOf(map["destinationLat"].toString())
                    }
                    if (map["destinationLng"] != null) {
                        destinationLng = java.lang.Double.valueOf(map["destinationLng"].toString())
                        destinationLatLng = LatLng(destinationLat, destinationLng)
                    }
                }
            }

            override fun onCancelled(@NonNull databaseError: DatabaseError) {}
        })
    }

    var pickupMarker: Marker? = null
    private var assignedCustomerPickupLocationRef: DatabaseReference? = null
    private var assignedCustomerPickupLocationRefListener: ValueEventListener? = null
    private fun getAssignedCustomerPickupLocation() {
        assignedCustomerPickupLocationRef =
            FirebaseDatabase.getInstance().reference.child("customerRequest").child(customerId)
                .child("l")
        assignedCustomerPickupLocationRefListener =
            assignedCustomerPickupLocationRef!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(@NonNull dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists() && customerId != "") {
                        val map = dataSnapshot.value as List<Any?>?
                        var LocationLat = 0.0
                        var LocationLng = 0.0
                        if (map!![0] != null) {
                            LocationLat = map[0].toString().toDouble()
                        }
                        if (map[1] != null) {
                            LocationLng = map[1].toString().toDouble()
                        }
                        val pickupLatLng = LatLng(LocationLat, LocationLng)
                        pickupMarker = mMap!!.addMarker(
                            MarkerOptions().position(pickupLatLng).title("Pickup Location")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.person_24))
                        )
                        getRouteToMarker(pickupLatLng)
                    }
                }

                override fun onCancelled(@NonNull databaseError: DatabaseError) {}
            })
    }

    private fun getRouteToMarker(pickupLatLng: LatLng?) {
        val routing = Routing.Builder()
            .travelMode(AbstractRouting.TravelMode.DRIVING)
            .withListener(this)
            .alternativeRoutes(false)
            .waypoints(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude), pickupLatLng)
            .build()
        routing.execute()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 1000
        mLocationRequest!!.fastestInterval = 1000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) === PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) === PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
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
    }

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                if (requireActivity() != null) {
                    if (customerId != "" && mLastLocation != null && location != null) {
                        rideDistance += mLastLocation!!.distanceTo(location) / 1000
                    }
                    mLastLocation = location
                    val latLng = LatLng(
                        location!!.latitude, location.longitude
                    )
                    myLocation = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
                    myMarker = mMap!!.addMarker(
                        MarkerOptions().position(myLocation).title("Youre here")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.tour_guide3))
                    )
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                    mMap!!.animateCamera(CameraUpdateFactory.zoomTo(11f))
                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    val refAvailable =
                        FirebaseDatabase.getInstance().getReference("driversAvailable")
                    val refWorking = FirebaseDatabase.getInstance().getReference("driversWorking")
                    val geoFireAvailable = GeoFire(refAvailable)
                    val geoFireWorking = GeoFire(refWorking)
                    when (customerId) {
                        "" -> {
                            geoFireWorking.removeLocation(userId)
                            geoFireAvailable.setLocation(
                                userId,
                                GeoLocation(location.latitude, location.longitude)
                            )
                        }
                        else -> {
                            geoFireAvailable.removeLocation(userId)
                            geoFireWorking.setLocation(
                                userId,
                                GeoLocation(location.latitude, location.longitude)
                            )
                        }
                    }
                }
            }
        }
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
                        mFusedLoactionClient!!.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                        mMap!!.isMyLocationEnabled = true
                    }
                } else {
                    Toast.makeText(
                        requireActivity(),
                        "Please provide the permission...",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun connectDriver() {
        checkLocationPermission()
        mFusedLoactionClient!!.requestLocationUpdates(
            mLocationRequest,
            mLocationCallback,
            Looper.myLooper()
        )
        mMap!!.isMyLocationEnabled = true
    }

    private fun disconnectDriver() {
        if (mFusedLoactionClient != null) {
            mFusedLoactionClient!!.removeLocationUpdates(mLocationCallback)
        }
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("driversAvailable")
        val geoFire = GeoFire(ref)
        geoFire.removeLocation(userId)
    }


    private var polylines: MutableList<Polyline>? = null
    private val COLORS = intArrayOf(R.color.primary_dark_material_light)
    override fun onRoutingFailure(p0: com.directions.route.RouteException?) {
        if (p0 != null) {
            Toast.makeText(requireActivity(), "Error: " + p0.message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireActivity(), "Something went wrong, Try again", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onRoutingStart() {}

    override fun onRoutingSuccess(
        p0: ArrayList<com.directions.route.Route>?,
        shortestRouteIndex: Int
    ) {
        if (polylines!!.size > 0) {
            for (poly in polylines!!) {
                poly.remove()
            }
        }
        polylines = ArrayList()
        //add route(s) to the map.
        for (i in p0?.indices!!) {

            //In case of more than 5 alternative routes
            val colorIndex = i % COLORS.size
            val polyOptions = PolylineOptions()
            polyOptions.color(resources.getColor(COLORS[colorIndex]))
            polyOptions.width((10 + i * 3).toFloat())
            polyOptions.addAll(p0?.get(i)?.points)
            val polyline = mMap!!.addPolyline(polyOptions)
            (polylines as ArrayList<Polyline>).add(polyline)
            Toast.makeText(
                requireActivity(),
                "Route " + (i + 1) + ": distance - " + (p0[i].distanceValue
                    ?: String()) + ": duration - " + (p0.get(i).durationValue
                    ?: String()),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onRoutingCancelled() {}

    private fun erasePolylines() {
        for (line in polylines!!) {
            line.remove()
        }
        polylines!!.clear()
    }


    override fun onResume() {
        super.onResume()
        val supportActionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
        supportActionBar?.hide()

    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        if (!isLoggingOut) {
            disconnectDriver()
        }
    }



}