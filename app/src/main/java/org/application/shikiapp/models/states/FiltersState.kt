package org.application.shikiapp.models.states

import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateSet
import org.application.shikiapp.utils.BLANK
import org.application.shikiapp.utils.enums.Order
import org.application.shikiapp.utils.enums.PeopleFilterItem

data class FiltersState(
    val order: Order = Order.RANKED,
    val kind: SnapshotStateSet<String> = mutableStateSetOf(),
    val status: SnapshotStateSet<String> = mutableStateSetOf(),
    val seasonYS: String = BLANK,
    val seasonYF: String = BLANK,
    val seasonS: SnapshotStateSet<String> = mutableStateSetOf(),
    val season: SnapshotStateSet<String> = mutableStateSetOf(),
    val score: Float = 6f,
    val duration: SnapshotStateSet<String> = mutableStateSetOf(),
    val rating: SnapshotStateSet<String> = mutableStateSetOf(),
    val genres: SnapshotStateSet<String> = mutableStateSetOf(),
    val studio: String? = null,
    val publisher: String? = null,
    val franchise: String? = null,
    val censored: Boolean? = null,
    val myList: String? = null,
    val roles: SnapshotStateSet<PeopleFilterItem> = mutableStateSetOf(),
    val title: String = BLANK
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (other !is FiltersState) return false

        if (order != other.order) return false
        if (seasonYS != other.seasonYS) return false
        if (seasonYF != other.seasonYF) return false
        if (score != other.score) return false
        if (studio != other.studio) return false
        if (publisher != other.publisher) return false
        if (franchise != other.franchise) return false
        if (censored != other.censored) return false
        if (myList != other.myList) return false
        if (title != other.title) return false

        // SnapshotStateSet имеет разные ссылки
        if (kind.toSet() != other.kind.toSet()) return false
        if (status.toSet() != other.status.toSet()) return false
        if (seasonS.toSet() != other.seasonS.toSet()) return false
        if (season.toSet() != other.season.toSet()) return false
        if (duration.toSet() != other.duration.toSet()) return false
        if (rating.toSet() != other.rating.toSet()) return false
        if (genres.toSet() != other.genres.toSet()) return false
        if (roles.toSet() != other.roles.toSet()) return false

        return true
    }

    override fun hashCode(): Int {
        var result = order.hashCode()

        result = 31 * result + kind.toSet().hashCode()
        result = 31 * result + status.toSet().hashCode()
        result = 31 * result + seasonYS.hashCode()
        result = 31 * result + seasonYF.hashCode()
        result = 31 * result + seasonS.toSet().hashCode()
        result = 31 * result + season.toSet().hashCode()
        result = 31 * result + score.hashCode()
        result = 31 * result + duration.toSet().hashCode()
        result = 31 * result + rating.toSet().hashCode()
        result = 31 * result + genres.toSet().hashCode()
        result = 31 * result + (studio?.hashCode() ?: 0)
        result = 31 * result + (publisher?.hashCode() ?: 0)
        result = 31 * result + (franchise?.hashCode() ?: 0)
        result = 31 * result + (censored?.hashCode() ?: 0)
        result = 31 * result + (myList?.hashCode() ?: 0)
        result = 31 * result + roles.toSet().hashCode()
        result = 31 * result + title.hashCode()

        return result
    }
}