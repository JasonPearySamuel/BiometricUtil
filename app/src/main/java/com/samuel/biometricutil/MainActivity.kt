package com.samuel.biometricutil

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.samuel.biometriclibrary.util.ToastUtil
import com.samuel.biometriclibrary.util.BiometricPromptUtil
import kotlinx.android.synthetic.main.main_activity.*

/**
 * create by jason_samuel
 * on 2020/7/16
 */
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