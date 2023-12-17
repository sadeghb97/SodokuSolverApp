package ir.sbpro.sudokusolver

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity

class AppSingleton private constructor(){
    companion object {
        public var sh: Int = 0
        public var sw: Int = 0
        public var shr: Double = 1.0
        public var swr: Double = 1.0
        public var minsm: Double = 1.0
        public var userFontScale = 1f

        @Composable
        fun ExtendScreenSize(){
            userFontScale = LocalDensity.current.fontScale

            val configuration = LocalConfiguration.current
            sh = configuration.screenHeightDp
            sw = configuration.screenWidthDp

            shr = sh.toDouble() / 773
            swr = sw.toDouble() / 411
            minsm = if(shr < swr) shr else swr
            Log.d("XQQQMinSM", minsm.toString())
            Log.d("XQQQSWR", swr.toString())
            Log.d("XQQQUFS", userFontScale.toString())
        }
    }
}