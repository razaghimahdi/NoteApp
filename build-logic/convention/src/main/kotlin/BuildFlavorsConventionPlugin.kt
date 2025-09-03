import com.android.build.api.dsl.ApplicationExtension
import com.razzaghi.noteapp.configureAndroidCompose
import com.razzaghi.noteapp.configureBuildFlavors
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class BuildFlavorsConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("com.android.application")
            val extension = extensions.getByType<ApplicationExtension>()
            configureBuildFlavors(extension)
        }
    }

}