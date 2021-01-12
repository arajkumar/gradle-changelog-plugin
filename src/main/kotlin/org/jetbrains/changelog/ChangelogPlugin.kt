package org.jetbrains.changelog

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.changelog.tasks.GetChangelogTask
import org.jetbrains.changelog.tasks.InitializeChangelogTask
import org.jetbrains.changelog.tasks.PatchChangelogTask

class ChangelogPlugin : Plugin<Project> {

    private lateinit var project: Project

    override fun apply(project: Project) {
        this.project = project

        project.run {
            extensions.create("changelog", ChangelogPluginExtension::class.java, objects, projectDir, version)

            tasks.apply {
                create("patchChangelog", PatchChangelogTask::class.java) {
                    it.group = "changelog"
                }
                create("initializeChangelog", InitializeChangelogTask::class.java) {
                    it.group = "changelog"
                }
                create("getChangelog", GetChangelogTask::class.java) {
                    it.group = "changelog"
                    it.outputs.upToDateWhen { false }
                }
            }
        }
    }
}
