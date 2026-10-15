package net.minheur

import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.AbstractNewProjectWizardStep
import com.intellij.ide.wizard.GeneratorNewProjectWizard
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.RootNewProjectWizardStep
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.Panel
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class ProjectGeneratorFactory : GeneratorNewProjectWizard {
    override val id = "net.mineur.potoflux-mods"
    override val name = "Potoflux Mod"
    override val ordinal = 500

    override fun isEnabled(): Boolean = true

    override fun createStep(context: WizardContext): NewProjectWizardStep = RootNewProjectWizardStep(context)
        .nextStep(::ModOptionsStep)
}

class ModOptionsStep(private val context: WizardContext) :
        AbstractNewProjectWizardStep {

    private var modId by propertyGraph.lazyProperty { "my_mod" }

    override fun setupUI(builder: Panel) {
        builder.row("Mod ID:") {
            textField().bindText(::modId).columns(25)
        }
    }

    override fun setupProject(project: Project) {

    }

}
