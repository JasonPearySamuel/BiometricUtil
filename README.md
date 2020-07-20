# BiometricUtil

It is suitable for fingerprint identification of Android 6.0 to Android 9.0+

适应Andorid 6.0以上所有版本指纹识别，包括9.0+以上系统。

本资源库在Android X下进行开发，部分使用Kotlin编写，兼容了Android 6.0以上所有安卓版本，并都尽量整合在了同一个Util工具类中。但由于6.0与9.0的差异性，即9.0以下的安卓系统不提供专门的页面，所以9.0以下设备必须自定义指纹识别页，FingerprintDialogFragment来专门供9.0以下的机型显示指纹识别页面，Android 9.0的指纹识别开启代码较为简单，故直接整理在了Util类里面。


**步骤 1. 在项目根目录的build.gradle中添加:**

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}


**步骤 2. 在app/build.gradle中添加:**

	dependencies {
	        api 'com.github.jasonpearysamuel:BiometricUtil:v1.0'
	}


**步骤 3. 食用:**

	// kotlin
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
		    biometricPromptUtil.startBiometricPromptIn28("指纹验证", "扫描指纹，验证身份", "取消")
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
		    biometricPromptUtil.startBiometricPromptIn23("请验证指纹解锁", "取消")
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
	
	<br>
	<br>
	// Java
	public class MainJavaActivity extends AppCompatActivity {

	    @Override
	    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		startFinger();
	    }

	    public void startFinger() {
		BiometricPromptUtil biometricPromptUtil = new BiometricPromptUtil(this);

		if (!biometricPromptUtil.supportFingerprint()) {
		    ToastUtil.showToast(this, "您的手机不支持指纹功能");
		    return;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
		    biometricPromptUtil.startBiometricPromptIn28("指纹验证", "扫描指纹，验证身份", "取消");
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
		    biometricPromptUtil.startBiometricPromptIn23("请验证指纹解锁", "取消");
		}

		biometricPromptUtil.addFingerResultListener(new BiometricPromptUtil.OnFingerResultListener() {
		    @Override
		    public void fingerResult(boolean result) {
			if (result) {
			    // 指纹正确
			}
		    }
		});
	    }
	}


>本项目的简书地址，链接如下：
>https://www.jianshu.com/p/5c0029374d47
