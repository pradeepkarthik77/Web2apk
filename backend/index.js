const express = require('express');
const axios = require('axios');
const fs = require('fs');
const secrets = require('./config.json');
const bodyParser = require('body-parser');
const path = require('path');
const { execSync } = require('child_process');
const { Octokit } = require('@octokit/rest');
const MongoClient = require('mongodb').MongoClient

const app = express();

const url = secrets.mongodb_url ;  
const client = new MongoClient(url);
const database = client.db("web2apk");
const ApkList = database.collection("AppList");

app.use(bodyParser.urlencoded({ limit: '10mb', extended: true }));
app.use(bodyParser.json({ limit: '10mb' }));
app.use(express.json());
// const projectPath = "/home/pradeep/AndroidStudioProjects/webviewapk";

const projectPath = "/home/pradeepkarthikm/WebView_Wrapper";

const appicon_path = path.join(projectPath, "app/src/main/res/drawable/app_icon.png");
const MainActivity_path = path.join(projectPath, "app/src/main/java/com/example/webviewapk/MainActivity.java");
const splash_fragment_path = path.join(projectPath, "app/src/main/java/com/example/webviewapk/splash_fragment.java");
const gradle_build_path = path.join(projectPath, "app/build.gradle.kts");
const manifest_file_path = path.join(projectPath, "app/src/main/AndroidManifest.xml");
const strings_path = path.join(projectPath, "app/src/main/res/values/strings.xml");
const colors_path = path.join(projectPath, "app/src/main/res/values/colors.xml");
// const apk_files_store = path.join(projectPath, "Downloads/Web2APK_collection")
const apk_path = path.join(projectPath, "app/build/outputs/apk/debug/app-debug.apk");


async function makeRequest(config) {
    try {
        const response = await axios(config);
        console.log(JSON.stringify(response.data));
    } catch (error) {
        console.error(error);
    }
}


async function replaceFirstLineAsync(filename, newFirstLine) {
    const data = await fs.promises.readFile(filename, 'utf8');
    const lines = data.split('\n');
    lines[0] = newFirstLine;
    await fs.promises.writeFile(filename, lines.join('\n'), 'utf8');
}

  async function uploadFileApi(appname) {

    const file = fs.readFileSync(apk_path);

    const token = secrets.github_token;

    const data = {
        message: "Upload APK files",
        content: file.toString('base64'),
        encoding: 'base64', // Specify encoding as base64 for binary files
    };

    const config = {
        method: 'put',
        url: `https://api.github.com/repos/pradeepkarthik77/Web2APK_collection/contents/${appname}.apk`,
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
        data: data,
    };

    await makeRequest(config);
}   
    
