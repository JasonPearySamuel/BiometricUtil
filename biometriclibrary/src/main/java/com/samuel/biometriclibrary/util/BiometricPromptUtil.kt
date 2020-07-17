package com.samuel.biometriclibrary.util

import android.app.KeyguardManager
import android.content.Context
import android.content.DialogInterface
import android.hardware.biometrics.BiometricPrompt
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.samuel.biometriclibrary.FingerprintDialogFragment
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator


/**
 * create by jason_samuel
 * on 2020/7/2
 *
 * Android的生物识别
 */
class BiometricPromptUtil(val mContext: Context) {

    private lateinit var keyStore: KeyStore         // 6.0~9.0的指纹识别
    private lateinit var mOnFingerResultListener: OnFingerResultListener

    /**
     * 判断是否支持指纹
     * @return
     */
    fun supportFingerprint(): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            //ToastUtil.showToast(mContext, "您的系统版本过低，不支持指纹功能")
            return false
        } else {
            //键盘锁管理者
            val keyguardManager = mContext.getSystemService(KeyguardManager::class.java)
            //指纹管理者
            val fingerprintManager = mContext.getSystemService(FingerprintManager::class.java)
            if (!fingerprintManager.isHardwareDetected()) { //判断硬件支不支持指纹
                //ToastUtil.showToast(mContext, "您的手机不支持指纹功能")
                return false
            } else if (!keyguardManager.isKeyguardSecure()) { //还未设置锁屏
                ToastUtil.showToast(mContext, "您还未设置锁屏，请先设置锁屏并添加一个指纹")
                return false
            } else if (!fingerprintManager.hasEnrolledFingerprints()) { //指纹未登记
                ToastUtil.showToast(mContext, "您至少需要在系统设置中添加一个指纹")
                return false
            }
        }
        return true
    }

    /**
     * 判断是否支持人脸
     * 注：目前安卓源生BiometricPrompt暂不支持调用系统的人脸识别，
     *     现阶段人脸识别均为厂家在系统中自己定制实现。
     * @return
     */
    fun supportFace(): Boolean {
        return false
    }

    fun addFingerResultListener(onFingerResult: OnFingerResultListener) {
        mOnFingerResultListener = onFingerResult
    }

    /**
     * 安卓9.0及以上的指纹识别
     */
    @RequiresApi(Build.VERSION_CODES.P)
    fun startBiometricPromptIn28(titleStr: String, description: String, cancelStr: String) {
        val mBiometricPrompt = BiometricPrompt.Builder(mContext)
            .setTitle(titleStr)
            .setDescription(description)
            .setNegativeButton(
                cancelStr,
                mContext.getMainExecutor(),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // ToastUtil.showToast(mContext, "Cancel")
                })
            .build()

        val mCancellationSignal = CancellationSignal()
        mCancellationSignal.setOnCancelListener {
            ToastUtil.showToast(mContext, "cancel")
        }

        val mAuthenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                ToastUtil.showToast(mContext, "$errString")
                if (::mOnFingerResultListener.isInitialized) {
                    mOnFingerResultListener.fingerResult(false)
                }
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                // ToastUtil.showToast(mContext, "Succeeded")
                if (::mOnFingerResultListener.isInitialized) {
                    mOnFingerResultListener.fingerResult(true)
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                ToastUtil.showToast(mContext, "Failed")
                if (::mOnFingerResultListener.isInitialized) {
                    mOnFingerResultListener.fingerResult(false)
                }
            }
        }
        mBiometricPrompt.authenticate(
            mCancellationSignal,
            mContext.getMainExecutor(),
            mAuthenticationCallback
        )
    }

    /**
     * 安卓6.0以上，9.0以下的指纹识别
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun startBiometricPromptIn23(titleStr: String, cancelStr: String) {
        initKey()
        initCipher(titleStr, cancelStr)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)
            //秘钥生成器
            val keyGenerator =
                KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            val builder = KeyGenParameterSpec.Builder(
                "DEFAULT_KEY_NAME",
                KeyProperties.PURPOSE_ENCRYPT or
                        KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(false)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            keyGenerator.init(builder.build())
            keyGenerator.generateKey()
        } catch (e: java.lang.Exception) {
            throw java.lang.RuntimeException(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initCipher(titleStr: String, cancelStr: String) {
        try {
            val key = keyStore.getKey("DEFAULT_KEY_NAME", null)
            val cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7
            )
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val fragment23 = FingerprintDialogFragment()
            fragment23.setString(titleStr, cancelStr)
            fragment23.setCipher(cipher)
            fragment23.show((mContext as AppCompatActivity).supportFragmentManager, "fingerprint")
            fragment23.setOnFingerResultListener { result ->
                mOnFingerResultListener.fingerResult(result)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    interface OnFingerResultListener {
        fun fingerResult(result: Boolean)
    }
}