package ir.sbpro.sudokusolver.models

data class SudokuResult (val sudoku: SudoNode?, val runtime: Long, val createdNodes: Int){
    fun getRuntimeString(runtime: Long): String {
        val secTime = runtime.toDouble() / 1000000000
        val decimalsNum: Int = if (secTime < 0.0001) 5 else if (secTime < 0.001) 4 else 3
        return roundDouble(secTime, decimalsNum)
    }

    private fun roundDouble(fNum: Double, decimals: Int): String {
        val coef = Math.pow(10.0, decimals.toDouble()).toInt()
        return ((fNum * coef).toInt().toDouble() / coef).toString()
    }
}