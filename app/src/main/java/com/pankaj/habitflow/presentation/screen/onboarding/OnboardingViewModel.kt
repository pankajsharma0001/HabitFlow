package com.pankaj.habitflow.presentation.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankaj.habitflow.data.local.ThemePreferences
import com.pankaj.habitflow.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val themePreferences: ThemePreferences,
    private val habitRepository: HabitRepository
) : ViewModel() {

    val onboardingCompleted: StateFlow<Boolean> = themePreferences.onboardingCompletedFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun completeOnboarding(selectedHabitIndices: Set<Int>) {
        viewModelScope.launch {
            val suggestions = getSuggestions()
            selectedHabitIndices.forEachIndexed { i, index ->
                val suggestion = suggestions.getOrNull(index)
                if (suggestion != null) {
                    habitRepository.insertHabit(
                        name = suggestion.name,
                        description = suggestion.description,
                        category = suggestion.category,
                        colorHex = suggestion.colorHex,
                        iconName = suggestion.iconName,
                        reminderTimeMinutes = suggestion.reminderTimeMinutes,
                        frequencyType = suggestion.frequencyType,
                        frequencyDays = suggestion.frequencyDays,
                        sortOrder = i,
                        timeOfDay = suggestion.timeOfDay
                    )
                }
            }
            themePreferences.setOnboardingCompleted(true)
        }
    }

    fun getSuggestions(): List<OnboardingSuggestion> {
        return listOf(
            OnboardingSuggestion("Drink Water 💧", "Drink 2.5 liters of water daily", "HEALTH", "#FF6B6B", "check_circle", null, "DAILY", null, "ANYTIME"),
            OnboardingSuggestion("Morning Stretch 🏃", "15 minutes of stretch exercises", "FITNESS", "#4ECDC4", "check_circle", 480, "DAILY", null, "MORNING"),
            OnboardingSuggestion("Read 📚", "Read books for 20 minutes", "LEARNING", "#A78BFA", "check_circle", 1200, "DAILY", null, "EVENING"),
            OnboardingSuggestion("Meditation 🧘", "10 minutes mindfulness breathing", "MINDFULNESS", "#6BCB77", "check_circle", 540, "DAILY", null, "MORNING"),
            OnboardingSuggestion("Review Budget 💰", "Track daily expenses", "FINANCE", "#50C878", "check_circle", 1260, "DAILY", null, "EVENING"),
            OnboardingSuggestion("Draw/Paint 🎨", "Express creativity", "CREATIVITY", "#FF7F50", "check_circle", null, "DAILY", null, "AFTERNOON")
        )
    }
}

data class OnboardingSuggestion(
    val name: String,
    val description: String,
    val category: String,
    val colorHex: String,
    val iconName: String,
    val reminderTimeMinutes: Int?,
    val frequencyType: String,
    val frequencyDays: String?,
    val timeOfDay: String
)
