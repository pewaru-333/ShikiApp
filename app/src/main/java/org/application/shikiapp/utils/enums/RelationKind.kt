package org.application.shikiapp.utils.enums

import androidx.annotation.StringRes
import org.application.shikiapp.R

enum class RelationKind(@StringRes val title: Int, val order: Int) {
    ADAPTATION(R.string.relation_kind_adaptation, 1),
    ALTERNATIVE_SETTING(R.string.relation_kind_alternative_setting, 5),
    ALTERNATIVE_VERSION(R.string.relation_kind_alternative_version, 5),
    CHARACTER(R.string.relation_kind_character, 7),
    FULL_STORY(R.string.relation_kind_full_story, 2),
    OTHER(R.string.relation_kind_other, 8),
    PARENT_STORY(R.string.relation_kind_parent_story, 6),
    PREQUEL(R.string.relation_kind_prequel, 3),
    SEQUEL(R.string.relation_kind_sequel, 4),
    SIDE_STORY(R.string.relation_kind_side_story, 7),
    SPIN_OFF(R.string.relation_kind_spin_off, 7),
    SUMMARY(R.string.relation_kind_summary, 2)
}