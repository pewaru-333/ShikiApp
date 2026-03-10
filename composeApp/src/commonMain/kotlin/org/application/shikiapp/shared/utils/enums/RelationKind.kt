package org.application.shikiapp.shared.utils.enums

import org.jetbrains.compose.resources.StringResource
import shikiapp.composeapp.generated.resources.Res
import shikiapp.composeapp.generated.resources.relation_kind_adaptation
import shikiapp.composeapp.generated.resources.relation_kind_alternative_setting
import shikiapp.composeapp.generated.resources.relation_kind_alternative_version
import shikiapp.composeapp.generated.resources.relation_kind_character
import shikiapp.composeapp.generated.resources.relation_kind_full_story
import shikiapp.composeapp.generated.resources.relation_kind_other
import shikiapp.composeapp.generated.resources.relation_kind_parent_story
import shikiapp.composeapp.generated.resources.relation_kind_prequel
import shikiapp.composeapp.generated.resources.relation_kind_sequel
import shikiapp.composeapp.generated.resources.relation_kind_side_story
import shikiapp.composeapp.generated.resources.relation_kind_spin_off
import shikiapp.composeapp.generated.resources.relation_kind_summary

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