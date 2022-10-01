# VOSK-Powered-LIVE-SUBTITLE
ANDROID APP that can RECOGNIZE words of ANY LIVE AUDIO/VIDEO STREAMING (using VOSK Speech Recognition Framework) then TRANSLATE (powered by ANDROID MLKIT TRANSLATE) and display them as SUBTITLES

This app has PROS & CONS compared to another Speech Recognition Framework like those free Android Developer Speech Recognition, IBM WATSON, and PREMIUM GOOGPLE SPEECH API

PROS:
It's FREE, FULL DUPLEX (CAN DIRECTLY LISTEN TO ANY AUDIO/VIDEO MEDIA PLAYERS inlcuding WEB BROWSERS!) and has a quietly good enough accuracy

CONS:
It's currently only support 20 languages and NEED BIG STORAGE to store those all LANGUAGE MODELS

NOTES:
Gitgub doesn't support big files upload, so I can only upload English model to this git, but you can try complete compiled apk in Realese page (about 1.4GB size!)

To use another language models in this source please download those SMALL MODELS from VOSK website https://alphacephei.com/vosk/models then extract them into models/src/main/assets folders with FOLDERS NAME in ISO CODES like en-US, ca-ES, de-DE, etc, just as they writen in models/build.gradle.

```
apply plugin: 'com.android.library'

android {
    compileSdkVersion 32
    defaultConfig {
        minSdkVersion 23
        targetSdkVersion 32
    }
    buildFeatures {
        buildConfig = false
    }
    sourceSets {
        main {
            assets.srcDirs += "$buildDir/generated/assets"
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

tasks.register('genUUID') {
    def uuid0 = UUID.randomUUID().toString()
    def odir0 = file("$buildDir/generated/assets/en-US")
    def ofile0 = file("$odir0/uuid")
    doLast {
        mkdir odir0
        ofile0.text = uuid0
    }

    def uuid1 = UUID.randomUUID().toString()
    def odir1 = file("$buildDir/generated/assets/zh-CN")
    def ofile1 = file("$odir1/uuid")
    doLast {
        mkdir odir1
        ofile1.text = uuid1
    }

    def uuid2 = UUID.randomUUID().toString()
    def odir2 = file("$buildDir/generated/assets/ru-RU")
    def ofile2 = file("$odir2/uuid")
    doLast {
        mkdir odir2
        ofile2.text = uuid2
    }

    def uuid3 = UUID.randomUUID().toString()
    def odir3 = file("$buildDir/generated/assets/fr-FR")
    def ofile3 = file("$odir3/uuid")
    doLast {
        mkdir odir3
        ofile3.text = uuid3
    }

    def uuid4 = UUID.randomUUID().toString()
    def odir4 = file("$buildDir/generated/assets/de-DE")
    def ofile4 = file("$odir4/uuid")
    doLast {
        mkdir odir4
        ofile4.text = uuid4
    }

    def uuid5 = UUID.randomUUID().toString()
    def odir5 = file("$buildDir/generated/assets/es-ES")
    def ofile5 = file("$odir5/uuid")
    doLast {
        mkdir odir5
        ofile5.text = uuid5
    }

    def uuid6 = UUID.randomUUID().toString()
    def odir6 = file("$buildDir/generated/assets/pt-PT")
    def ofile6 = file("$odir6/uuid")
    doLast {
        mkdir odir6
        ofile6.text = uuid6
    }

    def uuid7 = UUID.randomUUID().toString()
    def odir7 = file("$buildDir/generated/assets/tr-TR")
    def ofile7 = file("$odir7/uuid")
    doLast {
        mkdir odir7
        ofile7.text = uuid7
    }

    def uuid8 = UUID.randomUUID().toString()
    def odir8 = file("$buildDir/generated/assets/vi-VN")
    def ofile8 = file("$odir8/uuid")
    doLast {
        mkdir odir8
        ofile8.text = uuid8
    }

    def uuid9 = UUID.randomUUID().toString()
    def odir9 = file("$buildDir/generated/assets/it-IT")
    def ofile9 = file("$odir9/uuid")
    doLast {
        mkdir odir9
        ofile9.text = uuid9
    }

    def uuid10 = UUID.randomUUID().toString()
    def odir10 = file("$buildDir/generated/assets/nl-NL")
    def ofile10 = file("$odir10/uuid")
    doLast {
        mkdir odir10
        ofile10.text = uuid10
    }

    def uuid11 = UUID.randomUUID().toString()
    def odir11 = file("$buildDir/generated/assets/ca-ES")
    def ofile11 = file("$odir11/uuid")
    doLast {
        mkdir odir11
        ofile11.text = uuid11
    }

    def uuid12 = UUID.randomUUID().toString()
    def odir12 = file("$buildDir/generated/assets/fa-IR")
    def ofile12 = file("$odir12/uuid")
    doLast {
        mkdir odir12
        ofile12.text = uuid12
    }

    def uuid13 = UUID.randomUUID().toString()
    def odir13 = file("$buildDir/generated/assets/uk-UA")
    def ofile13 = file("$odir13/uuid")
    doLast {
        mkdir odir13
        ofile13.text = uuid13
    }

    def uuid14 = UUID.randomUUID().toString()
    def odir14 = file("$buildDir/generated/assets/kk-KZ")
    def ofile14 = file("$odir14/uuid")
    doLast {
        mkdir odir14
        ofile14.text = uuid14
    }

    def uuid15 = UUID.randomUUID().toString()
    def odir15 = file("$buildDir/generated/assets/sv-SE")
    def ofile15 = file("$odir15/uuid")
    doLast {
        mkdir odir15
        ofile15.text = uuid15
    }

    def uuid16 = UUID.randomUUID().toString()
    def odir16 = file("$buildDir/generated/assets/ja-JP")
    def ofile16 = file("$odir16/uuid")
    doLast {
        mkdir odir16
        ofile16.text = uuid16
    }

    def uuid17 = UUID.randomUUID().toString()
    def odir17 = file("$buildDir/generated/assets/eo-EO")
    def ofile17 = file("$odir17/uuid")
    doLast {
        mkdir odir17
        ofile17.text = uuid17
    }

    def uuid18 = UUID.randomUUID().toString()
    def odir18 = file("$buildDir/generated/assets/hi-IN")
    def ofile18 = file("$odir18/uuid")
    doLast {
        mkdir odir18
        ofile18.text = uuid18
    }

    def uuid19 = UUID.randomUUID().toString()
    def odir19 = file("$buildDir/generated/assets/cs-CZ")
    def ofile19 = file("$odir19/uuid")
    doLast {
        mkdir odir19
        ofile19.text = uuid19
    }

    def uuid20 = UUID.randomUUID().toString()
    def odir20 = file("$buildDir/generated/assets/pl-PL")
    def ofile20 = file("$odir20/uuid")
    doLast {
        mkdir odir20
        ofile20.text = uuid20
    }

}

preBuild.dependsOn(genUUID)
```

![image](https://user-images.githubusercontent.com/88623122/193415122-a183547c-0c78-4be0-9ce0-18b8d11fa548.png)

