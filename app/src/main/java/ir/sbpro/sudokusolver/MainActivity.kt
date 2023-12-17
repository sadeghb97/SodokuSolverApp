package ir.sbpro.sudokusolver

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Dialog
import ir.sbpro.sudokusolver.models.SudoNode
import ir.sbpro.sudokusolver.solver.SudokuSolver
import ir.sbpro.sudokusolver.ui.theme.Purple40
import ir.sbpro.sudokusolver.ui.theme.Purple80
import ir.sbpro.sudokusolver.ui.theme.SudokuSolverTheme

var cells: SnapshotStateList<SnapshotStateList<Int>> = mutableStateListOf()
var ourCells: SnapshotStateList<SnapshotStateList<Boolean>> = mutableStateListOf()
var valids: SnapshotStateList<Boolean> = mutableStateListOf()
var selectedRow: MutableState<Int> = mutableStateOf(-1)
var selectedColumn: MutableState<Int> = mutableStateOf(-1)
var selectedNum: MutableState<Int> = mutableStateOf(-1)
var noSolutionShown: MutableState<Boolean> = mutableStateOf(false)
var aboutAppShown: MutableState<Boolean> = mutableStateOf(false)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (cells.size == 0) {
            reset()
        }

        setContent {
            AppSingleton.ExtendScreenSize()
            SudokuSolverTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    // A surface container using the 'background' color from the theme
                    Scaffold(
                        topBar = {},
                        bottomBar = {},
                        floatingActionButton = {},
                        content = { padding ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(padding),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .paint(
                                            painterResource(id = R.drawable.bg),
                                            contentScale = ContentScale.FillBounds
                                        )
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight()
                                        .padding(18.dxp)
                                ) {
                                    Spacer(modifier = Modifier.height(10.dxp))
                                    Image(
                                        painter = painterResource(id = R.drawable.logo),
                                        contentDescription = null,
                                        contentScale = ContentScale.FillWidth,
                                        modifier = Modifier
                                            .fillMaxWidth(0.7f)
                                    )
                                    Spacer(modifier = Modifier.height(16.dxp))
                                    Sudoku()
                                    Spacer(modifier = Modifier.height(32.dxp))
                                    Numpad()

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                            .verticalScroll(rememberScrollState())
                                    ) {
                                        Spacer(modifier = Modifier.height(24.dxp))
                                        Actions()
                                    }
                                }
                                if (noSolutionShown.value) NoSolutionPopup {
                                    noSolutionShown.value = false
                                }
                                if (aboutAppShown.value) AboutAppDialog(
                                    packageManager,
                                    packageName
                                ) {
                                    aboutAppShown.value = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Numpad() {
    val locValids = remember {
        valids
    }

    val locSN = remember {
        selectedNum
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            horizontalArrangement = Arrangement.spacedBy(3.dxp),
            verticalArrangement = Arrangement.spacedBy(3.dxp)

        ) {
            for (i in 0..8) {
                item {
                    val v: Boolean = locValids[i]
                    val s: Boolean = locSN.value == i
                    val color: Color = if (v) {
                        if (s) MaterialTheme.colorScheme.primaryContainer else Color.White
                    } else Color.Gray

                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .clip(shape = RectangleShape)
                            .height(40.dxp)
                            .background(color = color)
                            .border(2.dxp, Color.Black, RectangleShape)
                            .clickable {
                                if (v && selectedRow.value >= 0 && selectedColumn.value >= 0) {
                                    locSN.value = i
                                    cells[selectedRow.value][selectedColumn.value] = i + 1
                                    ourCells[selectedRow.value][selectedColumn.value] = true
                                    updateValids()
                                }
                            }
                    ) {
                        Text(
                            text = (i + 1).toString(),
                            fontSize = 22.sxp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Sudoku() {
    val localCells = remember {
        cells
    }

    val localOurCells = remember {
        ourCells
    }

    val locSR = remember {
        selectedRow
    }

    val locSC = remember {
        selectedColumn
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(9),
            horizontalArrangement = Arrangement.spacedBy(0.dxp),
            verticalArrangement = Arrangement.spacedBy(0.dxp)

        ) {
            localCells.forEachIndexed { ri, row ->
                row.forEachIndexed { ci, column ->
                    item {
                        val s: Boolean = locSR.value == ri && locSC.value == ci
                        val isOurs = localOurCells[ri][ci]
                        val borderColor: Color = Color.Black
                        val textColor: Color = if (isOurs) Color.Magenta else Color(0xFF186F65)

                        val fullStrokeWidth = 12f
                        val halfStrokeWidth = fullStrokeWidth / 2

                        var strokeLeft =
                            if (ci == 0 || ci == 3 || ci == 6) fullStrokeWidth else halfStrokeWidth
                        var strokeRight =
                            if (ci == 2 || ci == 5 || ci == 8) fullStrokeWidth else halfStrokeWidth
                        var strokeTop =
                            if (ri == 0 || ri == 3 || ri == 6) fullStrokeWidth else halfStrokeWidth
                        var strokeBottom =
                            if (ri == 2 || ri == 5 || ri == 8) fullStrokeWidth else halfStrokeWidth

                        if (ci == 0) strokeLeft *= 2
                        if (ci == 8) strokeRight *= 2
                        if (ri == 0) strokeTop *= 2
                        if (ri == 8) strokeBottom *= 2

                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .clip(shape = RectangleShape)
                                //.border(width = 2.dxp, color = Color.Black, shape = RectangleShape)
                                .height(40.dxp)
                                .background(color = if (s) MaterialTheme.colorScheme.primaryContainer else Color.White)
                                .drawBehind {
                                    val y = size.height

                                    drawLine(
                                        borderColor,
                                        Offset(0f, y),
                                        Offset(size.width, y),
                                        strokeBottom
                                    )

                                    drawLine(
                                        borderColor,
                                        Offset(0f, 0f),
                                        Offset(size.width, 0f),
                                        strokeTop
                                    )

                                    drawLine(
                                        borderColor,
                                        Offset(0f, 0f),
                                        Offset(0f, y),
                                        strokeLeft
                                    )

                                    drawLine(
                                        borderColor,
                                        Offset(size.width, 0f),
                                        Offset(size.width, y),
                                        strokeRight
                                    )
                                }
                                .clickable {
                                    locSR.value = ri
                                    locSC.value = ci
                                    selectedNum.value = cells[ri][ci] - 1
                                    updateValids()
                                }
                        ) {
                            Text(
                                text = if (column > 0) column.toString() else "",
                                color = textColor,
                                fontSize = 22.sxp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

fun clearCell() {
    if (selectedRow.value >= 0 && selectedColumn.value >= 0) {
        selectedNum.value = -1
        cells[selectedRow.value][selectedColumn.value] = 0
        ourCells[selectedRow.value][selectedColumn.value] = true
        updateValids()
    }
}

fun reset() {
    cells.clear()
    ourCells.clear()
    valids.clear()
    selectedRow.value = -1
    selectedColumn.value = -1
    selectedNum.value = -1
    noSolutionShown.value = false
    aboutAppShown.value = false

    for (i in 1..9) {
        var row: SnapshotStateList<Int> = mutableStateListOf()
        var oRow: SnapshotStateList<Boolean> = mutableStateListOf()
        for (j in 1..9) {
            row.add(0)
            oRow.add(false)
        }
        cells.add(row)
        ourCells.add(oRow)
        valids.add(true)
    }
}

fun updateValids() {
    if (selectedRow.value < 0 || selectedColumn.value < 0) return
    val sudoku = initSudoku()
    for (i in 0..8) {
        val child = sudoku.getChild(selectedRow.value, selectedColumn.value, i + 1)
        valids[i] = child.isValid
    }
}

fun initSudoku(): SudoNode {
    val nCells: Array<IntArray> = Array(9) {
        IntArray(9)
    }
    for (i in 0..8) {
        for (j in 0..8) {
            nCells[i][j] = cells[i][j]
        }
    }

    return SudoNode(nCells)
}

fun solve() {
    val sudoku = initSudoku()
    val result = SudokuSolver.forwardCheckingMRVSolve(sudoku)

    if (result.sudoku != null) {
        loadResult(result.sudoku)
        updateValids()
    } else {
        noSolutionShown.value = true
    }
}

fun loadResult(result: SudoNode) {
    for (i in 0..8) {
        for (j in 0..8) {
            cells[i][j] = result.cells[i][j]
        }
    }
}

@Composable
fun AboutAppDialog(manager: PackageManager, packageName: String, onDismiss: () -> Unit) {
    val info = manager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    val verName = info.versionName
    val verCode = info.versionCode

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dxp)
                .padding(16.dxp),
            shape = RoundedCornerShape(16.dxp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .padding(top = 18.dxp, bottom = 12.dxp)
                )
                Image(
                    painter = painterResource(id = R.drawable.launcher),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(160.dxp)
                )
                Text(
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(12.dxp),
                )
                Text(
                    text = "نسخه " + "$verName ($verCode)"
                )
            }
        }
    }
}

@Composable
fun NoSolutionPopup(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = "این سودوکو جوابی ندارد" + " \uD83D\uDE48",
                textAlign = TextAlign.Justify,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge.merge(
                    TextStyle(
                        textDirection = TextDirection.Rtl,
                    )
                )
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "اوکی", style = MaterialTheme.typography.bodyLarge)
            }
        }
    )
}

@Composable
fun Actions() {
    val btnFontSize = 20

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { clearCell() },
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dxp, end = 2.dxp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface
            )
        ) {
            Text(
                text = "پاک کن",
                fontSize = btnFontSize.sxp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = {
                solve()
            },
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dxp, end = 2.dxp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "حل",
                fontSize = btnFontSize.sxp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Button(
            onClick = { reset() },
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dxp, end = 2.dxp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface
            )
        ) {
            Text(
                text = "ریست",
                fontSize = btnFontSize.sxp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dxp))

    Row(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = {
                aboutAppShown.value = true
            },
            modifier = Modifier
                .weight(1f)
                .padding(start = 2.dxp, end = 2.dxp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.inverseOnSurface
            )
        ) {
            Text(
                text = "درباره برنامه",
                fontSize = btnFontSize.sxp,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}