import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
	java
	alias(libs.plugins.spring.boot) apply false
	alias(libs.plugins.spring.dependency.management) apply false
	alias(libs.plugins.spotless) apply false
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
	}
}

allprojects {
	group = "com.github.asmln"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

val spotlessPluginId = libs.plugins.spotless.get().pluginId

subprojects {
	pluginManager.withPlugin(spotlessPluginId) {
		configure<SpotlessExtension> {
			java {
				googleJavaFormat()
				removeUnusedImports()
				trimTrailingWhitespace()
				endWithNewline()
			}
		}
	}
}



