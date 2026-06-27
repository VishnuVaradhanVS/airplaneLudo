package com.example.airplaneludo.service

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateListOf

class LudoNetworkDiscovery(context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val serviceType = "_ludoroom._tcp."
    val discoveredRooms = mutableStateListOf<NsdServiceInfo>()
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    fun advertiseRoom(roomId: Int, hostName: String) {
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = "LudoRoom_${roomId}_By_${hostName}"
            serviceType = this@LudoNetworkDiscovery.serviceType
            port = 8080
        }
        registrationListener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                println("NSD: Service successfully registered: ${NsdServiceInfo.serviceName}")
            }

            override fun onRegistrationFailed(arg0: NsdServiceInfo, arg1: Int) {}
            override fun onServiceUnregistered(arg0: NsdServiceInfo) {}
            override fun onUnregistrationFailed(arg0: NsdServiceInfo, arg1: Int) {}
        }

        nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener)
    }

    fun startScanning() {
        discoveredRooms.clear()
        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                nsdManager.stopServiceDiscovery(this)
            }

            override fun onDiscoveryStarted(serviceType: String) {
                println("NSD: Service discovery started")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                println("NSD: Service discovery stopped")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                if (serviceInfo.serviceType == serviceType) {
                    resolveService(serviceInfo)
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                discoveredRooms.removeIf { it.serviceName == serviceInfo.serviceName }
            }
        }
        nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    private fun resolveService(serviceInfo: NsdServiceInfo) {
        nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e("NSD", "Resolve failed with error code: $errorCode")
                if (errorCode == NsdManager.FAILURE_ALREADY_ACTIVE) {
                    Thread.sleep(100)
                    resolveService(serviceInfo)
                }
            }

            override fun onServiceResolved(resolvedServiceInfo: NsdServiceInfo) {
                Handler(Looper.getMainLooper()).post {
                    if (discoveredRooms.none { it.serviceName == resolvedServiceInfo.serviceName }) {
                        discoveredRooms.add(resolvedServiceInfo)
                    }
                }
            }
        })
    }

    fun stopAll() {
        try {
            registrationListener?.let { nsdManager.unregisterService(it) }
        } catch (_: Exception) {
        }
        try {
            discoveryListener?.let { nsdManager.stopServiceDiscovery(it) }
        } catch (_: Exception) {
        }
    }
}