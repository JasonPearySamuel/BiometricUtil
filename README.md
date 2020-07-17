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
	        api 'com.github.jasonpearysamuel:BiometricUtil:1.0.0'
	}


Step 3. How to use

	class MainActivity : AppCompatActivity() {

	    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.main_activity)
		btn_finger.setOnClickListener {
		    startFinger()
		}
	    }

	    private fun startFinger() {

		val biometricPromptUtil = BiometricPromptUtil(this)

		if (!biometricPromptUtil.supportFingerprint()) {
		    ToastUtil.showToast(this, "您的手机不支持指纹功能")
		    return
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
		    biometricPromptUtil.startBiometricPromptIn28()
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
		    biometricPromptUtil.startBiometricPromptIn23()
		}

		// 指纹识别返回数据
		biometricPromptUtil.addFingerResultListener(object :
		    BiometricPromptUtil.OnFingerResultListener {
		    override fun fingerResult(result: Boolean) {
			if (result) {
			    // 指纹正确

			}
		    }
		})
	    }
	}
