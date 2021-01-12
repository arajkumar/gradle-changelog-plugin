package org.jetbrains.changelog

import groovy.lang.Closure
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import java.io.File
import java.util.regex.Pattern

@Suppress("UnstableApiUsage")
open class ChangelogPluginExtension(
    objects: ObjectFactory,
    private val projectDir: File,
    private val projectVersion: String,
) {

    @Optional
    @Internal
    private val groupsProperty: ListProperty<String> = objects.listProperty(String::class.java)
    var groups: List<String>
        get() = groupsProperty.getOrElse(emptyList()).ifEmpty {
            listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security")
        }
        set(value) = groupsProperty.set(value)

    @Optional
    @Internal
    private val headerProperty: Property<Closure<*>> = objects.property(Closure::class.java).apply {
        set(closure { "[$version]" })
    }
    var header: Closure<*>
        get() = headerProperty.get()
        set(value) = headerProperty.set(value)

    @Optional
    @Internal
    private val headerParserRegexProperty: Property<Regex?> = objects.property(Regex::class.java)
    var headerParserRegex: Any?
        get() = headerParserRegexProperty.orNull
        set(value) = headerParserRegexProperty.set(headerParserRegexHelper(value))

    private fun <T> headerParserRegexHelper(t: T) = when (t) {
        is Regex -> t
        is String -> t.toRegex()
        is Pattern -> t.toRegex()
        else -> throw IllegalArgumentException()
    }

    @Optional
    @Internal
    private val itemPrefixProperty: Property<String> = objects.property(String::class.java).apply {
        set("-")
    }
    var itemPrefix: String
        get() = itemPrefixProperty.get()
        set(value) = itemPrefixProperty.set(value)

    @Optional
    @Internal
    private val keepUnreleasedSectionProperty: Property<Boolean> = objects.property(Boolean::class.java).apply {
        set(true)
    }
    var keepUnreleasedSection: Boolean
        get() = keepUnreleasedSectionProperty.get()
        set(value) = keepUnreleasedSectionProperty.set(value)

    @Optional
    @Internal
    private val patchEmptyProperty: Property<Boolean> = objects.property(Boolean::class.java).apply {
        set(true)
    }
    var patchEmpty: Boolean
        get() = patchEmptyProperty.get()
        set(value) = patchEmptyProperty.set(value)

    @Optional
    @Internal
    private val pathProperty: Property<String> = objects.property(String::class.java).apply {
        set("$projectDir/CHANGELOG.md")
    }
    var path: String
        get() = pathProperty.get()
        set(value) = pathProperty.set(value)

    @Optional
    @Internal
    private val versionProperty: Property<String> = objects.property(String::class.java)
    var version: String
        get() = versionProperty.getOrElse(projectVersion)
        set(value) = versionProperty.set(value)

    @Optional
    @Internal
    private val unreleasedTermProperty: Property<String> = objects.property(String::class.java).apply {
        set("[Unreleased]")
    }
    var unreleasedTerm: String
        get() = unreleasedTermProperty.get()
        set(value) = unreleasedTermProperty.set(value)

    fun getUnreleased() = get(unreleasedTerm)

    fun get(version: String = this.version) = Changelog(this).get(version)

    fun getLatest() = Changelog(this).getLatest()

    fun getAll() = Changelog(this).getAll()

    fun has(version: String) = Changelog(this).has(version)
}
