package net.minheur

import com.intellij.icons.AllIcons
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.GeneratorNewProjectWizard
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.RootNewProjectWizardStep
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindIntText
import com.intellij.ui.dsl.builder.bindItem
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

private enum class ExampleModLoader(val displayName: String) {
    FABRIC("Fabric"),
    FORGE("Forge"),
    NEOFORGE("NeoForge");

    override fun toString(): String = displayName
}

private class ProjectWizardStep(parent: NewProjectWizardStep) : AbstractNewProjectWizardStep(parent) {
    /*
     * These properties are the values entered in the wizard. They remain available in
     * setupProject(), which is called after the user presses Create.
     */
    private var modId = "my-mod"
    private var modName = "My Mod"
    private var description = "A generated Potoflux mod."
    private var minecraftVersion: String? = "1.21.1"
    private var initialItemCount = 1
    private var generateExampleCode = true
    private var modLoader: ExampleModLoader? = ExampleModLoader.FABRIC

    override fun setupUI(builder: Panel) {
        builder.group("Potoflux mod options") {
            row("Mod ID:") {
                // String field: bindText keeps the property synchronized with the text field.
                textField()
                    .bindText(::modId)
                    .columns(24)
            }
            row("Display name:") {
                // Another String field; this is the human-readable name used in generated files.
                textField()
                    .bindText(::modName)
                    .columns(24)
            }
            row("Description:") {
                // Multiline text is still read as a String in setupProject().
                textArea()
                    .bindText(::description)
                    .columns(24)
            }
            row("Minecraft version:") {
                // A combo box is useful for a finite set of allowed String values.
                comboBox(listOf("1.20.1", "1.21.1", "1.21.4"))
                    .bindItem(::minecraftVersion)
            }
            row("Mod loader:") {
                // Enum values can be bound directly; bindItem writes the selected enum back.
                comboBox(ExampleModLoader.entries)
                    .bindItem(::modLoader)
            }
            row("Initial item count:") {
                // Numeric field: bindIntText converts the text input to an Int.
                intTextField(range = 0..999)
                    .bindIntText(::initialItemCount)
                    .columns(5)
            }
            row {
                // Boolean field: bindSelected synchronizes a check box with a Boolean.
                checkBox("Generate example source code")
                    .bindSelected(::generateExampleCode)
            }
        }
    }

    override fun setupProject(project: Project) {
        /*
         * Every value is retrieved directly from the properties above:
         * modId, modName, description, minecraftVersion, initialItemCount,
         * generateExampleCode and modLoader. Replace this example with your
         * template rendering or module configuration.
         */
        val baseDirectory = project.basePath
            ?.let { LocalFileSystem.getInstance().findFileByPath(it) }
            ?: error("The generated project has no writable base directory")

        val selectedMinecraftVersion = minecraftVersion ?: error("Minecraft version must be selected")
        val selectedModLoader = modLoader ?: error("Mod loader must be selected")
        val generatedContent = buildString {
            appendLine("modId=$modId")
            appendLine("modName=$modName")
            appendLine("description=$description")
            appendLine("minecraftVersion=$selectedMinecraftVersion")
            appendLine("modLoader=${selectedModLoader.name}")
            appendLine("initialItemCount=$initialItemCount")
            appendLine("generateExampleCode=$generateExampleCode")
        }

        // File-system changes must happen inside a write action.
        WriteCommandAction.runWriteCommandAction(project) {
            val generatedFile = baseDirectory.createChildData(this, "potoflux-generator-example.txt")
            VfsUtil.saveText(generatedFile, generatedContent)
            if (generateExampleCode) {
                createExampleSourceFile(baseDirectory, selectedModLoader)
            }
        }
    }

    private fun createExampleSourceFile(baseDirectory: VirtualFile, selectedModLoader: ExampleModLoader) {
        val sourceDirectory = VfsUtil.createDirectoryIfMissing(baseDirectory, "src/main/kotlin")
        val sourceFile = sourceDirectory.createChildData(this, "GeneratedMod.kt")
        VfsUtil.saveText(
            sourceFile,
            """
            package generated

            object GeneratedMod {
                const val ID = "$modId"
                const val NAME = "$modName"
                const val LOADER = "${selectedModLoader.name}"
                const val INITIAL_ITEM_COUNT = $initialItemCount
            }
            """.trimIndent()
        )
    }
}
