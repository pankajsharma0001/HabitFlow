package com.pankaj.habitflow.domain.model

/**
 * Predefined categories for habits with associated colors and icons.
 */
enum class HabitCategory(
    val displayName: String,
    val emoji: String,
    val defaultColorHex: String
) {
    HEALTH("Health", "💪", "#FF6B6B"),
    FITNESS("Fitness", "🏃", "#4ECDC4"),
    PRODUCTIVITY("Productivity", "⚡", "#FFE66D"),
    LEARNING("Learning", "📚", "#A78BFA"),
    MINDFULNESS("Mindfulness", "🧘", "#6BCB77"),
    NUTRITION("Nutrition", "🥗", "#FF8C42"),
    SLEEP("Sleep", "😴", "#7B68EE"),
    SOCIAL("Social", "👥", "#FF69B4"),
    FINANCE("Finance", "💰", "#50C878"),
    CREATIVITY("Creativity", "🎨", "#FF7F50"),
    SELF_CARE("Self Care", "🌸", "#DDA0DD"),
    OTHER("Other", "📌", "#778899");

    companion object {
        fun fromName(name: String): HabitCategory {
            return entries.find { it.name == name } ?: OTHER
        }
    }
}
