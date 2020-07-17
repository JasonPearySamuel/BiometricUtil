# BiometricUtil
It is suitable for fingerprint identification of Android 6.0 to Android 9.0+

Step 1. Add the JitPack repository to your build file
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


Step 2. Add the dependency in app/build.gradle

	dependencies {
	        implementation 'com.github.JasonPearySamuel:BiometricUtil:Tag'
	}
