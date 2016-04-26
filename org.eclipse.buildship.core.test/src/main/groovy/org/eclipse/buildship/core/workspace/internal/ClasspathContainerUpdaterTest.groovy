package org.eclipse.buildship.core.workspace.internal

import com.gradleware.tooling.toolingmodel.OmniEclipseProject
import com.gradleware.tooling.toolingmodel.OmniEclipseProjectDependency
import com.gradleware.tooling.toolingmodel.OmniExternalDependency

import org.eclipse.jdt.core.IClasspathEntry
import org.eclipse.jdt.core.IJavaProject

import org.eclipse.buildship.core.test.fixtures.WorkspaceSpecification
import org.eclipse.buildship.core.workspace.GradleClasspathContainer

class ClasspathContainerUpdaterTest extends WorkspaceSpecification {

    IJavaProject project

    void setup() {
        project = newJavaProject("sample")
        project.setRawClasspath([GradleClasspathContainer.newClasspathEntry()] as IClasspathEntry[], null)
    }

    def "Folders are valid external dependencies"() {
        given:
        def gradleProject = gradleProjectWithClasspath(
            externalDependency(dir("foo"))
        )

        when:
        ClasspathContainerUpdater.updateFromModel(project, gradleProject, gradleProject.all.toSet(), null)

        then:
        resolvedClasspath[0].entryKind == IClasspathEntry.CPE_LIBRARY
        resolvedClasspath[0].path.toFile() == dir("foo")
    }

    OmniEclipseProject gradleProjectWithClasspath(Object... dependencies) {
        Stub(OmniEclipseProject) {
            getExternalDependencies() >> dependencies.findAll { it instanceof OmniExternalDependency }
            getProjectDependencies() >> dependencies.findAll { it instanceof OmniEclipseProjectDependency }
        }
    }

    OmniExternalDependency externalDependency(File location, File sources = null, File javaDoc = null, boolean exported = false) {
        Stub(OmniExternalDependency) {
            getFile() >> location
            getSource() >> sources
            getJavadoc() >> javaDoc
            isExported() >> exported
        }
    }

    IClasspathEntry[] getResolvedClasspath() {
        project.getResolvedClasspath(false)
    }
}
