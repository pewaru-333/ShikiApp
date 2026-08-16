package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.*

enum class RelationKind(val title: StringResource, val order: Int) {
    ADAPTATION(Res.string.relation_kind_adaptation, 1),
    ALTERNATIVE_SETTING(Res.string.relation_kind_alternative_setting, 5),
    ALTERNATIVE_VERSION(Res.string.relation_kind_alternative_version, 5),
    CHARACTER(Res.string.relation_kind_character, 7),
    FULL_STORY(Res.string.relation_kind_full_story, 2),
    OTHER(Res.string.relation_kind_other, 8),
    PARENT_STORY(Res.string.relation_kind_parent_story, 6),
    PREQUEL(Res.string.relation_kind_prequel, 3),
    SEQUEL(Res.string.relation_kind_sequel, 4),
    SIDE_STORY(Res.string.relation_kind_side_story, 7),
    SPIN_OFF(Res.string.relation_kind_spin_off, 7),
    SUMMARY(Res.string.relation_kind_summary, 2)
}