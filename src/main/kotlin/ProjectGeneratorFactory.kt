package net.minheur

import com.intellij.icons.AllIcons
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.GeneratorNewProjectWizard
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.RootNewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class ProjectGeneratorFactory : GeneratorNewProjectWizard {
    override val id: @NonNls String = "potoflux-mod"
    override val name: @Nls String = "Potoflux Mod"
    override val icon: Icon = AllIcons.General.Add
    override val description: @Nls String = "Create a Potoflux mod project"

    override fun createStep(context: WizardContext): NewProjectWizardStep =
        RootNewProjectWizardStep(context).nextStep(::ProjectWizardStep)
}

private class ProjectWizardStep(parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    private var packageName = "your.packagename"
    private var modId = "my-mod"
    private var mainClassName = "MyMod"
    private var pregenTrad = true
    private var pregenTabs = false
    private var pregenMenus = false
    private var pregenCommands = false
    private var pregenSettings = false
    private var pregenActionRuns = false
    private var pregenThemes = false
    private var pregenModRegistrations = false
    private var githubProjectUrl = "https://github.com/you/project"
    private val enableGithubPackagesPublishing = propertyGraph.property(false)

    override fun setupUI(builder: Panel) {
        builder.group("Potoflux Mod Options") {
            row("Package:") {
                textField()
                    .bindText(::packageName)
                    .columns(24)
            }
            row("Mod ID:") {
                textField()
                    .bindText(::modId)
                    .columns(24)
            }
            row("Main class:") {
                textField()
                    .bindText(::mainClassName)
                    .columns(24)
            }

            group("Pregenerate Registries Classes:") {
                row {
                    checkBox("Pregenerate traduction")
                        .bindSelected(::pregenTrad)
                }
                row {
                    checkBox("Pregenerate tabs")
                        .bindSelected(::pregenTabs)
                }
                row {
                    checkBox("Pregenerate menus")
                        .bindSelected(::pregenMenus)
                }
                row {
                    checkBox("Pregenerate commands")
                        .bindSelected(::pregenCommands)
                }
                row {
                    checkBox("Pregenerate settings")
                        .bindSelected(::pregenSettings)
                }
                row {
                    checkBox("Pregenerate actionRuns")
                        .bindSelected(::pregenActionRuns)
                }
                row {
                    checkBox("Pregenerate themes")
                        .bindSelected(::pregenThemes)
                }
                row {
                    checkBox("Pregenerate mod registries")
                        .bindSelected(::pregenModRegistrations)
                }
            }

            row {
                checkBox("Enable GitHub packages publishing")
                    .bindSelected(enableGithubPackagesPublishing)
            }
            row("GitHub project URL:") {
                textField()
                    .bindText(::githubProjectUrl)
                    .columns(32)
            }.enabledIf(enableGithubPackagesPublishing)
        }
    }

    override fun setupProject(project: Project) {
        // Project generation will be implemented once the project templates are defined.
    }
}
