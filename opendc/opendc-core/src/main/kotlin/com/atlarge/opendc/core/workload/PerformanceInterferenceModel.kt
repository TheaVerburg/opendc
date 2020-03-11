package com.atlarge.opendc.core.workload

import com.atlarge.opendc.core.resource.Resource
import java.util.UUID

/**
 * Meta-data key for the [PerformanceInterferenceModel] of an image.
 */
const val IMAGE_PERF_INTERFERENCE_MODEL = "image:performance-interference"

/**
 * Performance Interference Model describing the variability incurred by different sets of workloads if colocated.
 *
 * @param items The [PerformanceInterferenceModelItem]s that make up this model.
 */
data class PerformanceInterferenceModel(
    val items: Set<PerformanceInterferenceModelItem>
) {
    fun apply(colocatedWorkloads: Set<Resource>): Double {
        val colocatedWorkloadIds = colocatedWorkloads.map { it.uid }
        val intersectingItems = items.filter { item ->
            colocatedWorkloadIds.intersect(item.workloadIds).size > 1
        }

        if (intersectingItems.isEmpty()) {
            return 1.0
        }
        return intersectingItems.map { it.performanceScore }.min() ?: error("Minimum score must exist.")
    }
}

/**
 * Model describing how a specific set of workloads causes performance variability for each workload.
 *
 * @param workloadIds The IDs of the workloads that together cause performance variability for each workload in the set.
 * @param performanceScore The performance score that should be applied to each workload's performance. 1 means no
 * influence, <1 means that performance degrades, and >1 means that performance improves.
 */
data class PerformanceInterferenceModelItem(
    val workloadIds: Set<UUID>,
    val performanceScore: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PerformanceInterferenceModelItem

        if (workloadIds != other.workloadIds) return false

        return true
    }

    override fun hashCode(): Int {
        return workloadIds.hashCode()
    }
}