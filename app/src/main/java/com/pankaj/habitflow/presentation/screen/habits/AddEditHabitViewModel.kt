package com.pankaj.habitflow.presentation.screen.habits

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankaj.habitflow.domain.model.HabitCategory
import com.pankaj.habitflow.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class AddEditHabitViewModel @Inject constructor(
    private val repository: HabitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val habitId: String? = savedStateHandle["habitId"]

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    private val _category = MutableStateFlow(HabitCategory.HEALTH)
    val category: StateFlow<HabitCategory> = _category.asStateFlow()

    private val _colorHex = MutableStateFlow("#FF6B6B")
    val colorHex: StateFlow<String> = _colorHex.asStateFlow()

    private val _iconName = MutableStateFlow("check_circle")
    val iconName: StateFlow<String> = _iconName.asStateFlow()

    private val _reminderTime = MutableStateFlow<LocalTime?>(null)
    val reminderTime: StateFlow<LocalTime?> = _reminderTime.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow: SharedFlow<UiEvent> = _eventFlow.asSharedFlow()

    sealed class UiEvent {
        object SaveHabit : UiEvent()
        data class ShowSnackbar(val message: String) : UiEvent()
    }

    init {
        habitId?.let { id ->
            if (id.isNotEmpty() && id != "new") {
                viewModelScope.launch {
                    repository.getHabitById(id)?.let { habit ->
                        _name.value = habit.name
                        _description.value = habit.description
                        _category.value = habit.category
                        _colorHex.value = habit.colorHex
                        _iconName.value = habit.iconName
                        _reminderTime.value = habit.reminderTime
                    }
                }
            }
        }
    }

    fun onNameChange(value: String) {
        _name.value = value
    }

    fun onDescriptionChange(value: String) {
        _description.value = value
    }

    fun onCategoryChange(value: HabitCategory) {
        _category.value = value
        _colorHex.value = value.defaultColorHex
    }

    fun onColorChange(value: String) {
        _colorHex.value = value
    }

    fun onIconChange(value: String) {
        _iconName.value = value
    }

    fun onReminderTimeChange(value: LocalTime?) {
        _reminderTime.value = value
    }

    fun saveHabit() {
        viewModelScope.launch {
            if (_name.value.isBlank()) {
                _eventFlow.emit(UiEvent.ShowSnackbar("Habit name cannot be empty"))
                return@launch
            }

            val reminderTimeMinutes = _reminderTime.value?.let {
                it.hour * 60 + it.minute
            }

            if (habitId != null && habitId.isNotEmpty() && habitId != "new") {
                repository.updateHabit(
                    habitId = habitId,
                    name = _name.value.trim(),
                    description = _description.value.trim(),
                    category = _category.value.name,
                    colorHex = _colorHex.value,
                    iconName = _iconName.value,
                    reminderTimeMinutes = reminderTimeMinutes
                )
            } else {
                repository.insertHabit(
                    name = _name.value.trim(),
                    description = _description.value.trim(),
                    category = _category.value.name,
                    colorHex = _colorHex.value,
                    iconName = _iconName.value,
                    reminderTimeMinutes = reminderTimeMinutes
                )
            }
            _eventFlow.emit(UiEvent.SaveHabit)
        }
    }
}
