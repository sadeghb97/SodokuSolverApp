package ir.sbpro.sudokusolver

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil

val Int.dxp
    get() = ceil(this.toDouble() * AppSingleton.minsm).toInt().dp

val Int.dwp
    get() = ceil(this.toDouble() * AppSingleton.swr).toInt().dp

val Int.sxp
    get() = ceil(this.toDouble() * AppSingleton.minsm / AppSingleton.userFontScale).toInt().sp