app.post("/create_app", async (req, res) => {
    try {

        const appname = req.body.appname.trim();
        const weblink = req.body.applink;
        const appcolor = req.body.appcolor;
        const permission = req.body.permission;
        const webCache = req.body.webCache;
        const appicon = req.body.appicon;

        // res.status(200).send({appname:appname,apklink: 'https://github.com/pradeepkarthik77/Web2APK_collection/blob/main/Bard.apk' });
        // return;

        console.log("received create_app");
        console.log(appname, weblink, appcolor, permission, webCache);


        const result = await ApkList.findOne({appname: appname})

        if(result != null)
        {
            res.status(401).send();
            return;
        }

        const buffer = Buffer.from(appicon, 'base64');
        await fs.promises.writeFile(appicon_path, buffer);
        console.log('Image saved as app_icon.png');

        const package_name = appname.toLowerCase();

        // await replaceFirstLineAsync(MainActivity_path, 'package com.example.' + package_name + ";");
        // console.log('First line replaced successfully for MainActivity');

        const MainActivity_string = `package com.example.${package_name};

        import androidx.annotation.NonNull;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import android.Manifest;
        import android.annotation.TargetApi;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.view.KeyEvent;
        import android.view.View;
        import android.webkit.PermissionRequest;
        import android.webkit.WebChromeClient;
        import android.webkit.WebResourceRequest;
        import android.webkit.WebSettings;
        import android.webkit.WebView;
        import android.webkit.WebViewClient;
        import android.widget.FrameLayout;
        import android.widget.ProgressBar;
        import android.widget.Toast;
        
        import java.util.ArrayList;
        import java.util.List;
        
        public class MainActivity extends AppCompatActivity {
            WebView webView; //declare the webview
            ProgressBar progressBar;
        
            FrameLayout frameLayout;
        
            private PermissionRequest mPermissionRequest;
        
            SharedPreferences sharedPreferences;
        
        
            private static final int PERMISSION_REQUEST_CODE = 1234;
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event) {
        
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return super.onKeyDown(keyCode, event);
            }
        
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
        
                this.progressBar = findViewById(R.id.progressBar);
        
                this.webView = findViewById(R.id.web_view);
        
                this.frameLayout = findViewById(R.id.fragment_container);
        
                sharedPreferences = getSharedPreferences("permissions", MODE_PRIVATE);
        
                if(!sharedPreferences.getBoolean("hasAssigned",false))
                {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
        
                    editor.putString("permission","${permission}");
        
                    editor.putBoolean("hasAssigned",true);
        
                    editor.commit();
                }
        
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new splash_fragment())
                        .commit();
        
                webView.loadUrl(getString(R.string.base_URL));
        
                WebSettings webSettings = webView.getSettings();
                webSettings.setDomStorageEnabled(true);
                webSettings.setJavaScriptEnabled(true);
                webView.getSettings().setAllowFileAccess(true);
        
                this.webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT); // Use cache when content is available
        
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);
        
                webView.setNestedScrollingEnabled(true);
        
                webView.getSettings().setSupportZoom(true); //used for providing some flexibilty while zooming in or out
                webView.getSettings().setBuiltInZoomControls(true);
                webView.getSettings().setDisplayZoomControls(false);
        
                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                webView.setScrollbarFadingEnabled(false);
        
        //        webSettings.setUserAgentString(webSettings.getUserAgentString().replace("; wv",""));
                webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 10; Pixel 4 XL Build/QQ3A.200805.001) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.82 Mobile Safari/537.36");
        
        //        Toast.makeText(this,webSettings.getUserAgentString(),Toast.LENGTH_LONG).show();
                webView.setWebViewClient(new WebViewClient() {
        
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        view.loadUrl(request.getUrl().toString());
                        return false;
                    }
                });
        
                this.webView.setWebChromeClient(new WebChromeClient(){
        
                    @Override
                    public void onPermissionRequest(final PermissionRequest request) {
                        mPermissionRequest = request; // Store the permission request
                        runOnUiThread(() -> {
                            String[] requestedResources = request.getResources();
        
                            String[] androidPermissions = get_permissions();
        
                            String permission = "";
        
                            for(String perm: requestedResources)
                            {
                                permission+=perm;
                            }
        
                            Toast.makeText(getApplicationContext(),permission,Toast.LENGTH_LONG).show();
        
                            if (hasAllPermissions(androidPermissions)) {
                                request.grant(requestedResources);
                            } else {
                                ActivityCompat.requestPermissions(MainActivity.this, androidPermissions, PERMISSION_REQUEST_CODE);
                            }
                        });
                    }
        
                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        progressBar.setProgress(newProgress);
                        if (newProgress == 100) {
                            progressBar.setVisibility(View.INVISIBLE);
                            frameLayout.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        
            private boolean hasAllPermissions(String[] permissions) {
                for (String permission : permissions) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
                return true;
            }
        
            private String[] mapToAndroidPermissions(List<String> webResources) {
                List<String> permissions = new ArrayList<>();
                permissions.add(Manifest.permission.CAMERA);
                permissions.add(Manifest.permission.RECORD_AUDIO);
        
                return permissions.toArray(new String[0]);
            }
        
            private String[] get_permissions() {
                String permission = sharedPreferences.getString("permission", "0000");
                List<String> permissions = new ArrayList<>();
        
                if (permission.charAt(0) == '1') {
                    permissions.add(android.Manifest.permission.CAMERA);
                    permissions.add(android.Manifest.permission.RECORD_AUDIO);
                }
                if (permission.charAt(1) == '1') {
                    permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                    permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
                    permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION);
                }
                if (permission.charAt(2) == '1') {
                    permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                    permissions.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (permission.charAt(3) == '1') {
                    permissions.add(android.Manifest.permission.BLUETOOTH);
                    permissions.add(android.Manifest.permission.BLUETOOTH_ADMIN);
                    permissions.add(android.Manifest.permission.BLUETOOTH_CONNECT);
                    permissions.add(android.Manifest.permission.BLUETOOTH_ADVERTISE);
                    permissions.add(android.Manifest.permission.BLUETOOTH_SCAN);
                }
        
                return permissions.toArray(new String[0]);
            }
        
        
        
            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (requestCode == PERMISSION_REQUEST_CODE) {
                    Toast.makeText(getApplicationContext(),"Permissions Granted",Toast.LENGTH_LONG).show();
                }
            }
        }`

        await fs.promises.writeFile(MainActivity_path, MainActivity_string);
        console.log('MainActivity File written successfully!');


        await replaceFirstLineAsync(splash_fragment_path, 'package com.example.' + package_name + ";");
        console.log('First line replaced successfully for splash_fragment');

        const gradleConfig = `
        plugins {
            id("com.android.application")
        }

        android {
            namespace = "com.example.${package_name}"
            compileSdk = 33

            defaultConfig {
                applicationId = "com.example.${package_name}"
                minSdk = 24
                targetSdk = 33
                versionCode = 1
                versionName = "1.0"

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                    proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
                }
            }
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
        }

        dependencies {
            implementation("androidx.appcompat:appcompat:1.6.1")
            implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
            implementation("com.google.android.material:material:1.8.0")
            testImplementation("junit:junit:4.13.2")
            androidTestImplementation("androidx.test.ext:junit:1.1.5")
            androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
        }
        `;

        await fs.promises.writeFile(gradle_build_path, gradleConfig);
        console.log('Gradle File written successfully!');

        var permission_string = "";

        if(permission[0] == "1")
        {
            permission_string+=` <uses-feature
            android:name="android.hardware.camera"
            android:required="false" />
            <uses-permission android:name="android.permission.CAMERA" />
            <uses-permission android:name="android.permission.RECORD_AUDIO" />`;
        }
        if(permission[1] == "1")
        {
            permission_string+=`<!-- Coarse location (network-based) -->
            <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
            <!-- Fine location (GPS) -->
            <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
            <!-- If targeting Android 10 (API level 29) or higher -->
            <uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
            `;
        }
        if(permission[2] == "1")
        {
            permission_string+=`<!-- Read external storage -->
            <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
            <!-- Write to external storage -->
            <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />            
            `;
        }
        if(permission[3] == "1")
        {
            permission_string+=`<!-- Required for any Bluetooth communication -->
            <uses-permission android:name="android.permission.BLUETOOTH" />
            <!-- Required for discovering devices -->
            <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
            <!-- Required for accessing device location during Bluetooth scans on Android 13 (API level 33) and higher -->
            <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
            <!-- Required for Bluetooth device pairing -->
            <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
            <!-- Optional: for BLE (Bluetooth Low Energy) -->
            <uses-permission android:name="android.permission.BLUETOOTH_ADVERTISE" />
            `;
        }

        const manifest_string = `<?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            xmlns:tools="http://schemas.android.com/tools">
        
            <uses-permission android:name="android.permission.INTERNET" />

            ${permission_string}
        
            <application
                android:allowBackup="true"
                android:dataExtractionRules="@xml/data_extraction_rules"
                android:fullBackupContent="@xml/backup_rules"
                android:icon="@drawable/app_icon"
                android:label="@string/app_name"
                android:roundIcon="@drawable/app_icon"
                android:supportsRtl="true"
                android:theme="@style/Theme.Webviewapk"
                android:hardwareAccelerated="true"
                tools:targetApi="31">
        
                <activity
                    android:name=".MainActivity"
                    android:exported="true">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN" />
        
                        <category android:name="android.intent.category.LAUNCHER" />
                    </intent-filter>
        
                    <meta-data
                        android:name="android.app.lib_name"
                        android:value="" />
                </activity>
        
            </application>
        
        </manifest>`;

        await fs.promises.writeFile(manifest_file_path, manifest_string);
        console.log('Manifest File written successfully!');

        const colors_string = `<?xml version="1.0" encoding="utf-8"?>
        <resources>
            <color name="black">#FF000000</color>
            <color name="white">#FFFFFFFF</color>
            <color name="given_color">${appcolor}</color>
        </resources>`

        await fs.promises.writeFile(colors_path, colors_string);
        console.log('Colors.xml File written successfully!');


        const strings_string = `<resources>
        <string name="app_name">${appname}</string>
        <string name="base_URL">${weblink}</string>
    </resources>`

        await fs.promises.writeFile(strings_path, strings_string);
        console.log('Strings.xml File written successfully!');

        const cmd = `cd ${projectPath} && ./gradlew clean && ./gradlew assembleDebug`;
        const output = execSync(cmd);
        console.log(output.toString());
        console.log('Gradle sync completed.');

        await uploadFileApi(appname);

        let objecttoupdate = {}

        objecttoupdate.appname = appname;
        objecttoupdate.applink = weblink;
        objecttoupdate.apklink = `https://github.com/pradeepkarthik77/Web2APK_collection/blob/main/${appname}.apk`;

        const result2 = await ApkList.insertOne(objecttoupdate);

        res.status(200).send({appname:appname,apklink: objecttoupdate.apklink });
    } catch (error) {
        console.error('Error during operations:', error);
        // res.status(500).send("Internal Server Error");
    }
});

app.listen(5000, () => {
    console.log("Listening on port 5000....");
});
