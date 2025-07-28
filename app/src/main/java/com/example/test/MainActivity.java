package com.example.test;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private TextView connectionTypeText, SSIDText, connectionStrengthText,
            networkTypeText, internetAccessText, mobileOperatorText, linkSpeedText, ipAddressText,

            wifiFrequencyText, networkCountryText;
    private ImageView signalStrengthImage;

    // NETWORK TYPE
    private String getNetworkTypeName(int type) {
        switch (type) {
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPA+";
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            case TelephonyManager.NETWORK_TYPE_NR:
                return "5G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            case TelephonyManager.NETWORK_TYPE_GSM:
                return "GSM";
            default:
                return "Other";
        }
    }

    // IP FORMATTING
    private String formatIpAddress(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 24) & 0xFF);
    }

    // SIGNAL STRENGTH IMAGE
    private void updateSignalIcon(int rssi) {
        ImageView signalImage = findViewById(R.id.signalStrengthImage);

        int iconResId;
        if (rssi <= -100) {
            iconResId = R.drawable.signal_wifi_0_bar_24px;
        } else if (rssi <= -85) {
            iconResId = R.drawable.signal_wifi_1_bar_24px;
        } else if (rssi <= -70) {
            iconResId = R.drawable.signal_wifi_2_bar_24px;
        } else if (rssi <= -55) {
            iconResId = R.drawable.signal_wifi_3_bar_24px;
        } else {
            iconResId = R.drawable.signal_wifi_4_bar_24px;
        }

        signalImage.setImageResource(iconResId);
    }


    // INFO UPDATES
    private void updateNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        // INTERNET ACCESS
        boolean hasInternet = false;
        Network network = cm.getActiveNetwork();
        NetworkCapabilities caps = cm.getNetworkCapabilities(network);
        hasInternet = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        internetAccessText.setText("\uD83C\uDF10 Internet access: " + (hasInternet ? "Available" : "Unavailable"));

        if (activeNetwork != null && activeNetwork.isConnected()) {
            String type = activeNetwork.getTypeName();


            //############ WI-FI ######################
            if (type.equalsIgnoreCase("WIFI")) {
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String SSID = wifiInfo.getSSID().replaceAll("^\"|\"$", "");
                int rssi = wifiInfo.getRssi();
                updateSignalIcon(rssi);

                connectionTypeText.setText("Wi-Fi");
                SSIDText.setText("\uD83D\uDCE1 SSID: " + SSID);
                connectionStrengthText.setText("\uD83D\uDCF6 Connection strength: " + wifiInfo.getRssi() + " dBm");
                linkSpeedText.setText("\uD83D\uDD04 Link speed: " + wifiInfo.getLinkSpeed() + " " + WifiInfo.LINK_SPEED_UNITS);
                ipAddressText.setText("\uD83D\uDD22 IP-address (local): " + formatIpAddress(wifiInfo.getIpAddress()));
                wifiFrequencyText.setText("\uD83D\uDCF6 Wi-Fi Frequency: " + wifiInfo.getFrequency() + " Hz");
                mobileOperatorText.setText("\uD83D\uDCF1 Mobile operator: -");

                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    networkTypeText.setText("\uD83D\uDCCD Network type: " + getNetworkTypeName(tm.getDataNetworkType()));
                } else {
                    networkTypeText.setText("\uD83D\uDCCD Network type: permission not granted");
                }

                if (tm.getNetworkCountryIso() == null || tm.getNetworkCountryIso().isEmpty()) {
                    networkCountryText.setText("\uD83C\uDF0D Network country: " + tm.getSimCountryIso().toUpperCase());
                } else {
                    networkCountryText.setText("\uD83C\uDF0D Network country: " + tm.getNetworkCountryIso().toUpperCase());
                }

            } else {
                connectionTypeText.setText("Mobile Network");
                SSIDText.setText("\uD83D\uDCE1 SSID: -");
                signalStrengthImage.setImageResource(R.drawable.cell_wifi_24px);
                ipAddressText.setText("\uD83D\uDD22 IP-address (local): -");
                linkSpeedText.setText("\uD83D\uDD04 Link speed: -");
                wifiFrequencyText.setText("\uD83D\uDCF6 Wi-Fi Frequency: -");
                connectionStrengthText.setText("\uD83D\uDCF6 Connection strength: -");

                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                    networkTypeText.setText("\uD83D\uDCCD Network type: " + getNetworkTypeName(tm.getDataNetworkType()));
                } else {
                    networkTypeText.setText("\uD83D\uDCCD Network type: permission not granted");
                }

                if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
                    mobileOperatorText.setText("\uD83D\uDCF1 Mobile operator: " + tm.getSimOperatorName()
                            + " (" + tm.getNetworkOperator() + ")");
                } else {
                    mobileOperatorText.setText("SIM not available");
                }

                if (tm.getNetworkCountryIso() == null || tm.getNetworkCountryIso().isEmpty()) {
                    networkCountryText.setText("\uD83C\uDF0D Network country: " + tm.getSimCountryIso().toUpperCase());
                } else {
                    networkCountryText.setText("\uD83C\uDF0D Network country: "+ tm.getNetworkCountryIso().toUpperCase());
                }
            }

        } else {
            signalStrengthImage.setImageResource(R.drawable.signal_wifi_bad_24px);
            connectionTypeText.setText("No connection");
            SSIDText.setText("\uD83D\uDCE1 Wi-fi SSID: -");
            connectionStrengthText.setText("\uD83D\uDCF6 Connection strength: -");
            networkTypeText.setText("\uD83D\uDCCD Network type: -");
            internetAccessText.setText("\uD83C\uDF10 Internet access: Unavailable");
            mobileOperatorText.setText("\uD83D\uDCF1 Mobile operator: -");
            wifiFrequencyText.setText("\uD83D\uDCF6 Wi-Fi Frequency: -");
            networkCountryText.setText("\uD83C\uDF0D Network country: -");
            linkSpeedText.setText("\uD83D\uDD04 Link speed: -");
            ipAddressText.setText("\uD83D\uDD22 IP-address (local): -");
        }
    }


    // MAIN
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI CONNECTION
        connectionTypeText = findViewById(R.id.connectionTypeText);
        SSIDText = findViewById(R.id.SSIDText);
        connectionStrengthText = findViewById(R.id.connectionStrengthText);
        networkTypeText = findViewById(R.id.networkTypeText);
        internetAccessText = findViewById(R.id.internetAccessText);
        mobileOperatorText = findViewById(R.id.mobileOperatorText);
        linkSpeedText = findViewById(R.id.linkSpeedText);
        ipAddressText = findViewById(R.id.ipAddressText);
        ImageButton refreshButton = findViewById(R.id.refreshButton);
        signalStrengthImage = findViewById(R.id.signalStrengthImage);
        wifiFrequencyText = findViewById(R.id.wifiFrequencyText);
        networkCountryText = findViewById(R.id.networkCountryText);

        // PERMISSION CHECK
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.READ_PHONE_STATE
                    },
                    LOCATION_PERMISSION_REQUEST
            );
        } else {
            updateNetworkInfo();
        }

        refreshButton.setOnClickListener(v -> updateNetworkInfo());

    }
}
