package com.rff.boingballdemo.shell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rff.boingballdemo.component.OSStyle
import com.rff.boingballdemo.data.local.AppSettings
import com.rff.boingballdemo.utils.toAmigaDateText
import com.rff.boingballdemo.utils.toAmigaTimeText
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

private const val TYPE_DELAY_MS = 45L
private const val RETURN_DELAY_MS = 250L
private const val OUTPUT_DELAY_MS = 50L

/** Lines kept in the scrollback buffer. */
private const val MAX_LINES = 200

class ShellViewModel(
    private val settings: AppSettings
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShellState())
    val uiState: StateFlow<ShellState> = _uiState.asStateFlow()

    private var commandIndex = 0
    private var prompt = promptFor(OSStyle.AmigaOS13)
    private var runJob: Job? = null

    init {
        settings.boingBallPrefs
            .onEach { prefs -> onOsStyleChanged(prefs.osStyle) }
            .launchIn(viewModelScope)
    }

    /** Tap inside the shell window: run the next canned command. */
    fun onTap() {
        if (_uiState.value.isBusy) return

        val script = scriptFor(_uiState.value.osStyle)
        val command = script[commandIndex % script.size]
        commandIndex++

        runJob = viewModelScope.launch {
            _uiState.update { it.copy(isBusy = true) }

            val typedAt = prompt
            command.command.forEachIndexed { index, _ ->
                _uiState.update {
                    it.copy(currentLine = typedAt + command.command.take(index + 1))
                }
                delay(TYPE_DELAY_MS)
            }

            delay(RETURN_DELAY_MS)
            appendLine(typedAt + command.command)

            command.output.forEach { line ->
                appendLine(line.resolveTokens())
                delay(OUTPUT_DELAY_MS)
            }

            command.promptAfter?.let { prompt = it }
            _uiState.update { it.copy(currentLine = prompt, isBusy = false) }
        }
    }

    private fun onOsStyleChanged(osStyle: OSStyle) {
        if (_uiState.value.osStyle == osStyle && _uiState.value.lines.isNotEmpty()) return

        runJob?.cancel()
        commandIndex = 0
        prompt = promptFor(osStyle)
        _uiState.value = ShellState(
            osStyle = osStyle,
            lines = bannerFor(osStyle),
            currentLine = prompt,
        )
    }

    private fun appendLine(line: String) {
        _uiState.update { it.copy(lines = (it.lines + line).takeLast(MAX_LINES)) }
    }

    private fun String.resolveTokens(): String {
        if (!contains(SHELL_DATE_TOKEN) && !contains(SHELL_TIME_TOKEN)) return this

        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return replace(SHELL_DATE_TOKEN, now.toAmigaDateText())
            .replace(SHELL_TIME_TOKEN, now.toAmigaTimeText())
    }
}
