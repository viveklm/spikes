package com.novoda.gol

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.novoda.gol.patterns.PatternEntity
import com.novoda.gol.patterns.PatternRepository
import com.novoda.gol.presentation.*

class GameOfLife : AppCompatActivity(), AppView {
    override var onControlButtonClicked = {}
    override var onPatternSelected: (pattern: PatternEntity) -> Unit = {}

    override fun renderControlButtonLabel(controlButtonLabel: String) {
        controlButton.text = controlButtonLabel
    }

    override fun renderPatternSelectionVisibility(visibility: Boolean) {
        patternSelectionlButton.visibility = if (visibility) View.VISIBLE else View.INVISIBLE
    }

    override fun renderBoard(boardViewState: BoardViewState) {
        if (boardViewState.selectedPattern != null) {
            boardView.onPatternSelected(boardViewState.selectedPattern!!)
        }

        if (boardViewState.isIdle.not()) {
            boardView.onStartSimulationClicked()
        } else {
            boardView.onStopSimulationClicked()
        }
    }

    private val appPresenter = AppPresenter()
    private val boardPresenter = BoardPresenter(50, 50)
    private lateinit var boardView: BoardView
    private lateinit var controlButton: Button
    private lateinit var patternSelectionlButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_of_life)

        controlButton = findViewById(R.id.controlButton)
        patternSelectionlButton = findViewById(R.id.patternSelectionButton)
        boardView = findViewById<AndroidBoardView>(R.id.boardView)

        controlButton.setOnClickListener {
            onControlButtonClicked()
        }

        patternSelectionlButton.setOnClickListener {
            showPatternSelection()
        }
    }

    private fun showPatternSelection() {
        val patternSelectionView = LinearLayout(this)
        val dialog = AlertDialog.Builder(this).setView(patternSelectionView).create()

        patternSelectionView.orientation = LinearLayout.VERTICAL

        for (pattern in PatternRepository.patterns()) {
            val patternNameView = TextView(this)
            patternNameView.text = pattern.getName()
            patternSelectionView.addView(patternNameView)

            val cellMatrixView = CellMatrixView(this)
            patternSelectionView.addView(cellMatrixView, pxFromDp(25f), pxFromDp(25f))
            cellMatrixView.render(pattern)
            cellMatrixView.setOnClickListener {
                onPatternSelected(pattern)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    override fun onStart() {
        super.onStart()
        appPresenter.bind(this)
        boardPresenter.bind(boardView)
    }

    override fun onStop() {
        super.onStop()
        appPresenter.unbind(this)
        boardPresenter.unbind(boardView)
    }

    fun pxFromDp(dp: Float): Int {
        return (dp * this.resources.displayMetrics.density).toInt()
    }
}
